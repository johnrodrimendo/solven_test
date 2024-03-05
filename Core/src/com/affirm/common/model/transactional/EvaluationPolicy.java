/**
 *
 */
package com.affirm.common.model.transactional;

import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Locale;

/**
 * @author jrodriguez
 */
public class EvaluationPolicy implements Serializable {

    private Integer id;
    private Integer evaluationId;
    private Integer entityId;
    private Integer productId;
    private Policy policy;
    private Integer entityProductParameterId;
    private Integer expirationDays;
    private Integer evaluationOrder;
    private String param1;
    private String param2;
    private String param3;
    private Integer entityProductPolicySetId;
    private Integer employerId;
    private Integer step;
    private Boolean isApproved;

    public void fillFromDb(JSONObject json, CatalogService catalog, Locale locale) throws Exception {
        setId(JsonUtil.getIntFromJson(json, "evaluation_policy_id", null));
        setEvaluationId(JsonUtil.getIntFromJson(json, "evaluation_id", null));
        setEntityId(JsonUtil.getIntFromJson(json, "entity_id", null));
        setProductId(JsonUtil.getIntFromJson(json, "product_id", null));
        setPolicy(catalog.getPolicyById(JsonUtil.getIntFromJson(json, "policy_id", null)));
        setEntityProductParameterId(JsonUtil.getIntFromJson(json, "entity_product_parameter_id", null));
        setExpirationDays(JsonUtil.getIntFromJson(json, "expiration_days", null));
        setEvaluationOrder(JsonUtil.getIntFromJson(json, "evaluation_order", null));
        setParam1(JsonUtil.getStringFromJson(json, "parameter", null));
        setParam2(JsonUtil.getStringFromJson(json, "parameter_2", null));
        setParam3(JsonUtil.getStringFromJson(json, "parameter_3", null));
        setEntityProductPolicySetId(JsonUtil.getIntFromJson(json, "entity_product_policy_set_id", null));
        setEmployerId(JsonUtil.getIntFromJson(json, "employer_id", null));
        setStep(JsonUtil.getIntFromJson(json, "step", null));
        setApproved(JsonUtil.getBooleanFromJson(json, "is_approved", null));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getEvaluationId() {
        return evaluationId;
    }

    public void setEvaluationId(Integer evaluationId) {
        this.evaluationId = evaluationId;
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

    public Policy getPolicy() {
        return policy;
    }

    public void setPolicy(Policy policy) {
        this.policy = policy;
    }

    public Integer getEntityProductParameterId() {
        return entityProductParameterId;
    }

    public void setEntityProductParameterId(Integer entityProductParameterId) {
        this.entityProductParameterId = entityProductParameterId;
    }

    public Integer getExpirationDays() {
        return expirationDays;
    }

    public void setExpirationDays(Integer expirationDays) {
        this.expirationDays = expirationDays;
    }

    public Integer getEvaluationOrder() {
        return evaluationOrder;
    }

    public void setEvaluationOrder(Integer evaluationOrder) {
        this.evaluationOrder = evaluationOrder;
    }

    public String getParam1() {
        return param1;
    }

    public void setParam1(String param1) {
        this.param1 = param1;
    }

    public String getParam2() {
        return param2;
    }

    public void setParam2(String param2) {
        this.param2 = param2;
    }

    public String getParam3() {
        return param3;
    }

    public void setParam3(String param3) {
        this.param3 = param3;
    }

    public Integer getEntityProductPolicySetId() {
        return entityProductPolicySetId;
    }

    public void setEntityProductPolicySetId(Integer entityProductPolicySetId) {
        this.entityProductPolicySetId = entityProductPolicySetId;
    }

    public Integer getEmployerId() {
        return employerId;
    }

    public void setEmployerId(Integer employerId) {
        this.employerId = employerId;
    }

    public Integer getStep() {
        return step;
    }

    public void setStep(Integer step) {
        this.step = step;
    }

    public Boolean getApproved() {
        return isApproved;
    }

    public void setApproved(Boolean approved) {
        isApproved = approved;
    }
}
