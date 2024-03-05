package com.affirm.backoffice.model;

import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class OriginationReportPeriod {

    private String period;
    private List<OriginationReportPeriodDetail> details;

    public void fillFromDb(JSONObject json, CatalogService catalog) throws Exception {
        setPeriod(JsonUtil.getStringFromJson(json, "period", null));
        if (JsonUtil.getJsonArrayFromJson(json, "detail", null) != null) {
            details = new ArrayList<>();
            JSONArray detailArray = JsonUtil.getJsonArrayFromJson(json, "detail", null);
            for (int i = 0; i < detailArray.length(); i++) {
                OriginationReportPeriodDetail detail = new OriginationReportPeriodDetail();
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

    public List<OriginationReportPeriodDetail> getDetails() {
        return details;
    }

    public void setDetails(List<OriginationReportPeriodDetail> details) {
        this.details = details;
    }
}
