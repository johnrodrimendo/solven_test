package com.affirm.common.model.transactional;

import com.affirm.common.util.JsonUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by stbn on 27/10/16.
 */
public class IcarValidation implements Serializable {

    private Integer id;
    private Integer loanApplicationId;
    private Integer personId;
    private String result;
    private String warning;
    private Boolean active;
    private List<IcarValidationField> fields;

    public void fillFromDb(JSONObject json) throws Exception {
        setId(JsonUtil.getIntFromJson(json, "icar_validation_id", null));
        setLoanApplicationId(JsonUtil.getIntFromJson(json, "loan_application_id", null));
        setPersonId(JsonUtil.getIntFromJson(json, "person_id", null));
        setResult(JsonUtil.getStringFromJson(json, "result", null));
        setWarning(JsonUtil.getStringFromJson(json, "warning", null));
        setActive(JsonUtil.getBooleanFromJson(json, "is_active", null));
        if (JsonUtil.getJsonArrayFromJson(json, "icar_validation_fields", null) != null) {
            fields = new ArrayList<>();
            JSONArray arrayFields = JsonUtil.getJsonArrayFromJson(json, "icar_validation_fields", null);
            for (int i = 0; i < arrayFields.length(); i++) {
                IcarValidationField field = new IcarValidationField();
                field.fillFromDb(arrayFields.getJSONObject(i));
                fields.add(field);
            }
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getLoanApplicationId() {
        return loanApplicationId;
    }

    public void setLoanApplicationId(Integer loanApplicationId) {
        this.loanApplicationId = loanApplicationId;
    }

    public Integer getPersonId() {
        return personId;
    }

    public void setPersonId(Integer personId) {
        this.personId = personId;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getWarning() {
        return warning;
    }

    public void setWarning(String warning) {
        this.warning = warning;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public List<IcarValidationField> getFields() {
        return fields;
    }

    public void setFields(List<IcarValidationField> fields) {
        this.fields = fields;
    }
}

