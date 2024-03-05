package com.affirm.bantotalrest.model.RBTPG006;

import com.affirm.bantotalrest.model.common.BTResponseData;
import com.affirm.bantotalrest.model.common.SBTProducto;

import java.util.List;

public class BTClientesObtenerCuentasAhorroResponse extends BTResponseData {

    private SdtProductosPasivos sdtProductosPasivos;

    public SdtProductosPasivos getSdtProductosPasivos() {
        return sdtProductosPasivos;
    }

    public void setSdtProductosPasivos(SdtProductosPasivos sdtProductosPasivos) {
        this.sdtProductosPasivos = sdtProductosPasivos;
    }

    public static class SdtProductosPasivos{
        private List<SBTProductoPasivo> sBTProductoPasivo;

        public List<SBTProductoPasivo> getsBTProductoPasivo() {
            return sBTProductoPasivo;
        }

        public void setsBTProductoPasivo(List<SBTProductoPasivo> sBTProductoPasivo) {
            this.sBTProductoPasivo = sBTProductoPasivo;
        }
    }

    public static class SBTProductoPasivo{
        private String tipoProducto;
        private String estado;
        private String subCuenta;
        private Long operacionUId;
        private Double saldo;
        private String idOperacionFmt;
        private String idOperacionBT;
        private String sucursal;
        private SBTProducto producto;

        public SBTProducto getsBTProducto() {
            return producto;
        }

        public void setsBTProducto(SBTProducto producto) {
            this.producto = producto;
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

        public Long getOperacionUId() {
            return operacionUId;
        }

        public void setOperacionUId(Long operacionUId) {
            this.operacionUId = operacionUId;
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

        public String getSucursal() {
            return sucursal;
        }

        public void setSucursal(String sucursal) {
            this.sucursal = sucursal;
        }
    }

}
