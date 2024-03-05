package com.affirm.common.model.form;

import com.affirm.common.util.*;

import java.io.Serializable;

public class Question165Form extends FormGeneric implements Serializable {

    private Integer product;

    public Question165Form() {
        this.setValidator(new Question165Form.Validator());
    }

    public class Validator extends FormValidator implements Serializable {

        public IntegerFieldValidator product;

        public Validator() {
            addValidator(product = new IntegerFieldValidator().setRequired(true).setFieldName("Producto"));
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
            return Question165Form.this;
        }
    }

    public Integer getProduct() {
        return product;
    }

    public void setProduct(Integer product) {
        this.product = product;
    }
}