package com.affirm.common.service.impl;

import com.affirm.common.model.catalog.ProcessQuestion;
import com.affirm.common.model.catalog.ProcessQuestionCategory;
import com.affirm.common.model.catalog.ProcessQuestionsConfiguration;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.model.transactional.ProcessQuestionSequence;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.EvaluationService;
import com.affirm.common.service.LoanApplicationQuestionService;
import com.affirm.common.service.LoanApplicationService;
import com.affirm.common.service.question.AbstractQuestionService;
import com.affirm.common.service.question.QuestionFlowService;
import com.affirm.common.util.TreeProcessQuestionWrapper;
import com.affirm.system.configuration.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service("loanApplicationQuestionService")
public class LoanApplicationQuestionServiceImpl implements LoanApplicationQuestionService {

    @Autowired
    private EvaluationService evaluationService;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private LoanApplicationService loanApplicationService;
    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public Map<Integer, TreeProcessQuestionWrapper> getAnsweredQuestionsWrapperGroupedByCategory(LoanApplication loanApplication) throws Exception {
//        ProcessQuestionsConfiguration loanQuestionConfig = evaluationService.getEvaluationProcessByLoanApplication(loanApplication);
        List<ProcessQuestionSequence> sequence = loanApplicationService.getQuestionSequenceWithoutBackwards(loanApplication.getQuestionSequence());
        Map<Integer, TreeProcessQuestionWrapper> categoriesWrappers = new LinkedHashMap<>();

        // From the current proces questino, get all the questions of that category
        Integer questionIndex = 0;
        while (true) {
            ProcessQuestionSequence firstQuestion = sequence.get(questionIndex);
            Integer auxcategoryId = catalogService.getProcessQuestion(firstQuestion.getId()).getCategory().getId();
            TreeProcessQuestionWrapper categoryQuestionWrapper = getAnsweredQuestionChild(questionIndex, auxcategoryId, sequence);
            if (categoryQuestionWrapper == null)
                break;

            // If its the category income, check if its principal, secundary or other ocupation
            if (auxcategoryId == ProcessQuestionCategory.INCOME) {
                TreeProcessQuestionWrapper auxQuestionWraper = categoryQuestionWrapper;
                int number = 0;
                while (auxQuestionWraper != null) {
                    if (Arrays.asList(ProcessQuestion.Question.Constants.PRINCIPAL_INCOME_TYPE, ProcessQuestion.Question.Constants.OTHER_INCOME_TYPE).contains(auxQuestionWraper.getProcessQuestion().getId())) {
                        number++;
                    }
                    if (number > 0) {
                        auxQuestionWraper.addParam(AbstractQuestionService.VIEW_PARAM_OCUPATION_NUMBER, number);
                    }
                    auxQuestionWraper = auxQuestionWraper.getChilds() != null && !auxQuestionWraper.getChilds().isEmpty() ? auxQuestionWraper.getChilds().get(0) : null;
                }
            }

            categoriesWrappers.put(auxcategoryId, categoryQuestionWrapper);


            // View if there is more questions of other category
            Integer lastChildrenQuestionId;
            TreeProcessQuestionWrapper lastChildren = categoryQuestionWrapper.getLastChildOfTree();
            if (lastChildren != null)
                lastChildrenQuestionId = lastChildren.getProcessQuestion().getId();
            else
                lastChildrenQuestionId = categoryQuestionWrapper.getProcessQuestion().getId();

            List<ProcessQuestionSequence> answersOfCurrentQuestion = sequence.stream().filter(a -> a.getId().equals(lastChildrenQuestionId)).collect(Collectors.toList());
            ProcessQuestionSequence lastAnswer = answersOfCurrentQuestion.get(answersOfCurrentQuestion.size() - 1);
            int indexOfLastAnswer = sequence.indexOf(lastAnswer);
            if (indexOfLastAnswer < sequence.size() - 1) {
                questionIndex = indexOfLastAnswer + 1;
            } else {
                break;
            }
        }

        return categoriesWrappers;
    }

    private TreeProcessQuestionWrapper getAnsweredQuestionChild(int index, int categoryId, List<ProcessQuestionSequence> answeredQuestions) {

        // Find the last qustion node with the id
        ProcessQuestionSequence answer = answeredQuestions.get(index);
        if (answer == null)
            return null;

        Integer thisQuestioncategoryId = catalogService.getProcessQuestion(answer.getId()).getCategory().getId();
        if (thisQuestioncategoryId == null || !thisQuestioncategoryId.equals(categoryId))
            return null;

        TreeProcessQuestionWrapper wrapper = new TreeProcessQuestionWrapper();
        wrapper.setProcessQuestion(catalogService.getProcessQuestion(answer.getId()));

        // If the last answer has a question after, means that has a response
        if (index < answeredQuestions.size() - 1) {
            wrapper.addChild(getAnsweredQuestionChild(index + 1, categoryId, answeredQuestions));
        }

        return wrapper;
    }

    @Override
    public TreeProcessQuestionWrapper getCurrentCategoryQuestions(LoanApplication loanApplication, int questionId, int categoryId) throws Exception {
        int questionCategory = categoryId;
        int startQuestionId = questionId;
        ProcessQuestionsConfiguration loanQuestionConfig = evaluationService.getEvaluationProcessByLoanApplication(loanApplication);

        // From the current proces questino, get all the questions of that category
        TreeProcessQuestionWrapper firstQuestionWrapper = getQuestionChild(startQuestionId, questionCategory, 0, loanQuestionConfig, loanApplication);
        return firstQuestionWrapper;
    }

    private TreeProcessQuestionWrapper getQuestionChild(int questionId, int questionCategoryId, int level, ProcessQuestionsConfiguration loanQuestionConfig, LoanApplication loanApplication) throws Exception{
        ProcessQuestion question = loanQuestionConfig.getQuestions().stream().filter(q -> q.getId().equals(questionId)).findFirst().orElse(null);
        Integer thisQuestioncategoryId = catalogService.getProcessQuestion(questionId).getCategory().getId();
        if (question == null || thisQuestioncategoryId == null || !thisQuestioncategoryId.equals(questionCategoryId)) {
//        if (question == null || level > 5) {
            return null;
        }

        TreeProcessQuestionWrapper wrapper = new TreeProcessQuestionWrapper();
        wrapper.setProcessQuestion(question);
        if (question.getResults() != null) {
            if (question.getResults().keySet().size() == 1) {
                Iterator<String> keys = question.getResults().keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    Integer childQuestionId = question.getResultQuestionId(key);
                    if (childQuestionId != null) {
                        wrapper.addChild(getQuestionChild(childQuestionId, questionCategoryId, level + 1, loanQuestionConfig, loanApplication));
                    }
                }
            }else{
                // If have more than one result, check if it can be skipped
                Object auxQuestionServiceObject = applicationContext.getBean("question" + questionId + "Service");
                String skippedResult = ((AbstractQuestionService) auxQuestionServiceObject).getSkippedQuestionResultToGo(QuestionFlowService.Type.LOANAPPLICATION, loanApplication.getId(), Configuration.getDefaultLocale(), false);
                if(skippedResult != null){
                    wrapper.setSkipped(true);
                    Integer childQuestionId = question.getResultQuestionId(skippedResult);
                    if (childQuestionId != null) {
                        wrapper.addChild(getQuestionChild(childQuestionId, questionCategoryId, level + 1, loanQuestionConfig, loanApplication));
                    }
                }
            }

        }

        return wrapper;
    }

}
