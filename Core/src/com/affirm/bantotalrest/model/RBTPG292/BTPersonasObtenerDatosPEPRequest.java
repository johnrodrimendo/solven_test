package com.affirm.bantotalrest.model.RBTPG292;

import com.affirm.bantotalrest.model.common.BtRequestData;
import com.affirm.bantotalrest.model.common.SBTPCOInformacionFATCA;

public class BTPersonasObtenerDatosPEPRequest extends BtRequestData {

    private Long personaUId;

    public Long getPersonaUId() {
        return personaUId;
    }

    public void setPersonaUId(Long personaUId) {
        this.personaUId = personaUId;
    }

}
