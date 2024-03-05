package com.affirm.common.model.transactional;

import com.affirm.common.model.catalog.FraudFlag;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.util.Date;
import java.util.Locale;

public class LoanApplicationFraudFlag {
    private Integer id;
    private LoanApplication loanApplication;
    private FraudFlag fraudFlag;
    private String firstName;
    private String lastName;
    private String department;
    private Integer rejectionReasonId;
    private Integer flagId;
    private Date registeredDate;
    private String commentary;

    private Integer fraudFlagStatusId;

    private String reporter;
    private String reviewer;

    public void fillFromDb(JSONObject json, CatalogService catalogService, Locale locale) throws Exception {
        loanApplication = new LoanApplication();

        if(JsonUtil.getJsonObjectFromJson(json,"loan_application", null) != null)
        {
            loanApplication.fillFromDb(JsonUtil.getJsonObjectFromJson(json,"loan_application", null), catalogService, locale);
        }

        fraudFlag = catalogService.getFraudFlag(JsonUtil.getIntFromJson(json, "fraud_flag_id", null));

        setFirstName(JsonUtil.getStringFromJson(json, "first_name", null));
        setLastName(JsonUtil.getStringFromJson(json, "last_name", null));
        setDepartment(JsonUtil.getStringFromJson(json, "department", null));
        setId(JsonUtil.getIntFromJson(json, "loan_application_fraud_flag_id", null));
        setRejectionReasonId(JsonUtil.getIntFromJson(json, "fraud_flag_rejection_reason_id", null));
        setFlagId(JsonUtil.getIntFromJson(json, "fraud_flag_id", null));
        setRegisteredDate(JsonUtil.getPostgresDateFromJson(json, "registered_date", null));
        setCommentary(JsonUtil.getStringFromJson(json, "commentary", null));
        setReporter(JsonUtil.getStringFromJson(json, "reporter", null));
        setReviewer(JsonUtil.getStringFromJson(json, "reviewer", null));
        setFraudFlagStatusId(JsonUtil.getIntFromJson(json, "fraud_flag_status_id", null));
    }

    public FraudFlag getFraudFlag() {
        return fraudFlag;
    }

    public void setFraudFlag(FraudFlag fraudFlag) {
        this.fraudFlag = fraudFlag;
    }

    public Date getRegisteredDate() {
        return registeredDate;
    }

    public void setRegisteredDate(Date registeredDate) {
        this.registeredDate = registeredDate;
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

    public String getCommentary() {
        return commentary;
    }

    public void setCommentary(String commentary) {
        this.commentary = commentary;
    }

    public String getReporter() {
        return reporter;
    }

    public void setReporter(String reporter) {
        this.reporter = reporter;
    }

    public String getReviewer() {
        return reviewer;
    }

    public void setReviewer(String reviewer) {
        this.reviewer = reviewer;
    }

    public Integer getFraudFlagStatusId() {
        return fraudFlagStatusId;
    }

    public void setFraudFlagStatusId(Integer fraudFlagStatusId) {
        this.fraudFlagStatusId = fraudFlagStatusId;
    }
}
