
package com.affirm.sentinel;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Resultado" type="{PrivadoE2Cliente}sdt_respuesta_consulta_rapidaRSA"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "resultado"
})
@XmlRootElement(name = "ws_senestliteRSA.ExecuteResponse")
public class WsSenestliteRSAExecuteResponse {

    @XmlElement(name = "Resultado", required = true)
    protected SdtRespuestaConsultaRapidaRSA resultado;

    /**
     * Gets the value of the resultado property.
     * 
     * @return
     *     possible object is
     *     {@link SdtRespuestaConsultaRapidaRSA }
     *     
     */
    public SdtRespuestaConsultaRapidaRSA getResultado() {
        return resultado;
    }

    /**
     * Sets the value of the resultado property.
     * 
     * @param value
     *     allowed object is
     *     {@link SdtRespuestaConsultaRapidaRSA }
     *     
     */
    public void setResultado(SdtRespuestaConsultaRapidaRSA value) {
        this.resultado = value;
    }

}
