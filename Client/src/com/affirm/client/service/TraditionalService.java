package com.affirm.client.service;


import com.affirm.common.model.transactional.LoanApplication;

import javax.servlet.http.HttpServletRequest;

/**
 * @author jrodriguez
 */
public interface TraditionalService {
    LoanApplication registerLoanApplication(HttpServletRequest request, int userId, int ammount, int installments, Integer loanReason, char origin, Integer cluster, Integer registerType) throws Exception;
}
