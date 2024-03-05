package com.affirm.backoffice.model;

import java.util.Date;

public class ReportInteractionManagement {

    private Date sentDate;
    private Integer interactionId;
    private Integer userId;
    private Integer statusId;
    private Integer totalInteractions;
    private String appliedFilters;
    private Integer queryBotId;

    public Date getSentDate() {
        return sentDate;
    }

    public void setSentDate(Date sentDate) {
        this.sentDate = sentDate;
    }

    public Integer getInteractionId() {
        return interactionId;
    }

    public void setInteractionId(Integer interactionId) {
        this.interactionId = interactionId;
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

    public Integer getTotalInteractions() {
        return totalInteractions;
    }

    public void setTotalInteractions(Integer totalInteractions) {
        this.totalInteractions = totalInteractions;
    }

    public String getAppliedFilters() {
        return appliedFilters;
    }

    public void setAppliedFilters(String appliedFilters) {
        this.appliedFilters = appliedFilters;
    }

    public Integer getQueryBotId() {
        return queryBotId;
    }

    public void setQueryBotId(Integer queryBotId) {
        this.queryBotId = queryBotId;
    }
}
