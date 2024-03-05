
package com.affirm.abaco.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para crearCredito complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="crearCredito">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="entidadCredito" type="{http://servicio.ws/}eCredito"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "crearCredito", propOrder = {
    "entidadCredito"
})
public class CrearCredito {

    @XmlElement(required = true)
    protected ECredito entidadCredito;

    /**
     * Obtiene el valor de la propiedad entidadCredito.
     * 
     * @return
     *     possible object is
     *     {@link ECredito }
     *     
     */
    public ECredito getEntidadCredito() {
        return entidadCredito;
    }

    /**
     * Define el valor de la propiedad entidadCredito.
     * 
     * @param value
     *     allowed object is
     *     {@link ECredito }
     *     
     */
    public void setEntidadCredito(ECredito value) {
        this.entidadCredito = value;
    }

}
