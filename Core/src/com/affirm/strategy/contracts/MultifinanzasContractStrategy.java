package com.affirm.strategy.contracts;

import com.affirm.acceso.model.Direccion;
import com.affirm.common.dao.CreditDAO;
import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.model.catalog.*;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.UtilService;

import java.util.List;
import java.util.Locale;

public class MultifinanzasContractStrategy extends BaseContract {

    final int defaultFontSizeMultifinanzas = 12;

    public MultifinanzasContractStrategy(byte[] contract, CatalogService catalogService, PersonDAO personDAO, UtilService utilService,
                                         CreditDAO creditDAO, LoanApplicationDAO loanApplicationDAO) throws Exception {
        super(contract, catalogService, personDAO, utilService, creditDAO, loanApplicationDAO, null);
    }

    int getFontSize(String field) {
        if (field != null && field.length() > 18) {
            return defaultFontSizeMultifinanzas - 5;
        }
        return defaultFontSizeMultifinanzas;
    }

    public void fillPdf(Person person, Credit credit, LoanOffer loanOffer, EntityProductParams params, User user, String signature,
                        Person partner, PersonContactInformation contactInfo, List<PersonOcupationalInformation> ocupations,
                        PersonOcupationalInformation principalOcupation, PersonBankAccountInformation personBankAccountInformation,
                        LoanApplication loanApplication, Locale locale, SunatResult sunatResult) throws Exception {


        Currency currency;
        Double amount;
        Integer installments;
        Double installmentAmountAvg;
        Double scheduleInstallmentAmount;
        Double effectiveMonthlyRate;
        Double nominalAnualRate;
        Double effectiveAnualRate;
        Double effectiveAnualCostRate;
        if (credit != null) {
            currency = credit.getCurrency();
            amount = credit.getAmount();
            installments = credit.getInstallments();
            installmentAmountAvg = credit.getInstallmentAmountAvg();
            scheduleInstallmentAmount = credit.getTotalScheduleField("installmentAmount", 'O');
            effectiveMonthlyRate = credit.getEffectiveMonthlyRate();
            nominalAnualRate = credit.getNominalAnualRate();
            effectiveAnualRate = credit.getEffectiveAnnualRate();
            effectiveAnualCostRate = credit.getEffectiveAnnualCostRate();
        } else {
            currency = loanOffer.getCurrency();
            amount = loanOffer.getAmmount();
            installments = loanOffer.getInstallments();
            installmentAmountAvg = loanOffer.getInstallmentAmountAvg();
            scheduleInstallmentAmount = loanOffer.getTotalScheduleField("installmentAmount");
            effectiveMonthlyRate = loanOffer.getMonthlyRate();
            nominalAnualRate = loanOffer.getNominalAnualRate();
            effectiveAnualRate = loanOffer.getEffectiveAnualRate();
            effectiveAnualCostRate = loanOffer.getEffectiveAnnualCostRate();
        }

        fillText("person.fullName", person.getFullName(), defaultFont, defaultFontSizeMultifinanzas);

        String dni = "";
        String le = "";
        String lc = "";
        String ci = "";
        if (person.getDocumentType() != null) {
            switch (person.getDocumentType().getId()) {
                case IdentityDocumentType.DNI:
                    dni = "X";
                    break;
                case IdentityDocumentType.CDI:
                    dni = "X";
                    break;
            }
        }

        fillText("person.dniYes", dni, defaultFont, defaultFontSizeMultifinanzas);
        fillText("person.leYes", le, defaultFont, defaultFontSizeMultifinanzas);
        fillText("person.lcYes", lc, defaultFont, defaultFontSizeMultifinanzas);
        fillText("person.ciYes", ci, defaultFont, defaultFontSizeMultifinanzas);

        fillText("person.documentNumber", person.getDocumentNumber() != null && person.getDocumentNumber().length() >= 10 ? person.getDocumentNumber().substring(2, 10) : null, defaultFont, defaultFontSizeMultifinanzas);
        fillText("person.cuit", person.getDocumentNumber() != null ? person.getDocumentNumber() : null, defaultFont, defaultFontSizeMultifinanzas);
        fillText("person.citizenship", person.getNationality() != null ? person.getNationality().getName() : null, defaultFont, defaultFontSizeMultifinanzas);
        fillText("person.birthday", utilService.dateFormat(person.getBirthday()), defaultFont, defaultFontSizeMultifinanzas);
        fillText("person.email", user.getEmail(), defaultFont, defaultFontSizeMultifinanzas);
        fillText("person.activity", person.getProfession() != null ? person.getProfession().getProfession() : null, defaultFont, defaultFontSizeMultifinanzas);
        //principalOcupation.getOcupation().
        if (person.getGender() != null) {
            fillText(person.getGender().equals('M') ? "person.m" : "person.f", "X", defaultFont, defaultFontSizeMultifinanzas);
        }

        String married = "";
        String single = "";
        String widow = "";
        String divorce = "";
        if (person.getMaritalStatus() != null) {
            switch (person.getMaritalStatus().getId()) {
                case MaritalStatus.MARRIED:
                    married = "X";
                    break;
                case MaritalStatus.WIDOWED:
                    widow = "X";
                    break;
                case MaritalStatus.SINGLE:
                    single = "X";
                    break;
                case MaritalStatus.DIVORCED:
                    divorce = "X";
                    break;
            }
        }

        fillText("person.married", married, defaultFont, defaultFontSizeMultifinanzas);
        fillText("person.single", single, defaultFont, defaultFontSizeMultifinanzas);
        fillText("person.widowed", widow, defaultFont, defaultFontSizeMultifinanzas);
        fillText("person.divorced", divorce, defaultFont, defaultFontSizeMultifinanzas);

        String department = null;
        String province = null;
        String district = null;
        String postalCode = null;
        String street = null;
        String locality = null;
        Direccion disagregatedAddress = personDAO.getDisggregatedAddress(person.getId(), "H");
        District generalDistrict = null;
        if (disagregatedAddress != null) {
            //fillText("person.streetName",contactInfo.getAddressStreetName() , defaultFont, defaultFontSizeMultifinanzas);
            //fillText("person.streetNumber", contactInfo.getAddressStreetNumber(), defaultFont, defaultFontSizeMultifinanzas);
            street = disagregatedAddress.getNombreVia();
            fillText("person.streetName", street, defaultFont, getFontSize(street));
            fillText("person.streetNumber", disagregatedAddress.getNumeroVia(), defaultFont, defaultFontSizeMultifinanzas);
            fillText("person.floor", disagregatedAddress.getFloor(), defaultFont, defaultFontSizeMultifinanzas);
            generalDistrict = catalogService.getGeneralDistrictById(disagregatedAddress.getLocalityId());
            if (generalDistrict != null) {
                fillText("person.cp", generalDistrict.getPostalCode(), defaultFont, defaultFontSizeMultifinanzas);
            }
        }
        locality = contactInfo.getDistrict() != null ? contactInfo.getDistrict().getName() : null;
        fillText("person.locality", locality, defaultFont, getFontSize(locality));
        province = contactInfo.getProvince() != null ? contactInfo.getProvince().getName() : null;
        fillText("person.pcia", province, defaultFont, getFontSize(province));
        fillText("person.phoneNumber", contactInfo.getPhoneNumber(), defaultFont, defaultFontSizeMultifinanzas);
        fillText("person.cellphoneNumber", null, defaultFont, defaultFontSizeMultifinanzas);
        fillText("person.laboral", principalOcupation.getPhoneNumber(), defaultFont, defaultFontSizeMultifinanzas);

        Direccion jobAddress = personDAO.getDisggregatedAddress(person.getId(), "J");

        //PersonOcupationalInformation principalOcupation = personDAO.getPersonOcupationalInformation(locale, person.getId())
        //        .stream().filter(o -> o.getNumber() == PersonOcupationalInformation.PRINCIPAL)
        //        .findFirst().orElse(null);

        if (jobAddress != null) {
            generalDistrict = catalogService.getGeneralDistrictById(jobAddress.getLocalityId());
            if (generalDistrict != null) {
                province = generalDistrict.getProvince().getName() + "";
                district = generalDistrict.getName() + "";
                postalCode = generalDistrict.getPostalCode();
            }
            street = jobAddress.getNombreVia();
            fillText("job.streetName", street, defaultFont, getFontSize(street));
            fillText("job.streetNumber", jobAddress.getNumeroVia(), defaultFont, defaultFontSizeMultifinanzas);
            fillText("job.department", jobAddress.getNumeroInterior(), defaultFont, defaultFontSizeMultifinanzas);

        }

        // INFORMACION DE LA EMPRESA EN QUE TRABAJA
        if (principalOcupation.getActivityType() != null) {
            fillText(principalOcupation.getActivityType().getId() == ActivityType.DEPENDENT ? "person.dependencyYes" : "person.dependencyNo", "X", defaultFont, defaultFontSizeMultifinanzas);
        }
        String companyName = principalOcupation.getCompanyName();
        String companyCuit = principalOcupation.getRuc();
        if ((companyName == null || companyName.equals("")) && companyCuit != null && !companyCuit.equals("")) {
            companyName = "CUIT: " + companyCuit;
        }

        fillText("job.companyName", companyName, defaultFont, defaultFontSizeMultifinanzas);
        fillText("job.startDate", utilService.startDateFormat(principalOcupation.getStartDate()), defaultFont, defaultFontSizeMultifinanzas);

        fillText("job.locality", district, defaultFont, getFontSize(district));
        fillText("job.cp", postalCode, defaultFont, defaultFontSizeMultifinanzas);
        fillText("job.pcia", province, defaultFont, getFontSize(province));


        ///INFORMACION OCUPACIONAL
        fillText("person.jobTitle", principalOcupation.getOcupation() != null ? principalOcupation.getOcupation().getOcupation() : null, defaultFont, defaultFontSizeMultifinanzas);
        fillText("person.income", utilService.doubleOnlyMoneyFormat(principalOcupation.getFixedGrossIncome(), currency), defaultFont, defaultFontSizeMultifinanzas);
        fillText("person.otherIncome", principalOcupation.getOtherIncome(), defaultFont, defaultFontSizeMultifinanzas);
        fillText("job.locality", district, defaultFont, getFontSize(district));
        fillText("productDestiny", "", defaultFont, defaultFontSizeMultifinanzas);
        fillText("person.cbu", personBankAccountInformation.getCciCode(), defaultFont, defaultFontSizeMultifinanzas);
        fillText("person.accountType", personBankAccountInformation.getAccountType(), defaultFont, defaultFontSizeMultifinanzas);
        String bankName = personBankAccountInformation.getBank() != null ? personBankAccountInformation.getBank().getName() : null;
        fillText("person.bankName", bankName, defaultFont, getFontSize(bankName));

        /////////////pareja////
        partner = person.getPartner();
        fillText("partner.fullname", partner != null ? partner.getFullName() : null, defaultFont, defaultFontSizeMultifinanzas);

        dni = "";
        le = "";
        lc = "";
        ci = "";
        if (partner != null) {
            if (partner.getDocumentType() != null) {
                switch (partner.getDocumentType().getId()) {
                    case IdentityDocumentType.DNI:
                        dni = "X";
                        break;
                    case IdentityDocumentType.CDI:
                        ci = "X";
                        break;
                }
            }

            fillText("partner.dniYes", dni, defaultFont, defaultFontSizeMultifinanzas);
            fillText("partner.leYes", le, defaultFont, defaultFontSizeMultifinanzas);
            fillText("partner.lcYes", lc, defaultFont, defaultFontSizeMultifinanzas);
            fillText("partner.ciYes", ci, defaultFont, defaultFontSizeMultifinanzas);
            fillText("partner.ciEmiter", null, defaultFont, defaultFontSizeMultifinanzas);

            fillText("partner.documentNumber", partner != null ? partner.getDocumentNumber() != null && partner.getDocumentNumber().length() >= 10 ? partner.getDocumentNumber().substring(2, 10) : null : null, defaultFont, defaultFontSizeMultifinanzas);
            fillText("partner.cuit", partner != null ? partner.getDocumentNumber() : null, defaultFont, defaultFontSizeMultifinanzas);
            fillText("partner.nationality", partner != null ? partner.getNationality().getName() : null, defaultFont, defaultFontSizeMultifinanzas);
            fillText("partner.birthday", partner != null ? partner.getBirthday() : null, defaultFont, defaultFontSizeMultifinanzas);

            married = "";
            single = "";
            widow = "";
            divorce = "";

            if (partner.getMaritalStatus() != null) {
                switch (partner.getMaritalStatus().getId()) {
                    case MaritalStatus.MARRIED:
                        married = "X";
                        break;
                    case MaritalStatus.WIDOWED:
                        widow = "X";
                        break;
                    case MaritalStatus.SINGLE:
                        single = "X";
                        break;
                    case MaritalStatus.DIVORCED:
                        divorce = "X";
                        break;
                }
            }

            fillText("partner.married", married, defaultFont, defaultFontSizeMultifinanzas);
            fillText("partner.single", single, defaultFont, defaultFontSizeMultifinanzas);
            fillText("partner.widowed", widow, defaultFont, defaultFontSizeMultifinanzas);
            fillText("partner.divorced", divorce, defaultFont, defaultFontSizeMultifinanzas);

            if (partner.getGender() != null) {
                fillText(partner.getGender().equals('M') ? "partner.m" : "partner.f", "X", defaultFont, defaultFontSizeMultifinanzas);
            }
        }

        Double stampTax = 0.0;
        LoanOffer lo = loanApplicationDAO.getLoanOffers(loanApplication.getId()).stream().filter(o -> o.getSelected()).findFirst().orElse(null);
        if (lo != null) {
            stampTax = lo.getStampTax();
            fillText("otherCosts", "Impuesto de sellos " + utilService.doubleMoneyFormat(stampTax, currency) + " - Cancelación anticipada 3% + IVA", defaultFont, defaultFontSizeMultifinanzas - 4);
        }

        fillText("amount", utilService.doubleOnlyMoneyFormat(amount + stampTax, currency), defaultFont, defaultFontSizeMultifinanzas + 5);
        fillText("months", installments, defaultFont, defaultFontSizeMultifinanzas + 5);
        fillText("installments", utilService.doubleOnlyMoneyFormat(installmentAmountAvg, currency), defaultFont, defaultFontSizeMultifinanzas + 5);
        fillText("paybackAmount", utilService.doubleOnlyMoneyFormat(scheduleInstallmentAmount, currency), defaultFont, defaultFontSizeMultifinanzas);
        fillText("paymentPlace", "Débito en cuenta. Maipú 272, Ciudad Autónoma de Bs. As.", defaultFont, defaultFontSizeMultifinanzas - 3);
        fillText("tem", utilService.doubleOnlyMoneyFormat(effectiveMonthlyRate, currency), defaultFont, defaultFontSizeMultifinanzas);
        fillText("tna", utilService.doubleOnlyMoneyFormat(nominalAnualRate, currency), defaultFont, defaultFontSizeMultifinanzas);
        fillText("tea", utilService.doubleOnlyMoneyFormat(effectiveAnualRate, currency), defaultFont, defaultFontSizeMultifinanzas);
        fillText("administrativeCost", "", defaultFont, defaultFontSizeMultifinanzas - 7);
        fillText("totalFinanceCost", utilService.doubleOnlyMoneyFormat(effectiveAnualCostRate, currency), defaultFont, defaultFontSizeMultifinanzas);

    }
}
