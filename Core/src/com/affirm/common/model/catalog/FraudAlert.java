package com.affirm.common.model.catalog;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

public class FraudAlert {
    private Integer fraudAlertId;
    private String fraudAlertElemnt;
    private String fraudAlertCode;
    private String fraudAlertDescription;
    private Boolean supervisionRequired;
    private Boolean relatedRequired;
    private Integer relatedFraudAlertId;

    public static Integer MISSING_DOCUMENTATION = 90;
    public static Integer VERIFICATION_EMAIL_NOT_OPENED = 102;
    public static int MATI_VERIFICATION = 108;

    public static Integer[] RUC_RELATED = new Integer[]{4, 5, 85, 6, 7, 13, 14};
    public static Integer[] DNI_RELATED = new Integer[]{1, 2, 3, 16, 26, 27, 28};
    public static Integer[] CCI_RELATED = new Integer[]{41};


    public void fillFromDb(JSONObject json) {
        setFraudAlertCode(JsonUtil.getStringFromJson(json, "fraud_alert_code", null));
        setFraudAlertDescription(JsonUtil.getStringFromJson(json, "fraud_alert_description",null));
        setFraudAlertElemnt(JsonUtil.getStringFromJson(json, "fraud_alert_element", null));
        setFraudAlertId(JsonUtil.getIntFromJson(json, "fraud_alert_id", null));
        setSupervisionRequired(JsonUtil.getBooleanFromJson(json, "supervision_required", null));
        setRelatedRequired(JsonUtil.getBooleanFromJson(json, "related_required", null));
        setRelatedFraudAlertId(JsonUtil.getIntFromJson(json, "related_fraud_alert_id", null));
    }

    public Integer getFraudAlertId() {
        return fraudAlertId;
    }

    public void setFraudAlertId(Integer fraudAlertId) {
        this.fraudAlertId = fraudAlertId;
    }

    public String getFraudAlertElemnt() {
        return fraudAlertElemnt;
    }

    public void setFraudAlertElemnt(String fraudAlertElemnt) {
        this.fraudAlertElemnt = fraudAlertElemnt;
    }

    public String getFraudAlertCode() {
        return fraudAlertCode;
    }

    public void setFraudAlertCode(String fraudAlertCode) {
        this.fraudAlertCode = fraudAlertCode;
    }

    public String getFraudAlertDescription() {
        return fraudAlertDescription;
    }

    public void setFraudAlertDescription(String fraudAlertDescription) {
        this.fraudAlertDescription = fraudAlertDescription;
    }

    public Boolean getSupervisionRequired() {
        return supervisionRequired;
    }

    public void setSupervisionRequired(Boolean supervisionRequired) {
        this.supervisionRequired = supervisionRequired;
    }

    public Boolean getRelatedRequired() {
        return relatedRequired;
    }

    public void setRelatedRequired(Boolean relatedRequired) {
        this.relatedRequired = relatedRequired;
    }

    public Integer getRelatedFraudAlertId() {
        return relatedFraudAlertId;
    }

    public void setRelatedFraudAlertId(Integer relatedFraudAlertId) {
        this.relatedFraudAlertId = relatedFraudAlertId;
    }
}
