package com.affirm.fdlm.datacredito.model;

import com.google.gson.annotations.SerializedName;

public class ReporteClienteTPZResponse {

    @SerializedName("getReporteClienteTPZResult")
    private CommonResult result;

    public CommonResult getResult() {
        return result;
    }

    public void setResult(CommonResult result) {
        this.result = result;
    }
}
