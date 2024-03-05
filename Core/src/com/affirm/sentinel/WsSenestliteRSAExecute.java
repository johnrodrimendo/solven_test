
package com.affirm.sentinel;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Gx_usuenc" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="Gx_pasenc" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="Gx_key" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="Tipodoc" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="Nrodoc" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
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
    "gxUsuenc",
    "gxPasenc",
    "gxKey",
    "tipodoc",
    "nrodoc"
})
@XmlRootElement(name = "ws_senestliteRSA.Execute")
public class WsSenestliteRSAExecute {

    @XmlElement(name = "Gx_usuenc", required = true)
    protected String gxUsuenc;
    @XmlElement(name = "Gx_pasenc", required = true)
    protected String gxPasenc;
    @XmlElement(name = "Gx_key", required = true)
    protected String gxKey;
    @XmlElement(name = "Tipodoc", required = true)
    protected String tipodoc;
    @XmlElement(name = "Nrodoc", required = true)
    protected String nrodoc;

    /**
     * Gets the value of the gxUsuenc property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGxUsuenc() {
        return gxUsuenc;
    }

    /**
     * Sets the value of the gxUsuenc property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGxUsuenc(String value) {
        this.gxUsuenc = value;
    }

    /**
     * Gets the value of the gxPasenc property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGxPasenc() {
        return gxPasenc;
    }

    /**
     * Sets the value of the gxPasenc property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGxPasenc(String value) {
        this.gxPasenc = value;
    }

    /**
     * Gets the value of the gxKey property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGxKey() {
        return gxKey;
    }

    /**
     * Sets the value of the gxKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGxKey(String value) {
        this.gxKey = value;
    }

    /**
     * Gets the value of the tipodoc property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTipodoc() {
        return tipodoc;
    }

    /**
     * Sets the value of the tipodoc property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTipodoc(String value) {
        this.tipodoc = value;
    }

    /**
     * Gets the value of the nrodoc property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNrodoc() {
        return nrodoc;
    }

    /**
     * Sets the value of the nrodoc property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNrodoc(String value) {
        this.nrodoc = value;
    }

}
