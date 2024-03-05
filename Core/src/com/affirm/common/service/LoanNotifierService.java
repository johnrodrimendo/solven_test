package com.affirm.common.service;

import com.affirm.common.model.transactional.Credit;
import com.affirm.common.model.transactional.LoanApplication;

import java.util.Locale;

public interface LoanNotifierService {
    void notifyDisbursement(int loanApplicationId, Locale locale) throws Exception;
    void notifyDisbursement(LoanApplication loanApplication);

    void notifyRejection(LoanApplication loanApplication, Credit credit);
}
