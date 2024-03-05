package com.affirm.cajasullana.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by dev5 on 22/02/18.
 */
public class CreditosCancelarResponse {

    @SerializedName("message")
    private String errorMessage;
    @SerializedName("status")
    private String status;
    @SerializedName("listaCreditosCancelacion")
    private List<CreditoCancelar> creditosCancelar;


    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<CreditoCancelar> getCreditosCancelar() {
        return creditosCancelar;
    }

    public void setCreditosCancelar(List<CreditoCancelar> creditosCancelar) {
        this.creditosCancelar = creditosCancelar;
    }
}
