package com.affirm.wavy.model;

public class ReceivedMessageChat {

    public enum MessageType {
        TEXT,
        AUDIO,
        IMAGE,
    }

    private String type;
    private String messageText;
    private String mediaUrl;
    private String mimeType;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }
}
