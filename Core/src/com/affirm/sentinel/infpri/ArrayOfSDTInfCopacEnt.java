
package com.affirm.sentinel.infpri;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para ArrayOfSDT_InfCopacEnt complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfSDT_InfCopacEnt"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="SDT_InfCopacEnt" type="{PrivadoE2Cliente}SDT_InfCopacEnt" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfSDT_InfCopacEnt", propOrder = {
    "sdtInfCopacEnt"
})
public class ArrayOfSDTInfCopacEnt {

    @XmlElement(name = "SDT_InfCopacEnt")
    protected List<SDTInfCopacEnt> sdtInfCopacEnt;

    /**
     * Gets the value of the sdtInfCopacEnt property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the sdtInfCopacEnt property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSDTInfCopacEnt().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SDTInfCopacEnt }
     * 
     * 
     */
    public List<SDTInfCopacEnt> getSDTInfCopacEnt() {
        if (sdtInfCopacEnt == null) {
            sdtInfCopacEnt = new ArrayList<SDTInfCopacEnt>();
        }
        return this.sdtInfCopacEnt;
    }

}
