
package com.affirm.equifax.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para GetReporteHistoricoResponse complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="GetReporteHistoricoResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://ws.creditreport.equifax.com.pe/document}ReporteCrediticio" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetReporteHistoricoResponse", propOrder = {
    "reporteCrediticio"
})
public class GetReporteHistoricoResponse {

    @XmlElement(name = "ReporteCrediticio", namespace = "http://ws.creditreport.equifax.com.pe/document")
    protected ReporteCrediticio reporteCrediticio;

    /**
     * Obtiene el valor de la propiedad reporteCrediticio.
     * 
     * @return
     *     possible object is
     *     {@link ReporteCrediticio }
     *     
     */
    public ReporteCrediticio getReporteCrediticio() {
        return reporteCrediticio;
    }

    /**
     * Define el valor de la propiedad reporteCrediticio.
     * 
     * @param value
     *     allowed object is
     *     {@link ReporteCrediticio }
     *     
     */
    public void setReporteCrediticio(ReporteCrediticio value) {
        this.reporteCrediticio = value;
    }

}
