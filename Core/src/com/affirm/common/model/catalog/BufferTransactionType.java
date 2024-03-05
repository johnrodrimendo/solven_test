package com.affirm.common.model.catalog;

import java.io.Serializable;

/**
 * Created by jrodriguez on 11/08/16.
 */
public class BufferTransactionType implements Serializable {

    public static final int DISBURSEMENT_CONFIRMATION = 1;
    public static final int DISBURSEMENT_REJECTION = 2;
    public static final int ACCREDIT_PAYMENT = 3;

    private Integer id;
    private String transaction;

    public BufferTransactionType(Integer id, String transaction) {
        this.id = id;
        this.transaction = transaction;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTransaction() {
        return transaction;
    }

    public void setTransaction(String transaction) {
        this.transaction = transaction;
    }
}
