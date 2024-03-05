package com.affirm.common.util;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.Locale;

/**
 * @author jrodriguez
 */
public class CharFieldValidator extends FieldValidator<Character> implements Serializable {

    // Validations
    private boolean required = false;
    private Character[] validValues;
    // Error messages keys
    private String requiredErrorMsg = "validation.string.requiredNoFieldName";
    private String validValuesErrorMsg = "validation.char.validValues";

    public CharFieldValidator() {
        super(Character.class);
    }

    public CharFieldValidator(Character value) {
        super(Character.class);
        this.value = value;
    }

    public CharFieldValidator(CharFieldValidator charFieldValidator) {
        super(Character.class);
        update(charFieldValidator);
    }

    public CharFieldValidator(CharFieldValidator charFieldValidator, Character value) {
        super(Character.class);
        this.value = value;
        update(charFieldValidator);
    }

    @Override
    public boolean validate(Locale locale) {
        if ((value == null) && required) {
            setError(getErrorRequiredMessage(locale, requiredErrorMsg));
            return false;
        }
        if (value != null && validValues != null) {
            boolean valid = false;
            for (Character s : validValues) {
                if (s.equals(value)) {
                    valid = true;
                    break;
                }
            }
            if (!valid) {
                setError(getMessageSource().getMessage(validValuesErrorMsg, null, locale));
                return false;
            }
        }
        return true;
    }

    @Override
    public JSONObject toJson(Locale locale) {
        JSONObject json = new JSONObject();
        json.put("type", type);
        json.put("required", required);
        json.put("requiredErrorMsg", getErrorRequiredMessage(locale, requiredErrorMsg));
        json.put("validValues", validValues);
        json.put("validValuesErrorMsg", getMessageSource().getMessage(validValuesErrorMsg, null, locale));
        return json;
    }

    @Override
    public void update(FieldValidator validator) {
        CharFieldValidator charFieldValidator = (CharFieldValidator) validator;
        setRequired(charFieldValidator.isRequired());
        setValidValues(charFieldValidator.getValidValues());
        setRequiredErrorMsg(charFieldValidator.getRequiredErrorMsg());
        setValidValues(charFieldValidator.getValidValues());
    }

    public boolean isRequired() {
        return required;
    }

    public CharFieldValidator setRequired(boolean required) {
        this.required = required;
        return this;
    }

    public Character[] getValidValues() {
        return validValues;
    }

    public CharFieldValidator setValidValues(Character[] validValues) {
        this.validValues = validValues;
        return this;
    }

    public String getRequiredErrorMsg() {
        return requiredErrorMsg;
    }

    public CharFieldValidator setRequiredErrorMsg(String requiredErrorMsg) {
        this.requiredErrorMsg = requiredErrorMsg;
        return this;
    }

    public String getValidValuesErrorMsg() {
        return validValuesErrorMsg;
    }

    public CharFieldValidator setValidValuesErrorMsg(String validValuesErrorMsg) {
        this.validValuesErrorMsg = validValuesErrorMsg;
        return this;
    }

    public CharFieldValidator setFieldName(String fieldName) { this.fieldName = fieldName; return this;}
}
