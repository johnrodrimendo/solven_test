package com.affirm.banbif.model;

import com.affirm.common.util.FormGeneric;
import com.affirm.common.util.FormValidator;
import com.affirm.common.util.StringFieldValidator;
import com.affirm.common.util.ValidatorUtil;

import java.io.Serializable;

public class ParentsNameForm extends FormGeneric implements Serializable {

    private String motherNames;
    private String motherSurnames;
    private String fatherNames;
    private String fatherSurnames;

    public ParentsNameForm() { this.setValidator(new ParentsNameForm.Validator());}

    public class Validator extends FormValidator implements Serializable {

        private StringFieldValidator motherNames;
        private StringFieldValidator motherSurnames;
        private StringFieldValidator fatherNames;
        private StringFieldValidator fatherSurnames;

        public Validator() {
            addValidator(motherNames = new StringFieldValidator(ValidatorUtil.NAME).setFieldName("Nombres Madre"));
            addValidator(motherSurnames = new StringFieldValidator(ValidatorUtil.FIRST_SURNAME).setFieldName("Apellidos Madre"));
            addValidator(fatherNames = new StringFieldValidator(ValidatorUtil.NAME).setFieldName("Nombres Padre"));
            addValidator(fatherSurnames = new StringFieldValidator(ValidatorUtil.FIRST_SURNAME).setFieldName("Apellidos Padre"));
        }

        @Override
        protected void setDynamicValidations() { }

        @Override
        protected Object getSubclass() {
            return this;
        }

        @Override
        protected Object getFormClass() {
            return ParentsNameForm.this;
        }
    }

    public String getMotherNames() {
        return motherNames;
    }

    public void setMotherNames(String motherNames) {
        this.motherNames = motherNames;
    }

    public String getMotherSurnames() {
        return motherSurnames;
    }

    public void setMotherSurnames(String motherSurnames) {
        this.motherSurnames = motherSurnames;
    }

    public String getFatherNames() {
        return fatherNames;
    }

    public void setFatherNames(String fatherNames) {
        this.fatherNames = fatherNames;
    }

    public String getFatherSurnames() {
        return fatherSurnames;
    }

    public void setFatherSurnames(String fatherSurnames) {
        this.fatherSurnames = fatherSurnames;
    }
}
