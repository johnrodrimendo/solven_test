
package com.affirm.abaco.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para eMensaje complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="eMensaje">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="mensaje" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="codigoMensaje" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="idSocio" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nroCuentaAportacion" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "eMensaje", propOrder = {
    "mensaje",
    "codigoMensaje",
    "idSocio",
    "nroCuentaAportacion"
})
public class EMensaje {

    protected String mensaje;
    protected int codigoMensaje;
    protected String idSocio;
    protected String nroCuentaAportacion;

    public boolean isExitoso() {
        if (codigoMensaje == 0) return true;
        return false;
    }

    /**
     * Obtiene el valor de la propiedad mensaje.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMensaje() {
        return mensaje;
    }

    /**
     * Define el valor de la propiedad mensaje.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMensaje(String value) {
        this.mensaje = value;
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
     * Obtiene el valor de la propiedad nroCuentaAportacion.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNroCuentaAportacion() {
        return nroCuentaAportacion;
    }

    /**
     * Define el valor de la propiedad nroCuentaAportacion.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNroCuentaAportacion(String value) {
        this.nroCuentaAportacion = value;
    }

}
