package com.affirm.bantotalrest.model.customs;

import com.affirm.bantotalrest.model.common.BTResponseData;

public class ActualizarDireccionPersonaBcoAztecaResponse extends BTResponseData {

    private String Estado;
    private String Corr;
    private String Direccion;
    private String DepCod;
    private String Provcod;
    private String DistCod;
    private String LocDesc;
    private String TipoVivDesc;

    public String getEstado() {
        return Estado;
    }

    public void setEstado(String estado) {
        Estado = estado;
    }

    public String getCorr() {
        return Corr;
    }

    public void setCorr(String corr) {
        Corr = corr;
    }

    public String getDireccion() {
        return Direccion;
    }

    public void setDireccion(String direccion) {
        Direccion = direccion;
    }

    public String getDepCod() {
        return DepCod;
    }

    public void setDepCod(String depCod) {
        DepCod = depCod;
    }

    public String getProvcod() {
        return Provcod;
    }

    public void setProvcod(String provcod) {
        Provcod = provcod;
    }

    public String getDistCod() {
        return DistCod;
    }

    public void setDistCod(String distCod) {
        DistCod = distCod;
    }

    public String getLocDesc() {
        return LocDesc;
    }

    public void setLocDesc(String locDesc) {
        LocDesc = locDesc;
    }

    public String getTipoVivDesc() {
        return TipoVivDesc;
    }

    public void setTipoVivDesc(String tipoVivDesc) {
        TipoVivDesc = tipoVivDesc;
    }
}
