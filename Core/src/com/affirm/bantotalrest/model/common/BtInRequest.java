package com.affirm.bantotalrest.model.common;

public class BtInRequest {

    private String Canal;
    private String Requerimiento;
    private String Usuario;
    private String Token;
    private String Device;

    public String getCanal() {
        return Canal;
    }

    public void setCanal(String canal) {
        Canal = canal;
    }

    public String getRequerimiento() {
        return Requerimiento;
    }

    public void setRequerimiento(String requerimiento) {
        Requerimiento = requerimiento;
    }

    public String getUsuario() {
        return Usuario;
    }

    public void setUsuario(String usuario) {
        Usuario = usuario;
    }

    public String getToken() {
        return Token;
    }

    public void setToken(String token) {
        Token = token;
    }

    public String getDevice() {
        return Device;
    }

    public void setDevice(String device) {
        Device = device;
    }
}
