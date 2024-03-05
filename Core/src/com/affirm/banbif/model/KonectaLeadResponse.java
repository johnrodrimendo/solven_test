package com.affirm.banbif.model;

import com.affirm.sentinel.rest.CredencialesRequest;

public class KonectaLeadResponse extends CredencialesRequest {

    private Boolean status;
    private String msg;

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
