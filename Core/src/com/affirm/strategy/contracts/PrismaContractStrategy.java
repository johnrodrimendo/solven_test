package com.affirm.strategy.contracts;

import com.affirm.acceso.model.Direccion;
import com.affirm.common.dao.CreditDAO;
import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.model.catalog.*;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.UtilService;
import com.affirm.common.util.NumberToTextConverter;
import com.affirm.system.configuration.Configuration;
import com.google.common.primitives.Ints;
import edu.emory.mathcs.backport.java.util.Arrays;
import org.apache.commons.lang.time.DateUtils;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class PrismaContractStrategy extends BaseContract {

    private static final String MARK = "X";
    private static final List<Integer> GROUP_COMMERCE = Ints.asList(30);
    private static final List<Integer> GROUP_SERVICES = Ints.asList(13, 33, 34, 16, 37, 27, 201, 41, 42, 43, 44, 45, 48, 49);
    private static final List<Integer> GROUP_AGRICULTURAL = Ints.asList(28, 46);
    private static final List<Integer> GROUP_PRODUCTION = Ints.asList(29, 31, 35, 38, 39, 202, 40, 47);

    public PrismaContractStrategy(byte[] contract, CatalogService catalogService, PersonDAO personDAO, UtilService utilService, CreditDAO creditDAO, LoanApplicationDAO loanApplicationDAO) throws Exception {
        super(contract, catalogService, personDAO, utilService, creditDAO, loanApplicationDAO, null);
        this.defaultFont = prepareFont(pdfDoc, "/Arial.ttf");
    }

    @Override
    public void fillPdf(Person person, Credit credit, LoanOffer loanOffer, EntityProductParams params, User user, String signature, Person partner, PersonContactInformation contactInfo, List<PersonOcupationalInformation> ocupations, PersonOcupationalInformation principalOcupation, PersonBankAccountInformation personBankAccountInformation, LoanApplication loanApplication, Locale locale, SunatResult sunatResult) throws Exception {
        String defaultNoAplicaText = "N/A";
        Double efectiveAnualCostRate = null;
        Double admissionTotalIncome = null;
        Double effectiveAnualRate = null;
        Integer installments = null;

        commonFillText("first_surname", person.getFirstSurname(), 7);
        commonFillText("last_surname", person.getLastSurname(), 7);
        commonFillText("person_name", person.getName(), 7);
        commonFillText("document_number", person.getDocumentNumber(), 7);
        commonFillText("birthdate", new SimpleDateFormat("dd/MM/yyyy").format(person.getBirthday()), 7);

        if (person.getMaritalStatus() != null) {
            switch (person.getMaritalStatus().getId()) {
                case MaritalStatus.MARRIED:
                    commonFillText("married", MARK, 7);
                    break;
                case MaritalStatus.SINGLE:
                    commonFillText("single", MARK, 7);
                    break;
                case MaritalStatus.WIDOWED:
                    commonFillText("widowed", MARK, 7);
                    break;
                case MaritalStatus.COHABITANT:
                    commonFillText("cohabitant", MARK, 7);
                    break;
                case MaritalStatus.DIVORCED:
                    commonFillText("divorced", MARK, 7);
                    break;
            }
        }

        if (person.getGender() != null) {
            if (person.getGender() == 'M') {
                commonFillText("male", MARK, 7);
            } else if (person.getGender() == 'F') {
                commonFillText("female", MARK, 7);
            }
        }

        Direccion disaggregatedHomeAddress = personDAO.getDisggregatedAddress(person.getId(), "H");
        Ubigeo ubigeo = null;
        if (disaggregatedHomeAddress != null) {
            ubigeo = catalogService.getUbigeo(disaggregatedHomeAddress.getUbigeo());
            commonFillText("district", ubigeo.getDistrict().getName(), 7);
            commonFillText("province", ubigeo.getProvince().getName(), 7);
            commonFillText("department", ubigeo.getDepartment().getName(), 7);
        } else if (contactInfo != null && contactInfo.getAddressUbigeo() != null) {
            commonFillText("district", contactInfo.getAddressUbigeo().getDistrict().getName(), 7);
            commonFillText("province", contactInfo.getAddressUbigeo().getProvince().getName(), 7);
            commonFillText("department", contactInfo.getAddressUbigeo().getDepartment().getName(), 7);
        }

        commonFillText("home_address", contactInfo != null ? contactInfo.getAddressStreetName() : null, 7);
        commonFillText("phone_number", user.getPhoneNumber(), 7);

        if (principalOcupation != null) {
            switch (principalOcupation.getActivityType().getId()) {
                case ActivityType.INDEPENDENT: {
                    commonFillText("independent", MARK, 7);
                    commonFillText("main_economic_activity", person.getProfession().getProfession(), 7);

                    if (person.getProfession() != null) {
                        if (GROUP_COMMERCE.contains(person.getProfession().getId())) {
                            commonFillText("commerce", MARK, 7);
                        } else if (GROUP_SERVICES.contains(person.getProfession().getId())) {
                            commonFillText("services", MARK, 7);
                        } else if (GROUP_AGRICULTURAL.contains(person.getProfession().getId())) {
                            commonFillText("agricultural", MARK, 7);
                        } else if (GROUP_PRODUCTION.contains(person.getProfession().getId())) {
                            commonFillText("production", MARK, 7);
                        }
                    }

                    break;
                }
                case ActivityType.DEPENDENT: {
                    commonFillText("dependent", MARK, 7);
                    commonFillText("commercial_name", principalOcupation.getCompanyName(), 7);
                    commonFillText("workplace_addresss", principalOcupation.getAddress(), 7);
                    commonFillText("payroll_contract", MARK, 7);
                    commonFillText("job_phone_number", principalOcupation.getPhoneNumber(), 7);
                    commonFillText("income", utilService.doubleMoneyFormat(principalOcupation.getFixedGrossIncome()), 7);
                    commonFillText("occupation", principalOcupation.getOcupation() != null ? principalOcupation.getOcupation().getOcupation() : defaultNoAplicaText, 7);

                    if (principalOcupation.getStartDate() != null) {
                        LocalDate startDateLocal = principalOcupation.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                        LocalDate nowDateLocal = new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

                        commonFillText("job_start_date", new SimpleDateFormat("dd/MM/yyyy").format(principalOcupation.getStartDate()), 7);
                        commonFillText("employment_time", ChronoUnit.MONTHS.between(startDateLocal, nowDateLocal) + " meses", 7);
                    }

                    break;
                }
            }

            PersonProfessionOccupation professionOccupation = person.getProfessionOccupation();

            commonFillText("profession_occupation",
                    (professionOccupation != null ? professionOccupation.getOccupation() : defaultNoAplicaText) + "/" +
                    (principalOcupation.getOcupation() != null ? principalOcupation.getOcupation().getOcupation() : defaultNoAplicaText)
                    , 6
            );
        }

        if (person.getStudyLevel() != null) {
            commonFillText("study_level", person.getStudyLevel().getLevel(), 6);
        }

        commonFillText("email", user.getEmail(), 6);

        if (partner != null) {
            commonFillText("partner_first_surname", partner.getFirstSurname(), 7);
            commonFillText("partner_last_surname", partner.getLastSurname(), 7);
            commonFillText("partner_name", partner.getName(), 7);
            commonFillText("partner_document_number", partner.getDocumentNumber(), 7);
            commonFillText("partner_birthdate", partner.getBirthday() != null ? new SimpleDateFormat("dd/MM/yyyy").format(partner.getBirthday()) : null, 7);
        }

        commonFillText("sign_document_number", person.getDocumentNumber(), 7);
        commonFillText("sign_current_date", new SimpleDateFormat("dd/MM/yyyy").format(new Date()), 7);
        commonFillText("current_day", new SimpleDateFormat("dd").format(new Date()), 7);
        commonFillText("current_month", new SimpleDateFormat("MM").format(new Date()), 7);
        commonFillText("current_year", new SimpleDateFormat("yyyy").format(new Date()), 7);

        DateFormatSymbols sym = DateFormatSymbols.getInstance(new Locale("es","es"));
        sym.setMonths(new String[]{"Enero","Febrero","Marzo","Abril","Mayo","Junio","Julio","Agosto","Septiembre","Octubre","Noviembre","Diciembre"});

        //CONTRACT
        commonFillText("day", new SimpleDateFormat("dd").format(new Date()), 10);
        commonFillText("month", new SimpleDateFormat("MMMM",sym).format(new Date()), 10);
        commonFillText("year", new SimpleDateFormat("yy").format(new Date()), 10);
        commonFillText("person_name_contract", person.getFullName(), 5);
        commonFillText("person_document_number_contract", person.getDocumentNumber(), 7);
        commonFillText("person_address_contract", contactInfo != null ? contactInfo.getAddressStreetName() : null, 5);
        fillText("person_sing_contract", signature != null ? signature : " ", handWritingFont, 15);

        // PAGARE
        double amount;
        if (credit != null) {
            amount = credit.getAmount();
            effectiveAnualRate = credit.getEffectiveAnnualRate();
            efectiveAnualCostRate = credit.getEffectiveAnnualCostRate();
            admissionTotalIncome = credit.getAdmissionTotalIncome();
            installments = credit.getInstallments();
        } else {
            amount = loanApplication.getAmount();
            effectiveAnualRate = loanOffer.getEffectiveAnualRate();
            efectiveAnualCostRate = loanOffer.getEffectiveAnnualCostRate();
            admissionTotalIncome = loanOffer.getAdmissionTotalIncome();
            installments = loanOffer.getInstallments();
        }
        commonFillText("amount_pagare", "");
        commonFillText("expiration_date_pagare", "", 7);
        commonFillText("amount_text_pagare", "", 7);
        commonFillText("amount_formatted_pagare", "", 7);
        commonFillText("interest_pagare", "", 7);
        commonFillText("next_interest_pagare", "", 7);
        commonFillText("interest_2_pagare", "", 7);
        commonFillText("interest_3_pagare", "", 7);
        commonFillText("interest_3_next_pagare", "", 7);
        commonFillText("day_week_pagare", new SimpleDateFormat("EEEE", Configuration.getDefaultLocale()).format(new Date()), 7);
        commonFillText("day_month_pagare", new SimpleDateFormat("dd").format(new Date()), 7);
        commonFillText("month_text_pagare", new SimpleDateFormat("MMMM",sym).format(new Date()), 7);
        commonFillText("year_pagare", new SimpleDateFormat("yyyy").format(new Date()), 7);
        commonFillText("person_name_pagare", person.getFullName(), 8);
        commonFillText("address_pagare", contactInfo != null ? contactInfo.getAddressStreetName() : null, 8);
        commonFillText("doc_number_pagare", person.getDocumentNumber(), 8);
        fillText("sign_pagare", signature != null ? signature : " ", handWritingFont, 15);

        // HOJA RESUMEN
        commonFillText("person_fullname_summary", person.getFullName(), 7);
        commonFillText("address_summary", contactInfo != null ? contactInfo.getAddressStreetName() : null, 7);
        commonFillText("doc_number_summary", person.getDocumentNumber(), 7);
        commonFillText("amount_summary", amount, 7);
        commonFillText("tcea_summary", utilService.percentFormat(efectiveAnualCostRate, loanApplication.getCurrency()), 7); //TODO: PREGUNTAR
        commonFillText("total_amount_interest_summary", admissionTotalIncome, 7);
        commonFillText("tea_summary", utilService.percentFormat(effectiveAnualRate, loanApplication.getCurrency()), 7); //TODO: PREGUNTAR
        commonFillText("installments_summary", installments, 7);
        commonFillText("tma_summary", "", 7);//TODO
        commonFillText("currency_summary", "SOL", 7);
        commonFillText("comision_cobranza_morosa_summary", "", 7);//TODO
        commonFillText("installments_number_summary", installments, 7);
        commonFillText("gastos_notarial_cobranza_morosa_summary", "", 7);//TODO
        commonFillText("payment_frecuency_summary", "Mensual", 7);//TODO: PREGUNTAR
        commonFillText("gastos_notarial_protesto_pagare_summary", "", 7);//TODO
        commonFillText("payment_method_summary", "", 7);//TODO: PREGUNTAR
        commonFillText("total_debt_amount_summary", "", 7); //TODO: PREGUNTAR
        commonFillText("grace_period_summary", "", 7);//TODO: PREGUNTAR
        commonFillText("insurance_company_summary", "", 7);//TODO
        commonFillText("insurance_code_summary", "", 7);//TODO
        commonFillText("insurance_rate_summary", "", 7);//TODO
        commonFillText("zone_and_date_summary", String.format("%s, %s de %s de %s",
                ubigeo != null ? ubigeo.getDistrict().getName() : contactInfo != null ? contactInfo.getAddressUbigeo().getDistrict().getName() : "",
                new SimpleDateFormat("dd").format(new Date()),
                new SimpleDateFormat("MMMM",sym).format(new Date()),
                new SimpleDateFormat("yyyy").format(new Date())), 7);//TODO
        commonFillText("person_fullname_2_summary", person.getFullName(), 7);
        commonFillText("address_2_summary", contactInfo != null ? contactInfo.getAddressStreetName() : null, 7);
        commonFillText("doc_number_2_summary", person.getDocumentNumber(), 7);
        fillText("sign_summary", signature != null ? signature : " ", handWritingFont, 15);


    }

    private void commonFillText(String fieldName, Object value){
        fillText(fieldName, value != null ? value.toString().toUpperCase() + "" : "", defaultFont, 8);
    }

    private void commonFillText(String fieldName, Object value, int fontSize){
        fillText(fieldName, value != null ? value.toString().toUpperCase() + "" : "", defaultFont, fontSize);
    }
}
