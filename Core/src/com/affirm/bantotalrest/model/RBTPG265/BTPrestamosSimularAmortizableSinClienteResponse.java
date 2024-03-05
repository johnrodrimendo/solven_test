package com.affirm.bantotalrest.model.RBTPG265;

import com.affirm.bantotalrest.model.RBTPG075.BTPrestamosSimularResponse;
import com.affirm.bantotalrest.model.common.*;

public class BTPrestamosSimularAmortizableSinClienteResponse extends BTResponseData {

    private SBTSimulacionPrestamo sdtSimulacionPrestamo;

    public SBTSimulacionPrestamo getSdtSimulacionPrestamo() {
        return sdtSimulacionPrestamo;
    }

    public void setSdtSimulacionPrestamo(SBTSimulacionPrestamo sdtSimulacionPrestamo) {
        this.sdtSimulacionPrestamo = sdtSimulacionPrestamo;
    }
}
