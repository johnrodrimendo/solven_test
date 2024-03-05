
package com.affirm.qapaq.model;

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Opt" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Identificacion" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Username" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Llave" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "opt",
    "identificacion",
    "username",
    "llave"
})
@XmlRootElement(name = "B_CapturarValor")
public class BCapturarValor {

    @XmlElement(name = "Opt")
    protected String opt;
    @XmlElement(name = "Identificacion")
    protected String identificacion;
    @XmlElement(name = "Username")
    protected String username;
    @XmlElement(name = "Llave")
    protected String llave;

    /**
     * Gets the value of the opt property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOpt() {
        return opt;
    }

    /**
     * Sets the value of the opt property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOpt(String value) {
        this.opt = value;
    }

    /**
     * Gets the value of the identificacion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdentificacion() {
        return identificacion;
    }

    /**
     * Sets the value of the identificacion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdentificacion(String value) {
        this.identificacion = value;
    }

    /**
     * Gets the value of the username property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the value of the username property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUsername(String value) {
        this.username = value;
    }

    /**
     * Gets the value of the llave property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLlave() {
        return llave;
    }

    /**
     * Sets the value of the llave property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLlave(String value) {
        this.llave = value;
    }

}
