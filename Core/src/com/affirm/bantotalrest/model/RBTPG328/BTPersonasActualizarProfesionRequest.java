package com.affirm.bantotalrest.model.RBTPG328;

import com.affirm.bantotalrest.model.common.BtRequestData;

public class BTPersonasActualizarProfesionRequest extends BtRequestData {

    private Long personaUId;
    private Long profesionId;
    private String fechaInicioProfesion;

    public Long getPersonaUId() {
        return personaUId;
    }

    public void setPersonaUId(Long personaUId) {
        this.personaUId = personaUId;
    }

    public Long getProfesionId() {
        return profesionId;
    }

    public void setProfesionId(Long profesionId) {
        this.profesionId = profesionId;
    }

    public String getFechaInicioProfesion() {
        return fechaInicioProfesion;
    }

    public void setFechaInicioProfesion(String fechaInicioProfesion) {
        this.fechaInicioProfesion = fechaInicioProfesion;
    }
}
