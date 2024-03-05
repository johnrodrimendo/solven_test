package com.affirm.veraz.model;

import java.util.List;

public class VerazResponse {

    private VerazEstado estado;
    private VerazRespuesta respuesta;
    private VerazIdentificador identificador;

    public VerazEstado getEstado() {
        return estado;
    }

    public void setEstado(VerazEstado estado) {
        this.estado = estado;
    }

    public VerazRespuesta getRespuesta() {
        return respuesta;
    }

    public void setRespuesta(VerazRespuesta respuesta) {
        this.respuesta = respuesta;
    }

    public VerazIdentificador getIdentificador() {
        return identificador;
    }

    public void setIdentificador(VerazIdentificador identificador) {
        this.identificador = identificador;
    }

    public static class VerazEstado{
        private Integer codigoError;
        private String mensajeError;

        public Integer getCodigoError() {
            return codigoError;
        }

        public void setCodigoError(Integer codigoError) {
            this.codigoError = codigoError;
        }

        public String getMensajeError() {
            return mensajeError;
        }

        public void setMensajeError(String mensajeError) {
            this.mensajeError = mensajeError;
        }
    }

    public static class VerazRespuesta{

        private VerazRespuestaIntegrante integrante;
        private VerazRespuestaGrupo grupo;
        private Integer integrantes;

        public VerazRespuestaIntegrante getIntegrante() {
            return integrante;
        }

        public void setIntegrante(VerazRespuestaIntegrante integrante) {
            this.integrante = integrante;
        }

        public VerazRespuestaGrupo getGrupo() {
            return grupo;
        }

        public void setGrupo(VerazRespuestaGrupo grupo) {
            this.grupo = grupo;
        }

        public Integer getIntegrantes() {
            return integrantes;
        }

        public void setIntegrantes(Integer integrantes) {
            this.integrantes = integrantes;
        }
    }

    public static class VerazRespuestaGrupo{
        private String variables;

        public String getVariables() {
            return variables;
        }

        public void setVariables(String variables) {
            this.variables = variables;
        }
    }

    public static class VerazRespuestaIntegrante{

        private VerazRespuestaIntegranteVariables variables;
        private Integer valor;
        private Long documento;
        private String sexo;
        private String nombre;

        public Integer getValor() {
            return valor;
        }

        public void setValor(Integer valor) {
            this.valor = valor;
        }

        public Long getDocumento() {
            return documento;
        }

        public void setDocumento(Long documento) {
            this.documento = documento;
        }

        public String getSexo() {
            return sexo;
        }

        public void setSexo(String sexo) {
            this.sexo = sexo;
        }

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public VerazRespuestaIntegranteVariables getVariables() {
            return variables;
        }

        public void setVariables(VerazRespuestaIntegranteVariables variables) {
            this.variables = variables;
        }
    }

    public static class VerazRespuestaIntegranteVariables{
        private List<VerazRespuestaIntegranteVariableData> variable;

        public List<VerazRespuestaIntegranteVariableData> getVariable() {
            return variable;
        }

        public void setVariable(List<VerazRespuestaIntegranteVariableData> variable) {
            this.variable = variable;
        }
    }

    public static class VerazRespuestaIntegranteVariableData{
        private VerazRespuestaIntegranteVariableValor valor;
        private VerazRespuestaIntegranteVariableNombre nombre;

        public VerazRespuestaIntegranteVariableValor getValor() {
            return valor;
        }

        public void setValor(VerazRespuestaIntegranteVariableValor valor) {
            this.valor = valor;
        }

        public VerazRespuestaIntegranteVariableNombre getNombre() {
            return nombre;
        }

        public void setNombre(VerazRespuestaIntegranteVariableNombre nombre) {
            this.nombre = nombre;
        }
    }

    public static class VerazRespuestaIntegranteVariableValor{
        private String content;
        private String tipo;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getTipo() {
            return tipo;
        }

        public void setTipo(String tipo) {
            this.tipo = tipo;
        }
    }

    public static class VerazRespuestaIntegranteVariableNombre{
        private String content;
        private String clase;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getClase() {
            return clase;
        }

        public void setClase(String clase) {
            this.clase = clase;
        }
    }

    public static class VerazIdentificador{
        private Integer cliente;
        private Long lote;
        private String producto;
        private String fechaRecepcion;

        public Integer getCliente() {
            return cliente;
        }

        public void setCliente(Integer cliente) {
            this.cliente = cliente;
        }

        public Long getLote() {
            return lote;
        }

        public void setLote(Long lote) {
            this.lote = lote;
        }

        public String getProducto() {
            return producto;
        }

        public void setProducto(String producto) {
            this.producto = producto;
        }

        public String getFechaRecepcion() {
            return fechaRecepcion;
        }

        public void setFechaRecepcion(String fechaRecepcion) {
            this.fechaRecepcion = fechaRecepcion;
        }
    }
}
