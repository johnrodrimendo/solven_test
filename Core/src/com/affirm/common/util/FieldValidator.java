/**
 *
 */
package com.affirm.common.util;

import org.json.JSONObject;
import org.springframework.context.MessageSource;

import java.io.Serializable;
import java.util.Locale;

/**
 * @author jrodriguez
 *         <p>
 *         Class for the implementations of the validator for fields inside a
 *         formvalidator.
 */
public abstract class FieldValidator<T> implements Serializable {

    protected T value;
    protected boolean hasErrors = false;
    protected String errors = "";
    private transient MessageSource messageSource;
    protected String type;
    protected String fieldName;
    protected String requiredDefaultMsg = "validation.string.requiredNoFieldName";
    protected FormValidator formValidator;

    protected FieldValidator(Class classType) {
        this.type = classType.getSimpleName();
    }

    /**
     * Validates all the validations that the child could have set. <br>
     * Set the fields hasErrors and error if errors are found
     */
    public abstract boolean validate(Locale locale);

    /**
     * Updates the validations params with the ones at the validator
     */
    public abstract void update(FieldValidator validator);

    /**
     * Convert the object to json for the client side to process, but puttin the
     * error message instead of the key
     *
     * @param locale
     * @return
     */
    public abstract JSONObject toJson(Locale locale);

    /**
     * Sets the error properties to its defaults
     */
    public void reset() {
        hasErrors = false;
        errors = "";
    }

    /**
     * Sets the error and put true the haserror
     *
     * @param error
     */
    public void setError(String error) {
        this.hasErrors = true;
        this.errors = error;
        if(formValidator != null)
            formValidator.updateHasErrors();
    }

    public boolean isHasErrors() {
        return hasErrors;
    }

    public String getErrors() {
        return errors;
    }

    public MessageSource getMessageSource() {
        if (messageSource == null) {
            messageSource = SpringUtil.getMEssageSource();
        }
        return messageSource;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    protected String getErrorRequiredMessage(Locale locale, String requiredErrorMsg)
    {
        if (fieldName == null) {
            return getMessageSource().getMessage(requiredErrorMsg, new Object[]{""}, locale);
        } else {
            return getMessageSource().getMessage(requiredErrorMsg, new Object[]{fieldName}, locale);
        }
    }

    public void setFormValidator(FormValidator formValidator) {
        this.formValidator = formValidator;
    }

    @Override
    public String toString() {
        return "FieldValidator{" +
                "value=" + value +
                ", hasErrors=" + hasErrors +
                ", errors='" + errors + '\'' +
                ", messageSource=" + messageSource +
                ", type='" + type + '\'' +
                '}';
    }
}
