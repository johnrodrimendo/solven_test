package com.affirm.common.model.transactional;

import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

/**
 * Created by dev5 on 14/02/18.
 */
public class ReturningReason {

    private Integer id;
    private String reason;
    private CorrectionFlow correctionFlow;

    public void fillFromDb(JSONObject json, CatalogService catalog) {
        setId(JsonUtil.getIntFromJson(json, "returning_reason_id", null));
        setReason(JsonUtil.getStringFromJson(json, "returning_reason", null));
        setCorrectionFlow(catalog.getCorrectionFlowsById(JsonUtil.getIntFromJson(json, "correction_flow_id", null)));
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

    public CorrectionFlow getCorrectionFlow() {
        return correctionFlow;
    }

    public void setCorrectionFlow(CorrectionFlow correctionFlow) {
        this.correctionFlow = correctionFlow;
    }
}
