
package com.affirm.iovation.ws.transaction.check;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


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
 *         &lt;element name="subscriberid" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="subscriberaccount" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="subscriberpasscode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="enduserip" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="accountcode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="beginblackbox" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="type" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="txn_properties" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="property" maxOccurs="2" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
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
    "subscriberid",
    "subscriberaccount",
    "subscriberpasscode",
    "enduserip",
    "accountcode",
    "beginblackbox",
    "type",
    "txnProperties"
})
@XmlRootElement(name = "CheckTransactionDetails")
public class CheckTransactionDetails {

    @XmlElement(required = true)
    protected String subscriberid;
    @XmlElement(required = true)
    protected String subscriberaccount;
    @XmlElement(required = true)
    protected String subscriberpasscode;
    protected String enduserip;
    protected String accountcode;
    protected String beginblackbox;
    protected String type;
    @XmlElementRef(name = "txn_properties", namespace = "http://www.iesnare.com/dra/api/CheckTransactionDetails", type = JAXBElement.class, required = false)
    protected JAXBElement<CheckTransactionDetails.TxnProperties> txnProperties;

    /**
     * Obtiene el valor de la propiedad subscriberid.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSubscriberid() {
        return subscriberid;
    }

    /**
     * Define el valor de la propiedad subscriberid.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSubscriberid(String value) {
        this.subscriberid = value;
    }

    /**
     * Obtiene el valor de la propiedad subscriberaccount.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSubscriberaccount() {
        return subscriberaccount;
    }

    /**
     * Define el valor de la propiedad subscriberaccount.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSubscriberaccount(String value) {
        this.subscriberaccount = value;
    }

    /**
     * Obtiene el valor de la propiedad subscriberpasscode.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSubscriberpasscode() {
        return subscriberpasscode;
    }

    /**
     * Define el valor de la propiedad subscriberpasscode.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSubscriberpasscode(String value) {
        this.subscriberpasscode = value;
    }

    /**
     * Obtiene el valor de la propiedad enduserip.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEnduserip() {
        return enduserip;
    }

    /**
     * Define el valor de la propiedad enduserip.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEnduserip(String value) {
        this.enduserip = value;
    }

    /**
     * Obtiene el valor de la propiedad accountcode.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAccountcode() {
        return accountcode;
    }

    /**
     * Define el valor de la propiedad accountcode.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAccountcode(String value) {
        this.accountcode = value;
    }

    /**
     * Obtiene el valor de la propiedad beginblackbox.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBeginblackbox() {
        return beginblackbox;
    }

    /**
     * Define el valor de la propiedad beginblackbox.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBeginblackbox(String value) {
        this.beginblackbox = value;
    }

    /**
     * Obtiene el valor de la propiedad type.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
        return type;
    }

    /**
     * Define el valor de la propiedad type.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(String value) {
        this.type = value;
    }

    /**
     * Obtiene el valor de la propiedad txnProperties.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link CheckTransactionDetails.TxnProperties }{@code >}
     *     
     */
    public JAXBElement<CheckTransactionDetails.TxnProperties> getTxnProperties() {
        return txnProperties;
    }

    /**
     * Define el valor de la propiedad txnProperties.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link CheckTransactionDetails.TxnProperties }{@code >}
     *     
     */
    public void setTxnProperties(JAXBElement<CheckTransactionDetails.TxnProperties> value) {
        this.txnProperties = value;
    }


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
     *         &lt;element name="property" maxOccurs="2" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                   &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
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
        "property"
    })
    public static class TxnProperties {

        protected List<CheckTransactionDetails.TxnProperties.Property> property;

        /**
         * Gets the value of the property property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the property property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getProperty().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link CheckTransactionDetails.TxnProperties.Property }
         * 
         * 
         */
        public List<CheckTransactionDetails.TxnProperties.Property> getProperty() {
            if (property == null) {
                property = new ArrayList<CheckTransactionDetails.TxnProperties.Property>();
            }
            return this.property;
        }


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
         *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *         &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
            "name",
            "value"
        })
        public static class Property {

            @XmlElement(required = true)
            protected String name;
            @XmlElement(required = true)
            protected String value;

            /**
             * Obtiene el valor de la propiedad name.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getName() {
                return name;
            }

            /**
             * Define el valor de la propiedad name.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setName(String value) {
                this.name = value;
            }

            /**
             * Obtiene el valor de la propiedad value.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getValue() {
                return value;
            }

            /**
             * Define el valor de la propiedad value.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setValue(String value) {
                this.value = value;
            }

        }

    }

}
