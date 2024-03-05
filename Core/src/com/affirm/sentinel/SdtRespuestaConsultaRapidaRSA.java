
package com.affirm.sentinel;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for sdt_respuesta_consulta_rapidaRSA complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="sdt_respuesta_consulta_rapidaRSA"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Documento" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="RazonSocial" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="FechaNacimiento" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="FechaProceso" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="Semaforos" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="Nota" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="NroBancos" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="DeudaTotal" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="VencidoBanco" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="Calificativo" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="SemaActual" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="SemaPrevio" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="SemaPeorMejor" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="Documento2" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="EstDomic" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="CondDomic" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="DeudaTributaria" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="DeudaLaboral" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="DeudaImpaga" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="DeudaProtestos" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="DeudaSBS" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="TarCtas" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="RepNeg" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="TipoActv" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="FechIniActv" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="DireccionFiscal" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="CodigoWS" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "sdt_respuesta_consulta_rapidaRSA", propOrder = {
    "documento",
    "razonSocial",
    "fechaNacimiento",
    "fechaProceso",
    "semaforos",
    "nota",
    "nroBancos",
    "deudaTotal",
    "vencidoBanco",
    "calificativo",
    "semaActual",
    "semaPrevio",
    "semaPeorMejor",
    "documento2",
    "estDomic",
    "condDomic",
    "deudaTributaria",
    "deudaLaboral",
    "deudaImpaga",
    "deudaProtestos",
    "deudaSBS",
    "tarCtas",
    "repNeg",
    "tipoActv",
    "fechIniActv",
    "direccionFiscal",
    "codigoWS"
})
public class SdtRespuestaConsultaRapidaRSA {

    @XmlElement(name = "Documento", required = true)
    protected String documento;
    @XmlElement(name = "RazonSocial", required = true)
    protected String razonSocial;
    @XmlElement(name = "FechaNacimiento", required = true)
    protected String fechaNacimiento;
    @XmlElement(name = "FechaProceso", required = true)
    protected String fechaProceso;
    @XmlElement(name = "Semaforos", required = true)
    protected String semaforos;
    @XmlElement(name = "Nota", required = true)
    protected String nota;
    @XmlElement(name = "NroBancos", required = true)
    protected String nroBancos;
    @XmlElement(name = "DeudaTotal", required = true)
    protected String deudaTotal;
    @XmlElement(name = "VencidoBanco", required = true)
    protected String vencidoBanco;
    @XmlElement(name = "Calificativo", required = true)
    protected String calificativo;
    @XmlElement(name = "SemaActual", required = true)
    protected String semaActual;
    @XmlElement(name = "SemaPrevio", required = true)
    protected String semaPrevio;
    @XmlElement(name = "SemaPeorMejor", required = true)
    protected String semaPeorMejor;
    @XmlElement(name = "Documento2", required = true)
    protected String documento2;
    @XmlElement(name = "EstDomic", required = true)
    protected String estDomic;
    @XmlElement(name = "CondDomic", required = true)
    protected String condDomic;
    @XmlElement(name = "DeudaTributaria", required = true)
    protected String deudaTributaria;
    @XmlElement(name = "DeudaLaboral", required = true)
    protected String deudaLaboral;
    @XmlElement(name = "DeudaImpaga", required = true)
    protected String deudaImpaga;
    @XmlElement(name = "DeudaProtestos", required = true)
    protected String deudaProtestos;
    @XmlElement(name = "DeudaSBS", required = true)
    protected String deudaSBS;
    @XmlElement(name = "TarCtas", required = true)
    protected String tarCtas;
    @XmlElement(name = "RepNeg", required = true)
    protected String repNeg;
    @XmlElement(name = "TipoActv", required = true)
    protected String tipoActv;
    @XmlElement(name = "FechIniActv", required = true)
    protected String fechIniActv;
    @XmlElement(name = "DireccionFiscal", required = true)
    protected String direccionFiscal;
    @XmlElement(name = "CodigoWS", required = true)
    protected String codigoWS;

    /**
     * Gets the value of the documento property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDocumento() {
        return documento;
    }

    /**
     * Sets the value of the documento property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDocumento(String value) {
        this.documento = value;
    }

    /**
     * Gets the value of the razonSocial property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRazonSocial() {
        return razonSocial;
    }

    /**
     * Sets the value of the razonSocial property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRazonSocial(String value) {
        this.razonSocial = value;
    }

    /**
     * Gets the value of the fechaNacimiento property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    /**
     * Sets the value of the fechaNacimiento property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFechaNacimiento(String value) {
        this.fechaNacimiento = value;
    }

    /**
     * Gets the value of the fechaProceso property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFechaProceso() {
        return fechaProceso;
    }

    /**
     * Sets the value of the fechaProceso property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFechaProceso(String value) {
        this.fechaProceso = value;
    }

    /**
     * Gets the value of the semaforos property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSemaforos() {
        return semaforos;
    }

    /**
     * Sets the value of the semaforos property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSemaforos(String value) {
        this.semaforos = value;
    }

    /**
     * Gets the value of the nota property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNota() {
        return nota;
    }

    /**
     * Sets the value of the nota property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNota(String value) {
        this.nota = value;
    }

    /**
     * Gets the value of the nroBancos property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNroBancos() {
        return nroBancos;
    }

    /**
     * Sets the value of the nroBancos property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNroBancos(String value) {
        this.nroBancos = value;
    }

    /**
     * Gets the value of the deudaTotal property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDeudaTotal() {
        return deudaTotal;
    }

    /**
     * Sets the value of the deudaTotal property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDeudaTotal(String value) {
        this.deudaTotal = value;
    }

    /**
     * Gets the value of the vencidoBanco property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVencidoBanco() {
        return vencidoBanco;
    }

    /**
     * Sets the value of the vencidoBanco property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVencidoBanco(String value) {
        this.vencidoBanco = value;
    }

    /**
     * Gets the value of the calificativo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCalificativo() {
        return calificativo;
    }

    /**
     * Sets the value of the calificativo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCalificativo(String value) {
        this.calificativo = value;
    }

    /**
     * Gets the value of the semaActual property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSemaActual() {
        return semaActual;
    }

    /**
     * Sets the value of the semaActual property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSemaActual(String value) {
        this.semaActual = value;
    }

    /**
     * Gets the value of the semaPrevio property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSemaPrevio() {
        return semaPrevio;
    }

    /**
     * Sets the value of the semaPrevio property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSemaPrevio(String value) {
        this.semaPrevio = value;
    }

    /**
     * Gets the value of the semaPeorMejor property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSemaPeorMejor() {
        return semaPeorMejor;
    }

    /**
     * Sets the value of the semaPeorMejor property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSemaPeorMejor(String value) {
        this.semaPeorMejor = value;
    }

    /**
     * Gets the value of the documento2 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDocumento2() {
        return documento2;
    }

    /**
     * Sets the value of the documento2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDocumento2(String value) {
        this.documento2 = value;
    }

    /**
     * Gets the value of the estDomic property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEstDomic() {
        return estDomic;
    }

    /**
     * Sets the value of the estDomic property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEstDomic(String value) {
        this.estDomic = value;
    }

    /**
     * Gets the value of the condDomic property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCondDomic() {
        return condDomic;
    }

    /**
     * Sets the value of the condDomic property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCondDomic(String value) {
        this.condDomic = value;
    }

    /**
     * Gets the value of the deudaTributaria property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDeudaTributaria() {
        return deudaTributaria;
    }

    /**
     * Sets the value of the deudaTributaria property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDeudaTributaria(String value) {
        this.deudaTributaria = value;
    }

    /**
     * Gets the value of the deudaLaboral property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDeudaLaboral() {
        return deudaLaboral;
    }

    /**
     * Sets the value of the deudaLaboral property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDeudaLaboral(String value) {
        this.deudaLaboral = value;
    }

    /**
     * Gets the value of the deudaImpaga property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDeudaImpaga() {
        return deudaImpaga;
    }

    /**
     * Sets the value of the deudaImpaga property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDeudaImpaga(String value) {
        this.deudaImpaga = value;
    }

    /**
     * Gets the value of the deudaProtestos property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDeudaProtestos() {
        return deudaProtestos;
    }

    /**
     * Sets the value of the deudaProtestos property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDeudaProtestos(String value) {
        this.deudaProtestos = value;
    }

    /**
     * Gets the value of the deudaSBS property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDeudaSBS() {
        return deudaSBS;
    }

    /**
     * Sets the value of the deudaSBS property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDeudaSBS(String value) {
        this.deudaSBS = value;
    }

    /**
     * Gets the value of the tarCtas property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTarCtas() {
        return tarCtas;
    }

    /**
     * Sets the value of the tarCtas property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTarCtas(String value) {
        this.tarCtas = value;
    }

    /**
     * Gets the value of the repNeg property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRepNeg() {
        return repNeg;
    }

    /**
     * Sets the value of the repNeg property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRepNeg(String value) {
        this.repNeg = value;
    }

    /**
     * Gets the value of the tipoActv property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTipoActv() {
        return tipoActv;
    }

    /**
     * Sets the value of the tipoActv property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTipoActv(String value) {
        this.tipoActv = value;
    }

    /**
     * Gets the value of the fechIniActv property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFechIniActv() {
        return fechIniActv;
    }

    /**
     * Sets the value of the fechIniActv property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFechIniActv(String value) {
        this.fechIniActv = value;
    }

    /**
     * Gets the value of the direccionFiscal property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDireccionFiscal() {
        return direccionFiscal;
    }

    /**
     * Sets the value of the direccionFiscal property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDireccionFiscal(String value) {
        this.direccionFiscal = value;
    }

    /**
     * Gets the value of the codigoWS property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodigoWS() {
        return codigoWS;
    }

    /**
     * Sets the value of the codigoWS property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodigoWS(String value) {
        this.codigoWS = value;
    }

}
