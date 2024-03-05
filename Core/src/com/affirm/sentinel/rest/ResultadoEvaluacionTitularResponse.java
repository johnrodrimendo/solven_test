package com.affirm.sentinel.rest;

public class ResultadoEvaluacionTitularResponse {

    private VisoResultado viso_resultado;
    private String CodigoWS;

    public static class VisoResultado {

        private String Estado;
        private String CodEvaFin;
        private String CodError;

        public String getEstado() {
            return Estado;
        }

        public void setEstado(String estado) {
            Estado = estado;
        }

        public String getCodEvaFin() {
            return CodEvaFin;
        }

        public void setCodEvaFin(String codEvaFin) {
            CodEvaFin = codEvaFin;
        }

        public String getCodError() {
            return CodError;
        }

        public void setCodError(String codError) {
            CodError = codError;
        }
    }

    public VisoResultado getViso_resultado() {
        return viso_resultado;
    }

    public void setViso_resultado(VisoResultado viso_resultado) {
        this.viso_resultado = viso_resultado;
    }

    public String getCodigoWS() {
        return CodigoWS;
    }

    public void setCodigoWS(String codigoWS) {
        CodigoWS = codigoWS;
    }
}
