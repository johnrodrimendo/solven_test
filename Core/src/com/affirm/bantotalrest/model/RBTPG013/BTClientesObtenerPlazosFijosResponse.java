package com.affirm.bantotalrest.model.RBTPG013;

import com.affirm.bantotalrest.model.common.BTResponseData;
import com.affirm.bantotalrest.model.common.SDTCuotaPrestamo;
import com.affirm.bantotalrest.model.common.sBTProductoPasivo;

import java.util.List;

public class BTClientesObtenerPlazosFijosResponse extends BTResponseData {

    private SBTProductoPasivoListado sdtProductosPasivos;

    public static class SBTProductoPasivoListado {
        private List<com.affirm.bantotalrest.model.common.sBTProductoPasivo> sBTProductoPasivo;

        public List<com.affirm.bantotalrest.model.common.sBTProductoPasivo> getsBTProductoPasivo() {
            return sBTProductoPasivo;
        }

        public void setsBTProductoPasivo(List<com.affirm.bantotalrest.model.common.sBTProductoPasivo> sBTProductoPasivo) {
            this.sBTProductoPasivo = sBTProductoPasivo;
        }
    }

    public SBTProductoPasivoListado getSdtProductosPasivos() {
        return sdtProductosPasivos;
    }

    public void setSdtProductosPasivos(SBTProductoPasivoListado sdtProductosPasivos) {
        this.sdtProductosPasivos = sdtProductosPasivos;
    }
}
