package com.affirm.common.model.form;

import com.affirm.common.model.catalog.CountryParam;
import com.affirm.common.util.FormGeneric;
import com.affirm.common.util.FormValidator;
import com.affirm.common.util.IntegerFieldValidator;
import com.affirm.common.util.ValidatorUtil;

import java.io.Serializable;

public class Question47Form extends FormGeneric implements Serializable {

    private Integer rent;
    private Integer[] belongings;

    public Question47Form() {
        this.setValidator(new Question47Form.Validator());
    }

    public class Validator extends FormValidator implements Serializable {

        public IntegerFieldValidator rent;

        public Validator() {
            addValidator(rent = new IntegerFieldValidator(ValidatorUtil.NET_INCOME));
        }

        @Override
        protected void setDynamicValidations() {
        }

        public void configValidator(Integer countryId) {
            if (CountryParam.COUNTRY_ARGENTINA == countryId) {
                rent.update(ValidatorUtil.NET_INCOME.setMaxValue(999999));
            } else if (CountryParam.COUNTRY_PERU == countryId) {
                rent.update(ValidatorUtil.NET_INCOME);
            }
        }

        @Override
        protected Object getSubclass() {
            return this;
        }

        @Override
        protected Object getFormClass() {
            return Question47Form.this;
        }
    }

    public Integer getRent() {
        return rent;
    }

    public void setRent(Integer rent) {
        this.rent = rent;
    }

    public Integer[] getBelongings() {
        return belongings;
    }

    public void setBelongings(Integer[] belongings) {
        this.belongings = belongings;
    }
}