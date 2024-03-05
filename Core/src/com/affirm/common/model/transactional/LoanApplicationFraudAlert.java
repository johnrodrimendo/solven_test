package com.affirm.common.model.transactional;

import com.affirm.common.model.catalog.FraudAlert;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.util.Date;
import java.util.Locale;

public class LoanApplicationFraudAlert {
    private Integer id;
    private LoanApplication loanApplication;
    private LoanApplication relatedLoanApplication;
    private FraudAlert fraudAlert;
    private String firstName;
    private String lastName;
    private String department;
    private Integer rejectionReasonId;
    private Integer flagId;
    private String operatorName;
    private String operatorLastName;
    private String dni;
    private Date registeredDate;
    private Boolean active;

    public void fillFromDb(JSONObject json, CatalogService catalogService, Locale locale) throws Exception {
        loanApplication = new LoanApplication();
        relatedLoanApplication = new LoanApplication();

        if (JsonUtil.getJsonObjectFromJson(json, "loan_application", null) != null) {
            loanApplication.fillFromDb(JsonUtil.getJsonObjectFromJson(json, "loan_application", null), catalogService, locale);
        }

        if (JsonUtil.getJsonObjectFromJson(json, "related_loan_application", null) != null) {
            relatedLoanApplication.fillFromDb(JsonUtil.getJsonObjectFromJson(json, "related_loan_application", null), catalogService, locale);
        }

        fraudAlert = catalogService.getFraudAlert(JsonUtil.getIntFromJson(json, "fraud_alert_id", null));

        setFirstName(JsonUtil.getStringFromJson(json, "first_name", null));
        setLastName(JsonUtil.getStringFromJson(json, "last_name", null));
        setDepartment(JsonUtil.getStringFromJson(json, "department", null));
        setId(JsonUtil.getIntFromJson(json, "loan_application_fraud_alert_id", null));
        setRejectionReasonId(JsonUtil.getIntFromJson(json, "fraud_alert_rejection_reason_id", null));
        setFlagId(JsonUtil.getIntFromJson(json, "fraud_flag_id", null));

        setOperatorName(JsonUtil.getStringFromJson(json, "operator_name", null));
        setOperatorLastName(JsonUtil.getStringFromJson(json, "operator_last_name", null));
        setRegisteredDate(JsonUtil.getPostgresDateFromJson(json, "registered_date", null));
        setDni(JsonUtil.getStringFromJson(json, "document_number", null));
        setActive(JsonUtil.getBooleanFromJson(json, "is_active", null));
    }

    public LoanApplication getRelatedLoanApplication() {
        return relatedLoanApplication;
    }

    public void setRelatedLoanApplication(LoanApplication relatedLoanApplication) {
        this.relatedLoanApplication = relatedLoanApplication;
    }

    public FraudAlert getFraudAlert() {
        return fraudAlert;
    }

    public void setFraudAlert(FraudAlert fraudAlert) {
        this.fraudAlert = fraudAlert;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LoanApplication getLoanApplication() {
        return loanApplication;
    }

    public void setLoanApplication(LoanApplication loanApplication) {
        this.loanApplication = loanApplication;
    }

    public Integer getRejectionReasonId() {
        return rejectionReasonId;
    }

    public void setRejectionReasonId(Integer rejectionReasonId) {
        this.rejectionReasonId = rejectionReasonId;
    }

    public Integer getFlagId() {
        return flagId;
    }

    public void setFlagId(Integer flagId) {
        this.flagId = flagId;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getOperatorLastName() {
        return operatorLastName;
    }

    public void setOperatorLastName(String operatorLastName) {
        this.operatorLastName = operatorLastName;
    }

    public Date getRegisteredDate() {
        return registeredDate;
    }

    public void setRegisteredDate(Date registeredDate) {
        this.registeredDate = registeredDate;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
