/**
 *
 */
package com.affirm.common.util;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author jrodriguez
 * <p>
 * The child class should only have the fields to be validated.
 */
public abstract class FormValidator implements Serializable {

    private static final Logger logger = Logger.getLogger(FormValidator.class);

    private boolean hasErrors = false;
    private transient List<FieldValidator> validators = new ArrayList<>();

    public FormValidator() {
    }

    /**
     * Sets the validations for the FieldValidators properties that depends on other field's values
     */
    protected abstract void setDynamicValidations();

    /**
     * Gets the subclass
     *
     * @return
     */
    protected abstract Object getSubclass();

    /**
     * Gets the Form object that is being validated by this
     *
     * @return
     */
    protected abstract Object getFormClass();

    /**
     * Set the value of all the declared fields in the FormValidator that extends FieldValidator.
     * The values setted are brought by the reflection of the Form class
     */
    private void setValues() throws Exception {
        JSONObject json = new JSONObject();
        Field[] fields = getSubclass().getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {

            //Only get the fields that extends of FieldValidator
            if (FieldValidator.class.isAssignableFrom(fields[i].getType())) {
                fields[i].setAccessible(true);

                // Get the form field with the same name
                String fieldName = fields[i].getName();
                Object fieldValue = getFormClass().getClass().getMethod("get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1)).invoke(getFormClass());

                // Get the Field Validator Instance
                FieldValidator fieldValidatorInstance = (FieldValidator) fields[i].get(getSubclass());

                // Call the setValue method
                fieldValidatorInstance.getClass().getSuperclass().getMethod("setValue", Object.class).invoke(fieldValidatorInstance, fieldValue);
            }
        }
    }

    /**
     * Generate the json wich will be consumed by the client side for the
     * validations
     *
     * @param locale
     * @return
     */
    public String toJson(Locale locale) {
        try {
            JSONObject json = new JSONObject();
            Field[] fields = getSubclass().getClass().getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                //Only convert to json the FieldValidatos fields
                if (FieldValidator.class.isAssignableFrom(fields[i].getType())) {
                    fields[i].setAccessible(true);
                    FieldValidator value = (FieldValidator) fields[i].get(getSubclass());
                    if (value != null) {
                        String name = fields[i].getName();
                        json.put(name, value.toJson(locale));
                    }
                }
            }
            return json.toString();
        } catch (Exception ex) {
            logger.error("Error creating json of validator", ex);
        }
        return null;
    }

    /**
     * Generate the json only containing the erros messages of the fields
     *
     * @return
     */
    public String getErrorsJson() {
        try {
            JSONObject json = new JSONObject();
            Field[] fields = getSubclass().getClass().getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                //Only convert to json the FieldValidatos fields
                if (FieldValidator.class.isAssignableFrom(fields[i].getType())) {
                    fields[i].setAccessible(true);
                    FieldValidator value = (FieldValidator) fields[i].get(getSubclass());
                    if (value != null && value.hasErrors) {
                        String name = fields[i].getName();
                        json.put(name, value.getErrors());
                    }
                }
            }
            return json.toString();
        } catch (Exception ex) {
            logger.error("Error creating json of validator", ex);
        }
        return null;
    }

    /**
     * Resets all the FiledValidators and calls its validations method
     *
     * @param locale Locale for the messgae internacionalization
     */
    public void validate(Locale locale) throws Exception {
//		validators = new ArrayList<>();
        hasErrors = false;
        setValues();
        setDynamicValidations();

        for (FieldValidator validator : validators) {
            validator.reset();
            if (!validator.validate(locale)) {
                hasErrors = true;
            }
        }

        if (hasErrors) {
            logger.warn("[" + this.getFormClass().getClass().getSimpleName() + "]Form has the following errors: " + getErrorsJson());
        }
    }

    public void addValidator(FieldValidator validator) {
        validator.setFormValidator(this);
        validators.add(validator);
    }

    public boolean isHasErrors() {
        return hasErrors;
    }

    public void updateHasErrors() {
        hasErrors = false;
        for (FieldValidator validator : validators) {
            if (validator.hasErrors) {
                hasErrors = true;
                break;
            }
        }
    }

}
