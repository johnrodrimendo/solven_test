package com.affirm.common.model;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

public class FunnelStep {

    public final static int REGISTERED = 1;
    public final static int PRE_EVALUATION_APPROVED = 2;
    public final static int PIN_VALIDATED = 3;
    public final static int APPROVED_VALIDATION = 4;
    public final static int REQUEST_COMPLETE = 5;
    public final static int REQUEST_WITH_OFFER = 6;
    public final static int ACCEPTED_OFFER = 7;
    public final static int VALIDATION = 8;
    public final static int SIGNATURE = 9;
    public final static int VERIFICATION = 10;
    public final static int APPROBATION = 11;
    public final static int DISBURSEMENT = 12;
    public final static int DISBURSED = 13;
    public final static int HOUSING_ADDRESS = 14;
    public final static int REQUEST_FINALIZED = 15;
    public final static int LEAD_REERRED = 16;
    public final static int COMMITMENT_GENERATED = 17;
    public final static int COMMITMENT_PAID = 18;
    public final static int COMMITMENT_PENDING_CONFIRMATION = 19;
    public final static int COMMITMENT_PAYMENT_INFORMED = 20;
    public final static int PRE_LOAN_APPLICATION_REGISTER = 21;
    public final static int REGISTERED_ALFIN = 22;


    private Integer id;
    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void fillFromDb(JSONObject json) {
        setId(JsonUtil.getIntFromJson(json, "id", null));
        setName(JsonUtil.getStringFromJson(json, "name", null));
    }

}
