
package com.affirm.abaco.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para eCuota complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="eCuota">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="codigoCuota" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="fechaVencimientoCuota" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="remanenteCapital" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="capitalCuota" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="interesCuota" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="subtotalCuota" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="montoSeguro" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="totalCuota" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="comision" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="impuestoComision" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "eCuota", propOrder = {
    "codigoCuota",
    "fechaVencimientoCuota",
    "remanenteCapital",
    "capitalCuota",
    "interesCuota",
    "subtotalCuota",
    "montoSeguro",
    "totalCuota",
    "comision",
    "impuestoComision"
})
public class ECuota {

    protected int codigoCuota;
    @XmlElement(required = true)
    protected String fechaVencimientoCuota;
    protected double remanenteCapital;
    protected double capitalCuota;
    protected double interesCuota;
    protected double subtotalCuota;
    protected double montoSeguro;
    protected double totalCuota;
    protected double comision;
    protected double impuestoComision;

    /**
     * Obtiene el valor de la propiedad codigoCuota.
     * 
     */
    public int getCodigoCuota() {
        return codigoCuota;
    }

    /**
     * Define el valor de la propiedad codigoCuota.
     * 
     */
    public void setCodigoCuota(int value) {
        this.codigoCuota = value;
    }

    /**
     * Obtiene el valor de la propiedad fechaVencimientoCuota.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFechaVencimientoCuota() {
        return fechaVencimientoCuota;
    }

    /**
     * Define el valor de la propiedad fechaVencimientoCuota.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFechaVencimientoCuota(String value) {
        this.fechaVencimientoCuota = value;
    }

    /**
     * Obtiene el valor de la propiedad remanenteCapital.
     * 
     */
    public double getRemanenteCapital() {
        return remanenteCapital;
    }

    /**
     * Define el valor de la propiedad remanenteCapital.
     * 
     */
    public void setRemanenteCapital(double value) {
        this.remanenteCapital = value;
    }

    /**
     * Obtiene el valor de la propiedad capitalCuota.
     * 
     */
    public double getCapitalCuota() {
        return capitalCuota;
    }

    /**
     * Define el valor de la propiedad capitalCuota.
     * 
     */
    public void setCapitalCuota(double value) {
        this.capitalCuota = value;
    }

    /**
     * Obtiene el valor de la propiedad interesCuota.
     * 
     */
    public double getInteresCuota() {
        return interesCuota;
    }

    /**
     * Define el valor de la propiedad interesCuota.
     * 
     */
    public void setInteresCuota(double value) {
        this.interesCuota = value;
    }

    /**
     * Obtiene el valor de la propiedad subtotalCuota.
     * 
     */
    public double getSubtotalCuota() {
        return subtotalCuota;
    }

    /**
     * Define el valor de la propiedad subtotalCuota.
     * 
     */
    public void setSubtotalCuota(double value) {
        this.subtotalCuota = value;
    }

    /**
     * Obtiene el valor de la propiedad montoSeguro.
     * 
     */
    public double getMontoSeguro() {
        return montoSeguro;
    }

    /**
     * Define el valor de la propiedad montoSeguro.
     * 
     */
    public void setMontoSeguro(double value) {
        this.montoSeguro = value;
    }

    /**
     * Obtiene el valor de la propiedad totalCuota.
     * 
     */
    public double getTotalCuota() {
        return totalCuota;
    }

    /**
     * Define el valor de la propiedad totalCuota.
     * 
     */
    public void setTotalCuota(double value) {
        this.totalCuota = value;
    }

    /**
     * Obtiene el valor de la propiedad comision.
     * 
     */
    public double getComision() {
        return comision;
    }

    /**
     * Define el valor de la propiedad comision.
     * 
     */
    public void setComision(double value) {
        this.comision = value;
    }

    /**
     * Obtiene el valor de la propiedad impuestoComision.
     * 
     */
    public double getImpuestoComision() {
        return impuestoComision;
    }

    /**
     * Define el valor de la propiedad impuestoComision.
     * 
     */
    public void setImpuestoComision(double value) {
        this.impuestoComision = value;
    }

}
