
package com.affirm.efectiva.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Clase Java para ArrayOfMovilCMERpt complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfMovilCMERpt">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="MovilCMERpt" type="{http://ws_NCanales_movil/}MovilCMERpt" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfMovilCMERpt", propOrder = {
    "movilCMERpt"
})
public class ArrayOfMovilCMERpt {

    @XmlElement(name = "MovilCMERpt", nillable = true)
    protected List<MovilCMERpt> movilCMERpt;

    /**
     * Gets the value of the movilCMERpt property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the movilCMERpt property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMovilCMERpt().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link MovilCMERpt }
     * 
     * 
     */
    public List<MovilCMERpt> getMovilCMERpt() {
        if (movilCMERpt == null) {
            movilCMERpt = new ArrayList<MovilCMERpt>();
        }
        return this.movilCMERpt;
    }

}
