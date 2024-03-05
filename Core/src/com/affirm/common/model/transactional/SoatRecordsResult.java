package com.affirm.common.model.transactional;

import com.affirm.common.util.JsonUtil;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SoatRecordsResult implements Serializable {
    @SerializedName("query_id")
    @Expose
    private Integer queryId;

    @SerializedName("plate")
    @Expose
    private String regPlate;

    @SerializedName("company")
    @Expose
    private String company;

    @SerializedName("start_date")
    @Expose
    private Date startDate;

    @SerializedName("ending_date")
    @Expose
    private Date endingDate;

    @SerializedName("certificate")
    @Expose
    private String certificate;

    @SerializedName("use")
    @Expose
    private String usage;

    @SerializedName("class")
    @Expose
    private String vehicleCategory;

    @SerializedName("type")
    @Expose
    private String typeOfDocument;

    @SerializedName("creation_date")
    @Expose
    private Timestamp creationDate;

    @SerializedName("state")
    @Expose
    private String state;

    @SerializedName("js_soat")
    @Expose
    private List<SoatRecordsResult.Reports> reports = null;

    private JSONArray reportsJSONArray;

    public void fillFromDb(JSONObject json) {

        setQueryId(JsonUtil.getIntFromJson(json, "query_id", null));
        setRegPlate(JsonUtil.getStringFromJson(json, "in_plate", null));
        setCompany(JsonUtil.getStringFromJson(json, "company", null));
        if (JsonUtil.getStringFromJson(json, "start_date", null) != null)
            setStartDate(toDate(JsonUtil.getStringFromJson(json, "start_date", null)));
        if (JsonUtil.getStringFromJson(json, "ending_date", null) != null)
            setEndingDate(toDate(JsonUtil.getStringFromJson(json, "ending_date", null)));
        setCertificate(JsonUtil.getStringFromJson(json, "certificate", null));
        setUsage(JsonUtil.getStringFromJson(json, "use", null));
        setTypeOfDocument(JsonUtil.getStringFromJson(json, "type", null));
        if (JsonUtil.getStringFromJson(json, "creation_date", null) != null)
            setCreationDate(toTimeStamp(JsonUtil.getStringFromJson(json, "creation_date", null)));
        setState(JsonUtil.getStringFromJson(json, "state", null));
        setVehicleCategory(JsonUtil.getStringFromJson(json, "class", null));
        setReportsJSONArray(JsonUtil.getJsonArrayFromJson(json, "js_soat", null));

        if (JsonUtil.getJsonArrayFromJson(json, "js_soat", null) != null) {
            setReports(new Gson().fromJson(
                    JsonUtil.getJsonArrayFromJson(json, "js_soat", null).toString(), new TypeToken<List<SoatRecordsResult.Reports>>() {
                    }.getType()));
        }
    }

    @Override
    public String toString() {
        return "{" +
                "regPlate='" + regPlate + '\'' +
                ", company='" + company + '\'' +
                ", type=" + typeOfDocument + '\'' +
                ", document=" + certificate + '\'' +
                ", startDate=" + startDate + '\'' +
                ", creationDate=" + creationDate + '\'' +
                ", endingDate=" + endingDate + '\'' +
                ", class=" + vehicleCategory + '\'' +
                ", state=" + state + '\'' +
                ", use=" + usage + '\'' +
                ", reports=" + reports +
                '}';
    }

    public static class Reports {
        @SerializedName("regPlate")
        @Expose
        private String regPlate;

        @SerializedName("company")
        @Expose
        private String company;

        @SerializedName("startDate")
        @Expose
        private String startDate;

        @SerializedName("endingDate")
        @Expose
        private String endingDate;

        @SerializedName("certificate")
        @Expose
        private String certificate;

        @SerializedName("use")
        @Expose
        private String usage;

        @SerializedName("class")
        @Expose
        private String vehicleCategory;

        @SerializedName("document")
        @Expose
        private String typeOfDocument;


        @SerializedName("creationDate")
        @Expose
        private String creationDate;

        @SerializedName("state")
        @Expose
        private String state;

        public String getRegPlate() {
            return regPlate;
        }

        public void setRegPlate(String regPlate) {
            this.regPlate = regPlate;
        }

        public String getCompany() {
            return company;
        }

        public void setCompany(String company) {
            this.company = company;
        }

        public String getStartDate() {
            return startDate;
        }

        public void setStartDate(String startDate) {
            this.startDate = startDate;
        }

        public String getEndingDate() {
            return endingDate;
        }

        public void setEndingDate(String endingDate) {
            this.endingDate = endingDate;
        }

        public String getCertificate() {
            return certificate;
        }

        public void setCertificate(String certificate) {
            this.certificate = certificate;
        }

        public String getUsage() {
            return usage;
        }

        public void setUsage(String usage) {
            this.usage = usage;
        }

        public String getVehicleCategory() {
            return vehicleCategory;
        }

        public void setVehicleCategory(String vehicleCategory) {
            this.vehicleCategory = vehicleCategory;
        }

        public String getTypeOfDocument() {
            return typeOfDocument;
        }

        public void setTypeOfDocument(String typeOfDocument) {
            this.typeOfDocument = typeOfDocument;
        }

        public String getCreationDate() {
            return creationDate;
        }

        public void setCreationDate(String creationDate) {
            this.creationDate = creationDate;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        @Override
        public String toString() {
            return "{" +
                    "regPlate='" + regPlate + '\'' +
                    ", company='" + company + '\'' +
                    ", type=" + typeOfDocument + '\'' +
                    ", document=" + certificate + '\'' +
                    ", startDate=" + startDate + '\'' +
                    ", creationDate=" + creationDate + '\'' +
                    ", endingDate=" + endingDate + '\'' +
                    ", class=" + vehicleCategory + '\'' +
                    ", state=" + state + '\'' +
                    ", use=" + usage +
                    '}';
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

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndingDate() {
        return endingDate;
    }

    public void setEndingDate(Date endingDate) {
        this.endingDate = endingDate;
    }

    public String getCertificate() {
        return certificate;
    }

    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }

    public String getUsage() {
        return usage;
    }

    public void setUsage(String usage) {
        this.usage = usage;
    }

    public String getVehicleCategory() {
        return vehicleCategory;
    }

    public void setVehicleCategory(String vehicleCategory) {
        this.vehicleCategory = vehicleCategory;
    }

    public String getTypeOfDocument() {
        return typeOfDocument;
    }

    public void setTypeOfDocument(String typeOfDocument) {
        this.typeOfDocument = typeOfDocument;
    }

    public Timestamp getCreationDate() {
        return creationDate;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public void setCreationDate(Timestamp creationDate) {
        this.creationDate = creationDate;
    }

    public JSONArray getReportsJSONArray() {
        return reportsJSONArray;
    }

    public void setReportsJSONArray(JSONArray reportsJSONArray) {
        this.reportsJSONArray = reportsJSONArray;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public List<Reports> getReports() {
        return reports;
    }

    public void setReports(List<Reports> reports) {
        this.reports = reports;
    }

    public static Timestamp toTimeStamp(String timestampStr) {
        Timestamp timestamp = null;

        if (null != timestampStr && timestampStr.length() > 10) {

            SimpleDateFormat creationDateFormat = null;
            if (timestampStr.charAt(2) == '/')
                creationDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm");
            else if (timestampStr.charAt(4) == '-')
                creationDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

            try {
                Date parsedDate = creationDateFormat.parse(timestampStr);
                timestamp = new java.sql.Timestamp(parsedDate.getTime());

            } catch (ParseException ex) {
                ex.printStackTrace();
            }

        }
        return timestamp;
    }

    public static Date toDate(String dateStr) {
        Date date = null;
        if (null != dateStr && dateStr.length() >= 10) {
            SimpleDateFormat dateFormat = null;

            if (dateStr.charAt(2) == '-')
                dateFormat = new SimpleDateFormat("dd-MMM-yyyy", new Locale("es", "ES"));
            else if (dateStr.charAt(4) == '-')
                dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            try {
                date = dateFormat.parse(dateStr);

            } catch (ParseException ex) {
                ex.printStackTrace();
            }
        }
        return date;
    }
}
