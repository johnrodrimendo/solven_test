package com.affirm.common.service.entities;

import com.affirm.acceso.model.Direccion;
import com.affirm.common.dao.*;
import com.affirm.common.model.catalog.*;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.FileService;
import com.affirm.common.service.InteractionService;
import com.affirm.common.service.UtilService;
import com.affirm.common.service.impl.FileServiceImpl;
import com.affirm.system.configuration.Configuration;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.log4j.Logger;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.text.DateFormatSymbols;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service("aeluService")
public class AeluService {

    private static final Logger logger = Logger.getLogger(AeluService.class);

    @Autowired
    private CatalogService catalogService;
    @Autowired
    private FileService fileService;
    @Autowired
    private InteractionService interactionService;
    @Autowired
    private EmployerDAO employerDao;
    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private PersonDAO personDao;
    @Autowired
    private UserDAO userDao;
    @Autowired
    private CreditDAO creditDao;
    @Autowired
    private UtilService utilService;
    @Autowired
    private MessageSource messageSource;

    public static final String SOLICITUD_PRESTAMO_PERSONA_NATURAL = "1. Solicitud prestamo Persona Natural.pdf";
    public static final String AUTORIZACION_DESEMBOLSO = "2. Autorización de desembolso de Crédito y Transferencia.pdf";
    public static final String CONTRATO = "3. Contrato productos aelucoop solven reducido.pdf";

    public byte[] generatePreliminaryDocumentation(int loanApplicationId) throws Exception {
        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(loanApplicationId, Configuration.getDefaultLocale());
        Credit credit = creditDao.getCreditByID(loanApplication.getCreditId(), Configuration.getDefaultLocale(), false, Credit.class);
        Person person = personDao.getPerson(catalogService, Configuration.getDefaultLocale(), loanApplication.getPersonId(), false);
        PersonContactInformation personContactInformation = personDao.getPersonContactInformation(Configuration.getDefaultLocale(), person.getId());
        PersonBankAccountInformation personBankAccountInformation = personDao.getPersonBankAccountInformation(Configuration.getDefaultLocale(), person.getId());
        List<PersonOcupationalInformation> ocupations = personDao.getPersonOcupationalInformation(Configuration.getDefaultLocale(), person.getId());
        PersonOcupationalInformation personOcupationalInformation = ocupations != null ? ocupations.stream().filter(o -> o.getNumber() == PersonOcupationalInformation.PRINCIPAL).findFirst().orElse(null) : null;

        List<String> documents = Arrays.asList(SOLICITUD_PRESTAMO_PERSONA_NATURAL, AUTORIZACION_DESEMBOLSO, CONTRATO);
        return generateAeluDocumentsZip(documents, person, credit, loanApplication, personContactInformation, personOcupationalInformation, personBankAccountInformation);
    }

    public void sendPreliminaryDocumentationEmail(int loanApplicationId, byte[] documents) throws Exception {
        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(loanApplicationId, Configuration.getDefaultLocale());
        Credit credit = creditDao.getCreditByID(loanApplication.getCreditId(), Configuration.getDefaultLocale(), false, Credit.class);
        Person person = personDao.getPerson(catalogService, Configuration.getDefaultLocale(), loanApplication.getPersonId(), false);
        User user = userDao.getUser(person.getUserId());

        PersonInteraction interaction = new PersonInteraction();
        interaction.setInteractionType(catalogService.getInteractionType(InteractionType.MAIL));
        interaction.setInteractionContent(catalogService.getInteractionContent(InteractionContent.AELU_PRELIMINARY_DOCUMENTATION, loanApplication.getCountryId()));
        interaction.setDestination(user.getEmail());
        if (credit != null && credit.getEmployer() != null) {
            List<UserEmployer> employerUsers = employerDao.getUserEmployersByEmployer(credit.getEmployer().getId(), Configuration.getDefaultLocale());
            if (employerUsers != null && !employerUsers.isEmpty()) {
                List<String> employerEmails = employerUsers.stream().map(u -> u.getEmail()).collect(Collectors.toList());
                interaction.setCcMails(employerEmails.toArray(new String[employerEmails.size()]));
            }
        }
        interaction.setLoanApplicationId(loanApplication.getId());
        interaction.setPersonId(loanApplication.getPersonId());

        JSONObject jsonVars = new JSONObject();
        jsonVars.put("CLIENT_NAME", person.getFirstName());
        jsonVars.put("ENTITY", catalogService.getEntity(Entity.AELU).getFullName());
        jsonVars.put("AGENT_FULLNAME", loanApplication.getAgent() != null ? loanApplication.getAgent().getName() : Configuration.AGENT_FULLNAME_COLLECTION);
        jsonVars.put("AGENT_IMAGE_URL", loanApplication.getAgent() != null ? loanApplication.getAgent().getAvatarUrl() : Configuration.AGENT_IMAGE_URL_COLLECTION);

        PersonInteractionAttachment atachment = new PersonInteractionAttachment();
        atachment.setBytes(documents);
        atachment.setFilename(FileServiceImpl.AELU_DOCUMENTACION_PRELIMINAR);
        interaction.setAttachments(Arrays.asList(atachment));

        interactionService.sendPersonInteraction(interaction, jsonVars,null);
    }

    public void sendPendingPromisoryNotEmail(int loanApplicationId) throws Exception {

        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(loanApplicationId, Configuration.getDefaultLocale());
        Credit credit = creditDao.getCreditByID(loanApplication.getCreditId(), Configuration.getDefaultLocale(), false, Credit.class);
        Person person = personDao.getPerson(catalogService, Configuration.getDefaultLocale(), loanApplication.getPersonId(), false);
        User user = userDao.getUser(person.getUserId());

        PersonInteraction interaction = new PersonInteraction();
        interaction.setInteractionType(catalogService.getInteractionType(InteractionType.MAIL));
        interaction.setInteractionContent(catalogService.getInteractionContent(InteractionContent.AELU_PENDIENTE_PAGARE, loanApplication.getCountryId()));
        interaction.setDestination(user.getEmail());
        if (credit.getEmployer() != null) {
            List<UserEmployer> employerUsers = employerDao.getUserEmployersByEmployer(credit.getEmployer().getId(), Configuration.getDefaultLocale());
            if (employerUsers != null && !employerUsers.isEmpty()) {
                List<String> employerEmails = employerUsers.stream().map(u -> u.getEmail()).collect(Collectors.toList());
                interaction.setCcMails(employerEmails.toArray(new String[employerEmails.size()]));
            }
        }
        interaction.setLoanApplicationId(loanApplication.getId());
        interaction.setPersonId(loanApplication.getPersonId());

        JSONObject jsonVars = new JSONObject();
        jsonVars.put("CLIENT_NAME", person.getFirstName());
        jsonVars.put("ENTITY", catalogService.getEntity(Entity.AELU).getFullName());
        jsonVars.put("AGENT_FULLNAME", loanApplication.getAgent() != null ? loanApplication.getAgent().getName() : Configuration.AGENT_FULLNAME_COLLECTION);
        jsonVars.put("AGENT_IMAGE_URL", loanApplication.getAgent() != null ? loanApplication.getAgent().getAvatarUrl() : Configuration.AGENT_IMAGE_URL_COLLECTION);

        List<UserFile> userFiles = loanApplicationDao.getLoanApplicationUserFiles(loanApplicationId);
        if (userFiles != null) {
            UserFile finalDocFile = userFiles.stream().filter(u -> u.getFileType().getId().equals(UserFileType.FINAL_DOCUMENTATION)).findFirst().orElse(null);
            if (finalDocFile != null) {
                byte[] bytes = fileService.getUserFileById(finalDocFile.getId(), false);
                PersonInteractionAttachment atachment = new PersonInteractionAttachment();
                atachment.setBytes(bytes);
                atachment.setFilename(finalDocFile.getFileName());
                interaction.setAttachments(Arrays.asList(atachment));
            }
        }

        interactionService.sendPersonInteraction(interaction, jsonVars,null);
    }

    public byte[] generateAeluDocumentsZip(List<String> attachments, Person person, Credit credit, LoanApplication loanApplication, PersonContactInformation personContactInformation, PersonOcupationalInformation personOcupationalInformation, PersonBankAccountInformation personBankAccountInformation) throws Exception {
        java.io.ByteArrayOutputStream byteArrayOutputStream = new java.io.ByteArrayOutputStream();
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(byteArrayOutputStream);
        ZipOutputStream zipOutputStream = new ZipOutputStream(bufferedOutputStream);

        for (String attachment : attachments) {
            byte[] document = null;

            switch (attachment) {
                case SOLICITUD_PRESTAMO_PERSONA_NATURAL:
                    document = generateSolicitudPrestamoPersonaNatural(fileService.getAssociatedFile(SOLICITUD_PRESTAMO_PERSONA_NATURAL), person, credit, loanApplication, personContactInformation, personOcupationalInformation);
                    break;
                case AUTORIZACION_DESEMBOLSO:
                    document = generateAutorizacionDesembolsoCredito(fileService.getAssociatedFile(AUTORIZACION_DESEMBOLSO), credit, personBankAccountInformation);
                    break;
                case CONTRATO:
                    document = generateContratoAelucoop(fileService.getAssociatedFile(CONTRATO), credit, person, personContactInformation, personOcupationalInformation);
                    break;
            }

            File outputFile = new File(attachment);
            try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
                outputStream.write(document);

                zipOutputStream.putNextEntry(new ZipEntry(outputFile.getName()));
                FileInputStream fileInputStream = new FileInputStream(outputFile);
                IOUtils.copy(fileInputStream, zipOutputStream);

                fileInputStream.close();
                zipOutputStream.closeEntry();
                outputStream.close();
            }
        }

        zipOutputStream.finish();
        zipOutputStream.flush();
        IOUtils.closeQuietly(zipOutputStream);
        IOUtils.closeQuietly(bufferedOutputStream);
        IOUtils.closeQuietly(byteArrayOutputStream);

        return byteArrayOutputStream.toByteArray();
    }

    private byte[] generateSolicitudPrestamoPersonaNatural(byte[] document, Person person, Credit credit, LoanApplication loanApplication, PersonContactInformation personContactInformation, PersonOcupationalInformation personOcupationalInformation) throws Exception {
        PDDocument pdfDoc = PDDocument.load(document);
        PDAcroForm acroForm = pdfDoc.getDocumentCatalog().getAcroForm();

//        DESCRIPCION DOCUMENTO
        fillText(pdfDoc, acroForm, "person.request.currency", "Nuevos Soles Peruanos");//POR DEFECTO

//        CODIGO SOLICITANTE
        fillText(pdfDoc, acroForm, "person.request.code", "");

//        DATOS DEL SOLICITANTE - PARTE 1
        fillText(pdfDoc, acroForm, "person.fullname", person.getFullName());
        fillText(pdfDoc, acroForm, "person.birthday", utilService.dateFormat(person.getBirthday()));
        fillText(pdfDoc, acroForm, "person.civilstatus", messageSource.getMessage(person.getMaritalStatus().getMessageKey(), null, Configuration.getDefaultLocale()));
        fillText(pdfDoc, acroForm, "person.documentnumber", person.getDocumentNumber());
        fillText(pdfDoc, acroForm, "person.address", personContactInformation.getAddressStreetName());
        fillText(pdfDoc, acroForm, "person.district", personContactInformation.getAddressUbigeo().getDistrict().getName());
        fillText(pdfDoc, acroForm, "person.province", personContactInformation.getAddressUbigeo().getProvince().getName());
        fillText(pdfDoc, acroForm, "person.region", personContactInformation.getAddressUbigeo().getDepartment().getName());
        fillText(pdfDoc, acroForm, "person.phone", null);
        fillText(pdfDoc, acroForm, "person.cellphone", personContactInformation.getPhoneNumber());
        fillText(pdfDoc, acroForm, "person.work.name", personOcupationalInformation.getCompanyName(), 8);
        fillText(pdfDoc, acroForm, "person.work.position", personOcupationalInformation.getOcupation() != null ? messageSource.getMessage(personOcupationalInformation.getOcupation().getOcupation(), null, Configuration.getDefaultLocale()) : null);
        fillText(pdfDoc, acroForm, "person.work.since", personOcupationalInformation.getEmploymentTime() + " meses");
        fillText(pdfDoc, acroForm, "person.work.business", person.getProfession() != null ? person.getProfession().getProfession() : null);
        fillText(pdfDoc, acroForm, "person.work.ruc", personOcupationalInformation.getRuc());

        Direccion jobAddress = personDao.getDisggregatedAddress(person.getId(), "J");
        if (jobAddress != null) {
            fillText(pdfDoc, acroForm, "person.work.district", catalogService.getUbigeo(jobAddress.getUbigeo()).getDistrict().getName());
            fillText(pdfDoc, acroForm, "person.work.province", catalogService.getUbigeo(jobAddress.getUbigeo()).getProvince().getName());
            fillText(pdfDoc, acroForm, "person.work.region", catalogService.getUbigeo(jobAddress.getUbigeo()).getDepartment().getName());
        }
        fillText(pdfDoc, acroForm, "person.work.address", personOcupationalInformation.getAddress());

//        MONTO DEL SOLICITANTE - PARTE 2
        fillText(pdfDoc, acroForm, "person.request.amount", utilService.doubleMoneyFormat(loanApplication.getAmount().doubleValue(), loanApplication.getCurrency().getSymbol()));
        fillText(pdfDoc, acroForm, "person.request.approved", utilService.doubleMoneyFormat(credit.getAmount(), credit.getCurrency().getSymbol()));
        fillText(pdfDoc, acroForm, "person.request.installment", credit.getInstallments());
        fillText(pdfDoc, acroForm, "person.monthly.amount", utilService.doubleMoneyFormat(personOcupationalInformation.getFixedGrossIncome(), credit.getCurrency().getSymbol()));
        fillText(pdfDoc, acroForm, "person.request.discount.authorization", "X", 14);//PDF TIENE VALOR POR DEFECTO MARCADO

        return render(pdfDoc, acroForm);
    }

    private byte[] generateAutorizacionDesembolsoCredito(byte[] document, Credit credit, PersonBankAccountInformation personBankAccountInformation) throws Exception {
        PDDocument pdfDoc = PDDocument.load(document);
        PDAcroForm acroForm = pdfDoc.getDocumentCatalog().getAcroForm();

        fillText(pdfDoc, acroForm, "loan.bank.name", personBankAccountInformation.getBank().getName());
        fillText(pdfDoc, acroForm, "loan.bank.account", personBankAccountInformation.getBankAccount());
        fillText(pdfDoc, acroForm, "loan.request.city", "Lima");//TODO

        Calendar loanRequestDate = Calendar.getInstance();
        loanRequestDate.setTime(credit.getRegisterDate());

        fillText(pdfDoc, acroForm, "loan.request.day", loanRequestDate.get(Calendar.DAY_OF_MONTH));
        fillText(pdfDoc, acroForm, "loan.request.month", DateFormatSymbols.getInstance(Configuration.getDefaultLocale()).getMonths()[loanRequestDate.get(Calendar.MONTH)]);
        fillText(pdfDoc, acroForm, "loan.request.year.lasttwodigits", String.valueOf(loanRequestDate.get(Calendar.YEAR)).substring(2, 4));

        return render(pdfDoc, acroForm);
    }

    private byte[] generateContratoAelucoop(byte[] document, Credit credit, Person person, PersonContactInformation personContactInformation, PersonOcupationalInformation personOcupationalInformation) throws Exception {
        PDDocument pdfDoc = PDDocument.load(document);
        PDAcroForm acroForm = pdfDoc.getDocumentCatalog().getAcroForm();

        String contractStringDate = utilService.dateCustomFormat(credit.getRegisterDate(), "dd/MM/yyyy", Configuration.getDefaultLocale());
        String[] contractStringDateSplit = contractStringDate.split("/");

//        CABECERA
        fillText(pdfDoc, acroForm, "request.day", contractStringDateSplit[0]);
        fillText(pdfDoc, acroForm, "request.month", contractStringDateSplit[1]);
        fillText(pdfDoc, acroForm, "request.year", contractStringDateSplit[2]);

//        DATOS DEL SOLICITANTE
        fillText(pdfDoc, acroForm, "person.surname", person.getFirstSurname());
        fillText(pdfDoc, acroForm, "person.secondsurname", person.getLastSurname());
        fillText(pdfDoc, acroForm, "person.name", person.getName());
        fillText(pdfDoc, acroForm, "person.documentnumber", person.getDocumentNumber());

        String personBirthdateString = utilService.dateCustomFormat(person.getBirthday(), "dd/MM/yyyy", Configuration.getDefaultLocale());
        String[] personBirthdateStringSplit = personBirthdateString.split("/");

        fillText(pdfDoc, acroForm, "person.birthdate.day", personBirthdateStringSplit[0]);
        fillText(pdfDoc, acroForm, "person.birthdate.month", personBirthdateStringSplit[1]);
        fillText(pdfDoc, acroForm, "person.birthdate.year", personBirthdateStringSplit[2]);
        fillText(pdfDoc, acroForm, "person.gender.male", "M".equals(person.getGender().toString()) ? "X" : "");
        fillText(pdfDoc, acroForm, "person.gender.female", "F".equals(person.getGender().toString()) ? "X" : "");
        fillText(pdfDoc, acroForm, "person.civilstatus", messageSource.getMessage(person.getMaritalStatus().getMessageKey(), null, Configuration.getDefaultLocale()));
        fillText(pdfDoc, acroForm, "person.nationality", person.getNationality().getName());
        fillText(pdfDoc, acroForm, "person.profession", null);
        fillText(pdfDoc, acroForm, "person.ocupation", "Empleado");//POR DEFECTO EMPLEADO
        fillText(pdfDoc, acroForm, "person.economicsector", person.getProfession() != null ? person.getProfession().getProfession() : null);

//        DATOS DE SU DOMICILIO
        fillText(pdfDoc, acroForm, "person.home.own", HousingType.OWN == personContactInformation.getHousingType().getId() ? "X" : "");
        fillText(pdfDoc, acroForm, "person.home.rent", HousingType.RENTED == personContactInformation.getHousingType().getId() ? "X" : "");
        fillText(pdfDoc, acroForm, "person.home.family", HousingType.FAMILY == personContactInformation.getHousingType().getId() ? "X" : "");
        fillText(pdfDoc, acroForm, "person.home.verification", "");
        fillText(pdfDoc, acroForm, "person.address.name", personContactInformation.getAddressStreetName());
        fillText(pdfDoc, acroForm, "person.address.number", personContactInformation.getAddressStreetNumber());
        fillText(pdfDoc, acroForm, "person.address.apartment", personContactInformation.getAddressInterior());

        Direccion homeAddress = personDao.getDisggregatedAddress(person.getId(), "H");
        if (homeAddress != null) {
            fillText(pdfDoc, acroForm, "person.address.urbanization", homeAddress.getTipoZona() != null && homeAddress.getTipoZona() == AreaType.URBANIZACION ? homeAddress.getNombreZona() : null, 8);
        }

        fillText(pdfDoc, acroForm, "person.address.district", personContactInformation.getAddressUbigeo().getDistrict().getName(), 8);
        fillText(pdfDoc, acroForm, "person.address.province", personContactInformation.getAddressUbigeo().getProvince().getName(), 8);
        fillText(pdfDoc, acroForm, "person.address.region", personContactInformation.getAddressUbigeo().getDepartment().getName(), 8);
        fillText(pdfDoc, acroForm, "person.phone", null);
        fillText(pdfDoc, acroForm, "person.cellphone", personContactInformation.getPhoneNumber());
        fillText(pdfDoc, acroForm, "person.email", personContactInformation.getEmail());
        fillText(pdfDoc, acroForm, "person.center.name", personOcupationalInformation.getCompanyName());
        fillText(pdfDoc, acroForm, "person.center.activity", person.getProfession() != null ? person.getProfession().getProfession() : null);

        Direccion jobAddress = personDao.getDisggregatedAddress(person.getId(), "J");
        if (jobAddress != null) {
            fillText(pdfDoc, acroForm, "person.center.district", catalogService.getUbigeo(jobAddress.getUbigeo()).getDistrict().getName(), 8);
            fillText(pdfDoc, acroForm, "person.center.province", catalogService.getUbigeo(jobAddress.getUbigeo()).getProvince().getName(), 8);
            fillText(pdfDoc, acroForm, "person.center.region", catalogService.getUbigeo(jobAddress.getUbigeo()).getDepartment().getName(), 8);
            fillText(pdfDoc, acroForm, "person.center.number", jobAddress.getNumeroVia());
            fillText(pdfDoc, acroForm, "person.center.urbanization", jobAddress.getTipoZona() != null && jobAddress.getTipoZona() == AreaType.URBANIZACION ? jobAddress.getNombreZona() : null);
            fillText(pdfDoc, acroForm, "person.center.apartment", jobAddress.getNumeroInterior());
        }
        fillText(pdfDoc, acroForm, "person.center.address", personOcupationalInformation.getAddress());

        fillText(pdfDoc, acroForm, "person.center.since", personOcupationalInformation.getEmploymentTime());
        fillText(pdfDoc, acroForm, "person.center.phone", personOcupationalInformation.getPhoneNumber());
        fillText(pdfDoc, acroForm, "person.center.position", personOcupationalInformation.getOcupation() != null ? messageSource.getMessage(personOcupationalInformation.getOcupation().getOcupation(), null, Configuration.getDefaultLocale()) : null);
        fillText(pdfDoc, acroForm, "person.center.salary", utilService.doubleMoneyFormat(personOcupationalInformation.getFixedGrossIncome(), credit.getCurrency().getSymbol()));

        if (personOcupationalInformation.getActivityType() != null) {
            fillText(pdfDoc, acroForm, "person.business.own.yes", personOcupationalInformation.getActivityType().getId() == ActivityType.INDEPENDENT ? "X" : "");
            fillText(pdfDoc, acroForm, "person.business.own.no", personOcupationalInformation.getActivityType().getId() != ActivityType.INDEPENDENT ? "X" : "");

            if (personOcupationalInformation.getActivityType().getId() == ActivityType.INDEPENDENT) {
                fillText(pdfDoc, acroForm, "person.business.name", personOcupationalInformation.getCompanyName());
                fillText(pdfDoc, acroForm, "person.business.ruc", personOcupationalInformation.getRuc());
                fillText(pdfDoc, acroForm, "person.business.time", personOcupationalInformation.getEmploymentTime());
                fillText(pdfDoc, acroForm, "person.business.address", personOcupationalInformation.getAddress());
                fillText(pdfDoc, acroForm, "person.business.phone", personOcupationalInformation.getPhoneNumber());
                fillText(pdfDoc, acroForm, "person.business.sell.month", utilService.doubleMoneyFormat(personOcupationalInformation.getFixedGrossIncome(), credit.getCurrency().getSymbol()));
            }
        }

        Person partner = person.getPartner();
        if (partner == null)
            partner = personDao.getPerson(catalogService, Configuration.getDefaultLocale(), person.getId(), true).getPartner();

        if (partner != null) {
            fillText(pdfDoc, acroForm, "person.spouse.surname", partner.getFirstSurname());
            fillText(pdfDoc, acroForm, "person.spouse.secondsurname", partner.getLastSurname());
            fillText(pdfDoc, acroForm, "person.spouse.name", partner.getName());
            fillText(pdfDoc, acroForm, "person.spouse.documentnumber", partner.getDocumentNumber());

            String partnerBirthdateString = utilService.dateCustomFormat(partner.getBirthday(), "dd/MM/yyyy", Configuration.getDefaultLocale());
            String[] partnerBirthdateStringSplit = partnerBirthdateString.split("/");

            fillText(pdfDoc, acroForm, "person.spouse.birthdate.day", partnerBirthdateStringSplit[0]);
            fillText(pdfDoc, acroForm, "person.spouse.birthdate.month", partnerBirthdateStringSplit[1]);
            fillText(pdfDoc, acroForm, "person.spouse.birthdate.year", partnerBirthdateStringSplit[2]);
            fillText(pdfDoc, acroForm, "person.spouse.gender.male", "M".equals(partner.getGender().toString()) ? "X" : "");
            fillText(pdfDoc, acroForm, "person.spouse.gender.female", "F".equals(partner.getGender().toString()) ? "X" : "");
            fillText(pdfDoc, acroForm, "person.spouse.civilstatus", partner.getMaritalStatus() != null ? messageSource.getMessage(partner.getMaritalStatus().getMessageKey(), null, Configuration.getDefaultLocale()) : null);
            fillText(pdfDoc, acroForm, "person.spouse.nationality", partner.getNationality().getName());
        }

        fillText(pdfDoc, acroForm, "person.pep.no", !person.getPep() ? "X" : "");
        fillText(pdfDoc, acroForm, "person.pep.never", !person.getPep() ? "X" : "");
        fillText(pdfDoc, acroForm, "person.pep.norelations", !person.getPep() ? "X" : "");

        return render(pdfDoc, acroForm);
    }

    protected void fillText(PDDocument pdfDoc, PDAcroForm acroForm, String fieldName, Object value) {
        try {
            PDField field = acroForm.getField(fieldName);

            COSDictionary dict = field.getCOSObject();
            COSString defaultAppearance = (COSString) dict.getDictionaryObject(COSName.DA);
            if (defaultAppearance != null) {
                String[] daString = defaultAppearance.getASCII().split(" ", 2);
                String[] fontSizeSplit = daString[1].split(" ", 2);
                dict.setString(COSName.DA, String.format("/%s %s %s 0 0 0 rg",
                        prepareFont(pdfDoc, "/Calibri.ttf").getName(),
                        10,
                        fontSizeSplit[1])); // Force black font color, maybe customize in the future
                acroForm.setDefaultAppearance(defaultAppearance.getString());
            } else {
                acroForm.setDefaultAppearance("/Helv 10 Tf 0 0 0 rg");
            }
            field.setValue(value != null ? value + "" : "----");
        } catch (Exception ex) {
//            logger.error("Error setting value to pdfBox" + fieldName, ex);
        }
    }

    protected void fillText(PDDocument pdfDoc, PDAcroForm acroForm, String fieldName, Object value, int fontSize) {
        try {
            PDField field = acroForm.getField(fieldName);

            COSDictionary dict = field.getCOSObject();
            COSString defaultAppearance = (COSString) dict.getDictionaryObject(COSName.DA);
            if (defaultAppearance != null) {
                String[] daString = defaultAppearance.getASCII().split(" ", 2);
                String[] fontSizeSplit = daString[1].split(" ", 2);
                dict.setString(COSName.DA, String.format("/%s %s %s 0 0 0 rg",
                        prepareFont(pdfDoc, "/Calibri.ttf").getName(),
                        fontSize,
                        fontSizeSplit[1])); // Force black font color, maybe customize in the future
                acroForm.setDefaultAppearance(defaultAppearance.getString());
            } else {
                acroForm.setDefaultAppearance(String.format("/Helv %s Tf 0 0 0 rg", fontSize));
            }
            acroForm.getField(fieldName).setValue(value != null ? value + "" : "----");
        } catch (Exception ex) {
//            logger.error("Error setting value to pdfBox" + fieldName, ex);
        }
    }

    private COSName prepareFont(PDDocument _pdfDocument, String path) throws IOException {
        PDDocumentCatalog docCatalog = _pdfDocument.getDocumentCatalog();
        PDAcroForm acroForm = docCatalog.getAcroForm();

        PDResources res = acroForm.getDefaultResources();

        if (res == null) res = new PDResources();

        InputStream fontStream = getClass().getResourceAsStream(path);
        PDType0Font font = PDType0Font.load(_pdfDocument, fontStream);
        COSName fontName = res.add(font);
        acroForm.setDefaultResources(res);

        return fontName;
    }

    protected byte[] render(PDDocument pdfDoc, PDAcroForm acroForm) throws Exception {
        // Flat the pdf (not editable)
        acroForm.flatten();
        // Save it to byte array
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        pdfDoc.save(out);
        pdfDoc.close();

        return out.toByteArray();
    }

}
