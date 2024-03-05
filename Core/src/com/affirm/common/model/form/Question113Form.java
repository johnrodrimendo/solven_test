package com.affirm.common.model.form;

import com.affirm.common.util.BooleanFieldValidator;
import com.affirm.common.util.FormGeneric;
import com.affirm.common.util.FormValidator;
import com.affirm.common.util.StringFieldValidator;

import java.io.Serializable;

public class Question113Form extends FormGeneric implements Serializable {
    private Boolean isPep;
    private String pepDetail;

    public Question113Form() {
        this.setValidator(new Question113Form.Validator());
    }

    public class Validator extends FormValidator implements Serializable {

        public BooleanFieldValidator isPep;
        public StringFieldValidator pepDetail;

        public Validator() {
            addValidator(isPep = new BooleanFieldValidator().setRequired(true));
            addValidator(pepDetail = new StringFieldValidator().setRequired(false).setMinCharacters(3).setMaxCharacters(140));
        }

        @Override
        protected void setDynamicValidations() {
            if (isPep.getValue()) {
                pepDetail.setRequired(true);
            }
        }

        @Override
        protected Object getSubclass() { return this; }

        @Override
        protected Object getFormClass() { return Question113Form.this; }
    }

    public Boolean getIsPep() { return isPep; }

    public void setIsPep(Boolean isPep) { this.isPep = isPep; }

    public String getPepDetail() { return pepDetail; }

    public void setPepDetail(String pepDetail) { this.pepDetail = pepDetail; }
}