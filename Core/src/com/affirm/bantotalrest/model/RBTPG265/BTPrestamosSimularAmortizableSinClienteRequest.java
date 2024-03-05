package com.affirm.bantotalrest.model.RBTPG265;

import com.affirm.bantotalrest.model.common.BtRequestData;

public class BTPrestamosSimularAmortizableSinClienteRequest extends BtRequestData {

    private SBTDatosAmortizable sdtDatosAmortizable;

    public SBTDatosAmortizable getSdtDatosAmortizable() {
        return sdtDatosAmortizable;
    }

    public void setSdtDatosAmortizable(SBTDatosAmortizable sdtDatosAmortizable) {
        this.sdtDatosAmortizable = sdtDatosAmortizable;
    }

    public static class SBTDatosAmortizable{
        private Long productoUId;
        private String fechaPrimerPago;
        private Double monto;
        private Integer cantidadCuotas;
        private Integer periodoCuotas;
        private Integer pizarra;
        private Double tasa;
        private Long actividad;

        public Long getProductoUId() {
            return productoUId;
        }

        public void setProductoUId(Long productoUId) {
            this.productoUId = productoUId;
        }

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

        public Integer getCantidadCuotas() {
            return cantidadCuotas;
        }

        public void setCantidadCuotas(Integer cantidadCuotas) {
            this.cantidadCuotas = cantidadCuotas;
        }

        public Integer getPeriodoCuotas() {
            return periodoCuotas;
        }

        public void setPeriodoCuotas(Integer periodoCuotas) {
            this.periodoCuotas = periodoCuotas;
        }

        public Integer getPizarra() {
            return pizarra;
        }

        public void setPizarra(Integer pizarra) {
            this.pizarra = pizarra;
        }

        public Double getTasa() {
            return tasa;
        }

        public void setTasa(Double tasa) {
            this.tasa = tasa;
        }

        public Long getActividad() {
            return actividad;
        }

        public void setActividad(Long actividad) {
            this.actividad = actividad;
        }
    }
}
