
package com.affirm.equifax.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para GetReporteOnline complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="GetReporteOnline">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://ws.creditreport.equifax.com.pe/document}DatosConsulta" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetReporteOnline", propOrder = {
    "datosConsulta"
})
public class GetReporteOnline {

    @XmlElement(name = "DatosConsulta", namespace = "http://ws.creditreport.equifax.com.pe/document")
    protected QueryDataType datosConsulta;

    /**
     * Obtiene el valor de la propiedad datosConsulta.
     * 
     * @return
     *     possible object is
     *     {@link QueryDataType }
     *     
     */
    public QueryDataType getDatosConsulta() {
        return datosConsulta;
    }

    /**
     * Define el valor de la propiedad datosConsulta.
     * 
     * @param value
     *     allowed object is
     *     {@link QueryDataType }
     *     
     */
    public void setDatosConsulta(QueryDataType value) {
        this.datosConsulta = value;
    }

}
