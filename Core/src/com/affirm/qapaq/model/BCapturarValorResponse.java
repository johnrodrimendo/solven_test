
package com.affirm.qapaq.model;

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="B_CapturarValorResult" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "bCapturarValorResult"
})
@XmlRootElement(name = "B_CapturarValorResponse")
public class BCapturarValorResponse {

    @XmlElement(name = "B_CapturarValorResult")
    protected String bCapturarValorResult;

    public boolean isSuccessful() {
        return "1".equals(bCapturarValorResult);
    }

    /**
     * Gets the value of the bCapturarValorResult property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBCapturarValorResult() {
        return bCapturarValorResult;
    }

    /**
     * Sets the value of the bCapturarValorResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBCapturarValorResult(String value) {
        this.bCapturarValorResult = value;
    }

}
