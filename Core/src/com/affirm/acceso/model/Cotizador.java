package com.affirm.acceso.model;

import com.affirm.common.dao.TranslatorDAO;
import com.affirm.common.model.catalog.Entity;
import com.affirm.common.model.catalog.Vehicle;
import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by dev5 on 27/07/17.
 */
public class Cotizador {

    private static final int ENTITY_ID = Entity.ACCESO;

    @SerializedName("p_id_sesion")
    private Integer sesionId;
    @SerializedName("p_co_expedi")
    private Integer nroExpediente;
    @SerializedName("p_ti_moneda")
    private String tipoMoneda;
    @SerializedName("p_ti_cuotas")
    private String tipoCuota;
    @SerializedName("p_ti_usoveh")
    private Integer usoVehiculo;
    @SerializedName("p_co_marcas")
    private Integer marca;
    @SerializedName("p_co_modelo")
    private Integer modelo;
    @SerializedName("p_ti_asegur")
    private Integer tipoSeguro;
    @SerializedName("p_fe_simula")
    private String vencimientoPrimeraCuota;
    @SerializedName("p_im_cuoini")
    private Double cuotaInicial;
    @SerializedName("p_im_descue")
    private Double descuento;
    @SerializedName("p_ca_cuotas")
    private Integer numCuotas;
    @SerializedName("p_co_polveh")
    private Integer politicaVehicular;

    private transient boolean downPaymentForced = false;

    public Cotizador(TranslatorDAO translatorDAO,
                     Integer nroExpediente,
                     Vehicle vehicle,
                     Date vencimientoPrimeraCuota,
                     Double cuotaInicial,
                     Integer numCuotas,
                     Integer politicaVehicular) throws Exception {
        setSesionId(sesionId);
        setNroExpediente(nroExpediente);
        setTipoMoneda(translatorDAO.translate(Entity.ACCESO, 33, "0", null));
        setTipoCuota(translatorDAO.translate(Entity.ACCESO, 32, "M", null));
        setUsoVehiculo(null);
        setMarca(vehicle.getBrand().getBrandId());
        setModelo(vehicle.getModelId());
        setTipoSeguro(4); // Rimac seguros
        setVencimientoPrimeraCuota(vencimientoPrimeraCuota != null ? new SimpleDateFormat("yyyy-MM-dd").format(vencimientoPrimeraCuota) : null);
        setCuotaInicial(cuotaInicial);
        setDescuento(0.0);
        setNumCuotas(numCuotas != null ? numCuotas : 0);
        setPoliticaVehicular(politicaVehicular);
    }


    public Cotizador(Integer sesionId,
                     Integer nroExpediente,
                     String tipoMoneda,
                     String tipoCuota,
                     Integer usoVehiculo,
                     Integer marca,
                     Integer modelo,
                     Integer tipoSeguro,
                     String vencimientoPrimeraCuota,
                     Double cuotaInicial,
                     Double descuento,
                     Integer numCuotas,
                     Integer politicaVehicular) {
        setSesionId(sesionId);
        setNroExpediente(nroExpediente);
        setTipoMoneda(tipoMoneda);
        setTipoCuota(tipoCuota);
        setUsoVehiculo(usoVehiculo);
        setMarca(marca);
        setModelo(modelo);
        setTipoSeguro(tipoSeguro);
        setVencimientoPrimeraCuota(vencimientoPrimeraCuota);
        setCuotaInicial(cuotaInicial);
        setDescuento(descuento);
        setNumCuotas(numCuotas);
        setPoliticaVehicular(politicaVehicular);
    }

    public Integer getSesionId() {
        return sesionId;
    }

    public void setSesionId(Integer sesionId) {
        this.sesionId = sesionId;
    }

    public Integer getNroExpediente() {
        return nroExpediente;
    }

    public void setNroExpediente(Integer nroExpediente) {
        this.nroExpediente = nroExpediente;
    }

    public String getTipoMoneda() {
        return tipoMoneda;
    }

    public void setTipoMoneda(String tipoMoneda) {
        this.tipoMoneda = tipoMoneda;
    }

    public String getTipoCuota() {
        return tipoCuota;
    }

    public void setTipoCuota(String tipoCuota) {
        this.tipoCuota = tipoCuota;
    }

    public Integer getUsoVehiculo() {
        return usoVehiculo;
    }

    public void setUsoVehiculo(Integer usoVehiculo) {
        this.usoVehiculo = usoVehiculo;
    }

    public Integer getMarca() {
        return marca;
    }

    public void setMarca(Integer marca) {
        this.marca = marca;
    }

    public Integer getModelo() {
        return modelo;
    }

    public void setModelo(Integer modelo) {
        this.modelo = modelo;
    }

    public Integer getTipoSeguro() {
        return tipoSeguro;
    }

    public void setTipoSeguro(Integer tipoSeguro) {
        this.tipoSeguro = tipoSeguro;
    }

    public String getVencimientoPrimeraCuota() {
        return vencimientoPrimeraCuota;
    }

    public void setVencimientoPrimeraCuota(String vencimientoPrimeraCuota) {
        this.vencimientoPrimeraCuota = vencimientoPrimeraCuota;
    }

    public Double getCuotaInicial() {
        return cuotaInicial;
    }

    public void setCuotaInicial(Double cuotaInicial) {
        this.cuotaInicial = cuotaInicial;
    }

    public Double getDescuento() {
        return descuento;
    }

    public void setDescuento(Double descuento) {
        this.descuento = descuento;
    }

    public Integer getNumCuotas() {
        return numCuotas;
    }

    public void setNumCuotas(Integer numCuotas) {
        this.numCuotas = numCuotas;
    }

    public Integer getPoliticaVehicular() {
        return politicaVehicular;
    }

    public void setPoliticaVehicular(Integer politicaVehicular) {
        this.politicaVehicular = politicaVehicular;
    }

    public boolean isDownPaymentForced() {
        return downPaymentForced;
    }

    public void setDownPaymentForced(boolean downPaymentForced) {
        this.downPaymentForced = downPaymentForced;
    }
}
