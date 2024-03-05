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
public class IntegerFieldValidator extends FieldValidator<Integer> implements Serializable {

    // Validations
    private boolean required = false;
    private Integer maxValue;
    private Integer minValue;
    private boolean restricted = false;
    // Error messages keys
    private String requiredErrorMsg = "validation.int.required";
    private String maxValueErrorMsg = "validation.int.maxValue";
    private String minValueErrorMsg = "validation.int.minValue";
    private String integerErrorMsg = "validation.int.incorrectFormat";

    public IntegerFieldValidator() {
        super(Integer.class);
    }

    public IntegerFieldValidator(Integer value) {
        super(Integer.class);
        this.value = value;
    }

    public IntegerFieldValidator(IntegerFieldValidator integerFieldValidator) {
        super(Integer.class);
        update(integerFieldValidator);
    }

    public IntegerFieldValidator(IntegerFieldValidator integerFieldValidator, Integer value) {
        super(Integer.class);
        this.value = value;
        update(integerFieldValidator);
    }

    @Override
    public boolean validate(Locale locale) {
        if (value == null && required) {
            setError(getErrorRequiredMessage(locale, requiredErrorMsg));
            return false;
        }
        if (value != null && maxValue != null && value > maxValue) {
            setError(getMessageSource().getMessage(maxValueErrorMsg, new Object[]{maxValue + ""}, locale));
            return false;
        }
        if (value != null && minValue != null && value < minValue) {
            setError(getMessageSource().getMessage(minValueErrorMsg, new Object[]{minValue + ""}, locale));
            return false;
        }
        return true;
    }

    @Override
    public JSONObject toJson(Locale locale) {
        JSONObject json = new JSONObject();
        json.put("type", type);
        json.put("required", required);
        json.put("maxValue", maxValue);
        json.put("minValue", minValue);
        json.put("restricted", restricted);
        json.put("requiredErrorMsg", getErrorRequiredMessage(locale, requiredErrorMsg));
        json.put("maxValueErrorMsg", getMessageSource().getMessage(maxValueErrorMsg, new Object[]{maxValue + ""}, locale));
        json.put("minValueErrorMsg", getMessageSource().getMessage(minValueErrorMsg, new Object[]{minValue + ""}, locale));
        json.put("integerErrorMsg", getMessageSource().getMessage(integerErrorMsg, null, locale));
        return json;
    }

    @Override
    public void update(FieldValidator validator) {
        IntegerFieldValidator integerFieldValidator = (IntegerFieldValidator) validator;
        setRequired(integerFieldValidator.isRequired());
        setMaxValue(integerFieldValidator.getMaxValue());
        setMinValue(integerFieldValidator.getMinValue());
        setRestricted(integerFieldValidator.isRestricted());
        setRequiredErrorMsg(integerFieldValidator.getRequiredErrorMsg());
        setMaxValueErrorMsg(integerFieldValidator.getMaxValueErrorMsg());
        setMinValueErrorMsg(integerFieldValidator.getMinValueErrorMsg());
        setIntegerErrorMsg(integerFieldValidator.getIntegerErrorMsg());
    }

    public boolean isRequired() {
        return required;
    }

    public IntegerFieldValidator setRequired(boolean required) {
        this.required = required;
        return this;
    }

    public Integer getMaxValue() {
        return maxValue;
    }

    public IntegerFieldValidator setMaxValue(Integer maxValue) {
        this.maxValue = maxValue;
        return this;
    }

    public Integer getMinValue() {
        return minValue;
    }

    public IntegerFieldValidator setMinValue(Integer minValue) {
        this.minValue = minValue;
        return this;
    }

    public boolean isRestricted() {
        return restricted;
    }

    /**
     * Makes that the client can write values greather or less than the values established
     *
     * @param restricted
     */
    public IntegerFieldValidator setRestricted(boolean restricted) {
        this.restricted = restricted;
        return this;
    }

    public String getRequiredErrorMsg() {
        return requiredErrorMsg;
    }

    public IntegerFieldValidator setRequiredErrorMsg(String requiredErrorMsg) {
        this.requiredErrorMsg = requiredErrorMsg;
        return this;
    }

    public String getMaxValueErrorMsg() {
        return maxValueErrorMsg;
    }

    public IntegerFieldValidator setMaxValueErrorMsg(String maxValueErrorMsg) {
        this.maxValueErrorMsg = maxValueErrorMsg;
        return this;
    }

    public String getMinValueErrorMsg() {
        return minValueErrorMsg;
    }

    public IntegerFieldValidator setMinValueErrorMsg(String minValueErrorMsg) {
        this.minValueErrorMsg = minValueErrorMsg;
        return this;
    }

    public String getIntegerErrorMsg() {
        return integerErrorMsg;
    }

    public IntegerFieldValidator setIntegerErrorMsg(String integerErrorMsg) {
        this.integerErrorMsg = integerErrorMsg;
        return this;
    }

    public IntegerFieldValidator setFieldName(String fieldName) { this.fieldName = fieldName; return this;}
}
