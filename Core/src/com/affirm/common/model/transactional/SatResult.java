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

/**
 * Created by john on 29/09/16.
 */
public class SatResult implements Serializable {

    @SerializedName("query_id")
    @Expose
    private Integer queryId;
    @SerializedName("in_doc_type")
    @Expose
    private Integer inDocType;
    @SerializedName("in_doc_number")
    @Expose
    private String inDocNumber;
    /*@SerializedName("sat_id_report")
    @Expose
    private List<SatIdReport> satIdReport = null;*/

    private JSONArray satIdReportJSONArray;

    public void fillFromDb(JSONObject json) {
        SatResult satResult = new Gson().fromJson(json.toString(), SatResult.class);

        setQueryId(JsonUtil.getIntFromJson(json, "query_id", null));
        setInDocType(JsonUtil.getIntFromJson(json, "in_document_type", null));
        setInDocNumber(JsonUtil.getStringFromJson(json, "in_document_number", null));
        setSatIdReportJSONArray(JsonUtil.getJsonArrayFromJson(json, "sat_id_report", new JSONArray()));
        /*if (JsonUtil.getJsonArrayFromJson(json, "sat_id_report", null) != null) {
            setSatIdReport(new Gson().fromJson(
                    JsonUtil.getJsonArrayFromJson(json, "sat_id_report", null).toString(),
                    new TypeToken<List<SatIdReport>>() {
                    }.getType()));
        }*/
        // satIdReport = satResult.satIdReport;
    }

    public Integer getQueryId() {
        return queryId;
    }

    public void setQueryId(Integer queryId) {
        this.queryId = queryId;
    }

    public Integer getInDocType() {
        return inDocType;
    }

    public void setInDocType(Integer inDocType) {
        this.inDocType = inDocType;
    }

    public String getInDocNumber() {
        return inDocNumber;
    }

    public void setInDocNumber(String inDocNumber) {
        this.inDocNumber = inDocNumber;
    }

    /*public List<SatIdReport> getSatIdReport() {
        return satIdReport;
    }

    public void setSatIdReport(List<SatIdReport> satIdReport) {
        this.satIdReport = satIdReport;
    }*/

    public JSONArray getSatIdReportJSONArray() {
        return satIdReportJSONArray;
    }

    public void setSatIdReportJSONArray(JSONArray satIdReportJSONArray) {
        this.satIdReportJSONArray = satIdReportJSONArray;
    }

    @Override
    public String toString() {
        return "SatResult{" +
                "queryId=" + queryId +
                ", inDocType='" + inDocType + '\'' +
                ", inDocNumber='" + inDocNumber + '\'' +
//                ", satIdReport=" + satIdReport +
                '}';
    }

    public static class AnnualReport {

        @SerializedName("year")
        @Expose
        private String year;
        @SerializedName("amount")
        @Expose
        private Double amount;

        public String getYear() {
            return year;
        }

        public void setYear(String year) {
            this.year = year;
        }

        public Double getAmount() {
            return amount;
        }

        public void setAmount(Double amount) {
            this.amount = amount;
        }

        @Override
        public String toString() {
            return "AnnualReport{" +
                    "year='" + year + '\'' +
                    ", amount=" + amount +
                    '}';
        }
    }

    public static class SatIdReport {

        @SerializedName("id")
        @Expose
        public String id;
        @SerializedName("taxpayer")
        @Expose
        public String taxpayer;
        @SerializedName("annual_reports")
        @Expose
        public List<AnnualReport> annualReports = null;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTaxpayer() {
            return taxpayer;
        }

        public void setTaxpayer(String taxpayer) {
            this.taxpayer = taxpayer;
        }

        public List<AnnualReport> getAnnualReports() {
            return annualReports;
        }

        public void setAnnualReports(List<AnnualReport> annualReports) {
            this.annualReports = annualReports;
        }

        @Override
        public String toString() {
            return "SatIdReport{" +
                    "id='" + id + '\'' +
                    ", taxpayer='" + taxpayer + '\'' +
                    ", annualReports=" + annualReports +
                    '}';
        }
    }

}