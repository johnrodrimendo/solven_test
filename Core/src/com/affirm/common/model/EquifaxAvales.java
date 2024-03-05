package com.affirm.common.model;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

public class EquifaxAvales {

    private Integer loanApplicationId;
    private String calificacion;

    public void fillFromDb(JSONObject json) {
        setLoanApplicationId(JsonUtil.getIntFromJson(json, "loan_application_id", null));
        setCalificacion(JsonUtil.getStringFromJson(json, "calificacion", null));
    }

    public Integer getLoanApplicationId() {
        return loanApplicationId;
    }

    public void setLoanApplicationId(Integer loanApplicationId) {
        this.loanApplicationId = loanApplicationId;
    }

    public String getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(String calificacion) {
        this.calificacion = calificacion;
    }
}
