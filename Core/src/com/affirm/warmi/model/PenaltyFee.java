package com.affirm.warmi.model;

import java.util.List;

public class PenaltyFee {

    private List<ElectoralFine> electoralFines;
    private List<DriverFine> driverFines;
    private ChildSupportJudgement childSupportJudgement;

    public List<ElectoralFine> getElectoralFines() {
        return electoralFines;
    }

    public void setElectoralFines(List<ElectoralFine> electoralFines) {
        this.electoralFines = electoralFines;
    }

    public List<DriverFine> getDriverFines() {
        return driverFines;
    }

    public void setDriverFines(List<DriverFine> driverFines) {
        this.driverFines = driverFines;
    }

    public ChildSupportJudgement getChildSupportJudgement() {
        return childSupportJudgement;
    }

    public void setChildSupportJudgement(ChildSupportJudgement childSupportJudgement) {
        this.childSupportJudgement = childSupportJudgement;
    }
}
