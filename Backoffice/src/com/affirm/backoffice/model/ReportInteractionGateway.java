package com.affirm.backoffice.model;

import java.util.Date;

public class ReportInteractionGateway {

    private Date sentDate;
    private boolean sentWhatsapp;
    private boolean sentEmail;
    private Integer userId;
    private Integer statusId;
    private Integer totalMoratoriumFee;
    private Integer totalExpirationFee;
    private Integer queryBotId;

    public Date getSentDate() {
        return sentDate;
    }

    public void setSentDate(Date sentDate) {
        this.sentDate = sentDate;
    }

    public boolean isSentWhatsapp() {
        return sentWhatsapp;
    }

    public void setSentWhatsapp(boolean sentWhatsapp) {
        this.sentWhatsapp = sentWhatsapp;
    }

    public boolean isSentEmail() {
        return sentEmail;
    }

    public void setSentEmail(boolean sentEmail) {
        this.sentEmail = sentEmail;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getStatusId() {
        return statusId;
    }

    public void setStatusId(Integer statusId) {
        this.statusId = statusId;
    }

    public Integer getTotalMoratoriumFee() {
        return totalMoratoriumFee;
    }

    public void setTotalMoratoriumFee(Integer totalMoratoriumFee) {
        this.totalMoratoriumFee = totalMoratoriumFee;
    }

    public Integer getTotalExpirationFee() {
        return totalExpirationFee;
    }

    public void setTotalExpirationFee(Integer totalExpirationFee) {
        this.totalExpirationFee = totalExpirationFee;
    }

    public Integer getQueryBotId() {
        return queryBotId;
    }

    public void setQueryBotId(Integer queryBotId) {
        this.queryBotId = queryBotId;
    }
}
