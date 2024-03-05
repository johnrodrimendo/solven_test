package com.affirm.wavy.model;

import java.util.Date;

public class StatusMessage {

    public enum Status {
        CARRIER_COMMUNICATION_ERROR, // Error uploading media to WhatsApp.
        REJECTED_BY_CARRIER, // Database error occurred.
        SENT_SUCCESS,
        EXPIRED, // Message expired.
        // Failed to send message because it was too old.
        NOT_DELIVERED, // Rate limiting engaged - too many message sends attempted.
        // Failed to send message because this user’s phone number is part of an experiment.
        // Structure unavailable: Client could not display highly structured message.
        // Failed to send message because you are outside the support window for freeform messages to this user. Please use a valid HSM notification or reconsider.
        // Media upload error (Unknown Error).
        // Failed to send message because your account is ineligible. Please check your WhatsApp Business Account.
        // Temporary upload failure. Try again later.
        DELIVERED_SUCCESS,
        READ_SUCCESS,
        INVALID_DESTINATION_NUMBER, // Invalid WhatsApp Contact.
        DESTINATION_BLOCKED_BY_OPTOUT, // Destination Blocked by OptOut.
        INVALID_MESSAGE_LENGTH, // Message too long.
        INVALID_MESSAGE_TEXT, // Parameter value is not valid.
        INVALID_CONTENT, // Invalid message type UNKNOWN.
        INVALID_SESSION, // Session is not open and no fallback HSM is set.
        DESTINATION_BLOCKED_BY_OPTIN,
        DESTINATION_BLOCKED_BY_WHITELIST,
        INTERNAL_ERROR // Could not check contacts from WhatsApp API.
    }

    private String id;
    private String correlationId;
    private String destination;
    private String origin;
    private String campaignId;
    private String campaignStatus;
    private String extraInfo;
    private boolean sent;
    private int sentStatusCode;
    private String sentStatus;
    private Date sentDate;
    private long sentAt;
    private boolean delivered;
    private int deliveredStatusCode;
    private String deliveredStatus;
    private Date deliveredDate;
    private long deliveredAt;
    private boolean read;
    private Date readDate;
    private long readAt;
    private Date updatedDate;
    private long updatedAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(String campaignId) {
        this.campaignId = campaignId;
    }

    public String getCampaignStatus() {
        return campaignStatus;
    }

    public void setCampaignStatus(String campaignStatus) {
        this.campaignStatus = campaignStatus;
    }

    public String getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(String extraInfo) {
        this.extraInfo = extraInfo;
    }

    public boolean isSent() {
        return sent;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }

    public int getSentStatusCode() {
        return sentStatusCode;
    }

    public void setSentStatusCode(int sentStatusCode) {
        this.sentStatusCode = sentStatusCode;
    }

    public String getSentStatus() {
        return sentStatus;
    }

    public void setSentStatus(String sentStatus) {
        this.sentStatus = sentStatus;
    }

    public Date getSentDate() {
        return sentDate;
    }

    public void setSentDate(Date sentDate) {
        this.sentDate = sentDate;
    }

    public long getSentAt() {
        return sentAt;
    }

    public void setSentAt(long sentAt) {
        this.sentAt = sentAt;
    }

    public boolean isDelivered() {
        return delivered;
    }

    public void setDelivered(boolean delivered) {
        this.delivered = delivered;
    }

    public int getDeliveredStatusCode() {
        return deliveredStatusCode;
    }

    public void setDeliveredStatusCode(int deliveredStatusCode) {
        this.deliveredStatusCode = deliveredStatusCode;
    }

    public String getDeliveredStatus() {
        return deliveredStatus;
    }

    public void setDeliveredStatus(String deliveredStatus) {
        this.deliveredStatus = deliveredStatus;
    }

    public Date getDeliveredDate() {
        return deliveredDate;
    }

    public void setDeliveredDate(Date deliveredDate) {
        this.deliveredDate = deliveredDate;
    }

    public long getDeliveredAt() {
        return deliveredAt;
    }

    public void setDeliveredAt(long deliveredAt) {
        this.deliveredAt = deliveredAt;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public Date getReadDate() {
        return readDate;
    }

    public void setReadDate(Date readDate) {
        this.readDate = readDate;
    }

    public long getReadAt() {
        return readAt;
    }

    public void setReadAt(long readAt) {
        this.readAt = readAt;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }
}
