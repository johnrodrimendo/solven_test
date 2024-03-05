package com.affirm.bancodelsol.model;

import com.affirm.common.util.JsonUtil;
import org.apache.commons.lang.time.DateUtils;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

public class CampaniaBds implements Serializable {

    private Integer id;
    private String cuit;
    private Double montoMaximo;
    private Double tasa;
    private Integer plazoMaximo;
    private Double rciMaximo;
    private Double ingresos;
    private Double endeudamiento;
    private Double factorCuotaMaxima;
    private String cbu;
    private Date vigencia;
    private Double vcuotas;
    private String tipoCampania;
    private String datosPypNosis; // TODO: json
    private Double tna;

    public void fillFromDb(JSONObject json) {
        setId(JsonUtil.getIntFromJson(json, "id", null));
        setCuit(JsonUtil.getStringFromJson(json, "cuit", null));
        setMontoMaximo(JsonUtil.getDoubleFromJson(json, "monto_maximo", null));
        setTasa(JsonUtil.getDoubleFromJson(json, "tasa", null));
        setPlazoMaximo(JsonUtil.getIntFromJson(json, "plazo_maximo", null));
        setRciMaximo(JsonUtil.getDoubleFromJson(json, "rci_maximo", null));
        setIngresos(JsonUtil.getDoubleFromJson(json, "ingresos", null));
        setEndeudamiento(JsonUtil.getDoubleFromJson(json, "endeudamiento", null));
        setFactorCuotaMaxima(JsonUtil.getDoubleFromJson(json, "factor_cuota_maxima", null));
        setCbu(JsonUtil.getStringFromJson(json, "cbu", null));
        setVigencia(JsonUtil.getPostgresDateFromJson(json, "vigencia", null));
        setVcuotas(JsonUtil.getDoubleFromJson(json, "vcuotas", null));
        setTipoCampania(JsonUtil.getStringFromJson(json, "tipo_campania", null));
        if(JsonUtil.getJsonObjectFromJson(json, "datos_pyp_nosis", null) != null)
            setDatosPypNosis(JsonUtil.getJsonObjectFromJson(json, "datos_pyp_nosis", null).toString());
    }

    public Character getGender(){
        if(datosPypNosis != null){
            JSONObject json = new JSONObject(datosPypNosis);
            if(json.has("nosis")){
                JSONObject jsonNosis = json.getJSONObject("nosis");
                if(jsonNosis.has("nos_vi_sexo")){
                    if(!jsonNosis.getString("nos_vi_sexo").trim().isEmpty())
                        return jsonNosis.getString("nos_vi_sexo").charAt(0);
                }
            }
        }
        return null;
    }

    public String getNames(){
        if(datosPypNosis != null){
            JSONObject json = new JSONObject(datosPypNosis);
            if(json.has("nosis")){
                JSONObject jsonNosis = json.getJSONObject("nosis");
                if(jsonNosis.has("nos_vi_nombre")){
                    if(!jsonNosis.getString("nos_vi_nombre").trim().isEmpty())
                        return jsonNosis.getString("nos_vi_nombre");
                }
            }
        }
        return null;
    }

    public String getApellido(){
        if(datosPypNosis != null){
            JSONObject json = new JSONObject(datosPypNosis);
            if(json.has("nosis")){
                JSONObject jsonNosis = json.getJSONObject("nosis");
                if(jsonNosis.has("nos_vi_apellido")){
                    if(!jsonNosis.getString("nos_vi_apellido").trim().isEmpty())
                        return jsonNosis.getString("nos_vi_apellido");
                }
            }
        }
        return null;
    }

    public boolean isVigente(){
        Date today = DateUtils.truncate(new Date(), Calendar.DATE);
        return!today.after(vigencia);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCuit() {
        return cuit;
    }

    public void setCuit(String cuit) {
        this.cuit = cuit;
    }

    public Double getMontoMaximo() {
        return montoMaximo;
    }

    public void setMontoMaximo(Double montoMaximo) {
        this.montoMaximo = montoMaximo;
    }

    public Double getTasa() {
        return tasa;
    }

    public void setTasa(Double tasa) {
        this.tasa = tasa;
    }

    public Integer getPlazoMaximo() {
        return plazoMaximo;
    }

    public void setPlazoMaximo(Integer plazoMaximo) {
        this.plazoMaximo = plazoMaximo;
    }

    public Double getIngresos() {
        return ingresos;
    }

    public void setIngresos(Double ingresos) {
        this.ingresos = ingresos;
    }

    public Double getEndeudamiento() {
        return endeudamiento;
    }

    public void setEndeudamiento(Double endeudamiento) {
        this.endeudamiento = endeudamiento;
    }

    public Double getFactorCuotaMaxima() {
        return factorCuotaMaxima;
    }

    public void setFactorCuotaMaxima(Double factorCuotaMaxima) {
        this.factorCuotaMaxima = factorCuotaMaxima;
    }

    public String getCbu() {
        return cbu;
    }

    public void setCbu(String cbu) {
        this.cbu = cbu;
    }

    public Date getVigencia() {
        return vigencia;
    }

    public void setVigencia(Date vigencia) {
        this.vigencia = vigencia;
    }

    public Double getVcuotas() {
        return vcuotas;
    }

    public void setVcuotas(Double vcuotas) {
        this.vcuotas = vcuotas;
    }

    public String getTipoCampania() {
        return tipoCampania;
    }

    public void setTipoCampania(String tipoCampania) {
        this.tipoCampania = tipoCampania;
    }

    public String getDatosPypNosis() {
        return datosPypNosis;
    }

    public void setDatosPypNosis(String datosPypNosis) {
        this.datosPypNosis = datosPypNosis;
    }

    public Double getRciMaximo() {
        return rciMaximo;
    }

    public void setRciMaximo(Double rciMaximo) {
        this.rciMaximo = rciMaximo;
    }

    public Double getTna() {
        return tna;
    }

    public void setTna(Double tna) {
        this.tna = tna;
    }

    public static boolean hasSameValues(CampaniaBds a, CampaniaBds b){
        if(a.getId().intValue() == b.getId().intValue()) return true;
        if(a.getMontoMaximo().doubleValue() != b.getMontoMaximo().doubleValue()) return false;
        if(a.getTasa().doubleValue() != b.getTasa().doubleValue()) return false;
        if(a.getPlazoMaximo().intValue() != b.getPlazoMaximo().intValue()) return false;
        if(a.getRciMaximo().doubleValue() != b.getRciMaximo().doubleValue()) return false;
        if(a.getIngresos().doubleValue() != b.getIngresos().doubleValue()) return false;
        if(a.getEndeudamiento().doubleValue() != b.getEndeudamiento().doubleValue()) return false;
        if(a.getFactorCuotaMaxima().doubleValue() != b.getFactorCuotaMaxima().doubleValue()) return false;
        if(a.getVcuotas().doubleValue() != b.getVcuotas().doubleValue()) return false;
        return true;
    }
}
