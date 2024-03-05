package com.affirm.bantotalrest.model.RBTPG007;

import com.affirm.bantotalrest.model.common.BTResponseData;
import com.affirm.bantotalrest.model.common.SDTCajaAhorro;
import com.affirm.bantotalrest.model.common.SDTPrestamo;

public class BTCuentasDeAhorroObtenerDatosResponse extends BTResponseData {

    private SDTCajaAhorro sdtCajaAhorro;

    public SDTCajaAhorro getSdtCajaAhorro() {
        return sdtCajaAhorro;
    }

    public void setSdtCajaAhorro(SDTCajaAhorro sdtCajaAhorro) {
        this.sdtCajaAhorro = sdtCajaAhorro;
    }
}
