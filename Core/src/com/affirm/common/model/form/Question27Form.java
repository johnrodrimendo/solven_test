package com.affirm.common.model.form;

import com.affirm.common.model.catalog.CountryParam;
import com.affirm.common.util.FormGeneric;
import com.affirm.common.util.FormValidator;
import com.affirm.common.util.StringFieldValidator;
import com.affirm.common.util.ValidatorUtil;

import java.io.Serializable;

public class Question27Form extends FormGeneric implements Serializable {

    private String ruc;

    public Question27Form() {
        this.setValidator(new Question27Form.Validator());
    }

    public class Validator extends FormValidator implements Serializable {

        public StringFieldValidator ruc;

        public Validator() {
            addValidator(ruc = new StringFieldValidator(ValidatorUtil.RUC));
        }

        @Override
        protected void setDynamicValidations() {

        }

        public void configValidator(Integer countryId) {
            switch (countryId) {
                case CountryParam.COUNTRY_COLOMBIA: {
                    ruc.update(ValidatorUtil.NIT);
                    break;
                }
                case CountryParam.COUNTRY_ARGENTINA: {
                    ruc.update(ValidatorUtil.CUIT_COMPANY);
                    break;
                }
                case CountryParam.COUNTRY_PERU: {
                    ruc.update(ValidatorUtil.RUC);
                    break;
                }
            }
        }

        @Override
        protected Object getSubclass() {
            return this;
        }

        @Override
        protected Object getFormClass() {
            return Question27Form.this;
        }
    }

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }
}