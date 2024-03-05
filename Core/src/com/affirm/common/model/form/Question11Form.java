package com.affirm.common.model.form;

import com.affirm.common.util.FormGeneric;

import java.io.Serializable;

public class Question11Form extends FormGeneric implements Serializable {

    private Boolean advices;
    private Boolean affiliation;

    public Boolean getAdvices() {
        return advices;
    }

    public void setAdvices(Boolean advices) {
        this.advices = advices;
    }

    public Boolean getAffiliation() {
        return affiliation;
    }

    public void setAffiliation(Boolean affiliation) {
        this.affiliation = affiliation;
    }
}