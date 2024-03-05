
package com.affirm.sentinel.infpri;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para SDT_InfCopacEnt complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="SDT_InfCopacEnt"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Entidad" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="EntiFechaInf" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="EntiSaldo" type="{http://www.w3.org/2001/XMLSchema}double"/&gt;
 *         &lt;element name="EntiUltCal" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="EntiPerCal12M" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SDT_InfCopacEnt", propOrder = {
    "entidad",
    "entiFechaInf",
    "entiSaldo",
    "entiUltCal",
    "entiPerCal12M"
})
public class SDTInfCopacEnt {

    @XmlElement(name = "Entidad", required = true)
    protected String entidad;
    @XmlElement(name = "EntiFechaInf", required = true)
    protected String entiFechaInf;
    @XmlElement(name = "EntiSaldo")
    protected double entiSaldo;
    @XmlElement(name = "EntiUltCal", required = true)
    protected String entiUltCal;
    @XmlElement(name = "EntiPerCal12M", required = true)
    protected String entiPerCal12M;

    /**
     * Obtiene el valor de la propiedad entidad.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEntidad() {
        return entidad;
    }

    /**
     * Define el valor de la propiedad entidad.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEntidad(String value) {
        this.entidad = value;
    }

    /**
     * Obtiene el valor de la propiedad entiFechaInf.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEntiFechaInf() {
        return entiFechaInf;
    }

    /**
     * Define el valor de la propiedad entiFechaInf.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEntiFechaInf(String value) {
        this.entiFechaInf = value;
    }

    /**
     * Obtiene el valor de la propiedad entiSaldo.
     * 
     */
    public double getEntiSaldo() {
        return entiSaldo;
    }

    /**
     * Define el valor de la propiedad entiSaldo.
     * 
     */
    public void setEntiSaldo(double value) {
        this.entiSaldo = value;
    }

    /**
     * Obtiene el valor de la propiedad entiUltCal.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEntiUltCal() {
        return entiUltCal;
    }

    /**
     * Define el valor de la propiedad entiUltCal.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEntiUltCal(String value) {
        this.entiUltCal = value;
    }

    /**
     * Obtiene el valor de la propiedad entiPerCal12M.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEntiPerCal12M() {
        return entiPerCal12M;
    }

    /**
     * Define el valor de la propiedad entiPerCal12M.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEntiPerCal12M(String value) {
        this.entiPerCal12M = value;
    }

}
