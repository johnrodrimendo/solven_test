package com.affirm.common.model.transactional;

import com.affirm.common.util.JsonUtil;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

public class SatPlateResult implements Serializable {

    @SerializedName("query_id")
    @Expose
    private Integer queryId;

    @SerializedName("in_plate")
    @Expose
    private String regPlate;

    @SerializedName("total")
    @Expose
    private Double total;

    @SerializedName("message")
    @Expose
    private String message;


    @SerializedName("js_report")
    @Expose
    private List<SatPlateResult.Report> reports = null;


    private JSONArray reportsJSONArray;

    @Override
    public String toString() {
        return "SatPlateResult{" +
                "plate=" + regPlate +
                ", total='" + total + '\'' +
                ", message=" + message + '\'' +
                ", reports=" + reports +
                '}';
    }


    public void fillFromDb(JSONObject json) {

        setQueryId(JsonUtil.getIntFromJson(json, "query_id", null));
        setRegPlate(JsonUtil.getStringFromJson(json, "in_plate", null));
        setTotal(JsonUtil.getDoubleFromJson(json, "total", null));
        setMessage(JsonUtil.getStringFromJson(json, "message", null));

        setReportsJSONArray(JsonUtil.getJsonArrayFromJson(json, "js_report", new JSONArray()));
        if (JsonUtil.getJsonArrayFromJson(json, "js_report", null) != null) {
            setReports(new Gson().fromJson(
                    JsonUtil.getJsonArrayFromJson(json, "js_report", null).toString(),
                    new TypeToken<List<SatPlateResult.Report>>() {
                    }.getType()));
        }
    }

    public Integer getQueryId() {
        return queryId;
    }

    public void setQueryId(Integer queryId) {
        this.queryId = queryId;
    }

    public String getRegPlate() {
        return regPlate;
    }

    public void setRegPlate(String regPlate) {
        this.regPlate = regPlate;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public List<Report> getReports() {
        return reports;
    }

    public void setReports(List<Report> reports) {
        this.reports = reports;
    }

    public JSONArray getReportsJSONArray() {
        return reportsJSONArray;
    }

    public void setReportsJSONArray(JSONArray reportsJSONArray) {
        this.reportsJSONArray = reportsJSONArray;
    }

    public static class Report {

        @SerializedName("plate")
        @Expose
        private String plate;

        @SerializedName("amount")
        @Expose
        private String amount;

        @SerializedName("status")
        @Expose
        private String status;

        @SerializedName("doc")
        @Expose
        private String doc;

        @SerializedName("date")
        @Expose
        private String date;

        public String getPlate() {
            return plate;
        }

        public void setPlate(String plate) {
            this.plate = plate;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getDoc() {
            return doc;
        }

        public void setDoc(String doc) {
            this.doc = doc;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        @Override
        public String toString() {
            return "reports{" +
                    "plate='" + plate + '\'' +
                    ", amount='" + amount + '\'' +
                    ", status=" + status + '\'' +
                    ", doc=" + doc + '\'' +
                    ", date=" + date +
                    '}';
        }

    }
}
