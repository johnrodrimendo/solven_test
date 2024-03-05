package com.affirm.common.model.transactional;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by john on 29/09/16.
 */
public class RedamResult implements Serializable {

    public static final NumberFormat DECIMAL_FORMAT = NumberFormat.getInstance(Locale.US);

    public static final int DNI_TYPE = 1;
    public static final int CE_TYPE = 2;

    private Integer queryId;
    private Integer inDocType;
    private String inDocNumber;
    private String documentNumber;
    private String judicialDistrict;
    private String court;
    private String secretary;
    private String registry;
    private Double monthlyPension;
    private Integer installments;
    private Double amountDue;
    private Double interest;

    public void fillFromDb(JSONObject json) {
        setQueryId(JsonUtil.getIntFromJson(json, "query_id", null));
        setInDocType(JsonUtil.getIntFromJson(json, "in_document_type", null));
        setInDocNumber(JsonUtil.getStringFromJson(json, "in_document_number", null));
        setDocumentNumber(JsonUtil.getStringFromJson(json, "document_number", null));
        setJudicialDistrict(JsonUtil.getStringFromJson(json, "judicial_district", null));
        setCourt(JsonUtil.getStringFromJson(json, "court", null));
        setSecretary(JsonUtil.getStringFromJson(json, "secretary", null));
        setRegistry(JsonUtil.getStringFromJson(json, "registry", null));
        setMonthlyPension(JsonUtil.getDoubleFromJson(json, "monthly_pension", null));
        setInstallments(JsonUtil.getIntFromJson(json, "installments", null));
        setAmountDue(JsonUtil.getDoubleFromJson(json, "amount_due", null));
        setInterest(JsonUtil.getDoubleFromJson(json, "interest", null));
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

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getJudicialDistrict() {
        return judicialDistrict;
    }

    public void setJudicialDistrict(String judicialDistrict) {
        this.judicialDistrict = judicialDistrict;
    }

    public String getCourt() {
        return court;
    }

    public void setCourt(String court) {
        this.court = court;
    }

    public String getSecretary() {
        return secretary;
    }

    public void setSecretary(String secretary) {
        this.secretary = secretary;
    }

    public String getRegistry() {
        return registry;
    }

    public void setRegistry(String registry) {
        this.registry = registry;
    }

    public Double getMonthlyPension() {
        return monthlyPension;
    }

    public void setMonthlyPension(Double monthlyPension) {
        this.monthlyPension = monthlyPension;
    }

    public Integer getInstallments() {
        return installments;
    }

    public void setInstallments(Integer installments) {
        this.installments = installments;
    }

    public Double getAmountDue() {
        return amountDue;
    }

    public void setAmountDue(Double amountDue) {
        this.amountDue = amountDue;
    }

    public Double getInterest() {
        return interest;
    }

    public void setInterest(Double interest) {
        this.interest = interest;
    }

    @Override
    public String toString() {
        return "RedamResult{" +
                "queryId=" + queryId +
                ", inDocType=" + inDocType +
                ", inDocNumber='" + inDocNumber + '\'' +
                ", documentNumber='" + documentNumber + '\'' +
                ", judicialDistrict='" + judicialDistrict + '\'' +
                ", court='" + court + '\'' +
                ", secretary='" + secretary + '\'' +
                ", registry='" + registry + '\'' +
                ", monthlyPension=" + monthlyPension +
                ", installments=" + installments +
                ", amountDue=" + amountDue +
                ", interest=" + interest +
                '}';
    }
}
