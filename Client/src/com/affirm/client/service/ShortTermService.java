package com.affirm.client.service;


import com.affirm.common.model.transactional.LoanApplication;

import javax.servlet.http.HttpServletRequest;

/**
 * @author jrodriguez
 */
public interface ShortTermService {
    LoanApplication registerLoanApplication(HttpServletRequest request, int userId, int ammount, int installments, char origin, Integer cluster, Integer registerType) throws Exception;
}
