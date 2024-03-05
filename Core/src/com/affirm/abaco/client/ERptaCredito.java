
package com.affirm.abaco.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para eRptaCredito complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="eRptaCredito">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="codigoMensaje" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="idSocio" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="idCredito" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="saldoCredito" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="capitalCredito" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="cuotasTotales" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="cuotasPagadas" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="codigoOperacion" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "eRptaCredito", propOrder = {
    "codigoMensaje",
    "idSocio",
    "idCredito",
    "saldoCredito",
    "capitalCredito",
    "cuotasTotales",
    "cuotasPagadas",
    "codigoOperacion"
})
public class ERptaCredito {

    protected int codigoMensaje;
    @XmlElement(required = true)
    protected String idSocio;
    protected String idCredito;
    protected double saldoCredito;
    protected double capitalCredito;
    protected int cuotasTotales;
    protected int cuotasPagadas;
    protected String codigoOperacion;

    public boolean isExitoso() {
        if (codigoMensaje == 0) return true;
        return false;
    }

    /**
     * Obtiene el valor de la propiedad codigoMensaje.
     * 
     */
    public int getCodigoMensaje() {
        return codigoMensaje;
    }

    /**
     * Define el valor de la propiedad codigoMensaje.
     * 
     */
    public void setCodigoMensaje(int value) {
        this.codigoMensaje = value;
    }

    /**
     * Obtiene el valor de la propiedad idSocio.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdSocio() {
        return idSocio;
    }

    /**
     * Define el valor de la propiedad idSocio.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdSocio(String value) {
        this.idSocio = value;
    }

    /**
     * Obtiene el valor de la propiedad idCredito.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdCredito() {
        return idCredito;
    }

    /**
     * Define el valor de la propiedad idCredito.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdCredito(String value) {
        this.idCredito = value;
    }

    /**
     * Obtiene el valor de la propiedad saldoCredito.
     * 
     */
    public double getSaldoCredito() {
        return saldoCredito;
    }

    /**
     * Define el valor de la propiedad saldoCredito.
     * 
     */
    public void setSaldoCredito(double value) {
        this.saldoCredito = value;
    }

    /**
     * Obtiene el valor de la propiedad capitalCredito.
     * 
     */
    public double getCapitalCredito() {
        return capitalCredito;
    }

    /**
     * Define el valor de la propiedad capitalCredito.
     * 
     */
    public void setCapitalCredito(double value) {
        this.capitalCredito = value;
    }

    /**
     * Obtiene el valor de la propiedad cuotasTotales.
     * 
     */
    public int getCuotasTotales() {
        return cuotasTotales;
    }

    /**
     * Define el valor de la propiedad cuotasTotales.
     * 
     */
    public void setCuotasTotales(int value) {
        this.cuotasTotales = value;
    }

    /**
     * Obtiene el valor de la propiedad cuotasPagadas.
     * 
     */
    public int getCuotasPagadas() {
        return cuotasPagadas;
    }

    /**
     * Define el valor de la propiedad cuotasPagadas.
     * 
     */
    public void setCuotasPagadas(int value) {
        this.cuotasPagadas = value;
    }

    /**
     * Obtiene el valor de la propiedad codigoOperacion.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodigoOperacion() {
        return codigoOperacion;
    }

    /**
     * Define el valor de la propiedad codigoOperacion.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodigoOperacion(String value) {
        this.codigoOperacion = value;
    }

}
