package com.affirm.bantotalrest.model.RBTPG100;

import com.affirm.bantotalrest.model.common.BtRequestData;

public class BTCuentasDeAhorroContratarConFacultadesRequest extends BtRequestData {

    public static final String INTEGRACION_DISTINTA = "B";
    public static final String INTEGRACION_CONJUNTA = "C";

    private Long clienteUId;
    private Long productoUId;
    private String nombreSubCuenta;
    private String tipoIntegracion;

    public Long getClienteUId() {
        return clienteUId;
    }

    public void setClienteUId(Long clienteUId) {
        this.clienteUId = clienteUId;
    }

    public Long getProductoUId() {
        return productoUId;
    }

    public void setProductoUId(Long productoUId) {
        this.productoUId = productoUId;
    }

    public String getNombreSubCuenta() {
        return nombreSubCuenta;
    }

    public void setNombreSubCuenta(String nombreSubCuenta) {
        this.nombreSubCuenta = nombreSubCuenta;
    }

    public String getTipoIntegracion() {
        return tipoIntegracion;
    }

    public void setTipoIntegracion(String tipoIntegracion) {
        this.tipoIntegracion = tipoIntegracion;
    }
}
