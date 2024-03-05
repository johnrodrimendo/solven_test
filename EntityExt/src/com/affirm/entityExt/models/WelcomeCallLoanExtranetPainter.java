package com.affirm.entityExt.models;

import com.affirm.common.model.catalog.TrackingAction;
import com.affirm.common.model.transactional.PersonContactInformation;

public class WelcomeCallLoanExtranetPainter {

    private Integer loanId;
    private TrackingAction action;
    private String tokyCall;

    public Integer getLoanId() {
        return loanId;
    }

    public void setLoanId(Integer loanId) {
        this.loanId = loanId;
    }

    public TrackingAction getAction() {
        return action;
    }

    public void setAction(TrackingAction action) {
        this.action = action;
    }

    public String getTokyCall() {
        return tokyCall;
    }

    public void setTokyCall(String tokyCall) {
        this.tokyCall = tokyCall;
    }
}
