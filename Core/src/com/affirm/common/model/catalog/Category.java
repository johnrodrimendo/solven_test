package com.affirm.common.model.catalog;

import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

/**
 * Created by dev5 on 20/06/17.
 */
public class Category implements Serializable {

    public static final int CREDITO_PERSONAL = 1;
    public static final int TARJETA_CREDITO = 2;
    public static final int CREDITO_VEHICULAR = 3;
    public static final int CORTO_PLAZO = 4;
    public static final int VALIDACION_DE_IDENTIDAD = 9;

    private int id;
    private String name;
    private boolean isActive;
    private List<Integer> creditCosts;
    private Product product;

    public void fillFromDb(JSONObject json, CatalogService catalog) {
        setId(JsonUtil.getIntFromJson(json, "comparison_category_id", null));
        setName(JsonUtil.getStringFromJson(json, "comparison_category", null));
        setActive(JsonUtil.getBooleanFromJson(json, "is_active", null));
        setCreditCosts(JsonUtil.getListFromJsonArray(json.getJSONArray("ar_credit_cost"), (arr, i) -> arr.getInt(i)));
        if (JsonUtil.getIntFromJson(json, "product_id", null) != null)
            setProduct(catalog.getProduct(JsonUtil.getIntFromJson(json, "product_id", null)));
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public List<Integer> getCreditCosts() {
        return creditCosts;
    }

    public void setCreditCosts(List<Integer> creditCosts) {
        this.creditCosts = creditCosts;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
