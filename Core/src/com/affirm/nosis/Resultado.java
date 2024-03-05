package com.affirm.nosis;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Resultado")
public class Resultado {
    @XmlElement(name = "EstadoOk")
    private String estadoOk;

    @XmlElement(name = "IdNovedad")
    private String idNovedad;

    @XmlElement(name = "Novedad")
    private String Novedad;

    public String getEstadoOk() {
        return estadoOk;
    }

    public void setEstadoOk(String estadoOk) {
        this.estadoOk = estadoOk;
    }

    public String getIdNovedad() {
        return idNovedad;
    }

    public void setIdNovedad(String idNovedad) {
        this.idNovedad = idNovedad;
    }

    public String getNovedad() {
        return Novedad;
    }

    public void setNovedad(String novedad) {
        Novedad = novedad;
    }
}
