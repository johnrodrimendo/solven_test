package com.affirm.efl.model;

import java.util.List;

/**
 * Created by dev5 on 06/07/17.
 */
public class Identifications {

    private List<Identification> identification;

    public Identifications(List<Identification> identifications){
        this.identification = identifications;
    }

    public List<Identification> getIdentifications() {
        return identification;
    }

    public void setIdentifications(List<Identification> identifications) {
        this.identification = identifications;
    }
}
