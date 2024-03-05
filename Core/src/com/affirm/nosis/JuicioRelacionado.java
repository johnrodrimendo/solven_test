package com.affirm.nosis;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Relacionados")
public class JuicioRelacionado {

    @XmlElement(name = "Rel")
    private List<Rel> relacionados;

    public List<Rel> getRelacionados() {
            return relacionados;
        }

    public void setRelacionados(List<Rel> relacionados) { this.relacionados = relacionados; }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlRootElement(name = "Rel")
    public static class Rel {
        @XmlAttribute(name = "Doc")
        private String doc;

        @XmlAttribute(name = "RZ")
        private String rz;

        @XmlAttribute(name = "Estado")
        private String estado;

        public String getDoc() { return doc; }

        public void setDoc(String doc) { this.doc = doc; }

        public String getRz() { return rz; }

        public void setRz(String rz) { this.rz = rz; }

        public String getEstado() { return estado; }

        public void setEstado(String estado) { this.estado = estado; }
    }
}