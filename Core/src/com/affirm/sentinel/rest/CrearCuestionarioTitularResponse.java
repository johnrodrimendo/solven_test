package com.affirm.sentinel.rest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CrearCuestionarioTitularResponse {

    private VisoCuestionario viso_cuestionario;
    private Integer ResulVISO;
    private String CodigoWS;

    public VisoCuestionario getViso_cuestionario() {
        return viso_cuestionario;
    }

    public void setViso_cuestionario(VisoCuestionario viso_cuestionario) {
        this.viso_cuestionario = viso_cuestionario;
    }

    public Integer getResulVISO() {
        return ResulVISO;
    }

    public void setResulVISO(Integer resulVISO) {
        ResulVISO = resulVISO;
    }

    public String getCodigoWS() {
        return CodigoWS;
    }

    public void setCodigoWS(String codigoWS) {
        CodigoWS = codigoWS;
    }

    public static class VisoCuestionario {

        private Integer CodEvaluacion;
        private String FechaHoraInicio;
        private Integer TiempoEvaluacion;
        private List<Pregunta> Preguntas;

        public Integer getCodEvaluacion() {
            return CodEvaluacion;
        }

        public void setCodEvaluacion(Integer codEvaluacion) {
            CodEvaluacion = codEvaluacion;
        }

        public String getFechaHoraInicio() {
            return FechaHoraInicio;
        }

        public void setFechaHoraInicio(String fechaHoraInicio) {
            FechaHoraInicio = fechaHoraInicio;
        }

        public Integer getTiempoEvaluacion() {
            return TiempoEvaluacion;
        }

        public void setTiempoEvaluacion(Integer tiempoEvaluacion) {
            TiempoEvaluacion = tiempoEvaluacion;
        }

        public List<Pregunta> getPreguntas() {
            return Preguntas;
        }

        public void setPreguntas(List<Pregunta> preguntas) {
            Preguntas = preguntas;
        }
    }

    public static class Pregunta {

        private Integer CodPregunta;
        private String DesPregunta;
        private List<Alternativa> Alternativas;

        public boolean isYesNoQuestion(){
            if(getAlternativas() != null && getAlternativas().size() == 2){
                if(getAlternativas().stream().allMatch(p -> p.getDesAlternativa().equalsIgnoreCase("si") || p.getDesAlternativa().equalsIgnoreCase("no"))){
                    return true;
                }
            }
            return false;
        }

        public boolean isInputQuestion(){
            return getAlternativas() != null ? false : true;
        }

        public Integer getCodPregunta() {
            return CodPregunta;
        }

        public void setCodPregunta(Integer codPregunta) {
            CodPregunta = codPregunta;
        }

        public String getDesPregunta() {
            return DesPregunta;
        }

        public void setDesPregunta(String desPregunta) {
            DesPregunta = desPregunta;
        }

        public List<Alternativa> getAlternativas() {
            return Alternativas;
        }

        public void setAlternativas(List<Alternativa> alternativas) {
            Alternativas = alternativas;
        }
    }

    public static class Alternativa {

        private Integer CodAlternativa;
        private String DesAlternativa;

        public Integer getCodAlternativa() {
            return CodAlternativa;
        }

        public void setCodAlternativa(Integer codAlternativa) {
            CodAlternativa = codAlternativa;
        }

        public String getDesAlternativa() {
            return DesAlternativa;
        }

        public void setDesAlternativa(String desAlternativa) {
            DesAlternativa = desAlternativa;
        }
    }

    public boolean isValid() {
        if (getViso_cuestionario() == null || getViso_cuestionario().getFechaHoraInicio() == null || getViso_cuestionario().getTiempoEvaluacion() == null)
            return false;
        try {
            Date registerDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(getViso_cuestionario().getFechaHoraInicio());
            return new Date().toInstant().isBefore(registerDate.toInstant().plusSeconds(getViso_cuestionario().getTiempoEvaluacion()));
        } catch (ParseException e) {
            System.out.println(e.getMessage());
        }

        return false;
    }

}
