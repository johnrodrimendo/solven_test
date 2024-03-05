package com.affirm.common.model.form;

import com.affirm.common.util.FormGeneric;
import com.affirm.common.util.FormValidator;
import com.affirm.common.util.IntegerFieldValidator;

import java.io.Serializable;

public class Question107Form extends FormGeneric implements Serializable {

    private Integer productCategory;

    public Question107Form() {
        this.setValidator(new Question107Form.Validator());
    }

    public Integer getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(Integer productCategory) {
        this.productCategory = productCategory;
    }

    public class Validator extends FormValidator implements Serializable {

        public IntegerFieldValidator productCategory;

        public Validator() {
            addValidator(productCategory = new IntegerFieldValidator().setRequired(true));
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
            return Question107Form.this;
        }
    }


}