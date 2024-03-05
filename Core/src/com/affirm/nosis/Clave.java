package com.affirm.nosis;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Clave")
public class Clave {
    @XmlElement(name = "Identificador")
    private String identificador;

    @XmlElement(name = "Doc")
    private String doc;

    @XmlElement(name = "RZ")
    private String rz;

    @XmlElement(name = "Prefijo")
    private String prefijo;

    @XmlElement(name = "SubPrefijo")
    private String subPrefijo;

    @XmlElement(name = "Estado")
    private String estado;

    @XmlElement(name = "FechaNov")
    private String fechaNov;

    @XmlElement(name = "EncPor")
    private String encPor;

    public String getIdentificador() { return identificador; }

    public void setIdentificador(String identificador) { this.identificador = identificador; }

    public String getDoc() { return doc; }

    public void setDoc(String doc) { this.doc = doc; }

    public String getRz() { return rz; }

    public void setRz(String rz) { this.rz = rz; }

    public String getPrefijo() { return prefijo; }

    public void setPrefijo(String prefijo) { this.prefijo = prefijo; }

    public String getSubPrefijo() { return subPrefijo; }

    public void setSubPrefijo(String subPrefijo) { this.subPrefijo = subPrefijo; }

    public String getEstado() { return estado; }

    public void setEstado(String estado) { this.estado = estado; }

    public String getFechaNov() { return fechaNov; }

    public void setFechaNov(String fechaNov) { this.fechaNov = fechaNov; }

    public String getEncPor() { return encPor; }

    public void setEncPor(String encPor) { this.encPor = encPor; }
}