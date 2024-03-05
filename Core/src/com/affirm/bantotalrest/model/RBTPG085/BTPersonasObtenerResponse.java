package com.affirm.bantotalrest.model.RBTPG085;

import com.affirm.bantotalrest.model.common.BTResponseData;

public class BTPersonasObtenerResponse extends BTResponseData {

    private SdtPersona sdtPersona;

    public  static  class SdtPersona{
        private Integer paisDocumentoId;
        private String paisDocumento;
        private Integer tipoDocumentoId;
        private String tipoDocumento;
        private String nroDocumento;
        private String fechaVencimiento;
        private String primerApellido;
        private String segundoApellido;
        private String primerNombre;
        private String segundoNombre;
        private String fechaNacimiento;
        private String sexo;
        private String estadoCivilId;
        private String estadoCivil;
        private String nacionalidadId;
        private String nacionalidad;
        private Integer ocupacionId;
        private String ocupacion;
        private String fechaInicioActividad;
        private Double ingresos;
        private Integer paisDomicilioId;
        private String paisDomicilio;
        private Integer departamentoId;
        private String departamento;
        private Integer localidadId;
        private String localidad;
        private Integer barrioId;
        private String barrio;
        private String calle;
        private String numeroPuerta;
        private String apartamento;
        private String codigoPostal;
        private String telefonoFijo;
        private String telefonoCelular;
        private String correoElectronico;

        public Integer getPaisDocumentoId() {
            return paisDocumentoId;
        }

        public void setPaisDocumentoId(Integer paisDocumentoId) {
            this.paisDocumentoId = paisDocumentoId;
        }

        public String getPaisDocumento() {
            return paisDocumento;
        }

        public void setPaisDocumento(String paisDocumento) {
            this.paisDocumento = paisDocumento;
        }

        public Integer getTipoDocumentoId() {
            return tipoDocumentoId;
        }

        public void setTipoDocumentoId(Integer tipoDocumentoId) {
            this.tipoDocumentoId = tipoDocumentoId;
        }

        public String getTipoDocumento() {
            return tipoDocumento;
        }

        public void setTipoDocumento(String tipoDocumento) {
            this.tipoDocumento = tipoDocumento;
        }

        public String getNroDocumento() {
            return nroDocumento;
        }

        public void setNroDocumento(String nroDocumento) {
            this.nroDocumento = nroDocumento;
        }

        public String getFechaVencimiento() {
            return fechaVencimiento;
        }

        public void setFechaVencimiento(String fechaVencimiento) {
            this.fechaVencimiento = fechaVencimiento;
        }

        public String getPrimerApellido() {
            return primerApellido;
        }

        public void setPrimerApellido(String primerApellido) {
            this.primerApellido = primerApellido;
        }

        public String getSegundoApellido() {
            return segundoApellido;
        }

        public void setSegundoApellido(String segundoApellido) {
            this.segundoApellido = segundoApellido;
        }

        public String getPrimerNombre() {
            return primerNombre;
        }

        public void setPrimerNombre(String primerNombre) {
            this.primerNombre = primerNombre;
        }

        public String getSegundoNombre() {
            return segundoNombre;
        }

        public void setSegundoNombre(String segundoNombre) {
            this.segundoNombre = segundoNombre;
        }

        public String getFechaNacimiento() {
            return fechaNacimiento;
        }

        public void setFechaNacimiento(String fechaNacimiento) {
            this.fechaNacimiento = fechaNacimiento;
        }

        public String getSexo() {
            return sexo;
        }

        public void setSexo(String sexo) {
            this.sexo = sexo;
        }

        public String getEstadoCivilId() {
            return estadoCivilId;
        }

        public void setEstadoCivilId(String estadoCivilId) {
            this.estadoCivilId = estadoCivilId;
        }

        public String getEstadoCivil() {
            return estadoCivil;
        }

        public void setEstadoCivil(String estadoCivil) {
            this.estadoCivil = estadoCivil;
        }

        public String getNacionalidadId() {
            return nacionalidadId;
        }

        public void setNacionalidadId(String nacionalidadId) {
            this.nacionalidadId = nacionalidadId;
        }

        public String getNacionalidad() {
            return nacionalidad;
        }

        public void setNacionalidad(String nacionalidad) {
            this.nacionalidad = nacionalidad;
        }

        public Integer getOcupacionId() {
            return ocupacionId;
        }

        public void setOcupacionId(Integer ocupacionId) {
            this.ocupacionId = ocupacionId;
        }

        public String getOcupacion() {
            return ocupacion;
        }

        public void setOcupacion(String ocupacion) {
            this.ocupacion = ocupacion;
        }

        public String getFechaInicioActividad() {
            return fechaInicioActividad;
        }

        public void setFechaInicioActividad(String fechaInicioActividad) {
            this.fechaInicioActividad = fechaInicioActividad;
        }

        public Double getIngresos() {
            return ingresos;
        }

        public void setIngresos(Double ingresos) {
            this.ingresos = ingresos;
        }

        public Integer getPaisDomicilioId() {
            return paisDomicilioId;
        }

        public void setPaisDomicilioId(Integer paisDomicilioId) {
            this.paisDomicilioId = paisDomicilioId;
        }

        public String getPaisDomicilio() {
            return paisDomicilio;
        }

        public void setPaisDomicilio(String paisDomicilio) {
            this.paisDomicilio = paisDomicilio;
        }

        public Integer getDepartamentoId() {
            return departamentoId;
        }

        public void setDepartamentoId(Integer departamentoId) {
            this.departamentoId = departamentoId;
        }

        public String getDepartamento() {
            return departamento;
        }

        public void setDepartamento(String departamento) {
            this.departamento = departamento;
        }

        public Integer getLocalidadId() {
            return localidadId;
        }

        public void setLocalidadId(Integer localidadId) {
            this.localidadId = localidadId;
        }

        public String getLocalidad() {
            return localidad;
        }

        public void setLocalidad(String localidad) {
            this.localidad = localidad;
        }

        public Integer getBarrioId() {
            return barrioId;
        }

        public void setBarrioId(Integer barrioId) {
            this.barrioId = barrioId;
        }

        public String getBarrio() {
            return barrio;
        }

        public void setBarrio(String barrio) {
            this.barrio = barrio;
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

        public String getTelefonoFijo() {
            return telefonoFijo;
        }

        public void setTelefonoFijo(String telefonoFijo) {
            this.telefonoFijo = telefonoFijo;
        }

        public String getTelefonoCelular() {
            return telefonoCelular;
        }

        public void setTelefonoCelular(String telefonoCelular) {
            this.telefonoCelular = telefonoCelular;
        }

        public String getCorreoElectronico() {
            return correoElectronico;
        }

        public void setCorreoElectronico(String correoElectronico) {
            this.correoElectronico = correoElectronico;
        }
    }

    public SdtPersona getSdtPersona() {
        return sdtPersona;
    }

    public void setSdtPersona(SdtPersona sdtPersona) {
        this.sdtPersona = sdtPersona;
    }
}
