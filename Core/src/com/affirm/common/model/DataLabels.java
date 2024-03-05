package com.affirm.common.model;

public class DataLabels {

    private String type;
    private DataLabelsData data;
    private DataLabelsData error;

    private Boolean notFound;
    public DataLabels(String type, String value){
        this.type = type;
        this.data = new DataLabelsData(value);
    }

    public DataLabels(String type, String value, String reason){
        this.type = type;
        if(value != null) this.data = new DataLabelsData(value);
        if(reason != null) this.error = new DataLabelsData(null, reason);
    }

    public DataLabels(String type, String value, String reason, Boolean notFound){
        this.type = type;
        if(value != null) this.data = new DataLabelsData(value);
        if(reason != null) this.error = new DataLabelsData(null, reason);
        if(reason != null && notFound != null) this.notFound = notFound;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public DataLabelsData getData() {
        return data;
    }

    public DataLabelsData getError() {
        return error;
    }

    public void setError(DataLabelsData error) {
        this.error = error;
    }

    public void setData(DataLabelsData data) {
        this.data = data;
    }

    public Boolean getNotFound() {
        return notFound;
    }

    public void setNotFound(Boolean notFound) {
        this.notFound = notFound;
    }

    public String getValue(){
        if(data != null) return data.getValue();
        return null;
    }
}
