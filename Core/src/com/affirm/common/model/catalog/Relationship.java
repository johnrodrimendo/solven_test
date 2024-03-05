/**
 *
 */
package com.affirm.common.model.catalog;

import java.io.Serializable;

/**
 * @author jrodriguez
 */
public class Relationship implements Serializable {

    public static final int WIFE = 7;
    public static final int COHABITANT = 8;

    public static final int MOTHER = 1;
    public static final int FATHER = 2;
    public static final int CLIENT = 9;
    public static final int PROVIDER = 10;
    public static final int FRIEND = 11;
    public static final int HOME_OWNER = 12;

    private Integer id;
    private String relationship;
    private String messageKey;

    public Relationship() {
    }

    public Relationship(Integer id, String messageKey) {
        super();
        this.id = id;
        this.messageKey = messageKey;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public void setMessageKey(String messageKey) {
        this.messageKey = messageKey;
    }
}
