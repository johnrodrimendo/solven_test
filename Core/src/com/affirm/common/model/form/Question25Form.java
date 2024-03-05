package com.affirm.common.model.form;

import com.affirm.common.util.FormGeneric;
import com.affirm.common.util.FormValidator;
import com.affirm.common.util.IntegerFieldValidator;

import java.io.Serializable;

public class Question25Form extends FormGeneric implements Serializable {

    private Integer profession;

    public Question25Form() {
        this.setValidator(new Question25Form.Validator());
    }

    public class Validator extends FormValidator implements Serializable {

        public IntegerFieldValidator profession;

        public Validator() {
            addValidator(profession = new IntegerFieldValidator().setRequired(true));
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
            return Question25Form.this;
        }
    }

    public Integer getProfession() {
        return profession;
    }

    public void setProfession(Integer profession) {
        this.profession = profession;
    }
}