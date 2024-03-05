package com.affirm.api.endpoint.soap.model;

import javax.xml.bind.annotation.XmlElement;

public class CreditResponse {

    @XmlElement(name = "receptionId")
    private int receptionId;
    @XmlElement(name = "loanInfoURL")
    private String loanInfoURL;

    public void setReceptionId(int receptionId) {
        this.receptionId = receptionId;
    }
    public void setLoanInfoURL(String loanInfoURL) {
        this.loanInfoURL = loanInfoURL;
    }
}
