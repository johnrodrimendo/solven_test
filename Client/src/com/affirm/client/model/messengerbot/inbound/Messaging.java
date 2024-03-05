package com.affirm.client.model.messengerbot.inbound;

import com.affirm.client.model.messengerbot.contract.Postback;

/**
 * Created by jarmando on 30/11/16.
 */
public class Messaging {
    private Message message;
    private long timestamp;
    private Sender sender;
    private Recipient recipient;
    private Postback postback;

    public Postback getPostback(){
        return postback;
    }

    public void setPostback(Postback postback) {
        this.postback = postback;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Sender getSender() {
        return sender;
    }

    public void setSender(Sender sender) {
        this.sender = sender;
    }

    public Recipient getRecipient() {
        return recipient;
    }

    public void setRecipient(Recipient recipient) {
        this.recipient = recipient;
    }

}
