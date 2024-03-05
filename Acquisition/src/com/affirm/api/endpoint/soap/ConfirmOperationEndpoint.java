package com.affirm.api.endpoint.soap;

import com.affirm.api.endpoint.soap.errors.CreditoException;
import com.affirm.api.endpoint.soap.model.CreditResponse;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

@WebService
@SOAPBinding(style = Style.RPC)
public interface ConfirmOperationEndpoint {


    @WebMethod(operationName = "confirmarOperacion")
    @WebResult(name="credito")
    public CreditResponse confirmarOperacion(@WebParam(name = "creditId") String creditId,
                                             @WebParam(name = "operationId") int operationId,
                                             @WebParam(name = "disbursementDate") String disbursementDate) throws CreditoException;

//    @WebMethod(operationName = "confirmarOperacion2")
//    @WebResult(name="credito")
//    public CreditResponse confirmarOperacion2(@WebParam(name = "creditId") String creditId,
//                                              @WebParam(name = "operationId") int operationId,
//                                              @WebParam(name = "returningReasons") String returningReasons,
//                                              @WebParam(name = "disbursementDate") Date disbursementDate) throws CreditoException;

}