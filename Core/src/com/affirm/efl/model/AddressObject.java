package com.affirm.efl.model;

import com.affirm.common.model.transactional.PersonContactInformation;
import com.affirm.common.model.transactional.PersonOcupationalInformation;

/**
 * Created by dev5 on 04/07/17.
 */
public class AddressObject {

    private String city;
    private String country;
    private Double latitude;
    private Double longitude;
    private String street;
    private String postalCode;
    private String region;

    public AddressObject(PersonContactInformation contactInfo){
        this.city = "Lima";
        this.country = "PE";
        this.latitude = contactInfo.getAddressLatitude();
        this.longitude = contactInfo.getAddressLongitude();
        this.street = contactInfo.getAddressStreetName();
    }

    public AddressObject(PersonOcupationalInformation ocupationalInfo){
        if(ocupationalInfo != null){
            this.city = "Lima";
            this.country = "PE";
            this.street = ocupationalInfo.getAddress();
        }
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
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

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }
}
