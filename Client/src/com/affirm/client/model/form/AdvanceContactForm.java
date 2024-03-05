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
public class AdvanceContactForm extends FormGeneric implements Serializable {

    private static final Logger logger = Logger.getLogger(AdvanceContactForm.class);

    private String contactFullName;
    private String contactCompany;
    private String contactEmail;
    private String contactTelephone;
    private String annexTelephone;
    private String contactNumberEmployee;

    public AdvanceContactForm() {
        this.setValidator(new AdvanceContactForm.ContactFormValidator());
    }

    public static AdvanceContactForm newInstance() {
        return new AdvanceContactForm();
    }

    public class ContactFormValidator extends FormValidator implements Serializable {

        public StringFieldValidator contactFullName;
        public StringFieldValidator contactCompany;
        public StringFieldValidator contactEmail;
        public StringFieldValidator contactTelephone;
        public StringFieldValidator annexTelephone;
        public StringFieldValidator contactNumberEmployee;


        public ContactFormValidator() {
            addValidator(contactFullName = new StringFieldValidator(ValidatorUtil.FULLNAME).setMaxCharacters(30));
            addValidator(contactCompany = new StringFieldValidator(ValidatorUtil.COMPANY_NAME).setMaxCharacters(30));
            addValidator(contactEmail = new StringFieldValidator(ValidatorUtil.EMAIL));
            addValidator(contactTelephone = new StringFieldValidator(ValidatorUtil.PHONE_OR_CELLPHONE).setRequired(false));
            addValidator(annexTelephone = new StringFieldValidator(ValidatorUtil.ANNEX_PHONE).setRequired(false));
            addValidator(contactNumberEmployee = new StringFieldValidator().setRequired(true).setValidRegex(".*"));
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
            return AdvanceContactForm.this;
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

    public String getContactNumberEmployee() {
        return contactNumberEmployee;
    }

    public void setContactNumberEmployee(String contactNumberEmployee) {
        this.contactNumberEmployee = contactNumberEmployee;
    }

    public String getContactCompany() {
        return contactCompany;
    }

    public void setContactCompany(String contactCompany) {
        this.contactCompany = contactCompany;
    }


    public String getAnnexTelephone() {
        return annexTelephone;
    }

    public void setAnnexTelephone(String annexTelephone) {
        this.annexTelephone = annexTelephone;
    }
}
