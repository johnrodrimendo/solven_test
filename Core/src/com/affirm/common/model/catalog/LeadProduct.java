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
public class LeadProduct implements Serializable, ICatalogIntegerId {
    //
    public static final int LEAD_CREDIT_CARD = 1;
    public static final int LEAD_LOAN = 2;

    private Integer id;
    private String productPropertyKey;
    private String product;


    public void fillFromDb(JSONObject json) {
        setId(JsonUtil.getIntFromJson(json, "product_id", null));
        setProduct(JsonUtil.getStringFromJson(json, "product", null));
        setProductPropertyKey(JsonUtil.getStringFromJson(json, "text_int", null));
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    public String getProductPropertyKey() {
        return productPropertyKey;
    }

    public void setProductPropertyKey(String productPropertyKey) {
        this.productPropertyKey = productPropertyKey;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

}
