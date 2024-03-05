package com.affirm.common.model.catalog;

import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by john on 04/01/17.
 */
public class HumanFormProductParam implements Serializable {

    private Product product;
    private boolean skippable;

    public HumanFormProductParam() {
    }

    public HumanFormProductParam(Product product) {
        this.product = product;
    }

    public void fillFromDb(JSONObject json, CatalogService catalog) throws Exception {
        if (JsonUtil.getIntFromJson(json, "product_id", null) != null)
            setProduct(catalog.getProduct(JsonUtil.getIntFromJson(json, "product_id", null)));
        setSkippable(JsonUtil.getBooleanFromJson(json, "is_skippable", null));
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public boolean getSkippable() {
        return skippable;
    }

    public void setSkippable(boolean skippable) {
        this.skippable = skippable;
    }
}
