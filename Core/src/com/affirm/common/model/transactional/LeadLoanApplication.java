package com.affirm.common.model.transactional;

import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class LeadLoanApplication implements Serializable {

    private Integer loanApplicationLeadId;
    private Date registeredDate;
    private String email;
    private String personName;
    private String personFirstLastName;
    private String personSecondLastName;
    private Integer documentTypeId;
    private String docNumber;
    private Date dob;
    private String phone;
    private Integer productId;
    private Integer activityTypeId;
    private Double amount;

    private Date month;
    private Integer leads;
    private Double commissionAmount;

    public void fillFromDb(JSONObject json, CatalogService catalog, Locale locale) throws Exception {
        setLoanApplicationLeadId(JsonUtil.getIntFromJson(json, "loan_application_lead_id", null));
        setRegisteredDate(JsonUtil.getPostgresDateFromJson(json, "register_date", null));
        setEmail(JsonUtil.getStringFromJson(json, "email", null));
        setPersonName(JsonUtil.getStringFromJson(json, "person_name", null));
        setPersonFirstLastName(JsonUtil.getStringFromJson(json, "first_surname", null));
        setPersonSecondLastName(JsonUtil.getStringFromJson(json, "last_surname", null));
        setDocumentTypeId(JsonUtil.getIntFromJson(json, "document_type_id", null));
        setDocNumber(JsonUtil.getStringFromJson(json, "document_number", null));
        setDob(JsonUtil.getPostgresDateFromJson(json, "birthday", null));
        setPhone(JsonUtil.getStringFromJson(json, "phone_number", null));
        setProductId(JsonUtil.getIntFromJson(json, "product_id", null));
        setActivityTypeId(JsonUtil.getIntFromJson(json, "activity_type_id", null));
        setAmount(JsonUtil.getDoubleFromJson(json, "amount", null));

        setMonth(JsonUtil.getPostgresDateFromJson(json, "month", null));
        setLeads(JsonUtil.getIntFromJson(json, "leads", null));
        setCommissionAmount(JsonUtil.getDoubleFromJson(json, "commission_amount", null));
    }

    public String getMonthYear() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getMonth());
        return (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR);
    }

    public Date getRegisteredDate() {
        return registeredDate;
    }

    public void setRegisteredDate(Date registeredDate) {
        this.registeredDate = registeredDate;
    }

    public Integer getLoanApplicationLeadId() {
        return loanApplicationLeadId;
    }

    public void setLoanApplicationLeadId(Integer loanApplicationLeadId) {
        this.loanApplicationLeadId = loanApplicationLeadId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getActivityTypeId() {
        return activityTypeId;
    }

    public void setActivityTypeId(Integer activityTypeId) {
        this.activityTypeId = activityTypeId;
    }

    public Integer getDocumentTypeId() {
        return documentTypeId;
    }

    public void setDocumentTypeId(Integer documentTypeId) {
        this.documentTypeId = documentTypeId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getPersonFirstLastName() {
        return personFirstLastName;
    }

    public void setPersonFirstLastName(String personFirstLastName) {
        this.personFirstLastName = personFirstLastName;
    }

    public String getPersonSecondLastName() {
        return personSecondLastName;
    }

    public void setPersonSecondLastName(String personSecondLastName) {
        this.personSecondLastName = personSecondLastName;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDocNumber() {
        return docNumber;
    }

    public void setDocNumber(String docNumber) {
        this.docNumber = docNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getMonth() {
        return month;
    }

    public void setMonth(Date month) {
        this.month = month;
    }

    public Integer getLeads() {
        return leads;
    }

    public void setLeads(Integer leads) {
        this.leads = leads;
    }

    public Double getCommissionAmount() {
        return commissionAmount;
    }

    public void setCommissionAmount(Double commissionAmount) {
        this.commissionAmount = commissionAmount;
    }

}