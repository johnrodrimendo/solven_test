/**
 *
 */
package com.affirm.common.util;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.Locale;

/**
 * @author jrodriguez
 */
public class BooleanFieldValidator extends FieldValidator<Boolean> implements Serializable {

    // Validations
    private boolean required = false;
    // Error messages keys
    private String requiredErrorMsg = "validation.string.required";

    public BooleanFieldValidator() {
        super(Boolean.class);
    }

    public BooleanFieldValidator(Boolean value) {
        super(Boolean.class);
        this.value = value;
    }

    public BooleanFieldValidator(BooleanFieldValidator booleanFieldValidator) {
        super(Boolean.class);
        update(booleanFieldValidator);
    }

    @Override
    public boolean validate(Locale locale) {
        if (value == null && required) {
            setError(getErrorRequiredMessage(locale, requiredErrorMsg));
            return false;
        }
        return true;
    }

    @Override
    public JSONObject toJson(Locale locale) {
        JSONObject json = new JSONObject();
        json.put("type", type);
        json.put("required", required);
        json.put("requiredErrorMsg", getErrorRequiredMessage(locale, requiredErrorMsg));
        return json;
    }

    @Override
    public void update(FieldValidator validator) {
        BooleanFieldValidator booleanFieldValidator = (BooleanFieldValidator) validator;
        setRequired(booleanFieldValidator.isRequired());
        setRequiredErrorMsg(booleanFieldValidator.getRequiredErrorMsg());
    }

    public boolean isRequired() {
        return required;
    }

    public BooleanFieldValidator setRequired(boolean required) {
        this.required = required;
        return this;
    }

    public String getRequiredErrorMsg() {
        return requiredErrorMsg;
    }

    public BooleanFieldValidator setRequiredErrorMsg(String requiredErrorMsg) {
        this.requiredErrorMsg = requiredErrorMsg;
        return this;
    }

    public BooleanFieldValidator setFieldName(String fieldName) { this.fieldName = fieldName; return this;}
}
