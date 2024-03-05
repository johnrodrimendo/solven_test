package com.affirm.common.service.document.impl;

import com.affirm.acceso.model.Direccion;
import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.dao.SysUserDAO;
import com.affirm.common.dao.TranslatorDAO;
import com.affirm.common.model.PreApprovedInfo;
import com.affirm.common.model.catalog.*;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.FileService;
import com.affirm.common.service.UtilService;
import com.affirm.common.service.document.DocumentService;
import com.affirm.security.model.SysUser;
import com.affirm.system.configuration.Configuration;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

@Service
public class DocumentServiceImplem implements DocumentService {
    private static final Logger logger = Logger.getLogger(DocumentServiceImplem.class);
    private static final String RIPLEY_LOAN_REQUEST = "Ripley_Solicitud_de_Prestamo.xls";
    private static final String EFECTIVO_AL_TOQUE_TEMPLATE = "Efectivo_al_toque.xlsx";
    private static final String DASHED_FIELD = "---";

    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private PersonDAO personDao;
    @Autowired
    private UtilService utilService;
    @Autowired
    private FileService fileService;
    @Autowired
    private SysUserDAO sysUserDao;
    @Autowired
    private TranslatorDAO translatorDAO;

    public byte[] generateRipleyLoanSpreadSheet(Credit credit) throws Exception {

        Workbook workbook = null;
        Sheet sheet = null;
        ByteArrayInputStream inputStream = null;
        CellStyle valueStyle = null;
        Font valueStyleFont = null;
        Row row = null;
        LoanApplication loanApplication = null;
        Person person = null;
        PersonContactInformation contactInfo = null;
        List<PersonOcupationalInformation> ocupationalInformationList = null;
        PersonOcupationalInformation ocupationalInformation = null;

        logger.info("defaulting spreadsheet values");
        inputStream = new ByteArrayInputStream(fileService.getAssociatedFile(RIPLEY_LOAN_REQUEST));
        workbook = WorkbookFactory.create(inputStream);
        sheet = workbook.getSheetAt(0);
        row = sheet.getRow(47);
        valueStyle = workbook.createCellStyle();
        valueStyleFont = workbook.createFont();
        valueStyleFont.setFontHeightInPoints((short) 9);
        valueStyle.setFont(valueStyleFont);
        valueStyle.setAlignment(HorizontalAlignment.LEFT);
        valueStyle.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("@"));

        loanApplication = loanApplicationDao.getLoanApplication(credit.getLoanApplicationId(), Configuration.getDefaultLocale());
        person = personDao.getPerson(catalogService, Configuration.getDefaultLocale(), credit.getPersonId(), true);
        contactInfo = personDao.getPersonContactInformation(Configuration.getDefaultLocale(), person.getId());
        ocupationalInformationList = personDao.getPersonOcupationalInformation(Configuration.getDefaultLocale(), person.getId());

        String IdNumber = person.getDocumentNumber() != null ? person.getDocumentNumber() : DASHED_FIELD;
        String firstSurname = person.getFirstSurname() != null ? person.getFirstSurname() : DASHED_FIELD;
        String secondSurname = person.getFirstSurname() != null ? person.getLastSurname() : DASHED_FIELD;
        String firstName = person.getFirstName() != null ? person.getFirstName() : DASHED_FIELD;
        String middleName = person.getFirstName() != null ? person.getOtherNames() : DASHED_FIELD;

        String gender = String.valueOf(person.getGender());
        String birthDate = utilService.dateFormat(person.getBirthday());
        String maritalStatus = person.getMaritalStatus() != null ? person.getMaritalStatus().getStatus() : DASHED_FIELD;
        String educationLevel = person.getStudyLevel() != null ? person.getStudyLevel().getLevel() : DASHED_FIELD;
        String nationality = person.getNationality() != null ? person.getNationality().getName() : DASHED_FIELD;
        String ocupation = person.getProfession() != null ? person.getProfession().getProfession() : DASHED_FIELD;
        String dependants = person.getDependents() != null ? person.getDependents().toString() : DASHED_FIELD;
        Direccion disagregatedAddress = personDao.getDisggregatedAddress(person.getId(), "H");
        Direccion jobDisagregatedAddress = personDao.getDisggregatedAddress(person.getId(), "J");

        String roadType = DASHED_FIELD;
        String contactDate = DASHED_FIELD;
        String procedureType = DASHED_FIELD;
        String address = DASHED_FIELD;
        String addressNumber = DASHED_FIELD;
        String addressUnitNumber = DASHED_FIELD;
        String addressManzana = DASHED_FIELD;
        String addressLote = DASHED_FIELD;
        String interiorType = DASHED_FIELD;
        String urbanization = DASHED_FIELD;
        String district = DASHED_FIELD;
        String province = DASHED_FIELD;
        String department = DASHED_FIELD;
        String zoneType = DASHED_FIELD;

        String zoneName = DASHED_FIELD;
        String habitational = DASHED_FIELD;
        String reference = DASHED_FIELD;
        String phoneType1 = DASHED_FIELD;
        String phone1 = DASHED_FIELD;
        String email = DASHED_FIELD;
        String phone2 = DASHED_FIELD;
        String phone2Type = DASHED_FIELD;
        String economicActivity = DASHED_FIELD;
        String companyName = DASHED_FIELD;
        String ruc = DASHED_FIELD;
        Person partner = person.getPartner();
        String startDate = DASHED_FIELD;
        String cargo = DASHED_FIELD;
        String area = DASHED_FIELD;
        String contractType = DASHED_FIELD;
        String incomeType = DASHED_FIELD;
        String netIncome = DASHED_FIELD;
        String profesionalRuc = DASHED_FIELD;
        String taxationScheme = DASHED_FIELD;
        String profesionalActivity = DASHED_FIELD;
        String jobRoadType = DASHED_FIELD;
        String jobAddress = DASHED_FIELD;
        String jobAddressNumber = DASHED_FIELD;
        String jobAddressManzana = DASHED_FIELD;
        String jobAddressUnitNumber = DASHED_FIELD;
        String jobAddressLote = DASHED_FIELD;
        String jobUrbanization = DASHED_FIELD;
        String jobDistrict = DASHED_FIELD;
        String jobProvince = DASHED_FIELD;
        String jobDeparment = DASHED_FIELD;
        String jobZoneType = DASHED_FIELD;
        String jobZoneName = DASHED_FIELD;
        String partnerDocumentType = DASHED_FIELD;
        String partnerDocumentNumber = DASHED_FIELD;
        String partnerFirstName = DASHED_FIELD;
        String partnerSecondName = DASHED_FIELD;
        String partnerLastName = DASHED_FIELD;
        String partnerSecondLastName = DASHED_FIELD;
        String partnerEducationLevel = DASHED_FIELD;
        String partnerNationality = DASHED_FIELD;
        String partnerProfession = DASHED_FIELD;
        String partnerGender = DASHED_FIELD;
        String partnerMaritalStatus = DASHED_FIELD;
        String partnerBirthday = DASHED_FIELD;
        String disbursement = DASHED_FIELD;
        String callCenterPerson = DASHED_FIELD;
        String frecuency = DASHED_FIELD;
        String insurance = DASHED_FIELD;
        String jobPhone = DASHED_FIELD;
        String jobPhoneType = DASHED_FIELD;
        String payDay = DASHED_FIELD;
        String jobReference = DASHED_FIELD;
        String retirementScheme = DASHED_FIELD;
        String retirementDate = DASHED_FIELD;
        final String currency = "Soles";
        final String otherEconomicActivity = "Otros servicios";
        String bankName, acountCci, bankAccountNumber, translatedProfession, industry;

        if (ocupationalInformationList != null) {
            ocupationalInformation = ocupationalInformationList.stream().filter(o -> o.getNumber() == PersonOcupationalInformation.PRINCIPAL).findFirst().orElse(null);
        }

        if (credit.getRegisterDate() != null) {
            contactDate = utilService.dateFormat(credit.getRegisterDate());
        }
        PreApprovedInfo preApprovedInfo = loanApplicationDao.getApprovedLoanApplication(credit.getLoanApplicationId(), Configuration.getDefaultLocale())
                .stream().filter(p -> p.getEntity().getId() == Entity.RIPLEY).findFirst().orElse(null);
        if (preApprovedInfo != null && preApprovedInfo.getCluster() != null) {
            List<String> possibleValues = Arrays.asList("REG", "SEN", "TN1", "TN2", "TN3");
            procedureType = possibleValues.stream().filter(pv -> preApprovedInfo.getCluster().toLowerCase().contains(pv.toLowerCase())).findFirst().orElse(null);
            if (procedureType == null)
                procedureType = preApprovedInfo.getCluster();
        }
        //header
        int rowNum = 4;
        fillSheet(sheet, rowNum, 5, "Solven");
        fillSheet(sheet, rowNum, 11, procedureType);
        fillSheet(sheet, rowNum, 16, contactDate);

        //1.-Datos Personales
        rowNum = 8;
        fillSheet(sheet, rowNum, 2, IdNumber);
        fillSheet(sheet, rowNum, 4, firstSurname);
        fillSheet(sheet, rowNum, 8, secondSurname);
        fillSheet(sheet, rowNum, 12, firstName);
        fillSheet(sheet, rowNum, 15, middleName);


        rowNum = 10;
        fillSheet(sheet, rowNum, 2, gender);
        fillSheet(sheet, rowNum, 4, birthDate);
        fillSheet(sheet, rowNum, 6, maritalStatus);
        fillSheet(sheet, rowNum, 8, dependants);
        fillSheet(sheet, rowNum, 9, educationLevel);
        fillSheet(sheet, rowNum, 13, nationality);
        fillSheet(sheet, rowNum, 15, ocupation);


        //2.- Datos Domiciliarios

        if (disagregatedAddress != null) {
            address = disagregatedAddress.getNombreVia() != null ? disagregatedAddress.getNombreVia() : DASHED_FIELD;
            addressUnitNumber = disagregatedAddress.getNumeroInterior() != null ? disagregatedAddress.getNumeroInterior() : DASHED_FIELD;
            addressManzana = disagregatedAddress.getManzana() != null ? disagregatedAddress.getManzana() : DASHED_FIELD;
            addressLote = disagregatedAddress.getLote() != null ? disagregatedAddress.getLote() : DASHED_FIELD;
            interiorType = disagregatedAddress.getTipoInterior() != null ? String.valueOf(disagregatedAddress.getTipoInterior()) : DASHED_FIELD;
        }

        if (contactInfo != null) {
            roadType = contactInfo.getAddressStreetType() != null ? contactInfo.getAddressStreetType().getType() : DASHED_FIELD;
            addressNumber = contactInfo.getAddressStreetNumber() != null ? contactInfo.getAddressStreetNumber() : DASHED_FIELD;
            district = contactInfo.getDistrict() != null ? contactInfo.getDistrict().getName() : DASHED_FIELD;
            province = contactInfo.getProvince() != null ? contactInfo.getProvince().getName() : DASHED_FIELD;
            department = contactInfo.getDepartment() != null ? contactInfo.getDepartment().getName() : DASHED_FIELD;
            Ubigeo ubigeo = contactInfo.getAddressUbigeo();
            if (ubigeo != null) {
                district = ubigeo.getDistrict().getName();
                province = ubigeo.getProvince().getName();
                department = ubigeo.getDepartment().getName();
            }
            if (contactInfo.getHousingType() != null) {
                switch (contactInfo.getHousingType().getId()) {
                    case HousingType.OWN:
                    case HousingType.OWN_FINANCED:
                        habitational = "PROPIA";
                        break;
                    case HousingType.RENTED:
                    case HousingType.ROOM_RENTED:
                        habitational = "ALQUILADA";
                        break;
                    case HousingType.FAMILY:
                    case HousingType.FAMILY_2:
                        habitational = "DE LA FAMILIA";
                        break;
                }
            }
            reference = disagregatedAddress != null ? disagregatedAddress.getReferencia() : DASHED_FIELD;
            if (contactInfo.getPhoneNumberType() != null) {
                switch (contactInfo.getPhoneNumberType()) {
                    case PhoneType.LANDLINE:
                        phoneType1 = "FIJO";
                        break;
                    case PhoneType.CELLPHONE:
                        phoneType1 = "CELULAR";
                        break;
                }
            }
            phone1 = contactInfo.getPhoneNumber();
            email = contactInfo.getEmail();
        }

        rowNum = 14;
        fillSheet(sheet, rowNum, 2, roadType);
        fillSheet(sheet, rowNum, 4, address);
        fillSheet(sheet, rowNum, 9, addressNumber);
        fillSheet(sheet, rowNum, 10, addressUnitNumber);
        fillSheet(sheet, rowNum, 11, addressManzana);
        fillSheet(sheet, rowNum, 13, addressLote);
        fillSheet(sheet, rowNum, 15, interiorType);


        if (disagregatedAddress != null && disagregatedAddress.getTipoZona() != null) {
            zoneType = translatorDAO.translate(Entity.RIPLEY, 2, String.valueOf(disagregatedAddress.getTipoZona()), null);
            if (disagregatedAddress.getTipoZona() == AreaType.URBANIZACION) {
                urbanization = disagregatedAddress.getNombreZona();
            }
        }


        rowNum = 16;
        fillSheet(sheet, rowNum, 2, urbanization);
        fillSheet(sheet, rowNum, 5, district);
        fillSheet(sheet, rowNum, 8, province);
        fillSheet(sheet, rowNum, 11, department);
        fillSheet(sheet, rowNum, 15, zoneType);


        if (disagregatedAddress != null && disagregatedAddress.getNombreZona() != null) {
            zoneName = disagregatedAddress.getNombreZona();
        }

        rowNum = 18;
        fillSheet(sheet, rowNum, 2, zoneName);
        fillSheet(sheet, rowNum, 6, habitational);
        fillSheet(sheet, rowNum, 9, reference);
        fillSheet(sheet, rowNum, 15, phoneType1);


        rowNum = 20;
        fillSheet(sheet, rowNum, 2, phone1);
        fillSheet(sheet, rowNum, 5, phone2Type);
        fillSheet(sheet, rowNum, 8, phone2);
        fillSheet(sheet, rowNum, 11, email);


        // JOB
        if (ocupationalInformation != null) {
            economicActivity = ocupationalInformation.getActivityType().getType();
            companyName = ocupationalInformation.getCompanyName() != null ? ocupationalInformation.getCompanyName() : DASHED_FIELD;
            if (ocupationalInformation.getActivityType().getId() == ActivityType.DEPENDENT) {
                ruc = ocupationalInformation.getRuc();
            }

            if (ocupationalInformation.getActivityType().getId() == ActivityType.INDEPENDENT && ocupationalInformation.getRuc() == null) {
                economicActivity = "EN PROCESO DE FORMALIZACION";
            }
            startDate = utilService.dateFormat(ocupationalInformation.getStartDate());
            cargo = ocupationalInformation.getOcupation() != null ? ocupationalInformation.getOcupation().getOcupation() : DASHED_FIELD;
            area = ocupationalInformation.getActivityType() != null ? ocupationalInformation.getActivityType().getType() : DASHED_FIELD;
            switch (ocupationalInformation.getActivityType().getId()) {
                case ActivityType.DEPENDENT:
                    contractType = "PLAZO FIJO";
                    incomeType = "FIJO";
                    break;
                case ActivityType.INDEPENDENT:
                    contractType = "INDEPENDIENTE";
                    incomeType = "VARIABLE";
                    break;
            }

            netIncome = ocupationalInformation.getFixedGrossIncome().toString();
            if (ocupationalInformation.getActivityType().getId() == ActivityType.INDEPENDENT) {
                profesionalRuc = ocupationalInformation.getRuc();
                taxationScheme = ocupationalInformation.getTaxRegime() != null ? translatorDAO.translate(Entity.RIPLEY, 3, ocupationalInformation.getTaxRegime().getCode(), null) : DASHED_FIELD;
            }
            profesionalActivity = ocupationalInformation.getOcupation() != null ? ocupationalInformation.getOcupation().getOcupation() : DASHED_FIELD;
            jobPhone = ocupationalInformation.getPhoneNumberWithoutCode() != null ? ocupationalInformation.getPhoneNumberWithoutCode() : DASHED_FIELD;
            jobPhoneType = ocupationalInformation.getPhoneNumberType() != null ? ocupationalInformation.getPhoneNumberType() : DASHED_FIELD;
            if ("L".equals(jobPhoneType)) {
                jobPhoneType = "FIJO";
            }
            if ("M".equals(jobPhoneType)) {
                jobPhoneType = "CELULAR";
            }
            Date retDate = ocupationalInformation.getRetirementDate();

            retirementDate = retDate != null ? utilService.dateFormat(retDate) : DASHED_FIELD;
            retirementScheme = retDate != null ? ocupationalInformation.getRetirementScheme().getScheme() : DASHED_FIELD;

        }

        // 3.-Datos Laborales
        translatedProfession = translatorDAO.translate(credit.getEntity().getId(), 1, person.getProfession() != null ? person.getProfession().getId().toString() : null, null);
        industry = person.getProfession() != null ? translatedProfession != null ? translatedProfession : DASHED_FIELD : DASHED_FIELD;

        rowNum = 24;
        fillSheet(sheet, rowNum, 2, economicActivity);
        fillSheet(sheet, rowNum, 6, companyName);
        fillSheet(sheet, rowNum, 11, startDate);
        fillSheet(sheet, rowNum, 14, cargo);
        fillSheet(sheet, rowNum, 16, netIncome);


        rowNum = 26;
        fillSheet(sheet, rowNum, 2, ruc);
        fillSheet(sheet, rowNum, 6, profesionalRuc);
        fillSheet(sheet, rowNum, 9, industry);
        fillSheet(sheet, rowNum, 13, otherEconomicActivity);


        rowNum = 28;
        fillSheet(sheet, rowNum, 2, area);
        fillSheet(sheet, rowNum, 4, contractType);
        fillSheet(sheet, rowNum, 7, incomeType);
        fillSheet(sheet, rowNum, 9, currency);
        incomeType = ocupationalInformation != null ? "DECLARADO" : DASHED_FIELD;
        fillSheet(sheet, rowNum, 11, incomeType);
        fillSheet(sheet, rowNum, 13, taxationScheme);

        if (jobDisagregatedAddress != null) {
            jobRoadType = jobDisagregatedAddress.getTipoVia() != null ? catalogService.getStreetType(jobDisagregatedAddress.getTipoVia()).getType() : DASHED_FIELD;
            jobAddress = jobDisagregatedAddress.getNombreVia() != null ? jobDisagregatedAddress.getNombreVia() : DASHED_FIELD;
            jobAddressNumber = jobDisagregatedAddress.getNumeroVia() != null ? jobDisagregatedAddress.getNumeroVia() : DASHED_FIELD;
            jobAddressUnitNumber = jobDisagregatedAddress.getNumeroInterior() != null ? jobDisagregatedAddress.getNumeroInterior() : DASHED_FIELD;
            jobAddressManzana = jobDisagregatedAddress.getManzana() != null ? jobDisagregatedAddress.getManzana() : DASHED_FIELD;
            jobAddressLote = jobDisagregatedAddress.getLote() != null ? jobDisagregatedAddress.getLote() : DASHED_FIELD;
            jobUrbanization = jobDisagregatedAddress.getTipoZona() != null && jobDisagregatedAddress.getTipoZona() == AreaType.URBANIZACION ? jobDisagregatedAddress.getNombreZona() : DASHED_FIELD;
        }

        rowNum = 30;
        fillSheet(sheet, rowNum, 2, jobRoadType);
        fillSheet(sheet, rowNum, 4, jobAddress);
        fillSheet(sheet, rowNum, 9, jobAddressNumber);
        fillSheet(sheet, rowNum, 10, jobAddressUnitNumber);
        fillSheet(sheet, rowNum, 11, jobAddressManzana);
        fillSheet(sheet, rowNum, 13, jobAddressLote);
        fillSheet(sheet, rowNum, 15, jobUrbanization);

        Ubigeo ubigeo = ocupationalInformation != null ? ocupationalInformation.getAddressUbigeo() : null;
        if (ubigeo != null) {
            jobDistrict = ubigeo.getDistrict() != null ? ubigeo.getDistrict().getName() : DASHED_FIELD;
            jobProvince = ubigeo.getProvince() != null ? ubigeo.getProvince().getName() : DASHED_FIELD;
            jobDeparment = ubigeo.getDepartment() != null ? ubigeo.getDepartment().getName() : DASHED_FIELD;
        }
        if (disagregatedAddress != null && disagregatedAddress.getTipoZona() != null)
            jobZoneType = translatorDAO.translate(Entity.RIPLEY, 2, String.valueOf(disagregatedAddress.getTipoZona()), null);
        if (disagregatedAddress != null && disagregatedAddress.getNombreZona() != null)
            jobZoneName = disagregatedAddress.getNombreZona();

        rowNum = 32;
        fillSheet(sheet, rowNum, 2, jobDistrict);
        fillSheet(sheet, rowNum, 5, jobProvince);
        fillSheet(sheet, rowNum, 8, jobDeparment);
        fillSheet(sheet, rowNum, 11, jobZoneType);
        fillSheet(sheet, rowNum, 14, jobZoneName);

        jobReference = disagregatedAddress != null ? disagregatedAddress.getReferencia() : DASHED_FIELD;
        rowNum = 34;

        fillSheet(sheet, rowNum, 2, jobPhoneType);
        fillSheet(sheet, rowNum, 4, jobPhone);
        fillSheet(sheet, rowNum, 6, DASHED_FIELD);//anexo - extension
        fillSheet(sheet, rowNum, 9, jobReference);

        rowNum = 36;

        fillSheet(sheet, rowNum, 2, retirementDate);
        fillSheet(sheet, rowNum, 5, retirementScheme);


        //4.- DATOS DEL CONYUGUE

        if (partner != null) {
            partnerDocumentType = null != partner.getDocumentType() ? partner.getDocumentType().getName() : DASHED_FIELD;
            partnerDocumentNumber = null != partner.getDocumentNumber() ? partner.getDocumentNumber() : DASHED_FIELD;
            partnerFirstName = null != partner.getName() ? partner.getName() : DASHED_FIELD;
            partnerSecondName = null != partner.getOtherNames() ? partner.getOtherNames() : DASHED_FIELD;
            partnerLastName = null != partner.getFirstSurname() ? partner.getFirstSurname() : DASHED_FIELD;
            partnerSecondLastName = null != partner.getLastSurname() ? partner.getLastSurname() : DASHED_FIELD;
            partnerMaritalStatus = person.getMaritalStatus() != null && (person.getMaritalStatus().getId() == MaritalStatus.MARRIED || person.getMaritalStatus().getId() == MaritalStatus.COHABITANT) ? person.getMaritalStatus().getStatus() : DASHED_FIELD;
            partnerGender = partner.getGender() != null ? partner.getGender().toString() : DASHED_FIELD;
            partnerEducationLevel = partner.getStudyLevel() != null ? partner.getStudyLevel().getLevel() : "Otros";
            partnerNationality = partner.getNationality() != null ? partner.getNationality().getName() : DASHED_FIELD;
            partnerProfession = partner.getProfession() != null ? partner.getProfession().getProfession() : "Otros";
            partnerBirthday = partner.getBirthday() != null ? utilService.dateFormat(partner.getBirthday()) : DASHED_FIELD;
        }
        rowNum = 40;
        fillSheet(sheet, rowNum, 2, partnerDocumentType);
        fillSheet(sheet, rowNum, 5, partnerDocumentNumber);
        fillSheet(sheet, rowNum, 8, partnerLastName);
        fillSheet(sheet, rowNum, 13, partnerSecondLastName);
        rowNum = 42;
        fillSheet(sheet, rowNum, 2, partnerFirstName);
        fillSheet(sheet, rowNum, 7, partnerSecondName);
        fillSheet(sheet, rowNum, 12, partnerGender);
        fillSheet(sheet, rowNum, 14, partnerBirthday);
        fillSheet(sheet, rowNum, 16, partnerMaritalStatus);

        rowNum = 44;
        fillSheet(sheet, rowNum, 2, partnerEducationLevel);
        fillSheet(sheet, rowNum, 6, partnerNationality);
        fillSheet(sheet, rowNum, 9, partnerProfession);
        fillSheet(sheet, rowNum, 13, DASHED_FIELD);


        //5.- Referencias
        List<Referral> referrals = personDao.getReferrals(person.getId(), Configuration.getDefaultLocale());
        final int REFS_LIST_SIZE = referrals == null ? 0 : referrals.size();
        final int MAX_REF_NUMB = 2;

        if (REFS_LIST_SIZE > 0) {
            List<Referral> validatedReferrals = new ArrayList<>();
            List<Referral> noValidatedReferrals = new ArrayList<>();
            referrals.forEach(
                    ref -> {
                        if (ref.getValidated()) {
                            validatedReferrals.add(ref);
                        } else {
                            noValidatedReferrals.add(ref);
                        }
                    }
            );

            if (validatedReferrals.size() == 1 || noValidatedReferrals.size() == 1) {
                fillSheet(sheet, 50, 2, DASHED_FIELD);
                fillSheet(sheet, 50, 5, DASHED_FIELD);
                fillSheet(sheet, 50, 7, DASHED_FIELD);
                fillSheet(sheet, 50, 9, DASHED_FIELD);
            }

            if (validatedReferrals.size() > 0) {
                for (int i = 0; i < Math.min(MAX_REF_NUMB, validatedReferrals.size()); i++) {
                    rowNum = i == 0 ? 48 : 50;
                    Referral referral = validatedReferrals.get(i);

                    String phoneType = referral.getPhoneType() != null ? referral.getPhoneType() : DASHED_FIELD;
                    fillSheet(sheet, rowNum, 2, referral.getFullName() != null ? referral.getFullName() : DASHED_FIELD);
                    fillSheet(sheet, rowNum, 5, DASHED_FIELD);
                    fillSheet(sheet, rowNum, 7, phoneType.equals("L") ? "FIJO" : "CELULAR");
                    fillSheet(sheet, rowNum, 9, referral.getPhoneNumber() != null ? referral.getPhoneNumber() : DASHED_FIELD);

                }
            } else if (noValidatedReferrals.size() > 0) {
                for (int i = 0; i < Math.min(MAX_REF_NUMB, noValidatedReferrals.size()); i++) {
                    rowNum = i == 0 ? 48 : 50;
                    Referral referral = noValidatedReferrals.get(i);

                    String phoneType = referral.getPhoneType() != null ? referral.getPhoneType() : DASHED_FIELD;
                    fillSheet(sheet, rowNum, 2, referral.getFullName() != null ? referral.getFullName() : DASHED_FIELD);
                    fillSheet(sheet, rowNum, 5, DASHED_FIELD);
                    fillSheet(sheet, rowNum, 7, phoneType.equals("L") ? "FIJO" : "CELULAR");
                    fillSheet(sheet, rowNum, 9, referral.getPhoneNumber() != null ? referral.getPhoneNumber() : DASHED_FIELD);
                }
            }
        } else {
            for (int x = 0; x < 2; x++) {
                rowNum = x == 0 ? 48 : 50;
                fillSheet(sheet, rowNum, 2, DASHED_FIELD);
                fillSheet(sheet, rowNum, 5, DASHED_FIELD);
                fillSheet(sheet, rowNum, 7, DASHED_FIELD);
                fillSheet(sheet, rowNum, 9, DASHED_FIELD);
            }
        }

        //Credit Data

        LoanOffer lo = loanApplicationDao.getLoanOffers(
                loanApplication.getId()).stream().filter(
                o -> o.getSelected()).findFirst().orElse(null);
        if (lo != null) {
            payDay = utilService.dateFormat(lo.getFirstDueDate()).split("/")[0];
            frecuency = "MESES";
            insurance = "INDIVIDUAL";
        }
        rowNum = 54;
        fillSheet(sheet, rowNum, 2, String.valueOf(credit.getAmount()));
        fillSheet(sheet, rowNum, 5, String.valueOf(credit.getInstallments()));
        fillSheet(sheet, rowNum, 7, utilService.percentFormat(credit.getEffectiveMonthlyRate(), credit.getCurrency()));
        fillSheet(sheet, rowNum, 9, utilService.percentFormat(credit.getEffectiveAnnualCostRate(), credit.getCurrency()));

        fillSheet(sheet, rowNum, 11, payDay);
        fillSheet(sheet, rowNum, 13, frecuency);
        fillSheet(sheet, rowNum, 15, insurance);

        //Tipo de Desembolso, Banco	,N° de Cuenta, N° Cuenta Interbancaria, Ejecutivo Call Center
        PersonBankAccountInformation personBankAccountInformation = personDao.getPersonBankAccountInformation(Configuration.getDefaultLocale(), person.getId());
        EntityProductParams entityProductParams = catalogService.getEntityProductParamById(credit.getEntityProductParameterId());
        bankAccountNumber = personBankAccountInformation != null ? personBankAccountInformation.getBankAccount() : DASHED_FIELD;
        bankName = personBankAccountInformation != null ? personBankAccountInformation.getBank().getName() : DASHED_FIELD;
        acountCci = personBankAccountInformation != null ? personBankAccountInformation.getCciCode() : DASHED_FIELD;

        if (entityProductParams.getDisbursementType() != null) {
            switch (entityProductParams.getDisbursementType()) {
                case EntityProductParams.DISBURSEMENT_TYPE_DEPOSIT:
                    disbursement = "TRANSFERENCIA";
                    break;
                case EntityProductParams.DISBURSEMENT_TYPE_RETIREMNT:
                    disbursement = "PRESCENCIAL";
                    break;
            }
        }

        if (loanApplication != null && loanApplication.getCreditAnalystSysUserId() != null) {
            Integer analystId = loanApplication.getCreditAnalystSysUserId();
            SysUser sysUser = sysUserDao.getSysUserById(analystId);
            if (sysUser != null) {
                callCenterPerson = sysUser.getUserName();
            }
        }

        rowNum = 56;
        fillSheet(sheet, rowNum, 2, disbursement);
        fillSheet(sheet, rowNum, 6, bankName);
        fillSheet(sheet, rowNum, 8, bankAccountNumber);
        fillSheet(sheet, rowNum, 11, acountCci);
        fillSheet(sheet, rowNum, 15, callCenterPerson);

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

    private static void fillSheet(Sheet sheet, int rowN, int cellN, String val) {
        sheet.getRow(rowN).getCell(cellN).setCellValue(val == null || val.length() == 0 ? DASHED_FIELD : val);
    }

    private static void fillSheet(Sheet sheet, Row row, int column, Object val) {
        row.createCell(column).setCellValue(String.valueOf(null != val ? val : ""));
    }

    public byte[] generateLeadReportSpreadSheet(final Integer entityId, final Integer MONTH, final Integer YEAR) throws Exception {

//        ByteArrayInputStream inputStream = new ByteArrayInputStream(fileService.getAssociatedFile(EFECTIVO_AL_TOQUE_TEMPLATE));

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, YEAR);
        cal.set(Calendar.MONTH, MONTH - 1);
        cal.set(Calendar.DATE, 1);
        Date date = cal.getTime();

        Workbook workbook = null;
        Sheet sheet = null;
        CellStyle valueStyle = null;
        Font valueStyleFont = null;

        logger.info("defaulting spreadsheet values");
        workbook = new XSSFWorkbook();
        //        workbook = WorkbookFactory.create(inputStream);
        CreationHelper createHelper = workbook.getCreationHelper();

        sheet = workbook.createSheet("Reporte de Leads - " + cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Configuration.getDefaultLocale()));

        CellStyle headStyle = workbook.createCellStyle();
        headStyle.setAlignment(HorizontalAlignment.CENTER);
        //    headStyle.setFillPattern(FillPatternType.BIG_SPOTS);
        Font headStyleFont = workbook.createFont();
        headStyleFont.setFontHeightInPoints((short) 9);
        headStyleFont.setBold(true);
        headStyleFont.setColor(IndexedColors.BLACK.getIndex());
        headStyle.setFont(headStyleFont);

        Row headerRow = sheet.createRow(0);

        for (int x = 0; x <= 8; x++)
            sheet.setColumnWidth(x, 15 * 350);

        createCell(headerRow, 0, headStyle).setCellValue("Fecha de registro");
        createCell(headerRow, 1, headStyle).setCellValue("Nombre y apellidos");
        createCell(headerRow, 2, headStyle).setCellValue("Tipo de documento");
        createCell(headerRow, 3, headStyle).setCellValue("Número de documento");
        createCell(headerRow, 4, headStyle).setCellValue("Fecha de Nac. (C.E.)");
        createCell(headerRow, 5, headStyle).setCellValue("Celular");
        createCell(headerRow, 6, headStyle).setCellValue("E-mail");
        createCell(headerRow, 7, headStyle).setCellValue("Producto solicitado");
        createCell(headerRow, 8, headStyle).setCellValue("Tipo de actividad");

        List<LeadLoanApplication> leadLoanApplications = loanApplicationDao.getLeadLoanApplicationsByEntityAndDate(entityId, date);

        if (null != leadLoanApplications) {
            int index = 0;

            for (LeadLoanApplication leadLoanApplication : leadLoanApplications) {
                Row row = sheet.createRow(sheet.getPhysicalNumberOfRows());

                fillSheet(sheet, row, 0, utilService.startDateFormat(leadLoanApplication.getRegisteredDate()));
                fillSheet(sheet, row, 1, leadLoanApplication.getPersonName() + " " + leadLoanApplication.getPersonFirstLastName() + " " + leadLoanApplication.getPersonSecondLastName());
                fillSheet(sheet, row, 2, catalogService.getIdentityDocumentType(leadLoanApplication.getDocumentTypeId()).getName());
                fillSheet(sheet, row, 3, leadLoanApplication.getDocNumber());

                if (leadLoanApplication.getDocumentTypeId() == IdentityDocumentType.CE)
                    fillSheet(sheet, row, 4, utilService.dateFormat(leadLoanApplication.getDob()));
                fillSheet(sheet, row, 5, leadLoanApplication.getPhone());
                fillSheet(sheet, row, 6, leadLoanApplication.getEmail());
                fillSheet(sheet, row, 7, catalogService.getLeadProductById(leadLoanApplication.getProductId()).getProduct());
                fillSheet(sheet, row, 8, catalogService.getLeadActivityById(leadLoanApplication.getActivityTypeId()).getActivityType());
                index++;
            }
        }

        try {
            org.apache.commons.io.output.ByteArrayOutputStream outStream = new org.apache.commons.io.output.ByteArrayOutputStream();
            workbook.write(outStream);
            workbook.close();
            outStream.close();
            return outStream.toByteArray();
        } catch (IOException io) {
            logger.error("Error flushing streams in document generation", io);
            return null;
        }
    }

    private Cell createCell(Row row, int column, CellStyle style) {
        Cell cell = row.createCell(column);
        if (style != null)
            cell.setCellStyle(style);
        return cell;
    }
}