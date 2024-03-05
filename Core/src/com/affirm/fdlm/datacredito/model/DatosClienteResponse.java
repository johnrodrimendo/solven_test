package com.affirm.fdlm.datacredito.model;

import com.google.gson.annotations.SerializedName;

public class DatosClienteResponse {

    @SerializedName("getDatosClienteResult")
    private CommonResult result;

    public CommonResult getResult() {
        return result;
    }

    public void setResult(CommonResult result) {
        this.result = result;
    }
}
