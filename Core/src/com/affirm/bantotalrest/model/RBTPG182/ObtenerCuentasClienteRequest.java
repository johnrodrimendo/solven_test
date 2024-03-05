package com.affirm.bantotalrest.model.RBTPG182;

import com.affirm.bantotalrest.model.common.BtRequestData;

public class ObtenerCuentasClienteRequest extends BtRequestData {

    private Long personaUId;

    public Long getPersonaUId() {
        return personaUId;
    }

    public void setPersonaUId(Long personaUId) {
        this.personaUId = personaUId;
    }

}