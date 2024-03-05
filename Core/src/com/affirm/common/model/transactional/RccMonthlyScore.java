package com.affirm.common.model.transactional;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

public class RccMonthlyScore {

    private Integer monthId;
    private Integer score;

    public void fillFromDb(JSONObject json) throws Exception {
        setMonthId(JsonUtil.getIntFromJson(json, "month_id", null));
        setScore(JsonUtil.getIntFromJson(json, "score", null));
    }

    public Integer getMonthId() {
        return monthId;
    }

    public void setMonthId(Integer monthId) {
        this.monthId = monthId;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }
}
