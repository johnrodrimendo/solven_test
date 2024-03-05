package com.affirm.common.model.form;

import com.affirm.common.util.*;

import java.io.Serializable;

public class Question166Form extends FormGeneric implements Serializable {

    private Boolean isPep;
    private String pepDetail;
    private Integer professionOcupation;
    private Integer profession;
    private Integer ocupation;
    private Boolean isFatca;
    private Integer activityId;
    private Integer customProfessionId;

    public Question166Form() {
        this.setValidator(new Question166Form.Validator());
    }

    public class Validator extends FormValidator implements Serializable {

        public IntegerFieldValidator ocupation;
        public IntegerFieldValidator professionOcupation;
        public IntegerFieldValidator profession;
        public BooleanFieldValidator isPep;
        public StringFieldValidator pepDetail;
        public BooleanFieldValidator isFatca;
        public IntegerFieldValidator activityId;
        public IntegerFieldValidator customProfessionId;

        public Validator() {
            addValidator(isPep = new BooleanFieldValidator().setRequired(true));
            addValidator(pepDetail = new StringFieldValidator().setRequired(false).setMinCharacters(3).setMaxCharacters(140));
            addValidator(professionOcupation = new IntegerFieldValidator().setRequired(true));
            addValidator(profession = new IntegerFieldValidator().setRequired(true));
            addValidator(ocupation = new IntegerFieldValidator().setRequired(false));
            addValidator(isFatca = new BooleanFieldValidator().setRequired(true));
            addValidator(activityId = new IntegerFieldValidator().setRequired(false));
            addValidator(customProfessionId = new IntegerFieldValidator().setRequired(false));
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
            return Question166Form.this;
        }
    }

    public Boolean getIsPep() {
        return isPep;
    }

    public void setIsPep(Boolean pep) {
        isPep = pep;
    }

    public String getPepDetail() {
        return pepDetail;
    }

    public void setPepDetail(String pepDetail) {
        this.pepDetail = pepDetail;
    }

    public Integer getProfessionOcupation() {
        return professionOcupation;
    }

    public void setProfessionOcupation(Integer professionOcupation) {
        this.professionOcupation = professionOcupation;
    }

    public Integer getProfession() {
        return profession;
    }

    public void setProfession(Integer profession) {
        this.profession = profession;
    }

    public Integer getOcupation() {
        return ocupation;
    }

    public void setOcupation(Integer ocupation) {
        this.ocupation = ocupation;
    }

    public Boolean getIsFatca() {
        return isFatca;
    }

    public void setIsFatca(Boolean fatca) {
        isFatca = fatca;
    }

    public Integer getActivityId() {
        return activityId;
    }

    public void setActivityId(Integer activityId) {
        this.activityId = activityId;
    }

    public Integer getCustomProfessionId() {
        return customProfessionId;
    }

    public void setCustomProfessionId(Integer customProfessionId) {
        this.customProfessionId = customProfessionId;
    }
}