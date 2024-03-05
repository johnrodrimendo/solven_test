package com.affirm.common.model.transactional;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

public class AuditRejectionReason {
    private Integer userFileId;
    private String fileType;
    private String loanApplicationAuditRejectionReason;
    private String loanApplicationAuditRejectionComment;
    private String auditType;

    public void fillFromDb(JSONObject json) {
        setUserFileId(JsonUtil.getIntFromJson(json, "user_file_id", null));
        setFileType(JsonUtil.getStringFromJson(json, "filetype", null));
        setLoanApplicationAuditRejectionReason(JsonUtil.getStringFromJson(json, "loan_application_audit_rejection_reason", null));
        setLoanApplicationAuditRejectionComment(JsonUtil.getStringFromJson(json, "rejection_comment", null));
        setAuditType(JsonUtil.getStringFromJson(json, "loan_application_audit_type", null));
    }

    public Integer getUserFileId() {
        return userFileId;
    }

    public void setUserFileId(Integer userFileId) {
        this.userFileId = userFileId;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getLoanApplicationAuditRejectionReason() {
        return loanApplicationAuditRejectionReason;
    }

    public void setLoanApplicationAuditRejectionReason(String loanApplicationAuditRejectionReason) {
        this.loanApplicationAuditRejectionReason = loanApplicationAuditRejectionReason;
    }

    public String getLoanApplicationAuditRejectionComment() {
        return loanApplicationAuditRejectionComment;
    }

    public void setLoanApplicationAuditRejectionComment(String loanApplicationAuditRejectionComment) {
        this.loanApplicationAuditRejectionComment = loanApplicationAuditRejectionComment;
    }

    public String getAuditType() {
        return auditType;
    }

    public void setAuditType(String auditType) {
        this.auditType = auditType;
    }
}
