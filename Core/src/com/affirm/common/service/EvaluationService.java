package com.affirm.common.service;

import com.affirm.common.model.catalog.ProcessQuestion;
import com.affirm.common.model.catalog.ProcessQuestionsConfiguration;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.model.transactional.ProcessQuestionSequence;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by john on 21/10/16.
 */
public interface EvaluationService {

    String generateEvaluationToken(int userId, int personId, int loanApplicationId);

    Integer getIdFromToken(String token) throws Exception;

    ProcessQuestionsConfiguration getEvaluationProcessByProductCategory(int productCategoryId, int countryId, HttpServletRequest request) throws Exception;

    ProcessQuestionsConfiguration getEvaluationProcessByLoanApplication(LoanApplication loanApplication) throws Exception;

    JSONObject getQuestionConfiguration(LoanApplication loanApplication);

    Integer getQuestionIdByResult(LoanApplication loanApplication, int currentQuestion, String result, HttpServletRequest request) throws Exception;

    Integer getQuestionIdByResult(LoanApplication loanApplication, int productCategoryId, int questionId, String processQuestionResult, int countryId, HttpServletRequest request) throws Exception;

    Integer forwardByResult(LoanApplication loanApplication, String processQuestionResult, HttpServletRequest request) throws Exception;

    Integer forwardByResult(LoanApplication loanApplication, String processQuestionResult, int sequenceType, HttpServletRequest request) throws Exception;

    Integer forwardById(LoanApplication loanApplication, int processQuestionId, int sequenceType, HttpServletRequest request) throws Exception;

    Integer backward(LoanApplication loanApplication, HttpServletRequest request) throws Exception;

    Integer getBackwardId(LoanApplication loanApplication);

    Integer getQuestionCategoryId(int questionId, LoanApplication loanApplication);

    ProcessQuestion getQuestionFromEvaluationProcess(LoanApplication loanApplication, Integer productCategoryId, int questionId, Integer countryId, HttpServletRequest request) throws Exception;

    boolean isQuestionRepeatedInProces(List<ProcessQuestionSequence> sequence, int questionId);

    List<Integer> getIdsCatalogFromConfigurationInProcessQuestion(Integer loanApplicationId, int questionId, String configurationKey) throws Exception;
}
