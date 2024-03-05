package com.affirm.acceso.model;

public class EvaluacionGenerica {

    private String plantilla;
    private Prospecto prospecto;

    public String getPlantilla() {
        return plantilla;
    }

    public void setPlantilla(String plantilla) {
        this.plantilla = plantilla;
    }

    public Prospecto getProspecto() {
        return prospecto;
    }

    public void setProspecto(Prospecto prospecto) {
        this.prospecto = prospecto;
    }

    public static class Prospecto {
        private Integer tipoDocumento;
        private String documento;

        public Integer getTipoDocumento() {
            return tipoDocumento;
        }

        public void setTipoDocumento(Integer tipoDocumento) {
            this.tipoDocumento = tipoDocumento;
        }

        public String getDocumento() {
            return documento;
        }

        public void setDocumento(String documento) {
            this.documento = documento;
        }
    }

}


