package com.affirm.common.model.transactional;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

public class TarjetasPeruanasCreditActivation implements Serializable {
    public enum DeliveryAddress{
        HOME,WORKPLACE
    }

    Integer creditId, documentId, ocupationId, activityTypeId, professionId, maritalStatusId, nationalityId,employmentTime;
    String documentNumber, firstSurname, LastSurname, name, companyName, ruc, email, phoneNumber, landLine, homeAddress, ubigeoId,companyAddress,cardColor,income, workUbigeo;
    DeliveryAddress deliveryPlace;
    Date dob;

    public void fillFromDb(JSONObject json) throws Exception {
        if (null != JsonUtil.getStringFromJson(json, "person_name", null))
            setName(JsonUtil.getStringFromJson(json, "person_name", null));
        if (null != JsonUtil.getStringFromJson(json, "first_surname", null))
            setFirstSurname(JsonUtil.getStringFromJson(json, "first_surname", null));
        if (null != JsonUtil.getStringFromJson(json, "last_surname", null))
            setLastSurname(JsonUtil.getStringFromJson(json, "last_surname", null));
        if (null != JsonUtil.getStringFromJson(json, "birthday", null))
            setDob(JsonUtil.getPostgresDateFromJson(json, "birthday", null));
        if (null != JsonUtil.getStringFromJson(json, "ubigeo_id", null))
            setUbigeoId(JsonUtil.getStringFromJson(json, "ubigeo_id", null));
        if (null != JsonUtil.getIntFromJson(json, "credit_id", null))
            setCreditId(JsonUtil.getIntFromJson(json, "credit_id", null));
        if (null != JsonUtil.getIntFromJson(json, "document_id", null))
            setDocumentId(JsonUtil.getIntFromJson(json, "document_id", null));
        if (null != JsonUtil.getStringFromJson(json, "document_number", null))
            setDocumentNumber(JsonUtil.getStringFromJson(json, "document_number", null));
        if (null != JsonUtil.getIntFromJson(json, "ocupation_id", null))
            setOcupationId(JsonUtil.getIntFromJson(json, "ocupation_id", null));
        if (null != JsonUtil.getStringFromJson(json, "company_name", null))
            setCompanyName(JsonUtil.getStringFromJson(json, "company_name", null));
        if (null != JsonUtil.getStringFromJson(json, "ruc", null))
            setRuc(JsonUtil.getStringFromJson(json, "ruc", null));
        if (null != JsonUtil.getIntFromJson(json, "activity_type_id", null))
            setActivityTypeId(JsonUtil.getIntFromJson(json, "activity_type_id", null));
        if (null != JsonUtil.getIntFromJson(json, "profession_id", null))
            setProfessionId(JsonUtil.getIntFromJson(json, "profession_id", null));
        if (null != JsonUtil.getIntFromJson(json, "marital_status_id", null))
            setMaritalStatusId(JsonUtil.getIntFromJson(json, "marital_status_id", null));
        if (null != JsonUtil.getIntFromJson(json, "nationality_id", null))
            setNationalityId(JsonUtil.getIntFromJson(json, "nationality_id", null));
        if (null != JsonUtil.getStringFromJson(json, "email", null))
            setEmail(JsonUtil.getStringFromJson(json, "email", null));
        if (null != JsonUtil.getStringFromJson(json, "home_address", null))
            setHomeAddress(JsonUtil.getStringFromJson(json, "home_address", null));
        if (null != JsonUtil.getStringFromJson(json, "landline", null))
            setLandLine(JsonUtil.getStringFromJson(json, "landline", null));
        if (null != JsonUtil.getStringFromJson(json, "phone_number", null))
            setPhoneNumber(JsonUtil.getStringFromJson(json, "phone_number", null));
        if (null != JsonUtil.getStringFromJson(json, "company_address", null))
            setCompanyAddress(JsonUtil.getStringFromJson(json, "company_address", null));
        if (null != JsonUtil.getStringFromJson(json, "color", null))
            setCardColor(JsonUtil.getStringFromJson(json, "color", null));
        if (null != JsonUtil.getStringFromJson(json, "delivery_place", null)){
            String delivery = JsonUtil.getStringFromJson(json, "delivery_place", null);
            setDeliveryPlace(delivery.equals("H")?DeliveryAddress.HOME:DeliveryAddress.WORKPLACE);
        }
        if (null != JsonUtil.getDoubleFromJson(json, "income", null))
            setIncome(String.valueOf(JsonUtil.getDoubleFromJson(json, "income", null)));

        if (null != JsonUtil.getIntFromJson(json, "employment_time", null))
            setEmploymentTime(JsonUtil.getIntFromJson(json, "employment_time", null));
        if (null != JsonUtil.getStringFromJson(json, "work_ubigeo_id", null))
            setWorkUbigeoId(JsonUtil.getStringFromJson(json, "work_ubigeo_id", null));

    }

    public Integer getCreditId() {
        return creditId;
    }

    public void setCreditId(Integer creditId) {
        this.creditId = creditId;
    }

    public Integer getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Integer documentId) {
        this.documentId = documentId;
    }

    public Integer getOcupationId() {
        return ocupationId;
    }

    public void setOcupationId(Integer ocupationId) {
        this.ocupationId = ocupationId;
    }

    public Integer getActivityTypeId() {
        return activityTypeId;
    }

    public void setActivityTypeId(Integer activityTypeId) {
        this.activityTypeId = activityTypeId;
    }

    public Integer getProfessionId() {
        return professionId;
    }

    public void setProfessionId(Integer professionId) {
        this.professionId = professionId;
    }

    public Integer getMaritalStatusId() {
        return maritalStatusId;
    }

    public void setMaritalStatusId(Integer maritalStatusId) {
        this.maritalStatusId = maritalStatusId;
    }

    public Integer getNationalityId() {
        return nationalityId;
    }

    public void setNationalityId(Integer nationalityId) {
        this.nationalityId = nationalityId;
    }

    public String getUbigeoId() {
        return ubigeoId;
    }

    public void setUbigeoId(String ubigeoId) {
        this.ubigeoId = ubigeoId;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getFirstSurname() {
        return firstSurname;
    }

    public void setFirstSurname(String firstSurname) {
        this.firstSurname = firstSurname;
    }

    public String getLastSurname() {
        return LastSurname;
    }

    public void setLastSurname(String lastSurname) {
        LastSurname = lastSurname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getLandLine() {
        return landLine;
    }

    public void setLandLine(String landLine) {
        this.landLine = landLine;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }


    public String getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(String homeAddress) {
        this.homeAddress = homeAddress;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }
    public String getCardColor() {
        return cardColor;
    }

    public void setCardColor(String cardColor) {
        this.cardColor = cardColor;
    }

    public DeliveryAddress getDeliveryPlace() {
        return deliveryPlace;
    }

    public void setDeliveryPlace(DeliveryAddress deliveryPlace) {
        this.deliveryPlace = deliveryPlace;
    }


    public String getIncome() {
        return income;
    }

    public void setIncome(String income) {
        this.income = income;
    }
    public Integer getEmploymentTime() {
        return employmentTime;
    }

    public void setEmploymentTime(Integer employmentTime) {
        this.employmentTime = employmentTime;
    }
    public String getWorkUbigeoId() {
        return workUbigeo;
    }

    public void setWorkUbigeoId(String workUbigeo) {
        this.workUbigeo = workUbigeo;
    }
}