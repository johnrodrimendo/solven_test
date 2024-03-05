package com.affirm.bantotalrest.model.customs;

import com.affirm.bantotalrest.model.common.BtRequestData;

public class BTCorresponsalesObtenerDetallePagoDeCuotaRequest extends BtRequestData {

    private Long IdTrans;

    public Long getIdTrans() {
        return IdTrans;
    }

    public void setIdTrans(Long idTrans) {
        IdTrans = idTrans;
    }
}
