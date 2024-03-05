package com.affirm.common.model.catalog;

import com.affirm.common.model.transactional.Policy;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

public class EntityProductParamPolicyConfiguration {

    private String parameter;
    private String parameter2;
    private Policy policy;
    private Entity entity;
    private Boolean isActive;

    public void fillFromDb(JSONObject json, CatalogService catalogService) {
        setParameter(JsonUtil.getStringFromJson(json, "parameter", null));
        setParameter2(JsonUtil.getStringFromJson(json, "parameter_2", null));
        if (JsonUtil.getIntFromJson(json, "entity_id", null) != null)
            setEntity(catalogService.getEntity(JsonUtil.getIntFromJson(json, "entity_id", null)));
        if (JsonUtil.getIntFromJson(json, "policy_id", null) != null)
            setPolicy(catalogService.getPolicyById(JsonUtil.getIntFromJson(json, "policy_id", null)));
        setIsActive(JsonUtil.getBooleanFromJson(json, "is_active", null));
    }


    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    public String getParameter2() {
        return parameter2;
    }

    public void setParameter2(String parameter2) {
        this.parameter2 = parameter2;
    }

    public Policy getPolicy() {
        return policy;
    }

    public void setPolicy(Policy policy) {
        this.policy = policy;
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean active) {
        isActive = active;
    }
}
