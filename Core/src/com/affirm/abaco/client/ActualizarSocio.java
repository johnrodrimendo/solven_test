
package com.affirm.abaco.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para actualizarSocio complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="actualizarSocio">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="entidadSocio" type="{http://servicio.ws/}eSocio"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "actualizarSocio", propOrder = {
    "entidadSocio"
})
public class ActualizarSocio {

    @XmlElement(required = true)
    protected ESocio entidadSocio;

    /**
     * Obtiene el valor de la propiedad entidadSocio.
     * 
     * @return
     *     possible object is
     *     {@link ESocio }
     *     
     */
    public ESocio getEntidadSocio() {
        return entidadSocio;
    }

    /**
     * Define el valor de la propiedad entidadSocio.
     * 
     * @param value
     *     allowed object is
     *     {@link ESocio }
     *     
     */
    public void setEntidadSocio(ESocio value) {
        this.entidadSocio = value;
    }

}
