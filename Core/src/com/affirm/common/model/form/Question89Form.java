package com.affirm.common.model.form;

import com.affirm.common.util.FormGeneric;
import com.affirm.common.util.FormValidator;
import com.affirm.common.util.StringFieldValidator;
import com.affirm.common.util.ValidatorUtil;

import java.io.Serializable;

public class Question89Form extends FormGeneric implements Serializable {

    private String startDate;

    public Question89Form() {
        this.setValidator(new Question89Form.Validator());
    }

    public String getMonth(){
        return startDate != null && startDate.split("/").length == 2 ? startDate.split("/")[0] : null;
    }

    public String getYear(){
        return startDate != null && startDate.split("/").length == 2 ? startDate.split("/")[1] : null;
    }

    public class Validator extends FormValidator implements Serializable {
        public StringFieldValidator startDate;

        public Validator() {
            addValidator(startDate = new StringFieldValidator(ValidatorUtil.OCUPATION_START_DATE).setRequired(true));
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
            return Question89Form.this;
        }
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }
}