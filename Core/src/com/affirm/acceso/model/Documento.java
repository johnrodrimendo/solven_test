package com.affirm.acceso.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dev5 on 29/08/17.
 */
public class Documento {

    @SerializedName("co_docume")
    private String id;
    @SerializedName("no_docume")
    private String url;

    public Documento(String id, String url){
        setId(id);
        setUrl(url);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
