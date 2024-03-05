package com.affirm.common.model.transactional;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

public class AztecaGatewayBasePhone implements Serializable {

    private String numeroDocumento;
    private String celular1;
    private String celular2;
    private String celular3;
    private String celular4;
    private String celular5;
    private String nombre;
    private String apPaterno;
    private String apMaterno;
    private Double montoCampania;

    public void fillFromDb(JSONObject json) {
        setNumeroDocumento(JsonUtil.getStringFromJson(json, "numero_documento", null));
        setCelular1(JsonUtil.getStringFromJson(json, "celular_1", null));
        setCelular2(JsonUtil.getStringFromJson(json, "celular_2", null));
        setCelular3(JsonUtil.getStringFromJson(json, "celular_3", null));
        setCelular4(JsonUtil.getStringFromJson(json, "celular_4", null));
        setCelular5(JsonUtil.getStringFromJson(json, "celular_5", null));
        setNombre(JsonUtil.getStringFromJson(json, "nombre", null));
        setApPaterno(JsonUtil.getStringFromJson(json, "ap_paterno", null));
        setApMaterno(JsonUtil.getStringFromJson(json, "ap_materno", null));
        setMontoCampania(JsonUtil.getDoubleFromJson(json, "monto_campania", null));
    }

    public String getPhoneToSend() {
        return celular1 != null ? celular1 : (celular2 != null ? celular2 : (celular3 != null ? celular3 : (celular4 != null ? celular4 : celular5)));
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public String getCelular1() {
        return celular1;
    }

    public void setCelular1(String celular1) {
        this.celular1 = celular1;
    }

    public String getCelular2() {
        return celular2;
    }

    public void setCelular2(String celular2) {
        this.celular2 = celular2;
    }

    public String getCelular3() {
        return celular3;
    }

    public void setCelular3(String celular3) {
        this.celular3 = celular3;
    }

    public String getCelular4() {
        return celular4;
    }

    public void setCelular4(String celular4) {
        this.celular4 = celular4;
    }

    public String getCelular5() {
        return celular5;
    }

    public void setCelular5(String celular5) {
        this.celular5 = celular5;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
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

    public Double getMontoCampania() {
        return montoCampania;
    }

    public void setMontoCampania(Double montoCampania) {
        this.montoCampania = montoCampania;
    }

    public String getNameAndFirstSurname(){
        if(apPaterno == null) return nombre;
        return String.format("%s %s", nombre, apPaterno).trim();
    }
}
