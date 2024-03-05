package com.affirm.efl.model;

import com.affirm.common.dao.TranslatorDAO;
import com.affirm.common.model.transactional.Person;
import com.affirm.common.model.transactional.PersonContactInformation;
import com.affirm.common.model.transactional.PersonOcupationalInformation;
import com.affirm.common.model.transactional.User;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dev5 on 04/07/17.
 */
public class ApplicantObject {

    private String birthday;
    private LoanObject loan;
    private String employmentStatus = "privateSector";
    private AddressObjectList addresses;
    private String maritalStatus;
    private String email;
    private String firstName;
    private String lastName;
    private Map<String, String> idNumbers;
    private String gender;

    private transient DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    public ApplicantObject(int entityId, TranslatorDAO translatorDAO, int loanApplicationId, Person person, User user, PersonContactInformation contactInfo, PersonOcupationalInformation ocupationalInfo) throws Exception{
        this.birthday = person.getBirthday() != null ? df.format(person.getBirthday()) : null;
        this.loan = new LoanObject(ocupationalInfo);
        this.employmentStatus = "privateSector";
        this.addresses = new AddressObjectList(contactInfo, ocupationalInfo);
        this.maritalStatus = translatorDAO.translate(entityId, 15, person.getMaritalStatus().getId().toString(), null);
        this.email = user.getEmail();
        this.firstName = person.getName();
        this.lastName = person.getFullSurnames();
        this.idNumbers = new HashMap<String, String>();
        this.idNumbers.put("externalKey",String.valueOf(loanApplicationId));
        if(person.getGender() != null && !person.getGender().toString().isEmpty())
            this.gender = translatorDAO.translate(entityId, 16,person.getGender().toString(), null);
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public LoanObject getLoan() {
        return loan;
    }

    public void setLoan(LoanObject loan) {
        this.loan = loan;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(String maritalStus) {
        this.maritalStatus = maritalStus;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
