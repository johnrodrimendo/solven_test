package com.affirm.bantotalrest.model.RBTPG042;

import com.affirm.bantotalrest.model.common.*;

import java.util.List;

public class BTConfiguracionBantotalObtenerActividadesResponse  extends BTResponseData {

    private sBTActividadList sdtActividades;

    public sBTActividadList getSdtActividades() {
        return sdtActividades;
    }

    public void setSdtActividades(sBTActividadList sdtActividades) {
        this.sdtActividades = sdtActividades;
    }

    public static class sBTActividadList{
        private List<sBTActividad> sBTActividad;

        public List<com.affirm.bantotalrest.model.common.sBTActividad> getsBTActividad() {
            return sBTActividad;
        }

        public void setsBTActividad(List<com.affirm.bantotalrest.model.common.sBTActividad> sBTActividad) {
            this.sBTActividad = sBTActividad;
        }
    }
}
