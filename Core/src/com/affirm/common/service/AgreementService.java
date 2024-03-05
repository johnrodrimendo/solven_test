package com.affirm.common.service;


import com.affirm.abaco.client.ERptaCredito;
import com.affirm.common.model.transactional.Credit;
import com.affirm.common.model.transactional.Person;
import org.thymeleaf.spring4.SpringTemplateEngine;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

/**
 * @author jrodriguez
 */
public interface AgreementService {

//    LoanApplication registerLoanApplication(Employee employee, String clientIp, char origin, Locale locale, Integer registerType, int loanApplicationReasonId, HttpServletRequest request) throws Exception;

    void confirmDisbursement(int creditId, HttpServletRequest request, HttpServletResponse response, Locale locale, SpringTemplateEngine templateEngine) throws Exception;

    void createAssociatedCredit(Person person, Credit credit) throws Exception;

    ERptaCredito getAsociatedByEmployeeEntity(int docType, String docNumber, int entityId, Integer loanApplicationId) throws Exception;

    void sendAssociatedToEntity(int personId, int entityId, Locale locale, Integer loanApplicationId) throws Exception;
}