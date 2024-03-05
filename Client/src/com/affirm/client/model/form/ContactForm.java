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
public class ContactForm extends FormGeneric implements Serializable {

    private static final Logger logger = Logger.getLogger(ContactForm.class);

    private String contactFullName;
    private String contactTelephone;
    private String contactEmail;
    private String contactSubject;
    private String contactMessage;

    public ContactForm() {
        this.setValidator(new ContactForm.ContactFormValidator());
    }

    public static ContactForm newInstance() {
        return new ContactForm();
    }

    public class ContactFormValidator extends FormValidator implements Serializable {

        public StringFieldValidator contactFullName;
        public StringFieldValidator contactTelephone;
        public StringFieldValidator contactEmail;
        public StringFieldValidator contactSubject;
        public StringFieldValidator contactMessage;


        public ContactFormValidator() {
            addValidator(contactFullName = new StringFieldValidator(ValidatorUtil.FULLNAME));
            addValidator(contactTelephone = new StringFieldValidator(ValidatorUtil.PHONE_OR_CELLPHONE).setRequired(false));
            addValidator(contactEmail = new StringFieldValidator(ValidatorUtil.EMAIL));
            addValidator(contactSubject = new StringFieldValidator(ValidatorUtil.EMAIL_SUBJECT));
            addValidator(contactMessage = new StringFieldValidator(ValidatorUtil.EMAIL_MESSAGE));
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
            return ContactForm.this;
        }
    }

    public String getContactFullName() {
        return contactFullName;
    }

    public void setContactFullName(String contactFullName) {
        this.contactFullName = contactFullName;
    }

    public String getContactTelephone() {
        return contactTelephone;
    }

    public void setContactTelephone(String contactTelephone) {
        this.contactTelephone = contactTelephone;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getContactSubject() {
        return contactSubject;
    }

    public void setContactSubject(String contactSubject) {
        this.contactSubject = contactSubject;
    }

    public String getContactMessage() {
        return contactMessage;
    }

    public void setContactMessage(String contactMessage) {
        this.contactMessage = contactMessage;
    }
}
