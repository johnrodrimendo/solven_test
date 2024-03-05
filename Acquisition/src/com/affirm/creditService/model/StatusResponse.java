package com.affirm.creditService.model;

/**
 * Created by dev5 on 22/11/17.
 */
public class StatusResponse {

    private int receptionId;
    private String loanInfoURL;

    public StatusResponse(Integer receptionId, String loanInfoURL){
        setReceptionId(receptionId);
        setLoanInfoURL(loanInfoURL);
    }

    public void setReceptionId(int receptionId) {
        this.receptionId = receptionId;
    }

    public int getReceptionId() {
        return receptionId;
    }

    public String getLoanInfoURL() {
        return loanInfoURL;
    }

    public void setLoanInfoURL(String loanInfoURL) {
        this.loanInfoURL = loanInfoURL;
    }
}
