package com.affirm.common.model.form;

import com.affirm.common.util.FormGeneric;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;

public class Question86Form extends FormGeneric implements Serializable {

    private HttpServletRequest request;
    private String ioBlackbox;

    public HttpServletRequest getRequest() {
        return request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public String getIoBlackbox() {
        return ioBlackbox;
    }

    public void setIoBlackbox(String ioBlackbox) {
        this.ioBlackbox = ioBlackbox;
    }
}