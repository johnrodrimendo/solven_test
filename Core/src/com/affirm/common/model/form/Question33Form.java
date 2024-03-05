package com.affirm.common.model.form;

import com.affirm.common.model.catalog.CountryParam;
import com.affirm.common.util.*;

import java.io.Serializable;

public class Question33Form extends FormGeneric implements Serializable {

    private String ruc;
    private Boolean haventRuc;

    public Question33Form() {
        this.setValidator(new Question33Form.Validator());
    }

    public class Validator extends FormValidator implements Serializable {

        public StringFieldValidator ruc;
        public BooleanFieldValidator haventRuc;

        public Validator() {
            addValidator(ruc = new StringFieldValidator(ValidatorUtil.RUC));
            addValidator(haventRuc = new BooleanFieldValidator().setRequired(true));
        }

        @Override
        protected void setDynamicValidations() {

            if (Question33Form.this.haventRuc) {
                ruc.setRequired(false);
            } else {
                ruc.setRequired(true);
            }

        }

        @Override
        protected Object getSubclass() {
            return this;
        }

        @Override
        protected Object getFormClass() {
            return Question33Form.this;
        }

        public void configValidator(Integer countryId) {
            if (countryId == CountryParam.COUNTRY_COLOMBIA) {
                ruc.update(ValidatorUtil.NIT);
            }
        }
    }

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public Boolean getHaventRuc() {
        return haventRuc;
    }

    public void setHaventRuc(Boolean haventRuc) {
        this.haventRuc = haventRuc;
    }

}