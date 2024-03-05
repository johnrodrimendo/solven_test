package com.affirm.common.model.form;

import com.affirm.common.util.CharFieldValidator;
import com.affirm.common.util.FormGeneric;
import com.affirm.common.util.FormValidator;

import java.io.Serializable;

public class Question133Form extends FormGeneric implements Serializable {

    private Character place;

    public Question133Form() {
        this.setValidator(new Question133Form.Validator());
    }

    public class Validator extends FormValidator implements Serializable {

        public CharFieldValidator place;

        public Validator() {
            addValidator(place = new CharFieldValidator().setRequired(true));
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
            return Question133Form.this;
        }
    }

    public Character getPlace() {
        return place;
    }

    public void setPlace(Character place) {
        this.place = place;
    }
}