package com.affirm.common.model.form;

import com.affirm.common.model.catalog.CountryParam;
import com.affirm.common.util.FormGeneric;
import com.affirm.common.util.FormValidator;
import com.affirm.common.util.StringFieldValidator;
import com.affirm.common.util.ValidatorUtil;

import java.io.Serializable;

public class Question38Form extends FormGeneric implements Serializable {

    private String ruc;
    private boolean notLegal;
    private String regime;

    public Question38Form() {
        this.setValidator(new Question38Form.Validator());
    }

    public class Validator extends FormValidator implements Serializable {

        public StringFieldValidator ruc;
        public StringFieldValidator regime;

        public Validator() {
            addValidator(ruc = new StringFieldValidator(ValidatorUtil.RUC));
            addValidator(regime = new StringFieldValidator().setRequired(true));
        }

        @Override
        protected void setDynamicValidations() {
            if(notLegal) {
                ruc.setRequired(false);
                regime.setRequired(false);
            }
        }

        @Override
        protected Object getSubclass() {
            return this;
        }

        @Override
        protected Object getFormClass() {
            return Question38Form.this;
        }

        public void configValidator(Integer countryId) {
            switch (countryId) {
                case CountryParam.COUNTRY_COLOMBIA: {
                    regime.setRequired(false);
                    ruc.update(ValidatorUtil.NIT);
                    break;
                }
                case CountryParam.COUNTRY_ARGENTINA: {
                    regime.setRequired(false);
                    ruc.update(ValidatorUtil.CUIT_COMPANY);
                    break;
                }
                case CountryParam.COUNTRY_PERU: {
                    regime.setRequired(true);
                    ruc.update(ValidatorUtil.RUC);
                    break;
                }
            }
        }
    }

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public boolean isNotLegal() { return notLegal; }

    public void setNotLegal(boolean notLegal) { this.notLegal = notLegal; }

    public String getRegime() {
        return regime;
    }

    public void setRegime(String regime) {
        this.regime = regime;
    }
}