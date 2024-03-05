package com.affirm.bantotalrest.model.RBTPG218;

import com.affirm.bantotalrest.model.common.BtRequestData;

public class BTClientesCrearConPersonaExistenteRequest extends BtRequestData {

    private Long personaUId;
    private Integer sectorId;
    private Integer clasificacionInternaId;
    private Integer ejecutivoId;

    public Long getPersonaUId() {
        return personaUId;
    }

    public void setPersonaUId(Long personaUId) {
        this.personaUId = personaUId;
    }

    public Integer getSectorId() {
        return sectorId;
    }

    public void setSectorId(Integer sectorId) {
        this.sectorId = sectorId;
    }

    public Integer getClasificacionInternaId() {
        return clasificacionInternaId;
    }

    public void setClasificacionInternaId(Integer clasificacionInternaId) {
        this.clasificacionInternaId = clasificacionInternaId;
    }

    public Integer getEjecutivoId() {
        return ejecutivoId;
    }

    public void setEjecutivoId(Integer ejecutivoId) {
        this.ejecutivoId = ejecutivoId;
    }
}
