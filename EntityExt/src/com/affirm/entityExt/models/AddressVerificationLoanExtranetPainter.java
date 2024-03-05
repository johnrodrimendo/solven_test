package com.affirm.entityExt.models;

import com.affirm.common.model.catalog.TrackingAction;
import com.affirm.common.model.transactional.PersonContactInformation;
import com.affirm.common.model.transactional.Referral;

import java.util.List;

public class AddressVerificationLoanExtranetPainter {

    private Integer loanId;
    private String address;
    private String email;
    private Double latitude;
    private Double longitude;
    private String phoneNumber;
    private TrackingAction action;
    private Integer countryId;
    private PersonContactInformation personContactInformation;

    public Integer getLoanId() {
        return loanId;
    }

    public void setLoanId(Integer loanId) {
        this.loanId = loanId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public TrackingAction getAction() {
        return action;
    }

    public void setAction(TrackingAction action) {
        this.action = action;
    }

    public Integer getCountryId() {
        return countryId;
    }

    public void setCountryId(Integer countryId) {
        this.countryId = countryId;
    }

    public PersonContactInformation getPersonContactInformation() {
        return personContactInformation;
    }

    public void setPersonContactInformation(PersonContactInformation personContactInformation) {
        this.personContactInformation = personContactInformation;
    }
}
