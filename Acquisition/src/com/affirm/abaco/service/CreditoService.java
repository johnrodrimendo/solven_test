package com.affirm.abaco.service;

import com.affirm.abaco.errors.CreditoException;
import com.affirm.abaco.model.CreditResponse;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

@WebService
@SOAPBinding(style = Style.RPC)
public interface CreditoService {


    @WebMethod(operationName = "confirmarOperacion")
    @WebResult(name="credito")
    public CreditResponse confirmarOperacion(@WebParam(name = "creditId") String creditId,
                                             @WebParam(name = "operationId") int operationId) throws CreditoException;

    @WebMethod(operationName = "confirmarOperacion2")
    @WebResult(name="credito")
    public CreditResponse confirmarOperacion2(@WebParam(name = "creditId") String creditId,
                                              @WebParam(name = "operationId") int operationId,
                                              @WebParam(name = "returningReasons") String returningReasons) throws CreditoException;

}