package com.affirm.bantotalrest.model.common;

public class SBTPCOResidenciaFiscal {

    private Integer correlativo;
    private Integer paisId;
    private String pais;
    private String codigoTIN;


    public Integer getCorrelativo() {
        return correlativo;
    }

    public void setCorrelativo(Integer correlativo) {
        this.correlativo = correlativo;
    }

    public Integer getPaisId() {
        return paisId;
    }

    public void setPaisId(Integer paisId) {
        this.paisId = paisId;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getCodigoTIN() {
        return codigoTIN;
    }

    public void setCodigoTIN(String codigoTIN) {
        this.codigoTIN = codigoTIN;
    }
}
