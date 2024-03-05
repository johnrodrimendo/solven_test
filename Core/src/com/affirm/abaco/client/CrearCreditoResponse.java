
package com.affirm.abaco.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para crearCreditoResponse complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="crearCreditoResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="rptaCredito" type="{http://servicio.ws/}eRptaCredito" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "crearCreditoResponse", propOrder = {
    "rptaCredito"
})
public class CrearCreditoResponse {

    protected ERptaCredito rptaCredito;

    /**
     * Obtiene el valor de la propiedad rptaCredito.
     * 
     * @return
     *     possible object is
     *     {@link ERptaCredito }
     *     
     */
    public ERptaCredito getRptaCredito() {
        return rptaCredito;
    }

    /**
     * Define el valor de la propiedad rptaCredito.
     * 
     * @param value
     *     allowed object is
     *     {@link ERptaCredito }
     *     
     */
    public void setRptaCredito(ERptaCredito value) {
        this.rptaCredito = value;
    }

}
