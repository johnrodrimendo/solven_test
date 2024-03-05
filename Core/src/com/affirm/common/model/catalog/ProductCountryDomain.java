/**
 *
 */
package com.affirm.common.model.catalog;

import java.io.Serializable;
import java.util.List;

/**
 * @author jrodriguez
 */
public class ProductCountryDomain implements Serializable{

    private Integer countryId;
    private List<String> domains;
    private Integer productId;

    public Integer getCountryId() {
        return countryId;
    }

    public void setCountryId(Integer countryId) {
        this.countryId = countryId;
    }

    public List<String> getDomains() {
        return domains;
    }

    public void setDomains(List<String> domains) {
        this.domains = domains;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }
}
