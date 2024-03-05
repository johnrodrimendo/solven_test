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
public class DoubleFieldValidator extends FieldValidator<Double> implements Serializable {

    // Validations
    private boolean required = false;
    private Double maxValue;
    private Double minValue;
    private Double minPercentageValue;
    private boolean restricted = false;
    // Error messages keys
    private String requiredErrorMsg = "validation.double.required";
    private String maxValueErrorMsg = "validation.double.maxValue";
    private String minValueErrorMsg = "validation.double.minValue";
    private String minPercentageValueErrorMsg = "validation.double.minPercentageValue";
    private String doubleErrorMsg = "validation.double.incorrectFormat";

    public DoubleFieldValidator() {
        super(Double.class);
    }

    public DoubleFieldValidator(Double value) {
        super(Double.class);
        this.value = value;
    }

    public DoubleFieldValidator(DoubleFieldValidator doubleFieldValidator) {
        super(Double.class);
        update(doubleFieldValidator);
    }

    public DoubleFieldValidator(DoubleFieldValidator doubleFieldValidator, Double value) {
        super(Integer.class);
        this.value = value;
        update(doubleFieldValidator);
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

        if (value != null && minPercentageValue != null && value < minPercentageValue) {
            setError(getMessageSource().getMessage(minPercentageValueErrorMsg, new Object[]{minPercentageValue + ""}, locale));
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
        json.put("minPercentageValue", minPercentageValue);
        json.put("restricted", restricted);
        json.put("requiredErrorMsg", getErrorRequiredMessage(locale, requiredErrorMsg));
        json.put("maxValueErrorMsg",
                getMessageSource().getMessage(maxValueErrorMsg, new Object[]{maxValue + ""}, locale));
        json.put("minValueErrorMsg",
                getMessageSource().getMessage(minValueErrorMsg, new Object[]{minValue + ""}, locale));
        json.put("minPercentageValueErrorMsg",
                getMessageSource().getMessage(minPercentageValueErrorMsg, new Object[]{minPercentageValue + ""}, locale));
        json.put("doubleErrorMsg", getMessageSource().getMessage(doubleErrorMsg, null, locale));
        return json;
    }

    @Override
    public void update(FieldValidator validator) {
        DoubleFieldValidator doubleFieldValidator = (DoubleFieldValidator) validator;
        setRequired(doubleFieldValidator.isRequired());
        setMaxValue(doubleFieldValidator.getMaxValue());
        setMinValue(doubleFieldValidator.getMinValue());
        setMinPercentageValue(doubleFieldValidator.getMinPercentageValue());
        setRestricted(doubleFieldValidator.isRestricted());
        setRequiredErrorMsg(doubleFieldValidator.getRequiredErrorMsg());
        setMaxValueErrorMsg(doubleFieldValidator.getMaxValueErrorMsg());
        setMinValueErrorMsg(doubleFieldValidator.getMinValueErrorMsg());
        setMinPercentageValueErrorMsg(doubleFieldValidator.getMinPercentageValueErrorMsg());
        setDoubleErrorMsg(doubleFieldValidator.getDoubleErrorMsg());
    }

    public boolean isRequired() {
        return required;
    }

    public DoubleFieldValidator setRequired(boolean required) {
        this.required = required;
        return this;
    }

    public Double getMaxValue() {
        return maxValue;
    }

    public DoubleFieldValidator setMaxValue(Double maxValue) {
        this.maxValue = maxValue;
        return this;
    }

    public Double getMinValue() {
        return minValue;
    }

    public DoubleFieldValidator setMinValue(Double minValue) {
        this.minValue = minValue;
        return this;
    }

    public Double getMinPercentageValue() {
        return minPercentageValue;
    }

    public DoubleFieldValidator setMinPercentageValue(Double minValue) {
        this.minPercentageValue = minValue;
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
    public DoubleFieldValidator setRestricted(boolean restricted) {
        this.restricted = restricted;
        return this;
    }

    public String getRequiredErrorMsg() {
        return requiredErrorMsg;
    }

    public DoubleFieldValidator setRequiredErrorMsg(String requiredErrorMsg) {
        this.requiredErrorMsg = requiredErrorMsg;
        return this;
    }

    public String getMaxValueErrorMsg() {
        return maxValueErrorMsg;
    }

    public DoubleFieldValidator setMaxValueErrorMsg(String maxValueErrorMsg) {
        this.maxValueErrorMsg = maxValueErrorMsg;
        return this;
    }

    public String getMinValueErrorMsg() {
        return minValueErrorMsg;
    }

    public DoubleFieldValidator setMinValueErrorMsg(String minValueErrorMsg) {
        this.minValueErrorMsg = minValueErrorMsg;
        return this;
    }

    public String getDoubleErrorMsg() {
        return doubleErrorMsg;
    }

    public DoubleFieldValidator setDoubleErrorMsg(String doubleErrorMsg) {
        this.doubleErrorMsg = doubleErrorMsg;
        return this;
    }

    public String getMinPercentageValueErrorMsg() {
        return minPercentageValueErrorMsg;
    }

    public DoubleFieldValidator setMinPercentageValueErrorMsg(String minPercentageValueErrorMsg) {
        this.minPercentageValueErrorMsg = minPercentageValueErrorMsg;
        return this;
    }

    public DoubleFieldValidator setFieldName(String fieldName) { this.fieldName = fieldName; return this;}
}
