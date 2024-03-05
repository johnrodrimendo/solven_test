package com.affirm.fdlm.datacredito.model;

import com.google.gson.annotations.SerializedName;

public class UltimaConsultaResponse {

    @SerializedName("getUltimaConsultaResult")
    private String result;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
