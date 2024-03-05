package com.affirm.common.model.transactional;

import com.affirm.common.util.JsonUtil;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by dev5 on 14/02/18.
 */
public class CorrectionFlow {

    public static final int BACK_ACCOUNT = 3;
    public static final int CREDIT_AMOUNT = 4;
    public static final int DEBTS = 3;
    public static final int DOCUMENTATION = 3;

    private Integer id;
    private String flow;
    private JSONArray flowArray;

    public void fillFromDb(JSONObject json) {
        setId(JsonUtil.getIntFromJson(json, "correction_flow_id", null));
        setFlow(JsonUtil.getStringFromJson(json, "correction_flow", null));
        setFlowArray(JsonUtil.getJsonArrayFromJson(json, "js_process_question_id", null));
    }

    public Integer getId() { return id; }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFlow() {
        return flow;
    }

    public void setFlow(String flow) {
        this.flow = flow;
    }

    public JSONArray getFlowArray() {
        return flowArray;
    }

    public void setFlowArray(JSONArray flowArray) {
        this.flowArray = flowArray;
    }
}
