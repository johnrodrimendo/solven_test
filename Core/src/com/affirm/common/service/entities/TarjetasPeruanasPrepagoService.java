package com.affirm.common.service.entities;

import com.affirm.common.dao.CreditDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.model.catalog.*;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.FileService;
import com.affirm.common.service.UtilService;
import com.affirm.system.configuration.Configuration;
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
import org.apache.pdfbox.pdmodel.interactive.form.PDCheckBox;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.List;

@Service("tarjetasPeruanasPrepagoService")
public class TarjetasPeruanasPrepagoService {

    private static final Logger logger = Logger.getLogger(TarjetasPeruanasPrepagoService.class);

    private static final String FICHA_REGIMEN = "FICHA_REGIMEN_GENERAL_-_TPP.pdf";
    private static final String SPREAD_SHEET_FILE = "Formato de Activación- Solven.xls";
    private static final String DASHED_FIELD = "---";
    private final UtilService utilService;
    private final FileService fileService;
    private final PersonDAO personDAO;
    private final MessageSource messageSource;
    private final CatalogService catalogService;
    private final CreditDAO creditDAO;

    @Autowired
    public TarjetasPeruanasPrepagoService(UtilService utilService, FileService fileService, PersonDAO personDAO, MessageSource messageSource, CatalogService catalogService, CreditDAO creditDAO) {
        this.utilService = utilService;
        this.fileService = fileService;
        this.personDAO = personDAO;
        this.messageSource = messageSource;
        this.catalogService = catalogService;
        this.creditDAO = creditDAO;
    }

    public byte[] generatePDF(LoanApplication loanApplication, Person person, User user, String signature) throws Exception {
        byte[] document = fileService.getAssociatedFile(FICHA_REGIMEN);
        return generateGeneralRegimeFile(document, loanApplication, person, user, signature);
    }

    private byte[] generateGeneralRegimeFile(byte[] document, LoanApplication loanApplication, Person person, User user, String signature) throws Exception {
        PDDocument pdfDoc = PDDocument.load(document);
        PDAcroForm acroForm = pdfDoc.getDocumentCatalog().getAcroForm();

        PersonContactInformation personContactInformation = personDAO.getPersonContactInformation(Configuration.getDefaultLocale(), person.getId());
        List<PersonOcupationalInformation> ocupations = personDAO.getPersonOcupationalInformation(Configuration.getDefaultLocale(), person.getId());
        PersonOcupationalInformation personOcupationalInformation = ocupations != null ? ocupations.stream().filter(o -> o.getNumber() == PersonOcupationalInformation.PRINCIPAL).findFirst().orElse(null) : null;

        Calendar birthdate = Calendar.getInstance();

//        1ERA PAGINA
//        CABECERA
        fillText(pdfDoc, acroForm, "form.header.date", utilService.dateFormat(loanApplication.getRegisterDate()));
        fillText(pdfDoc, acroForm, "form.header.card.number", null);// TODO
//        CUERPO
        fillText(pdfDoc, acroForm, "form.client.names", person.getName());
        fillText(pdfDoc, acroForm, "form.client.father.lastname", person.getFirstSurname());
        fillText(pdfDoc, acroForm, "form.client.mother.lastname", person.getLastSurname());

        birthdate.setTime(person.getBirthday());

        fillText(pdfDoc, acroForm, "form.client.birthdate.day", birthdate.get(Calendar.DATE));
        fillText(pdfDoc, acroForm, "form.client.birthdate.month", birthdate.get(Calendar.MONTH) + 1);
        fillText(pdfDoc, acroForm, "form.client.birthdate.year", birthdate.get(Calendar.YEAR));

        fillCheckBox(pdfDoc, acroForm, "form.client.document.hasDni", person.getDocumentType().getId() == IdentityDocumentType.DNI);
        fillCheckBox(pdfDoc, acroForm, "form.client.document.hasCe", person.getDocumentType().getId() == IdentityDocumentType.CE);
        fillCheckBox(pdfDoc, acroForm, "form.client.document.hasPassport", false);// NO HAY PASAPORTE
        fillText(pdfDoc, acroForm, "form.client.document.number", person.getDocumentNumber());

        fillText(pdfDoc, acroForm, "form.client.nationality", person.getNationality() != null ? person.getNationality().getName() : null);
        if(person.getDocumentType().getId() == IdentityDocumentType.CE) {
            fillText(pdfDoc, acroForm, "form.client.address.residence", personContactInformation.getAddressStreetName());
        }

        fillText(pdfDoc, acroForm, "form.client.email", user.getEmail());

        if (personContactInformation != null) {
            fillText(pdfDoc, acroForm, "form.client.address.name", personContactInformation.getAddressStreetName());

            if (personContactInformation.getAddressUbigeo() != null) {
                fillText(pdfDoc, acroForm, "form.client.address.district", personContactInformation.getAddressUbigeo().getDistrict().getName());
                fillText(pdfDoc, acroForm, "form.client.address.province", personContactInformation.getAddressUbigeo().getProvince().getName());
                fillText(pdfDoc, acroForm, "form.client.address.department", personContactInformation.getAddressUbigeo().getDepartment().getName());
            }

            fillText(pdfDoc, acroForm, "form.client.phone", null);
            fillText(pdfDoc, acroForm, "form.client.cellphone", personContactInformation.getPhoneNumber());
        }

        if (personOcupationalInformation != null) {
            fillText(pdfDoc, acroForm, "form.client.work", personOcupationalInformation.getCompanyName(), 8);
            fillText(pdfDoc, acroForm, "form.client.workphone", personOcupationalInformation.getPhoneNumber());

            if (personOcupationalInformation.getOcupation() != null) {
                fillText(pdfDoc, acroForm, "form.client.workas",  messageSource.getMessage(personOcupationalInformation.getOcupation().getOcupation(), null, Configuration.getDefaultLocale()), 8);
                fillText(pdfDoc, acroForm, "form.client.ocupation", messageSource.getMessage(personOcupationalInformation.getOcupation().getOcupation(), null, Configuration.getDefaultLocale()));
            }

            if (personOcupationalInformation.getActivityType() != null) {
                fillCheckBox(pdfDoc, acroForm, "form.client.isDependent", personOcupationalInformation.getActivityType().getId() == ActivityType.DEPENDENT);
                fillCheckBox(pdfDoc, acroForm, "form.client.isIndependent", personOcupationalInformation.getActivityType().getId() == ActivityType.INDEPENDENT);
            }

        }

        fillCheckBox(pdfDoc, acroForm, "form.client.isOfac", false);
        fillCheckBox(pdfDoc, acroForm, "form.client.isNotOfac", true);
        fillCheckBox(pdfDoc, acroForm, "form.client.isPep", false);
        fillCheckBox(pdfDoc, acroForm, "form.client.isNotPep", true);

        fillText(pdfDoc, acroForm, "form.client.beneficiary", null);
        fillText(pdfDoc, acroForm, "form.beneficiary.name", null);
        fillText(pdfDoc, acroForm, "form.beneficiary.father.lastname", null);
        fillText(pdfDoc, acroForm, "form.beneficiary.mother.lastname", null);
        fillText(pdfDoc, acroForm, "form.beneficiary.document.number", null);
        fillText(pdfDoc, acroForm, "form.beneficiary.birthdate.day", null);
        fillText(pdfDoc, acroForm, "form.beneficiary.birthdate.month", null);
        fillText(pdfDoc, acroForm, "form.beneficiary.birthdate.year", null);
        fillText(pdfDoc, acroForm, "form.beneficiary.relationship", null);

//        PIE
        fillText(pdfDoc, acroForm, "form.purpose", "Tarjetahabiente", 11);// POR DEFECTO
        fillText(pdfDoc, acroForm, "form.obs", null);// OBS
        fillCheckBox(pdfDoc, acroForm, "form.hasAccept", true);
        fillSignature(pdfDoc, acroForm, "form.client.sign", signature);

//        2DA PAGINA
//        if(personDisqualifier.stream().anyMatch(pd -> PersonDisqualifier.PEP.equals(pd.getType()))) {
//            fillCheckBox(pdfDoc, acroForm, "form.client.pep.inActivity.yes", "");
//            fillCheckBox(pdfDoc, acroForm, "form.client.pep.inActivity.no", "");
//            fillText(pdfDoc, acroForm, "form.client.pep.title", "");
//            fillText(pdfDoc, acroForm, "form.client.pep.institution", "");
//        }

        return render(pdfDoc, acroForm);
    }

    //    TODO. COMMON FUNCTIONS. REFACTOR
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

    protected void fillSignature(PDDocument pdfDoc, PDAcroForm acroForm, String fieldName, Object value) {
        try {
            PDField field = acroForm.getField(fieldName);

            COSDictionary dict = field.getCOSObject();
            COSString defaultAppearance = (COSString) dict.getDictionaryObject(COSName.DA);
            if (defaultAppearance != null) {
                String[] daString = defaultAppearance.getASCII().split(" ", 2);
                String[] fontSizeSplit = daString[1].split(" ", 2);
                dict.setString(COSName.DA, String.format("/%s %s %s 0 0 0 rg",
                        prepareFont(pdfDoc, "/PhontPhreak-sHandwriting.ttf").getName(),
                        18,
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

    protected void fillCheckBox(PDDocument pdfDoc, PDAcroForm acroForm, String fieldName, boolean value) {
        try {
            if (value)
                ((PDCheckBox) acroForm.getField(fieldName)).check();
            else
                ((PDCheckBox) acroForm.getField(fieldName)).unCheck();
        } catch (Exception ex) {
//            logger.error("Error setting value to pdfBox", ex);
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


    public byte[] createActivationSpreadSheet() throws Exception {


        List<TarjetasPeruanasCreditActivation> tarjetasPeruanasCreditActivationList = creditDAO.getLigoCardReports();

        Workbook workbook = null;
        Sheet sheet = null;
        ByteArrayInputStream inputStream = null;
        CellStyle valueStyle = null;
        Font valueStyleFont = null;

        logger.info("defaulting spreadsheet values");
        inputStream = new ByteArrayInputStream(fileService.getAssociatedFile(SPREAD_SHEET_FILE));
        workbook = WorkbookFactory.create(inputStream);
        sheet = workbook.getSheetAt(0);
        valueStyle = workbook.createCellStyle();
        valueStyleFont = workbook.createFont();
        valueStyleFont.setFontHeightInPoints((short) 9);
        valueStyle.setFont(valueStyleFont);
        valueStyle.setAlignment(HorizontalAlignment.LEFT);
        valueStyle.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("@"));

        if (null!= tarjetasPeruanasCreditActivationList){
            for (TarjetasPeruanasCreditActivation tarjeta : tarjetasPeruanasCreditActivationList) {
                Row row = sheet.createRow(sheet.getPhysicalNumberOfRows());

                if (IdentityDocumentType.DNI == tarjeta.getDocumentId()) {
                    fillSheet(sheet, row, 1, tarjeta.getDocumentNumber());
                } else if (IdentityDocumentType.CE == tarjeta.getDocumentId()) {
                    fillSheet(sheet, row, 2, tarjeta.getDocumentNumber());
                }

                fillSheet(sheet, row, 3, tarjeta.getFirstSurname());
                fillSheet(sheet, row, 4, tarjeta.getLastSurname());
                fillSheet(sheet, row, 5, tarjeta.getName());
                fillSheet(sheet, row, 6, utilService.dateFormat(tarjeta.getDob()));
                Ubigeo ubigeo = catalogService.getUbigeo(tarjeta.getUbigeoId());
                if (null != ubigeo) {
                    fillSheet(sheet, row, 9, null != ubigeo.getDepartment() ? ubigeo.getDepartment().getName() : null);
                    fillSheet(sheet, row, 10, null != ubigeo.getProvince() ? ubigeo.getProvince().getName() : null);
                    fillSheet(sheet, row, 11, null != ubigeo.getDistrict() ? ubigeo.getDistrict().getName() : null);
                    fillSheet(sheet, row, 17, null != ubigeo.getDepartment() ? ubigeo.getDepartment().getName() : null);
                }
                fillSheet(sheet, row, 12, tarjeta.getHomeAddress());
                fillSheet(sheet, row, 13, tarjeta.getLandLine());
                fillSheet(sheet, row, 14, tarjeta.getPhoneNumber());
                fillSheet(sheet, row, 15, tarjeta.getEmail());

                String nationality = "";
                if (null != tarjeta.getNationalityId()) {
                    Nationality nationalityCatalog = catalogService.getNationality(Configuration.getDefaultLocale(), tarjeta.getNationalityId());
                    nationality = nationalityCatalog != null ?  nationalityCatalog.getName() : "";
                }else if(IdentityDocumentType.DNI == tarjeta.getDocumentId()){
                    nationality = "Peruano";
                }
                fillSheet(sheet, row, 18, nationality);

                if (null != tarjeta.getMaritalStatusId()) {
                    MaritalStatus maritalStatus = catalogService.getMaritalStatus(Configuration.getDefaultLocale(), tarjeta.getMaritalStatusId());
                    fillSheet(sheet, row, 19, null != maritalStatus ? maritalStatus.getStatus() : "");
                }

                if (null != tarjeta.getProfessionId()) {
                    Profession profession = catalogService.getProfession(Configuration.getDefaultLocale(), tarjeta.getProfessionId());
                    fillSheet(sheet, row, 20, null != profession ? profession.getProfession() : "");
                }

                if (null != tarjeta.getOcupationId()) {
                    Ocupation ocupation = catalogService.getOcupation(Configuration.getDefaultLocale(), tarjeta.getOcupationId());
                    fillSheet(sheet, row, 21, null != ocupation ? ocupation.getOcupation() : "");
                    fillSheet(sheet, row, 28, null != ocupation ? ocupation.getOcupation() : "");
                }

                if (null != tarjeta.getIncome()) {
                    fillSheet(sheet, row, 22, tarjeta.getIncome());
                }

                fillSheet(sheet, row, 24, "N/A");
                if (null != tarjeta.getActivityTypeId() && ActivityType.DEPENDENT == tarjeta.getActivityTypeId()) {
                    fillSheet(sheet, row, 25, "Dependiente");
                } else if (null != tarjeta.getActivityTypeId() && ActivityType.INDEPENDENT == tarjeta.getActivityTypeId()) {
                    fillSheet(sheet, row, 25, "Independiente");
                }

                if (null != tarjeta.getRuc()) {
                    fillSheet(sheet, row, 26, tarjeta.getRuc());
                }

                if (null != tarjeta.getCompanyName()) {
                    fillSheet(sheet, row, 27, tarjeta.getCompanyName());
                }

                if (null != tarjeta.getEmploymentTime()) {
                    fillSheet(sheet, row, 29, tarjeta.getEmploymentTime()+" meses");
                }
                fillSheet(sheet, row, 30, "NO");
                Ubigeo ubigeoDelivery = null;

                if(null != tarjeta.getDeliveryPlace() && tarjeta.getDeliveryPlace() == TarjetasPeruanasCreditActivation.DeliveryAddress.HOME){
                    ubigeoDelivery = catalogService.getUbigeo(tarjeta.getUbigeoId());
                    fillSheet(sheet, row, 35, tarjeta.getHomeAddress());

                }else if(null != tarjeta.getDeliveryPlace() && tarjeta.getDeliveryPlace() == TarjetasPeruanasCreditActivation.DeliveryAddress.WORKPLACE){
                    ubigeoDelivery = catalogService.getUbigeo(tarjeta.getWorkUbigeoId());
                    fillSheet(sheet, row, 35, tarjeta.getCompanyAddress());
                }
                if(ubigeoDelivery != null){
                    fillSheet(sheet, row, 32, ubigeoDelivery.getDepartment().getName());
                    fillSheet(sheet, row, 33, ubigeoDelivery.getProvince().getName());
                    fillSheet(sheet, row, 34, ubigeoDelivery.getDistrict().getName());
                }

                if (null != tarjeta.getCardColor()) {
                    String color ="";
                    if(tarjeta.getCardColor().equalsIgnoreCase("green"))
                        color = "verde";
                    else if(tarjeta.getCardColor().equalsIgnoreCase("purple"))
                        color = "morado";
                    fillSheet(sheet, row, 31, color);
                }
            }
        }

        try {
            inputStream.close();
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            workbook.write(outStream);
            workbook.close();
            outStream.close();
            return outStream.toByteArray();
        } catch (IOException io) {
            logger.error("Error flushing streams in document generation", io);
            return null;
        }
    }

    private static void fillSheet(Sheet sheet, Row row, int column, Object val) {
        row.createCell(column).setCellValue(String.valueOf(null != val ? val : ""));
    }
}
