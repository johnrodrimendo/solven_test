package com.affirm.common.model.catalog;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

/**
 * Created by dev5 on 09/10/17.
 */
public class DisbursementType {

    private Integer disbursementTypeId;
    private String disbursementType;

    public void fillFromDb(JSONObject json) {
        setDisbursementTypeId(JsonUtil.getIntFromJson(json, "disbursement_type_id", null));
        setDisbursementType(JsonUtil.getStringFromJson(json, "disbursement_type", null));
    }

    public Integer getDisbursementTypeId() {
        return disbursementTypeId;
    }

    public void setDisbursementTypeId(Integer disbursementTypeId) {
        this.disbursementTypeId = disbursementTypeId;
    }

    public String getDisbursementType() {
        return disbursementType;
    }

    public void setDisbursementType(String disbursementType) {
        this.disbursementType = disbursementType;
    }
}
