package com.affirm.common.model.catalog;

import java.io.Serializable;

/**
 * Created by jrodriguez on 06/06/16.
 */
public class Nationality implements Serializable {

    private Integer id;
    private String name;
    private String messageKey;

    public Nationality() {
    }

    public Nationality(Integer id, String name, String messageKey) {
        this.id = id;
        this.name = name;
        this.messageKey = messageKey;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public void setMessageKey(String messageKey) {
        this.messageKey = messageKey;
    }
}
