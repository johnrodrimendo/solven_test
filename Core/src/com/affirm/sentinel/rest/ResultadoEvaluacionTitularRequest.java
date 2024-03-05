package com.affirm.sentinel.rest;

import java.util.List;

public class ResultadoEvaluacionTitularRequest extends CredencialesRequest {

    private String TipoDoc;
    private String NroDoc;
    private Integer CodCue;
    private Integer CodEva;
    private List<PreguntaRespuesta> PreRpta;

    public static class PreguntaRespuesta {

        private String preguntaId;
        private String rptaId;

        public String getPreguntaId() {
            return preguntaId;
        }

        public void setPreguntaId(String preguntaId) {
            this.preguntaId = preguntaId;
        }

        public String getRptaId() {
            return rptaId;
        }

        public void setRptaId(String rptaId) {
            this.rptaId = rptaId;
        }
    }

    public String getTipoDoc() {
        return TipoDoc;
    }

    public void setTipoDoc(String tipoDoc) {
        TipoDoc = tipoDoc;
    }

    public String getNroDoc() {
        return NroDoc;
    }

    public void setNroDoc(String nroDoc) {
        NroDoc = nroDoc;
    }

    public Integer getCodCue() {
        return CodCue;
    }

    public void setCodCue(Integer codCue) {
        CodCue = codCue;
    }

    public Integer getCodEva() {
        return CodEva;
    }

    public void setCodEva(Integer codEva) {
        CodEva = codEva;
    }

    public List<PreguntaRespuesta> getPreRpta() {
        return PreRpta;
    }

    public void setPreRpta(List<PreguntaRespuesta> preRpta) {
        PreRpta = preRpta;
    }
}
