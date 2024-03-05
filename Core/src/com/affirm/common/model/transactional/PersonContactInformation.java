package com.affirm.common.model.transactional;

import com.affirm.common.model.catalog.*;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;
import java.util.Locale;

/**
 * Created by jrodriguez on 08/06/16.
 */
public class PersonContactInformation implements Serializable {

    private Integer personId;
    private String email;
    private Boolean emailVerified;
    private Integer phoneNumberId;
    private String phoneCountryCode;
    private String phoneNumber;
    private Boolean phoneVerified;
    private Integer addressId;
    private Ubigeo addressUbigeo;
    private StreetType addressStreetType;
    private String addressStreetName;
    private String addressStreetNumber;
    private String addressInterior;
    private String addressDetail;
    private Double addressLatitude;
    private Double addressLongitude;
    private Boolean phonePreviouslyUsed;
    private HousingType housingType;
    private Date registerDate;
    private String phoneIndicator;
    private Department department;
    private Province province;
    private District district;
    private String phoneNumberType;
    private Integer residenceTime;

    public void fillFromDb(JSONObject json, CatalogService catalog, Locale locale) throws Exception {
        setPersonId(JsonUtil.getIntFromJson(json, "person_id", null));
        setEmail(JsonUtil.getStringFromJson(json, "email", null));
        setEmailVerified(JsonUtil.getBooleanFromJson(json, "email_verified", null));
        setPhoneNumberId(JsonUtil.getIntFromJson(json, "phone_number_id", null));
        setPhoneCountryCode(JsonUtil.getStringFromJson(json, "country_code", null));
        setPhoneNumber(JsonUtil.getStringFromJson(json, "phone_number", null));
        setPhoneVerified(JsonUtil.getBooleanFromJson(json, "phone_verified", null));
        setAddressId(JsonUtil.getIntFromJson(json, "address_id", null));
        if (JsonUtil.getStringFromJson(json, "ubigeo_id", null) != null) {
            setAddressUbigeo(catalog.getUbigeo(JsonUtil.getStringFromJson(json, "ubigeo_id", null)));
        }
        if (JsonUtil.getIntFromJson(json, "street_type_id", null) != null) {
            setAddressStreetType(catalog.getStreetType(JsonUtil.getIntFromJson(json, "street_type_id", null)));
        }
        setAddressStreetName(JsonUtil.getStringFromJson(json, "street_name", null));
        setAddressStreetNumber(JsonUtil.getStringFromJson(json, "street_number", null));
        setAddressInterior(JsonUtil.getStringFromJson(json, "interior", null));
        setAddressDetail(JsonUtil.getStringFromJson(json, "detail", null));
        setAddressLatitude(JsonUtil.getDoubleFromJson(json, "address_latitude", null));
        setAddressLongitude(JsonUtil.getDoubleFromJson(json, "address_longitude", null));
        setPhonePreviouslyUsed(JsonUtil.getBooleanFromJson(json, "phone_previously_used", null));
        if (JsonUtil.getIntFromJson(json, "housing_type_id", null) != null)
            setHousingType(catalog.getHousingType(locale, JsonUtil.getIntFromJson(json, "housing_type_id", null)));
        if(JsonUtil.getPostgresDateFromJson(json, "register_date", null) != null)
            setRegisterDate(JsonUtil.getPostgresDateFromJson(json, "register_date", null));
        setPhoneIndicator(JsonUtil.getStringFromJson(json, "phone_indicator", null));
        if (JsonUtil.getIntFromJson(json, "locality_id", null) != null) {
            setDistrict(catalog.getGeneralDistrictById(JsonUtil.getLongFromJson(json, "locality_id", null)));
            setProvince(catalog.getGeneralProvinceByDistrict(getDistrict().getDistrictId()));

            if (("+" + CountryParam.COUNTRY_COLOMBIA).equals(phoneCountryCode))
                setDepartment(catalog.getGeneralDepartmentById(getProvince().getDepartment().getDepartmentId()));
        }
        setPhoneNumberType(JsonUtil.getStringFromJson(json, "phone_number_type", null));
        setResidenceTime(JsonUtil.getIntFromJson(json, "residence_time", null));
    }

    public String getPhoneNumberWithoutCode() {
        if (phoneNumber == null)
            return null;
        if (phoneNumber.contains("(") && phoneNumber.contains(")"))
            return phoneNumber.substring(phoneNumber.indexOf(')') + 1).replaceAll(" ", "");
        return phoneNumber;
    }

    public String getPhoneCode() {
        if (phoneNumber == null)
            return null;
        if (phoneNumber.contains("(") && phoneNumber.contains(")"))
            return phoneNumber.substring(phoneNumber.indexOf('(') + 1, phoneNumber.indexOf(')')).replaceAll(" ", "");
        return null;
    }

    public String getFullAddressBO() {
        String fullAddress = "";
        if (addressStreetName != null) {
            fullAddress = fullAddress + addressStreetName + " ";
        }

        if (addressUbigeo != null) {
            fullAddress = fullAddress + " - " + addressUbigeo.getDistrict().getName() + " " +
                    addressUbigeo.getProvince().getName() + " " + addressUbigeo.getDepartment().getName();
        }else{
            fullAddress = fullAddress + " - " + (getProvince() != null ? getProvince().getName() : "") + ", " + (getDistrict() != null ? getDistrict().getName() : "");
        }
        return fullAddress;
    }

    public String getFullAddressBOWithReference() {
        String fullAddress = "";
        if (addressStreetName != null) {
            fullAddress = fullAddress + addressStreetName + " ";
        }
        if(addressDetail != null){
            fullAddress =  fullAddress + "Ref.: " + addressDetail + " ";
        }
        if (addressUbigeo != null) {
            fullAddress = fullAddress + " - " + addressUbigeo.getDistrict().getName() + " " +
                    addressUbigeo.getProvince().getName() + " " + addressUbigeo.getDepartment().getName();
        }else{
            fullAddress = fullAddress + " - " + (getDistrict() != null ? getDistrict().getName() : "") + ", " + (getProvince() != null ? getProvince().getName() : "") + ", " + (getDepartment() != null ? getDepartment().getName() : "");
        }

        return fullAddress;
    }

    public String getFullAddressBOWithoutUbigeo() {
        String fullAddress = "";
        if (addressStreetName != null) {
            fullAddress = fullAddress + addressStreetName + " ";
        }
        if(addressDetail != null){
            fullAddress =  fullAddress + "Ref.: " + addressDetail + " ";
        }
        return fullAddress;
    }

    public String getFullAddress() {
        StringBuilder fullAddress = new StringBuilder();

        if (addressStreetType != null && addressStreetType.getType() != null && !addressStreetType.getType().isEmpty()) fullAddress.append(addressStreetType.getType()).append(" ");

        if (addressStreetName != null && !addressStreetName.isEmpty()) fullAddress.append(addressStreetName).append(" ");

        if (addressStreetNumber != null && !addressStreetNumber.isEmpty()) fullAddress.append(addressStreetNumber).append(" ");

        if (addressInterior != null && !addressInterior.isEmpty()) fullAddress.append(", Int: ").append(addressInterior).append(" ");

        if (addressUbigeo != null) {
            fullAddress.append(String.format("%s %s %s ",
                    addressUbigeo.getDistrict().getName(),
                    addressUbigeo.getProvince().getName(),
                    addressUbigeo.getDepartment().getName()));
        } else {
            fullAddress.append(String.format("%s, %s ",
                    getProvince() != null ? getProvince().getName() : "",
                    getDistrict() != null ? getDistrict().getName() : ""));
        }

        if (addressDetail != null && !addressDetail.isEmpty()) {
            fullAddress.append(String.format(", Ref.: %s", addressDetail));
        }

        return fullAddress.toString().trim();
    }

    public Integer getPersonId() {
        return personId;
    }

    public void setPersonId(Integer personId) {
        this.personId = personId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(Boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public Integer getPhoneNumberId() {
        return phoneNumberId;
    }

    public void setPhoneNumberId(Integer phoneNumberId) {
        this.phoneNumberId = phoneNumberId;
    }

    public String getPhoneCountryCode() {
        return phoneCountryCode;
    }

    public void setPhoneCountryCode(String phoneCountryCode) {
        this.phoneCountryCode = phoneCountryCode;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Boolean getPhoneVerified() {
        return phoneVerified;
    }

    public void setPhoneVerified(Boolean phoneVerified) {
        this.phoneVerified = phoneVerified;
    }

    public Integer getAddressId() {
        return addressId;
    }

    public void setAddressId(Integer addressId) {
        this.addressId = addressId;
    }

    public Ubigeo getAddressUbigeo() {
        return addressUbigeo;
    }

    public void setAddressUbigeo(Ubigeo addressUbigeo) {
        this.addressUbigeo = addressUbigeo;
    }

    public StreetType getAddressStreetType() {
        return addressStreetType;
    }

    public void setAddressStreetType(StreetType addressStreetType) {
        this.addressStreetType = addressStreetType;
    }

    public String getAddressStreetName() {
        return addressStreetName != null ? addressStreetName.replace("null","") : null;
    }

    public void setAddressStreetName(String addressStreetName) {
        this.addressStreetName = addressStreetName;
    }

    public String getAddressStreetNumber() {
        return addressStreetNumber;
    }

    public void setAddressStreetNumber(String addressStreetNumber) {
        this.addressStreetNumber = addressStreetNumber;
    }

    public String getAddressInterior() {
        return addressInterior;
    }

    public void setAddressInterior(String addressInterior) {
        this.addressInterior = addressInterior;
    }

    public String getAddressDetail() {
        return addressDetail;
    }

    public void setAddressDetail(String addressDetail) {
        this.addressDetail = addressDetail;
    }

    public Double getAddressLatitude() {
        return addressLatitude;
    }

    public void setAddressLatitude(Double addressLatitude) {
        this.addressLatitude = addressLatitude;
    }

    public Double getAddressLongitude() {
        return addressLongitude;
    }

    public void setAddressLongitude(Double addressLongitude) {
        this.addressLongitude = addressLongitude;
    }

    public Boolean getPhonePreviouslyUsed() {
        return phonePreviouslyUsed;
    }

    public void setPhonePreviouslyUsed(Boolean phonePreviouslyUsed) {
        this.phonePreviouslyUsed = phonePreviouslyUsed;
    }

    public HousingType getHousingType() {
        return housingType;
    }

    public void setHousingType(HousingType housingType) {
        this.housingType = housingType;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public String getPhoneIndicator() {
        return phoneIndicator;
    }

    public void setPhoneIndicator(String phoneIndicator) {
        this.phoneIndicator = phoneIndicator;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public Province getProvince() {
        return province;
    }

    public void setProvince(Province province) {
        this.province = province;
    }

    public District getDistrict() {
        return district;
    }

    public void setDistrict(District district) {
        this.district = district;
    }

    public String getPhoneNumberType() {
        return phoneNumberType;
    }

    public void setPhoneNumberType(String phoneNumberType) {
        this.phoneNumberType = phoneNumberType;
    }

    public Integer getResidenceTime() { return residenceTime; }

    public void setResidenceTime(Integer residenceTime) { this.residenceTime = residenceTime; }
}