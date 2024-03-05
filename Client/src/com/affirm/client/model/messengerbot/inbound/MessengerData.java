package com.affirm.client.model.messengerbot.inbound;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jarmando on 30/11/16.
 */
public class MessengerData {
    private String object;
    private List<Entry> entry = new ArrayList<>();

    public boolean isPage(){
        return "page".equals(object);
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public List<Entry> getEntry() {
        return entry;
    }

    public void setEntry(List<Entry> entry) {
        this.entry = entry;
    }

    @Override
    public String toString() {
        return "MessengerData{" +
                "object='" + object + '\'' +
                ", entrySize=" + entry.size() +
                '}';
    }
}