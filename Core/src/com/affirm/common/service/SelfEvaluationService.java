package com.affirm.common.service;

import com.affirm.common.model.catalog.ProcessQuestionsConfiguration;
import com.affirm.common.model.transactional.SelfEvaluation;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by john on 21/10/16.
 */
public interface SelfEvaluationService {
    String generateSelfEvaluationToken(int selfEvaluationId);

    Integer getIdFromToken(String token) throws Exception;

    ProcessQuestionsConfiguration getSelfEvaluationProcess(SelfEvaluation selfEvaluation, HttpServletRequest request) throws Exception;

    Integer getQuestionIdByResult(SelfEvaluation selfEvaluation, int questionId, String processQuestionResult, HttpServletRequest request) throws Exception;

    Integer forwardByResult(SelfEvaluation selfEvaluation, String processQuestionResult, HttpServletRequest request) throws Exception;

    Integer backward(SelfEvaluation selfEvaluation, HttpServletRequest request) throws Exception;

    String getCalificationLabel(Integer calification);

    void runSelfEvaluationArgentina(SelfEvaluation selfEvaluation) throws Exception;
}
