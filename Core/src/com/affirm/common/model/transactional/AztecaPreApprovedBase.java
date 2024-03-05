package com.affirm.common.model.transactional;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Arrays;

public class AztecaPreApprovedBase implements Serializable {

    public static final Integer CALL_CENTER_CAMP = 29;
    public static final Integer BANKED_CAMP = 49;
    public static final Integer NO_BANKED_CAMP = 50;

    public static final Integer CALL_CENTER_CAMP_CAMPAIGN_API = 1;
    public static final Integer BANKED_CAMP_CAMPAIGN_API = 2;
    public static final Integer NO_BANKED_CAMP_CAMPAIGN_API = 3;

    private String dni;
    private String apPaterno;
    private String apMaterno;
    private String nombre;
    private Double capacidad;
    private Double ofertaMax;
    private Integer plazo;
    private Integer idCampania;
    private Integer landingPp;
    private Integer landingCc;
    private Integer landingMoto;
    private String tipoVerificacion;
    private String grupoRiesgo;
    private String tipovisita;
    private Double tasa5999;//  deprecated
    private Double tasa19999;//  deprecated
    private Double tasaMas;//  deprecated
    private Double tasa1999;
    private Double tasa20000;

    private Double tasa2999;
    private Double tasa4999;
    private Double tasa6999;
    private Double tasa9999;
    private Double tasa14999;
    private Double tasa24999;
    private Double tasa30000;

    private Double oferta12Meses;
    private Double oferta18Meses;
    private Double oferta24Meses;
    private Double oferta36Meses;
    private Double ofertaMas;

    public void fillFromDb(JSONObject json) {
        setDni(JsonUtil.getStringFromJson(json, "dni", null));
        setApPaterno(JsonUtil.getStringFromJson(json, "x_appaterno", null));
        setApMaterno(JsonUtil.getStringFromJson(json, "x_apmaterno", null));
        setNombre(JsonUtil.getStringFromJson(json, "x_nombre", null));
        setCapacidad(JsonUtil.getDoubleFromJson(json, "capacidad", null));
        setOfertaMax(JsonUtil.getDoubleFromJson(json, "oferta_max", null));
        setPlazo(JsonUtil.getIntFromJson(json, "plazo", null));
        setIdCampania(JsonUtil.getIntFromJson(json, "idcampania", null));
        setLandingPp(JsonUtil.getIntFromJson(json, "landing_pp", null));
        setLandingCc(JsonUtil.getIntFromJson(json, "landing_cc", null));
        setLandingMoto(JsonUtil.getIntFromJson(json, "landing_moto", null));
        setTipoVerificacion(JsonUtil.getStringFromJson(json, "tipo_verificacion", null));
        setGrupoRiesgo(JsonUtil.getStringFromJson(json, "grupo_riesgo", null));
        setTipovisita(JsonUtil.getStringFromJson(json, "tipovisita", null));
        setTasa1999(JsonUtil.getDoubleFromJson(json, "tasa_1999", null));
        setTasa2999(JsonUtil.getDoubleFromJson(json, "tasa_2999", null));
        setTasa4999(JsonUtil.getDoubleFromJson(json, "tasa_4999", null));
        setTasa5999(JsonUtil.getDoubleFromJson(json, "tasa_5999", null));
        setTasa6999(JsonUtil.getDoubleFromJson(json, "tasa_6999", null));
        setTasa9999(JsonUtil.getDoubleFromJson(json, "tasa_9999", null));
        setTasa14999(JsonUtil.getDoubleFromJson(json, "tasa_14999", null));
        setTasa19999(JsonUtil.getDoubleFromJson(json, "tasa_19999", null));
        setTasa20000(JsonUtil.getDoubleFromJson(json, "tasa_20000", null));
        setTasa24999(JsonUtil.getDoubleFromJson(json, "tasa_24999", null));
        setTasa30000(JsonUtil.getDoubleFromJson(json, "tasa_30000", null));
        setTasaMas(JsonUtil.getDoubleFromJson(json, "tasa_mas", null));
        setOferta12Meses(JsonUtil.getDoubleFromJson(json, "oferta_12meses", null));
        setOferta18Meses(JsonUtil.getDoubleFromJson(json, "oferta_18meses", null));
        setOferta24Meses(JsonUtil.getDoubleFromJson(json, "oferta_24meses", null));
        setOferta36Meses(JsonUtil.getDoubleFromJson(json, "oferta_36meses", null));
        setOfertaMas(JsonUtil.getDoubleFromJson(json, "oferta_mas", null));
    }

    public double getTasaForMonto(double monto){
        // This if is for theprevious version of this base. Now it returns different
        if(tasa5999 != null){
            if (monto <= 2999)
                return tasa2999;
            else if (monto <= 5999)
                return tasa5999;
            else if (monto <= 9999)
                return tasa9999;
            else
                return tasa19999;
        }else{
            if (monto <= 2999)
                return tasa2999;
            else if (monto <= 4999)
                return tasa4999;
            else if (monto <= 6999)
                return tasa6999;
            else if (monto <= 9999)
                return tasa9999;
            else if (monto <= 14999)
                return tasa14999;
            else if (monto <= 24999)
                return tasa24999;
            else
                return tasa30000;
        }
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getApPaterno() {
        return apPaterno;
    }

    public void setApPaterno(String apPaterno) {
        this.apPaterno = apPaterno;
    }

    public String getApMaterno() {
        return apMaterno;
    }

    public void setApMaterno(String apMaterno) {
        this.apMaterno = apMaterno;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Double getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(Double capacidad) {
        this.capacidad = capacidad;
    }

    public Double getOfertaMax() {
        return ofertaMax;
    }

    public void setOfertaMax(Double ofertaMax) {
        this.ofertaMax = ofertaMax;
    }

    public Integer getPlazo() {
        return plazo;
    }

    public void setPlazo(Integer plazo) {
        this.plazo = plazo;
    }

    public Integer getIdCampania() {
        return idCampania;
    }

    public void setIdCampania(Integer idCampania) {
        this.idCampania = idCampania;
    }

    public Integer getLandingPp() {
        return landingPp;
    }

    public void setLandingPp(Integer landingPp) {
        this.landingPp = landingPp;
    }

    public Integer getLandingCc() {
        return landingCc;
    }

    public void setLandingCc(Integer landingCc) {
        this.landingCc = landingCc;
    }

    public Integer getLandingMoto() {
        return landingMoto;
    }

    public void setLandingMoto(Integer landingMoto) {
        this.landingMoto = landingMoto;
    }

    public String getTipoVerificacion() {
        return tipoVerificacion;
    }

    public void setTipoVerificacion(String tipoVerificacion) {
        this.tipoVerificacion = tipoVerificacion;
    }

    public String getGrupoRiesgo() {
        return grupoRiesgo;
    }

    public void setGrupoRiesgo(String grupoRiesgo) {
        this.grupoRiesgo = grupoRiesgo;
    }

    public String getTipovisita() {
        return tipovisita;
    }

    public void setTipovisita(String tipovisita) {
        this.tipovisita = tipovisita;
    }

    public Double getTasa2999() {
        return tasa2999;
    }

    public void setTasa2999(Double tasa2999) {
        this.tasa2999 = tasa2999;
    }

    public Double getTasa5999() {
        return tasa5999;
    }

    public void setTasa5999(Double tasa5999) {
        this.tasa5999 = tasa5999;
    }

    public Double getTasa9999() {
        return tasa9999;
    }

    public void setTasa9999(Double tasa9999) {
        this.tasa9999 = tasa9999;
    }

    public Double getTasa19999() {
        return tasa19999;
    }

    public void setTasa19999(Double tasa19999) {
        this.tasa19999 = tasa19999;
    }

    public Double getTasaMas() {
        return tasaMas;
    }

    public void setTasaMas(Double tasaMas) {
        this.tasaMas = tasaMas;
    }

    public Double getOferta12Meses() {
        return oferta12Meses;
    }

    public void setOferta12Meses(Double oferta12Meses) {
        this.oferta12Meses = oferta12Meses;
    }

    public Double getOferta18Meses() {
        return oferta18Meses;
    }

    public void setOferta18Meses(Double oferta18Meses) {
        this.oferta18Meses = oferta18Meses;
    }

    public Double getOferta24Meses() {
        return oferta24Meses;
    }

    public void setOferta24Meses(Double oferta24Meses) {
        this.oferta24Meses = oferta24Meses;
    }

    public Double getOferta36Meses() {
        return oferta36Meses;
    }

    public void setOferta36Meses(Double oferta36Meses) {
        this.oferta36Meses = oferta36Meses;
    }

    public Double getOfertaMas() {
        return ofertaMas;
    }

    public void setOfertaMas(Double ofertaMas) {
        this.ofertaMas = ofertaMas;
    }

    public Double getTasa1999() {
        return tasa1999;
    }

    public void setTasa1999(Double tasa1999) {
        this.tasa1999 = tasa1999;
    }

    public Double getTasa4999() {
        return tasa4999;
    }

    public void setTasa4999(Double tasa4999) {
        this.tasa4999 = tasa4999;
    }

    public Double getTasa6999() {
        return tasa6999;
    }

    public void setTasa6999(Double tasa6999) {
        this.tasa6999 = tasa6999;
    }

    public Double getTasa14999() {
        return tasa14999;
    }

    public void setTasa14999(Double tasa14999) {
        this.tasa14999 = tasa14999;
    }

    public Double getTasa20000() {
        return tasa20000;
    }

    public void setTasa20000(Double tasa20000) {
        this.tasa20000 = tasa20000;
    }

    public Double getTasa24999() {
        return tasa24999;
    }

    public void setTasa24999(Double tasa24999) {
        this.tasa24999 = tasa24999;
    }

    public Double getTasa30000() {
        return tasa30000;
    }

    public void setTasa30000(Double tasa30000) {
        this.tasa30000 = tasa30000;
    }

    public boolean isCallCenterCamp(){
        if(this.idCampania != null){
            if(Arrays.asList(CALL_CENTER_CAMP,CALL_CENTER_CAMP_CAMPAIGN_API).contains(this.idCampania )) return true;
        }
        return false;
    }

    public boolean isBankedCamp(){
        if(this.idCampania != null){
            if(Arrays.asList(BANKED_CAMP,BANKED_CAMP_CAMPAIGN_API).contains(this.idCampania )) return true;
        }
        return false;
    }

    public boolean isNoBankedCamp(){
        if(this.idCampania != null){
            if(Arrays.asList(NO_BANKED_CAMP,NO_BANKED_CAMP_CAMPAIGN_API).contains(this.idCampania )) return true;
        }
        return false;
    }
}
