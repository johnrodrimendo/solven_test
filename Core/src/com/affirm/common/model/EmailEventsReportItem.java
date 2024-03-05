package com.affirm.common.model;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class EmailEventsReportItem {

    public static final String EVENT_SEND = "send";
    public static final String EVENT_DELIVERY = "delivery";
    public static final String EVENT_OPEN = "open";
    public static final String EVENT_CLICK = "click";
    public static final String EVENT_BOUNCE = "bounce";
    public static final String EVENT_SPAM = "spam";
    public static final String EVENT_UNSUBSCRIBED = "unsubscribed";
    public static final String EVENT_TOTAL = "total";

    private String event;
    private Double percentage = 0.0;
    private Integer uniqueHits = 0;
    private Integer totalHits = 0;
    private List<Integer> personInteractionIds = new ArrayList<>();


    public EmailEventsReportItem() {
    }

    public EmailEventsReportItem(String event) {
        this.event = event;
    }

    public void addPersonInteractionHit(Integer personInteractionId) {
        if (!personInteractionIds.contains(personInteractionId)) {
            uniqueHits = uniqueHits + 1;
            personInteractionIds.add(personInteractionId);
        }
        totalHits = totalHits + 1;
    }

    public String getPersonInteractionsAsString(){
        return new Gson().toJson(personInteractionIds);
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public Double getPercentage() {
        return percentage;
    }

    public void setPercentage(Double percentage) {
        this.percentage = percentage;
    }

    public Integer getUniqueHits() {
        return uniqueHits;
    }

    public void setUniqueHits(Integer uniqueHits) {
        this.uniqueHits = uniqueHits;
    }

    public Integer getTotalHits() {
        return totalHits;
    }

    public void setTotalHits(Integer totalHits) {
        this.totalHits = totalHits;
    }

    public List<Integer> getPersonInteractionIds() {
        return personInteractionIds;
    }

    public void setPersonInteractionIds(List<Integer> personInteractionIds) {
        this.personInteractionIds = personInteractionIds;
    }
}
