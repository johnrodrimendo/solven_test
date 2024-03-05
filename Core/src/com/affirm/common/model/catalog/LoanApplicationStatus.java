package com.affirm.common.model.catalog;

import java.io.Serializable;

/**
 * Created by jrodriguez on 13/06/16.
 */
public class LoanApplicationStatus implements Serializable {

    public static final int NEW = 1;
    public static final int PRE_EVAL_APPROVED = 2;
    public static final int EVAL_APPROVED = 3;
    public static final int WAITING_APPROVAL = 4;
    public static final int APPROVED = 5;
    public static final int REJECTED = 6;
    public static final int REJECTED_AUTOMATIC = 7;
    public static final int APPROVED_SIGNED = 8;
    public static final int EXPIRED = 9;
    public static final int LEAD_REFERRED = 10;
    public static final int REJECTED_AUTOMATICALLY_EVALUATION = 11;
    public static final int CROSS_SELLING_OFFER = 12;
    public static final int LEAD_CONVERTED = 13;

    private Integer id;
    private String status;
    private String messageKey;

    public LoanApplicationStatus(Integer id, String messageKey) {
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
