
package com.affirm.equifax.ws;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.*;


/**
 * <p>Clase Java para anonymous complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Puntaje" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="NivelRiesgo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Conclusion" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "puntaje",
    "nivelRiesgo",
    "conclusion"
})
@XmlRootElement(name = "RiskPredictor")
public class RiskPredictor {

    @XmlElementRef(name = "Puntaje", type = JAXBElement.class, required = false)
    protected JAXBElement<String> puntaje;
    @XmlElementRef(name = "NivelRiesgo", type = JAXBElement.class, required = false)
    protected JAXBElement<String> nivelRiesgo;
    @XmlElementRef(name = "Conclusion", type = JAXBElement.class, required = false)
    protected JAXBElement<String> conclusion;

    /**
     * Obtiene el valor de la propiedad puntaje.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getPuntaje() {
        return puntaje;
    }

    /**
     * Define el valor de la propiedad puntaje.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setPuntaje(JAXBElement<String> value) {
        this.puntaje = value;
    }

    /**
     * Obtiene el valor de la propiedad nivelRiesgo.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getNivelRiesgo() {
        return nivelRiesgo;
    }

    /**
     * Define el valor de la propiedad nivelRiesgo.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setNivelRiesgo(JAXBElement<String> value) {
        this.nivelRiesgo = value;
    }

    /**
     * Obtiene el valor de la propiedad conclusion.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getConclusion() {
        return conclusion;
    }

    /**
     * Define el valor de la propiedad conclusion.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setConclusion(JAXBElement<String> value) {
        this.conclusion = value;
    }

}
