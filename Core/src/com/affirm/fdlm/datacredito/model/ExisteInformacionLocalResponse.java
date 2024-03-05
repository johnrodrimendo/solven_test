package com.affirm.fdlm.datacredito.model;

import com.google.gson.annotations.SerializedName;

public class ExisteInformacionLocalResponse {

    @SerializedName("getExisteInformacionLocalResult")
    private CommonResult result;

    public CommonResult getResult() {
        return result;
    }

    public void setResult(CommonResult result) {
        this.result = result;
    }
}
