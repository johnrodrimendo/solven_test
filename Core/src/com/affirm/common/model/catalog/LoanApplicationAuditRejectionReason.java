/**
 *
 */
package com.affirm.common.model.catalog;

import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * @author jrodriguez
 */
public class LoanApplicationAuditRejectionReason implements Serializable {

    private Integer id;
    private String reason;
    private LoanApplicationAuditType auditType;

    public void fillFromDb(JSONObject json, CatalogService catalog) {
        setId(JsonUtil.getIntFromJson(json, "loan_application_audit_rejection_reason_id", null));
        setReason(JsonUtil.getStringFromJson(json, "loan_application_audit_rejection_reason", null));
        if(JsonUtil.getIntFromJson(json, "loan_application_audit_id_type", null) != null)
            setAuditType(catalog.getLoanApplicationAuditType(JsonUtil.getIntFromJson(json, "loan_application_audit_id_type", null)));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LoanApplicationAuditType getAuditType() {
        return auditType;
    }

    public void setAuditType(LoanApplicationAuditType auditType) {
        this.auditType = auditType;
    }
}
