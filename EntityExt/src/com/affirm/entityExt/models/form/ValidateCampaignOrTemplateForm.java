package com.affirm.entityExt.models.form;

import com.affirm.common.util.*;
import com.affirm.marketingCampaign.model.MarketingCampaign;

import java.io.Serializable;

public class ValidateCampaignOrTemplateForm extends FormGeneric implements Serializable {

    private Boolean sendTest;
    private Boolean isCreateTemplate;
    private String headerImg;
    private String bodySms;
    private String bodyEmail;
    private String subject;
    private Character type;
    private String templateName;
    private String campaignName;
    private String destinationPhoneNumber;
    private String destinationEmail;

    public String getHeaderImg() {
        return headerImg;
    }

    public void setHeaderImg(String headerImg) {
        this.headerImg = headerImg;
    }

    public String getBodySms() {
        return bodySms;
    }

    public void setBodySms(String bodySms) {
        this.bodySms = bodySms;
    }

    public String getBodyEmail() {
        return bodyEmail;
    }

    public void setBodyEmail(String bodyEmail) {
        this.bodyEmail = bodyEmail;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Character getType() {
        return type;
    }

    public void setType(Character type) {
        this.type = type;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getCampaignName() {
        return campaignName;
    }

    public void setCampaignName(String campaignName) {
        this.campaignName = campaignName;
    }

    public String getDestinationPhoneNumber() {
        return destinationPhoneNumber;
    }

    public void setDestinationPhoneNumber(String destinationPhoneNumber) {
        this.destinationPhoneNumber = destinationPhoneNumber;
    }

    public String getDestinationEmail() {
        return destinationEmail;
    }

    public void setDestinationEmail(String destinationEmail) {
        this.destinationEmail = destinationEmail;
    }

    public Boolean getSendTest() {
        return sendTest;
    }

    public void setSendTest(Boolean sendTest) {
        this.sendTest = sendTest;
    }

    public Boolean getIsCreateTemplate() {
        return isCreateTemplate;
    }

    public void setIsCreateTemplate(Boolean createTemplate) {
        isCreateTemplate = createTemplate;
    }

    public ValidateCampaignOrTemplateForm() {
        this.setValidator(new Validator());
    }



    public class Validator extends FormValidator implements Serializable{


        public BooleanFieldValidator sendTest;
        public BooleanFieldValidator isCreateTemplate;
        public StringFieldValidator campaignName;
        public StringFieldValidator headerImg;
        public StringFieldValidator bodySms;
        public StringFieldValidator bodyEmail;
        public StringFieldValidator templateName;
        public StringFieldValidator subject;
        public CharFieldValidator type;
        public StringFieldValidator destinationPhoneNumber;
        public StringFieldValidator destinationEmail;


        public Validator() {
            addValidator(isCreateTemplate = new BooleanFieldValidator().setRequired(false));
            addValidator(sendTest = new BooleanFieldValidator().setRequired(true));
            addValidator(campaignName = new StringFieldValidator().setValidRegex(null).setRequired(true));
            addValidator(bodySms = new StringFieldValidator().setValidRegex(null).setMaxCharacters(140).setRequired(true));
            addValidator(bodyEmail = new StringFieldValidator().setValidRegex(null).setRequired(true));
            addValidator(headerImg = new StringFieldValidator().setValidRegex(null).setRequired(true));
            addValidator(subject = new StringFieldValidator().setValidRegex(null).setMaxCharacters(null).setRequired(true));
            addValidator(type = new CharFieldValidator().setRequired(true));
            addValidator(destinationPhoneNumber = new StringFieldValidator(ValidatorUtil.PHONE_OR_CELLPHONE).setRequired(false));
            addValidator(destinationEmail = new StringFieldValidator(ValidatorUtil.EMAIL).setRequired(false));
            addValidator(templateName = new StringFieldValidator().setValidRegex(null).setRequired(true));
        }

        @Override
        protected void setDynamicValidations() {
            Boolean isATest = false;
            if(isCreateTemplate.getValue() != null && isCreateTemplate.getValue()) campaignName.setRequired(false);
            if(sendTest.getValue() != null && sendTest.getValue()){
                isATest = true;
            }
            if(type.getValue() != null && type.getValue().equals(MarketingCampaign.EMAIL)) {
                bodySms.setRequired(false);
                if(isATest) destinationEmail.setRequired(true);
            } else if(type.getValue() != null && type.getValue().equals(MarketingCampaign.SMS)) {
                headerImg.setRequired(false);
                bodyEmail.setRequired(false);
                subject.setRequired(false);
                if(isATest) destinationPhoneNumber.setRequired(true);
            }
            if(isATest){
                templateName.setRequired(false);
                campaignName.setRequired(false);
            }

        }

        @Override
        protected Object getSubclass() {
            return this;
        }

        @Override
        protected Object getFormClass() {
            return ValidateCampaignOrTemplateForm.this;
        }

    }
}
