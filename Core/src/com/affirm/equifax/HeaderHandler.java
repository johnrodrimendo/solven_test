package com.affirm.equifax;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPHeader;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Collections;
import java.util.Set;

/**
 * Created by stbn on 31/10/16.
 */
public class HeaderHandler implements SOAPHandler<SOAPMessageContext> {

    private static final String SOAP_ELEMENT_PASSWORD = "Password";
    private static final String SOAP_ELEMENT_USERNAME = "Username";
    private static final String SOAP_ELEMENT_USERNAME_TOKEN = "UsernameToken";
    private static final String SOAP_ELEMENT_SECURITY = "Security";
    private static final String NAMESPACE_SECURITY = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";
    private static final String PREFIX_SECURITY = "wsse";

    private String usernameText = System.getenv("EFX_USR");//"USERUAT";
    private String passwordText = System.getenv("EFX_PWD");//"peru2016";

    public HeaderHandler() {
    }

    public HeaderHandler(String usernameText, String passwordText) {
        this.usernameText = usernameText;
        this.passwordText = passwordText;
    }

    @Override
    public void close(MessageContext context) {
    }

    @Override
    public boolean handleFault(SOAPMessageContext context) {
        return true;
    }

    @Override
    public boolean handleMessage(SOAPMessageContext soapMessageContext) {
        Boolean outboundProperty = (Boolean) soapMessageContext.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

        System.out.println("\nOutbound message:" + outboundProperty);

        if (outboundProperty.booleanValue()) {

            try {
                SOAPEnvelope soapEnvelope = soapMessageContext.getMessage().getSOAPPart().getEnvelope();

                SOAPHeader header = soapEnvelope.getHeader();
                if (header == null) {
                    header = soapEnvelope.addHeader();
                }

                SOAPElement soapElementSecurityHeader = header.addChildElement(SOAP_ELEMENT_SECURITY, PREFIX_SECURITY,
                        NAMESPACE_SECURITY);

                SOAPElement soapElementUsernameToken = soapElementSecurityHeader
                        .addChildElement(SOAP_ELEMENT_USERNAME_TOKEN, PREFIX_SECURITY);
                SOAPElement soapElementUsername = soapElementUsernameToken.addChildElement(SOAP_ELEMENT_USERNAME,
                        PREFIX_SECURITY);
                soapElementUsername.addTextNode(this.usernameText);

                SOAPElement soapElementPassword = soapElementUsernameToken.addChildElement(SOAP_ELEMENT_PASSWORD,
                        PREFIX_SECURITY);
                soapElementPassword.addTextNode(this.passwordText);
                soapElementPassword.setAttribute("Type",
                        "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText");

                SOAPElement soapElementNonce = soapElementUsernameToken.addChildElement("Nonce", PREFIX_SECURITY);
                java.security.SecureRandom random = java.security.SecureRandom.getInstance("SHA1PRNG");
                random.setSeed(System.currentTimeMillis());
                byte[] nonceBytes = new byte[16];
                random.nextBytes(nonceBytes);
                String nonce = new String(Base64.getEncoder().encode(nonceBytes), "UTF-8");
                soapElementNonce.addTextNode(nonce);
                soapElementNonce.setAttribute("EncodingType",
                        "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-soap-message-security-1.0#Base64Binary");

                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                SOAPElement soapElementCreated = soapElementUsernameToken.addChildElement("Created", PREFIX_SECURITY);
                soapElementCreated.addTextNode(format.format(System.currentTimeMillis()));

            } catch (Exception e) {
                throw new RuntimeException("Error on wsSecurityHandler: " + e.getMessage());
            }

        }

        return true;
    }

    @Override
    public Set<QName> getHeaders() {
        return Collections.emptySet();
    }
}
