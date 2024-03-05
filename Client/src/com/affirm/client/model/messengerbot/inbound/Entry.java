package com.affirm.client.model.messengerbot.inbound;

import java.util.ArrayList;
import java.util.List;

public class Entry {
    private List<Messaging> messaging = new ArrayList<Messaging>();
    private long time;
    private String id;

    public List<Messaging> getMessaging() {
        return messaging;
    }

    public void setMessaging(List<Messaging> messaging) {
        this.messaging = messaging;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
