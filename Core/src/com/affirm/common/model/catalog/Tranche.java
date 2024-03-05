package com.affirm.common.model.catalog;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by jrodriguez on 08/09/16.
 */
public class Tranche implements Serializable {

    private Integer id;
    private String tranche;
    private Integer minValue;
    private Integer maxValue;

    public void fillFromDb(JSONObject json) throws Exception {
        setId(JsonUtil.getIntFromJson(json, "tranche_id", null));
        setTranche(JsonUtil.getStringFromJson(json, "tranche", null));
        setMinValue(JsonUtil.getIntFromJson(json, "min_value", null));
        setMaxValue(JsonUtil.getIntFromJson(json, "max_value", null));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTranche() {
        return tranche;
    }

    public void setTranche(String tranche) {
        this.tranche = tranche;
    }

    public Integer getMinValue() {
        return minValue;
    }

    public void setMinValue(Integer minValue) {
        this.minValue = minValue;
    }

    public Integer getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(Integer maxValue) {
        this.maxValue = maxValue;
    }
}
