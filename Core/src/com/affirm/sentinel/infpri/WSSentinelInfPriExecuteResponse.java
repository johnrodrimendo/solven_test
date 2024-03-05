
package com.affirm.sentinel.infpri;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para anonymous complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Ti_nombres" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="Ti_paterno" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="Ti_materno" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="Ti_fchnac" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="Ti_entidades" type="{PrivadoE2Cliente}ArrayOfSDT_InfCopacEnt"/&gt;
 *         &lt;element name="Ti_calddpult24" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="Ti_montodocumento" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="Cg_nombres" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="Cg_paterno" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="Cg_materno" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="Cg_fchnac" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="Cg_entidades" type="{PrivadoE2Cliente}ArrayOfSDT_InfCopacEnt"/&gt;
 *         &lt;element name="Cg_calddpult24" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="Cg_montodocumento" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="Codigows" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "tiNombres",
    "tiPaterno",
    "tiMaterno",
    "tiFchnac",
    "tiEntidades",
    "tiCalddpult24",
    "tiMontodocumento",
    "cgNombres",
    "cgPaterno",
    "cgMaterno",
    "cgFchnac",
    "cgEntidades",
    "cgCalddpult24",
    "cgMontodocumento",
    "codigows"
})
@XmlRootElement(name = "WS_SentinelInfPri.ExecuteResponse")
public class WSSentinelInfPriExecuteResponse {

    @XmlElement(name = "Ti_nombres", required = true)
    protected String tiNombres;
    @XmlElement(name = "Ti_paterno", required = true)
    protected String tiPaterno;
    @XmlElement(name = "Ti_materno", required = true)
    protected String tiMaterno;
    @XmlElement(name = "Ti_fchnac", required = true)
    protected String tiFchnac;
    @XmlElement(name = "Ti_entidades", required = true)
    protected ArrayOfSDTInfCopacEnt tiEntidades;
    @XmlElement(name = "Ti_calddpult24", required = true)
    protected String tiCalddpult24;
    @XmlElement(name = "Ti_montodocumento", required = true)
    protected String tiMontodocumento;
    @XmlElement(name = "Cg_nombres", required = true)
    protected String cgNombres;
    @XmlElement(name = "Cg_paterno", required = true)
    protected String cgPaterno;
    @XmlElement(name = "Cg_materno", required = true)
    protected String cgMaterno;
    @XmlElement(name = "Cg_fchnac", required = true)
    protected String cgFchnac;
    @XmlElement(name = "Cg_entidades", required = true)
    protected ArrayOfSDTInfCopacEnt cgEntidades;
    @XmlElement(name = "Cg_calddpult24", required = true)
    protected String cgCalddpult24;
    @XmlElement(name = "Cg_montodocumento", required = true)
    protected String cgMontodocumento;
    @XmlElement(name = "Codigows", required = true)
    protected String codigows;

    /**
     * Obtiene el valor de la propiedad tiNombres.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTiNombres() {
        return tiNombres;
    }

    /**
     * Define el valor de la propiedad tiNombres.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTiNombres(String value) {
        this.tiNombres = value;
    }

    /**
     * Obtiene el valor de la propiedad tiPaterno.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTiPaterno() {
        return tiPaterno;
    }

    /**
     * Define el valor de la propiedad tiPaterno.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTiPaterno(String value) {
        this.tiPaterno = value;
    }

    /**
     * Obtiene el valor de la propiedad tiMaterno.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTiMaterno() {
        return tiMaterno;
    }

    /**
     * Define el valor de la propiedad tiMaterno.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTiMaterno(String value) {
        this.tiMaterno = value;
    }

    /**
     * Obtiene el valor de la propiedad tiFchnac.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTiFchnac() {
        return tiFchnac;
    }

    /**
     * Define el valor de la propiedad tiFchnac.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTiFchnac(String value) {
        this.tiFchnac = value;
    }

    /**
     * Obtiene el valor de la propiedad tiEntidades.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfSDTInfCopacEnt }
     *     
     */
    public ArrayOfSDTInfCopacEnt getTiEntidades() {
        return tiEntidades;
    }

    /**
     * Define el valor de la propiedad tiEntidades.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfSDTInfCopacEnt }
     *     
     */
    public void setTiEntidades(ArrayOfSDTInfCopacEnt value) {
        this.tiEntidades = value;
    }

    /**
     * Obtiene el valor de la propiedad tiCalddpult24.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTiCalddpult24() {
        return tiCalddpult24;
    }

    /**
     * Define el valor de la propiedad tiCalddpult24.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTiCalddpult24(String value) {
        this.tiCalddpult24 = value;
    }

    /**
     * Obtiene el valor de la propiedad tiMontodocumento.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTiMontodocumento() {
        return tiMontodocumento;
    }

    /**
     * Define el valor de la propiedad tiMontodocumento.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTiMontodocumento(String value) {
        this.tiMontodocumento = value;
    }

    /**
     * Obtiene el valor de la propiedad cgNombres.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCgNombres() {
        return cgNombres;
    }

    /**
     * Define el valor de la propiedad cgNombres.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCgNombres(String value) {
        this.cgNombres = value;
    }

    /**
     * Obtiene el valor de la propiedad cgPaterno.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCgPaterno() {
        return cgPaterno;
    }

    /**
     * Define el valor de la propiedad cgPaterno.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCgPaterno(String value) {
        this.cgPaterno = value;
    }

    /**
     * Obtiene el valor de la propiedad cgMaterno.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCgMaterno() {
        return cgMaterno;
    }

    /**
     * Define el valor de la propiedad cgMaterno.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCgMaterno(String value) {
        this.cgMaterno = value;
    }

    /**
     * Obtiene el valor de la propiedad cgFchnac.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCgFchnac() {
        return cgFchnac;
    }

    /**
     * Define el valor de la propiedad cgFchnac.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCgFchnac(String value) {
        this.cgFchnac = value;
    }

    /**
     * Obtiene el valor de la propiedad cgEntidades.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfSDTInfCopacEnt }
     *     
     */
    public ArrayOfSDTInfCopacEnt getCgEntidades() {
        return cgEntidades;
    }

    /**
     * Define el valor de la propiedad cgEntidades.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfSDTInfCopacEnt }
     *     
     */
    public void setCgEntidades(ArrayOfSDTInfCopacEnt value) {
        this.cgEntidades = value;
    }

    /**
     * Obtiene el valor de la propiedad cgCalddpult24.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCgCalddpult24() {
        return cgCalddpult24;
    }

    /**
     * Define el valor de la propiedad cgCalddpult24.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCgCalddpult24(String value) {
        this.cgCalddpult24 = value;
    }

    /**
     * Obtiene el valor de la propiedad cgMontodocumento.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCgMontodocumento() {
        return cgMontodocumento;
    }

    /**
     * Define el valor de la propiedad cgMontodocumento.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCgMontodocumento(String value) {
        this.cgMontodocumento = value;
    }

    /**
     * Obtiene el valor de la propiedad codigows.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodigows() {
        return codigows;
    }

    /**
     * Define el valor de la propiedad codigows.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodigows(String value) {
        this.codigows = value;
    }

}
