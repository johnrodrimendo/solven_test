package com.affirm.bantotalrest.model.common;

public class SBTSimulacionPrestamo {

    private String fechaValor;
    private Double capital;
    private Double valorCuota;
    private Double intereses;
    private Double tasa;
    private Double tasaEfectiva;
    private Double tasaNominalAnual;
    private Double tasaEfectivaAnual;
    private Double totalPrestamo;
    private Long operacionUId;
    private String fechaPrimerPago;
    private String fechaVencimiento;
    private Integer plazo;
    private SBTProducto producto;
    private SBTConcepto otrosConceptos;
    private CronogramaData cronograma;

    public String getFechaValor() {
        return fechaValor;
    }

    public void setFechaValor(String fechaValor) {
        this.fechaValor = fechaValor;
    }

    public Double getCapital() {
        return capital;
    }

    public void setCapital(Double capital) {
        this.capital = capital;
    }

    public Double getValorCuota() {
        return valorCuota;
    }

    public void setValorCuota(Double valorCuota) {
        this.valorCuota = valorCuota;
    }

    public Double getIntereses() {
        return intereses;
    }

    public void setIntereses(Double intereses) {
        this.intereses = intereses;
    }

    public Double getTasa() {
        return tasa;
    }

    public void setTasa(Double tasa) {
        this.tasa = tasa;
    }

    public Double getTasaEfectiva() {
        return tasaEfectiva;
    }

    public void setTasaEfectiva(Double tasaEfectiva) {
        this.tasaEfectiva = tasaEfectiva;
    }

    public Double getTasaNominalAnual() {
        return tasaNominalAnual;
    }

    public void setTasaNominalAnual(Double tasaNominalAnual) {
        this.tasaNominalAnual = tasaNominalAnual;
    }

    public Double getTasaEfectivaAnual() {
        return tasaEfectivaAnual;
    }

    public void setTasaEfectivaAnual(Double tasaEfectivaAnual) {
        this.tasaEfectivaAnual = tasaEfectivaAnual;
    }

    public Double getTotalPrestamo() {
        return totalPrestamo;
    }

    public void setTotalPrestamo(Double totalPrestamo) {
        this.totalPrestamo = totalPrestamo;
    }

    public Long getOperacionUId() {
        return operacionUId;
    }

    public void setOperacionUId(Long operacionUId) {
        this.operacionUId = operacionUId;
    }

    public String getFechaPrimerPago() {
        return fechaPrimerPago;
    }

    public void setFechaPrimerPago(String fechaPrimerPago) {
        this.fechaPrimerPago = fechaPrimerPago;
    }

    public String getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(String fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public Integer getPlazo() {
        return plazo;
    }

    public void setPlazo(Integer plazo) {
        this.plazo = plazo;
    }

    public SBTProducto getProducto() {
        return producto;
    }

    public void setProducto(SBTProducto producto) {
        this.producto = producto;
    }

    public SBTConcepto getOtrosConceptos() {
        return otrosConceptos;
    }

    public void setOtrosConceptos(SBTConcepto otrosConceptos) {
        this.otrosConceptos = otrosConceptos;
    }

    public CronogramaData getCronograma() {
        return cronograma;
    }

    public void setCronograma(CronogramaData cronograma) {
        this.cronograma = cronograma;
    }
}
