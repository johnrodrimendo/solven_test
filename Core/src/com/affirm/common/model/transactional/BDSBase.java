package com.affirm.common.model.transactional;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

public class BDSBase {

    private String cuit;
    private String documento;
    private String sexo;
    private String nombre;
    private Integer inhabcc;
    private Integer embajud;
    private Integer entliq;
    private Integer mora;
    private Double vcuotas;
    private Double vcapitales;

    public void fillFromDb(JSONObject json) throws Exception {
        setCuit(JsonUtil.getStringFromJson(json, "cuit", null));
        setDocumento(JsonUtil.getStringFromJson(json, "documento", null));
        setSexo(JsonUtil.getStringFromJson(json, "sexo", null));
        setNombre(JsonUtil.getStringFromJson(json, "nombre", null));
        setInhabcc(JsonUtil.getIntFromJson(json, "inhabcc", null));
        setEmbajud(JsonUtil.getIntFromJson(json, "embajud", null));
        setEntliq(JsonUtil.getIntFromJson(json, "entliq", null));
        setMora(JsonUtil.getIntFromJson(json, "mora", null));
        setVcuotas(JsonUtil.getDoubleFromJson(json, "vcuotas", null));
        setVcapitales(JsonUtil.getDoubleFromJson(json, "vcapitales", null));
    }

    public String getCuit() {
        return cuit;
    }

    public void setCuit(String cuit) {
        this.cuit = cuit;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getInhabcc() {
        return inhabcc;
    }

    public void setInhabcc(Integer inhabcc) {
        this.inhabcc = inhabcc;
    }

    public Integer getEmbajud() {
        return embajud;
    }

    public void setEmbajud(Integer embajud) {
        this.embajud = embajud;
    }

    public Integer getEntliq() {
        return entliq;
    }

    public void setEntliq(Integer entliq) {
        this.entliq = entliq;
    }

    public Integer getMora() {
        return mora;
    }

    public void setMora(Integer mora) {
        this.mora = mora;
    }

    public Double getVcuotas() {
        return vcuotas;
    }

    public void setVcuotas(Double vcuotas) {
        this.vcuotas = vcuotas;
    }

    public Double getVcapitales() {
        return vcapitales;
    }

    public void setVcapitales(Double vcapitales) {
        this.vcapitales = vcapitales;
    }
}
