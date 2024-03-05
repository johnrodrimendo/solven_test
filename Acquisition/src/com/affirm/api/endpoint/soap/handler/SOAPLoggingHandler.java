package com.affirm.api.endpoint.soap.handler;

import com.affirm.common.dao.CreditDAO;
import com.affirm.common.dao.ServiceLogDAO;
import org.json.JSONObject;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.servlet.ServletContext;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.util.Set;

public class SOAPLoggingHandler implements SOAPHandler<SOAPMessageContext>{

    private Document loadXMLString(String response) throws Exception {
        DocumentBuilderFactory dbf =DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(response));
        return db.parse(is);
    }

    private String getCreditIdFromRequest(String response, String tagName) throws Exception {
        Document xmlDoc = loadXMLString(response);
        NodeList nodeList = xmlDoc.getElementsByTagName(tagName);
        return nodeList.item(0).getFirstChild().getNodeValue();
    }

    private void registerMessage(SOAPMessageContext context, int status) throws Exception{
        Boolean isRequest = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
        SOAPMessage soapMsg = context.getMessage();

        ServletContext servletContext = (ServletContext) context.get("javax.xml.ws.servlet.context");
        WebApplicationContext webApplicationContext = WebApplicationContextUtils
                .getRequiredWebApplicationContext(servletContext);

        ServiceLogDAO serviceLogDAO = (ServiceLogDAO) webApplicationContext
                .getAutowireCapableBeanFactory().getBean("serviceLogDAO");

        CreditDAO creditDAO = (CreditDAO) webApplicationContext
                .getAutowireCapableBeanFactory().getBean("creditDAO");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        soapMsg.writeTo(out);
        String strMsg = new String(out.toByteArray());

        JSONObject jsonResponse = serviceLogDAO.getService(((QName)context.get(MessageContext.WSDL_OPERATION)).getLocalPart());

        if(!isRequest){
            Integer loanApplicationId = creditDAO.getLoanApplicationIdByCreditCode(getCreditIdFromRequest(strMsg, "creditId"), jsonResponse.getInt("entity_id"));
            Integer logId = serviceLogDAO.registerServiceCall(jsonResponse.getInt("entity_id"), loanApplicationId, strMsg, null, status, jsonResponse.getInt("service_id"), jsonResponse.getInt("operation_id"));
            context.put("logId", logId);
        }else{
            if(context.get("loanApplicationId") != null)
                serviceLogDAO.updateServiceCall(Integer.parseInt(context.get("logId").toString()), jsonResponse.getInt("entity_id"), Integer.parseInt(context.get("loanApplicationId").toString()), strMsg, status, jsonResponse.getInt("service_id"), jsonResponse.getInt("operation_id"));
        }
    }

    public boolean handleMessage(SOAPMessageContext context) {
        System.out.println("SOAP Handler");
        try {
            registerMessage(context, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean handleFault(SOAPMessageContext context) {
        System.out.println("SOAP Handler Fault");
        try {
            registerMessage(context, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public void close(MessageContext context) {
    }

    @Override
    public Set<QName> getHeaders() {
        return null;
    }

}