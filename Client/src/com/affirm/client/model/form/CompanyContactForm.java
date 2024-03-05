package com.affirm.client.model.form;

import com.affirm.common.util.FormGeneric;
import com.affirm.common.util.FormValidator;
import com.affirm.common.util.StringFieldValidator;
import com.affirm.common.util.ValidatorUtil;
import org.apache.log4j.Logger;

import java.io.Serializable;

/**
 * Created by stbn on 07/10/16.
 */
public class CompanyContactForm extends FormGeneric implements Serializable {

    private static final Logger logger = Logger.getLogger(CompanyContactForm.class);

    private String contactFullName;
    private String email;
    private String ocupation;
    private String reason;
    private String message;
    private String phoneNumber;

    public CompanyContactForm() {
        this.setValidator(new CompanyContactForm.Validator());
    }

    public static CompanyContactForm newInstance() {
        return new CompanyContactForm();
    }

    public class Validator extends FormValidator implements Serializable {

        public StringFieldValidator contactFullName;
        public StringFieldValidator email;
        public StringFieldValidator ocupation;
        public StringFieldValidator reason;
        public StringFieldValidator message;
        public StringFieldValidator phoneNumber;

        public Validator() {
            addValidator(contactFullName = new StringFieldValidator(ValidatorUtil.FULLNAME));
            addValidator(email = new StringFieldValidator(ValidatorUtil.EMAIL));
            addValidator(ocupation = new StringFieldValidator().setRequired(true).setRestricted(true).setMaxCharacters(100));
            addValidator(reason = new StringFieldValidator().setRequired(true).setRestricted(true).setMaxCharacters(100));
            addValidator(message = new StringFieldValidator(ValidatorUtil.EMAIL_MESSAGE));
            addValidator(phoneNumber = new StringFieldValidator(ValidatorUtil.PHONE_OR_CELLPHONE));
        }

        @Override
        protected void setDynamicValidations() {
        }

        @Override
        protected Object getSubclass() {
            return this;
        }

        @Override
        protected Object getFormClass() {
            return CompanyContactForm.this;
        }
    }

    public String getContactFullName() {
        return contactFullName;
    }

    public void setContactFullName(String contactFullName) {
        this.contactFullName = contactFullName;
    }

    public String getOcupation() {
        return ocupation;
    }

    public void setOcupation(String ocupation) {
        this.ocupation = ocupation;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
