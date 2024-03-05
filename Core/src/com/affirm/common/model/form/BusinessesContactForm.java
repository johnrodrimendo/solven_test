package com.affirm.common.model.form;

import com.affirm.common.util.FormGeneric;
import com.affirm.common.util.FormValidator;
import com.affirm.common.util.StringFieldValidator;
import com.affirm.common.util.ValidatorUtil;

import java.io.Serializable;

public class BusinessesContactForm extends FormGeneric implements Serializable {

    private String name;
    private String email;
    private String phone;
    private String company;
    private String source;
    private String size;
    private String position;

    public BusinessesContactForm() {
        this.setValidator(new BusinessesContactForm.Validator());
    }

    public class Validator extends FormValidator implements Serializable {

        public StringFieldValidator source;
        public StringFieldValidator name;
        public StringFieldValidator company;
        public StringFieldValidator size;
        public StringFieldValidator position;
        public StringFieldValidator email;
        public StringFieldValidator phone;

        public Validator() {
            addValidator(source = new StringFieldValidator().setRequired(true).setValidRegex(StringFieldValidator.REGEXP_ALPHANUMERIC_SPEC_CHARS).setFieldName("¿Cómo nos conociste?"));
            addValidator(name = new StringFieldValidator().setRequired(true).setFieldName("nombre"));
            addValidator(company = new StringFieldValidator().setRequired(true).setFieldName("empresa"));
            addValidator(size = new StringFieldValidator().setRequired(true).setValidRegex(StringFieldValidator.REGEXP_ALPHANUMERIC_SPEC_CHARS).setFieldName("tamaño"));
            addValidator(position = new StringFieldValidator().setRequired(true).setFieldName("cargo"));
            addValidator(email = new StringFieldValidator(ValidatorUtil.EMAIL).setRequired(true).setFieldName("correo electrónico"));
            addValidator(phone = new StringFieldValidator(ValidatorUtil.PHONE_OR_CELLPHONE).setRequired(true).setFieldName("teléfono").setMaxCharacters(9));
        }

        @Override
        protected void setDynamicValidations() {}

        @Override
        protected Object getSubclass() {
            return this;
        }

        @Override
        protected Object getFormClass() {
            return BusinessesContactForm.this;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }
}