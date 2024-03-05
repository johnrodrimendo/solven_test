package com.affirm.bantotalrest.model.RBTPCO12;

import com.affirm.bantotalrest.model.common.BTResponseData;
import com.affirm.bantotalrest.model.common.SBTPCOInformacionFATCA;

public class BTPersonasObtenerFATCAResponse extends BTResponseData {

    private Long personaUId;
    private SBTPCOInformacionFATCA sdtInformacionFATCA;

    public Long getPersonaUId() {
        return personaUId;
    }

    public void setPersonaUId(Long personaUId) {
        this.personaUId = personaUId;
    }

    public SBTPCOInformacionFATCA getSdtInformacionFATCA() {
        return sdtInformacionFATCA;
    }

    public void setSdtInformacionFATCA(SBTPCOInformacionFATCA sdtInformacionFATCA) {
        this.sdtInformacionFATCA = sdtInformacionFATCA;
    }

}
