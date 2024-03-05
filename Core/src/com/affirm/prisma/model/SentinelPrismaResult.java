package com.affirm.prisma.model;

import java.util.List;

public class SentinelPrismaResult {

    private String Ti_nombres;
    private String Ti_paterno;
    private String Ti_materno;
    private String Ti_fchnac;
    private List<Entidades> Ti_entidades;
    private String Ti_calddpult24;
    private String Ti_montodocumento;
    private String Cg_nombres;
    private String Cg_paterno;
    private String Cg_materno;
    private String Cg_fchnac;
    private List<Entidades> Cg_entidades;
    private String Cg_calddpult24;
    private String Cg_montodocumento;
    private String Codigows;

    public static class Entidades {
        private String Entidad;
        private String EntiFechaInf;
        private double EntiSaldo;
        private String EntiUltCal;
        private String EntiPerCal12M;

        public String getEntidad() {
            return Entidad;
        }

        public void setEntidad(String entidad) {
            Entidad = entidad;
        }

        public String getEntiFechaInf() {
            return EntiFechaInf;
        }

        public void setEntiFechaInf(String entiFechaInf) {
            EntiFechaInf = entiFechaInf;
        }

        public double getEntiSaldo() {
            return EntiSaldo;
        }

        public void setEntiSaldo(double entiSaldo) {
            EntiSaldo = entiSaldo;
        }

        public String getEntiUltCal() {
            return EntiUltCal;
        }

        public void setEntiUltCal(String entiUltCal) {
            EntiUltCal = entiUltCal;
        }

        public String getEntiPerCal12M() {
            return EntiPerCal12M;
        }

        public void setEntiPerCal12M(String entiPerCal12M) {
            EntiPerCal12M = entiPerCal12M;
        }
    }

    public String getTi_nombres() {
        return Ti_nombres;
    }

    public void setTi_nombres(String ti_nombres) {
        Ti_nombres = ti_nombres;
    }

    public String getTi_paterno() {
        return Ti_paterno;
    }

    public void setTi_paterno(String ti_paterno) {
        Ti_paterno = ti_paterno;
    }

    public String getTi_materno() {
        return Ti_materno;
    }

    public void setTi_materno(String ti_materno) {
        Ti_materno = ti_materno;
    }

    public String getTi_fchnac() {
        return Ti_fchnac;
    }

    public void setTi_fchnac(String ti_fchnac) {
        Ti_fchnac = ti_fchnac;
    }

    public List<Entidades> getTi_entidades() {
        return Ti_entidades;
    }

    public void setTi_entidades(List<Entidades> ti_entidades) {
        Ti_entidades = ti_entidades;
    }

    public String getTi_calddpult24() {
        return Ti_calddpult24;
    }

    public void setTi_calddpult24(String ti_calddpult24) {
        Ti_calddpult24 = ti_calddpult24;
    }

    public String getTi_montodocumento() {
        return Ti_montodocumento;
    }

    public void setTi_montodocumento(String ti_montodocumento) {
        Ti_montodocumento = ti_montodocumento;
    }

    public String getCg_nombres() {
        return Cg_nombres;
    }

    public void setCg_nombres(String cg_nombres) {
        Cg_nombres = cg_nombres;
    }

    public String getCg_paterno() {
        return Cg_paterno;
    }

    public void setCg_paterno(String cg_paterno) {
        Cg_paterno = cg_paterno;
    }

    public String getCg_materno() {
        return Cg_materno;
    }

    public void setCg_materno(String cg_materno) {
        Cg_materno = cg_materno;
    }

    public String getCg_fchnac() {
        return Cg_fchnac;
    }

    public void setCg_fchnac(String cg_fchnac) {
        Cg_fchnac = cg_fchnac;
    }

    public List<Entidades> getCg_entidades() {
        return Cg_entidades;
    }

    public void setCg_entidades(List<Entidades> cg_entidades) {
        Cg_entidades = cg_entidades;
    }

    public String getCg_calddpult24() {
        return Cg_calddpult24;
    }

    public void setCg_calddpult24(String cg_calddpult24) {
        Cg_calddpult24 = cg_calddpult24;
    }

    public String getCg_montodocumento() {
        return Cg_montodocumento;
    }

    public void setCg_montodocumento(String cg_montodocumento) {
        Cg_montodocumento = cg_montodocumento;
    }

    public String getCodigows() {
        return Codigows;
    }

    public void setCodigows(String codigows) {
        Codigows = codigows;
    }
}
