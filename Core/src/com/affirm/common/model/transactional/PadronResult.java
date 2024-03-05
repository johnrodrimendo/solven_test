package com.affirm.common.model.transactional;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * @author Romulo Galindo Tanta
 */
public class PadronResult implements Serializable {

    private Integer queryId;
    private Integer inDocumentType;
    private String inDocumentNumber;
    private String fullName;
    private String matricula;
    private String distritoName;
    private String circuito;
    private String seccion;
    private String establecimiento;
    private String direccion;
    private String mesa;
    private String orden;

    public void fillFromDb(JSONObject json) {
        setQueryId(JsonUtil.getIntFromJson(json, "queryId", null));
        setInDocumentType(JsonUtil.getIntFromJson(json, "in_document_type", null));
        setInDocumentNumber(JsonUtil.getStringFromJson(json, "in_document_number", null));
        setFullName(JsonUtil.getStringFromJson(json, "full_name", null));
        setMatricula(JsonUtil.getStringFromJson(json, "matricula", null));
        setDistritoName(JsonUtil.getStringFromJson(json, "distritoname", null));
        setCircuito(JsonUtil.getStringFromJson(json, "circuito", null));
        setSeccion(JsonUtil.getStringFromJson(json, "seccion", null));
        setEstablecimiento(JsonUtil.getStringFromJson(json, "establecimiento", null));
        setDireccion(JsonUtil.getStringFromJson(json, "direccion", null));
        setMesa(JsonUtil.getStringFromJson(json, "mesa", null));
        setOrden(JsonUtil.getStringFromJson(json, "orden", null));
    }

    public Integer getQueryId() {
        return queryId;
    }

    public void setQueryId(Integer queryId) {
        this.queryId = queryId;
    }

    public Integer getInDocumentType() {
        return inDocumentType;
    }

    public void setInDocumentType(Integer inDocumentType) {
        this.inDocumentType = inDocumentType;
    }

    public String getInDocumentNumber() {
        return inDocumentNumber;
    }

    public void setInDocumentNumber(String inDocumentNumber) {
        this.inDocumentNumber = inDocumentNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getDistritoName() {
        return distritoName;
    }

    public void setDistritoName(String distritoName) {
        this.distritoName = distritoName;
    }

    public String getCircuito() {
        return circuito;
    }

    public void setCircuito(String circuito) {
        this.circuito = circuito;
    }

    public String getSeccion() {
        return seccion;
    }

    public void setSeccion(String seccion) {
        this.seccion = seccion;
    }

    public String getEstablecimiento() {
        return establecimiento;
    }

    public void setEstablecimiento(String establecimiento) {
        this.establecimiento = establecimiento;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getMesa() {
        return mesa;
    }

    public void setMesa(String mesa) {
        this.mesa = mesa;
    }

    public String getOrden() {
        return orden;
    }

    public void setOrden(String orden) {
        this.orden = orden;
    }
}
