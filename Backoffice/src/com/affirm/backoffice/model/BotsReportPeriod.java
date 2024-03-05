package com.affirm.backoffice.model;

import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BotsReportPeriod {
    private String period;
    List<BotsReportPeriodDetail> details;

    public void fillFromDb(JSONObject json, CatalogService catalog) throws Exception {
        setPeriod(JsonUtil.getStringFromJson(json, "period", null));
        if (JsonUtil.getJsonArrayFromJson(json, "detail", null) != null) {
            details = new ArrayList<>();
            JSONArray detailArray = JsonUtil.getJsonArrayFromJson(json, "detail", null);
            for (int i = 0; i < detailArray.length(); i++) {
                BotsReportPeriodDetail detail = new BotsReportPeriodDetail();
                detail.fillFromDb(detailArray.getJSONObject(i), catalog);
                details.add(detail);
            }
        }
    }
    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public List<BotsReportPeriodDetail> getDetails() {
        return details;
    }

    public void setDetails(List<BotsReportPeriodDetail> details) {
        this.details = details;
    }
}
