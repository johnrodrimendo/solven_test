package com.affirm.bantotalrest.model.RBTPG036;

import com.affirm.bantotalrest.model.common.BTResponseData;
import com.affirm.bantotalrest.model.common.SdtProfesiones;

import java.util.List;

public class BTPersonasObtenerProfesionesResponse extends BTResponseData {

    private ListaObtenerProfesiones sdtProfesiones;

    public ListaObtenerProfesiones getSdtProfesiones() {
        return sdtProfesiones;
    }

    public void setSdtProfesiones(ListaObtenerProfesiones sdtProfesiones) {
        this.sdtProfesiones = sdtProfesiones;
    }

    public static class ListaObtenerProfesiones{

        private List<SdtProfesiones> sBTProfesion;

        public List<SdtProfesiones> getsBTProfesion() {
            return sBTProfesion;
        }

        public void setsBTProfesion(List<SdtProfesiones> sBTProfesion) {
            this.sBTProfesion = sBTProfesion;
        }
    }
}
