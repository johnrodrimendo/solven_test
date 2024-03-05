package com.affirm.bantotalrest.model.RBTPG011;

import com.affirm.bantotalrest.model.common.BTResponseData;

import java.util.List;

public class ObtenerPrestamosClienteResponse extends BTResponseData {

    private SBTProductosPrestamos sdtProductosPrestamos;

    public SBTProductosPrestamos getSdtProductosPrestamos() {
        return sdtProductosPrestamos;
    }

    public void setSdtProductosPrestamos(SBTProductosPrestamos sdtProductosPrestamos) {
        this.sdtProductosPrestamos = sdtProductosPrestamos;
    }

    public static class SBTProductoPrestamo {
        private Long operacionUId;
        private String idOperacionFmt;
        private String idOperacionBT;
        private SBTProducto producto;
        private String sucursal;
        private Double saldo;
        private String estado;

        public Long getOperacionUId() {
            return operacionUId;
        }

        public void setOperacionUId(Long operacionUId) {
            this.operacionUId = operacionUId;
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

        public Double getSaldo() {
            return saldo;
        }

        public void setSaldo(Double saldo) {
            this.saldo = saldo;
        }

        public String getEstado() {
            return estado;
        }

        public void setEstado(String estado) {
            this.estado = estado;
        }
    }

    public static class SBTProducto {
        private Long productoUId;
        private String nombre;
        private String moneda;
        private String papel;

        public Long getProductoUId() {
            return productoUId;
        }

        public void setProductoUId(Long productoUId) {
            this.productoUId = productoUId;
        }

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public String getMoneda() {
            return moneda;
        }

        public void setMoneda(String moneda) {
            this.moneda = moneda;
        }

        public String getPapel() {
            return papel;
        }

        public void setPapel(String papel) {
            this.papel = papel;
        }
    }

    public static class SBTProductosPrestamos {
        private List<SBTProductoPrestamo> sBTProductoPrestamo;

        public List<SBTProductoPrestamo> getsBTProductoPrestamo() {
            return sBTProductoPrestamo;
        }

        public void setsBTProductoPrestamo(List<SBTProductoPrestamo> sBTProductoPrestamo) {
            this.sBTProductoPrestamo = sBTProductoPrestamo;
        }
    }

}
