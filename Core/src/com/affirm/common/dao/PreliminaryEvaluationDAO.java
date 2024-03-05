/**
 *
 */
package com.affirm.common.dao;

import com.affirm.common.model.transactional.LoanApplicationPreliminaryEvaluation;

import java.util.List;
import java.util.Locale;

/**
 * @author jrodriguez
 */
public interface PreliminaryEvaluationDAO {
    void updateStatus(Integer prelimimaryEvaluationId, Character status);

    void updateIsApproved(Integer prelimimaryEvaluationId, Boolean isApproved);

    void updateRunDefaultEvaluation(Integer prelimimaryEvaluationId, Boolean runDefaultEvaluation);

    List<LoanApplicationPreliminaryEvaluation> getPreliminaryEvaluationsWithHardFilters(int loanApplicationId, Locale locale) throws Exception;
}