package com.affirm.common.model.catalog;

import com.affirm.common.util.JsonUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by jrodriguez on 25/07/16.
 */
public class CreditRejectionReason implements Serializable {
    public static final int POSSIBLE_FRAUD = 2;
    public static final int USER_CANCELATION = 5;
    public static final int BLACK_LIST = 35;
    public static final int BASE_HAS_CHANGED = 72;
    public static final int PREVIOUSLY_DISBURSED = 68;

    public static final String ONLY_VERIFICATION_LIST_TYPE = "OVLT";
    public static final String REGULAR_LIST_TYPE = "RLT";

    private Integer id;
    private String reason;
    private String reasonMail;
    private Boolean active;
    private Boolean disbursement;
    private Boolean extranetEntity;
    private Boolean verificationEntity;
    private JSONArray jsonArrayExclusiveRejectionReason;
    private Integer expirationDays;

    public void fillFromDb(JSONObject json) throws Exception {
        setId(JsonUtil.getIntFromJson(json, "credit_rejection_reason_id", null));
        setReason(JsonUtil.getStringFromJson(json, "credit_rejection_reason", null));
        setReasonMail(JsonUtil.getStringFromJson(json, "credit_rejection_reason_mail", null));
        setActive(JsonUtil.getBooleanFromJson(json, "is_active", null));
        setDisbursement(JsonUtil.getBooleanFromJson(json, "disbursement", null));
        setExtranetEntity(JsonUtil.getBooleanFromJson(json, "extranet_entity", false));
        setVerificationEntity(JsonUtil.getBooleanFromJson(json, "verification_entity", false));
        setJsonArrayExclusiveRejectionReason(JsonUtil.getJsonArrayFromJson(json, "exclusive_rejection_reason", null));
        setExpirationDays(JsonUtil.getIntFromJson(json, "expiration_days", null));
    }

    public boolean exclusiveForEntityProduct(int entityProductParamId, boolean toAdd) {
        if (jsonArrayExclusiveRejectionReason == null)
            return false;

        for (int i = 0; i < jsonArrayExclusiveRejectionReason.length(); i++) {
            JSONObject jsonObj = (JSONObject) jsonArrayExclusiveRejectionReason.get(i);
            Integer jsonEntityProductParamId = JsonUtil.getIntFromJson(jsonObj, "entity_product_param_id", null);
            Boolean jsonToAdd = JsonUtil.getBooleanFromJson(jsonObj, "to_add", true);
            if (jsonEntityProductParamId != null && jsonEntityProductParamId.equals(entityProductParamId) && jsonToAdd == toAdd) {
                return true;
            }
        }
        return false;
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

    public String getReasonMail() {
        return reasonMail;
    }

    public void setReasonMail(String reasonMail) {
        this.reasonMail = reasonMail;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean getDisbursement() {
        return disbursement;
    }

    public void setDisbursement(Boolean disbursement) {
        this.disbursement = disbursement;
    }

    public Boolean getExtranetEntity() {
        return extranetEntity;
    }

    public void setExtranetEntity(Boolean extranetEntity) {
        this.extranetEntity = extranetEntity;
    }

    public Boolean getVerificationEntity() {
        return verificationEntity;
    }

    public void setVerificationEntity(Boolean verificationEntity) {
        this.verificationEntity = verificationEntity;
    }

    public JSONArray getJsonArrayExclusiveRejectionReason() {
        return jsonArrayExclusiveRejectionReason;
    }

    public void setJsonArrayExclusiveRejectionReason(JSONArray jsonArrayExclusiveRejectionReason) {
        this.jsonArrayExclusiveRejectionReason = jsonArrayExclusiveRejectionReason;
    }

    public Integer getExpirationDays() {
        return expirationDays;
    }

    public void setExpirationDays(Integer expirationDays) {
        this.expirationDays = expirationDays;
    }
}
