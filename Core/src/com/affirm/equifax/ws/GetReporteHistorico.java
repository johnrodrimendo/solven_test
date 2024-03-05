
package com.affirm.equifax.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para GetReporteHistorico complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="GetReporteHistorico">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://ws.creditreport.equifax.com.pe/document}ConsultaHistorica" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetReporteHistorico", propOrder = {
    "consultaHistorica"
})
public class GetReporteHistorico {

    @XmlElement(name = "ConsultaHistorica", namespace = "http://ws.creditreport.equifax.com.pe/document")
    protected HistoricType consultaHistorica;

    /**
     * Obtiene el valor de la propiedad consultaHistorica.
     * 
     * @return
     *     possible object is
     *     {@link HistoricType }
     *     
     */
    public HistoricType getConsultaHistorica() {
        return consultaHistorica;
    }

    /**
     * Define el valor de la propiedad consultaHistorica.
     * 
     * @param value
     *     allowed object is
     *     {@link HistoricType }
     *     
     */
    public void setConsultaHistorica(HistoricType value) {
        this.consultaHistorica = value;
    }

}
