package com.affirm.abaco.service.impl;

import com.affirm.abaco.errors.CreditoException;
import com.affirm.abaco.model.CreditResponse;
import com.affirm.abaco.service.CreditoService;
import com.affirm.common.dao.CreditDAO;
import com.affirm.common.model.catalog.CreditStatus;
import com.affirm.common.model.transactional.Credit;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.service.AgreementService;
import com.affirm.common.service.FileService;
import com.affirm.common.service.LoanNotifierService;
import com.affirm.common.service.OfflineConversionService;
import com.affirm.common.service.impl.ErrorServiceImpl;
import com.affirm.system.configuration.Configuration;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.thymeleaf.spring4.SpringTemplateEngine;

import javax.annotation.Resource;
import javax.jws.HandlerChain;
import javax.jws.WebService;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.util.Date;

@Service
@WebService(endpointInterface = "com.affirm.abaco.service.CreditoService")
@HandlerChain(file = "com/affirm/api/endpoint/soap/handler-chain.xml")
public class CreditoServiceImpl extends SpringBeanAutowiringSupport implements CreditoService {

    @Resource
    private WebServiceContext wsContext;

    @Override
    public CreditResponse confirmarOperacion(String creditId, int operationId) throws CreditoException {
        CreditDAO creditDAO;
        AgreementService agreementService;
        FileService fileService;

        ServletContext servletContext = (ServletContext) wsContext
                .getMessageContext().get("javax.xml.ws.servlet.context");
        WebApplicationContext webApplicationContext = WebApplicationContextUtils
                .getRequiredWebApplicationContext(servletContext);

        fileService = (FileService) webApplicationContext
                .getAutowireCapableBeanFactory().getBean("fileService");

        creditDAO = (CreditDAO) webApplicationContext
                .getAutowireCapableBeanFactory().getBean("creditDAO");

        agreementService = (AgreementService) webApplicationContext
                .getAutowireCapableBeanFactory().getBean("agreementService");

        MessageSource messageSource = (MessageSource) webApplicationContext
                .getAutowireCapableBeanFactory().getBean("messageSource");

        SpringTemplateEngine templateEngine = (SpringTemplateEngine) webApplicationContext
                .getAutowireCapableBeanFactory().getBean("templateEngine");

        LoanNotifierService loanNotifierService = (LoanNotifierService) webApplicationContext
                .getAutowireCapableBeanFactory().getBean("loanNotifierService");

        OfflineConversionService offlineConversionService = (OfflineConversionService) webApplicationContext
                .getAutowireCapableBeanFactory().getBean("offlineConversionService");

        if (creditId == null || creditId.isEmpty()) {
            throw new CreditoException("-1", messageSource.getMessage("servicio.abaco.creditoerror", null, Configuration.getDefaultLocale()));
        }

        if (operationId <= 0) {
            throw new CreditoException("-1", messageSource.getMessage("servicio.abaco.operacionerror", null, Configuration.getDefaultLocale()));
        }

        CreditResponse responseService = new CreditResponse();
        boolean isOperationValid = true;
        MessageContext msgCtxt = wsContext.getMessageContext();

        int entityId = Integer.valueOf(servletContext.getAttribute("entityId").toString());
        try {
            Integer solvenCreditId = creditDAO.getCreditByCreditCode(creditId, entityId);
            if (solvenCreditId == null)
                throw new CreditoException("-1", messageSource.getMessage("servicio.abaco.nocreditoerror", null, Configuration.getDefaultLocale()));
            Integer loanApplicationId = creditDAO.getLoanApplicationIdByCreditCode(creditId, entityId);


            msgCtxt.put("loanApplicationId", loanApplicationId);

            if (operationId == 1) {
                creditDAO.updateCreditStatus(solvenCreditId, CreditStatus.ORIGINATED, entityId);
            } else if (operationId == 2) {
                HttpServletRequest request = (HttpServletRequest) msgCtxt.get(MessageContext.SERVLET_REQUEST);
                ServletWebRequest servletWebRequest = new ServletWebRequest(request);
                HttpServletResponse response = (HttpServletResponse) msgCtxt.get(SOAPMessageContext.SERVLET_RESPONSE);
                creditDAO.updateCreditStatus(solvenCreditId, CreditStatus.ORIGINATED_DISBURSED, entityId);
                agreementService.confirmDisbursement(solvenCreditId, request, response, Configuration.getDefaultLocale(), templateEngine);
                creditDAO.updateDisbursementDate(solvenCreditId, new Date());

                Credit credito = creditDAO.getCreditByID(solvenCreditId, Configuration.getDefaultLocale(), false, Credit.class);
                responseService.setLoanInfoURL(fileService.generatePublicUserFileUrl(credito.getContractUserFileId().get(0), false));

                loanNotifierService.notifyDisbursement(credito.getLoanApplicationId(), Configuration.getDefaultLocale());
                offlineConversionService.sendOfflineConversion(credito);
            } else {
                isOperationValid = false;
            }
            responseService.setReceptionId(1);
        } catch (CreditoException e) {
            System.out.println(e.toString());
            throw new CreditoException("-1", messageSource.getMessage("servicio.abaco.nocreditoerror", null, Configuration.getDefaultLocale()));
        } catch (Exception e) {
            ErrorServiceImpl.onErrorStatic(e);
            throw new CreditoException("-2", messageSource.getMessage("servicio.abaco.error", null, Configuration.getDefaultLocale()));
        }
        if (!isOperationValid)
            throw new CreditoException("-1", messageSource.getMessage("servicio.abaco.nocreditoerror", null, Configuration.getDefaultLocale()));
        return responseService;
    }

    @Override
    public CreditResponse confirmarOperacion2(String creditId, int operationId, String returningReasons) throws CreditoException {
        ServletContext servletContext = (ServletContext) wsContext
                .getMessageContext().get("javax.xml.ws.servlet.context");
        WebApplicationContext webApplicationContext = WebApplicationContextUtils
                .getRequiredWebApplicationContext(servletContext);
        MessageSource messageSource = (MessageSource) webApplicationContext
                .getAutowireCapableBeanFactory().getBean("messageSource");

        boolean isOperationValid = true;
        if (operationId == 1 || operationId == 2) {
            return confirmarOperacion(creditId, operationId);
        }else if(operationId == 3){
            return confirmarOperacion(creditId, operationId);
        }else{
            isOperationValid = false;
        }

        CreditResponse responseService = new CreditResponse();
        if (!isOperationValid)
            throw new CreditoException("-1", messageSource.getMessage("servicio.abaco.nocreditoerror", null, Configuration.getDefaultLocale()));
        return responseService;


    }

}