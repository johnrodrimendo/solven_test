package com.affirm.strategy.contracts;

import com.affirm.acceso.model.Direccion;
import com.affirm.common.dao.CreditDAO;
import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.model.catalog.*;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.UtilService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AccesoContractStrategy extends BaseContract {
    public AccesoContractStrategy(byte[] contract, CatalogService catalogService, PersonDAO personDAO, UtilService utilService, CreditDAO creditDAO, LoanApplicationDAO loanApplicationDAO) throws Exception {
        super(contract, catalogService, personDAO, utilService, creditDAO, loanApplicationDAO, null);
    }

    @Override
    public void fillPdf(Person person, Credit credit, LoanOffer loanOffer, EntityProductParams params, User user, String signature, Person partner, PersonContactInformation contactInfo, List<PersonOcupationalInformation> ocupations, PersonOcupationalInformation principalOcupation, PersonBankAccountInformation personBankAccountInformation, LoanApplication loanApplication, Locale locale, SunatResult sunatResult) throws Exception {

        Direccion disaggregatedHomeAddress;
        Double amount;
        Double downPayment;
        Double exchangeRate;
        String entityCreditCode;
        Integer installments;
        Double installmentAmountAvg;
        Double carInsuranceAmount;
        Double effectiveMonthlyRate;
        if (credit != null) {
            disaggregatedHomeAddress = personDAO.getDisaggregatedHomeAddressByCredit(person.getId(), credit.getId());
            amount = credit.getAmount();
            downPayment = credit.getDownPayment();
            exchangeRate = credit.getExchangeRate();
            entityCreditCode = credit.getEntityCreditCode();
            installments = credit.getInstallments();
            installmentAmountAvg = credit.getInstallmentAmountAvg();
            carInsuranceAmount = credit.getOriginalSchedule().get(0).getCarInsurance();
            effectiveMonthlyRate = credit.getEffectiveMonthlyRate();
        } else {
            disaggregatedHomeAddress = personDAO.getDisggregatedAddress(person.getId(), "H");
            amount = loanOffer.getAmmount();
            downPayment = loanOffer.getDownPayment();
            exchangeRate = loanOffer.getExchangeRate();
            entityCreditCode = null;
            installments = loanOffer.getInstallments();
            installmentAmountAvg = loanOffer.getInstallmentAmountAvg();
            carInsuranceAmount = loanOffer.getOfferSchedule().get(0).getCarInsurance();
            effectiveMonthlyRate = loanOffer.getMonthlyRate();
        }


        fillText("loanApplication.date", new SimpleDateFormat("dd/MM/yyyy").format(new Date()), 10);
        fillCheckBox("credit.type.consumo", true);
        fillCheckBox("credit.type.mes", false);
        fillCheckBox("credit.type.pequenaEmpresa", false);
        fillText("credit.maf", utilService.doubleMoneyFormat(amount, (String)null), 10);
        fillText("credit.downPayment", utilService.doubleMoneyFormat(downPayment * exchangeRate, (String)null), 10);
        fillText("credit.codigoExpediente", entityCreditCode, 10);
        fillText("credit.periodoGracia", null, 10);
        fillText("credit.installments", installments + " meses", 10);
        fillText("credit.servicioBancarizacion", null, 10);
        fillText("credit.installmentAmount", utilService.doubleMoneyFormat(installmentAmountAvg), 10);
        fillText("credit.insuranceAmount", utilService.doubleMoneyFormat(carInsuranceAmount), 10);
        fillText("credit.effectiveMonthlyRate", utilService.percentFormat(effectiveMonthlyRate), 10);

        fillText("person.firstSurname", person.getFirstSurname(), 10);
        fillText("person.lastSurname", person.getLastSurname(), 10);
        fillText("person.firstName", person.getFirstName(), 10);
        fillText("person.secondName", person.getName().replace(person.getFirstName(), ""), 10);
        fillCheckBox("person.docType.ce", person.getDocumentType().getId() == IdentityDocumentType.CE);
        fillCheckBox("person.docType.dni", person.getDocumentType().getId() == IdentityDocumentType.DNI);
        fillText("person.docNumber", person.getDocumentNumber(), 10);
        fillText("person.birthDate", utilService.dateFormat(person.getBirthday()), 10);
        fillText("person.birthPlace", "Lima", 10);
        fillCheckBox("person.gender.male", person.getGender() != null && person.getGender() == 'M');
        fillCheckBox("person.gender.female", person.getGender() != null && person.getGender() == 'F');
        fillText("person.nationality", person.getNationality().getName(), 10);
        fillText("person.residencia", "Perú", 10);
        if (person.getMaritalStatus() != null) {
            fillCheckBox("person.maritalStatus.single", person.getMaritalStatus().getId() == MaritalStatus.SINGLE);
            fillCheckBox("person.maritalStatus.married", person.getMaritalStatus().getId() == MaritalStatus.MARRIED);
            fillCheckBox("person.maritalStatus.cohabitant", person.getMaritalStatus().getId() == MaritalStatus.COHABITANT);
            fillCheckBox("person.maritalStatus.divorced", person.getMaritalStatus().getId() == MaritalStatus.DIVORCED);
            fillCheckBox("person.maritalStatus.widow", person.getMaritalStatus().getId() == MaritalStatus.WIDOWED);
        }
        if (disaggregatedHomeAddress != null) {
            Ubigeo ubigeo = catalogService.getUbigeo(disaggregatedHomeAddress.getUbigeo());
            fillText("person.address.address", contactInfo.getAddressStreetName(), 10);
            fillText("person.address.mz", null, 10);
            fillText("person.address.dpto", null, 10);
            fillText("person.address.urbanizacion", null, 10);
            fillText("person.address.district", ubigeo.getDistrict().getName(), 10);
            fillText("person.address.province", ubigeo.getProvince().getName(), 10);
            fillText("person.address.department", ubigeo.getDepartment().getName(), 10);
            fillText("person.address.reference", disaggregatedHomeAddress.getReferencia(), 10);
        }
        fillText("person.email", user.getEmail(), 10);
        fillText("person.phoneNumber", user.getPhoneNumber(), 10);
        if (principalOcupation != null) {
            switch (principalOcupation.getActivityType().getId()) {
                case ActivityType.INDEPENDENT:
                    fillCheckBox("person.activityType.independent", true);
                    break;
                case ActivityType.DEPENDENT:
                    fillCheckBox("person.activityType.dependent", true);
                    break;
                case ActivityType.PENSIONER:
                    fillCheckBox("person.activityType.pensioner", true);
                    break;
                default:
                    fillCheckBox("person.activityType.other", true);
                    break;
            }
            fillText("person.profession", person.getProfession() != null ? person.getProfession().getProfession() : null, 10);
            fillText("person.ocupationalInformation.ocupation", principalOcupation.getOcupation() != null ? principalOcupation.getOcupation().getOcupation() : null, 10);
            fillText("person.ocupationalInformation.address", principalOcupation.getAddress(), 10);
            fillText("person.ocupationalInformation.ruc", principalOcupation.getRuc(), 10);
            fillText("person.ocupationalInformation.ciiu", null, 10);
            fillText("person.ocupationalInformation.phoneNumber", principalOcupation.getPhoneNumber(), 10);
            fillText("person.ocupationalInformation.startDate", utilService.dateFormat(principalOcupation.getStartDate()), 10);
            fillText("person.ocupationalInformation.monthlyNetIncome", utilService.doubleMoneyFormat(principalOcupation.getFixedGrossIncome()), 10);
            fillText("person.ocupationalInformation.companyName", principalOcupation.getCompanyName(), 10);
        }
        fillText("person.phoneNumber", user.getPhoneNumber(), 10);

        if (person.getPartner() != null) {
            fillText("person.partner.firstSurname", person.getPartner().getFirstSurname(), 10);
            fillText("person.partner.lastSurname", person.getPartner().getLastSurname(), 10);
            fillText("person.partner.firstName", person.getPartner().getFirstName(), 10);
            fillText("person.partner.secondName", person.getPartner().getName().replace(person.getPartner().getFirstName(), ""), 10);
            fillCheckBox("person.partner.docType.ce", person.getPartner().getDocumentType().getId() == IdentityDocumentType.CE);
            fillCheckBox("person.partner.docType.dni", person.getPartner().getDocumentType().getId() == IdentityDocumentType.DNI);
            fillText("person.partner.docNumber", person.getPartner().getDocumentNumber(), 10);
            fillText("person.partner.birthDate", utilService.dateFormat(person.getPartner().getBirthday()), 10);
            fillText("person.partner.birthPlace", null, 10);
            fillCheckBox("person.partner.gender.male", person.getPartner().getGender() != null && person.getPartner().getGender() == 'M');
            fillCheckBox("person.partner.gender.female", person.getPartner().getGender() != null && person.getPartner().getGender() == 'F');
            fillText("person.partner.nationality", person.getPartner().getNationality().getName(), 10);
            fillText("person.partner.residencia", null, 10);
            if (person.getPartner().getMaritalStatus() != null) {
                fillCheckBox("person.partner.maritalStatus.single", person.getPartner().getMaritalStatus().getId() == MaritalStatus.SINGLE);
                fillCheckBox("person.partner.maritalStatus.married", person.getPartner().getMaritalStatus().getId() == MaritalStatus.MARRIED);
                fillCheckBox("person.partner.maritalStatus.cohabitant", person.getPartner().getMaritalStatus().getId() == MaritalStatus.COHABITANT);
                fillCheckBox("person.partner.maritalStatus.divorced", person.getPartner().getMaritalStatus().getId() == MaritalStatus.DIVORCED);
                fillCheckBox("person.partner.maritalStatus.widow", person.getPartner().getMaritalStatus().getId() == MaritalStatus.WIDOWED);
            }
        }
    }
}
