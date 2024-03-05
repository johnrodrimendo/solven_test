package com.affirm.backoffice.model;

import com.affirm.common.model.catalog.Product;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class OriginationReportProductGroup {

    private Product product;
    private List<OriginationReportPeriodDetail> details;

    public OriginationReportProductGroup(Product product) {
        this.product = product;
        details = new ArrayList<>();
    }

    public OriginationReportPeriodDetail getDetailByPeriod(String period){
        return details.stream().filter(d -> d.getPeriod().equalsIgnoreCase(period)).findFirst().orElse(new OriginationReportPeriodDetail(null, product, null, period, 0, 0.0, 0.0));
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public List<OriginationReportPeriodDetail> getDetails() {
        return details;
    }

    public void setDetails(List<OriginationReportPeriodDetail> details) {
        this.details = details;
    }
}
