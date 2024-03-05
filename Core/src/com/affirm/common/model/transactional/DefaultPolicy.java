package com.affirm.common.model.transactional;

import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

/**
 * Created by dev5 on 21/03/18.
 */
public class DefaultPolicy {

    private Integer policyId;
    private Policy policy;
    private Integer countryId;
    private String parameter1;
    private String parameter2;
    private String parameter3;
    private Integer orderId;

    public void fillFromDb(JSONObject json, CatalogService catalogService){
        setPolicyId(JsonUtil.getIntFromJson(json, "policy_id", null));
        if(getPolicyId() != null)
            setPolicy(catalogService.getPolicyById(getPolicyId()));
        setCountryId(JsonUtil.getIntFromJson(json, "country_id", null));
        setParameter1(JsonUtil.getStringFromJson(json, "parameter_1", null));
        setParameter2(JsonUtil.getStringFromJson(json, "parameter_2", null));
        setParameter3(JsonUtil.getStringFromJson(json, "parameter_3", null));
        setOrderId(JsonUtil.getIntFromJson(json, "order_id", null));
    }

    public Integer getPolicyId() {
        return policyId;
    }

    public void setPolicyId(Integer policyId) {
        this.policyId = policyId;
    }

    public Integer getCountryId() {
        return countryId;
    }

    public void setCountryId(Integer countryId) {
        this.countryId = countryId;
    }

    public String getParameter1() {
        return parameter1;
    }

    public void setParameter1(String parameter1) {
        this.parameter1 = parameter1;
    }

    public String getParameter2() {
        return parameter2;
    }

    public void setParameter2(String parameter2) {
        this.parameter2 = parameter2;
    }

    public String getParameter3() {
        return parameter3;
    }

    public void setParameter3(String parameter3) {
        this.parameter3 = parameter3;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Policy getPolicy() {
        return policy;
    }

    public void setPolicy(Policy policy) {
        this.policy = policy;
    }
}
