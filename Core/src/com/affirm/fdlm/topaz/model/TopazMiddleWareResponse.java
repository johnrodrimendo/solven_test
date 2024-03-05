package com.affirm.fdlm.topaz.model;

import com.google.gson.annotations.SerializedName;

public class TopazMiddleWareResponse {

    private Integer errorCode;

    @SerializedName("GetNumeradors")
    private Numerators numerators;

    private String serviceName;

    private String error;

    private Boolean status;

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public Numerators getNumerators() {
        return numerators;
    }

    public void setNumerators(Numerators numerators) {
        this.numerators = numerators;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
