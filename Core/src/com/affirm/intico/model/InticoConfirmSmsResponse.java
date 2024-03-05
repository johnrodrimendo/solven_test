package com.affirm.intico.model;

public class InticoConfirmSmsResponse {

    public static final char INTICO_WAITING_CONFIRMATION = 'P';
    public static final char INTICO_SMS_DELIVERED = 'C';
    public static final char INTICO_SMS_REJECTED = 'R';

    public static final int INTICO_DELIVERED_STATUS = 1;


    private Integer estado;
    private Character flag_entrega;
    private String fecha_entrega;
    private String hora_entrega;

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    public Character getFlag_entrega() {
        return flag_entrega;
    }

    public void setFlag_entrega(Character flag_entrega) {
        this.flag_entrega = flag_entrega;
    }

    public String getFecha_entrega() {
        return fecha_entrega;
    }

    public void setFecha_entrega(String fecha_entrega) {
        this.fecha_entrega = fecha_entrega;
    }

    public String getHora_entrega() {
        return hora_entrega;
    }

    public void setHora_entrega(String hora_entrega) {
        this.hora_entrega = hora_entrega;
    }
}
