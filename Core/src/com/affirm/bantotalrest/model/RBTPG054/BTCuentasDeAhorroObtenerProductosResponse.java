package com.affirm.bantotalrest.model.RBTPG054;

import com.affirm.bantotalrest.model.common.BTResponseData;
import com.affirm.bantotalrest.model.common.SBTProducto;

import java.util.List;

public class BTCuentasDeAhorroObtenerProductosResponse extends BTResponseData {

    private ListSBTProducto sdtProductos;

    public static class ListSBTProducto{
        private List<SBTProducto> sBTProducto;

        public List<SBTProducto> getsBTProducto() {
            return sBTProducto;
        }

        public void setsBTProducto(List<SBTProducto> sBTProducto) {
            this.sBTProducto = sBTProducto;
        }
    }

    public ListSBTProducto getSdtProductos() {
        return sdtProductos;
    }

    public void setSdtProductos(ListSBTProducto sdtProductos) {
        this.sdtProductos = sdtProductos;
    }
}
