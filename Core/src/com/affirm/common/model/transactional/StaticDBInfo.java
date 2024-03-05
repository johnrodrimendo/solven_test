package com.affirm.common.model.transactional;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;

public class StaticDBInfo implements Serializable {

    private Double income;//salario bruto
    private String ruc;//
    private String inicio;//inicio laboral
    private String razonSocial;

    public void fillFromDB(JSONObject json) {
        setRuc(JsonUtil.getStringFromJson(json, "ruc", null));
        setInicio(JsonUtil.getStringFromJson(json, "inicio", null));
        setIncome(JsonUtil.getDoubleFromJson(json, "bruto", null));
        setRazonSocial(JsonUtil.getStringFromJson(json, "razon_social", null));
    }

    public Double getIncome() {
        return income;
    }

    public void setIncome(Double income) {
        this.income = income;
    }

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public String getInicio() {
        return inicio;
    }

    public void setInicio(String inicio) {
        this.inicio = inicio;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }
}
