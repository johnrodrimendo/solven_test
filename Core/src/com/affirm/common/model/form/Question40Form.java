package com.affirm.common.model.form;

import com.affirm.common.util.*;

import java.io.Serializable;

public class Question40Form extends FormGeneric implements Serializable {

    private Double sales;
    private String hopeGrow;

    public Question40Form() {
        this.setValidator(new Question40Form.Validator());
    }

    public class Validator extends FormValidator implements Serializable {

        public DoubleFieldValidator sales;
        public StringFieldValidator hopeGrow;

        public Validator() {
            addValidator(sales = new DoubleFieldValidator(ValidatorUtil.OCUPATION_LAST_YEAR_SELLINGS));
            addValidator(hopeGrow = new StringFieldValidator(ValidatorUtil.HOPE_GROW));
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
            return Question40Form.this;
        }
    }

    public Double getSales() {
        return sales;
    }

    public void setSales(Double sales) {
        this.sales = sales;
    }

    public String getHopeGrow() { return hopeGrow; }

    public void setHopeGrow(String hopeGrow) { this.hopeGrow = hopeGrow; }
}