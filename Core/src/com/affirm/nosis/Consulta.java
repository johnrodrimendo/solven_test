package com.affirm.nosis;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Consulta")
public class Consulta {

    @XmlElement(name = "Fec")
    private String fec;

    @XmlElement(name = "ServidorWeb")
    private String servidorWeb;

    @XmlElement(name = "Tipo")
    private String tipo;

    @XmlElement(name = "CodCliente")
    private String codCliente;

    @XmlElement(name = "NroConsulta")
    private Integer nroConsulta;

    @XmlElement(name = "ConsCDA")
    private String consCDA;

    @XmlElement(name = "ConsGrupoVR")
    private String consNroGrupoVR;

    @XmlElement(name = "ConsSoloDoc")
    private String consSoloDoc;

    @XmlElement(name = "ConsultaHTML")
    private ConsultaHTML consultaHTML;

    @XmlElement(name = "ConsultaXML")
    private ConsultaXML consultaXML;

    @XmlElement(name = "Resultado")
    private Resultado resultado;

    public String getFec() {
        return fec;
    }

    public void setFec(String fec) {
        this.fec = fec;
    }

    public String getServidorWeb() {
        return servidorWeb;
    }

    public void setServidorWeb(String servidorWeb) {
        this.servidorWeb = servidorWeb;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getCodCliente() {
        return codCliente;
    }

    public void setCodCliente(String codCliente) {
        this.codCliente = codCliente;
    }

    public Integer getNroConsulta() {
        return nroConsulta;
    }

    public void setNroConsulta(Integer nroConsulta) {
        this.nroConsulta = nroConsulta;
    }

    public String getConsCDA() {
        return consCDA;
    }

    public void setConsCDA(String consCDA) {
        this.consCDA = consCDA;
    }

    public String getConsNroGrupoVR() {
        return consNroGrupoVR;
    }

    public void setConsNroGrupoVR(String consNroGrupoVR) {
        this.consNroGrupoVR = consNroGrupoVR;
    }

    public String getConsSoloDoc() {
        return consSoloDoc;
    }

    public void setConsSoloDoc(String consSoloDoc) {
        this.consSoloDoc = consSoloDoc;
    }

    public ConsultaHTML getConsultaHTML() {
        return consultaHTML;
    }

    public void setConsultaHTML(ConsultaHTML consultaHTML) {
        this.consultaHTML = consultaHTML;
    }

    public ConsultaXML getConsultaXML() {
        return consultaXML;
    }

    public void setConsultaXML(ConsultaXML consultaXML) {
        this.consultaXML = consultaXML;
    }
}
