package com.affirm.client.model;

import java.io.Serializable;

/**
 * Created by jarmando on 19/12/16.
 */
public class Sample implements Serializable {
    private String id;
    private String dsc;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDsc() {
        return dsc;
    }

    public void setDsc(String dsc) {
        this.dsc = dsc;
    }
}