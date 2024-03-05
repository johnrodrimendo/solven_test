package com.affirm.common.model.catalog;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by jrodriguez on 07/09/16.
 */
public class ComparisonCreditCost implements Serializable {

    public static final int DESGRAVAMEN = 1;
    public static final int DESGRAVAMEN_PRIMA_UNICA = 5;
    public static final int DESGRAVAMEN_FIJO = 6;

    private Integer id;
    private String cost;
    private Character type;
    private String costKey;

    public void fillFromDb(JSONObject json) {
        setId(JsonUtil.getIntFromJson(json, "credit_cost_id", null));
        setCost(JsonUtil.getStringFromJson(json, "credit_cost", null));
        setType(JsonUtil.getCharacterFromJson(json, "cost_type", null));
        setCostKey(JsonUtil.getStringFromJson(json, "text_int", null));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public Character getType() {
        return type;
    }

    public void setType(Character type) {
        this.type = type;
    }

    public String getCostKey() {
        return costKey;
    }

    public void setCostKey(String costKey) {
        this.costKey = costKey;
    }
}
