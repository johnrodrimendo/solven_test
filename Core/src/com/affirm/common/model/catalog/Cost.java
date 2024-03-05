package com.affirm.common.model.catalog;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

/**
 * Created by dev5 on 20/06/17.
 */
public class Cost {

    public static final char PORCENTAJE = 'P';
    public static final char VALOR = 'V';

    private int id;
    private String costName;
    private char costType;

    public void fillFromDb(JSONObject json){
        setId(JsonUtil.getIntFromJson(json, "credit_cost_id", null));
        setCostName(JsonUtil.getStringFromJson(json, "credit_cost", null));
        setCostType(JsonUtil.getCharacterFromJson(json, "cost_type", null));
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCostName() {
        return costName;
    }

    public void setCostName(String costName) {
        this.costName = costName;
    }

    public char getCostType() {
        return costType;
    }

    public void setCostType(char costType) {
        this.costType = costType;
    }
}
