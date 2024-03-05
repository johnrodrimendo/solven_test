package com.affirm.common.model;

public class DataLabelsData {

    private String value;
    private String reason;

    DataLabelsData(String value){
        this.value = value;
    }

    public DataLabelsData(String value, String reason){
        this.value = value;
        this.reason = reason;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

}
