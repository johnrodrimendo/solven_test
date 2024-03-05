package com.affirm.common.util;

import org.json.JSONObject;

import java.io.Serializable;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by miberico on 27/03/17.
 */
public class DateFieldValidator extends FieldValidator<Date> implements Serializable {

    public static final String REGEXP_DDMMYYYY_SLASH_DATE = "[0-3][0-9][/][0-1][0-9][/][0-9][0-9][0-9][0-9]";


    // Validations
    private boolean required = false;
    private Integer maxCharacters;
    private Integer minCharacters;
    private String validRegex = REGEXP_DDMMYYYY_SLASH_DATE;
    private String validPattern;
    private boolean restricted = false;
    private boolean futureDate = true;
    private boolean pastDate = true;

    // Error messages keys
    private String requiredErrorMsg = "validation.date.required";
    private String maxCharactersErrorMsg = "validation.date.maxCharacters";
    private String minCharactersErrorMsg = "validation.date.minCharacters";
    private String validRegexErrorMsg = "validation.date.validRegex";
    private String validPatternErrorMsg = "validation.date.validRegex";
    private String futureDateErrorMsg = "validation.date.futureDate";
    private String pastDateErrorMsg = "validation.date.pastDate";

    public DateFieldValidator() {
        super(Date.class);
    }

    public DateFieldValidator(Date value) {
        super(Date.class);
        this.value = value;
    }

    public DateFieldValidator(DateFieldValidator dateFieldValidator) {
        super(Date.class);
        update(dateFieldValidator);
    }

    public DateFieldValidator(DateFieldValidator dateFieldValidator, Date value) {
        super(Date.class);
        this.value = value;
        update(dateFieldValidator);
    }

    @Override
    public boolean validate(Locale locale) {
            if ((value == null || value.toString().isEmpty()) && required) {
            setError(getMessageSource().getMessage(requiredErrorMsg, null, locale));
            return false;
        }
        Format formatter = new SimpleDateFormat("dd/MM/yyyy");
        if ((value != null && !value.toString().isEmpty()) && validRegex != null && !formatter.format(value).matches(validRegex)) {
            setError(getMessageSource().getMessage(validRegexErrorMsg, null, locale));
            return false;
        }

        if ((value != null && !value.toString().isEmpty()) && validPattern != null && !formatter.format(value).matches(validPattern)) {
            setError(getMessageSource().getMessage(validPatternErrorMsg, null, locale));
            return false;
        }

        if ((value != null && !value.toString().isEmpty()) && maxCharacters != null && value.toString().length() > maxCharacters) {
            setError(getMessageSource().getMessage(maxCharactersErrorMsg, new Object[]{maxCharacters + ""}, locale));
            return false;
        }
        if ((value != null && !value.toString().isEmpty()) && minCharacters != null && value.toString().length() < minCharacters) {
            setError(getMessageSource().getMessage(minCharactersErrorMsg, new Object[]{minCharacters + ""}, locale));
            return false;
        }
        if ((value != null && !value.toString().isEmpty()) && !isFutureDate() && value.after(new Date())) {
            setError(getMessageSource().getMessage(getFutureDateErrorMsg(), null, locale));
            return false;
        }
        if ((value != null && !value.toString().isEmpty()) && !isPastDate() && value.before(new Date())) {
            setError(getMessageSource().getMessage(getPastDateErrorMsg(), null, locale));
            return false;
        }
        return true;
    }

    @Override
    public void update(FieldValidator validator) {
        DateFieldValidator dateFieldValidator = (DateFieldValidator) validator;

    }

    @Override
    public JSONObject toJson(Locale locale) {
        JSONObject json = new JSONObject();
        json.put("type", type);
        json.put("required", required);
        json.put("maxCharacters", maxCharacters);
        json.put("minCharacters", minCharacters);
        json.put("validRegex", validRegex);
        json.put("validPattern", validPattern);
        json.put("restricted", restricted);
        json.put("requiredErrorMsg", getMessageSource().getMessage(requiredErrorMsg, null, locale));
        json.put("maxCharactersErrorMsg",
                getMessageSource().getMessage(maxCharactersErrorMsg, new Object[]{maxCharacters + ""}, locale));
        json.put("minCharactersErrorMsg",
                getMessageSource().getMessage(minCharactersErrorMsg, new Object[]{minCharacters + ""}, locale));
        json.put("validRegexErrorMsg", getMessageSource().getMessage(validRegexErrorMsg, null, locale));
        json.put("validPatternErrorMsg", getMessageSource().getMessage(validPatternErrorMsg, null, locale));
        json.put("pastDateErrorMsg", getMessageSource().getMessage(pastDateErrorMsg, null, locale));
        json.put("futureDateErrorMsg", getMessageSource().getMessage(futureDateErrorMsg, null, locale));
        return json;
    }

    public boolean isRequired() {
        return required;
    }

    public boolean isFutureDate() {
        return futureDate;
    }

    public boolean isPastDate() {
        return pastDate;
    }

    public DateFieldValidator setRequired(boolean required) {
        this.required = required;
        return this;
    }

    public Integer getMaxCharacters() {
        return maxCharacters;
    }

    public DateFieldValidator setMaxCharacters(Integer maxCharacters) {
        this.maxCharacters = maxCharacters;
        return this;
    }

    public Integer getMinCharacters() {
        return minCharacters;
    }

    public DateFieldValidator setMinCharacters(Integer minCharacters) {
        this.minCharacters = minCharacters;
        return this;
    }

    public String getValidRegex() {
        return validRegex;
    }

    /**
     * The Regular expression that the characters have to match to be valid
     *
     * @param validRegex
     */
    public DateFieldValidator setValidRegex(String validRegex) {
        this.validRegex = validRegex;
        return this;
    }

    public boolean isRestricted() {
        return restricted;
    }

    /**
     * Makes that the client can write more or less characters than the range established
     *
     * @param restricted
     */
    public DateFieldValidator setRestricted(boolean restricted) {
        this.restricted = restricted;
        return this;
    }

    public String getRequiredErrorMsg() {
        return requiredErrorMsg;
    }

    public DateFieldValidator setRequiredErrorMsg(String requiredErrorMsg) {
        this.requiredErrorMsg = requiredErrorMsg;
        return this;
    }

    public String getMaxCharactersErrorMsg() {
        return maxCharactersErrorMsg;
    }

    public DateFieldValidator setMaxCharactersErrorMsg(String maxCharactersErrorMsg) {
        this.maxCharactersErrorMsg = maxCharactersErrorMsg;
        return this;
    }

    public String getMinCharactersErrorMsg() {
        return minCharactersErrorMsg;
    }

    public DateFieldValidator setMinCharactersErrorMsg(String minCharactersErrorMsg) {
        this.minCharactersErrorMsg = minCharactersErrorMsg;
        return this;
    }

    public String getValidPattern() {
        return validPattern;
    }

    public DateFieldValidator setValidPattern(String validPattern) {
        this.validPattern = validPattern;
        return this;
    }

    public String getValidRegexErrorMsg() {
        return validRegexErrorMsg;
    }

    public DateFieldValidator setValidRegexErrorMsg(String validRegexErrorMsg) {
        this.validRegexErrorMsg = validRegexErrorMsg;
        return this;
    }

    public String getValidPatternErrorMsg() {
        return validPatternErrorMsg;
    }

    public DateFieldValidator setValidPatternErrorMsg(String validPatternErrorMsg) {
        this.validPatternErrorMsg = validPatternErrorMsg;
        return this;
    }

    public DateFieldValidator setFutureDate(boolean futureDate) {
        this.futureDate = futureDate;
        return this;
    }

    public DateFieldValidator setPastDate(boolean pastDate) {
        this.pastDate = pastDate;
        return this;
    }


    public String getFutureDateErrorMsg() {
        return futureDateErrorMsg;
    }

    public String getPastDateErrorMsg() {
        return pastDateErrorMsg;
    }

}
