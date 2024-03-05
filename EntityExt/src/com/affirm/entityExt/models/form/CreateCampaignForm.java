package com.affirm.entityExt.models.form;

import com.affirm.common.util.*;
import com.affirm.marketingCampaign.model.MarketingCampaign;

import java.io.Serializable;

public class CreateCampaignForm extends FormGeneric implements Serializable {

    private String headerImg;
    private String body;
    private String subject;
    private Character receiverType;
    private Character type;
    private Integer parentTemplateId;
    private Integer parentCampaignId;
    private String templateName;
    private String campaignName;


    public CreateCampaignForm() {
        this.setValidator(new Validator());
    }

    public String getHeaderImg() {
        return headerImg;
    }

    public void setHeaderImg(String headerImg) {
        this.headerImg = headerImg;
    }

    public Integer getParentTemplateId() {
        return parentTemplateId;
    }

    public void setParentTemplateId(Integer parentTemplateId) {
        this.parentTemplateId = parentTemplateId;
    }

    public Integer getParentCampaignId() {
        return parentCampaignId;
    }

    public void setParentCampaignId(Integer parentCampaignId) {
        this.parentCampaignId = parentCampaignId;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
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

    public Character getReceiverType() {
        return receiverType;
    }

    public void setReceiverType(Character receiverType) {
        this.receiverType = receiverType;
    }

    public Character getType() {
        return type;
    }

    public void setType(Character type) {
        this.type = type;
    }

    public String getCampaignName() {
        return campaignName;
    }

    public void setCampaignName(String campaignName) {
        this.campaignName = campaignName;
    }

    public class Validator extends FormValidator implements Serializable{

        public StringFieldValidator campaignName;
        public StringFieldValidator headerImg;
        public StringFieldValidator body;
        public StringFieldValidator subject;
        public CharFieldValidator receiverType;
        public CharFieldValidator type;
        public IntegerFieldValidator parentTemplateId;
        public IntegerFieldValidator parentCampaignId;
        public StringFieldValidator templateName;

        public Validator() {
            addValidator(campaignName = new StringFieldValidator().setValidRegex(null).setRequired(true));
            addValidator(body = new StringFieldValidator().setValidRegex(null).setRequired(true));
            addValidator(headerImg = new StringFieldValidator().setValidRegex(null).setRequired(false));
            addValidator(subject = new StringFieldValidator().setValidRegex(null).setMaxCharacters(null).setRequired(true));
            addValidator(receiverType = new CharFieldValidator().setRequired(true));
            addValidator(type = new CharFieldValidator().setRequired(true));
            addValidator(parentTemplateId = new IntegerFieldValidator().setRequired(false));
            addValidator(parentCampaignId = new IntegerFieldValidator().setRequired(false));
            addValidator(templateName = new StringFieldValidator().setValidRegex(null).setRequired(false));
        }

        @Override
        protected void setDynamicValidations() {
            if(type.getValue() != null && type.getValue().equals(MarketingCampaign.EMAIL)) {
                headerImg.setRequired(true);
                body.setRequired(true);
                subject.setRequired(true);
            } else if(type.getValue() != null && type.getValue().equals(MarketingCampaign.SMS)) {
                headerImg.setRequired(false);
                subject.setRequired(false);
                body.setMaxCharacters(140);
            }
        }

        @Override
        protected Object getSubclass() {
            return this;
        }

        @Override
        protected Object getFormClass() {
            return CreateCampaignForm.this;
        }

    }
}
