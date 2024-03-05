
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
 *         &lt;element name="Gx_usuenc" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="Gx_pasenc" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="Gx_key" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="Ti_tipodoc" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="Ti_nrodoc" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="Cg_tipodoc" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="Cg_nrodoc" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
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
    "tiTipodoc",
    "tiNrodoc",
    "cgTipodoc",
    "cgNrodoc"
})
@XmlRootElement(name = "WS_SentinelInfPri.Execute")
public class WSSentinelInfPriExecute {

    @XmlElement(name = "Gx_usuenc", required = true)
    protected String gxUsuenc;
    @XmlElement(name = "Gx_pasenc", required = true)
    protected String gxPasenc;
    @XmlElement(name = "Gx_key", required = true)
    protected String gxKey;
    @XmlElement(name = "Ti_tipodoc", required = true)
    protected String tiTipodoc;
    @XmlElement(name = "Ti_nrodoc", required = true)
    protected String tiNrodoc;
    @XmlElement(name = "Cg_tipodoc", required = false)
    protected String cgTipodoc;
    @XmlElement(name = "Cg_nrodoc", required = false)
    protected String cgNrodoc;

    /**
     * Obtiene el valor de la propiedad gxUsuenc.
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
     * Define el valor de la propiedad gxUsuenc.
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
     * Obtiene el valor de la propiedad gxPasenc.
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
     * Define el valor de la propiedad gxPasenc.
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
     * Obtiene el valor de la propiedad gxKey.
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
     * Define el valor de la propiedad gxKey.
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
     * Obtiene el valor de la propiedad tiTipodoc.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTiTipodoc() {
        return tiTipodoc;
    }

    /**
     * Define el valor de la propiedad tiTipodoc.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTiTipodoc(String value) {
        this.tiTipodoc = value;
    }

    /**
     * Obtiene el valor de la propiedad tiNrodoc.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTiNrodoc() {
        return tiNrodoc;
    }

    /**
     * Define el valor de la propiedad tiNrodoc.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTiNrodoc(String value) {
        this.tiNrodoc = value;
    }

    /**
     * Obtiene el valor de la propiedad cgTipodoc.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCgTipodoc() {
        return cgTipodoc;
    }

    /**
     * Define el valor de la propiedad cgTipodoc.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCgTipodoc(String value) {
        this.cgTipodoc = value;
    }

    /**
     * Obtiene el valor de la propiedad cgNrodoc.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCgNrodoc() {
        return cgNrodoc;
    }

    /**
     * Define el valor de la propiedad cgNrodoc.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCgNrodoc(String value) {
        this.cgNrodoc = value;
    }

}
