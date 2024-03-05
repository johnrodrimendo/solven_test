package com.affirm.bancoazteca.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ReniecDataResponse extends ReniecComonResponse{

    public static final String DATE_FORMAT = "yyyyMMdd";
    private ReniecData Data;

    public ReniecData getData() {
        return Data;
    }

    public void setData(ReniecData data) {
        Data = data;
    }

    public static class ReniecData{
        private String CodigoError;
        private String DescriError;
        private String NroDocumento;
        private String DigitoVerificacion;
        private String ApellidoPaterno;
        private String ApellidoMaterno;
        private String ApellidoCasada;
        private String Nombres;
        private String DepartamentoDomicilio;
        private String ProvinciaDomicilio;
        private String DistritoDomicilio;
        private String EstadoCivil;
        private String Sexo;
        private String FechaNacimiento;
        private String FechaExpedicion;
        private String Direccion;
        private String Foto;

        public String getCodigoError() {
            return CodigoError;
        }

        public void setCodigoError(String codigoError) {
            CodigoError = codigoError;
        }

        public String getDescriError() {
            return DescriError;
        }

        public void setDescriError(String descriError) {
            DescriError = descriError;
        }

        public String getNroDocumento() {
            return NroDocumento;
        }

        public void setNroDocumento(String nroDocumento) {
            NroDocumento = nroDocumento;
        }

        public String getDigitoVerificacion() {
            return DigitoVerificacion;
        }

        public void setDigitoVerificacion(String digitoVerificacion) {
            DigitoVerificacion = digitoVerificacion;
        }

        public String getApellidoPaterno() {
            return ApellidoPaterno;
        }

        public void setApellidoPaterno(String apellidoPaterno) {
            ApellidoPaterno = apellidoPaterno;
        }

        public String getApellidoMaterno() {
            return ApellidoMaterno;
        }

        public void setApellidoMaterno(String apellidoMaterno) {
            ApellidoMaterno = apellidoMaterno;
        }

        public String getApellidoCasada() {
            return ApellidoCasada;
        }

        public void setApellidoCasada(String apellidoCasada) {
            ApellidoCasada = apellidoCasada;
        }

        public String getNombres() {
            return Nombres;
        }

        public void setNombres(String nombres) {
            Nombres = nombres;
        }

        public String getDepartamentoDomicilio() {
            return DepartamentoDomicilio;
        }

        public void setDepartamentoDomicilio(String departamentoDomicilio) {
            DepartamentoDomicilio = departamentoDomicilio;
        }

        public String getProvinciaDomicilio() {
            return ProvinciaDomicilio;
        }

        public void setProvinciaDomicilio(String provinciaDomicilio) {
            ProvinciaDomicilio = provinciaDomicilio;
        }

        public String getDistritoDomicilio() {
            return DistritoDomicilio;
        }

        public void setDistritoDomicilio(String distritoDomicilio) {
            DistritoDomicilio = distritoDomicilio;
        }

        public String getEstadoCivil() {
            return EstadoCivil;
        }

        public void setEstadoCivil(String estadoCivil) {
            EstadoCivil = estadoCivil;
        }

        public String getSexo() {
            return Sexo;
        }

        public void setSexo(String sexo) {
            Sexo = sexo;
        }

        public String getFechaNacimiento() {
            return FechaNacimiento;
        }

        public void setFechaNacimiento(String fechaNacimiento) {
            FechaNacimiento = fechaNacimiento;
        }

        public String getFechaExpedicion() {
            return FechaExpedicion;
        }

        public void setFechaExpedicion(String fechaExpedicion) {
            FechaExpedicion = fechaExpedicion;
        }

        public String getDireccion() {
            return Direccion;
        }

        public void setDireccion(String direccion) {
            Direccion = direccion;
        }

        public String getFoto() {
            return Foto;
        }

        public void setFoto(String foto) {
            Foto = foto;
        }

        public Date getFechaNacimientoAsDate() throws ParseException {
            return getValueAsDate(getFechaNacimiento());
        }

        public Date getFechaExpedicionAsDate() throws ParseException {
            return getValueAsDate(getFechaExpedicion());
        }

        private Date getValueAsDate(String value) throws ParseException {
            if(value == null || value.isEmpty()) return null;
            return new SimpleDateFormat(DATE_FORMAT).parse(value);
        }

        public String getLocalidad() {
            if(getDepartamentoDomicilio() != null && getProvinciaDomicilio() != null && getDistritoDomicilio() != null) return String.format("%s/%s/%s", getDepartamentoDomicilio(), getProvinciaDomicilio(), getDistritoDomicilio());
            return null;
        }
    }
}
