package com.affirm.strategy.contracts;

import com.affirm.common.dao.CreditDAO;
import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.model.catalog.EntityProductParams;
import com.affirm.common.model.catalog.IdentityDocumentType;
import com.affirm.common.model.catalog.MaritalStatus;
import com.affirm.common.model.catalog.StudyLevel;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.UtilService;
import com.affirm.common.util.n2t;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.text.WordUtils;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BancoFinancieroContractStrategy extends BaseContract {

    public BancoFinancieroContractStrategy(byte[] contract, CatalogService catalogService, PersonDAO personDAO, UtilService utilService, CreditDAO creditDAO, LoanApplicationDAO loanApplicationDAO) throws Exception {
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
        fillText("city", "Lima", 8);
        fillText("date.day", utilService.dateCustomFormat(new Date(), "dd", locale), 8);
        fillText("date.month", utilService.dateCustomFormat(new Date(), "MMMM", locale), 8);
        String year = utilService.dateCustomFormat(new Date(), "yyyy", locale);
        fillText("date.year.lastDigit", year.substring(year.length() - 1, year.length()), 8);

        fillText("person.fullname", person.getFullName(), 8);
        if (person.getDocumentType().getId() == IdentityDocumentType.DNI)
            fillText("Text1", "X", 8);
        else if (person.getDocumentType().getId() == IdentityDocumentType.CE)
            fillText("Text2", "X", 8);
        fillText("person.docNumber", person.getDocumentNumber(), 11);
        fillText("person.address", contactInfo.getFullAddress(), 11);
        fillText("Text6", "X", 8);
        String amount = utilService.doubleFormat(credit.getLoanCapital());
        String amountFormatted = StringUtils.leftPad(amount, 9, " ");
        fillText("credit.amount8", amountFormatted.charAt(0), 8);
        fillText("credit.amount7", amountFormatted.charAt(1), 8);
        fillText("credit.amount6", amountFormatted.charAt(2), 8);
        fillText("credit.amount5", amountFormatted.charAt(3), 8);
        fillText("credit.amount4", amountFormatted.charAt(4), 8);
        fillText("credit.amount3", amountFormatted.charAt(5), 8);
        fillText("credit.amount2", amountFormatted.charAt(6), 8);
        fillText("credit.amount1", amountFormatted.charAt(7), 8);
        fillText("credit.amount0", amountFormatted.charAt(8), 8);

        String firstName = person.getFirstName();
        String firstSurname = person.getFirstSurname();
        String lastSurname = person.getLastSurname();

        for (int i = 0; i < Math.min(firstName.length(), 15); ++i) {
            fillText("person.name." + String.valueOf(i + 1), firstName.charAt(i), 8);
        }

        for (int i = 0; i < Math.min(firstSurname.length(), 16); ++i) {
            fillText("person.firstSurname." + String.valueOf(i + 1), firstSurname.charAt(i), 8);
        }

        for (int i = 0; i < Math.min(lastSurname.length(), 16); ++i) {
            fillText("person.lastSurname." + String.valueOf(i + 1), lastSurname.charAt(i), 8);
        }

        String docNumber = person.getDocumentNumber();

        for (int i = 0; i < Math.min(docNumber.length(), 12); ++i) {
            fillText("person.docNumber" + String.valueOf(i + 1), docNumber.charAt(i), 8);
        }

        LocalDateTime date = LocalDateTime.now();

        String yr = utilService.dateCustomFormat(new Date(), "yy", locale);
        String month = utilService.dateCustomFormat(new Date(), "MM", locale);
        String day = utilService.dateCustomFormat(new Date(), "dd", locale);

        fillText("date.day1", day.charAt(0), 8);
        fillText("date.day2", day.charAt(1), 8);

        fillText("date.month1", month.charAt(0), 8);
        fillText("date.month2", month.charAt(1), 8);

        fillText("date.year1", yr.charAt(0), 8);
        fillText("date.year2", yr.charAt(1), 8);

        fillCheckBox("person.docType.dni", person.getDocumentType().getId() == IdentityDocumentType.DNI);
        fillCheckBox("person.docType.ce", person.getDocumentType().getId() == IdentityDocumentType.CE);

        n2t converter = new n2t();
        String entero = converter.convertirLetras(Integer.valueOf(amountFormatted.split("\\.")[0].trim()));
        String decimal = "";

        StringBuffer resultado = new StringBuffer();
        resultado.append(entero);
        if (Integer.valueOf(amountFormatted.split("\\.")[1].trim()) != 0) {
            decimal = converter.convertirLetras(Integer.valueOf(amountFormatted.split("\\.")[1].trim()));
            resultado.append(" Soles con ");
            resultado.append(decimal);
            resultado.append(" Céntimos ");
            fillText("credit.amount.letters", WordUtils.capitalize(resultado.toString()), 8);
        } else {
            fillText("credit.amount.letters", WordUtils.capitalize(resultado.toString().concat(" Soles")), 8);
        }


        List<OriginalSchedule> scheduleArr = creditDAO.getOriginalSchedule(credit.getId());
        double interestsD = 0;
        for (OriginalSchedule original : scheduleArr) {
            interestsD += (original.getInterest() + original.getInterestTax());
        }

        String interests = utilService.doubleFormat(interestsD);
        String interestsFormatted = StringUtils.leftPad(interests, 9, " ");

        fillText("credit.interest8", interestsFormatted.charAt(0), 8);
        fillText("credit.interest7", interestsFormatted.charAt(1), 8);
        fillText("credit.interest6", interestsFormatted.charAt(2), 8);
        fillText("credit.interest5", interestsFormatted.charAt(3), 8);
        fillText("credit.interest4", interestsFormatted.charAt(4), 8);
        fillText("credit.interest3", interestsFormatted.charAt(5), 8);
        fillText("credit.interest2", interestsFormatted.charAt(6), 8);
        fillText("credit.interest1", interestsFormatted.charAt(7), 8);
        fillText("credit.interest0", interestsFormatted.charAt(8), 8);
        fillText("credit.tea", utilService.doubleFormat(credit.getEffectiveAnnualRate()), 8);

        converter = new n2t();
        entero = converter.convertirLetras(Integer.valueOf(interestsFormatted.split("\\.")[0].trim()));
        decimal = "";

        resultado = new StringBuffer();
        resultado.append(entero);
        if (Integer.valueOf(interestsFormatted.split("\\.")[1].trim()) != 0) {
            decimal = converter.convertirLetras(Integer.valueOf(interestsFormatted.split("\\.")[1].trim()));
            resultado.append(" Soles con ");
            resultado.append(decimal);
            resultado.append(" Céntimos ");
            fillText("credit.interest.letters", WordUtils.capitalize(resultado.toString()), 8);
        } else {
            fillText("credit.interest.letters", WordUtils.capitalize(resultado.toString().concat(" Soles")), 8);
        }

        fillText("credit.installsment", credit.getInstallments().toString(), 8);

        if (partner != null) {
            if (partner.getDocumentType().getId() == IdentityDocumentType.DNI)
                fillText("Text3", "X", 8);
            else if (partner.getDocumentType().getId() == IdentityDocumentType.CE)
                fillText("Text4", "X", 8);
            fillText("partner.fullname", WordUtils.capitalizeFully(partner.getFullName()), 8);
            fillText("partner.docNumber", partner.getDocumentNumber(), 8);

            fillCheckBox("person.partner.gender.female", partner.getGender() != null && partner.getGender() == 'F');
            fillCheckBox("person.partner.gender.male", partner.getGender() != null && partner.getGender() == 'M');
            fillText("person.partner.firstSurnameCopy", partner.getFirstSurname(), 8);
            fillText("person.partner.lastSurnameCopy", partner.getLastSurname(), 8);
            fillText("person.partner.names", partner.getName(), 8);
            fillText("person.partner.birthday", utilService.dateCustomFormat(partner.getBirthday(), "dd-MM-yyyy", locale), 8);
            fillText("person.partner.birthday", utilService.dateCustomFormat(partner.getBirthday(), "dd-MM-yyyy", locale), 8);
            if (partner.getStudyLevel() != null) {
                fillCheckBox("person.partner.estudiesLevel.primaria", false);
                switch (partner.getStudyLevel().getId()) {
                    case StudyLevel.MIDDLE_SCHOOL:
                        fillCheckBox("person.partner.estudiesLevel.secundaria", true);
                        break;
                    case StudyLevel.TECHNICIAN:
                        fillCheckBox("person.partner.estudiesLevel.tecnico", true);
                        break;
                    case StudyLevel.COLLEGE:
                        fillCheckBox("person.partner.estudiesLevel.universitario", true);
                        break;
                    default:
                        fillCheckBox("person.partner.estudiesLevel.niguno", true);
                        break;
                }
            }
            fillText("person.partner.docTypeCopy", partner.getDocumentType().getName(), 8);
            fillText("person.partner.docNumber", partner.getDocumentNumber(), 8);
            fillText("person.partner.nationality", partner.getNationality().getName(), 8);
            fillText("person.partner.nationality", partner.getNationality() != null ? partner.getNationality().getName():null, 8);
            if (partner.getMaritalStatus() != null) {
                switch (partner.getMaritalStatus().getId()) {
                    case MaritalStatus.SINGLE:
                        fillCheckBox("person.partner.maritalStatus.soltero", true);
                        break;
                    case MaritalStatus.MARRIED:
                    case MaritalStatus.COHABITANT:
                        fillCheckBox("person.partner.maritalStatus.casado", true);
                        break;
                }
            }
        }

        fillText("person.ocupationalInformation.salaryAmount", utilService.integerOnlyMoney(principalOcupation.getFixedGrossIncome()), 8);
        fillCheckBox("clientType.client", false);
        fillCheckBox("clientType.preApproved", true);
        fillText("credit.currency", "Soles", 8);
        fillText("credit.amountWithoutCurrency", utilService.integerOnlyMoney(credit.getAmount()), 8);
        fillText("credit.downPayment", utilService.integerOnlyMoney(credit.getDownPayment()), 8);
        fillText("credit.installments", credit.getInstallments(), 8);
        fillText("credit.registerDate", utilService.dateCustomFormat(credit.getRegisterDate(), "dd-MM-yyyy", locale), 8);
        fillCheckBox("person.gender.female", person.getGender() != null && person.getGender() == 'F');
        fillCheckBox("person.gender.male", person.getGender() != null && person.getGender() == 'M');
        fillText("person.firstSurnameCopy", person.getFirstSurname(), 8);
        fillText("person.lastSurnameCopy", person.getLastSurname(), 8);
        fillText("person.names", person.getName(), 8);
        fillText("person.birthday", person.getBirthday() != null ? utilService.dateCustomFormat(person.getBirthday(), "dd-MM-yyyy", locale) : null, 8);
        if (person.getStudyLevel() != null) {
            fillCheckBox("person.estudiesLevel.primaria", false);
            switch (person.getStudyLevel().getId()) {
                case StudyLevel.MIDDLE_SCHOOL:
                    fillCheckBox("person.estudiesLevel.secundaria", true);
                    break;
                case StudyLevel.TECHNICIAN:
                    fillCheckBox("person.estudiesLevel.tecnico", true);
                    break;
                case StudyLevel.COLLEGE:
                    fillCheckBox("person.estudiesLevel.universitario", true);
                    break;
                default:
                    fillCheckBox("person.estudiesLevel.niguno", true);
                    break;
            }
        }
        fillText("person.docTypeCopy", person.getDocumentType().getName(), 8);
        fillText("person.docNumber", person.getDocumentNumber(), 8);
        fillText("person.nationality", person.getNationality() != null ? person.getNationality().getName() : null, 8);
        if (person.getMaritalStatus() != null) {
            switch (person.getMaritalStatus().getId()) {
                case MaritalStatus.SINGLE:
                    fillCheckBox("person.maritalStatus.soltero", true);
                    break;
                case MaritalStatus.MARRIED:
                case MaritalStatus.COHABITANT:
                    fillCheckBox("person.maritalStatus.casado", true);
                    break;
            }
        }
        fillText("person.address", contactInfo.getAddressStreetName(), 8);
        fillText("person.addressDistrict", contactInfo.getAddressUbigeo() != null ? contactInfo.getAddressUbigeo().getDistrict().getName() : null, 8);
        fillText("person.addressCity", contactInfo.getAddressUbigeo() != null ? contactInfo.getAddressUbigeo().getDepartment().getName() : null, 8);
        fillText("person.phoneNumber", user.getPhoneNumber(), 8);
        fillText("person.email", user.getEmail(), 8);
        fillText("person.dependents", person.getDependents(), 8);
        fillText("person.ocupationalInformation.companyName", principalOcupation.getCompanyName(), 8);
        fillText("person.ocupationalInformation.ruc", principalOcupation.getRuc(), 8);
        fillText("person.ocupationalInformation.ocupation", principalOcupation.getOcupation() != null ? principalOcupation.getOcupation().getOcupation() : null, 8);
        fillText("person.ocupationalInformation.startDate", utilService.dateCustomFormat(principalOcupation.getStartDate(), "dd-MM-yyyy", locale), 8);
        fillText("person.ocupationalInformation.address", principalOcupation.getAddress(), 8);
        fillText("person.ocupationalInformation.phoneNumber", principalOcupation.getPhoneNumber(), 8);
        fillText("person.ocupationalInformation.salaryAmount", utilService.integerOnlyMoney(principalOcupation.getFixedGrossIncome()), 8);

        MutableInt otherMonthlyIncomes = new MutableInt();
        if (ocupations != null)
            ocupations.forEach(o -> otherMonthlyIncomes.add(o.getFixedGrossIncome() != null ? o.getFixedGrossIncome() : 0));
        fillText("person.ocupationalInformation.otherIncome", utilService.integerOnlyMoney(otherMonthlyIncomes.intValue()), 8);

        fillText("person.signature", signature != null ? signature : " ", handWritingFont, 16);
        fillText("credit.signature", signature != null ? signature : " ", handWritingFont, 16);
    }
}
