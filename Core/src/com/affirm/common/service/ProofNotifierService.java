package com.affirm.common.service;

import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.model.transactional.SelfEvaluation;

public interface ProofNotifierService {
    void notifySelfEvaluation(SelfEvaluation selfEvaluation) throws  Exception;
    void notifySelfEvaluation(Integer selfEvaluationId) throws Exception;
    void notifyCreditConsultation(LoanApplication loanApplication) throws Exception;
    void notifyCreditConsultation(Integer loanApplicationId) throws Exception;
}
