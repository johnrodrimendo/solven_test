package com.affirm.fdlm.listascontrol.model;

import com.google.gson.annotations.SerializedName;

public class ConsultaPersonaRequest extends ConsultaSolicitudRequest {

    @SerializedName("TipoDocumento")
    private String tipoDocumento;

    @SerializedName("NumeroDocumento")
    private String numeroDocumento;

    @SerializedName("IdFormiik")
    private String idFormiik;

    @SerializedName("RolSolicitud")
    private String rolSolicitud;

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public String getIdFormiik() {
        return idFormiik;
    }

    public void setIdFormiik(String idFormiik) {
        this.idFormiik = idFormiik;
    }

    public String getRolSolicitud() {
        return rolSolicitud;
    }

    public void setRolSolicitud(String rolSolicitud) {
        this.rolSolicitud = rolSolicitud;
    }
}
