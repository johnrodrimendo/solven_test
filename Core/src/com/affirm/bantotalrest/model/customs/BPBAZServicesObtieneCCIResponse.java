package com.affirm.bantotalrest.model.customs;

import com.affirm.bantotalrest.model.common.BTResponseData;

public class BPBAZServicesObtieneCCIResponse extends BTResponseData {

    private String CCI;

    public String getCCI() {
        return CCI;
    }

    public void setCCI(String CCI) {
        this.CCI = CCI;
    }
}
