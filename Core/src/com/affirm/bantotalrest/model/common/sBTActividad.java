package com.affirm.bantotalrest.model.common;

public class sBTActividad {

    private Integer identificador;
    private String descripcion;
    private Long identificadorEntidadReguladora;

    public Integer getIdentificador() {
        return identificador;
    }

    public void setIdentificador(Integer identificador) {
        this.identificador = identificador;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Long getIdentificadorEntidadReguladora() {
        return identificadorEntidadReguladora;
    }

    public void setIdentificadorEntidadReguladora(Long identificadorEntidadReguladora) {
        this.identificadorEntidadReguladora = identificadorEntidadReguladora;
    }
}
