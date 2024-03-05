package com.affirm.fdlm.topaz.model;

import com.google.gson.annotations.SerializedName;

public class ExecuteResponse {

    @SerializedName("TopazMiddleWareResponse")
    private TopazMiddleWareResponse topazMiddleWareResponse;

    public TopazMiddleWareResponse getTopazMiddleWareResponse() {
        return topazMiddleWareResponse;
    }

    public void setTopazMiddleWareResponse(TopazMiddleWareResponse topazMiddleWareResponse) {
        this.topazMiddleWareResponse = topazMiddleWareResponse;
    }
}
