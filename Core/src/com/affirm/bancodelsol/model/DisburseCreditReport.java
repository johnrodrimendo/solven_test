package com.affirm.bancodelsol.model;

import com.affirm.common.model.catalog.CreditSubStatus;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.util.Date;
import java.util.Locale;

public class DisburseCreditReport {

    private Date loanRegisterDate;
    private Date creditOriginatedDate;
    private Integer creditId;
    private String creditCode;
    private String clientName;
    private String clientFirstSurname;
    private String clientLastSurname;
    private String documentNumber;
    private Double amount;
    private Double loanCapital;
    private Double rate;
    private Integer installments;
    private String cciCode;
    private String organizerName;
    private String organizerFirstSurname;
    private String organizerLastSurname;
    private String productorName;
    private String productorFirstSurname;
    private String productorLastSurname;
    private String productorUsername;
    private JSONObject entityCustomData;
    private CreditSubStatus creditSubStatus;

    public void fillFromDb(JSONObject json, CatalogService catalog, Locale locale) {
        setLoanRegisterDate(JsonUtil.getPostgresDateFromJson(json, "la_register_date", null));
        setCreditOriginatedDate(JsonUtil.getPostgresDateFromJson(json, "originated", null));
        setCreditId(JsonUtil.getIntFromJson(json, "credit_id", null));
        setCreditCode(JsonUtil.getStringFromJson(json, "credit_code", null));
        setClientName(JsonUtil.getStringFromJson(json, "client_name", null));
        setClientFirstSurname(JsonUtil.getStringFromJson(json, "client_first_surname", null));
        setClientLastSurname(JsonUtil.getStringFromJson(json, "client_last_surname", null));
        setDocumentNumber(JsonUtil.getStringFromJson(json, "document_number", null));
        setAmount(JsonUtil.getDoubleFromJson(json, "amount", null));
        setLoanCapital(JsonUtil.getDoubleFromJson(json, "loan_capital", null));
        setRate(JsonUtil.getDoubleFromJson(json, "nominal_annual_rate", null));
        setInstallments(JsonUtil.getIntFromJson(json, "installments", null));
        setCciCode(JsonUtil.getStringFromJson(json, "cci_code", null));
        setOrganizerName(JsonUtil.getStringFromJson(json, "organizer_name", null));
        setOrganizerFirstSurname(JsonUtil.getStringFromJson(json, "organizer_first_surname", null));
        setOrganizerLastSurname(JsonUtil.getStringFromJson(json, "organizer_last_surname", null));
        setProductorName(JsonUtil.getStringFromJson(json, "productor_name", null));
        setProductorFirstSurname(JsonUtil.getStringFromJson(json, "productor_first_surname", null));
        setProductorLastSurname(JsonUtil.getStringFromJson(json, "productor_last_surname", null));
        setProductorUsername(JsonUtil.getStringFromJson(json, "productor_user", null));
        Integer creditSubStatusId = JsonUtil.getIntFromJson(json, "status", null);
        setEntityCustomData(JsonUtil.getJsonObjectFromJson(json, "entity_custom_data", null));
        if (creditSubStatusId != null) {
            setCreditSubStatus(catalog.getCreditSubStatus(locale, creditSubStatusId));
        }
    }

    public String getClientFullName() {
        return String.format("%s %s %s", clientName != null ? clientName : "", clientFirstSurname != null ? clientFirstSurname : "", clientLastSurname != null ? clientLastSurname : "");
    }

    public String getOrganizerFullName() {
        return String.format("%s %s %s", organizerName != null ? organizerName : "", organizerFirstSurname != null ? organizerFirstSurname : "", organizerLastSurname != null ? organizerLastSurname : "");
    }

    public String getProductorFullName() {
        return String.format("%s %s %s", productorName != null ? productorName : "", productorFirstSurname != null ? productorFirstSurname : "", productorLastSurname != null ? productorLastSurname : "");
    }

    public Date getLoanRegisterDate() {
        return loanRegisterDate;
    }

    public void setLoanRegisterDate(Date loanRegisterDate) {
        this.loanRegisterDate = loanRegisterDate;
    }

    public Integer getCreditId() {
        return creditId;
    }

    public void setCreditId(Integer creditId) {
        this.creditId = creditId;
    }

    public String getCreditCode() {
        return creditCode;
    }

    public void setCreditCode(String creditCode) {
        this.creditCode = creditCode;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientFirstSurname() {
        return clientFirstSurname;
    }

    public void setClientFirstSurname(String clientFirstSurname) {
        this.clientFirstSurname = clientFirstSurname;
    }

    public String getClientLastSurname() {
        return clientLastSurname;
    }

    public void setClientLastSurname(String clientLastSurname) {
        this.clientLastSurname = clientLastSurname;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public Integer getInstallments() {
        return installments;
    }

    public void setInstallments(Integer installments) {
        this.installments = installments;
    }

    public String getCciCode() {
        return cciCode;
    }

    public void setCciCode(String cciCode) {
        this.cciCode = cciCode;
    }

    public String getOrganizerName() {
        return organizerName;
    }

    public void setOrganizerName(String organizerName) {
        this.organizerName = organizerName;
    }

    public String getOrganizerFirstSurname() {
        return organizerFirstSurname;
    }

    public void setOrganizerFirstSurname(String organizerFirstSurname) { this.organizerFirstSurname = organizerFirstSurname; }

    public String getOrganizerLastSurname() {
        return organizerLastSurname;
    }

    public void setOrganizerLastSurname(String organizerLastSurname) { this.organizerLastSurname = organizerLastSurname; }

    public String getProductorName() {
        return productorName;
    }

    public void setProductorName(String productorName) {
        this.productorName = productorName;
    }

    public String getProductorFirstSurname() {
        return productorFirstSurname;
    }

    public void setProductorFirstSurname(String productorFirstSurname) { this.productorFirstSurname = productorFirstSurname; }

    public String getProductorLastSurname() {
        return productorLastSurname;
    }

    public void setProductorLastSurname(String productorLastSurname) { this.productorLastSurname = productorLastSurname; }

    public String getProductorUsername() {
        return productorUsername;
    }

    public void setProductorUsername(String productorUsername) {
        this.productorUsername = productorUsername;
    }

    public CreditSubStatus getCreditSubStatus() {
        return creditSubStatus;
    }

    public void setCreditSubStatus(CreditSubStatus creditSubStatus) {
        this.creditSubStatus = creditSubStatus;
    }

    public Double getLoanCapital() { return loanCapital; }

    public void setLoanCapital(Double loanCapital) { this.loanCapital = loanCapital; }

    public JSONObject getEntityCustomData() { return entityCustomData; }

    public void setEntityCustomData(JSONObject entityCustomData) { this.entityCustomData = entityCustomData; }

    public Date getCreditOriginatedDate() { return creditOriginatedDate; }

    public void setCreditOriginatedDate(Date creditOriginatedDate) { this.creditOriginatedDate = creditOriginatedDate; }
}
