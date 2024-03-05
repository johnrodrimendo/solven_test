package com.affirm.common.util;

import org.apache.commons.validator.routines.EmailValidator;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Locale;

/**
 * @author jrodriguez
 */
public class StringFieldValidator extends FieldValidator<String> implements Serializable {

    public static final String REGEXP_INTEGER_NUMBERS = "^[0-9]+$";
    public static final String REGEXP_PHONE_NUMBERS = "^[0-9]+$";
    public static final String REGEXP_PHONE_NUMBERS_HYPHEN = "^[0-9\\-]+$";
    public static final String REGEXP_PHONE_NUMBERS_WITH_AREA_CODE = "^[0-9 \\-\\(\\)]+$";
    public static final String REGEXP_DOUBLE_NUMBERS = "^[0-9.]+$";
    public static final String REGEXP_ALPHANUMERIC = "^[a-zA-Z0-9]+$";
    public static final String REGEXP_ALPHANUMERIC_SPEC_CHARS = "^[a-zA-Z0-9ñÑáÁéÉíÍóÓúÚüÜ .,;@+*{}¿?!¡%#:()+/=\\-\\–\\_]+$";
    public static final String REGEXP_NAMES = "^['a-zA-ZñÑáÁéÉíÍóÓúÚ -]+$";
    public static final String REGEXP_FULLNAMES = "^[a-zA-Z\\-ñÑáÁéÉíÍóÓúÚüÜ'´]{2,20} [a-zA-Z\\-ñÑáÁéÉíÍóÓúÚüÜ'´]{2,20}( [a-zA-Z\\-ñÑáÁéÉíÍóÓúÚüÜ'´]{2,20})*$";
    public static final String REGEXP_TELEPHONE = "^[0-9 ()]+$";
    public static final String REGEXP_EMAIL = "^[a-zA-Z0-9 .@_-]+$";
    public static final String REGEXP_WITHOUT_SPEC_CHARS = "^[a-zA-Z0-9ñÑáÁéÉíÍóÓúÚ .,;\\-\\–\\_]+$";
    public static final String REGEXP_WITH_SPEC_CHARS = "^[a-zA-ZñÑ .,;@+*{}¿?!¡%#:()+/=\\-\\–\\_]+$";
    public static final String REGEXP_DDMMYYYY_SLASH_DATE = "[0-3][0-9][/][0-1][0-9][/][0-9][0-9][0-9][0-9]";
    public static final String REGEXP_MMYYYY_SLASH_DATE = "[0-1][0-9][/][0-9][0-9][0-9][0-9]";
    public static final String REGEXP_FULLADDRESS = "^[a-zA-ZñÑáÁéÉíÍóÓúÚ0-9üÜ'´ .:,;#º°\\-\\–\\_\\n]*$";
    public static final String REGEXP_SEARCH = "^[a-zA-ZñÑáÁéÉíÍóÓúÚüÜ´0-9 @.]*$";

    public static final String PATTER_REGEX_PASSWORD = "^(?=.*[a-zA-ZñÑáÁéÉíÍóÓúÚüÜ])(?=.*[!@#$%^&*_\\d]).{8,}$";
    public static final String PATTER_REGEX_RUC = "^(10|15|20)[0-9]{9}?$";
    public static final String PATTER_REGEX_PHONE_OR_CELLPHONE = "^(?!0+$)\\d{6,9}$";
    public static final String PATTER_REGEX_CELLPHONE = "^(9)[0-9]{8}?$";
    public static final String PATTER_REGEX_PERCENT = "^(100([\\.][0-9]{2})?$|[0-9]{1,2}([\\.][0-9]{1,})?)$";
    public static final String PATTER_REGEX_EMAIL = "^(([^<>()\\[\\]\\\\.,;:\\s@\"]+(\\.[^<>()\\[\\]\\\\.,;:\\s@\"]+)*)|(\".+\"))@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
    public static final String PATTER_REGEX_CDI_CUIT_CUIL = "^(20|23|24|27|30|33|34)[0-9]{9}?$";
    public static final String PATTER_REGEX_CDI_CUIT_CUIL_PERSONAL = "^(20|23|24|27)[0-9]{9}?$";
    public static final String PATTER_REGEX_CUIT = "^(10|15|20|30)[0-9]{9}?$";
    public static final String PATTER_REGEX_CUIT_PERSON = "^(20|23|24|27)[0-9]{9}?$";
    public static final String PATTER_REGEX_CUIT_COMPANY = "^(20|23|24|27|30|33|34)[0-9]{9}?$";
    public static final String PATTER_REGEX_NIT_COMPANY = "^\\d{9,10}$";
    public static final String PATTER_PHONE_NUMBERS_PERU = "^(9)[0-9]{8}$";
    public static final String PATTER_PHONE_NUMBERS_ARGENTINA = "^[0-9]{3,4}\\-[0-9]{3,4}$";
    public static final String PATTER_PHONE_NUMBERS_COLOMBIA = "^3\\d{2}\\d{7}$";
    public static final String PATTER_PHONE_CODE_AR="^[1-9][0-9]*$";
    public static final String LEAST_ONE_ALPHANUMERIC_CHARACTER="^.*[a-zA-Z0-9]+.*$";

    // Validations
    private boolean required = false;
    private Integer maxCharacters;
    private Integer minCharacters;
    private boolean emailFormat = false;
    private String validRegex = REGEXP_WITHOUT_SPEC_CHARS;
    private String validPattern;
    private boolean restricted = false;
    // Error messages keys
    private String requiredErrorMsg = "validation.string.required";
    private String maxCharactersErrorMsg = "validation.string.maxCharacters";
    private String minCharactersErrorMsg = "validation.string.minCharacters";
    private String emailFormatErrorMsg = "validation.string.emailFormat";
    private String validRegexErrorMsg = "validation.string.validRegex";
    private String validPatternErrorMsg = "validation.string.validRegex";
    private String typeField = "caracteres";

    public StringFieldValidator() {
        super(String.class);
    }

    public StringFieldValidator(String value) {
        super(String.class);
        this.value = value;
    }

    public StringFieldValidator(StringFieldValidator stringFieldValidator) {
        super(String.class);
        update(stringFieldValidator);
    }

    public StringFieldValidator(StringFieldValidator stringFieldValidator, String value) {
        super(String.class);
        this.value = value;
        update(stringFieldValidator);
    }

    @Override
    public boolean validate(Locale locale) {
        if ((value == null || value.isEmpty()) && required) {
            setError(getErrorRequiredMessage(locale, requiredErrorMsg));
            return false;
        }
        if ((value != null && !value.isEmpty()) && validRegex != null && !value.matches(validRegex)) {
            setError(getMessageSource().getMessage(validRegexErrorMsg, null, locale));
            return false;
        }

        if ((value != null && !value.isEmpty()) && validPattern != null && !value.matches(validPattern)) {
            setError(getMessageSource().getMessage(validPatternErrorMsg, null, locale));
            return false;
        }

        if ((value != null && !value.isEmpty()) && maxCharacters != null && value.length() > maxCharacters) {
            setError(getMessageSource().getMessage(maxCharactersErrorMsg, new Object[]{maxCharacters + "", typeField}, locale));
            return false;
        }
        if ((value != null && !value.isEmpty()) && minCharacters != null && value.length() < minCharacters) {
            setError(getMessageSource().getMessage(minCharactersErrorMsg, new Object[]{minCharacters + "", typeField}, locale));
            return false;
        }
        if ((value != null && !value.isEmpty()) && emailFormat && !EmailValidator.getInstance().isValid(value)) {
            setError(getMessageSource().getMessage(emailFormatErrorMsg, null, locale));
            return false;
        }
        return true;
    }

    @Override
    public JSONObject toJson(Locale locale) {
        JSONObject json = new JSONObject();
        json.put("type", type);
        json.put("required", required);
        json.put("maxCharacters", maxCharacters);
        json.put("minCharacters", minCharacters);
        json.put("emailFormat", emailFormat);
        json.put("validRegex", validRegex);
        json.put("validPattern", validPattern);
        json.put("restricted", restricted);
        json.put("requiredErrorMsg", getErrorRequiredMessage(locale, requiredErrorMsg));
        json.put("maxCharactersErrorMsg",
                getMessageSource().getMessage(maxCharactersErrorMsg, new Object[]{maxCharacters + "", typeField}, locale));
        json.put("minCharactersErrorMsg",
                getMessageSource().getMessage(minCharactersErrorMsg, new Object[]{minCharacters + "", typeField}, locale));
        json.put("emailFormatErrorMsg", getMessageSource().getMessage(emailFormatErrorMsg, null, locale));
        json.put("validRegexErrorMsg", getMessageSource().getMessage(validRegexErrorMsg, null, locale));
        json.put("validPatternErrorMsg", getMessageSource().getMessage(validPatternErrorMsg, null, locale));
        return json;
    }

    @Override
    public void update(FieldValidator validator) {
        StringFieldValidator stringFieldValidator = (StringFieldValidator) validator;
        setRequired(stringFieldValidator.isRequired());
        setMaxCharacters(stringFieldValidator.getMaxCharacters());
        setMinCharacters(stringFieldValidator.getMinCharacters());
        setEmailFormat(stringFieldValidator.isEmailFormat());
        setValidRegex(stringFieldValidator.getValidRegex());
        setValidPattern(stringFieldValidator.getValidPattern());
        setRestricted(stringFieldValidator.isRestricted());
        setRequiredErrorMsg(stringFieldValidator.getRequiredErrorMsg());
        setMaxCharactersErrorMsg(stringFieldValidator.getMaxCharactersErrorMsg());
        setMinCharactersErrorMsg(stringFieldValidator.getMinCharactersErrorMsg());
        setEmailFormatErrorMsg(stringFieldValidator.getEmailFormatErrorMsg());
        setValidRegexErrorMsg(stringFieldValidator.getValidRegexErrorMsg());
        setValidPatternErrorMsg(stringFieldValidator.getValidPatternErrorMsg());
        setTypeField(stringFieldValidator.getTypeField());
    }

    public boolean isRequired() {
        return required;
    }

    public StringFieldValidator setRequired(boolean required) {
        this.required = required;
        return this;
    }

    public Integer getMaxCharacters() {
        return maxCharacters;
    }

    public StringFieldValidator setMaxCharacters(Integer maxCharacters) {
        this.maxCharacters = maxCharacters;
        return this;
    }

    public Integer getMinCharacters() {
        return minCharacters;
    }

    public StringFieldValidator setMinCharacters(Integer minCharacters) {
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
    public StringFieldValidator setValidRegex(String validRegex) {
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
    public StringFieldValidator setRestricted(boolean restricted) {
        this.restricted = restricted;
        return this;
    }

    public String getRequiredErrorMsg() {
        return requiredErrorMsg;
    }

    public StringFieldValidator setRequiredErrorMsg(String requiredErrorMsg) {
        this.requiredErrorMsg = requiredErrorMsg;
        return this;
    }

    public String getMaxCharactersErrorMsg() {
        return maxCharactersErrorMsg;
    }

    public StringFieldValidator setMaxCharactersErrorMsg(String maxCharactersErrorMsg) {
        this.maxCharactersErrorMsg = maxCharactersErrorMsg;
        return this;
    }

    public String getMinCharactersErrorMsg() {
        return minCharactersErrorMsg;
    }

    public StringFieldValidator setMinCharactersErrorMsg(String minCharactersErrorMsg) {
        this.minCharactersErrorMsg = minCharactersErrorMsg;
        return this;
    }

    public boolean isEmailFormat() {
        return emailFormat;
    }

    public StringFieldValidator setEmailFormat(boolean emailFormat) {
        this.emailFormat = emailFormat;
        this.validRegex = REGEXP_EMAIL;
        return this;
    }

    public String getEmailFormatErrorMsg() {
        return emailFormatErrorMsg;
    }

    public StringFieldValidator setEmailFormatErrorMsg(String emailFormatErrorMsg) {
        this.emailFormatErrorMsg = emailFormatErrorMsg;
        return this;
    }

    public String getValidPattern() {
        return validPattern;
    }

    public StringFieldValidator setValidPattern(String validPattern) {
        this.validPattern = validPattern;
        return this;
    }

    public String getValidRegexErrorMsg() {
        return validRegexErrorMsg;
    }

    public StringFieldValidator setValidRegexErrorMsg(String validRegexErrorMsg) {
        this.validRegexErrorMsg = validRegexErrorMsg;
        return this;
    }

    public String getValidPatternErrorMsg() {
        return validPatternErrorMsg;
    }

    public StringFieldValidator setValidPatternErrorMsg(String validPatternErrorMsg) {
        this.validPatternErrorMsg = validPatternErrorMsg;
        return this;
    }

    public String getTypeField() {
        return typeField;
    }

    public StringFieldValidator setTypeField(String typeField) {
        this.typeField = typeField;
        return this;
    }

    public StringFieldValidator setFieldName(String fieldName) { this.fieldName = fieldName; return this;}
}
