
package com.affirm.iovation.ws.transaction.check;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.affirm.iovation.ws.transaction.check package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _CheckTransactionDetailsTxnProperties_QNAME = new QName("http://www.iesnare.com/dra/api/CheckTransactionDetails", "txn_properties");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.affirm.iovation.ws.transaction.check
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link CheckTransactionDetails }
     * 
     */
    public CheckTransactionDetails createCheckTransactionDetails() {
        return new CheckTransactionDetails();
    }

    /**
     * Create an instance of {@link CheckTransactionDetailsResponse }
     * 
     */
    public CheckTransactionDetailsResponse createCheckTransactionDetailsResponse() {
        return new CheckTransactionDetailsResponse();
    }

    /**
     * Create an instance of {@link CheckTransactionDetailsResponse.Details }
     * 
     */
    public CheckTransactionDetailsResponse.Details createCheckTransactionDetailsResponseDetails() {
        return new CheckTransactionDetailsResponse.Details();
    }

    /**
     * Create an instance of {@link CheckTransactionDetails.TxnProperties }
     * 
     */
    public CheckTransactionDetails.TxnProperties createCheckTransactionDetailsTxnProperties() {
        return new CheckTransactionDetails.TxnProperties();
    }

    /**
     * Create an instance of {@link CheckTransactionDetailsResponse.Details.Detail }
     * 
     */
    public CheckTransactionDetailsResponse.Details.Detail createCheckTransactionDetailsResponseDetailsDetail() {
        return new CheckTransactionDetailsResponse.Details.Detail();
    }

    /**
     * Create an instance of {@link CheckTransactionDetails.TxnProperties.Property }
     * 
     */
    public CheckTransactionDetails.TxnProperties.Property createCheckTransactionDetailsTxnPropertiesProperty() {
        return new CheckTransactionDetails.TxnProperties.Property();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CheckTransactionDetails.TxnProperties }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.iesnare.com/dra/api/CheckTransactionDetails", name = "txn_properties", scope = CheckTransactionDetails.class)
    public JAXBElement<CheckTransactionDetails.TxnProperties> createCheckTransactionDetailsTxnProperties(CheckTransactionDetails.TxnProperties value) {
        return new JAXBElement<CheckTransactionDetails.TxnProperties>(_CheckTransactionDetailsTxnProperties_QNAME, CheckTransactionDetails.TxnProperties.class, CheckTransactionDetails.class, value);
    }

}
