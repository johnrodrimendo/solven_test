
package com.affirm.equifax.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.9-b130926.1035
 * Generated source version: 2.2
 * 
 */
@WebService(name = "Endpoint", targetNamespace = "http://ws.creditreport.equifax.com.pe/endpoint")
@XmlSeeAlso({
    ObjectFactory.class
})
public interface Endpoint {


    /**
     * 
     * @param consultaHistorica
     * @return
     *     returns com.affirm.equifax.ws.ReporteCrediticio
     * @throws EquifaxInterfaceException_Exception
     */
    @WebMethod(operationName = "GetReporteHistorico")
    @WebResult(name = "ReporteCrediticio", targetNamespace = "http://ws.creditreport.equifax.com.pe/document")
    @RequestWrapper(localName = "GetReporteHistorico", targetNamespace = "http://ws.creditreport.equifax.com.pe/endpoint", className = "com.affirm.equifax.ws.GetReporteHistorico")
    @ResponseWrapper(localName = "GetReporteHistoricoResponse", targetNamespace = "http://ws.creditreport.equifax.com.pe/endpoint", className = "com.affirm.equifax.ws.GetReporteHistoricoResponse")
    public ReporteCrediticio getReporteHistorico(
        @WebParam(name = "ConsultaHistorica", targetNamespace = "http://ws.creditreport.equifax.com.pe/document")
        HistoricType consultaHistorica)
        throws EquifaxInterfaceException_Exception
    ;

    /**
     * 
     * @param datosConsulta
     * @return
     *     returns com.affirm.equifax.ws.ReporteCrediticio
     * @throws EquifaxInterfaceException_Exception
     */
    @WebMethod(operationName = "GetReporteOnline")
    @WebResult(name = "ReporteCrediticio", targetNamespace = "http://ws.creditreport.equifax.com.pe/document")
    @RequestWrapper(localName = "GetReporteOnline", targetNamespace = "http://ws.creditreport.equifax.com.pe/endpoint", className = "com.affirm.equifax.ws.GetReporteOnline")
    @ResponseWrapper(localName = "GetReporteOnlineResponse", targetNamespace = "http://ws.creditreport.equifax.com.pe/endpoint", className = "com.affirm.equifax.ws.GetReporteOnlineResponse")
    public ReporteCrediticio getReporteOnline(
        @WebParam(name = "DatosConsulta", targetNamespace = "http://ws.creditreport.equifax.com.pe/document")
        QueryDataType datosConsulta)
        throws EquifaxInterfaceException_Exception
    ;

}
