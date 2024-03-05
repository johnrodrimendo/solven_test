package com.affirm.common.service.impl;

import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.model.catalog.ProcessQuestion;
import com.affirm.common.model.catalog.ProcessQuestionCategory;
import com.affirm.common.model.catalog.ProcessQuestionsConfiguration;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.model.transactional.ProcessQuestionSequence;
import com.affirm.common.service.*;
import com.affirm.common.util.BellmanFord;
import com.affirm.common.util.Graph;
import com.affirm.common.util.JsonUtil;
import com.affirm.system.configuration.Configuration;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service("questionPercentageService")
public class QuestionPercentageServiceImpl implements QuestionPercentageService {

    @Autowired
    private PathService pathService;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private EvaluationService evaluationService;
    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private LoanApplicationService loanApplicationService;

    @Override
    public double getCurrentCategoryPercentage(Integer questionId, Integer countryId, Integer productCategoryId, double previousPercentage, Integer loanApplicationId) throws Exception {
        ProcessQuestion question = catalogService.getProcessQuestion(questionId);
        switch (question.getCategory().getId()) {
            case ProcessQuestionCategory.PRE_INFORMATION:
            case ProcessQuestionCategory.PERSONAL_INFORMATION:
            case ProcessQuestionCategory.INCOME:
            case ProcessQuestionCategory.VERIFICATION:
                ProcessQuestionsConfiguration questionConfig = null;
                LoanApplication loanApplication = null;
                if (loanApplicationId != null) {
                    loanApplication = loanApplicationDao.getLoanApplication(loanApplicationId, Configuration.getDefaultLocale());
                    questionConfig = evaluationService.getEvaluationProcessByLoanApplication(loanApplication);
                } else {
                    questionConfig = evaluationService.getEvaluationProcessByProductCategory(productCategoryId, countryId, null);
                }

                ProcessQuestion questionToGo = questionConfig.getQuestions().stream().filter(q -> q.getId().equals(questionId)).findFirst().orElse(null);
                if(questionToGo != null){
                    List<List<Pair<Integer, Double>>> listOfSteps = new ArrayList<>();
                    List<Pair<Integer, Double>> currentListOfSteps = new ArrayList<>();
                    listOfSteps.add(currentListOfSteps);
                    calculateLargePath(questionToGo, questionConfig, question.getCategory().getId(), listOfSteps, currentListOfSteps, loanApplication != null ? loanApplication.getQuestionSequence() : null);

                    List<Pair<Integer, Double>> maxListSize = listOfSteps.stream().max(Comparator.comparing(l -> l.size())).orElse(null);
                    double currentQuestionPoint = maxListSize.stream().filter(p -> p.getKey() == questionId.intValue()).findFirst().orElse(null).getValue();
                    double totalPointsLeft = maxListSize.stream().mapToDouble(p -> p.getValue()).sum();
                    double percentageleft = 100.0 - previousPercentage;

                    double currentPointInPercentage = (currentQuestionPoint * 100.0) / totalPointsLeft;
                    double stepSize = (currentPointInPercentage * percentageleft) / 100.0;
                    return previousPercentage + stepSize;
                }
                return 100.0;
            default:
                return 100.0;
        }
    }

    private void calculateLargePath(ProcessQuestion processQuestion, ProcessQuestionsConfiguration questionConfig, int procesQuestionCategoryId, List<List<Pair<Integer, Double>>> listOfSteps, List<Pair<Integer, Double>> currentListOfSteps, List<ProcessQuestionSequence> loanQestionSequence) {
        ProcessQuestion processQuestionWithCategory = catalogService.getProcessQuestion(processQuestion.getId());
        if (processQuestionWithCategory.getCategory().getId() != procesQuestionCategoryId)
            return;
        if (processQuestion.getResults() == null || processQuestion.getResults().keySet().isEmpty())
            return;
        //Only 2 reps max
        long questionRepetitions;
        if(procesQuestionCategoryId == ProcessQuestionCategory.INCOME && loanQestionSequence != null){
            List<ProcessQuestionSequence> sequenceWithoutBackwards = loanApplicationService.getQuestionSequenceWithoutBackwards(loanQestionSequence);
            boolean isSecundaryIncome = evaluationService.isQuestionRepeatedInProces(sequenceWithoutBackwards, ProcessQuestion.Question.Constants.DEPENDENT_HAS_OTHER_INCOMES);
            if(!isSecundaryIncome)
                isSecundaryIncome = currentListOfSteps.stream().anyMatch(c -> c.getKey().equals(ProcessQuestion.Question.Constants.DEPENDENT_HAS_OTHER_INCOMES));
            questionRepetitions = isSecundaryIncome ? 1 : 0;
            questionRepetitions = questionRepetitions + currentListOfSteps.stream().filter(c -> c.getKey().intValue() == processQuestion.getId()).count();
        }else{
            questionRepetitions = currentListOfSteps.stream().filter(c -> c.getKey().intValue() == processQuestion.getId()).count();
        }

        if (questionRepetitions >= 2)
            return;

        currentListOfSteps.add(Pair.of(processQuestion.getId(), questionRepetitions == 0 ? 1.0 : 0.3));
        List<Pair<Integer, Double>> originalCurrentListOfSteps = new ArrayList<>(currentListOfSteps);
        List<String> keys = new ArrayList<>(processQuestion.getResults().keySet());
        for (int i = 0; i < keys.size(); i++) {
            Integer nextQuestionId = JsonUtil.getIntFromJson(processQuestion.getResults(), keys.get(i), null);
            if(nextQuestionId != null){
                ProcessQuestion nextQuestion = questionConfig.getQuestions().stream().filter(q -> q.getId().equals(nextQuestionId)).findFirst().orElse(null);
                if(nextQuestion != null){
                    if (i == 0) {
                        calculateLargePath(nextQuestion, questionConfig, procesQuestionCategoryId, listOfSteps, currentListOfSteps, loanQestionSequence);
                    }else{
                        List<Pair<Integer, Double>> newPathListOfSteps = new ArrayList<>(originalCurrentListOfSteps);
                        listOfSteps.add(newPathListOfSteps);
                        calculateLargePath(nextQuestion, questionConfig, procesQuestionCategoryId, listOfSteps, newPathListOfSteps, loanQestionSequence);
                    }
                }
            }
        }
    }

    @Override
    public double getCurrentPercentage(Integer questionId, Integer countryId, Integer categoryId, Integer entityId, Integer productId, double previousPercentage) throws Exception {
        if (questionId == 11){
            return 100.0;
        } else {
            Graph prevOffer, afterOffer;

            prevOffer = pathService.getGraphByProductCategory(countryId, categoryId);

            if (entityId == null) {
                afterOffer = pathService.getLargestGraphByProductEntity();
            } else {
                afterOffer = pathService.getGraphByProductEntity(entityId, productId);
            }

            ArrayList<Integer> banned = new ArrayList<>();

            banned.add(ProcessQuestion.Question.Constants.OTHER_INCOME_CAN_DEMONSTRATE);
            banned.add(ProcessQuestion.Question.Constants.VERIFICATION_SOCIAL_NETWORKS);
            banned.add(ProcessQuestion.Question.Constants.MAINTAINED_CAR);
            banned.add(ProcessQuestion.Question.Constants.GUARANTEED_PRODUCT);
            banned.add(ProcessQuestion.Question.Constants.VERIFICATION_EMAIL);
            banned.add(ProcessQuestion.Question.Constants.VARIABLE_U3M_INCOME_DEPENDENT);
            banned.add(ProcessQuestion.Question.Constants.FIXED_U6M_INCOME_PROFESSIONAL_SERVICES);
            banned.add(ProcessQuestion.Question.Constants.COMPENSATION_U12M_OWN_BUSINESS);
            banned.add(ProcessQuestion.Question.Constants.RESCUE_OFFERS);
            banned.add(ProcessQuestion.Question.Constants.RESCUE_CONSOLIDATION_DEBTS);
            banned.add(ProcessQuestion.Question.Constants.WRONG_PHONE_NUMBER);
            banned.add(ProcessQuestion.Question.Constants.OFFER_REJECTION_REASON);

            Graph merged = Graph.merge(prevOffer, afterOffer);

            BellmanFord bellmanFord = new BellmanFord();
            bellmanFord.setBanned(banned);
            bellmanFord.configure(merged);

            double questionsPassed = bellmanFord.query(ProcessQuestion.Question.ARE_YOU_READY.getId(), questionId) * 1.0;
            double questionsToEnd = bellmanFord.query(ProcessQuestion.Question.WAITING_APPROVAL.getId(), questionId) * 1.0;

            //return Math.abs(( questionsPassed * 100.0) / (questionsPassed + questionsToEnd));

            return previousPercentage + ((100.0 - previousPercentage) / (Math.abs(questionsToEnd) + 1));
        }
    }
}
