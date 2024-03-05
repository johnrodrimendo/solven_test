package com.affirm.acceso.model;

import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by dev5 on 31/07/17.
 */
public class Firma {

    @SerializedName("p_id_sesion")
    private Integer sesionId;
    @SerializedName("p_co_expedi")
    private Integer nroExpediente;
    @SerializedName("p_fe_regist")
    private String fechaAgendamiento;
    @SerializedName("p_co_ubifir")
    private Integer ubicacionFirma;
    @SerializedName("p_no_direcc")
    private String direccionFirma;
    @SerializedName("p_ho_inicia")
    private String horaInicio;
    @SerializedName("p_ho_finali")
    private String horaFin;
    @SerializedName("p_de_refere")
    private String referencia;


    public Firma(Integer nroExpediente,
                 String fechaAgendamiento,
                 Integer ubicacionFirma,
                 String direccionFirma,
                 String horaInicio,
                 String horaFin,
                 String referencia){
        try {
            setSesionId(sesionId);
            setNroExpediente(nroExpediente);
            Date fecha = new SimpleDateFormat("dd/MM/yyyy").parse(fechaAgendamiento);
            setFechaAgendamiento(new SimpleDateFormat("yyyy-MM-dd").format(fecha));
            setUbicacionFirma(ubicacionFirma);
            setDireccionFirma(direccionFirma);
            setHoraInicio(horaInicio);
            setHoraFin(horaFin);
            setReferencia(referencia);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public Firma(Integer nroExpediente,
                 Date fechaAgendamiento,
                 Integer ubicacionFirma,
                 String direccionFirma,
                 String horaInicio,
                 String horaFin,
                 String referencia){
        setSesionId(sesionId);
        setNroExpediente(nroExpediente);
        setFechaAgendamiento(new SimpleDateFormat("yyyy-MM-dd").format(fechaAgendamiento));
        setUbicacionFirma(ubicacionFirma);
        setDireccionFirma(direccionFirma);
        setHoraInicio(horaInicio);
        setHoraFin(horaFin);
        setReferencia(referencia);
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

    public String getFechaAgendamiento() {
        return fechaAgendamiento;
    }

    public void setFechaAgendamiento(String fechaAgendamiento) {
        this.fechaAgendamiento = fechaAgendamiento;
    }


    public Integer getUbicacionFirma() {
        return ubicacionFirma;
    }

    public void setUbicacionFirma(Integer ubicacionFirma) {
        this.ubicacionFirma = ubicacionFirma;
    }

    public String getDireccionFirma() {
        return direccionFirma;
    }

    public void setDireccionFirma(String direccionFirma) {
        this.direccionFirma = direccionFirma;
    }

    public String getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(String horaInicio) {
        this.horaInicio = horaInicio;
    }

    public String getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(String horaFin) {
        this.horaFin = horaFin;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }
}
