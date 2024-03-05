package com.affirm.bantotalrest.model.RBTPG220;

import com.affirm.bantotalrest.model.common.BTResponseData;

public class ObtenerIdentificadorUnicoResponse extends BTResponseData {

    private Long personaUId;

    public Long getPersonaUId() {
        return personaUId;
    }

    public void setPersonaUId(Long personaUId) {
        this.personaUId = personaUId;
    }
}
