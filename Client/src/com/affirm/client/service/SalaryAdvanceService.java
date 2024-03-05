package com.affirm.client.service;


import com.affirm.common.model.catalog.Employer;
import com.affirm.common.model.transactional.Employee;
import com.affirm.common.model.transactional.LoanApplication;

import java.util.Locale;

/**
 * @author jrodriguez
 */
public interface SalaryAdvanceService {

    LoanApplication registerLoanApplication(Employee employee, String clientIp, char origin, Locale locale, Integer registerType, int countryId) throws Exception;

    void sendConfirmationMail(int loanApplicationId, int userId, int personId, String email, Employer employer, Locale locale);

    void sendValidationMail(int loanApplicationId, int userId, int personId, Employer employer, Integer emailId, String email, Locale locale);
}
