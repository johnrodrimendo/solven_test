package com.affirm.api.endpoint.soap.handler;

import com.affirm.common.dao.RestApiDAO;
import com.affirm.common.model.transactional.WsClient;
import com.affirm.system.configuration.Configuration;
import com.sun.xml.ws.developer.JAXWSProperties;
import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;
import javax.xml.soap.*;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import javax.xml.ws.soap.SOAPFaultException;
import java.net.URLEncoder;
import java.time.Instant;
import java.util.Set;

/**
 * Created by miberico on 17/04/17.
 */
@Component
public class AuthHandler implements SOAPHandler<SOAPMessageContext> {

    private static Logger logger = Logger.getLogger(AuthHandler.class);

    @Override
    public boolean handleMessage(SOAPMessageContext context) {
        System.out.println("AuthInterceptor");

        HttpServletRequest request = (HttpServletRequest) context.get(MessageContext.SERVLET_REQUEST);
        SOAPMessage soapMsg = context.getMessage();

        ServletContext servletContext = (ServletContext) context.get("javax.xml.ws.servlet.context");
        WebApplicationContext webApplicationContext = WebApplicationContextUtils
                .getRequiredWebApplicationContext(servletContext);

        RestApiDAO restApiDao = (RestApiDAO) webApplicationContext
                .getAutowireCapableBeanFactory().getBean("restApiDAO");

        MessageSource messageSource = (MessageSource) webApplicationContext
                .getAutowireCapableBeanFactory().getBean("messageSource");

        Boolean outBoundProperty = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

        // if this is an incoming message from the client
        if (!outBoundProperty) {
            try {

                if (Configuration.hostEnvIsNotLocal() && !((String) context.get(JAXWSProperties.HTTP_REQUEST_URL)).toLowerCase().contains("https")) {
                    logger.debug("Is not https");
                    generateSOAPErrMessage(soapMsg, HttpServletResponse.SC_UNAUTHORIZED + "", messageSource.getMessage("servicio.abaco.noautorizado", null, Configuration.getDefaultLocale()));
                    return false;
                }

                // Get the signature
                String authHeader = request.getHeader("Authorization");
                logger.debug("Auth header: " + authHeader);
                if (authHeader == null) {
                    logger.debug("Auth header is null");
                    generateSOAPErrMessage(soapMsg, HttpServletResponse.SC_UNAUTHORIZED + "", messageSource.getMessage("servicio.abaco.noautorizado", null, Configuration.getDefaultLocale()));
                    return false;
                }

                if (authHeader.split("=").length != 3) {
                    logger.debug("Auth header size is not 3 ");
                    generateSOAPErrMessage(soapMsg, HttpServletResponse.SC_UNAUTHORIZED + "", messageSource.getMessage("servicio.abaco.noautorizado", null, Configuration.getDefaultLocale()));
                    return false;
                }

                String apiKey = authHeader.split("=")[0];
                String requestSignature = authHeader.split("=")[1];
                long requestUnixTime = Long.parseLong(authHeader.split("=")[2]);
                long actualUnixTime = Instant.now().getEpochSecond();

                // Validate that is only 1 min of diference between the request and now
                if (Math.abs(actualUnixTime - requestUnixTime) > 120) {
                    logger.debug("Auth header time is not valid");
                    generateSOAPErrMessage(soapMsg, HttpServletResponse.SC_UNAUTHORIZED + "", messageSource.getMessage("servicio.abaco.noautorizado", null, Configuration.getDefaultLocale()));
                    return false;
                }

                WsClient wsClient = restApiDao.getWsClientByApiKey(apiKey);

                if (wsClient == null || !wsClient.getActive() || !wsClient.getApiKeyActive()) {
                    logger.debug("The ws client is not ative");
                    generateSOAPErrMessage(soapMsg, HttpServletResponse.SC_UNAUTHORIZED + "", messageSource.getMessage("servicio.abaco.noautorizado", null, Configuration.getDefaultLocale()));
                    return false;
                }

                // Validate the signature
                String signature = encodeSignature(wsClient.getApiKeySecret(), wsClient.getApiKeySharedKey() + requestUnixTime + request.getRequestURI());
                if (!signature.equals(requestSignature)) {
                    logger.debug("Signatures doesnt match");
                    generateSOAPErrMessage(soapMsg, HttpServletResponse.SC_UNAUTHORIZED + "", messageSource.getMessage("servicio.abaco.noautorizado", null, Configuration.getDefaultLocale()));
                    return false;
                }

                servletContext.setAttribute("entityId", restApiDao.getEntityByClientId(wsClient.getId()));

            } catch (SOAPException e) {
                generateSOAPErrMessage(soapMsg, "-2", messageSource.getMessage("servicio.abaco.error", null, Configuration.getDefaultLocale()));
                return false;
            } catch (SOAPFaultException e) {
                return false;
            } catch (Exception e) {
                e.printStackTrace();
                generateSOAPErrMessage(soapMsg, "-2", messageSource.getMessage("generic.abaco.error", null, Configuration.getDefaultLocale()));
                return false;
            }
        }
        return true;
    }

    private String encodeSignature(String secret, String data) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        return URLEncoder.encode(Base64.encodeBase64String(sha256_HMAC.doFinal(data.getBytes("UTF-8"))), "UTF-8");
    }

    @Override
    public boolean handleFault(SOAPMessageContext context) {
        return true;
    }

    @Override
    public void close(MessageContext context) {
    }

    @Override
    public Set<QName> getHeaders() {
        return null;
    }

    private void generateSOAPErrMessage(SOAPMessage msg, String codeReason, String reason) {
        try {
            SOAPBody soapBody = msg.getSOAPPart().getEnvelope().getBody();
            soapBody.removeContents();
            SOAPFault soapFault = soapBody.addFault();
            QName faultName = new QName(SOAPConstants.URI_NS_SOAP_ENVELOPE, "Server");
            soapFault.setFaultCode(faultName);
            soapFault.setFaultString(reason);
            Detail detail = soapFault.addDetail();
            QName entryName = new QName("http://service.abaco.affirm.com/", "CreditoFault", "ns2");
            DetailEntry entry = detail.addDetailEntry(entryName);
            SOAPElement faulCodeTag = entry.addChildElement("faultCode");
            faulCodeTag.addTextNode(codeReason);
            SOAPElement faulStringTag = entry.addChildElement("faultString");
            faulStringTag.addTextNode(reason);
            throw new SOAPFaultException(soapFault);
        } catch (SOAPException e) {
        }
    }


}
