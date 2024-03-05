package com.affirm.common.model.form;

import com.affirm.common.util.*;

import java.io.Serializable;

public class Question156Form extends FormGeneric implements Serializable {

    private Boolean propertyGuarantee;
    private Boolean propertySunarp;
    private Boolean propertyNearHill;
    private Boolean propertyHasSidewalk;

    public Question156Form() {
        this.setValidator(new Question156Form.Validator());
    }

    public class Validator extends FormValidator implements Serializable {

        private BooleanFieldValidator propertyGuarantee;
        private BooleanFieldValidator propertySunarp;
        private BooleanFieldValidator propertyNearHill;
        private BooleanFieldValidator propertyHasSidewalk;


        public Validator() {
            addValidator(propertyGuarantee = new BooleanFieldValidator().setRequired(true));
            addValidator(propertySunarp = new BooleanFieldValidator().setRequired(true));
            addValidator(propertyNearHill = new BooleanFieldValidator().setRequired(true));
            addValidator(propertyHasSidewalk = new BooleanFieldValidator().setRequired(true));
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
            return Question156Form.this;
        }
    }

    public Boolean getPropertyGuarantee() {
        return propertyGuarantee;
    }

    public void setPropertyGuarantee(Boolean propertyGuarantee) {
        this.propertyGuarantee = propertyGuarantee;
    }

    public Boolean getPropertySunarp() {
        return propertySunarp;
    }

    public void setPropertySunarp(Boolean propertySunarp) {
        this.propertySunarp = propertySunarp;
    }

    public Boolean getPropertyNearHill() {
        return propertyNearHill;
    }

    public void setPropertyNearHill(Boolean propertyNearHill) {
        this.propertyNearHill = propertyNearHill;
    }

    public Boolean getPropertyHasSidewalk() {
        return propertyHasSidewalk;
    }

    public void setPropertyHasSidewalk(Boolean propertyHasSidewalk) {
        this.propertyHasSidewalk = propertyHasSidewalk;
    }
}