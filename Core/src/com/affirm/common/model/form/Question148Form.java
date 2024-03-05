package com.affirm.common.model.form;

import com.affirm.common.util.FormGeneric;
import com.affirm.common.util.FormValidator;
import com.affirm.common.util.IntegerFieldValidator;

import java.io.Serializable;

public class Question148Form extends FormGeneric implements Serializable {

    private Integer area;
    private Integer occupation;

    public Question148Form() {
        this.setValidator(new Question148Form.Validator());
    }

    public class Validator extends FormValidator implements Serializable {

        public IntegerFieldValidator area;
        public IntegerFieldValidator occupation;

        public Validator() {
            addValidator(area = new IntegerFieldValidator().setRequired(true));
            addValidator(occupation = new IntegerFieldValidator().setRequired(true));
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
            return Question148Form.this;
        }
    }

    public Integer getArea() {
        return area;
    }

    public void setArea(Integer area) {
        this.area = area;
    }

    public Integer getOccupation() {
        return occupation;
    }

    public void setOccupation(Integer occupation) {
        this.occupation = occupation;
    }

}
