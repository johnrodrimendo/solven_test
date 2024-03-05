package com.affirm.nosis;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ConsultaHTML")
public class ConsultaHTML {

    @XmlElement(name = "Doc")
    private String doc;

    @XmlElement(name = "RZ")
    private String rz;

    @XmlElement(name = "Filtro")
    private String filtro;

    @XmlElement(name = "MaxResp")
    private String maxResp;

    public String getDoc() {
        return doc;
    }

    public void setDoc(String doc) {
        this.doc = doc;
    }

    public String getRz() {
        return rz;
    }

    public void setRz(String rz) {
        this.rz = rz;
    }

    public String getFiltro() {
        return filtro;
    }

    public void setFiltro(String filtro) {
        this.filtro = filtro;
    }

    public String getMaxResp() {
        return maxResp;
    }

    public void setMaxResp(String maxResp) {
        this.maxResp = maxResp;
    }
}
