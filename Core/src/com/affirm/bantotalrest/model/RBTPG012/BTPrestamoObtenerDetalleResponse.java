package com.affirm.bantotalrest.model.RBTPG012;

import com.affirm.bantotalrest.model.common.BTResponseData;
import com.affirm.bantotalrest.model.common.SBTPCOInformacionFATCA;
import com.affirm.bantotalrest.model.common.SDTPrestamo;

public class BTPrestamoObtenerDetalleResponse extends BTResponseData {

    private SDTPrestamo sdtPrestamo;

    public SDTPrestamo getSdtPrestamo() {
        return sdtPrestamo;
    }

    public void setSdtPrestamo(SDTPrestamo sdtPrestamo) {
        this.sdtPrestamo = sdtPrestamo;
    }
}
