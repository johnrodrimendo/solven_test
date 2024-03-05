package com.affirm.common.model.transactional;

import com.affirm.common.model.catalog.CountryParam;
import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

public class User implements Serializable {

    private Integer id;
    private Integer personId;
    //private Integer clientId;
    private String facebookId;
    private String fullName;
    private String countryCode;
    private String phoneNumber;
    private Boolean phoneVerified;
    private String email;
    private Boolean emailVerified;
    private Integer samplingId;
    private JSONObject jsEmailage;
    private Date noAuthLinkExpiration;
    private String messengerId;
    private String password;

    private Person person;

    public void fillFromDb(JSONObject json) {
        setId(JsonUtil.getIntFromJson(json, "user_id", null));
        setPersonId(JsonUtil.getIntFromJson(json, "person_id", null));
        //setClientId(JsonUtil.getIntFromJson(json, "client_id", null));
        setFacebookId(JsonUtil.getStringFromJson(json, "facebook_id", null));
        setFullName(JsonUtil.getStringFromJson(json, "full_name", null));
        setCountryCode(JsonUtil.getStringFromJson(json, "country_code", null));
        setPhoneNumber(JsonUtil.getStringFromJson(json, "phone_number", null));
        setPhoneVerified(JsonUtil.getBooleanFromJson(json, "phone_verified", null));
        setEmail(JsonUtil.getStringFromJson(json, "email", null));
        setEmailVerified(JsonUtil.getBooleanFromJson(json, "email_verified", null));
        setSamplingId(JsonUtil.getIntFromJson(json, "sampling_id", null));
        setJsEmailage(JsonUtil.getJsonObjectFromJson(json, "js_emailage", null));
        setNoAuthLinkExpiration(JsonUtil.getPostgresDateFromJson(json, "auth_link_expiration", null));
        setMessengerId(JsonUtil.getStringFromJson(json, "messenger_id", null));
        setPassword(JsonUtil.getStringFromJson(json, "password", null));
    }

    public String getSimpleName() {
        if (fullName != null)
            return fullName.split(" ")[0];
        return null;
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

    public String getPhoneNumberForCall(int countryId) {
        if (phoneNumber != null) {
            switch (countryId) {
                case CountryParam.COUNTRY_ARGENTINA:
                    return "9" + phoneNumber;
            }
        }
        return phoneNumber;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPersonId() {
        return personId;
    }

    public void setPersonId(Integer personId) {
        this.personId = personId;
    }

    /*
        public Integer getClientId() {
            return clientId;
        }

        public void setClientId(Integer clientId) {
            this.clientId = clientId;
        }
    */
    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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

    public Integer getSamplingId() {
        return samplingId;
    }

    public void setSamplingId(Integer samplingId) {
        this.samplingId = samplingId;
    }

    public JSONObject getJsEmailage() {
        return jsEmailage;
    }

    public void setJsEmailage(JSONObject jsEmailage) {
        this.jsEmailage = jsEmailage;
    }

    public Date getNoAuthLinkExpiration() {
        return noAuthLinkExpiration;
    }

    public void setNoAuthLinkExpiration(Date noAuthLinkExpiration) {
        this.noAuthLinkExpiration = noAuthLinkExpiration;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public String getEmailValidationResult() {
        JSONObject json = new JSONObject();
        if (jsEmailage != null) {
            if (!jsEmailage.isNull("email")) {
                json.put("emailResults", jsEmailage.getJSONObject("email").getJSONObject("query").getJSONArray("results"));
            }
            if (!jsEmailage.isNull("emailIp")) {
                json.put("emailIpResults", jsEmailage.getJSONObject("emailIp").getJSONObject("query").getJSONArray("results"));
            }

            return json.toString();
        }
        return "{\"error\":\"No existe el reporte.\"}";
    }

    public String getEmailResult() {
        if (jsEmailage != null) {
            if (!jsEmailage.isNull("email") && !jsEmailage.getJSONObject("email").isNull("query")) {
                JSONObject json = new JSONObject(jsEmailage.getJSONObject("email").getJSONObject("query").toString());
                json.remove("queryType");
                return json.toString();
            }
        }
        return "{\"error\":\"No existe el reporte.\"}";
    }

    public String getEmailIpResult() {
        if (jsEmailage != null) {
            if (!jsEmailage.isNull("emailIp")) {
                JSONObject json = new JSONObject(jsEmailage.getJSONObject("emailIp").getJSONObject("query").toString());
                json.remove("queryType");
                return json.toString();
            }

        }
        return "{\"error\":\"No existe el reporte.\"}";
    }

    public String getMessengerId() {
        return messengerId;
    }

    public void setMessengerId(String messengerId) {
        this.messengerId = messengerId;
    }

    public Boolean getPhoneVerified() {
        return phoneVerified;
    }

    public void setPhoneVerified(Boolean phoneVerified) {
        this.phoneVerified = phoneVerified;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}