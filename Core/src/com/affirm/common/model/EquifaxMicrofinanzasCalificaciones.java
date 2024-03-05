package com.affirm.common.model;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

// TODO MOVE TO FOLDER EQUIFAX/MODEL ???
public class EquifaxMicrofinanzasCalificaciones {

    private Integer loanApplicationId;
    private String valor;
    private Boolean flag;
    private Integer nroentidades;
    private String calificacion;

    public void fillFromDb(JSONObject json) {
        setLoanApplicationId(JsonUtil.getIntFromJson(json, "loan_application_id", null));
        setValor(JsonUtil.getStringFromJson(json, "valor", null));
        setFlag(JsonUtil.getBooleanFromJson(json, "flag", null));
        setNroentidades(JsonUtil.getIntFromJson(json, "nroentidades", null));
        setCalificacion(JsonUtil.getStringFromJson(json, "calificacion", null));
    }

    public Integer getLoanApplicationId() {
        return loanApplicationId;
    }

    public void setLoanApplicationId(Integer loanApplicationId) {
        this.loanApplicationId = loanApplicationId;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public Boolean getFlag() {
        return flag;
    }

    public void setFlag(Boolean flag) {
        this.flag = flag;
    }

    public Integer getNroentidades() {
        return nroentidades;
    }

    public void setNroentidades(Integer nroentidades) {
        this.nroentidades = nroentidades;
    }

    public String getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(String calificacion) {
        this.calificacion = calificacion;
    }
}
