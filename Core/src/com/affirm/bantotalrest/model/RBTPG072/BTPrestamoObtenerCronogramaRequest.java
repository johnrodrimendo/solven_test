package com.affirm.bantotalrest.model.RBTPG072;

import com.affirm.bantotalrest.model.common.BtRequestData;

public class BTPrestamoObtenerCronogramaRequest extends BtRequestData {

    private Long operacionUId;

    public Long getOperacionUId() {
        return operacionUId;
    }

    public void setOperacionUId(Long operacionUId) {
        this.operacionUId = operacionUId;
    }
}
