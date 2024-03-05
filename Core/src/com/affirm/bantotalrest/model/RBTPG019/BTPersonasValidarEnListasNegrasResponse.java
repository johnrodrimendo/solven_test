package com.affirm.bantotalrest.model.RBTPG019;

import com.affirm.bantotalrest.model.common.BTResponseData;
import com.affirm.bantotalrest.model.common.SBTListaInhabilitados;

import java.util.List;

public class BTPersonasValidarEnListasNegrasResponse extends BTResponseData {

    private String existeEnLista;
    private SBTListaInhabilitadosList sdtListasInhabilitados;

    public static class SBTListaInhabilitadosList{
        private List<SBTListaInhabilitados> sBTListaInhabilitados;

        public List<SBTListaInhabilitados> getsBTListaInhabilitados() {
            return sBTListaInhabilitados;
        }

        public void setsBTListaInhabilitados(List<SBTListaInhabilitados> sBTListaInhabilitados) {
            this.sBTListaInhabilitados = sBTListaInhabilitados;
        }
    }

    public String getExisteEnLista() {
        return existeEnLista;
    }

    public void setExisteEnLista(String existeEnLista) {
        this.existeEnLista = existeEnLista;
    }

    public SBTListaInhabilitadosList getSdtListasInhabilitados() {
        return sdtListasInhabilitados;
    }

    public void setSdtListasInhabilitados(SBTListaInhabilitadosList sdtListasInhabilitados) {
        this.sdtListasInhabilitados = sdtListasInhabilitados;
    }
}
