package com.affirm.common.model.rcc;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;


public class CuentaContable {

    private String codigoCuenta;
    private String nombreCuenta;
    private String codigoCuentaSoles;
    private String codigoCuentaDolares;
    private String codigoCuentaPat;
    private String tipoCuenta;
    private String codigoNivelA;
    private String codigoNivelB;
    private String codigoNivelC;
    private String codigoNivelD;
    private String codigoNivelE;
    private String codigoNivelF;
    private String codigoNivelG;
    private String codigoNivelH;

    public void fillFromDb(JSONObject json) {
        setCodigoCuenta(JsonUtil.getStringFromJson(json, "co_cuenta", null));
        setNombreCuenta(JsonUtil.getStringFromJson(json, "no_cuenta", null));
        setCodigoCuentaSoles(JsonUtil.getStringFromJson(json, "co_cuesol", null));
        setCodigoCuentaDolares(JsonUtil.getStringFromJson(json, "co_cuedol", null));
        setCodigoCuentaPat(JsonUtil.getStringFromJson(json, "co_cuepat", null));
        setTipoCuenta(JsonUtil.getStringFromJson(json, "ti_cuenta", null));
        setCodigoNivelA(JsonUtil.getStringFromJson(json, "co_nivela", null));
        setCodigoNivelB(JsonUtil.getStringFromJson(json, "co_nivelb", null));
        setCodigoNivelC(JsonUtil.getStringFromJson(json, "co_nivelc", null));
        setCodigoNivelD(JsonUtil.getStringFromJson(json, "co_niveld", null));
        setCodigoNivelE(JsonUtil.getStringFromJson(json, "co_nivele", null));
        setCodigoNivelF(JsonUtil.getStringFromJson(json, "co_nivelr", null));
        setCodigoNivelG(JsonUtil.getStringFromJson(json, "co_nivelg", null));
        setCodigoNivelH(JsonUtil.getStringFromJson(json, "co_nivelh", null));
    }

    public String getCodigoCuenta() {
        return codigoCuenta;
    }

    public void setCodigoCuenta(String codigoCuenta) {
        this.codigoCuenta = codigoCuenta;
    }

    public String getNombreCuenta() {
        return nombreCuenta;
    }

    public void setNombreCuenta(String nombreCuenta) {
        this.nombreCuenta = nombreCuenta;
    }

    public String getCodigoCuentaSoles() {
        return codigoCuentaSoles;
    }

    public void setCodigoCuentaSoles(String codigoCuentaSoles) {
        this.codigoCuentaSoles = codigoCuentaSoles;
    }

    public String getCodigoCuentaDolares() {
        return codigoCuentaDolares;
    }

    public void setCodigoCuentaDolares(String codigoCuentaDolares) {
        this.codigoCuentaDolares = codigoCuentaDolares;
    }

    public String getCodigoCuentaPat() {
        return codigoCuentaPat;
    }

    public void setCodigoCuentaPat(String codigoCuentaPat) {
        this.codigoCuentaPat = codigoCuentaPat;
    }

    public String getTipoCuenta() {
        return tipoCuenta;
    }

    public void setTipoCuenta(String tipoCuenta) {
        this.tipoCuenta = tipoCuenta;
    }

    public String getCodigoNivelA() {
        return codigoNivelA;
    }

    public void setCodigoNivelA(String codigoNivelA) {
        this.codigoNivelA = codigoNivelA;
    }

    public String getCodigoNivelB() {
        return codigoNivelB;
    }

    public void setCodigoNivelB(String codigoNivelB) {
        this.codigoNivelB = codigoNivelB;
    }

    public String getCodigoNivelC() {
        return codigoNivelC;
    }

    public void setCodigoNivelC(String codigoNivelC) {
        this.codigoNivelC = codigoNivelC;
    }

    public String getCodigoNivelD() {
        return codigoNivelD;
    }

    public void setCodigoNivelD(String codigoNivelD) {
        this.codigoNivelD = codigoNivelD;
    }

    public String getCodigoNivelE() {
        return codigoNivelE;
    }

    public void setCodigoNivelE(String codigoNivelE) {
        this.codigoNivelE = codigoNivelE;
    }

    public String getCodigoNivelF() {
        return codigoNivelF;
    }

    public void setCodigoNivelF(String codigoNivelF) {
        this.codigoNivelF = codigoNivelF;
    }

    public String getCodigoNivelG() {
        return codigoNivelG;
    }

    public void setCodigoNivelG(String codigoNivelG) {
        this.codigoNivelG = codigoNivelG;
    }

    public String getCodigoNivelH() {
        return codigoNivelH;
    }

    public void setCodigoNivelH(String codigoNivelH) {
        this.codigoNivelH = codigoNivelH;
    }
}
