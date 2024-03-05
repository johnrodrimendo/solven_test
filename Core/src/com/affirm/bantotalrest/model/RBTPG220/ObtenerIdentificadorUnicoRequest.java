package com.affirm.bantotalrest.model.RBTPG220;

import com.affirm.bantotalrest.model.common.BtRequestData;

public class ObtenerIdentificadorUnicoRequest  extends BtRequestData {

    public final static int PERU_DOCUMENTO_ID = 604;
    public final static int PERU_TIPO_DOCUMENTO_DNI = 1;

    private Integer paisDocumentoId;
    private Integer tipoDocumentoId;
    private String numeroDocumento;

    public Integer getPaisDocumentoId() {
        return paisDocumentoId;
    }

    public void setPaisDocumentoId(Integer paisDocumentoId) {
        this.paisDocumentoId = paisDocumentoId;
    }

    public Integer getTipoDocumentoId() {
        return tipoDocumentoId;
    }

    public void setTipoDocumentoId(Integer tipoDocumentoId) {
        this.tipoDocumentoId = tipoDocumentoId;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }
}
