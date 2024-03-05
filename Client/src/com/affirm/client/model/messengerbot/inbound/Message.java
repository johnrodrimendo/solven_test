package com.affirm.client.model.messengerbot.inbound;

import com.affirm.client.model.messengerbot.contract.QuickReply;

/**
 * Created by jarmando on 30/11/16.
 */

public class Message {
    private String text;
    private long seq;
    private String mid;
    private QuickReply quick_reply;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getSeq() {
        return seq;
    }

    public void setSeq(long seq) {
        this.seq = seq;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public QuickReply getQuick_reply() {
        return quick_reply;
    }

    public void setQuick_reply(QuickReply quick_reply) {
        this.quick_reply = quick_reply;
    }
}
