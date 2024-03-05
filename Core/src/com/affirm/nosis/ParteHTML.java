package com.affirm.nosis;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ParteHTML")
public class ParteHTML {

    @XmlAttribute(name = "Existe")
    private String existe;

    @XmlAttribute(name = "Cant")
    private Integer cant;

    @XmlValue
    private String html;

    public String getExiste() { return existe; }

    public void setExiste(String existe) { this.existe = existe; }

    public Integer getCant() { return cant; }

    public void setCant(Integer cant) { this.cant = cant; }

    public String getHtml() { return html; }

    public void setHtml(String html) { this.html = html; }
}
