package com.affirm.bantotalrest.model.RBTPG146;

import com.affirm.bantotalrest.model.common.BtRequestData;
import com.affirm.bantotalrest.model.common.SdtDatosPEP;

public class BTPersonasAgregarDatosPEPRequest extends BtRequestData {

    private Long personaUId;
    private String esPEP;
    private SdtDatosPEP sdtDatosPEP;

    public Long getPersonaUId() {
        return personaUId;
    }

    public void setPersonaUId(Long personaUId) {
        this.personaUId = personaUId;
    }

    public String getEsPEP() {
        return esPEP;
    }

    public void setEsPEP(String esPEP) {
        this.esPEP = esPEP;
    }

    public SdtDatosPEP getSdtDatosPEP() {
        return sdtDatosPEP;
    }

    public void setSdtDatosPEP(SdtDatosPEP sdtDatosPEP) {
        this.sdtDatosPEP = sdtDatosPEP;
    }
}
