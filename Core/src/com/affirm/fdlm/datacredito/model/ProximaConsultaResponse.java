package com.affirm.fdlm.datacredito.model;

import com.google.gson.annotations.SerializedName;

public class ProximaConsultaResponse {

    @SerializedName("getProximaConsultaResult")
    private String result;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
