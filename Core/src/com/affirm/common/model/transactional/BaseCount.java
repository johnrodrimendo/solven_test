package com.affirm.common.model.transactional;

import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.util.Locale;

public class BaseCount {

    private Integer totalCountBase;
    private Integer totalCountBaseToSend;
    private Integer totalCountActive;
    private Integer totalCountActiveToSend;
    private Integer totalCountNotActive;
    private Integer totalCountNotActiveToSend;

    public void fillFromDb(JSONObject json, CatalogService catalog, Locale locale) throws Exception {

        setTotalCountBase(JsonUtil.getIntFromJson(json, "count_total_base", null));
        setTotalCountBaseToSend(JsonUtil.getIntFromJson(json, "count_total_base_to_send", null));
        setTotalCountActive(JsonUtil.getIntFromJson(json, "count_active", null));
        setTotalCountActiveToSend(JsonUtil.getIntFromJson(json, "count_active_to_send", null));
        setTotalCountNotActive(JsonUtil.getIntFromJson(json, "count_not_active", null));
        setTotalCountNotActiveToSend(JsonUtil.getIntFromJson(json, "count_not_active_to_send", null));
    }

    public Integer getTotalCountBase() {
        return totalCountBase;
    }

    public void setTotalCountBase(Integer totalCountBase) {
        this.totalCountBase = totalCountBase;
    }

    public Integer getTotalCountBaseToSend() {
        return totalCountBaseToSend;
    }

    public void setTotalCountBaseToSend(Integer totalCountBaseToSend) {
        this.totalCountBaseToSend = totalCountBaseToSend;
    }

    public Integer getTotalCountActive() {
        return totalCountActive;
    }

    public void setTotalCountActive(Integer totalCountActive) {
        this.totalCountActive = totalCountActive;
    }

    public Integer getTotalCountActiveToSend() {
        return totalCountActiveToSend;
    }

    public void setTotalCountActiveToSend(Integer totalCountActiveToSend) {
        this.totalCountActiveToSend = totalCountActiveToSend;
    }

    public Integer getTotalCountNotActive() {
        return totalCountNotActive;
    }

    public void setTotalCountNotActive(Integer totalCountNotActive) {
        this.totalCountNotActive = totalCountNotActive;
    }

    public Integer getTotalCountNotActiveToSend() {
        return totalCountNotActiveToSend;
    }

    public void setTotalCountNotActiveToSend(Integer totalCountNotActiveToSend) {
        this.totalCountNotActiveToSend = totalCountNotActiveToSend;
    }
}
