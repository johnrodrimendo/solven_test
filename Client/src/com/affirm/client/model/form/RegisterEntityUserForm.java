package com.affirm.client.model.form;

import com.affirm.common.util.FormGeneric;
import com.affirm.common.util.FormValidator;
import com.affirm.common.util.StringFieldValidator;
import com.affirm.common.util.ValidatorUtil;

import java.io.Serializable;

public class RegisterEntityUserForm extends FormGeneric implements Serializable {

    private String name;
    private String firstSurname;
    private String email;

    public RegisterEntityUserForm() {
        this.setValidator(new RegisterEntityUserForm.RegisterEntityUserFormValidator());
    }

    public static RegisterEntityUserForm newInstance() {
        return new RegisterEntityUserForm();
    }

    public class RegisterEntityUserFormValidator extends FormValidator implements Serializable {

        public StringFieldValidator name;
        public StringFieldValidator firstSurname;
        public StringFieldValidator email;

        public RegisterEntityUserFormValidator() {
            addValidator(name         = new StringFieldValidator(ValidatorUtil.NAME));
            addValidator(firstSurname = new StringFieldValidator(ValidatorUtil.FIRST_SURNAME).setRequired(false));
            addValidator(email        = new StringFieldValidator(ValidatorUtil.EMAIL));
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
            return RegisterEntityUserForm.this;
        }
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getFirstSurname() { return firstSurname; }

    public void setFirstSurname(String firstSurname) { this.firstSurname = firstSurname; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }
}
