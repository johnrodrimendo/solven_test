package com.affirm.bantotalrest.model.RBTPG182;

import com.affirm.bantotalrest.model.common.BTResponseData;

import java.util.List;

public class ObtenerCuentasClienteResponse extends BTResponseData {

    private SdtCuentasCliente sdtCuentasCliente;

    public SdtCuentasCliente getSdtCuentasCliente() {
        return sdtCuentasCliente;
    }

    public void setSdtCuentasCliente(SdtCuentasCliente sdtCuentasCliente) {
        this.sdtCuentasCliente = sdtCuentasCliente;
    }

    public static class SBTCuentaCliente{
        private Long clienteUId;
        private String representativo;
        private sBTTipoIntegrante titularidad;

        public Long getClienteUId() {
            return clienteUId;
        }

        public void setClienteUId(Long clienteUId) {
            this.clienteUId = clienteUId;
        }

        public String getRepresentativo() {
            return representativo;
        }

        public void setRepresentativo(String representativo) {
            this.representativo = representativo;
        }

        public sBTTipoIntegrante getTitularidad() {
            return titularidad;
        }

        public void setTitularidad(sBTTipoIntegrante titularidad) {
            this.titularidad = titularidad;
        }
    }

    public static class sBTTipoIntegrante{
        private Integer identificador;
        private String descripcion;

        public Integer getIdentificador() {
            return identificador;
        }

        public void setIdentificador(Integer identificador) {
            this.identificador = identificador;
        }

        public String getDescripcion() {
            return descripcion;
        }

        public void setDescripcion(String descripcion) {
            this.descripcion = descripcion;
        }
    }

    public static class SdtCuentasCliente{
        private List<SBTCuentaCliente> sBTCuentaCliente;
        private List<SBTCuentaCliente> cuenta;

        public List<SBTCuentaCliente> getsBTCuentaCliente() {
            if(cuenta != null && sBTCuentaCliente == null) return  cuenta;
            return sBTCuentaCliente;
        }

        public void setsBTCuentaCliente(List<SBTCuentaCliente> sBTCuentaCliente) {
            this.sBTCuentaCliente = sBTCuentaCliente;
        }

        public List<SBTCuentaCliente> getCuenta() {
            return cuenta;
        }

        public void setCuenta(List<SBTCuentaCliente> cuenta) {
            this.cuenta = cuenta;
        }
    }

}
