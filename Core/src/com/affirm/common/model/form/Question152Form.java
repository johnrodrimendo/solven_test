package com.affirm.common.model.form;

import com.affirm.common.util.FormGeneric;
import com.affirm.common.util.FormValidator;
import com.affirm.common.util.IntegerFieldValidator;

import java.io.Serializable;

public class Question152Form extends FormGeneric implements Serializable {

    private Integer professionOcupation;
    private Integer profession;
    private Integer ocupation;

    public Question152Form() {
        this.setValidator(new Question152Form.Validator());
    }

    public class Validator extends FormValidator implements Serializable {

        public IntegerFieldValidator professionOcupation;
        public IntegerFieldValidator profession;
        public IntegerFieldValidator ocupation;

        public Validator() {
            addValidator(professionOcupation = new IntegerFieldValidator().setRequired(true));
            addValidator(profession = new IntegerFieldValidator().setRequired(true));
            addValidator(ocupation = new IntegerFieldValidator().setRequired(true));
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
            return Question152Form.this;
        }
    }

    public Integer getProfessionOcupation() {
        return professionOcupation;
    }

    public void setProfessionOcupation(Integer professionOcupation) {
        this.professionOcupation = professionOcupation;
    }

    public Integer getProfession() {
        return profession;
    }

    public void setProfession(Integer profession) {
        this.profession = profession;
    }

    public Integer getOcupation() {
        return ocupation;
    }

    public void setOcupation(Integer ocupation) {
        this.ocupation = ocupation;
    }
}
