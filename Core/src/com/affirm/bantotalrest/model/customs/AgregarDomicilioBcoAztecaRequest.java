package com.affirm.bantotalrest.model.customs;

import com.affirm.bantotalrest.model.common.BtRequestData;

public class AgregarDomicilioBcoAztecaRequest extends BtRequestData {

    private Integer Pgcod;
    private String CuentaBT;
    private Integer Pais;
    private Integer Petdoc;
    private String Pendoc;
    private String Ubigeo;
    private String Nivel1;
    private String DescNivel1;
    private String Nivel2;
    private String DescNivel2;
    private String Nivel3;
    private String DescNivel3;
    private String Nivel4;
    private String DescNivel4;
    private DomicilioBantotal Domicilio;

    public Integer getPgcod() {
        return Pgcod;
    }

    public void setPgcod(Integer pgcod) {
        Pgcod = pgcod;
    }

    public String getCuentaBT() {
        return CuentaBT;
    }

    public void setCuentaBT(String cuentaBT) {
        CuentaBT = cuentaBT;
    }

    public Integer getPais() {
        return Pais;
    }

    public void setPais(Integer pais) {
        Pais = pais;
    }

    public Integer getPetdoc() {
        return Petdoc;
    }

    public void setPetdoc(Integer petdoc) {
        Petdoc = petdoc;
    }

    public String getPendoc() {
        return Pendoc;
    }

    public void setPendoc(String pendoc) {
        Pendoc = pendoc;
    }

    public String getUbigeo() {
        return Ubigeo;
    }

    public void setUbigeo(String ubigeo) {
        Ubigeo = ubigeo;
    }

    public String getNivel1() {
        return Nivel1;
    }

    public void setNivel1(String nivel1) {
        Nivel1 = nivel1;
    }

    public String getDescNivel1() {
        return DescNivel1;
    }

    public void setDescNivel1(String descNivel1) {
        DescNivel1 = descNivel1;
    }

    public String getNivel2() {
        return Nivel2;
    }

    public void setNivel2(String nivel2) {
        Nivel2 = nivel2;
    }

    public String getDescNivel2() {
        return DescNivel2;
    }

    public void setDescNivel2(String descNivel2) {
        DescNivel2 = descNivel2;
    }

    public String getNivel3() {
        return Nivel3;
    }

    public void setNivel3(String nivel3) {
        Nivel3 = nivel3;
    }

    public String getDescNivel3() {
        return DescNivel3;
    }

    public void setDescNivel3(String descNivel3) {
        DescNivel3 = descNivel3;
    }

    public String getNivel4() {
        return Nivel4;
    }

    public void setNivel4(String nivel4) {
        Nivel4 = nivel4;
    }

    public String getDescNivel4() {
        return DescNivel4;
    }

    public void setDescNivel4(String descNivel4) {
        DescNivel4 = descNivel4;
    }

    public DomicilioBantotal getDomicilio() {
        return Domicilio;
    }

    public void setDomicilio(DomicilioBantotal domicilio) {
        Domicilio = domicilio;
    }

    public static class DomicilioBantotal {
        private Integer paisDomicilioId;
        private Integer departamentoId;
        private Integer localidadId;
        private Integer barrioId;
        private String calle;
        private String numeroPuerta;
        private String apartamento;
        private String codigoPostal;
        private String tipoDeDomicilioId;

        public Integer getPaisDomicilioId() {
            return paisDomicilioId;
        }

        public void setPaisDomicilioId(Integer paisDomicilioId) {
            this.paisDomicilioId = paisDomicilioId;
        }

        public Integer getDepartamentoId() {
            return departamentoId;
        }

        public void setDepartamentoId(Integer departamentoId) {
            this.departamentoId = departamentoId;
        }

        public Integer getLocalidadId() {
            return localidadId;
        }

        public void setLocalidadId(Integer localidadId) {
            this.localidadId = localidadId;
        }

        public Integer getBarrioId() {
            return barrioId;
        }

        public void setBarrioId(Integer barrioId) {
            this.barrioId = barrioId;
        }

        public String getCalle() {
            return calle;
        }

        public void setCalle(String calle) {
            this.calle = calle;
        }

        public String getNumeroPuerta() {
            return numeroPuerta;
        }

        public void setNumeroPuerta(String numeroPuerta) {
            this.numeroPuerta = numeroPuerta;
        }

        public String getApartamento() {
            return apartamento;
        }

        public void setApartamento(String apartamento) {
            this.apartamento = apartamento;
        }

        public String getCodigoPostal() {
            return codigoPostal;
        }

        public void setCodigoPostal(String codigoPostal) {
            this.codigoPostal = codigoPostal;
        }

        public String getTipoDeDomicilioId() {
            return tipoDeDomicilioId;
        }

        public void setTipoDeDomicilioId(String tipoDeDomicilioId) {
            this.tipoDeDomicilioId = tipoDeDomicilioId;
        }
    }


}
