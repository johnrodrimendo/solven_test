package com.affirm.common.model.catalog;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by jrodriguez on 25/07/16.
 */
public class ApplicationRejectionReason implements Serializable {
    public static final int INCOME_NOT_VALIDATED = 1;
    public static final int REFERENCE_NOT_VALIDATED = 2;
    public static final int USER_CANCELATION = 3;
    public static final int BANK_ACCOUNT_INVALID = 4;
    public static final int POSSIBLE_FRAUD = 5;
    public static final int CREDIGOB_NO_ORDEN_SERVICIO = 19;
    public static final int VALIDACIONES_FALLIDAS = 32;
    public static final int PRUEBA_VIDA_FALLIDA = 33;

    public static final int GEOLOCALIZACION_IP = 34;

    private Integer id;
    private String reason;
    private String reasonMail;
    private Boolean active;
    private Integer expirationDays;

    public void fillFromDb(JSONObject json) throws Exception {
        setId(JsonUtil.getIntFromJson(json, "application_rejection_reason_id", null));
        setReason(JsonUtil.getStringFromJson(json, "application_rejection_reason", null));
        setReasonMail(JsonUtil.getStringFromJson(json, "application_rejection_reason_mail", null));
        setActive(JsonUtil.getBooleanFromJson(json, "is_active", null));
        setExpirationDays(JsonUtil.getIntFromJson(json, "expiration_days", null));
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

    public Integer getExpirationDays() {
        return expirationDays;
    }

    public void setExpirationDays(Integer expirationDays) {
        this.expirationDays = expirationDays;
    }
}
