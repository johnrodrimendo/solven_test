package com.affirm.common.model.transactional;

import com.affirm.common.util.JsonUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

/**
 * @author Romulo Galindo Tanta
 */
public class RescueScreenParams implements Serializable {

    private Boolean showScreen;
    private List<Integer> products;
    private List<Integer> entityProductParams;

    public void fillFromDb(JSONObject json) {
        setShowScreen(JsonUtil.getBooleanFromJson(json, "show_rescue_screen", null));
        if (JsonUtil.getJsonArrayFromJson(json, "js_product_id", null) != null) {
            setProducts(new Gson().fromJson(
                    JsonUtil.getJsonArrayFromJson(json, "js_product_id", null).toString(),
                    new TypeToken<List<Integer>>() {
                    }.getType()));
        }
        if (JsonUtil.getJsonArrayFromJson(json, "js_entity_product_parameter_id", null) != null) {
            setEntityProductParams(new Gson().fromJson(
                    JsonUtil.getJsonArrayFromJson(json, "js_entity_product_parameter_id", null).toString(),
                    new TypeToken<List<Integer>>() {
                    }.getType()));
        }
    }

    public Boolean getShowScreen() {
        return showScreen;
    }

    public void setShowScreen(Boolean showScreen) {
        this.showScreen = showScreen;
    }

    public List<Integer> getProducts() {
        return products;
    }

    public void setProducts(List<Integer> products) {
        this.products = products;
    }

    public List<Integer> getEntityProductParams() {
        return entityProductParams;
    }

    public void setEntityProductParams(List<Integer> entityProductParams) {
        this.entityProductParams = entityProductParams;
    }
}
