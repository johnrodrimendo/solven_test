package com.affirm.fdlm.creditoconsumo.request;

import com.google.gson.annotations.SerializedName;

public class ConsultaCreditoRequest {

    @SerializedName("producto")
    private Integer producto;

    @SerializedName("nro_documento")
    private Long numeroDocumento;

    @SerializedName("tipo_documento")
    private String tipoDocumento;

    public Integer getProducto() {
        return producto;
    }

    public void setProducto(Integer producto) {
        this.producto = producto;
    }

    public Long getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(Long numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }
}
