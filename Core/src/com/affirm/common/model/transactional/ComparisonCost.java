package com.affirm.common.model.transactional;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

/**
 * Created by dev5 on 19/06/17.
 */
public class ComparisonCost {

    private Integer id;
    private Double cost;
    private Character costType;


    public void fillFromDb(JSONObject json){
        setId(JsonUtil.getIntFromJson(json, "credit_cost_id", null));
        setCost(JsonUtil.getDoubleFromJson(json, "cost", null));
        setCostType(JsonUtil.getCharacterFromJson(json, "cost_type", null));
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public Character getCostType() {
        return costType;
    }

    public void setCostType(Character costType) {
        this.costType = costType;
    }
}
