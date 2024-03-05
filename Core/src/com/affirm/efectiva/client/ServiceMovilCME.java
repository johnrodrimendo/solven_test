
package com.affirm.efectiva.client;

import javax.xml.namespace.QName;
import javax.xml.ws.*;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.9-b130926.1035
 * Generated source version: 2.2
 * 
 */
@WebServiceClient(name = "ServiceMovilCME", targetNamespace = "http://ws_NCanales_movil/", wsdlLocation = "http://190.116.176.18/WS_NCanales_movil/ServiceMovilCME.asmx?wsdl")
public class ServiceMovilCME
    extends Service
{

    private final static URL SERVICEMOVILCME_WSDL_LOCATION;
    private final static WebServiceException SERVICEMOVILCME_EXCEPTION;
    private final static QName SERVICEMOVILCME_QNAME = new QName("http://ws_NCanales_movil/", "ServiceMovilCME");

    static {
        URL url = null;
        WebServiceException e = null;
        try {
            //url = new URL("http://190.116.176.18/WS_NCanales_movil/ServiceMovilCME.asmx?wsdl");
            url = new URL(System.getenv("EFECTIVA_URL"));
        } catch (MalformedURLException ex) {
            e = new WebServiceException(ex);
        }
        SERVICEMOVILCME_WSDL_LOCATION = url;
        SERVICEMOVILCME_EXCEPTION = e;
    }

    public ServiceMovilCME() {
        super(__getWsdlLocation(), SERVICEMOVILCME_QNAME);
    }

    public ServiceMovilCME(WebServiceFeature... features) {
        super(__getWsdlLocation(), SERVICEMOVILCME_QNAME, features);
    }

    public ServiceMovilCME(URL wsdlLocation) {
        super(wsdlLocation, SERVICEMOVILCME_QNAME);
    }

    public ServiceMovilCME(URL wsdlLocation, WebServiceFeature... features) {
        super(wsdlLocation, SERVICEMOVILCME_QNAME, features);
    }

    public ServiceMovilCME(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public ServiceMovilCME(URL wsdlLocation, QName serviceName, WebServiceFeature... features) {
        super(wsdlLocation, serviceName, features);
    }

    /**
     * 
     * @return
     *     returns ServiceMovilCMESoap
     */
    @WebEndpoint(name = "ServiceMovilCMESoap")
    public ServiceMovilCMESoap getServiceMovilCMESoap() {
        return super.getPort(new QName("http://ws_NCanales_movil/", "ServiceMovilCMESoap"), ServiceMovilCMESoap.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns ServiceMovilCMESoap
     */
    @WebEndpoint(name = "ServiceMovilCMESoap")
    public ServiceMovilCMESoap getServiceMovilCMESoap(WebServiceFeature... features) {
        return super.getPort(new QName("http://ws_NCanales_movil/", "ServiceMovilCMESoap"), ServiceMovilCMESoap.class, features);
    }

    /**
     * 
     * @return
     *     returns ServiceMovilCMESoap
     */
    @WebEndpoint(name = "ServiceMovilCMESoap12")
    public ServiceMovilCMESoap getServiceMovilCMESoap12() {
        return super.getPort(new QName("http://ws_NCanales_movil/", "ServiceMovilCMESoap12"), ServiceMovilCMESoap.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns ServiceMovilCMESoap
     */
    @WebEndpoint(name = "ServiceMovilCMESoap12")
    public ServiceMovilCMESoap getServiceMovilCMESoap12(WebServiceFeature... features) {
        return super.getPort(new QName("http://ws_NCanales_movil/", "ServiceMovilCMESoap12"), ServiceMovilCMESoap.class, features);
    }

    private static URL __getWsdlLocation() {
        if (SERVICEMOVILCME_EXCEPTION!= null) {
            throw SERVICEMOVILCME_EXCEPTION;
        }
        return SERVICEMOVILCME_WSDL_LOCATION;
    }

}