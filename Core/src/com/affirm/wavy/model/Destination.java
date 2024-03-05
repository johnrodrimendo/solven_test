package com.affirm.wavy.model;

import java.util.Date;

public class Destination {

    public enum RecipientType {
        individual,
        group;
    }

    private String correlationId;
    private String destination;
    private RecipientType recipientType;

    public Destination() {
        this.correlationId = new Date().getTime() + "";
        this.recipientType = RecipientType.individual;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public RecipientType getRecipientType() {
        return recipientType;
    }

    public void setRecipientType(RecipientType recipientType) {
        this.recipientType = recipientType;
    }
}
