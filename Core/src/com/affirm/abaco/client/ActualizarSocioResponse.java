
package com.affirm.abaco.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para actualizarSocioResponse complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="actualizarSocioResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="rptaMensaje" type="{http://servicio.ws/}eMensaje" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "actualizarSocioResponse", propOrder = {
    "rptaMensaje"
})
public class ActualizarSocioResponse {

    protected EMensaje rptaMensaje;

    /**
     * Obtiene el valor de la propiedad rptaMensaje.
     * 
     * @return
     *     possible object is
     *     {@link EMensaje }
     *     
     */
    public EMensaje getRptaMensaje() {
        return rptaMensaje;
    }

    /**
     * Define el valor de la propiedad rptaMensaje.
     * 
     * @param value
     *     allowed object is
     *     {@link EMensaje }
     *     
     */
    public void setRptaMensaje(EMensaje value) {
        this.rptaMensaje = value;
    }

}
