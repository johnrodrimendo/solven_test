
package com.affirm.equifax.ws;

import javax.xml.bind.annotation.*;
import java.math.BigDecimal;


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
 *         &lt;element name="Categoria" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="RangoInicial" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="RangoFinal" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
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
    "categoria",
    "rangoInicial",
    "rangoFinal"
})
@XmlRootElement(name = "IncomePredictor")
public class IncomePredictor {

    @XmlElement(name = "Categoria", required = true)
    protected String categoria;
    @XmlElement(name = "RangoInicial", required = true)
    protected BigDecimal rangoInicial;
    @XmlElement(name = "RangoFinal", required = true)
    protected BigDecimal rangoFinal;

    /**
     * Obtiene el valor de la propiedad categoria.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCategoria() {
        return categoria;
    }

    /**
     * Define el valor de la propiedad categoria.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCategoria(String value) {
        this.categoria = value;
    }

    /**
     * Obtiene el valor de la propiedad rangoInicial.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getRangoInicial() {
        return rangoInicial;
    }

    /**
     * Define el valor de la propiedad rangoInicial.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setRangoInicial(BigDecimal value) {
        this.rangoInicial = value;
    }

    /**
     * Obtiene el valor de la propiedad rangoFinal.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getRangoFinal() {
        return rangoFinal;
    }

    /**
     * Define el valor de la propiedad rangoFinal.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setRangoFinal(BigDecimal value) {
        this.rangoFinal = value;
    }

}
