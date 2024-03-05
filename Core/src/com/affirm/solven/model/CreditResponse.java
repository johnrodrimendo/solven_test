package com.affirm.solven.model;

import javax.xml.bind.annotation.XmlElement;

public class CreditResponse {

    @XmlElement(name = "receptionId")
    private int receptionId;

    public void setReceptionId(int receptionId) {
        this.receptionId = receptionId;
    }
}
