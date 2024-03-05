package com.affirm.client.model.form;

import com.affirm.common.util.FormGeneric;
import com.affirm.common.util.FormValidator;
import com.affirm.common.util.StringFieldValidator;
import com.affirm.common.util.ValidatorUtil;
import org.apache.log4j.Logger;

import java.io.Serializable;

/**
 * Created by renzodiaz on 5/16/17.
 */
public class ProcessContactForm extends FormGeneric implements Serializable {

    private static final Logger logger = Logger.getLogger(ContactForm.class);

    private String contactFullName;
    private String contactTelephone;
    private String contactEmail;
    private String contactSubject;
    private String contactMessage;

    public ProcessContactForm() {
        this.setValidator(new ProcessContactForm.ProcessContactFormValidator());
    }

    public static ProcessContactForm newInstance() {
        return new ProcessContactForm();
    }

    public class ProcessContactFormValidator extends FormValidator implements Serializable {

        public StringFieldValidator contactFullName;
        public StringFieldValidator contactTelephone;
        public StringFieldValidator contactEmail;
        public StringFieldValidator contactSubject;
        public StringFieldValidator contactMessage;


        public ProcessContactFormValidator() {
            addValidator(contactFullName = new StringFieldValidator(ValidatorUtil.FULLNAME).setRequired(true));
            addValidator(contactTelephone = new StringFieldValidator(ValidatorUtil.PHONE_OR_CELLPHONE).setRequired(false));
            addValidator(contactEmail = new StringFieldValidator(ValidatorUtil.EMAIL).setRequired(true));
            addValidator(contactSubject = new StringFieldValidator(ValidatorUtil.EMAIL_SUBJECT));
            addValidator(contactMessage = new StringFieldValidator(ValidatorUtil.EMAIL_MESSAGE).setRequired(true));
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
            return ProcessContactForm.this;
        }
    }

    public String getContactFullName() {
        return contactFullName;
    }

    public void setContactFullName(String contactFullName) {
        this.contactFullName = contactFullName;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getContactMessage() {
        return contactMessage;
    }

    public void setContactMessage(String contactMessage) {
        this.contactMessage = contactMessage;
    }

    public String getContactTelephone() {
        return contactTelephone;
    }

    public void setContactTelephone(String contactTelephone) {
        this.contactTelephone = contactTelephone;
    }

    public String getContactSubject() {
        return contactSubject;
    }

    public void setContactSubject(String contactSubject) {
        this.contactSubject = contactSubject;
    }
}
