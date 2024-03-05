package com.affirm.api.endpoint.soap.errors;

import javax.xml.ws.WebFault;

@WebFault(name="CreditoFault")
public class CreditoException extends Exception {


    private CreditoFault fault;

    public CreditoException() {
        // TODO Auto-generated constructor stub
    }
    protected CreditoException(CreditoFault fault) {
        super(fault.getFaultString());
        this.fault = fault;
    }

    public CreditoException(String message, CreditoFault faultInfo){
        super(message);
        this.fault = faultInfo;
    }

    public CreditoException(String message, CreditoFault faultInfo, Throwable cause){
        super(message,cause);
        this.fault = faultInfo;
    }

    public CreditoFault getFaultInfo(){
        return fault;
    }

    public CreditoException(String message) {
        super(message);
    }

    public CreditoException(String code, String message) {
        super(message);
        this.fault = new CreditoFault();
        this.fault.setFaultString(message);
        this.fault.setFaultCode(code);
    }

    public CreditoException(Throwable cause) {
        super(cause);

    }

    public CreditoException(String message, Throwable cause) {
        super(message, cause);
    }

}