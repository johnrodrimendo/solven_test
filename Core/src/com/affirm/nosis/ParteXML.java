package com.affirm.nosis;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ParteXML")
public class ParteXML {
    @XmlElement(name = "Dato")
    private List<Dato> datos;

    @XmlAttribute(name = "Existe")
    private String existe;

    @XmlAttribute(name = "Cant")
    private Integer cant;

    public List<Dato> getDatos() { return datos; }

    public void setDatos(List<Dato> datos) { this.datos = datos; }

    public String getExiste() { return existe; }

    public void setExiste(String existe) { this.existe = existe; }

    public Integer getCant() { return cant; }

    public void setCant(Integer cant) { this.cant = cant; }
}
