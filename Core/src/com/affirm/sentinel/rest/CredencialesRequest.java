package com.affirm.sentinel.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CredencialesRequest {

    private String Gx_UsuEnc;
    private String Gx_PasEnc;
    private String Gx_Key;

    public String getGx_UsuEnc() {
        return Gx_UsuEnc;
    }

    public void setGx_UsuEnc(String gx_UsuEnc) {
        Gx_UsuEnc = gx_UsuEnc;
    }

    public String getGx_PasEnc() {
        return Gx_PasEnc;
    }

    public void setGx_PasEnc(String gx_PasEnc) {
        Gx_PasEnc = gx_PasEnc;
    }

    public String getGx_Key() {
        return Gx_Key;
    }

    public void setGx_Key(String gx_Key) {
        Gx_Key = gx_Key;
    }
}
