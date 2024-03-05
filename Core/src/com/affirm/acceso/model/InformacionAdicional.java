package com.affirm.acceso.model;

import com.affirm.common.dao.TranslatorDAO;
import com.google.gson.annotations.SerializedName;

/**
 * Created by dev5 on 31/07/17.
 */
public class InformacionAdicional {

    @SerializedName("p_id_sesion")
    private Integer sesionId;
    @SerializedName("p_co_expedi")
    private Integer nroExpediente;
    @SerializedName("p_ti_solici")
    private String tipoSolicitante;
    @SerializedName("p_id_nacion")
    private Integer nacionalidad;
    @SerializedName("p_no_carlab")
    private String cargoLaboral;
    @SerializedName("p_no_cenlab")
    private String centroLaboral;
    @SerializedName("p_co_doctri")
    private String rucCentroLaboral;
    @SerializedName("p_no_acteco")
    private String actividadEmpresa;
    @SerializedName("p_ti_nivedu")
    private String gradoInstruccion;
    @SerializedName("p_no_corele")
    private String email;
    @SerializedName("p_fe_inglab")
    private String fechaIngreso;
    @SerializedName("p_nu_telefo")
    private String telefonoLaboral;
    @SerializedName("p_im_ingnet")
    private Double ingresos;
    @SerializedName("p_ti_ocupac")
    private String tipoOcupacion;

    public InformacionAdicional(
            TranslatorDAO translatorDAO,
            Integer entityId,
            Integer nroExpediente,
            Integer nacionalidad,
            String cargoLaboral,
            String centroLaboral,
            String rucCentroLaboral,
            String actividadEmpresa,
            String gradoInstruccion,
            String email,
            String fechaIngreso,
            String telefonoLaboral,
            Double ingresos,
            String tipoOcupacion) throws Exception{

        setNroExpediente(nroExpediente);
        setTipoSolicitante("TI");
        setNacionalidad(Integer.valueOf(translatorDAO.translate(entityId, 38, String.valueOf(nacionalidad), null)));
        setCargoLaboral(cargoLaboral);
        setCentroLaboral(centroLaboral);
        setRucCentroLaboral(rucCentroLaboral);
        setActividadEmpresa(actividadEmpresa);
        setGradoInstruccion(translatorDAO.translate(entityId, 24, gradoInstruccion, null));
        setEmail(email);
        setFechaIngreso(fechaIngreso);
        setTelefonoLaboral(telefonoLaboral);
        setIngresos(ingresos);
        setTipoOcupacion(tipoOcupacion);
    }

    public InformacionAdicional(
            Integer sesionId,
            Integer nroExpediente,
            String tipoSolicitante,
            Integer nacionalidad,
            String cargoLaboral,
            String centroLaboral,
            String rucCentroLaboral,
            String actividadEmpresa,
            String gradoInstruccion,
            String email,
            String fechaIngreso,
            String telefonoLaboral,
            Double ingresos,
            String tipoOcupacion){

        setSesionId(sesionId);
        setNroExpediente(nroExpediente);
        setTipoSolicitante(tipoSolicitante);
        setNacionalidad(nacionalidad);
        setCargoLaboral(cargoLaboral);
        setCentroLaboral(centroLaboral);
        setRucCentroLaboral(rucCentroLaboral);
        setActividadEmpresa(actividadEmpresa);
        setGradoInstruccion(gradoInstruccion);
        setEmail(email);
        setFechaIngreso(fechaIngreso);
        setTelefonoLaboral(telefonoLaboral);
        setIngresos(ingresos);
        setTipoOcupacion(tipoOcupacion);
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

    public String getTipoSolicitante() {
        return tipoSolicitante;
    }

    public void setTipoSolicitante(String tipoSolicitante) {
        this.tipoSolicitante = tipoSolicitante;
    }

    public Integer getNacionalidad() {
        return nacionalidad;
    }

    public void setNacionalidad(Integer nacionalidad) {
        this.nacionalidad = nacionalidad;
    }

    public String getCargoLaboral() {
        return cargoLaboral;
    }

    public void setCargoLaboral(String cargoLaboral) {
        this.cargoLaboral = cargoLaboral;
    }

    public String getCentroLaboral() {
        return centroLaboral;
    }

    public void setCentroLaboral(String centroLaboral) {
        this.centroLaboral = centroLaboral;
    }

    public String getRucCentroLaboral() {
        return rucCentroLaboral;
    }

    public void setRucCentroLaboral(String rucCentroLaboral) {
        this.rucCentroLaboral = rucCentroLaboral;
    }

    public String getActividadEmpresa() {
        return actividadEmpresa;
    }

    public void setActividadEmpresa(String actividadEmpresa) {
        this.actividadEmpresa = actividadEmpresa;
    }

    public String getGradoInstruccion() {
        return gradoInstruccion;
    }

    public void setGradoInstruccion(String gradoInstruccion) {
        this.gradoInstruccion = gradoInstruccion;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(String fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public String getTelefonoLaboral() {
        return telefonoLaboral;
    }

    public void setTelefonoLaboral(String telefonoLaboral) {
        this.telefonoLaboral = telefonoLaboral;
    }

    public Double getIngresos() {
        return ingresos;
    }

    public void setIngresos(Double ingresos) {
        this.ingresos = ingresos;
    }

    public String getTipoOcupacion() {
        return tipoOcupacion;
    }

    public void setTipoOcupacion(String tipoOcupacion) {
        this.tipoOcupacion = tipoOcupacion;
    }
}
