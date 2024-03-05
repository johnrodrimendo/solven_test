package com.affirm.strategy.contracts;

import com.affirm.common.dao.CreditDAO;
import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.model.catalog.*;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.UtilService;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AbacoContractStrategy extends BaseContract {

    public AbacoContractStrategy(byte[] contract, CatalogService catalogService, PersonDAO personDAO, UtilService utilService, CreditDAO creditDAO, LoanApplicationDAO loanApplicationDAO) throws Exception {
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

        MutableInt otherMonthlyIncomes = new MutableInt();

        Double effectiveAnnualCostRate;
        Double effectiveAnnualRate;
        Integer employer;
        Integer entityId;
        Integer installments;
        Double amount;
        LoanApplicationReason loanReason = loanApplication.getReason();
        if (credit != null) {
            effectiveAnnualCostRate = credit.getEffectiveAnnualCostRate();
            effectiveAnnualRate = credit.getEffectiveAnnualRate();
            employer = credit.getEmployer() != null ? credit.getEmployer().getId() : null;
            entityId = credit.getEntity().getId();
            installments = credit.getInstallments();
            amount = credit.getAmount();
        } else {
            effectiveAnnualCostRate = loanOffer.getEffectiveAnnualCostRate();
            effectiveAnnualRate = loanOffer.getEffectiveAnualRate();
            employer = loanOffer.getEmployer() != null ? loanOffer.getEmployer().getId() : null;
            entityId = loanOffer.getEntity().getId();
            installments = loanOffer.getInstallments();
            amount = loanOffer.getAmmount();
        }

        if (ocupations != null)
            ocupations.forEach(o -> otherMonthlyIncomes.add(o.getFixedGrossIncome() != null ? o.getFixedGrossIncome() : 0));

        fillText("abaco.contract.annualrate", String.format("tasa ascenderá a %s efectivo %s", utilService.percentFormat(effectiveAnnualRate), "anual".toUpperCase()));

        // TODO Select the employee that the person selected in the beginig
        List<Employee> personEmployees = personDAO.getEmployeesByDocument(person.getDocumentType().getId(), person.getDocumentNumber(), locale);
        PersonBankAccountInformation personBankInformation = personDAO.getPersonBankAccountInformation(locale, person.getId());
        Employee personEmployee = null;
        if (personEmployees != null && employer != null)
            personEmployee = personEmployees.stream().filter(e -> e.getEmployer().getId().intValue() == employer)
                    .findFirst().orElse(null);

        PersonAssociated associated = personDAO.getAssociated(person.getId(), entityId);

        fillText("credit.amount", utilService.doubleMoneyFormat(amount, (String) null));
        fillText("credit.installments", installments + " meses");
        fillText("credit.reason", loanReason.getReason().toUpperCase());
        fillCheckBox("credit.disbursmentAccount", true);
        fillText("credit.bank", personBankInformation != null && personBankInformation.getBank() != null ? personBankInformation.getBank().getName() : null);
        fillText("credit.bankAccount", personBankInformation != null ? personBankInformation.getBankAccount() : null);
        fillText("person.fullname", person.getFullNameSurnameFirst());
        fillText("person.birthPlace", null);
        fillText("person.birthDay.day", utilService.dateCustomFormat(person.getBirthday(), "dd", locale));
        fillText("person.birthDay.month", utilService.dateCustomFormat(person.getBirthday(), "MM", locale));
        fillText("person.birthDay.year", utilService.dateCustomFormat(person.getBirthday(), "yyyy", locale));
        fillCheckBox("person.gender.male", person.getGender() != null && person.getGender().equals('M'));
        fillCheckBox("person.gender.female", person.getGender() != null && person.getGender().equals('F'));
        fillText("person.docNumber", person.getDocumentNumber());
        fillCheckBox("person.maritalStatus.single", person.getMaritalStatus().getId() == MaritalStatus.SINGLE);
        fillCheckBox("person.maritalStatus.married", person.getMaritalStatus().getId() == MaritalStatus.MARRIED);
        fillCheckBox("person.maritalStatus.widower", person.getMaritalStatus().getId() == MaritalStatus.WIDOWED);
        fillCheckBox("person.maritalStatus.divorced", person.getMaritalStatus().getId() == MaritalStatus.DIVORCED);
        fillCheckBox("person.maritalStatus.cohabitant", person.getMaritalStatus().getId() == MaritalStatus.COHABITANT);
        fillCheckBox("person.goodsSeparation.no", true);
        fillText("person.landLine", person.getLandline());
        fillText("person.cellphone", user.getPhoneNumber());
        fillText("person.nationality", person.getNationality() != null ? person.getNationality().getName() : null);
        fillText("person.home.address", contactInfo != null ? contactInfo.getFullAddress() : null);
        fillText("person.home.district", contactInfo != null && contactInfo.getAddressUbigeo() != null ? contactInfo.getAddressUbigeo().getDistrict().getName() : null);
        fillText("person.home.reference", null);
        fillCheckBox("person.home.type.own", contactInfo.getHousingType().getId() == contactInfo.getHousingType().OWN || contactInfo.getHousingType().getId() == contactInfo.getHousingType().OWN_FINANCED);
        fillCheckBox("person.home.type.rented", contactInfo.getHousingType().getId() == contactInfo.getHousingType().RENTED);
        fillCheckBox("person.home.type.family", contactInfo.getHousingType().getId() == contactInfo.getHousingType().FAMILY);

        fillCheckBox("person.home.type.rentSale", false);
        if (person.getStudyLevel() != null && person.getStudyLevel().getId() == StudyLevel.CONCLUDED_COLLEGE)
            fillCheckBox("person.profession.finish", true);
        else if (person.getStudyLevel() != null && person.getStudyLevel().getId() == StudyLevel.COLLEGE)
            fillCheckBox("person.profession.incomplete", true);

        fillText("person.instructionDegree", person.getStudyLevel() != null ? person.getStudyLevel().getLevel() : null);
        fillText("employee.startDate", personEmployee != null ? utilService.dateCustomFormat(personEmployee.getEmploymentStartDateDate(), "dd/MM/yyyy", locale) : null);
        fillText("employee.employer.name", personEmployee != null ? personEmployee.getEmployer().getName() : null);
        fillText("employee.employer.commercialAddress", personEmployee != null ? personEmployee.getEmployer().getAddress() : null);
        fillText("employee.employer.ruc", personEmployee != null ? personEmployee.getEmployer().getRuc() : null);
        fillText("employee.position", principalOcupation != null && principalOcupation.getOcupation() != null ? principalOcupation.getOcupation().getOcupation() : null);
        fillText("employee.employer.activityType", null);
        fillText("employee.employer.phoneNumber", personEmployee != null ? personEmployee.getEmployer().getPhoneNumber() : null);
        fillText("employee.employer.annex", null);
        fillText("employee.netIncome", utilService.doubleMoneyFormat(personEmployee != null ? personEmployee.getFixedGrossIncome() : null, (String) null)); //TODO Convert to dollar
        fillText("person.email", user.getEmail());
        fillText("partner.fullName", partner != null ? partner.getFullNameSurnameFirst() : null);
        fillText("partner.birthPlace", null);
        fillText("partner.birthDay.day", partner != null ?
                utilService.dateCustomFormat(partner.getBirthday(), "dd", locale) : null);
        fillText("partner.birthDay.month", partner != null ?
                utilService.dateCustomFormat(partner.getBirthday(), "MM", locale) : null);
        fillText("partner.birthDay.year", partner != null ?
                utilService.dateCustomFormat(partner.getBirthday(), "yyyy", locale) : null);
        fillText("partner.nationality", partner != null && partner.getNationality() != null ? partner.getNationality().getName() : null);
        fillText("partner.docNumber", partner != null ? partner.getDocumentNumber() : null);
        fillText("partner.instructionDegree", null);
        fillText("partner.profession", null);
        fillText("partner.employee.employer.name", null);
        fillText("partner.employee.employer.commercialAddress", null);
        fillText("partner.employee.employer.district", null);
        fillText("partner.employee.employee.position", null);
        fillText("partner.employee.employer.ruc", null);
        fillText("partner.employee.employer.activityType", null);
        fillText("partner.employee.employer.phoneNumber", null);
        fillText("partner.employee.employer.annex", null);
        fillText("partner.employee.netIncome", null);
        fillText("partner.email", null);
        fillCheckBox("declaration.1.no", true);
        fillCheckBox("declaration.2.no", true);
        fillCheckBox("declaration.3.1.no", true);
        fillCheckBox("declaration.3.2.no", true);
        fillCheckBox("declaration.4.1.no", true);
        fillCheckBox("declaration.4.2.no", true);

        fillText("person.partnerCode", associated != null ? associated.getAssociatedId() : null);

        ArrayList elements = new ArrayList();
        elements.add(UserFileType.DNI_FRONTAL);
        elements.add(UserFileType.DNI_ANVERSO);
        fillCheckBox("documents.dni", principalOcupation != null && principalOcupation.getRequiredDocuments().containsAll(elements));
        fillCheckBox("documents.homeServiceReceipts", principalOcupation != null && principalOcupation.getRequiredDocuments().contains(UserFileType.COMPROBANTE_DIRECCION));
        fillCheckBox("documents.paymentReceipt", principalOcupation != null && principalOcupation.getRequiredDocuments().contains(UserFileType.BOLETA_PAGO));
        fillText("signature.date.day", utilService.dateCustomFormat(new Date(), "dd", locale));
        fillText("signature.date.month", utilService.dateCustomFormat(new Date(), "MMMM", locale));
        fillText("signature.date.year", utilService.dateCustomFormat(new Date(), "yyyy", locale));
        fillText("signature.signature", signature != null ? signature : " ", handWritingFont, 16);
        fillText("signature.fullName", person.getFullName());
        fillText("signature.identityDocument", person.getDocumentType().getName() + " " + person.getDocumentNumber());
    }
}
