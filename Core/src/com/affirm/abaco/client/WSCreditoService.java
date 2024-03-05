
package com.affirm.abaco.client;

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

@WebServiceClient(name = "WSCreditoService", targetNamespace = "http://servicio.ws/", wsdlLocation = "xxxxxxxxxxxxxxxxxxxx")

public class WSCreditoService
    extends Service
{

    private final static URL WSCREDITOSERVICE_WSDL_LOCATION;
    private final static WebServiceException WSCREDITOSERVICE_EXCEPTION;
    private final static QName WSCREDITOSERVICE_QNAME = new QName("http://servicio.ws/", "WSCreditoService");

    static {
        URL url = null;
        WebServiceException e = null;
        try {
            //url = new URL("file:/home/dev5/solven/Core/src/com/affirm/abaco/client/WSCredito.wsdl");
            url = new URL(System.getenv("ABACO_URL"));
        } catch (MalformedURLException ex) {
            e = new WebServiceException(ex);
        }
        WSCREDITOSERVICE_WSDL_LOCATION = url;
        WSCREDITOSERVICE_EXCEPTION = e;
    }

    public WSCreditoService() {
        super(__getWsdlLocation(), WSCREDITOSERVICE_QNAME);
    }

    public WSCreditoService(WebServiceFeature... features) {
        super(__getWsdlLocation(), WSCREDITOSERVICE_QNAME, features);
    }

    public WSCreditoService(URL wsdlLocation) {
        super(wsdlLocation, WSCREDITOSERVICE_QNAME);
    }

    public WSCreditoService(URL wsdlLocation, WebServiceFeature... features) {
        super(wsdlLocation, WSCREDITOSERVICE_QNAME, features);
    }

    public WSCreditoService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public WSCreditoService(URL wsdlLocation, QName serviceName, WebServiceFeature... features) {
        super(wsdlLocation, serviceName, features);
    }

    /**
     * 
     * @return
     *     returns WSCredito
     */
    @WebEndpoint(name = "WSCreditoPort")
    public WSCredito getWSCreditoPort() {
        return super.getPort(new QName("http://servicio.ws/", "WSCreditoPort"), WSCredito.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns WSCredito
     */
    @WebEndpoint(name = "WSCreditoPort")
    public WSCredito getWSCreditoPort(WebServiceFeature... features) {
        return super.getPort(new QName("http://servicio.ws/", "WSCreditoPort"), WSCredito.class, features);
    }

    private static URL __getWsdlLocation() {
        if (WSCREDITOSERVICE_EXCEPTION!= null) {
            throw WSCREDITOSERVICE_EXCEPTION;
        }
        return WSCREDITOSERVICE_WSDL_LOCATION;
    }

}
