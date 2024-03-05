package com.affirm.entityExt.models.form;


import com.affirm.common.util.*;

import java.io.Serializable;

public class UpdateMarketingConfiguration  extends FormGeneric implements Serializable {

    private String senderName;
    private String senderEmail;
    private String status;
    private Boolean emailOnDemand;
    private Boolean emailFollowUp;
    private Boolean smsOnDemand;
    private Boolean smsFollowUp;
    private Character smsServiceType;


    public UpdateMarketingConfiguration() {
        this.setValidator(new UpdateMarketingConfiguration.UpdateMarketingConfigurationFormValidator());
    }

    public class UpdateMarketingConfigurationFormValidator extends FormValidator implements Serializable {

        public StringFieldValidator senderName;
        public StringFieldValidator senderEmail;
        public StringFieldValidator status;
        public BooleanFieldValidator emailOnDemand;
        public BooleanFieldValidator emailFollowUp;
        public BooleanFieldValidator smsOnDemand;
        public BooleanFieldValidator smsFollowUp;
        public StringFieldValidator smsServiceType;

        public UpdateMarketingConfigurationFormValidator() {
            addValidator(senderName = new StringFieldValidator(ValidatorUtil.NAME_AND_LASTNAMES).setRequired(false));
            addValidator(senderEmail = new StringFieldValidator(ValidatorUtil.EMAIL).setRequired(false));
            addValidator(status = new StringFieldValidator().setRequired(false));
            addValidator(emailOnDemand = new BooleanFieldValidator().setRequired(false));
            addValidator(emailFollowUp = new BooleanFieldValidator().setRequired(false));
            addValidator(smsOnDemand = new BooleanFieldValidator().setRequired(false));
            addValidator(smsFollowUp = new BooleanFieldValidator().setRequired(false));
            addValidator(smsServiceType = new StringFieldValidator().setRequired(false));
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
            return UpdateMarketingConfiguration.this;
        }


    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getEmailOnDemand() {
        return emailOnDemand;
    }

    public void setEmailOnDemand(Boolean emailOnDemand) {
        this.emailOnDemand = emailOnDemand;
    }

    public Boolean getEmailFollowUp() {
        return emailFollowUp;
    }

    public void setEmailFollowUp(Boolean emailFollowUp) {
        this.emailFollowUp = emailFollowUp;
    }

    public Boolean getSmsOnDemand() {
        return smsOnDemand;
    }

    public void setSmsOnDemand(Boolean smsOnDemand) {
        this.smsOnDemand = smsOnDemand;
    }

    public Boolean getSmsFollowUp() {
        return smsFollowUp;
    }

    public void setSmsFollowUp(Boolean smsFollowUp) {
        this.smsFollowUp = smsFollowUp;
    }

    public Character getSmsServiceType() {
        return smsServiceType;
    }

    public void setSmsServiceType(Character smsServiceType) {
        this.smsServiceType = smsServiceType;
    }
}
