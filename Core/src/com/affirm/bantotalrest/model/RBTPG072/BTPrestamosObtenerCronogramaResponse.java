package com.affirm.bantotalrest.model.RBTPG072;

import com.affirm.bantotalrest.model.common.BTResponseData;
import com.affirm.bantotalrest.model.common.SDTCuotaPrestamo;

import java.util.List;

public class BTPrestamosObtenerCronogramaResponse extends BTResponseData {

    private SDTCuotaPrestamoListado sdtCuotaPrestamo;

    public SDTCuotaPrestamoListado getSdtCuotaPrestamo() {
        return sdtCuotaPrestamo;
    }

    public void setSdtCuotaPrestamo(SDTCuotaPrestamoListado sdtCuotaPrestamo) {
        this.sdtCuotaPrestamo = sdtCuotaPrestamo;
    }

    public static class SDTCuotaPrestamoListado {
        private List<SDTCuotaPrestamo> sBTCuotaPrestamo;

        public List<SDTCuotaPrestamo> getsBTCuotaPrestamo() {
            return sBTCuotaPrestamo;
        }

        public void setsBTCuotaPrestamo(List<SDTCuotaPrestamo> sBTCuotaPrestamo) {
            this.sBTCuotaPrestamo = sBTCuotaPrestamo;
        }
    }

    public Double getTotalMontoDeudaImpaga() {
        if (sdtCuotaPrestamo != null && sdtCuotaPrestamo.getsBTCuotaPrestamo() != null) {
            return sdtCuotaPrestamo.getsBTCuotaPrestamo().stream().filter(c -> c.getEstado() != null && (c.getEstado().equalsIgnoreCase("Impaga") || c.getEstado().equalsIgnoreCase("Pago parcial")))
                    .mapToDouble(c -> c.getTotal()).sum();
        }
        return null;
    }

}
