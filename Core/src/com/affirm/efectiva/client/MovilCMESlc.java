
package com.affirm.efectiva.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para MovilCMESlc complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="MovilCMESlc">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ObjCliente" type="{http://ws_NCanales_movil/}Cliente" minOccurs="0"/>
 *         &lt;element name="ObjAutenticacion" type="{http://ws_NCanales_movil/}Autenticacion" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MovilCMESlc", propOrder = {
    "objCliente",
    "objAutenticacion"
})
public class MovilCMESlc {

    @XmlElement(name = "ObjCliente")
    protected Cliente objCliente;
    @XmlElement(name = "ObjAutenticacion")
    protected Autenticacion objAutenticacion;

    /**
     * Obtiene el valor de la propiedad objCliente.
     * 
     * @return
     *     possible object is
     *     {@link Cliente }
     *     
     */
    public Cliente getObjCliente() {
        return objCliente;
    }

    /**
     * Define el valor de la propiedad objCliente.
     * 
     * @param value
     *     allowed object is
     *     {@link Cliente }
     *     
     */
    public void setObjCliente(Cliente value) {
        this.objCliente = value;
    }

    /**
     * Obtiene el valor de la propiedad objAutenticacion.
     * 
     * @return
     *     possible object is
     *     {@link Autenticacion }
     *     
     */
    public Autenticacion getObjAutenticacion() {
        return objAutenticacion;
    }

    /**
     * Define el valor de la propiedad objAutenticacion.
     * 
     * @param value
     *     allowed object is
     *     {@link Autenticacion }
     *     
     */
    public void setObjAutenticacion(Autenticacion value) {
        this.objAutenticacion = value;
    }

}
