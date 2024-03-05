package com.affirm.common.model.transactional;

import java.util.List;

public class EntityProductParamApprovalValidationConfig {

    private String code;
    private List<Integer> validationIds;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<Integer> getValidationIds() {
        return validationIds;
    }

    public void setValidationIds(List<Integer> validationIds) {
        this.validationIds = validationIds;
    }
}
