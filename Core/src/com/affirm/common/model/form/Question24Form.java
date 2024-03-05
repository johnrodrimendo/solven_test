package com.affirm.common.model.form;

import com.affirm.common.util.FormGeneric;
import com.affirm.common.util.FormValidator;
import com.affirm.common.util.IntegerFieldValidator;

import java.io.Serializable;

public class Question24Form extends FormGeneric implements Serializable {

    private Integer studyLevel;

    public Question24Form() {
        this.setValidator(new Question24Form.Validator());
    }

    public class Validator extends FormValidator implements Serializable {

        public IntegerFieldValidator studyLevel;

        public Validator() {
            addValidator(studyLevel = new IntegerFieldValidator().setRequired(true));
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
            return Question24Form.this;
        }
    }

    public Integer getStudyLevel() {
        return studyLevel;
    }

    public void setStudyLevel(Integer studyLevel) {
        this.studyLevel = studyLevel;
    }

}
