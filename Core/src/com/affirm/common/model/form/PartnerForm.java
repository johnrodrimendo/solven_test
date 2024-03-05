package com.affirm.common.model.form;

import com.affirm.common.model.catalog.CountryParam;
import com.affirm.common.util.FormGeneric;
import com.affirm.common.util.FormValidator;
import com.affirm.common.util.StringFieldValidator;
import com.affirm.common.util.ValidatorUtil;

public class PartnerForm extends FormGeneric {

    private String fullname;
    private String company;
    private String position;
    private String email;
    private String code;
    private String phone;
    private Integer countryId;

    public PartnerForm() {
        this.setValidator(new PartnerForm.Validator());
    }

    public class Validator extends FormValidator {

        public StringFieldValidator fullname;
        public StringFieldValidator company;
        public StringFieldValidator position;
        public StringFieldValidator email;
        public StringFieldValidator code;
        public StringFieldValidator phone;

        public Validator() {
            addValidator(fullname = new StringFieldValidator(ValidatorUtil.FULLNAME));
            addValidator(company = new StringFieldValidator().setRequired(true).setMaxCharacters(150));
            addValidator(position = new StringFieldValidator().setRequired(false).setMaxCharacters(100));
            addValidator(email = new StringFieldValidator(ValidatorUtil.EMAIL));
            addValidator(code = new StringFieldValidator(ValidatorUtil.COUNTRY_CODE));
            addValidator(phone = new StringFieldValidator(ValidatorUtil.PHONE_OR_CELLPHONE));
        }

        @Override
        protected void setDynamicValidations() {
        }

        public void configValidator(Integer countryId) {
            if (countryId == CountryParam.COUNTRY_ARGENTINA) {
                phone.update(ValidatorUtil.CELLPHONE_NUMBER_ARGENTINA.setMinCharacters(9).setMaxCharacters(11));
                code.update(ValidatorUtil.ARGENTINA_PHONE_AREA_CODE);
            } else if (countryId == CountryParam.COUNTRY_PERU) {
                code.setRequired(false);
            }
        }

        @Override
        protected Object getSubclass() {
            return this;
        }

        @Override
        protected Object getFormClass() {
            return PartnerForm.this;
        }
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getCountryId() {
        return countryId;
    }

    public void setCountryId(Integer countryId) {
        this.countryId = countryId;
    }
}
