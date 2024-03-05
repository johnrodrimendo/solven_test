package com.affirm.strategy.contracts;

import com.affirm.common.dao.CreditDAO;
import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.model.PreApprovedInfo;
import com.affirm.common.model.catalog.*;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.UtilService;

import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RipleyContractStrategy extends BaseContract {

    public RipleyContractStrategy(byte[] contract, CatalogService catalogService, PersonDAO personDAO, UtilService utilService, CreditDAO creditDAO, LoanApplicationDAO loanApplicationDAO) throws Exception {
        super(contract, catalogService, personDAO, utilService, creditDAO, loanApplicationDAO, null);
    }

    @Override
    public void fillPdf(Person person,
                        Credit credit,
                        LoanOffer loanOffer,
                        EntityProductParams params,
                        User user,
                        String signature,
                        Person partner,
                        PersonContactInformation contactInfo,
                        List<PersonOcupationalInformation> ocupations,
                        PersonOcupationalInformation principalOcupation,
                        PersonBankAccountInformation personBankAccountInformation,
                        LoanApplication loanApplication,
                        Locale locale,
                        SunatResult sunatResult) throws Exception {

        Entity entity;
        Double amount;
        Integer installments;
        Double effectiveAnualRate;
        Double effectiveAnualCostRate;
        Double installmentAmountAvg;
        if (credit != null) {
            entity = credit.getEntity();
            amount = credit.getAmount();
            installments = credit.getInstallments();
            effectiveAnualRate = credit.getEffectiveAnnualRate();
            effectiveAnualCostRate = credit.getEffectiveAnnualCostRate();
            installmentAmountAvg = credit.getInstallmentAmountAvg();
        } else {
            entity = loanOffer.getEntity();
            amount = loanOffer.getAmmount();
            installments = loanOffer.getInstallments();
            effectiveAnualRate = loanOffer.getEffectiveAnualRate();
            effectiveAnualCostRate = loanOffer.getEffectiveAnnualCostRate();
            installmentAmountAvg = loanOffer.getInstallmentAmountAvg();
        }

        List<PreApprovedInfo> personPreApprovedInfos = personDAO.getPreApprovedData(person.getDocumentType().getId(), person.getDocumentNumber());
        PreApprovedInfo personPreApprovedInfo = null;
        if (personPreApprovedInfos != null) {
            personPreApprovedInfo = personPreApprovedInfos.stream().filter(p -> p.getEntity().getId().intValue() == entity.getId()).findFirst().orElse(null);
        }

        if (personPreApprovedInfo != null) {
            fillText("creditCard.number", personPreApprovedInfo.getCardNumber(), 8);
            if (personPreApprovedInfo.getCardNumber() != null) {
                for (int i = 0; i < personPreApprovedInfo.getCardNumber().length() && i <= 16; i++) {
                    fillText("creditCard.numbers." + (i + 1), personPreApprovedInfo.getCardNumber().charAt(i), 8);
                }
            }
            if (personPreApprovedInfo.getCardType() != null) {
                fillCheckBox("creditCard.type.clasica", false);
                fillCheckBox("creditCard.type.silver", personPreApprovedInfo.getCardType().trim().equalsIgnoreCase("silver"));
                fillCheckBox("creditCard.type.gold", personPreApprovedInfo.getCardType().trim().equalsIgnoreCase("gold"));
            }
            if (personPreApprovedInfo.getPaymentDay() != null) {
                fillCheckBox("credit.fechaPago.1", personPreApprovedInfo.getPaymentDay() == 1);
                fillCheckBox("credit.fechaPago.5", personPreApprovedInfo.getPaymentDay() == 5);
                fillCheckBox("credit.fechaPago.10", personPreApprovedInfo.getPaymentDay() == 10);
                fillCheckBox("credit.fechaPago.15", personPreApprovedInfo.getPaymentDay() == 15);
                fillCheckBox("credit.fechaPago.20", personPreApprovedInfo.getPaymentDay() == 20);
            }
            fillText("credit.paymentDay", personPreApprovedInfo.getPaymentDay(), 8);
        }

        fillCheckBox("person.docType.dni", person.getDocumentType().getId() == IdentityDocumentType.DNI);
        fillCheckBox("person.docType.ce", person.getDocumentType().getId() == IdentityDocumentType.CE);
        fillText("person.docNumber", person.getDocumentNumber(), 8);
        fillText("person.firstSurname", person.getFirstSurname(), 8);
        fillText("preson.lastSurname", person.getLastSurname(), 8);
        fillText("person.name", person.getName(), 8);
        fillText("person.fullName", person.getFullName(), 8);
        fillCheckBox("person.gender.male", person.getGender() != null && person.getGender() == 'M');
        fillCheckBox("person.gender.female", person.getGender() != null && person.getGender() == 'F');
        if (person.getMaritalStatus() != null) {
            String maritalStatusCode = "";
            switch (person.getMaritalStatus().getId()) {
                case MaritalStatus.MARRIED:
                case MaritalStatus.COHABITANT:
                    maritalStatusCode = "1";
                    break;
                case MaritalStatus.SINGLE:
                    maritalStatusCode = "2";
                    break;
            }
            fillText("person.maritalStatusCode", maritalStatusCode, 8);
            fillText("person.maritalStatus.name", person.getMaritalStatus().getStatus(), 8);
        }
        fillText("person.landLine", null, 8);
        fillText("person.phoneNumber", user.getPhoneNumber(), 8);
        fillText("person.email", user.getEmail(), 8);
        fillText("person.nationality", person.getNationality().getName(), 8);
        fillText("person.profession", person.getProfession() != null ? person.getProfession().getProfession() : null, 8);
        if (person.getBirthday() != null) {
            fillText("person.birthday.d1", utilService.dateCustomFormat(person.getBirthday(), "dd", locale).charAt(0), 8);
            fillText("person.birthday.d2", utilService.dateCustomFormat(person.getBirthday(), "dd", locale).charAt(1), 8);
            fillText("person.birthday.m1", utilService.dateCustomFormat(person.getBirthday(), "MM", locale).charAt(0), 8);
            fillText("person.birthday.m2", utilService.dateCustomFormat(person.getBirthday(), "MM", locale).charAt(1), 8);
            fillText("person.birthday.y1", utilService.dateCustomFormat(person.getBirthday(), "yyyy", locale).charAt(0), 8);
            fillText("person.birthday.y2", utilService.dateCustomFormat(person.getBirthday(), "yyyy", locale).charAt(1), 8);
            fillText("person.birthday.y3", utilService.dateCustomFormat(person.getBirthday(), "yyyy", locale).charAt(2), 8);
            fillText("person.birthday.y4", utilService.dateCustomFormat(person.getBirthday(), "yyyy", locale).charAt(3), 8);

            fillText("person.birthday.dd", utilService.dateCustomFormat(person.getBirthday(), "dd", locale), 8);
            fillText("person.birthday.mm", utilService.dateCustomFormat(person.getBirthday(), "MM", locale), 8);
            fillText("person.birthday.yyyy", utilService.dateCustomFormat(person.getBirthday(), "yyyy", locale), 8);
        }
        fillText("person.address.streetName", contactInfo.getAddressStreetName(), 8);
        fillText("person.address.nro", null, 8);
        fillText("person.address.interior", null, 8);
        fillText("person.address.urbanizacion", null, 8);
        fillText("person.address.district", contactInfo.getAddressUbigeo().getDistrict().getName(), 8);
        fillText("person.address.province", contactInfo.getAddressUbigeo().getProvince().getName(), 8);
        fillText("person.address.department", contactInfo.getAddressUbigeo().getDepartment().getName(), 8);
        fillText("person.address.reference", null, 8);
        if (principalOcupation != null) {
            fillText("person.netIncome", principalOcupation.getFixedGrossIncome(), 8);
            fillCheckBox("person.ocupationalInformation.dependent", principalOcupation.getActivityType().getId() == ActivityType.DEPENDENT);
            fillCheckBox("person.ocupationalInformation.independent", principalOcupation.getActivityType().getId() == ActivityType.INDEPENDENT);
            if (principalOcupation.getOcupation() != null) {
                fillText("person.ocupation", principalOcupation.getOcupation().getOcupation(), 8);
            }
        }

        fillText("credit.amount", amount, 8);
        fillText("credit.installments", installments, 8);
        fillText("credit.tea", effectiveAnualRate, 8);
        fillText("credit.tcea", effectiveAnualCostRate, 8);
        fillText("credit.installmentAmount", installmentAmountAvg, 8);

        fillText("credit.plazoGracia", null, 8);

        fillText("actualDate.city", "Lima", 8);
        fillText("actualDate.dd", utilService.dateCustomFormat(new Date(), "dd", locale), 8);
        fillText("actualDate.mmmm", utilService.dateCustomFormat(new Date(), "MMMM", locale), 8);
        fillText("actualDate.yyyy", utilService.dateCustomFormat(new Date(), "yyyy", locale), 8);


        // If credit > 8000 remove pages: Seguro proteccion, cretificado seguro proteccion
        if (amount > 8000) {
            pdfDoc.removePage(13);
            pdfDoc.removePage(12);
            pdfDoc.removePage(11);
            pdfDoc.removePage(10);
        }
    }
}
