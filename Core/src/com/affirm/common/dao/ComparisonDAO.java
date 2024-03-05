package com.affirm.common.dao;

import com.affirm.common.model.transactional.Comparison;
import com.affirm.common.model.transactional.ComparisonResult;

import java.util.List;
import java.util.Locale;

/**
 * @author jrodriguez
 */
public interface ComparisonDAO {
    Comparison registerComparison(int formAssistant);

    void updateCurrentQuestion(int comparisonId, int questionId);

    void updateQuestionSequence(int comparisonId, String sequence);

    Comparison getComparison(int comparisonId, Locale locale);

    void updateActivityType(int comparisonId, Integer activityType);

    void updateComparisonReason(int comparisonId, Integer comparisonReasonId);

    void updateFixedGrosIncome(int comparisonId, Double fixedGrossIncome);

    void updateInstallments(int comparisonId, int installments);

    void updateAmount(int comparisonId, int amount);

    void updateEmail(int comparisonId, String email);

    void updatePassword(int comparisonId, String password);

    void updateNetworkToken(int comparisonId, Integer networkToken);

    void updateSelfEvaluationId(int comparisonId, int selfEvaluationId);

    List<ComparisonResult> executeComparison(int comparisonId, Locale locale);
}