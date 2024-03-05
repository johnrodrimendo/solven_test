package com.affirm.common.model.form;

import com.affirm.common.util.*;

import java.io.Serializable;

public class Question41Form extends FormGeneric implements Serializable {

    private Double exerciseOutcome;
    private String hopeGrow;

    public Question41Form() {
        this.setValidator(new Question41Form.Validator());
    }

    public class Validator extends FormValidator implements Serializable {

        public DoubleFieldValidator exerciseOutcome;
        public StringFieldValidator hopeGrow;

        public Validator() {
            addValidator(exerciseOutcome = new DoubleFieldValidator(ValidatorUtil.OCUPATION_LAST_YEAR_SELLINGS));
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
            return Question41Form.this;
        }
    }

    public Double getExerciseOutcome() {
        return exerciseOutcome;
    }

    public void setExerciseOutcome(Double exerciseOutcome) {
        this.exerciseOutcome = exerciseOutcome;
    }

    public String getHopeGrow() { return hopeGrow; }

    public void setHopeGrow(String hopeGrow) { this.hopeGrow = hopeGrow; }
}