package com.affirm.strategy.contracts;

import com.affirm.common.dao.CreditDAO;
import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.model.catalog.*;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.UtilService;

import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EfectivaContractStrategy extends BaseContract {
    public EfectivaContractStrategy(byte[] contract, CatalogService catalogService, PersonDAO personDAO, UtilService utilService, CreditDAO creditDAO, LoanApplicationDAO loanApplicationDAO) throws Exception {
        super(contract, catalogService, personDAO, utilService, creditDAO, loanApplicationDAO, null);
    }

    @Override
    public void fillPdf(Person person, Credit credit, LoanOffer loanOffer, EntityProductParams params, User user, String signature, Person partner, PersonContactInformation contactInfo, List<PersonOcupationalInformation> ocupations, PersonOcupationalInformation principalOcupation, PersonBankAccountInformation personBankAccountInformation, LoanApplication loanApplication, Locale locale, SunatResult sunatResult) throws Exception {

        Employer employer;
        Double amount;
        Double scheduleTotalInterest;
        Double effectiveAnnualRate;
        Double effectiveAnnualCostRate;
        if (credit != null) {
            employer = credit.getEmployer();
            amount = credit.getAmount();
            scheduleTotalInterest = credit.getTotalScheduleField("totalInterest", 'O');
            effectiveAnnualRate = credit.getEffectiveAnnualRate();
            effectiveAnnualCostRate = credit.getEffectiveAnnualCostRate();
        } else {
            employer = loanOffer.getEmployer();
            amount = loanOffer.getAmmount();
            scheduleTotalInterest = loanOffer.getTotalScheduleField("totalInterest");
            effectiveAnnualRate = loanOffer.getEffectiveAnualRate();
            effectiveAnnualCostRate = loanOffer.getEffectiveAnnualCostRate();
        }

        Date date = new Date();
        String dateDay = utilService.dateCustomFormat(date, "dd", locale);
        String dateMonth = utilService.dateCustomFormat(date, "MM", locale);
        String dateYear = utilService.dateCustomFormat(date, "yyyy", locale);

        if (dateDay.length() < 2)
            dateDay = "0" + dateDay;

        if (dateMonth.length() < 2)
            dateMonth = "0" + dateMonth;

        if (dateYear.length() < 2)
            dateYear = "0" + dateYear;

        fillText("date.day0", dateDay.charAt(0));
        fillText("date.day1", dateDay.charAt(1));
        fillText("date.day", dateDay);
        fillText("date.month0", dateMonth.charAt(0));
        fillText("date.month1", dateMonth.charAt(1));
        fillText("date.month", utilService.dateCustomFormat(date, "MMMM", locale));
        fillText("date.year0", dateYear.charAt(0));
        fillText("date.year1", dateYear.charAt(1));
        fillText("date.year2", dateYear.charAt(2));
        fillText("date.year3", dateYear.charAt(3));
        fillText("date.year", dateYear);
        fillText("date.year_last_two", dateYear.substring(2, 4));

        String birthDay = utilService.dateCustomFormat(person.getBirthday(), "dd/MM/yyyy", locale);

        fillText("person.birthDay.day0", birthDay.charAt(0));
        fillText("person.birthDay.day1", birthDay.charAt(1));
        fillText("person.birthDay.month0", birthDay.charAt(3));
        fillText("person.birthDay.month1", birthDay.charAt(4));
        fillText("person.birthDay.year0", birthDay.charAt(6));
        fillText("person.birthDay.year1", birthDay.charAt(7));
        fillText("person.birthDay.year2", birthDay.charAt(8));
        fillText("person.birthDay.year3", birthDay.charAt(9));

        String dependents = String.valueOf(person.getDependents());

        while (dependents.length() < 2)
            dependents = "0" + dependents;

        fillText("person.dependents0", dependents.charAt(0));
        fillText("person.dependents1", dependents.charAt(1));

        fillText("person.fullName", person.getFullName());
        fillText("person.docNumber", person.getDocumentNumber());
        fillText("person.address", contactInfo.getFullAddress());
        fillText("person.cellphone", contactInfo.getPhoneNumber());

        String firstName = person.getFirstName();
        String firstSurname = person.getFirstSurname();
        String lastSurname = person.getLastSurname();
        String docNumber = person.getDocumentNumber();
        String address = contactInfo.getFullAddress();
        String district = contactInfo.getAddressUbigeo().getDistrict().getName();
        String email = contactInfo.getEmail();
        String cellphone = contactInfo.getPhoneNumber();

        for (int i = 0; i < Math.min(firstName.length(), 20); ++i) {
            fillText("person.name" + String.valueOf(i), firstName.charAt(i), 8);
        }

        for (int i = 0; i < Math.min(firstSurname.length(), 16); ++i) {
            fillText("person.firstSurname" + String.valueOf(i), firstSurname.charAt(i), 8);
        }

        for (int i = 0; i < Math.min(lastSurname.length(), 16); ++i) {
            fillText("person.lastSurname" + String.valueOf(i), lastSurname.charAt(i), 8);
        }

        for (int i = 0; i < Math.min(docNumber.length(), 8); ++i) {
            fillText("person.docNumber" + String.valueOf(i), docNumber.charAt(i), 8);
        }

        for (int i = 0; i < Math.min(address.length(), 26); ++i) {
            fillText("person.address" + String.valueOf(i), address.charAt(i), 8);
        }

        for (int i = 0; i < Math.min(district.length(), 19); ++i) {
            fillText("person.district" + String.valueOf(i), district.charAt(i), 8);
        }

        for (int i = 0; i < Math.min(email.length(), 19); ++i) {
            fillText("person.email" + String.valueOf(i), email.charAt(i), 8);
        }

        for (int i = 0; i < Math.min(cellphone.length(), 19); ++i) {
            fillText("person.cellphone" + String.valueOf(i), cellphone.charAt(i), 8);
        }

        fillCheckBox("maritalStatus.single", person.getMaritalStatus().getId() == MaritalStatus.SINGLE);
        fillCheckBox("person.maritalStatus.married", person.getMaritalStatus().getId() == MaritalStatus.MARRIED);
        fillCheckBox("person.maritalStatus.widower", person.getMaritalStatus().getId() == MaritalStatus.WIDOWED);
        fillCheckBox("person.maritalStatus.divorced", person.getMaritalStatus().getId() == MaritalStatus.DIVORCED);
        fillCheckBox("person.maritalStatus.cohabitant", person.getMaritalStatus().getId() == MaritalStatus.COHABITANT);

        fillText("person.signature", signature != null ? signature : " ", handWritingFont, 16);

        if (partner != null) {
            String partnerName = partner.getName();
            String partnerFirstSurname = partner.getFirstSurname();
            String partnerLastSurname = partner.getLastSurname();
            String partnerDocNumber = partner.getDocumentNumber();

            for (int i = 0; i < Math.min(partnerName.length(), 20); ++i) {
                fillText("partner.name" + String.valueOf(i), partnerName.charAt(i), 8);
            }

            for (int i = 0; i < Math.min(partnerFirstSurname.length(), 16); ++i) {
                fillText("partner.firstSurname" + String.valueOf(i), partnerFirstSurname.charAt(i), 8);
            }

            for (int i = 0; i < Math.min(partnerLastSurname.length(), 16); ++i) {
                fillText("partner.lastSurname" + String.valueOf(i), partnerLastSurname.charAt(i), 8);
            }

            for (int i = 0; i < Math.min(partnerDocNumber.length(), 8); ++i) {
                fillText("partner.docNumber" + String.valueOf(i), partnerDocNumber.charAt(i), 8);
            }

            String partnerBirthDay = utilService.dateCustomFormat(partner.getBirthday(), "dd/MM/yyyy", locale);

            fillText("partner.birthDay.day0", partnerBirthDay.charAt(0));
            fillText("partner.birthDay.day1", partnerBirthDay.charAt(1));
            fillText("partner.birthDay.month0", partnerBirthDay.charAt(3));
            fillText("partner.birthDay.month1", partnerBirthDay.charAt(4));
            fillText("partner.birthDay.year0", partnerBirthDay.charAt(6));
            fillText("partner.birthDay.year1", partnerBirthDay.charAt(7));
            fillText("partner.birthDay.year2", partnerBirthDay.charAt(8));
            fillText("partner.birthDay.year3", partnerBirthDay.charAt(9));
        }

        if (employer != null) {
            String employerName = employer.getName();
            String employerCommercialAddress = employer.getAddress();
            String employerPhoneNumber = employer.getPhoneNumber();

            fillText("partner.fullName", partner.getFullName());
            fillText("partner.docNumber", partner.getDocumentNumber());

            for (int i = 0; i < Math.min(employerName.length(), 17); ++i) {
                fillText("employer.name" + String.valueOf(i), employerName.charAt(i), 8);
            }

            for (int i = 0; i < Math.min(employerCommercialAddress.length(), 26); ++i) {
                fillText("employer.commercialAddress" + String.valueOf(i), employerCommercialAddress.charAt(i), 8);
            }

            for (int i = 0; i < Math.min(employerPhoneNumber.length(), 8); ++i) {
                fillText("employer.phoneNumber" + String.valueOf(i), employerPhoneNumber.charAt(i), 8);
            }
        }

//        Employee employee = personDAO.getEmployeeById(person.getId(), locale);

        if (principalOcupation != null) {
            String netIncome = String.valueOf(principalOcupation.getFixedGrossIncome());

            for (int i = 0; i < Math.min(netIncome.length(), 5); ++i) {
                fillText("employee.netIncome" + String.valueOf(i), netIncome.charAt(i), 8);
            }

            fillCheckBox("person.dependent", principalOcupation.getActivityType().getId() == ActivityType.DEPENDENT);
            fillCheckBox("person.independent", principalOcupation.getActivityType().getId() == ActivityType.INDEPENDENT);
        }

        List<Referral> referrals = personDAO.getReferrals(person.getId(), locale);

        if (referrals != null) {
            int index = 0;
            for (Referral referral : referrals) {
                if (index >= 2)
                    break;

                Relationship relationship = referral.getRelationship();

                String type = relationship.getRelationship();
                String referalPhone = referral.getPhoneNumber();
                String referalName = referral.getFullName();

                for (int i = 0; i < Math.min(type.length(), 7); ++i) {
                    fillText("referal" + String.valueOf(index) + ".type" + String.valueOf(i), type.charAt(i), 8);
                }

                for (int i = 0; i < Math.min(referalPhone.length(), 7); ++i) {
                    fillText("referal" + String.valueOf(index) + ".phone" + String.valueOf(i), referalPhone.charAt(i), 8);
                }

                for (int i = 0; i < Math.min(referalName.length(), 21); ++i) {
                    fillText("referal" + String.valueOf(index) + ".name" + String.valueOf(i), referalName.charAt(i), 8);
                }
                index++;
            }
        }

        String total_amount = utilService.doubleMoneyFormat(scheduleTotalInterest, (String) null);
        String currency = "Soles";
        String tea = utilService.percentFormat(effectiveAnnualRate);
        String tcea = utilService.percentFormat(effectiveAnnualCostRate);

        fillCheckBox("person.home.type.own", contactInfo.getHousingType().getId() == HousingType.OWN);
        fillCheckBox("person.home.type.family", contactInfo.getHousingType().getId() == HousingType.FAMILY);
        fillCheckBox("person.home.type.rented", contactInfo.getHousingType().getId() == HousingType.RENTED);

        fillCheckBox("person.instructionDegree.college", person.getStudyLevel().getId() == StudyLevel.COLLEGE);
        fillCheckBox("person.instructionDegree.postgraduate", person.getStudyLevel().getId() == StudyLevel.DOCTORATE || person.getStudyLevel().getId() == StudyLevel.PHD);
        fillCheckBox("person.instructionDegree.technician", person.getStudyLevel().getId() == StudyLevel.TECHNICIAN);
        fillCheckBox("person.instructionDegree.middleSchool", person.getStudyLevel().getId() == StudyLevel.MIDDLE_SCHOOL);

        fillText("currency", currency, 8);
        fillText("tea", tea, 8);
        fillText("tcea", tcea, 8);
        fillText("total_amount", total_amount, 8);
        fillText("amount", utilService.doubleMoneyFormat(amount, (String) null), 8);
    }
}
