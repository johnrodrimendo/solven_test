package com.affirm.common.model.form;

import com.affirm.common.util.*;

import java.io.Serializable;

public class Question36Form extends FormGeneric implements Serializable {

    private String ruc;
    private Integer clients;

    public Question36Form() {
        this.setValidator(new Question36Form.Validator());
    }

    public class Validator extends FormValidator implements Serializable {

        public StringFieldValidator ruc;
        public IntegerFieldValidator clients;

        public Validator() {
            addValidator(ruc = new StringFieldValidator(ValidatorUtil.RUC));
            addValidator(clients = new IntegerFieldValidator());
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
            return Question36Form.this;
        }
    }

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public Integer getClients() {
        return clients;
    }

    public void setClients(Integer clients) {
        this.clients = clients;
    }
}