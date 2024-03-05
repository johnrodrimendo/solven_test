package com.affirm.common.model.transactional;

import java.util.Date;

public class LoanApplicationFunnelStep {

    private Integer stepId;
    private Date date;

    public Integer getStepId() {
        return stepId;
    }

    public void setStepId(Integer stepId) {
        this.stepId = stepId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
