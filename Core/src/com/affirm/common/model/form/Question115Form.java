package com.affirm.common.model.form;

import com.affirm.common.util.BooleanFieldValidator;
import com.affirm.common.util.FormGeneric;
import com.affirm.common.util.FormValidator;

import java.io.Serializable;

public class Question115Form extends FormGeneric implements Serializable {

    private Boolean assistedProcess;
    private String date;
    private String time;

    public Question115Form() {
        this.setValidator(new Question115Form.Validator());
    }

    public class Validator extends FormValidator implements Serializable {

        public BooleanFieldValidator assistedProcess;

        public Validator() {
            addValidator(assistedProcess = new BooleanFieldValidator().setRequired(true));
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
            return Question115Form.this;
        }
    }

    public Boolean getAssistedProcess() {
        return assistedProcess;
    }

    public void setAssistedProcess(Boolean assistedProcess) {
        this.assistedProcess = assistedProcess;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}