/**
 *
 */
package com.affirm.common.model.transactional;

import com.affirm.common.model.catalog.IdentityDocumentType;
import com.affirm.common.model.catalog.Relationship;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Locale;

/**
 * @author jrodriguez
 */
public class Referral implements Serializable {

    private Integer id;
    private Integer personId;
    private Relationship relationship;
    private String fullName;
    private String countryCode;
    private String phoneNumber;
    private String referralInfo;
    private Boolean validated;
    private String phoneType;
    private IdentityDocumentType documentType;
    private String documentNumber;

    public void fillFromDb(JSONObject json, CatalogService catalog, Locale locale) throws Exception {
        setId(JsonUtil.getIntFromJson(json, "referral_id", null));
        setPersonId(JsonUtil.getIntFromJson(json, "person_id", null));
        if (JsonUtil.getIntFromJson(json, "relationship_id", null) != null) {
            setRelationship(catalog.getRelationship(JsonUtil.getIntFromJson(json, "relationship_id", null), locale));
        }
        setFullName(JsonUtil.getStringFromJson(json, "full_name", null));
        setCountryCode(JsonUtil.getStringFromJson(json, "country_code", null));
        setPhoneNumber(JsonUtil.getStringFromJson(json, "phone_number", null));
        setReferralInfo(JsonUtil.getStringFromJson(json, "referral_info", null));
        setValidated(JsonUtil.getBooleanFromJson(json, "is_validated", null));
        setPhoneType(JsonUtil.getStringFromJson(json, "phone_number_type", null));
        if (JsonUtil.getIntFromJson(json, "document_id", null) != null) {
            setDocumentType(catalog.getIdentityDocumentType(JsonUtil.getIntFromJson(json, "document_id", null)));
        }
        setDocumentNumber(JsonUtil.getStringFromJson(json, "document_number", null));
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

    public Relationship getRelationship() {
        return relationship;
    }

    public void setRelationship(Relationship relationship) {
        this.relationship = relationship;
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

    public String getReferralInfo() {
        return referralInfo;
    }

    public void setReferralInfo(String referralInfo) {
        this.referralInfo = referralInfo;
    }

    public Boolean getValidated() {
        return validated;
    }

    public void setValidated(Boolean validated) {
        this.validated = validated;
    }

    public String getPhoneType() {
        return phoneType;
    }

    public void setPhoneType(String phoneType) {
        this.phoneType = phoneType;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public IdentityDocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(IdentityDocumentType documentType) {
        this.documentType = documentType;
    }
}

