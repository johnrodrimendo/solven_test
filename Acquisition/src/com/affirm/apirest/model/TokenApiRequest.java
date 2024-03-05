package com.affirm.apirest.model;

import com.affirm.common.util.*;

import java.io.Serializable;

public class TokenApiRequest extends FormGeneric {

    private String user;
    private String password;

    public TokenApiRequest() {
        this.setValidator(new TokenApiRequest.Validator());
    }

    public class Validator extends FormValidator implements Serializable {

        public StringFieldValidator user;
        public StringFieldValidator password;

        public Validator() {
            addValidator(user = new StringFieldValidator().setValidRegex(null).setRequired(true));
            addValidator(password = new StringFieldValidator().setValidRegex(null).setRequired(true));
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
            return TokenApiRequest.this;
        }

    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
