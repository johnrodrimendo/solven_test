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
public class PreliminaryHardFilter implements Serializable {

    private Integer id;
    private Integer preliminaryEvaluationId;
    private HardFilter hardFilter;
    private Integer expirationDays;
    private Integer evaluationOrder;
    private String param1;
    private String param2;
    private String param3;
    private Integer entityProductHardFilterSetId;
    private Boolean isApproved;

    public void fillFromDb(JSONObject json, CatalogService catalog, Locale locale) throws Exception {
        setId(JsonUtil.getIntFromJson(json, "preliminary_evaluation_hard_filter_id", null));
        setPreliminaryEvaluationId(JsonUtil.getIntFromJson(json, "preliminary_evaluation_id", null));
        setHardFilter(catalog.getHardFilterById(JsonUtil.getIntFromJson(json, "hard_filter_id", null)));
        setExpirationDays(JsonUtil.getIntFromJson(json, "expiration_days", null));
        setEvaluationOrder(JsonUtil.getIntFromJson(json, "evaluation_order", null));
        setParam1(JsonUtil.getStringFromJson(json, "parameter", null));
        setParam2(JsonUtil.getStringFromJson(json, "parameter_2", null));
        setParam3(JsonUtil.getStringFromJson(json, "parameter_3", null));
        setEntityProductHardFilterSetId(JsonUtil.getIntFromJson(json, "entity_product_hard_filter_set_id", null));
        setApproved(JsonUtil.getBooleanFromJson(json, "is_approved", null));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPreliminaryEvaluationId() {
        return preliminaryEvaluationId;
    }

    public void setPreliminaryEvaluationId(Integer preliminaryEvaluationId) {
        this.preliminaryEvaluationId = preliminaryEvaluationId;
    }

    public HardFilter getHardFilter() {
        return hardFilter;
    }

    public void setHardFilter(HardFilter hardFilter) {
        this.hardFilter = hardFilter;
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

    public Integer getEntityProductHardFilterSetId() {
        return entityProductHardFilterSetId;
    }

    public void setEntityProductHardFilterSetId(Integer entityProductHardFilterSetId) {
        this.entityProductHardFilterSetId = entityProductHardFilterSetId;
    }

    public Boolean getApproved() {
        return isApproved;
    }

    public void setApproved(Boolean approved) {
        isApproved = approved;
    }
}
