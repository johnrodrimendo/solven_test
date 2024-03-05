package com.affirm.efl.model;

/**
 * Created by dev5 on 04/07/17.
 */
public class Observation {

    private String responseValue;
    private String responseLabel;

    public Observation(String responseValue){
        this.responseValue = responseValue;
    }

    public Observation(String responseValue, String responseLabel){
        this.responseValue = responseValue;
        this.responseLabel = responseLabel;
    }

    public String getResponseValue() {
        return responseValue;
    }

    public void setResponseValue(String responseValue) {
        this.responseValue = responseValue;
    }

    public String getResponseLabel() {
        return responseLabel;
    }

    public void setResponseLabel(String responseLabel) {
        this.responseLabel = responseLabel;
    }
}
