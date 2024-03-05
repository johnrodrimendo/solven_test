package com.affirm.bantotalrest.model.RBTPG075;

import com.affirm.bantotalrest.model.common.BtRequestData;

public class BTPrestamosSimularRequest extends BtRequestData {

   private SBTPrestamoAlta sdtPrestamo;

    public SBTPrestamoAlta getSdtPrestamo() {
        return sdtPrestamo;
    }

    public void setSdtPrestamo(SBTPrestamoAlta sdtPrestamo) {
        this.sdtPrestamo = sdtPrestamo;
    }

    public static class SBTPrestamoAlta{

        private String fechaPrimerPago;
        private Double monto;
        private Double tasa;
        private Integer periodoCuotas;
        private Integer cantidadCuotas;
        private Long productoUId;
        private Integer pizarra;
        private Long clienteUId;
        private Long actividad;

        public String getFechaPrimerPago() {
            return fechaPrimerPago;
        }

        public void setFechaPrimerPago(String fechaPrimerPago) {
            this.fechaPrimerPago = fechaPrimerPago;
        }

        public Double getMonto() {
            return monto;
        }

        public void setMonto(Double monto) {
            this.monto = monto;
        }

        public Double getTasa() {
            return tasa;
        }

        public void setTasa(Double tasa) {
            this.tasa = tasa;
        }

        public Integer getPeriodoCuotas() {
            return periodoCuotas;
        }

        public void setPeriodoCuotas(Integer periodoCuotas) {
            this.periodoCuotas = periodoCuotas;
        }

        public Integer getCantidadCuotas() {
            return cantidadCuotas;
        }

        public void setCantidadCuotas(Integer cantidadCuotas) {
            this.cantidadCuotas = cantidadCuotas;
        }

        public Long getProductoUId() {
            return productoUId;
        }

        public void setProductoUId(Long productoUId) {
            this.productoUId = productoUId;
        }

        public Integer getPizarra() {
            return pizarra;
        }

        public void setPizarra(Integer pizarra) {
            this.pizarra = pizarra;
        }

        public Long getClienteUId() {
            return clienteUId;
        }

        public void setClienteUId(Long clienteUId) {
            this.clienteUId = clienteUId;
        }

        public Long getActividad() {
            return actividad;
        }

        public void setActividad(Long actividad) {
            this.actividad = actividad;
        }
    }

}
