package com.affirm.bancoazteca.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RolConsejero {

    private List<Diagnostico> Diagnostico;
    private String Recomendacion;
    private String SolucionFinanciera;

    public List<RolConsejero.Diagnostico> getDiagnostico() {
        return Diagnostico;
    }

    public void setDiagnostico(List<RolConsejero.Diagnostico> diagnostico) {
        Diagnostico = diagnostico;
    }

    public String getRecomendacion() {
        return Recomendacion;
    }

    public void setRecomendacion(String recomendacion) {
        Recomendacion = recomendacion;
    }

    public String getSolucionFinanciera() {
        return SolucionFinanciera;
    }

    public void setSolucionFinanciera(String solucionFinanciera) {
        SolucionFinanciera = solucionFinanciera;
    }

    public static class Diagnostico {
        private List<Descripcion> Descripciones;
        private Integer CodigoTitulo;
        private String Titulo;

        public List<Diagnostico.Descripcion> getDescripciones() {
            return Descripciones;
        }

        public void setDescripciones(List<Descripcion> descripciones) {
            Descripciones = descripciones;
        }

        public Integer getCodigoTitulo() {
            return CodigoTitulo;
        }

        public void setCodigoTitulo(Integer codigoTitulo) {
            CodigoTitulo = codigoTitulo;
        }

        public String getTitulo() {
            return Titulo;
        }

        public void setTitulo(String titulo) {
            Titulo = titulo;
        }

        public boolean haveDescription(){
            System.out.println(this.Titulo +" "+ getDescripciones().size());
            List<Descripcion> descripcion = new ArrayList<>();

            for (int i = 0; i < Descripciones.size(); i++) {
                if(Descripciones.get(i).Estatus != 0){
                    descripcion.add(Descripciones.get(i));
                }
            }
            System.out.println(this.Titulo +" "+ descripcion.size());
            if(descripcion.isEmpty()){
                return false;
            }
            return true;
        }

        public static class Descripcion {
            private String Descripcion;
            private Integer Estatus;
            private String Resumen;

            public String getDescripcion() {
                return Descripcion;
            }

            public void setDescripcion(String descripcion) {
                Descripcion = descripcion;
            }

            public Integer getEstatus() {
                return Estatus;
            }

            public void setEstatus(Integer estatus) {
                Estatus = estatus;
            }

            public String getResumen() {
                return Resumen;
            }

            public void setResumen(String resumen) {
                Resumen = resumen;
            }
        }
    }

}
