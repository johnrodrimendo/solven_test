package com.affirm.efl.model;

/**
 * Created by dev5 on 06/07/17.
 */
public class Identification {

    private String type;
    private String value;

    public Identification(String type, String value){
        this.type = type;
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
