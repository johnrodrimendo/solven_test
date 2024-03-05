
package com.affirm.efectiva.client;

import javax.xml.bind.annotation.*;


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
 *         &lt;element name="wm_interface_cme_movilResult" type="{http://ws_NCanales_movil/}ArrayOfMovilCMERpt" minOccurs="0"/>
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
    "wmInterfaceCmeMovilResult"
})
@XmlRootElement(name = "wm_interface_cme_movilResponse")
public class WmInterfaceCmeMovilResponse {

    @XmlElement(name = "wm_interface_cme_movilResult")
    protected ArrayOfMovilCMERpt wmInterfaceCmeMovilResult;

    /**
     * Obtiene el valor de la propiedad wmInterfaceCmeMovilResult.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfMovilCMERpt }
     *     
     */
    public ArrayOfMovilCMERpt getWmInterfaceCmeMovilResult() {
        return wmInterfaceCmeMovilResult;
    }

    /**
     * Define el valor de la propiedad wmInterfaceCmeMovilResult.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfMovilCMERpt }
     *     
     */
    public void setWmInterfaceCmeMovilResult(ArrayOfMovilCMERpt value) {
        this.wmInterfaceCmeMovilResult = value;
    }

}
