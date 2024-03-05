package com.affirm.common.model.catalog;

import java.io.Serializable;

/**
 * Created by jrodriguez on 13/06/16.
 */
public class CreditStatus implements Serializable {

    public static final int INACTIVE_WO_SCHEDULE = 1;
    public static final int INACTIVE_W_SCHEDULE = 2;
    public static final int ACTIVE = 3;
    public static final int CANCELED = 4;
    public static final int REJECTED = 5;
    public static final int ORIGINATED = 6;
    public static final int ORIGINATED_DISBURSED = 7;
    public static final int ACCEPTED_OFFER = 8;
    public static final int PENDING_PAYMENT = 9;
    public static final int PAYED = 10;
    public static final int PENDING_CONFIRMATION_BT = 11;
    public static final int PAYED_INFORMED = 12;

    private Integer id;
    private String status;
    private String messageKey;

    public CreditStatus(Integer id, String messageKey) {
        this.id = id;
        this.messageKey = messageKey;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public void setMessageKey(String messageKey) {
        this.messageKey = messageKey;
    }
}
