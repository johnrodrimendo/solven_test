package com.affirm.client.model.form;

import com.affirm.common.util.*;
import org.apache.log4j.Logger;

import java.io.Serializable;

/**
 * Created by stbn on 07/10/16.
 */
public class EarlyAccessForm extends FormGeneric implements Serializable {

    private static final Logger logger = Logger.getLogger(UserRegisterForm.class);

    private Integer docTypeSooner;
    private String docNumberSooner;
    private String emailSooner;

    public EarlyAccessForm() {
        this.setValidator(new EarlyAccessForm.EarlyAccessFormValidator());
    }

    public class EarlyAccessFormValidator extends FormValidator implements Serializable {

        public IntegerFieldValidator docTypeSooner;
        public StringFieldValidator docNumberSooner;
        public StringFieldValidator emailSooner;

        public EarlyAccessFormValidator() {
            addValidator(docTypeSooner = new IntegerFieldValidator(ValidatorUtil.DOC_TYPE_ID).setRequired(true));
            addValidator(docNumberSooner = new StringFieldValidator(ValidatorUtil.DOC_NUMBER_DNI).setRequired(true));
            addValidator(emailSooner = new StringFieldValidator(ValidatorUtil.EMAIL).setRequired(true));
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
            return EarlyAccessFormValidator.this;
        }
    }

    public static Logger getLogger() {
        return logger;
    }

    public Integer getDocTypeSooner() {
        return docTypeSooner;
    }

    public void setDocTypeSooner(Integer docTypeSooner) {
        this.docTypeSooner = docTypeSooner;
    }

    public String getDocNumberSooner() {
        return docNumberSooner;
    }

    public void setDocNumberSooner(String docNumberSooner) {
        this.docNumberSooner = docNumberSooner;
    }

    public String getEmailSooner() {
        return emailSooner;
    }

    public void setEmailSooner(String emailSooner) {
        this.emailSooner = emailSooner;
    }
}
