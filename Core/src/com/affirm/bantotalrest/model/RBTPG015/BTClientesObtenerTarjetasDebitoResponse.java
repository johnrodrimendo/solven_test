package com.affirm.bantotalrest.model.RBTPG015;

import com.affirm.bantotalrest.model.common.BTResponseData;
import com.affirm.bantotalrest.model.common.SBTPCOInformacionFATCA;
import com.affirm.bantotalrest.model.common.SDTProductosTarjeta;

import java.util.List;

public class BTClientesObtenerTarjetasDebitoResponse extends BTResponseData {

    private ProductosTarjetaDebitoListado sdtProductosTarjeta;

    public ProductosTarjetaDebitoListado getSdtProductosTarjeta() {
        return sdtProductosTarjeta;
    }

    public void setSdtProductosTarjeta(ProductosTarjetaDebitoListado sdtProductosTarjeta) {
        this.sdtProductosTarjeta = sdtProductosTarjeta;
    }

    public static class ProductosTarjetaDebitoListado{
        private List<SDTProductosTarjeta> sBDProductoTarjeta;

        public List<SDTProductosTarjeta> getsBDProductoTarjeta() {
            return sBDProductoTarjeta;
        }

        public void setsBDProductoTarjeta(List<SDTProductosTarjeta> sBDProductoTarjeta) {
            this.sBDProductoTarjeta = sBDProductoTarjeta;
        }
    }

}
