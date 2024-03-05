package com.affirm.common.dao;

import com.affirm.common.model.transactional.SelfEvaluation;

import java.util.Locale;

/**
 * @author jrodriguez
 */
public interface SelfEvaluationDAO {

    SelfEvaluation registerSelfEvaluation(int countryId);

    void updateCurrentQuestion(int selfEvaluationId, int questionId);

    SelfEvaluation getSelfEvaluation(int selfEvaluationId, Locale locale);

    void updateQuestionSequence(int selfEvaluationId, String sequence);

    void updatePerson(int selfEvaluationId, int personId);

    void updateInstallments(int selfEvaluationId, int installments);

    void updateAmount(int selfEvaluationId, int amount);

    void updateFixedGrossIncome(int selfEvaluationId, int fixedGrossIncome);

    void updateLoanReason(int selfEvaluationId, int loanReasonId);

    void updateUsage(int selfEvaluationId, int usageId);

    void updateDownPayment(int selfEvaluationId, int downPayment);

    void updateFormAssistant(int selfEvaluationId, int formAssistant);

    String runSelfEvaluation(int selfEvaluationId);

    void updateBots(int selfEvaluationId, Integer[] bots);

    SelfEvaluation getActiveSelfEvaluationByPerson(int personId, Locale locale);
}