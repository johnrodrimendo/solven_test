package com.affirm.common.model;

import com.affirm.common.util.JsonUtil;
import org.jooq.lambda.tuple.Tuple3;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FunnelReportSection {

    private Integer id;
    private String title;
    private Integer count;
    private Double amount;
    private List<Tuple3<String, Integer, Double>> tableDetail;

    public void fillFromDb(JSONObject json) throws Exception {
        setId(JsonUtil.getIntFromJson(json, "table_id", null));
        setTitle(JsonUtil.getStringFromJson(json, "table_name", null));
        setCount(JsonUtil.getIntFromJson(json, "table_count", null));
        setAmount(JsonUtil.getDoubleFromJson(json, "table_value", null));
        if (JsonUtil.getJsonArrayFromJson(json, "table_detail", null) != null) {
            JSONArray jsonRejections = JsonUtil.getJsonArrayFromJson(json, "table_detail", null);
            tableDetail = new ArrayList<>();
            for (int i = 0; i < jsonRejections.length(); i++) {
                if (JsonUtil.getIntFromJson(jsonRejections.getJSONObject(i), "hard_filter_id", null) != null) {
                    tableDetail.add(new Tuple3<>("hard_filter_id", JsonUtil.getIntFromJson(jsonRejections.getJSONObject(i), "hard_filter_id", null), JsonUtil.getDoubleFromJson(jsonRejections.getJSONObject(i), "detail_percentage", null)));
                } else if (JsonUtil.getIntFromJson(jsonRejections.getJSONObject(i), "policy_id", null) != null) {
                    tableDetail.add(new Tuple3<>("policy_id", JsonUtil.getIntFromJson(jsonRejections.getJSONObject(i), "policy_id", null), JsonUtil.getDoubleFromJson(jsonRejections.getJSONObject(i), "detail_percentage", null)));
                } else if (JsonUtil.getIntFromJson(jsonRejections.getJSONObject(i), "offer_rejection_id", null) != null) {
                    tableDetail.add(new Tuple3<>("offer_rejection_id", JsonUtil.getIntFromJson(jsonRejections.getJSONObject(i), "offer_rejection_id", null), JsonUtil.getDoubleFromJson(jsonRejections.getJSONObject(i), "detail_percentage", null)));
                } else if(JsonUtil.getIntFromJson(jsonRejections.getJSONObject(i), "la_rejection_reason_id", null) != null) {
                    tableDetail.add(new Tuple3<>("la_rejection_reason_id", JsonUtil.getIntFromJson(jsonRejections.getJSONObject(i), "la_rejection_reason_id", null), JsonUtil.getDoubleFromJson(jsonRejections.getJSONObject(i), "detail_percentage", null)));
                }
            }
        }
    }

    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id; }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public Integer getCount() { return count; }

    public void setCount(Integer count) { this.count = count; }

    public Double getAmount() { return amount; }

    public void setAmount(Double amount) { this.amount = amount; }

    public List<Tuple3<String, Integer, Double>> getTableDetail() {
        return tableDetail;
    }

    public void setTableDetail(List<Tuple3<String, Integer, Double>> tableDetail) {
        this.tableDetail = tableDetail;
    }
}
