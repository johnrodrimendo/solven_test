package com.affirm.bantotalrest.model.common;

import java.util.List;

public class SBTPCOInformacionFATCA {
    private String permanencia183Dias;
    private String tieneGreenCard;
    private String pagoFuenteEEUU;
    private String ingresoBrutoEEUU;
    private String codigoTIN;
    private String residenciaMasPaises;
    private SBTPCOResidenciaFiscalList residenciasFiscales;

    public String getPermanencia183Dias() {
        return permanencia183Dias;
    }

    public void setPermanencia183Dias(String permanencia183Dias) {
        this.permanencia183Dias = permanencia183Dias;
    }

    public String getTieneGreenCard() {
        return tieneGreenCard;
    }

    public void setTieneGreenCard(String tieneGreenCard) {
        this.tieneGreenCard = tieneGreenCard;
    }

    public String getPagoFuenteEEUU() {
        return pagoFuenteEEUU;
    }

    public void setPagoFuenteEEUU(String pagoFuenteEEUU) {
        this.pagoFuenteEEUU = pagoFuenteEEUU;
    }

    public String getIngresoBrutoEEUU() {
        return ingresoBrutoEEUU;
    }

    public void setIngresoBrutoEEUU(String ingresoBrutoEEUU) {
        this.ingresoBrutoEEUU = ingresoBrutoEEUU;
    }

    public String getCodigoTIN() {
        return codigoTIN;
    }

    public void setCodigoTIN(String codigoTIN) {
        this.codigoTIN = codigoTIN;
    }

    public String getResidenciaMasPaises() {
        return residenciaMasPaises;
    }

    public void setResidenciaMasPaises(String residenciaMasPaises) {
        this.residenciaMasPaises = residenciaMasPaises;
    }

    public SBTPCOResidenciaFiscalList getResidenciasFiscales() {
        return residenciasFiscales;
    }

    public void setResidenciasFiscales(SBTPCOResidenciaFiscalList residenciasFiscales) {
        this.residenciasFiscales = residenciasFiscales;
    }

    public static class SBTPCOResidenciaFiscalList{
        private List<SBTPCOResidenciaFiscal> sBTPCOResidenciaFiscal;

        public List<SBTPCOResidenciaFiscal> getsBTPCOResidenciaFiscal() {
            return sBTPCOResidenciaFiscal;
        }

        public void setsBTPCOResidenciaFiscal(List<SBTPCOResidenciaFiscal> sBTPCOResidenciaFiscal) {
            this.sBTPCOResidenciaFiscal = sBTPCOResidenciaFiscal;
        }
    }
}
