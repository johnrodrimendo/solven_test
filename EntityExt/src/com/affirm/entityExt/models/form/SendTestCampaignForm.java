package com.affirm.entityExt.models.form;

import com.affirm.common.util.*;
import com.affirm.marketingCampaign.model.MarketingCampaign;

import java.io.Serializable;

public class SendTestCampaignForm extends FormGeneric implements Serializable {

    private String headerImg;
    private String body;
    private String subject;
    private String destination;
    private Character type;

    public SendTestCampaignForm() {
        this.setValidator(new Validator());
    }

    public String getHeaderImg() {
        return headerImg;
    }

    public void setHeaderImg(String headerImg) {
        this.headerImg = headerImg;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public Character getType() {
        return type;
    }

    public void setType(Character type) {
        this.type = type;
    }

    public class Validator extends FormValidator implements Serializable{

        public StringFieldValidator headerImg;
        public StringFieldValidator body;
        public StringFieldValidator subject;
        public StringFieldValidator destination;
        public CharFieldValidator type;

        public Validator() {
            addValidator(type = new CharFieldValidator().setRequired(true));
            addValidator(body = new StringFieldValidator().setValidRegex(null).setRequired(true));
            addValidator(headerImg = new StringFieldValidator().setValidRegex(null).setRequired(false));
            addValidator(subject = new StringFieldValidator().setValidRegex(null).setMaxCharacters(null).setRequired(true));
            addValidator(destination = new StringFieldValidator().setRequired(true));
        }

        @Override
        protected void setDynamicValidations() {
            if(type.getValue() != null && type.getValue().equals(MarketingCampaign.EMAIL)) {
                headerImg.setRequired(true);
                subject.setRequired(true);
                destination.update(ValidatorUtil.EMAIL);
            } else if(type.getValue() != null && type.getValue().equals(MarketingCampaign.SMS)) {
                headerImg.setRequired(false);
                subject.setRequired(false);
                body.setMaxCharacters(140);
                destination.update(ValidatorUtil.CELLPHONE_NUMBER);
            }

        }

        @Override
        protected Object getSubclass() {
            return this;
        }

        @Override
        protected Object getFormClass() {
            return SendTestCampaignForm.this;
        }

    }
}
