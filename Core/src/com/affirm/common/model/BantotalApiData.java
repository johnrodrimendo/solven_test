package com.affirm.common.model;

import com.affirm.bantotalrest.model.common.SDTCuotaPrestamo;

import java.util.Date;
import java.util.List;

public class BantotalApiData {

    private Long personaUId;
    private Long clienteUId;
    private Long operacionUIdSimulacion;
    private Long operacionUIdContratarCuenta;
    private Long movimientoUId;
    private Long operacionUIdSimulacionConCliente;
    private Boolean poseeCuentaExistente;
    private Long tarjetaUId;
    private Boolean validacionTarjeta;
    private Boolean detallePrestamoExiste;
    private Boolean detalleCuentaExiste;
    private Date fechaGeneracionSimulacion;
    private List<BantotalPrestamos> btPrestamos;
    private Boolean poseeTarjetaDebito;
    private Boolean creacionCliente;
    private Boolean profesionActualizada;
    private Boolean procesoEstadoDeCuenta;
    private String CCI;
    private Boolean direccionViviendaParaAgregar;
    private Boolean direccionLaboralParaAgregar;
    private Boolean direccionViviendaPersonaAgregada;
    private Boolean direccionViviendaCuentaAgregada;
    private Boolean direccionLaboralPersonaAgregada;
    private Boolean direccionLaboralCuentaAgregada;
    private Boolean ejecutarInhabilitarDireccionVivienda;
    private Boolean ejecutarInhabilitarDireccionLaboral;
    private Boolean inhabilitarDireccionViviendaEjecutada;
    private Boolean inhabilitarDireccionLaboralEjecutada;
    private Long cuentaBT;

    public Long getPersonaUId() {
        return personaUId;
    }

    public void setPersonaUId(Long personaUId) {
        this.personaUId = personaUId;
    }

    public Long getClienteUId() {
        return clienteUId;
    }

    public void setClienteUId(Long clienteUId) {
        this.clienteUId = clienteUId;
    }

    public Long getOperacionUIdSimulacion() {
        return operacionUIdSimulacion;
    }

    public void setOperacionUIdSimulacion(Long operacionUIdSimulacion) {
        this.operacionUIdSimulacion = operacionUIdSimulacion;
    }

    public Long getOperacionUIdContratarCuenta() {
        return operacionUIdContratarCuenta;
    }

    public void setOperacionUIdContratarCuenta(Long operacionUIdContratarCuenta) {
        this.operacionUIdContratarCuenta = operacionUIdContratarCuenta;
    }

    public Long getMovimientoUId() {
        return movimientoUId;
    }

    public void setMovimientoUId(Long movimientoUId) {
        this.movimientoUId = movimientoUId;
    }

    public Long getOperacionUIdSimulacionConCliente() {
        return operacionUIdSimulacionConCliente;
    }

    public void setOperacionUIdSimulacionConCliente(Long operacionUIdSimulacionConCliente) {
        this.operacionUIdSimulacionConCliente = operacionUIdSimulacionConCliente;
    }

    public Boolean getPoseeCuentaExistente() {
        return poseeCuentaExistente;
    }

    public void setPoseeCuentaExistente(Boolean poseeCuentaExistente) {
        this.poseeCuentaExistente = poseeCuentaExistente;
    }

    public Long getTarjetaUId() {
        return tarjetaUId;
    }

    public void setTarjetaUId(Long tarjetaUId) {
        this.tarjetaUId = tarjetaUId;
    }

    public Boolean getDetallePrestamoExiste() {
        return detallePrestamoExiste;
    }

    public void setDetallePrestamoExiste(Boolean detallePrestamoExiste) {
        this.detallePrestamoExiste = detallePrestamoExiste;
    }

    public Boolean getDetalleCuentaExiste() {
        return detalleCuentaExiste;
    }

    public void setDetalleCuentaExiste(Boolean detalleCuentaExiste) {
        this.detalleCuentaExiste = detalleCuentaExiste;
    }

    public List<BantotalPrestamos> getBtPrestamos() {
        return btPrestamos;
    }

    public void setBtPrestamos(List<BantotalPrestamos> btPrestamos) {
        this.btPrestamos = btPrestamos;
    }

    public static class BantotalPrestamos{
        private String idOperacionFmt;
        private Long operacionUId;
        private Date fechaValor;
        private Integer moneda;
        private Double montoCancTotal;

        public Long getOperacionUId() {
            return operacionUId;
        }

        public void setOperacionUId(Long operacionUId) {
            this.operacionUId = operacionUId;
        }

        public Date getFechaValor() {
            return fechaValor;
        }

        public void setFechaValor(Date fechaValor) {
            this.fechaValor = fechaValor;
        }

        public String getIdOperacionFmt() {
            return idOperacionFmt;
        }

        public void setIdOperacionFmt(String idOperacionFmt) {
            this.idOperacionFmt = idOperacionFmt;
        }

        public Integer getMoneda() {
            return moneda;
        }

        public void setMoneda(Integer moneda) {
            this.moneda = moneda;
        }

        public Double getMontoCancTotal() {
            return montoCancTotal;
        }

        public void setMontoCancTotal(Double montoCancTotal) {
            this.montoCancTotal = montoCancTotal;
        }
    }

    public Date getFechaGeneracionSimulacion() {
        return fechaGeneracionSimulacion;
    }

    public void setFechaGeneracionSimulacion(Date fechaGeneracionSimulacion) {
        this.fechaGeneracionSimulacion = fechaGeneracionSimulacion;
    }

    public Boolean getPoseeTarjetaDebito() {
        return poseeTarjetaDebito;
    }

    public void setPoseeTarjetaDebito(Boolean poseeTarjetaDebito) {
        this.poseeTarjetaDebito = poseeTarjetaDebito;
    }
    
    public Boolean getCreacionCliente() {
        return creacionCliente;
    }

    public void setCreacionCliente(Boolean creacionCliente) {
        this.creacionCliente = creacionCliente;
    }

    public Boolean getProfesionActualizada() {
        return profesionActualizada;
    }

    public void setProfesionActualizada(Boolean profesionActualizada) {
        this.profesionActualizada = profesionActualizada;
    }

    public Boolean getValidacionTarjeta() {
        return validacionTarjeta;
    }

    public void setValidacionTarjeta(Boolean validacionTarjeta) {
        this.validacionTarjeta = validacionTarjeta;
    }

    public String getCCI() {
        return CCI;
    }

    public void setCCI(String CCI) {
        this.CCI = CCI;
    }

    public Boolean getDireccionViviendaParaAgregar() {
        return direccionViviendaParaAgregar;
    }

    public void setDireccionViviendaParaAgregar(Boolean direccionViviendaParaAgregar) {
        this.direccionViviendaParaAgregar = direccionViviendaParaAgregar;
    }

    public Boolean getDireccionLaboralParaAgregar() {
        return direccionLaboralParaAgregar;
    }

    public void setDireccionLaboralParaAgregar(Boolean direccionLaboralParaAgregar) {
        this.direccionLaboralParaAgregar = direccionLaboralParaAgregar;
    }

    public Boolean getDireccionViviendaPersonaAgregada() {
        return direccionViviendaPersonaAgregada;
    }

    public void setDireccionViviendaPersonaAgregada(Boolean direccionViviendaPersonaAgregada) {
        this.direccionViviendaPersonaAgregada = direccionViviendaPersonaAgregada;
    }

    public Boolean getDireccionViviendaCuentaAgregada() {
        return direccionViviendaCuentaAgregada;
    }

    public void setDireccionViviendaCuentaAgregada(Boolean direccionViviendaCuentaAgregada) {
        this.direccionViviendaCuentaAgregada = direccionViviendaCuentaAgregada;
    }

    public Boolean getDireccionLaboralPersonaAgregada() {
        return direccionLaboralPersonaAgregada;
    }

    public void setDireccionLaboralPersonaAgregada(Boolean direccionLaboralPersonaAgregada) {
        this.direccionLaboralPersonaAgregada = direccionLaboralPersonaAgregada;
    }

    public Boolean getDireccionLaboralCuentaAgregada() {
        return direccionLaboralCuentaAgregada;
    }

    public void setDireccionLaboralCuentaAgregada(Boolean direccionLaboralCuentaAgregada) {
        this.direccionLaboralCuentaAgregada = direccionLaboralCuentaAgregada;
    }

    public Long getCuentaBT() {
        return cuentaBT;
    }

    public void setCuentaBT(Long cuentaBT) {
        this.cuentaBT = cuentaBT;
    }

    public Boolean getInhabilitarDireccionViviendaEjecutada() {
        return inhabilitarDireccionViviendaEjecutada;
    }

    public void setInhabilitarDireccionViviendaEjecutada(Boolean inhabilitarDireccionViviendaEjecutada) {
        this.inhabilitarDireccionViviendaEjecutada = inhabilitarDireccionViviendaEjecutada;
    }

    public Boolean getInhabilitarDireccionLaboralEjecutada() {
        return inhabilitarDireccionLaboralEjecutada;
    }

    public void setInhabilitarDireccionLaboralEjecutada(Boolean inhabilitarDireccionLaboralEjecutada) {
        this.inhabilitarDireccionLaboralEjecutada = inhabilitarDireccionLaboralEjecutada;
    }

    public Boolean getEjecutarInhabilitarDireccionVivienda() {
        return ejecutarInhabilitarDireccionVivienda;
    }

    public void setEjecutarInhabilitarDireccionVivienda(Boolean ejecutarInhabilitarDireccionVivienda) {
        this.ejecutarInhabilitarDireccionVivienda = ejecutarInhabilitarDireccionVivienda;
    }

    public Boolean getEjecutarInhabilitarDireccionLaboral() {
        return ejecutarInhabilitarDireccionLaboral;
    }

    public void setEjecutarInhabilitarDireccionLaboral(Boolean ejecutarInhabilitarDireccionLaboral) {
        this.ejecutarInhabilitarDireccionLaboral = ejecutarInhabilitarDireccionLaboral;
    }

    public Boolean getProcesoEstadoDeCuenta() {
        return procesoEstadoDeCuenta;
    }

    public void setProcesoEstadoDeCuenta(Boolean procesoEstadoDeCuenta) {
        this.procesoEstadoDeCuenta = procesoEstadoDeCuenta;
    }
}
