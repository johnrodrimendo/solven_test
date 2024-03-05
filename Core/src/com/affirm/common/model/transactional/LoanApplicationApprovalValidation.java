package com.affirm.common.model.transactional;

import com.affirm.common.model.catalog.ApprovalValidation;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.util.Date;
import java.util.Locale;

public class LoanApplicationApprovalValidation {

    public static final char CUSTOM_STATUS_MANUAL_REVISION = 'M';
    public static final char CUSTOM_STATUS_MANUAL_REVISION_WAITING_APPROVAL = 'W';
    public static final char CUSTOM_STATUS_NO_PARAM = 'N';

    private Integer approvalValidationId;
    private Date registerDate;
    private Boolean approved;
    private String message;
    private Character status;

    public LoanApplicationApprovalValidation() {
    }

    public LoanApplicationApprovalValidation(Integer approvalValidationId, Date registerDate, Boolean approved, String message, Character status) {
        this.approvalValidationId = approvalValidationId;
        this.registerDate = registerDate;
        this.approved = approved;
        this.message = message;
        this.status = status;
    }

    public Integer getApprovalValidationId() {
        return approvalValidationId;
    }

    public void setApprovalValidationId(Integer approvalValidationId) {
        this.approvalValidationId = approvalValidationId;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public Boolean getApproved() {
        return approved;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Character getStatus() {
        return status;
    }

    public void setStatus(Character status) {
        this.status = status;
    }
}
