package com.affirm.bantotalrest.model.customs;

import com.affirm.bantotalrest.model.common.BtRequestData;

public class BPBAZServicesGenerarCCIRequest extends BtRequestData {

    private Long OperacionUID;

    public Long getOperacionUID() {
        return OperacionUID;
    }

    public void setOperacionUID(Long operacionUID) {
        OperacionUID = operacionUID;
    }
}
