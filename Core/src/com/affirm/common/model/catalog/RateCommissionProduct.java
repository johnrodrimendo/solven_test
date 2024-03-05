/**
 *
 */
package com.affirm.common.model.catalog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jrodriguez
 */
public class RateCommissionProduct implements Serializable {

    private Integer productId;
    private String product;
    private List<RateCommissionPrice> prices = new ArrayList<>();

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public List<RateCommissionPrice> getPrices() {
        return prices;
    }

    public void setPrices(List<RateCommissionPrice> prices) {
        this.prices = prices;
    }
}

