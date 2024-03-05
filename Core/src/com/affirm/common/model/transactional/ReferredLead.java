package com.affirm.common.model.transactional;

import com.affirm.common.model.catalog.Entity;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

public class ReferredLead implements Serializable {
    private Integer Id;
    private Integer loanApplicationId;
    private Entity entity;
    private Boolean isDisbursed;
    private Date referredDate;
    private Date disbursementDate;

    public void fillFromDb(JSONObject json, CatalogService catalog) throws Exception {
        setId(JsonUtil.getIntFromJson(json, "referred_lead_id", null));
        if (JsonUtil.getIntFromJson(json, "entity_id", null) != null)
            setEntity(catalog.getEntity(JsonUtil.getIntFromJson(json, "entity_id", null)));
        if (JsonUtil.getIntFromJson(json, "loan_application_id", null) != null)
            setLoanApplicationId(JsonUtil.getIntFromJson(json, "loan_application_id", null));
        setIsDisbursed(JsonUtil.getBooleanFromJson(json, "is_disbursed", null));
        setDisbursementDate(JsonUtil.getPostgresDateFromJson(json, "referred_date", null));
        setDisbursementDate(JsonUtil.getPostgresDateFromJson(json, "disbursement_date", null));
    }

    public Integer getLoanApplicationId() { return loanApplicationId; }

    public void setLoanApplicationId(Integer loanApplicationId) { this.loanApplicationId = loanApplicationId; }

    public Entity getEntity() { return entity; }

    public void setEntity(Entity entity) { this.entity = entity; }

    public Boolean getIsDisbursed() { return isDisbursed; }

    public void setIsDisbursed(Boolean isDisbursed) { this.isDisbursed = isDisbursed; }

    public Integer getId() { return Id; }

    public void setId(Integer id) { Id = id; }

    public Date getReferredDate() { return referredDate; }

    public void setReferredDate(Date referredDate) { this.referredDate = referredDate; }

    public Date getDisbursementDate() { return disbursementDate; }

    public void setDisbursementDate(Date disbursementDate) { this.disbursementDate = disbursementDate; }
}
