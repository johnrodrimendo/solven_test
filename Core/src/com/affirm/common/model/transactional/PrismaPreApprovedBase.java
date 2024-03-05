package com.affirm.common.model.transactional;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;

public class PrismaPreApprovedBase implements Serializable {

    private String documentNumber;
    private String documentType;
    private String names;
    private String lastName;
    private Integer maxInstallments;
    private Integer maxAmount;
    private Double tea;

    public void fillFromDb(JSONObject json) {
        setDocumentNumber(JsonUtil.getStringFromJson(json, "document_number", null));
        setDocumentType(JsonUtil.getStringFromJson(json, "document_type", null));
        setNames(JsonUtil.getStringFromJson(json, "names", null));
        setLastName(JsonUtil.getStringFromJson(json, "last_name", null));
        setMaxInstallments(JsonUtil.getIntFromJson(json, "max_installments", null));
        setMaxAmount(JsonUtil.getIntFromJson(json, "max_amount", null));
        setTea(JsonUtil.getDoubleFromJson(json, "tea", null));
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getNames() {
        return names;
    }

    public void setNames(String names) {
        this.names = names;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Integer getMaxInstallments() {
        return maxInstallments;
    }

    public void setMaxInstallments(Integer maxInstallments) {
        this.maxInstallments = maxInstallments;
    }

    public Integer getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(Integer maxAmount) {
        this.maxAmount = maxAmount;
    }

    public Double getTea() {
        return tea;
    }

    public void setTea(Double tea) {
        this.tea = tea;
    }
}
