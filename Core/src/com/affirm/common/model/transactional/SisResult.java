package com.affirm.common.model.transactional;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by john on 29/09/16.
 */
public class SisResult implements Serializable {

    private Integer queryId;
    private Integer inDocType;
    private String inDocNumber;

    private String fullName;
    private String documentNumber;
    private String affiliationNumber;
    private String insuranceType;
    private String insuredType;
    private String formatType;
    private Date enrollmentDate;
    private String benefitPlan;
    private String healthCenter;
    private String healthCenterAddress;
    private String status;
    private String validUntil;

    public void fillFromDb(JSONObject json) {
        queryId = JsonUtil.getIntFromJson(json, "query_id", null);
        inDocType = JsonUtil.getIntFromJson(json, "in_document_type", null);
        inDocNumber = JsonUtil.getStringFromJson(json, "in_document_number", null);

        fullName = JsonUtil.getStringFromJson(json, "full_name", null);
        documentNumber = JsonUtil.getStringFromJson(json, "document_number", null);
        affiliationNumber = JsonUtil.getStringFromJson(json, "affiliation_number", null);
        insuranceType = JsonUtil.getStringFromJson(json, "insurance_type", null);

        insuredType = JsonUtil.getStringFromJson(json, "insured_type", null);
        formatType = JsonUtil.getStringFromJson(json, "format_type", null);
        enrollmentDate = JsonUtil.getPostgresDateFromJson(json, "enrollment_date", null);
        benefitPlan = JsonUtil.getStringFromJson(json, "benefit_plan", null);

        healthCenter = JsonUtil.getStringFromJson(json, "health_center", null);
        healthCenterAddress = JsonUtil.getStringFromJson(json, "health_center_address", null);
        status = JsonUtil.getStringFromJson(json, "status", null);
        validUntil = JsonUtil.getStringFromJson(json, "valid_until", null);
    }

    public Integer getQueryId() {
        return queryId;
    }

    public void setQueryId(Integer queryId) {
        this.queryId = queryId;
    }

    public Integer getInDocType() {
        return inDocType;
    }

    public void setInDocType(Integer inDocType) {
        this.inDocType = inDocType;
    }

    public String getInDocNumber() {
        return inDocNumber;
    }

    public void setInDocNumber(String inDocNumber) {
        this.inDocNumber = inDocNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getAffiliationNumber() {
        return affiliationNumber;
    }

    public void setAffiliationNumber(String affiliationNumber) {
        this.affiliationNumber = affiliationNumber;
    }

    public String getInsuranceType() {
        return insuranceType;
    }

    public void setInsuranceType(String insuranceType) {
        this.insuranceType = insuranceType;
    }

    public String getInsuredType() {
        return insuredType;
    }

    public void setInsuredType(String insuredType) {
        this.insuredType = insuredType;
    }

    public String getFormatType() {
        return formatType;
    }

    public void setFormatType(String formatType) {
        this.formatType = formatType;
    }

    public Date getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(Date enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    public String getBenefitPlan() {
        return benefitPlan;
    }

    public void setBenefitPlan(String benefitPlan) {
        this.benefitPlan = benefitPlan;
    }

    public String getHealthCenter() {
        return healthCenter;
    }

    public void setHealthCenter(String healthCenter) {
        this.healthCenter = healthCenter;
    }

    public String getHealthCenterAddress() {
        return healthCenterAddress;
    }

    public void setHealthCenterAddress(String healthCenterAddress) {
        this.healthCenterAddress = healthCenterAddress;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(String validUntil) {
        this.validUntil = validUntil;
    }

    @Override
    public String toString() {
        return "SisResult{" +
                "queryId=" + queryId +
                ", inDocType='" + inDocType + '\'' +
                ", inDocNumber='" + inDocNumber + '\'' +
                ", fullName='" + fullName + '\'' +
                ", documentNumber='" + documentNumber + '\'' +
                ", affiliationNumber='" + affiliationNumber + '\'' +
                ", insuranceType='" + insuranceType + '\'' +
                ", insuredType='" + insuredType + '\'' +
                ", formatType='" + formatType + '\'' +
                ", enrollmentDate=" + enrollmentDate +
                ", benefitPlan='" + benefitPlan + '\'' +
                ", healthCenter='" + healthCenter + '\'' +
                ", healthCenterAddress='" + healthCenterAddress + '\'' +
                ", status='" + status + '\'' +
                ", validUntil='" + validUntil + '\'' +
                '}';
    }
}
