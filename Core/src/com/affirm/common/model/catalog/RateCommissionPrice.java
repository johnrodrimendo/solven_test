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
public class RateCommissionPrice implements Serializable {

    private Integer priceId;
    private String price;
    private List<RateCommissionCluster> clusters = new ArrayList<>();

    public Integer getPriceId() {
        return priceId;
    }

    public void setPriceId(Integer priceId) {
        this.priceId = priceId;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public List<RateCommissionCluster> getClusters() {
        return clusters;
    }

    public void setClusters(List<RateCommissionCluster> clusters) {
        this.clusters = clusters;
    }
}

