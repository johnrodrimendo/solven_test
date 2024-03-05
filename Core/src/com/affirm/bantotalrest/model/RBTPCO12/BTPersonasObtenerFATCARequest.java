package com.affirm.bantotalrest.model.RBTPCO12;

import com.affirm.bantotalrest.model.common.BtRequestData;

public class BTPersonasObtenerFATCARequest extends BtRequestData {

    private Long personaUId;

    public Long getPersonaUId() {
        return personaUId;
    }

    public void setPersonaUId(Long personaUId) {
        this.personaUId = personaUId;
    }
}
