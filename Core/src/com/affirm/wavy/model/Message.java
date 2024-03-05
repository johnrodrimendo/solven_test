package com.affirm.wavy.model;

public class Message {

    private String messageText;
    private MessageCaption image;
    private MessageAudio audio;
    private MessageCaption document;
    private boolean previewFirstUrl;

    public Message() {
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public MessageCaption getImage() {
        return image;
    }

    public void setImage(MessageCaption image) {
        this.image = image;
    }

    public MessageAudio getAudio() {
        return audio;
    }

    public void setAudio(MessageAudio audio) {
        this.audio = audio;
    }

    public MessageCaption getDocument() {
        return document;
    }

    public void setDocument(MessageCaption document) {
        this.document = document;
    }

    public boolean isPreviewFirstUrl() {
        return previewFirstUrl;
    }

    public void setPreviewFirstUrl(boolean previewFirstUrl) {
        this.previewFirstUrl = previewFirstUrl;
    }
}
