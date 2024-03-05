package com.affirm.common.service;

import com.affirm.common.model.catalog.ProcessQuestionsConfiguration;
import com.affirm.common.model.transactional.Comparison;
import com.affirm.common.model.transactional.ComparisonResult;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Locale;

/**
 * Created by john on 21/10/16.
 */
public interface ComparisonService {
    String generateComparisonToken(int comparisonId);

    Integer getIdFromToken(String token) throws Exception;

    ProcessQuestionsConfiguration getComparisonProcess(Comparison comparison) throws Exception;

    Integer getQuestionIdByResult(Comparison comparison, int questionId, String processQuestionResult) throws Exception;

    Integer forwardByResult(Comparison comparison, String processQuestionResult, HttpServletRequest request) throws Exception;

    Integer backward(Comparison comparison, HttpServletRequest request) throws Exception;

    List<ComparisonResult> executeComparison(Comparison comparison, Locale locale, int comparisonRateType, Integer selfEvaluaionScore, int offset) throws Exception;

    void setComparisonResultPayments(Double amount, int installment, Double tea, Double desgravamen, Double desgravamenPrimaUnica, ComparisonResult comparisonResult) throws Exception;
}
