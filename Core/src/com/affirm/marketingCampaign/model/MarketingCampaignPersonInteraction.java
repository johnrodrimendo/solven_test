package com.affirm.marketingCampaign.model;

public class MarketingCampaignPersonInteraction {

    private Integer personInteractionId;
    private String inticoSmsCode;
    private Boolean emailBounce;
    private Boolean emailComplaint;
    private Boolean emailDelivery;
    private Boolean emailSend;
    private Boolean emailOpen;
    private Boolean emailClick;
    private Boolean smsPending;
    private Boolean smsDelivered;
    private Boolean smsRejected;
    private Boolean stopTracking;

    public Integer getPersonInteractionId() {
        return personInteractionId;
    }

    public void setPersonInteractionId(Integer personInteractionId) {
        this.personInteractionId = personInteractionId;
    }

    public String getInticoSmsCode() {
        return inticoSmsCode;
    }

    public void setInticoSmsCode(String inticoSmsCode) {
        this.inticoSmsCode = inticoSmsCode;
    }

    public Boolean getEmailBounce() {
        return emailBounce;
    }

    public void setEmailBounce(Boolean emailBounce) {
        this.emailBounce = emailBounce;
    }

    public Boolean getEmailComplaint() {
        return emailComplaint;
    }

    public void setEmailComplaint(Boolean emailComplaint) {
        this.emailComplaint = emailComplaint;
    }

    public Boolean getEmailDelivery() {
        return emailDelivery;
    }

    public void setEmailDelivery(Boolean emailDelivery) {
        this.emailDelivery = emailDelivery;
    }

    public Boolean getEmailSend() {
        return emailSend;
    }

    public void setEmailSend(Boolean emailSend) {
        this.emailSend = emailSend;
    }

    public Boolean getEmailOpen() {
        return emailOpen;
    }

    public void setEmailOpen(Boolean emailOpen) {
        this.emailOpen = emailOpen;
    }

    public Boolean getEmailClick() {
        return emailClick;
    }

    public void setEmailClick(Boolean emailClick) {
        this.emailClick = emailClick;
    }

    public Boolean getSmsPending() {
        return smsPending;
    }

    public void setSmsPending(Boolean smsPending) {
        this.smsPending = smsPending;
    }

    public Boolean getSmsDelivered() {
        return smsDelivered;
    }

    public void setSmsDelivered(Boolean smsDelivered) {
        this.smsDelivered = smsDelivered;
    }

    public Boolean getSmsRejected() {
        return smsRejected;
    }

    public void setSmsRejected(Boolean smsRejected) {
        this.smsRejected = smsRejected;
    }

    public Boolean getStopTracking() {
        return stopTracking;
    }

    public void setStopTracking(Boolean stopTracking) {
        this.stopTracking = stopTracking;
    }
}
