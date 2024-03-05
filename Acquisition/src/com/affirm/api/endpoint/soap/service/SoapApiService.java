package com.affirm.api.endpoint.soap.service;

import com.affirm.api.endpoint.soap.errors.CreditoException;
import com.affirm.api.endpoint.soap.model.CreditResponse;
import com.affirm.common.dao.CreditDAO;
import com.affirm.common.model.transactional.Credit;
import com.affirm.common.service.CreditService;
import com.affirm.common.service.FileService;
import com.affirm.common.service.impl.ErrorServiceImpl;
import com.affirm.system.configuration.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring4.SpringTemplateEngine;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Service("soapApiService")
public class SoapApiService {

    @Autowired
    private MessageSource messageSource;
    @Autowired
    private CreditDAO creditDao;
    @Autowired
    private CreditService creditService;
    @Autowired
    private SpringTemplateEngine templateEngine;
    @Autowired
    private FileService fileService;

    public CreditResponse confirmOperation(String entityCreditCode, int operationId, String disbursementDate, Integer entityId, WebServiceContext wsContext, HttpServletRequest request, HttpServletResponse response) throws CreditoException{

        CreditResponse responseService = new CreditResponse();
        try {

            if (entityCreditCode == null || entityCreditCode.isEmpty())
                throw new CreditoException("-1", messageSource.getMessage("servicio.abaco.creditoerror", null, Configuration.getDefaultLocale()));
            if (operationId <= 0)
                throw new CreditoException("-1", messageSource.getMessage("servicio.abaco.operacionerror", null, Configuration.getDefaultLocale()));

            Integer solvenCreditId = creditDao.getCreditByCreditCode(entityCreditCode, entityId);
            if (solvenCreditId == null) {
                throw new CreditoException("-1", messageSource.getMessage("servicio.abaco.nocreditoerror", null, Configuration.getDefaultLocale()));
            }

            Integer loanApplicationId = creditDao.getLoanApplicationIdByCreditCode(entityCreditCode, entityId);
            MessageContext msgCtxt = wsContext.getMessageContext();
            msgCtxt.put("loanApplicationId", loanApplicationId); // Used by the logger

            Credit credito = creditDao.getCreditByID(solvenCreditId, Configuration.getDefaultLocale(), false, Credit.class);
            if (operationId == 1) {

                if (disbursementDate == null) {
                    throw new CreditoException("-3", messageSource.getMessage("servicio.abaco.disbursementdateerror", null, Configuration.getDefaultLocale()));
                }

                Date disbursementDateParsed = null;
                try {
                    disbursementDateParsed = new SimpleDateFormat("dd/MM/yyyy").parse(disbursementDate);
                } catch (ParseException parseException) {
                    throw new CreditoException("-3", messageSource.getMessage("servicio.abaco.disbursementdateerror", null, Configuration.getDefaultLocale()));
                }

                Calendar registeredDateCal = Calendar.getInstance();
                registeredDateCal.setTime(credito.getRegisterDate());
                registeredDateCal.set(Calendar.HOUR_OF_DAY, 0);
                registeredDateCal.set(Calendar.MINUTE, 0);
                registeredDateCal.set(Calendar.SECOND, 0);
                registeredDateCal.set(Calendar.MILLISECOND, 0);


                if (disbursementDateParsed.compareTo(registeredDateCal.getTime()) >= 0 && disbursementDateParsed.compareTo(new Date()) <= 0) {
                    creditService.originateCreditByEntity(credito.getId(), entityId, disbursementDateParsed, templateEngine, Configuration.getDefaultLocale(), request, response);
                } else {
                    throw new CreditoException("-3", messageSource.getMessage("servicio.abaco.disbursementdateerror", null, Configuration.getDefaultLocale()));
                }

            } else if (operationId == 2) {
                creditService.disburseCreditByEntity(credito.getId(), entityId, request, response, templateEngine, Configuration.getDefaultLocale());
                credito = creditDao.getCreditByID(solvenCreditId, Configuration.getDefaultLocale(), false, Credit.class);
                responseService.setLoanInfoURL(fileService.generatePublicUserFileUrl(credito.getContractUserFileId().get(0), false));
            } else {
                throw new CreditoException("-1", messageSource.getMessage("servicio.abaco.nocreditoerror", null, Configuration.getDefaultLocale()));
            }

            responseService.setReceptionId(1);

        } catch (CreditoException e) {
            throw new CreditoException(e.getFaultInfo().getFaultCode(), e.getMessage());
        } catch (Exception e) {
            ErrorServiceImpl.onErrorStatic(e);
            throw new CreditoException("-2", messageSource.getMessage("servicio.abaco.error", null, Configuration.getDefaultLocale()));
        }

        return responseService;
    }

}
