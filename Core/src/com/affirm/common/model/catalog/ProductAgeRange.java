/**
 *
 */
package com.affirm.common.model.catalog;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * @author jrodriguez
 */
public class ProductAgeRange implements Serializable {


    private Integer productId;
    private Integer minAge;
    private Integer maxAge;
    private Integer entityId;

    public void fillFromDb(JSONObject json) throws Exception {
        setProductId(JsonUtil.getIntFromJson(json, "product_id", null));
        setEntityId(JsonUtil.getIntFromJson(json, "entity_id", null));
        setMinAge(JsonUtil.getIntFromJson(json, "min_age", null));
        setMaxAge(JsonUtil.getIntFromJson(json, "max_age", null));
    }

    public Integer getMinAge() {
        return minAge;
    }

    public void setMinAge(Integer minAge) {
        this.minAge = minAge;
    }

    public Integer getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(Integer maxAge) {
        this.maxAge = maxAge;
    }

    public Integer getEntityId() {
        return entityId;
    }

    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

}
