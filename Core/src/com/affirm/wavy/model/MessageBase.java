package com.affirm.wavy.model;

public abstract class MessageBase {

    public enum MessageType {
        JPG,
        JPEG,
        PNG,
        AAC,
        MP4,
        AMR,
        MP3,
        OGG,
        PDF;
    }

    private MessageType type;
    private String url;
    private String data;

    public MessageBase() {
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
