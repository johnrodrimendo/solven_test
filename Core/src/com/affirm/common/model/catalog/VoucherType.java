package com.affirm.common.model.catalog;

import java.io.Serializable;

/**
 * Created by sTbn on 4/08/16.
 */
public class VoucherType implements Serializable {

    private Integer id;
    private String name;
    private String messageKey;

    public VoucherType(Integer id, String name, String messageKey) {
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
