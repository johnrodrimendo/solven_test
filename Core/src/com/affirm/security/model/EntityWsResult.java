package com.affirm.security.model;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

/**
 * Created by dev5 on 23/02/18.
 */
public class EntityWsResult {

    private Integer entityWsResultId;
    private Integer loanApplicationId;
    private Integer entityWsId;
    private JSONObject result;

    public void fillFromDb(JSONObject json) {
        setEntityWsResultId(JsonUtil.getIntFromJson(json, "entity_ws_result_id", null));
        setLoanApplicationId(JsonUtil.getIntFromJson(json, "loan_application_id", null));
        setEntityWsId(JsonUtil.getIntFromJson(json, "entity_ws_id", null));
        setResult(JsonUtil.getJsonObjectFromJson(json, "result", null));
    }

    public Integer getEntityWsResultId() {
        return entityWsResultId;
    }

    public void setEntityWsResultId(Integer entityWsResultId) {
        this.entityWsResultId = entityWsResultId;
    }

    public Integer getLoanApplicationId() {
        return loanApplicationId;
    }

    public void setLoanApplicationId(Integer loanApplicationId) {
        this.loanApplicationId = loanApplicationId;
    }

    public Integer getEntityWsId() {
        return entityWsId;
    }

    public void setEntityWsId(Integer entityWsId) {
        this.entityWsId = entityWsId;
    }

    public JSONObject getResult() {
        return result;
    }

    public void setResult(JSONObject result) {
        this.result = result;
    }
}
