package com.affirm.strategy.contracts;

import com.affirm.common.dao.CreditDAO;
import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.model.catalog.ActivityType;
import com.affirm.common.model.catalog.EntityProductParams;
import com.affirm.common.model.catalog.IdentityDocumentType;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.UtilService;
import com.affirm.strategy.ContractStrategy;
import com.affirm.system.configuration.Configuration;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class TarjetasPeruanasContractStrategy extends BaseContract implements ContractStrategy {
    public TarjetasPeruanasContractStrategy(byte[] contract, CatalogService catalogService, PersonDAO personDAO, UtilService utilService, CreditDAO creditDAO, LoanApplicationDAO loanApplicationDAO) throws Exception {
        super(contract, catalogService, personDAO, utilService, creditDAO, loanApplicationDAO, null);
    }

    @Override
    public void fillPdf(Person person, Credit credit, LoanOffer loanOffer, EntityProductParams params, User user, String signature, Person partner, PersonContactInformation contactInfo, List<PersonOcupationalInformation> ocupations, PersonOcupationalInformation principalOcupation, PersonBankAccountInformation personBankAccountInformation, LoanApplication loanApplication, Locale locale, SunatResult sunatResult) throws Exception {

        PersonContactInformation personContactInformation = personDAO.getPersonContactInformation(Configuration.getDefaultLocale(), person.getId());
        PersonOcupationalInformation personOcupationalInformation = ocupations != null ? ocupations.stream().filter(o -> o.getNumber() == PersonOcupationalInformation.PRINCIPAL).findFirst().orElse(null) : null;

        fillText("form.header.date", utilService.dateFormat(loanApplication.getRegisterDate()));
        fillText("form.header.card.number", null);// TODO
//        CUERPO
        fillText("form.client.names", person.getName());
        fillText("form.client.father.lastname", person.getFirstSurname());
        fillText("form.client.mother.lastname", person.getLastSurname());

        Calendar birthdate = Calendar.getInstance();
        birthdate.setTime(person.getBirthday());

        fillText("form.client.birthdate.day", birthdate.get(Calendar.DATE));
        fillText("form.client.birthdate.month", birthdate.get(Calendar.MONTH) + 1);
        fillText("form.client.birthdate.year", birthdate.get(Calendar.YEAR));

        fillCheckBox("form.client.document.hasDni", person.getDocumentType().getId() == IdentityDocumentType.DNI);
        fillCheckBox("form.client.document.hasCe", person.getDocumentType().getId() == IdentityDocumentType.CE);
        fillCheckBox("form.client.document.hasPassport", false);// NO HAY PASAPORTE
        fillText("form.client.document.number", person.getDocumentNumber());

        fillText("form.client.nationality", person.getNationality() != null ? person.getNationality().getName() : null);
        if(person.getDocumentType().getId() == IdentityDocumentType.CE) {
            fillText("form.client.address.residence", personContactInformation.getAddressStreetName());
        }

        fillText("form.client.email", user.getEmail());

        if (personContactInformation != null) {
            fillText("form.client.address.name", personContactInformation.getAddressStreetName());

            if (personContactInformation.getAddressUbigeo() != null) {
                fillText("form.client.address.district", personContactInformation.getAddressUbigeo().getDistrict().getName());
                fillText("form.client.address.province", personContactInformation.getAddressUbigeo().getProvince().getName());
                fillText("form.client.address.department", personContactInformation.getAddressUbigeo().getDepartment().getName());
            }

            fillText("form.client.phone", null);
            fillText("form.client.cellphone", personContactInformation.getPhoneNumber());
        }

        if (personOcupationalInformation != null) {
            fillText("form.client.work", personOcupationalInformation.getCompanyName(), 8);
            fillText("form.client.workphone", personOcupationalInformation.getPhoneNumber());

            if (personOcupationalInformation.getOcupation() != null) {
                fillText("form.client.workas",  personOcupationalInformation.getOcupation().getOcupation(), 8);
                fillText("form.client.ocupation", personOcupationalInformation.getOcupation().getOcupation());
            }

            if (personOcupationalInformation.getActivityType() != null) {
                fillCheckBox("form.client.isDependent", personOcupationalInformation.getActivityType().getId() == ActivityType.DEPENDENT);
                fillCheckBox("form.client.isIndependent", personOcupationalInformation.getActivityType().getId() == ActivityType.INDEPENDENT);
            }

        }

        fillCheckBox("form.client.isOfac", false);
        fillCheckBox("form.client.isNotOfac", true);
        fillCheckBox("form.client.isPep", false);
        fillCheckBox("form.client.isNotPep", true);

        fillText("form.client.beneficiary", null);
        fillText("form.beneficiary.name", null);
        fillText("form.beneficiary.father.lastname", null);
        fillText("form.beneficiary.mother.lastname", null);
        fillText("form.beneficiary.document.number", null);
        fillText("form.beneficiary.birthdate.day", null);
        fillText("form.beneficiary.birthdate.month", null);
        fillText("form.beneficiary.birthdate.year", null);
        fillText("form.beneficiary.relationship", null);

//        PIE
        fillText("form.purpose", "Tarjetahabiente", 11);// POR DEFECTO
        fillText("form.obs", null);// OBS
        fillCheckBox("form.hasAccept", true);
        fillText("form.client.sign", signature != null ? signature : " ", handWritingFont);

//        2DA PAGINA
//        if(personDisqualifier.stream().anyMatch(pd -> PersonDisqualifier.PEP.equals(pd.getType()))) {
//            fillCheckBox(pdfDoc, acroForm, "form.client.pep.inActivity.yes", "");
//            fillCheckBox(pdfDoc, acroForm, "form.client.pep.inActivity.no", "");
//            fillText(pdfDoc, acroForm, "form.client.pep.title", "");
//            fillText(pdfDoc, acroForm, "form.client.pep.institution", "");
//        }
    }
}
