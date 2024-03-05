package com.affirm.api.endpoint.soap.impl;

import com.affirm.api.endpoint.soap.ConfirmOperationEndpoint;
import com.affirm.api.endpoint.soap.errors.CreditoException;
import com.affirm.api.endpoint.soap.model.CreditResponse;
import com.affirm.api.endpoint.soap.service.SoapApiService;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.annotation.Resource;
import javax.jws.HandlerChain;
import javax.jws.WebService;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPMessageContext;

@Service
@WebService(endpointInterface = "com.affirm.api.endpoint.soap.ConfirmOperationEndpoint")
@HandlerChain(file = "com/affirm/api/endpoint/soap/handler-chain.xml")
public class ConfirmOperationEndpointImpl implements ConfirmOperationEndpoint {

    @Resource
    private WebServiceContext wsContext;

    @Override
    public CreditResponse confirmarOperacion(String creditId, int operationId, String disbursementDate) throws CreditoException {

        MessageContext msgCtxt = wsContext.getMessageContext();
        ServletContext servletContext = (ServletContext) msgCtxt.get("javax.xml.ws.servlet.context");
        WebApplicationContext webApplicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
        SoapApiService soapApiService = (SoapApiService) webApplicationContext.getAutowireCapableBeanFactory().getBean("soapApiService");

        HttpServletRequest request = (HttpServletRequest) msgCtxt.get(MessageContext.SERVLET_REQUEST);
        HttpServletResponse response = (HttpServletResponse) msgCtxt.get(SOAPMessageContext.SERVLET_RESPONSE);
        int entityId = Integer.valueOf(servletContext.getAttribute("entityId").toString());

        return soapApiService.confirmOperation(creditId, operationId, disbursementDate, entityId, wsContext, request, response);
    }
}
