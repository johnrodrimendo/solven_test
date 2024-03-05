package com.affirm.bantotalrest.model.RBTPG079;

import com.affirm.bantotalrest.model.common.*;

import java.util.List;

public class BTConfiguracionBantotalObtenerPizarrasResponse  extends BTResponseData {

    private sBTPizarraList sdtPizarras;

    public sBTPizarraList getSdtPizarras() {
        return sdtPizarras;
    }

    public void setSdtPizarras(sBTPizarraList sdtPizarras) {
        this.sdtPizarras = sdtPizarras;
    }

    public static class sBTPizarraList{
        private List<sBTPizarra> sBTPizarra;

        public List<sBTPizarra> getsBTPizarra() {
            return sBTPizarra;
        }

        public void setsBTPizarra(List<sBTPizarra> sBTPizarra) {
            this.sBTPizarra = sBTPizarra;
        }
    }
}
