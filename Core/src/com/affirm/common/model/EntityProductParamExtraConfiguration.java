package com.affirm.common.model;

public class EntityProductParamExtraConfiguration {

    private Boolean isCreditAfterApproval;
    private String messageInManualApproval;

    public Boolean getCreditAfterApproval() {
        return isCreditAfterApproval;
    }

    public void setCreditAfterApproval(Boolean creditAfterApproval) {
        isCreditAfterApproval = creditAfterApproval;
    }

    public String getMessageInManualApproval() {
        return messageInManualApproval;
    }

    public void setMessageInManualApproval(String messageInManualApproval) {
        this.messageInManualApproval = messageInManualApproval;
    }
}
