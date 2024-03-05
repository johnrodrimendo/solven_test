package com.affirm.common.util;

import com.affirm.common.service.impl.ErrorServiceImpl;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.io.ByteArrayOutputStream;
import java.util.Set;

public class SOAPLogRetainerHandler implements SOAPHandler<SOAPMessageContext> {

    private String requestLog;
    private String responseLog;

    @Override
    public Set<QName> getHeaders() {
        return null;
    }

    private void saveLog(SOAPMessageContext context) {
        boolean isRequest = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
        SOAPMessage soapMsg = context.getMessage();

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            soapMsg.writeTo(out);
            if (isRequest)
                requestLog = new String(out.toByteArray());
            else
                responseLog = new String(out.toByteArray());
        } catch (Exception e) {
            ErrorServiceImpl.onErrorStatic(e);
        }
    }

    @Override
    public boolean handleMessage(SOAPMessageContext context) {
        saveLog(context);
        return true;
    }

    @Override
    public boolean handleFault(SOAPMessageContext context) {
        saveLog(context);
        return true;
    }

    @Override
    public void close(MessageContext context) {
    }

    public String getRequestLog() {
        return requestLog;
    }

    public void setRequestLog(String requestLog) {
        this.requestLog = requestLog;
    }

    public String getResponseLog() {
        return responseLog;
    }

    public void setResponseLog(String responseLog) {
        this.responseLog = responseLog;
    }
}
