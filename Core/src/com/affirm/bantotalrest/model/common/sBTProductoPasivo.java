package com.affirm.bantotalrest.model.common;

public class sBTProductoPasivo {

    private Long operacionUId;
    private String tipoProducto;
    private String estado;
    private String subCuenta;
    private Double saldo;
    private String idOperacionFmt;
    private String idOperacionBT;
    private SBTProducto producto;
    private String sucursal;

    public Long getOperacionUId() {
        return operacionUId;
    }

    public void setOperacionUId(Long operacionUId) {
        this.operacionUId = operacionUId;
    }

    public String getTipoProducto() {
        return tipoProducto;
    }

    public void setTipoProducto(String tipoProducto) {
        this.tipoProducto = tipoProducto;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getSubCuenta() {
        return subCuenta;
    }

    public void setSubCuenta(String subCuenta) {
        this.subCuenta = subCuenta;
    }

    public Double getSaldo() {
        return saldo;
    }

    public void setSaldo(Double saldo) {
        this.saldo = saldo;
    }

    public String getIdOperacionFmt() {
        return idOperacionFmt;
    }

    public void setIdOperacionFmt(String idOperacionFmt) {
        this.idOperacionFmt = idOperacionFmt;
    }

    public String getIdOperacionBT() {
        return idOperacionBT;
    }

    public void setIdOperacionBT(String idOperacionBT) {
        this.idOperacionBT = idOperacionBT;
    }

    public SBTProducto getProducto() {
        return producto;
    }

    public void setProducto(SBTProducto producto) {
        this.producto = producto;
    }

    public String getSucursal() {
        return sucursal;
    }

    public void setSucursal(String sucursal) {
        this.sucursal = sucursal;
    }
}
