package com.affirm.common.service;

import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.util.TreeProcessQuestionWrapper;

import java.util.Map;

public interface LoanApplicationQuestionService {

    Map<Integer, TreeProcessQuestionWrapper> getAnsweredQuestionsWrapperGroupedByCategory(LoanApplication loanApplication) throws Exception;

    TreeProcessQuestionWrapper getCurrentCategoryQuestions(LoanApplication loanApplication, int questionId, int categoryId) throws Exception;
}
