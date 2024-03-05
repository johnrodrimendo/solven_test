package com.affirm.common.model.form;

import com.affirm.common.model.catalog.CountryParam;
import com.affirm.common.model.catalog.PhoneType;
import com.affirm.common.util.FormGeneric;
import com.affirm.common.util.FormValidator;
import com.affirm.common.util.StringFieldValidator;
import com.affirm.common.util.ValidatorUtil;

import java.io.Serializable;

public class Question151Form extends FormGeneric implements Serializable {

    private String name;
    private String typePhone;
    private String phoneNumber;

    public Question151Form() {
        this.setValidator(new Question151Form.Validator());
    }

    public String getTypePhone() {
        return typePhone;
    }

    public void setTypePhone(String typePhone) {
        this.typePhone = typePhone;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public class Validator extends FormValidator implements Serializable {

        public StringFieldValidator typePhone;
        public StringFieldValidator phoneNumber;
        public StringFieldValidator name;

        public Validator() {
            addValidator(typePhone = new StringFieldValidator(ValidatorUtil.PHONE_TYPE));
            addValidator(phoneNumber = new StringFieldValidator(ValidatorUtil.PHONE_OR_CELLPHONE).setMaxCharacters(10).setMinCharacters(7).setFieldName("Teléfono"));
            addValidator(name = new StringFieldValidator(ValidatorUtil.COMPANY_NAME).setFieldName("Nombre"));
        }

        @Override
        protected void setDynamicValidations() {
            if (Question151Form.this.typePhone.equals(PhoneType.LANDLINE)) {
                phoneNumber.setMaxCharacters(10).setMinCharacters(7);
            } else {
                phoneNumber.setMaxCharacters(10).setMinCharacters(7);
            }
        }

        @Override
        protected Object getSubclass() {
            return this;
        }

        @Override
        protected Object getFormClass() {
            return Question151Form.this;
        }
    }
}