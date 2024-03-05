package com.affirm.common.model.form;

import com.affirm.common.model.catalog.CountryParam;
import com.affirm.common.model.catalog.PhoneType;
import com.affirm.common.util.FormGeneric;
import com.affirm.common.util.FormValidator;
import com.affirm.common.util.StringFieldValidator;
import com.affirm.common.util.ValidatorUtil;

import java.io.Serializable;

public class Question101Form extends FormGeneric implements Serializable {

    private String typePhone;
    private String phoneCode;
    private String phoneNumber;
    private Integer countryId;

    public Question101Form() {
        this.setValidator(new Question101Form.Validator());
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

    public class Validator extends FormValidator implements Serializable {

        public StringFieldValidator typePhone;
        public StringFieldValidator phoneCode;
        public StringFieldValidator phoneNumber;

        public Validator() {
            addValidator(typePhone = new StringFieldValidator(ValidatorUtil.PHONE_TYPE));
            addValidator(phoneCode = new StringFieldValidator().setFieldName("Código"));
            addValidator(phoneNumber = new StringFieldValidator(ValidatorUtil.PHONE_OR_CELLPHONE).setFieldName("Teléfono"));
        }

        @Override
        protected void setDynamicValidations() {
            if (Question101Form.this.countryId == CountryParam.COUNTRY_PERU) {
                if (Question101Form.this.typePhone.equals(PhoneType.LANDLINE)) {
                    phoneCode.setRequired(true);
                    phoneNumber.setMaxCharacters(10 - Question101Form.this.phoneCode.length());
                } else {
                    phoneCode.setRequired(false);
                    phoneNumber.setMaxCharacters(10).setMinCharacters(10);
                }
            } else if (Question101Form.this.countryId == CountryParam.COUNTRY_ARGENTINA) {
                phoneNumber.setMaxCharacters(11 - Question101Form.this.phoneCode.length());
                phoneNumber.setMinCharacters(11 - Question101Form.this.phoneCode.length());
            }
        }

        public void configValidator(Integer countryId) {
            Question101Form.this.countryId = countryId;
            if (CountryParam.COUNTRY_ARGENTINA == countryId) {
                phoneCode.update(ValidatorUtil.ARGENTINA_PHONE_AREA_CODE);
                phoneNumber.update(ValidatorUtil.CELLPHONE_NUMBER_ARGENTINA.setMinCharacters(9).setMaxCharacters(11));
            } else if (CountryParam.COUNTRY_PERU == countryId) {
                phoneCode.update(ValidatorUtil.PERU_PHONE_AREA_CODE);
                phoneNumber.update(ValidatorUtil.PHONE_OR_CELLPHONE);
            }
        }

        @Override
        protected Object getSubclass() {
            return this;
        }

        @Override
        protected Object getFormClass() {
            return Question101Form.this;
        }
    }

    public String getPhoneCode() {
        return phoneCode;
    }

    public void setPhoneCode(String phoneCode) {
        this.phoneCode = phoneCode;
    }
}