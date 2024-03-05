package com.affirm.common.model.form;

import com.affirm.common.model.catalog.CountryParam;
import com.affirm.common.util.*;

import java.io.Serializable;

public class Question119Form extends FormGeneric implements Serializable {

    private String name;
    private String firstSurname;
    private String lastSurname;
    private String birthday;
    private Integer loanOrigin;

    public Question119Form() { this.setValidator(new Question119Form.Validator());}

    public class Validator extends FormValidator implements Serializable {

        private StringFieldValidator name;
        private StringFieldValidator firstSurname;
        private StringFieldValidator lastSurname;
        private StringFieldValidator birthday;
        private IntegerFieldValidator loanOrigin;

        public Validator() {
            addValidator(name = new StringFieldValidator(ValidatorUtil.NAME).setFieldName("Nombres"));
            addValidator(firstSurname = new StringFieldValidator(ValidatorUtil.FIRST_SURNAME).setFieldName("Apellido Paterno"));
            addValidator(lastSurname = new StringFieldValidator(ValidatorUtil.LAST_SURNAME).setFieldName("Apellido Materno"));
            addValidator(birthday = new StringFieldValidator(ValidatorUtil.BIRTHDAY));
            addValidator(loanOrigin = new IntegerFieldValidator().setFieldName("Procedencia de solicitud").setRequired(false));
        }

        @Override
        protected void setDynamicValidations() { }

        public void configValidator(Integer countryId) {
            if (countryId == CountryParam.COUNTRY_COLOMBIA) {
                loanOrigin.setRequired(true);
                lastSurname.setRequired(false);
            }
        }

        @Override
        protected Object getSubclass() {
            return this;
        }

        @Override
        protected Object getFormClass() {
            return Question119Form.this;
        }
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getFirstSurname() { return firstSurname; }

    public void setFirstSurname(String firstSurname) { this.firstSurname = firstSurname; }

    public String getLastSurname() { return lastSurname; }

    public void setLastSurname(String lastSurname) { this.lastSurname = lastSurname; }

    public String getBirthday() { return birthday; }

    public void setBirthday(String birthday) { this.birthday = birthday; }

    public Integer getLoanOrigin() {
        return loanOrigin;
    }

    public void setLoanOrigin(Integer loanOrigin) {
        this.loanOrigin = loanOrigin;
    }
}
