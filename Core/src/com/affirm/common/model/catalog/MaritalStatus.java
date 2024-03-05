/**
 *
 */
package com.affirm.common.model.catalog;

import java.io.Serializable;

/**
 * @author jrodriguez
 */
public class MaritalStatus implements Serializable {

    public static final int SINGLE = 1;
    public static final int MARRIED = 2;
    public static final int DIVORCED = 3;
    public static final int WIDOWED = 4;
    public static final int COHABITANT = 5;

    private Integer id;
    private String status;
    private String messageKey;

    public MaritalStatus() {
    }

    public MaritalStatus(Integer id, String messageKey) {
        super();
        this.id = id;
        this.messageKey = messageKey;
    }

    public boolean hasPartner() {
        return id == MARRIED || id == COHABITANT;
    }

    public static boolean hasPartner(int maritalStatusId) {
        return maritalStatusId == MARRIED || maritalStatusId == COHABITANT;
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
