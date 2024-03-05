
package com.affirm.abaco.client;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.affirm.abaco.client package. 
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

    private final static QName _CrearCredito_QNAME = new QName("http://servicio.ws/", "crearCredito");
    private final static QName _CreditoFault_QNAME = new QName("http://servicio.ws/", "CreditoFault");
    private final static QName _CrearSocio_QNAME = new QName("http://servicio.ws/", "crearSocio");
    private final static QName _EsSocio_QNAME = new QName("http://servicio.ws/", "esSocio");
    private final static QName _RptaDeudaSocio_QNAME = new QName("http://servicio.ws/", "rptaDeudaSocio");
    private final static QName _Socio_QNAME = new QName("http://servicio.ws/", "socio");
    private final static QName _EsSocioResponse_QNAME = new QName("http://servicio.ws/", "esSocioResponse");
    private final static QName _Persona_QNAME = new QName("http://servicio.ws/", "persona");
    private final static QName _CrearCreditoResponse_QNAME = new QName("http://servicio.ws/", "crearCreditoResponse");
    private final static QName _ActualizarSocio_QNAME = new QName("http://servicio.ws/", "actualizarSocio");
    private final static QName _Credito_QNAME = new QName("http://servicio.ws/", "credito");
    private final static QName _RptaSocio_QNAME = new QName("http://servicio.ws/", "rptaSocio");
    private final static QName _ActualizarSocioResponse_QNAME = new QName("http://servicio.ws/", "actualizarSocioResponse");
    private final static QName _CrearSocioResponse_QNAME = new QName("http://servicio.ws/", "crearSocioResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.affirm.abaco.client
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ECredito }
     * 
     */
    public ECredito createECredito() {
        return new ECredito();
    }

    /**
     * Create an instance of {@link EsSocioResponse }
     * 
     */
    public EsSocioResponse createEsSocioResponse() {
        return new EsSocioResponse();
    }

    /**
     * Create an instance of {@link EPersona }
     * 
     */
    public EPersona createEPersona() {
        return new EPersona();
    }

    /**
     * Create an instance of {@link ERptaCredito }
     * 
     */
    public ERptaCredito createERptaCredito() {
        return new ERptaCredito();
    }

    /**
     * Create an instance of {@link ESocio }
     * 
     */
    public ESocio createESocio() {
        return new ESocio();
    }

    /**
     * Create an instance of {@link ECreditoFault }
     * 
     */
    public ECreditoFault createECreditoFault() {
        return new ECreditoFault();
    }

    /**
     * Create an instance of {@link CrearSocio }
     * 
     */
    public CrearSocio createCrearSocio() {
        return new CrearSocio();
    }

    /**
     * Create an instance of {@link EsSocio }
     * 
     */
    public EsSocio createEsSocio() {
        return new EsSocio();
    }

    /**
     * Create an instance of {@link CrearCredito }
     * 
     */
    public CrearCredito createCrearCredito() {
        return new CrearCredito();
    }

    /**
     * Create an instance of {@link CrearSocioResponse }
     * 
     */
    public CrearSocioResponse createCrearSocioResponse() {
        return new CrearSocioResponse();
    }

    /**
     * Create an instance of {@link ActualizarSocioResponse }
     * 
     */
    public ActualizarSocioResponse createActualizarSocioResponse() {
        return new ActualizarSocioResponse();
    }

    /**
     * Create an instance of {@link EMensaje }
     * 
     */
    public EMensaje createEMensaje() {
        return new EMensaje();
    }

    /**
     * Create an instance of {@link ActualizarSocio }
     * 
     */
    public ActualizarSocio createActualizarSocio() {
        return new ActualizarSocio();
    }

    /**
     * Create an instance of {@link CrearCreditoResponse }
     * 
     */
    public CrearCreditoResponse createCrearCreditoResponse() {
        return new CrearCreditoResponse();
    }

    /**
     * Create an instance of {@link ECuota }
     * 
     */
    public ECuota createECuota() {
        return new ECuota();
    }

    /**
     * Create an instance of {@link ECredito.Cuotas }
     * 
     */
    public ECredito.Cuotas createECreditoCuotas() {
        return new ECredito.Cuotas();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CrearCredito }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://servicio.ws/", name = "crearCredito")
    public JAXBElement<CrearCredito> createCrearCredito(CrearCredito value) {
        return new JAXBElement<CrearCredito>(_CrearCredito_QNAME, CrearCredito.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ECreditoFault }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://servicio.ws/", name = "CreditoFault")
    public JAXBElement<ECreditoFault> createCreditoFault(ECreditoFault value) {
        return new JAXBElement<ECreditoFault>(_CreditoFault_QNAME, ECreditoFault.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CrearSocio }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://servicio.ws/", name = "crearSocio")
    public JAXBElement<CrearSocio> createCrearSocio(CrearSocio value) {
        return new JAXBElement<CrearSocio>(_CrearSocio_QNAME, CrearSocio.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EsSocio }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://servicio.ws/", name = "esSocio")
    public JAXBElement<EsSocio> createEsSocio(EsSocio value) {
        return new JAXBElement<EsSocio>(_EsSocio_QNAME, EsSocio.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ERptaCredito }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://servicio.ws/", name = "rptaDeudaSocio")
    public JAXBElement<ERptaCredito> createRptaDeudaSocio(ERptaCredito value) {
        return new JAXBElement<ERptaCredito>(_RptaDeudaSocio_QNAME, ERptaCredito.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ESocio }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://servicio.ws/", name = "socio")
    public JAXBElement<ESocio> createSocio(ESocio value) {
        return new JAXBElement<ESocio>(_Socio_QNAME, ESocio.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EsSocioResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://servicio.ws/", name = "esSocioResponse")
    public JAXBElement<EsSocioResponse> createEsSocioResponse(EsSocioResponse value) {
        return new JAXBElement<EsSocioResponse>(_EsSocioResponse_QNAME, EsSocioResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EPersona }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://servicio.ws/", name = "persona")
    public JAXBElement<EPersona> createPersona(EPersona value) {
        return new JAXBElement<EPersona>(_Persona_QNAME, EPersona.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CrearCreditoResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://servicio.ws/", name = "crearCreditoResponse")
    public JAXBElement<CrearCreditoResponse> createCrearCreditoResponse(CrearCreditoResponse value) {
        return new JAXBElement<CrearCreditoResponse>(_CrearCreditoResponse_QNAME, CrearCreditoResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ActualizarSocio }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://servicio.ws/", name = "actualizarSocio")
    public JAXBElement<ActualizarSocio> createActualizarSocio(ActualizarSocio value) {
        return new JAXBElement<ActualizarSocio>(_ActualizarSocio_QNAME, ActualizarSocio.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ECredito }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://servicio.ws/", name = "credito")
    public JAXBElement<ECredito> createCredito(ECredito value) {
        return new JAXBElement<ECredito>(_Credito_QNAME, ECredito.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EMensaje }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://servicio.ws/", name = "rptaSocio")
    public JAXBElement<EMensaje> createRptaSocio(EMensaje value) {
        return new JAXBElement<EMensaje>(_RptaSocio_QNAME, EMensaje.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ActualizarSocioResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://servicio.ws/", name = "actualizarSocioResponse")
    public JAXBElement<ActualizarSocioResponse> createActualizarSocioResponse(ActualizarSocioResponse value) {
        return new JAXBElement<ActualizarSocioResponse>(_ActualizarSocioResponse_QNAME, ActualizarSocioResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CrearSocioResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://servicio.ws/", name = "crearSocioResponse")
    public JAXBElement<CrearSocioResponse> createCrearSocioResponse(CrearSocioResponse value) {
        return new JAXBElement<CrearSocioResponse>(_CrearSocioResponse_QNAME, CrearSocioResponse.class, null, value);
    }

}
