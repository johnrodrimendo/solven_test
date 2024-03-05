package com.affirm.strategy.contracts;

import com.affirm.common.dao.CreditDAO;
import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.model.catalog.EntityProductParams;
import com.affirm.common.model.catalog.MaritalStatus;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.UtilService;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class FinansolFichaInscripcionContractStrategy extends BaseContract {

    private static final String BLANK = "";

    public FinansolFichaInscripcionContractStrategy(byte[] contract, CatalogService catalogService, PersonDAO personDAO, UtilService utilService, CreditDAO creditDAO, LoanApplicationDAO loanApplicationDAO) throws Exception {
        super(contract, catalogService, personDAO, utilService, creditDAO, loanApplicationDAO, null);
        this.defaultFont = prepareFont(pdfDoc, "/Arial.ttf");
    }

    @Override
    public void fillPdf(Person person, Credit credit, LoanOffer loanOffer, EntityProductParams params, User user, String signature, Person partner, PersonContactInformation contactInfo, List<PersonOcupationalInformation> ocupations, PersonOcupationalInformation principalOcupation, PersonBankAccountInformation personBankAccountInformation, LoanApplication loanApplication, Locale locale, SunatResult sunatResult) throws Exception {

        Calendar calendar = Calendar.getInstance(locale);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", locale);

        String maritalStatus = person.getMaritalStatus().getStatus().toLowerCase();

        if (person.getGender() != null && person.getGender() == 'F' && person.getMaritalStatus().getId() != MaritalStatus.COHABITANT) {
            maritalStatus = maritalStatus.substring(0, maritalStatus.length() - 1) + "a";
        }

        fillText("admission_date", sdf.format(calendar.getTime()));
        fillText("first_surname", person.getFirstSurname().toUpperCase());
        fillText("last_surname", person.getLastSurname() != null ? person.getLastSurname().toUpperCase() : BLANK);
        fillText("person_name", person.getName().toUpperCase());
        fillText("birthdate", sdf.format(person.getBirthday()));
        fillText("country", person.getCountry() != null ? person.getCountry().getName() : BLANK);
        fillText("department", person.getBirthUbigeo() != null ? person.getBirthUbigeo().getDepartment().getName() : BLANK);
        fillText("province", person.getBirthUbigeo() != null ? person.getBirthUbigeo().getProvince().getName() : BLANK);
        fillText("district", person.getBirthUbigeo() != null ? person.getBirthUbigeo().getDistrict().getName() : BLANK);
        fillText("gen", person.getGender() != null ? String.valueOf(person.getGender()) : BLANK);
        fillText("age", person.getBirthday() != null ? String.valueOf(utilService.yearsBetween(person.getBirthday(), calendar.getTime())) : BLANK);
        fillText("document_number", person.getDocumentNumber());
        fillText("document_type", person.getDocumentType().getName());
        fillText("phone_number", contactInfo.getPhoneNumber());
        fillText("email", contactInfo.getEmail());
        fillText("housing_type", contactInfo.getHousingType().getType().toUpperCase());
        fillText("address", String.format("DIRECCIÓN: %s", contactInfo.getFullAddressBO()));
        fillText("marital_status", maritalStatus.toUpperCase());
        fillText("study_level", person.getStudyLevel().getLevel().toUpperCase());
        fillText("dependents_number", String.valueOf(person.getDependents()));
        fillText("profession_occupation", String.format("%s / %s", person.getProfessionOccupation().getOccupation(), person.getProfession().getProfession()).toUpperCase());

        if (person.hasPartner()) {
            fillText("partner_fullname", String.format("%s %s %s", partner.getFirstSurname(), partner.getLastSurname(), partner.getName()).toUpperCase());
            fillText("relationship", person.getMaritalStatus().getId() == MaritalStatus.COHABITANT ? "Conviviente" : "Esposo(a)");
            fillText("gender_partner", partner.getGender() != null ? String.valueOf(partner.getGender()) : BLANK);
            fillText("birthdate_partner", partner.getBirthday() != null ? sdf.format(partner.getBirthday()) : BLANK);
            fillText("identification_partner", String.format("%s. %s", partner.getDocumentType().getName(), partner.getDocumentNumber()));
            fillText("country_partner", partner.getCountry() != null ? partner.getCountry().getName() : BLANK);
        }
    }

}
