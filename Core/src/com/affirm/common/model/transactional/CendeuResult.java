package com.affirm.common.model.transactional;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

public class CendeuResult {

    private Integer codMes;
    private String entityCode;
    private Integer tipoDocumento;
    private String numDocumento;
    private String actividad;
    private Integer situacion;
    private Double prestamos;
    private Integer participantes;
    private Double garantiasOtorgadas;
    private Double otrosConceptos;

    public void fillFromDb(JSONObject json) {
        setCodMes(JsonUtil.getIntFromJson(json, "cod_mes", null));
        setEntityCode(JsonUtil.getStringFromJson(json, "entity_code", null));
        setTipoDocumento(JsonUtil.getIntFromJson(json, "tipo_documento", null));
        setNumDocumento(JsonUtil.getStringFromJson(json, "num_documento", null));
        setActividad(JsonUtil.getStringFromJson(json, "actividad", null));
        setSituacion(JsonUtil.getIntFromJson(json, "situacion", null));
        setPrestamos(JsonUtil.getDoubleFromJson(json, "prestamos", null));
        setParticipantes(JsonUtil.getIntFromJson(json, "participantes", null));
        setGarantiasOtorgadas(JsonUtil.getDoubleFromJson(json, "garantias_otorgadas", null));
        setOtrosConceptos(JsonUtil.getDoubleFromJson(json, "otros_conceptos", null));
    }

    public Integer getCodMes() {
        return codMes;
    }

    public void setCodMes(Integer codMes) {
        this.codMes = codMes;
    }

    public String getEntityCode() {
        return entityCode;
    }

    public void setEntityCode(String entityCode) {
        this.entityCode = entityCode;
    }

    public Integer getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(Integer tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getNumDocumento() {
        return numDocumento;
    }

    public void setNumDocumento(String numDocumento) {
        this.numDocumento = numDocumento;
    }

    public String getActividad() {
        return actividad;
    }

    public void setActividad(String actividad) {
        this.actividad = actividad;
    }

    public Integer getSituacion() {
        return situacion;
    }

    public void setSituacion(Integer situacion) {
        this.situacion = situacion;
    }

    public Double getPrestamos() {
        return prestamos;
    }

    public void setPrestamos(Double prestamos) {
        this.prestamos = prestamos;
    }

    public Integer getParticipantes() {
        return participantes;
    }

    public void setParticipantes(Integer participantes) {
        this.participantes = participantes;
    }

    public Double getGarantiasOtorgadas() {
        return garantiasOtorgadas;
    }

    public void setGarantiasOtorgadas(Double garantiasOtorgadas) {
        this.garantiasOtorgadas = garantiasOtorgadas;
    }

    public Double getOtrosConceptos() {
        return otrosConceptos;
    }

    public void setOtrosConceptos(Double otrosConceptos) {
        this.otrosConceptos = otrosConceptos;
    }
}
