package com.affirm.common.model;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.util.Date;

public class EquifaxSicomCabecera {

    private Integer loanApplicationId;
    private Date fechaVencimientoReciente;
    private Integer cantidadSoles;
    private Double montoSoles;
    private Integer cantidadDolares;
    private Double montoDolares;

    public void fillFromDb(JSONObject json) {
        setLoanApplicationId(JsonUtil.getIntFromJson(json, "loan_application_id", null));
        setFechaVencimientoReciente(JsonUtil.getPostgresDateFromJson(json, "fechavencimientoreciente", null));
        setCantidadSoles(JsonUtil.getIntFromJson(json, "cantidadsoles", null));
        setMontoSoles(JsonUtil.getDoubleFromJson(json, "montosoles", null));
        setCantidadDolares(JsonUtil.getIntFromJson(json, "cantidaddolares", null));
        setMontoDolares(JsonUtil.getDoubleFromJson(json, "montodolares", null));
    }

    public Integer getLoanApplicationId() {
        return loanApplicationId;
    }

    public void setLoanApplicationId(Integer loanApplicationId) {
        this.loanApplicationId = loanApplicationId;
    }

    public Date getFechaVencimientoReciente() {
        return fechaVencimientoReciente;
    }

    public void setFechaVencimientoReciente(Date fechaVencimientoReciente) {
        this.fechaVencimientoReciente = fechaVencimientoReciente;
    }

    public Integer getCantidadSoles() {
        return cantidadSoles;
    }

    public void setCantidadSoles(Integer cantidadSoles) {
        this.cantidadSoles = cantidadSoles;
    }

    public Double getMontoSoles() {
        return montoSoles;
    }

    public void setMontoSoles(Double montoSoles) {
        this.montoSoles = montoSoles;
    }

    public Integer getCantidadDolares() {
        return cantidadDolares;
    }

    public void setCantidadDolares(Integer cantidadDolares) {
        this.cantidadDolares = cantidadDolares;
    }

    public Double getMontoDolares() {
        return montoDolares;
    }

    public void setMontoDolares(Double montoDolares) {
        this.montoDolares = montoDolares;
    }
}
