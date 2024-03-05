package com.affirm.nosis;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Historia")
public class Historia {
    @XmlElement(name = "Deudas")
    private DeudasEntidad deudas;

    public DeudasEntidad getDeudas() { return deudas; }

    public void setDeudas(DeudasEntidad deudas) { this.deudas = deudas; }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlRootElement(name = "Deudas")
    public static class DeudasEntidad {
        @XmlElement(name = "Deuda")
        private List<DeudaEntidad> deuda;

        @XmlAttribute(name = "Cant")
        private String cant;

        public List<DeudaEntidad> getDeudas() {
            return deuda;
        }

        public void setDeudas(List<DeudaEntidad> deuda) {
            this.deuda = deuda;
        }

        public String getCant() {
            return cant;
        }

        public void setCant(String cant) {
            this.cant = cant;
        }

        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlRootElement(name = "Deuda")
        public static class DeudaEntidad {
            @XmlAttribute(name = "Ent_Cod")
            private String entCod;

            @XmlAttribute(name = "Sit")
            private String sit;

            @XmlAttribute(name = "Periodo")
            private String periodo;

            @XmlAttribute(name = "Monto")
            private String monto;

            @XmlAttribute(name = "Proc_Jud")
            private String procJud;

            @XmlAttribute(name = "CodEntidad")
            private String codEntidad;

            @XmlAttribute(name = "Entidad")
            private String entidad;

            public String getEntCod() { return entCod; }

            public void setEntCod(String entCod) { this.entCod = entCod; }

            public String getSit() { return sit; }

            public void setSit(String sit) { this.sit = sit; }

            public String getPeriodo() { return periodo; }

            public void setPeriodo(String periodo) { this.periodo = periodo; }

            public String getMonto() { return monto; }

            public void setMonto(String monto) { this.monto = monto; }

            public String getProcJud() { return procJud; }

            public void setProcJud(String procJud) { this.procJud = procJud; }

            public String getCodEntidad() { return codEntidad; }

            public void setCodEntidad(String codEntidad) { this.codEntidad = codEntidad; }

            public String getEntidad() { return entidad; }

            public void setEntidad(String entidad) { this.entidad = entidad; }
        }
    }
}
