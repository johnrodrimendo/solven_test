package com.affirm.common.model.transactional;

import com.affirm.common.model.catalog.Entity;
import com.affirm.common.model.catalog.Product;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

/**
 * Created by dev5 on 21/03/18.
 */
public class PreEvaluationRejection {

    private Entity entity;
    private Product product;
    private HardFilter hardFilter;
    private boolean status;

    public void fillFromDb(JSONObject json, CatalogService catalogService) throws Exception {
        if(JsonUtil.getIntFromJson(json, "entity_id", null) != null)
            setEntity(catalogService.getEntity(JsonUtil.getIntFromJson(json, "entity_id", null)));
        if(JsonUtil.getIntFromJson(json, "product_id", null) != null)
            setProduct(catalogService.getProduct(JsonUtil.getIntFromJson(json, "product_id", null)));
        if(JsonUtil.getIntFromJson(json, "hard_filter_id", null) != null)
            setHardFilter(catalogService.getHardFilterById(JsonUtil.getIntFromJson(json, "hard_filter_id", null)));
        if(JsonUtil.getBooleanFromJson(json, "status", null) != null)
            setStatus(JsonUtil.getBooleanFromJson(json, "status", null));
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public HardFilter getHardFilter() {
        return hardFilter;
    }

    public void setHardFilter(HardFilter hardFilter) {
        this.hardFilter = hardFilter;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
