/**
 *
 */
package com.affirm.common.service.impl;

import com.affirm.common.dao.CreditDAO;
import com.affirm.common.dao.JsonResolverDAO;
import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.model.catalog.*;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.*;
import com.affirm.common.service.question.Question50Service;
import com.affirm.common.service.question.QuestionFlowService;
import com.affirm.common.util.CryptoUtil;
import com.affirm.common.util.JsonUtil;
import com.affirm.system.configuration.Configuration;
import com.google.gson.Gson;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author jrodriguez
 */

@Service("EvaluationService")
public class EvaluationServiceImpl implements EvaluationService {

    private static Logger logger = Logger.getLogger(EvaluationServiceImpl.class);

    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private LoanApplicationService loanApplicationService;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private CountryContextService countryContextService;
    @Autowired
    private PathService pathService;
    @Autowired
    private BrandingService brandingService;
    @Autowired
    private QuestionPercentageService questionPercentageService;
    @Autowired
    private FunnelStepService funnelStepService;
    @Autowired
    private CreditDAO creditDao;
    @Autowired
    private Question50Service question50Service;

    @Override
    public String generateEvaluationToken(int userId, int personId, int loanApplicationId) {
        return loanApplicationService.generateLoanApplicationToken(userId, personId, loanApplicationId);
    }

    @Override
    public Integer getIdFromToken(String token) throws Exception {
        String decripted = CryptoUtil.decrypt(token);
        if (decripted == null)
            throw new Exception("The token " + token + " could not be decrypted");

        JSONObject jsonObject = new JSONObject(decripted);
        return JsonUtil.getIntFromJson(jsonObject, "loan", null);
    }

    @Override
    public ProcessQuestionsConfiguration getEvaluationProcessByProductCategory(int productCategoryId, int countryId, HttpServletRequest request) throws Exception {
        ProductCategory productCategory = catalogService.getCatalogById(ProductCategory.class, productCategoryId, Configuration.getDefaultLocale());
        ProductCategoryCountry productCategoryCountry = null;
        if (productCategory != null && productCategory.getCountriesConfig() != null)
            productCategoryCountry = productCategory.getCountriesConfig().stream().filter(c -> c.getCountryId() == countryId).findFirst().orElse(null);

        if (productCategoryCountry == null)
            return null;

        ProcessQuestionsConfiguration config = new ProcessQuestionsConfiguration();
        config.fillFromDb(productCategoryCountry.getJsonProcess());


        // If its branding, apply the branding question config
        if (request != null && brandingService.isBranded(request)) {
            EntityBranding entityBranding = brandingService.getEntityBranding(request);
            if (entityBranding != null && entityBranding.getBrandingQuestionConfiguration() != null) {
                config.getQuestions().removeIf(q -> entityBranding.getBrandingQuestionConfiguration().getQuestions().stream().anyMatch(eq -> eq.getId().equals(q.getId())));
                config.getQuestions().addAll(entityBranding.getBrandingQuestionConfiguration().getQuestions());
            }
        }

        return config;
    }

    @Override
    public ProcessQuestionsConfiguration getEvaluationProcessByLoanApplication(LoanApplication loanApplication) throws Exception {
        ProcessQuestionsConfiguration loanConfig = getEvaluationProcessByProductCategory(loanApplication.getProductCategoryId(), loanApplication.getCountryId(), null);
        List<LoanApplicationPreliminaryEvaluation> preEvaluations = loanApplicationDao.getPreliminaryEvaluations(loanApplication.getId(), Configuration.getDefaultLocale(), JsonResolverDAO.EVALUATION_FOLLOWER_DB);

        // Replace the marektplace questions with the branding questions
        // If is branded and (doesnt have any pre evaluations yet or is not PREAPROVED, EVAAPPROVED or WAITING), replace
        if (loanApplication.getEntityId() != null) {
            List<LoanApplicationPreliminaryEvaluation> entityPreEvaluations = preEvaluations.stream().filter(p -> p.getEntity().getId().intValue() == loanApplication.getEntityId()).collect(Collectors.toList());
            if (preEvaluations.isEmpty() || preEvaluations.stream().anyMatch(p -> p.getApproved() == null || !p.getApproved()) || entityPreEvaluations.stream().anyMatch(p -> p.getApproved() != null && p.getApproved()) ||
                    !Arrays.asList(LoanApplicationStatus.NEW, LoanApplicationStatus.PRE_EVAL_APPROVED, LoanApplicationStatus.EVAL_APPROVED, LoanApplicationStatus.WAITING_APPROVAL).contains(loanApplication.getStatus().getId())) {
                EntityBranding entityBranding = catalogService.getEntityBranding(loanApplication.getEntityId());
                if (entityBranding != null && entityBranding.getBrandingQuestionConfiguration() != null) {
                    loanConfig.getQuestions().removeIf(q -> entityBranding.getBrandingQuestionConfiguration().getQuestions().stream().anyMatch(eq -> eq.getId().equals(q.getId())));
                    loanConfig.getQuestions().addAll(entityBranding.getBrandingQuestionConfiguration().getQuestions());
                }
            }
        }


        // If all the preliminary evaluations has ran and the approved ones are from the same entity, check if there's a question process for the entityProductParams approved
        boolean isPreEvaluationQuestionReplaced = false;
        if (loanApplication.getStatus().getId() == LoanApplicationStatus.NEW || loanApplication.getStatus().getId() == LoanApplicationStatus.PRE_EVAL_APPROVED || loanApplication.getStatus().getId() == LoanApplicationStatus.CROSS_SELLING_OFFER || loanApplication.getStatus().getId() == LoanApplicationStatus.EVAL_APPROVED || loanApplication.getStatus().getId() == LoanApplicationStatus.LEAD_REFERRED || loanApplication.getStatus().getId() == LoanApplicationStatus.LEAD_REFERRED) {
//            List<LoanApplicationPreliminaryEvaluation> preEvaluations = loanApplicationDao.getPreliminaryEvaluations(loanApplication.getId(), Configuration.getDefaultLocale());
            if (preEvaluations != null && preEvaluations.stream().noneMatch(p -> p.getApproved() == null & p.getStatus() != null)) {
                List<LoanApplicationPreliminaryEvaluation> approvedPreEvaluations = preEvaluations.stream().filter(p -> p.getApproved() != null && p.getApproved()).collect(Collectors.toList());
                if (approvedPreEvaluations.stream().mapToInt(p -> p.getEntityId()).distinct().count() == 1) {
                    EntityBranding entityBranding = catalogService.getEntityBranding(approvedPreEvaluations.get(0).getEntityId());
                    if (entityBranding != null) {
                        isPreEvaluationQuestionReplaced = true;

                        ProcessQuestionsConfiguration approvedQuestionConfig = entityBranding.getEntityProductParamQuestionConfiguration(approvedPreEvaluations.stream().mapToInt(a -> a.getEntityProductParameterId()).boxed().toArray(Integer[]::new));
                        if (approvedQuestionConfig != null) {
                            loanConfig.getQuestions().removeIf(q -> approvedQuestionConfig.getQuestions().stream().anyMatch(eq -> eq.getId().equals(q.getId())));
                            loanConfig.getQuestions().addAll(approvedQuestionConfig.getQuestions());
                        }
                    }
                } else {
                    LoanApplicationEvaluation loanEvaluation = loanApplicationService.getLastEvaluation(loanApplication.getId(), loanApplication.getPersonId(), Configuration.getDefaultLocale(), true);
                    if (loanEvaluation != null) {
                        List<LoanApplicationEvaluation> evaluations = loanApplicationDao.getEvaluations(loanApplication.getId(), Configuration.getDefaultLocale(), JsonResolverDAO.EVALUATION_FOLLOWER_DB);
                        if (evaluations != null && evaluations.stream().filter(p -> p.getApproved() != null && p.getApproved()).count() == 1) {
                            LoanApplicationEvaluation evaluation = evaluations.stream().filter(p -> p.getApproved() != null && p.getApproved()).findFirst().orElse(null);
                            EntityBranding entityBranding = catalogService.getEntityBranding(evaluation.getEntityId());
                            if (entityBranding != null) {
                                isPreEvaluationQuestionReplaced = true;

                                ProcessQuestionsConfiguration approvedQuestionConfig = entityBranding.getEntityProductParamQuestionConfiguration(evaluation.getEntityProductParameterId());
                                if (approvedQuestionConfig != null) {
                                    loanConfig.getQuestions().removeIf(q -> approvedQuestionConfig.getQuestions().stream().anyMatch(eq -> eq.getId().equals(q.getId())));
                                    loanConfig.getQuestions().addAll(approvedQuestionConfig.getQuestions());
                                }
                            }
                        }
                    }
                }
            }
        }

        // If its EvaluationApproved of WaitingEvaluation, set the qeustionConfig of the EntityProductParam
        if (
                loanApplication.getStatus().getId() == LoanApplicationStatus.EVAL_APPROVED || loanApplication.getStatus().getId() == LoanApplicationStatus.WAITING_APPROVAL || loanApplication.getStatus().getId() == LoanApplicationStatus.APPROVED || loanApplication.getStatus().getId() == LoanApplicationStatus.APPROVED_SIGNED || loanApplication.getStatus().getId() == LoanApplicationStatus.REJECTED
        ) {
            List<LoanOffer> offers = loanApplicationDao.getLoanOffers(loanApplication.getId());
            if (offers != null) {
                LoanOffer selectedOffer = offers.stream().filter(o -> o.getSelected() != null && o.getSelected()).findFirst().orElse(null);
                if (selectedOffer != null) {
                    // Merge the config of entity with the loan
                    ProcessQuestionsConfiguration entityConfig = catalogService.getEntityProductParamById(selectedOffer.getEntityProductParameterId()).getEvaluation();
                    if (entityConfig != null) {
                        loanConfig.getQuestions().removeIf(q -> q.getId() == ProcessQuestion.Question.OFFER.getId());
                        loanConfig.getQuestions().removeIf(q -> q.getId() == ProcessQuestion.Question.CONSOLIDATION_OFFER.getId());
                        loanConfig.getQuestions().removeIf(q -> q.getId() == ProcessQuestion.Question.VEHICLE_OFFER.getId());
                        loanConfig.getQuestions().removeIf(q -> entityConfig.getQuestions().stream().anyMatch(qu -> qu.getId().equals(q.getId())));

                        // Change the 62 result question with the one configured in the product category
                        if (entityConfig.getQuestions().stream().anyMatch(q -> q.getResults().toMap().entrySet().stream().anyMatch(e -> e.getValue().equals(62)))) {
                            for (ProcessQuestion pq : entityConfig.getQuestions()) {
                                List<String> keys = pq.getResults().toMap().entrySet().stream().filter(e -> e.getValue().equals(62)).map(e -> e.getKey()).collect(Collectors.toList());
                                for (String key : keys) {
                                    pq.getResults().put(key, loanConfig.getAfterValidationQuestionId());
                                }
                            }
                        }

                        loanConfig.getQuestions().addAll(entityConfig.getQuestions());
                    }
                }
            }
        }

        // If the preevaluation questions are still not replaced and its branding, apply the branding question config
//        if (!isPreEvaluationQuestionReplaced && loanApplication.getEntityId() != null && loanApplication.getStatus().getId() == LoanApplicationStatus.NEW) {
//            EntityBranding entityBranding = catalogService.getEntityBranding(loanApplication.getEntityId());
//            if (entityBranding != null && entityBranding.getBrandingQuestionConfiguration() != null) {
//                loanConfig.getQuestions().removeIf(q -> entityBranding.getBrandingQuestionConfiguration().getQuestions().stream().anyMatch(eq -> eq.getId().equals(q.getId())));
//                loanConfig.getQuestions().addAll(entityBranding.getBrandingQuestionConfiguration().getQuestions());
//            }
//        }

        return loanConfig;
    }

    @Override
    public JSONObject getQuestionConfiguration(LoanApplication loanApplication) {
        return new JSONObject(Configuration.EVALUATION_CONFIG);
    }

    @Override
    public Integer getQuestionIdByResult(LoanApplication loanApplication, int currentQuestion, String result, HttpServletRequest request) throws Exception {
        return getQuestionIdByResult(
                loanApplication,
                loanApplication.getProductCategoryId(),
                currentQuestion,
                result,
                loanApplication != null ? loanApplication.getCountryId() : countryContextService.getCountryParamsByRequest(request).getId(),
                request);
    }

    @Override
    public Integer getQuestionIdByResult(LoanApplication loanApplication, int productCategoryId, int questionId, String processQuestionResult, int countryId, HttpServletRequest request) throws Exception {
        ProcessQuestion processQuestion = null;
        if (loanApplication != null)
            processQuestion = getEvaluationProcessByLoanApplication(loanApplication).getQuestions().stream().filter(q -> q.getId() == questionId).findFirst().orElse(null);
        else
            processQuestion = getEvaluationProcessByProductCategory(productCategoryId, countryId, request).getQuestions().stream().filter(q -> q.getId() == questionId).findFirst().orElse(null);

        if (processQuestion != null) {
            return processQuestion.getResultQuestionId(processQuestionResult);
        }
        return null;
    }

    @Override
    public Integer forwardByResult(LoanApplication loanApplication, String processQuestionResult, HttpServletRequest request) throws Exception {
        return forwardByResult(loanApplication, processQuestionResult, ProcessQuestionSequence.TYPE_FORWARD, request);
    }

    @Override
    public Integer forwardByResult(LoanApplication loanApplication, String processQuestionResult, int sequenceType, HttpServletRequest request) throws Exception {

        Integer questionIdtoUpdate;
        Integer originalCurrentQuestion = loanApplication.getCurrentQuestionId();
        Integer originalCurrentQuestionCategory = originalCurrentQuestion != null ? catalogService.getProcessQuestion(originalCurrentQuestion).getCategory().getId() : null;

        // If there is no current question, update with the first question in configuration
        if (loanApplication.getCurrentQuestionId() == null) {
            questionIdtoUpdate = getEvaluationProcessByLoanApplication(loanApplication).getFirstQuestionId();
        }
        // Else, update with the result question
        else {
            questionIdtoUpdate = getQuestionIdByResult(loanApplication, loanApplication.getCurrentQuestionId(), processQuestionResult, request);
        }
        loanApplicationDao.updateCurrentQuestion(loanApplication.getId(), questionIdtoUpdate);
        loanApplication.setCurrentQuestionId(questionIdtoUpdate);

        // Update finish date last question
        if (!loanApplication.getQuestionSequence().isEmpty())
            loanApplication.getQuestionSequence().get(loanApplication.getQuestionSequence().size() - 1).setFinishDate(new Date());

        // Update sequence
        loanApplication.getQuestionSequence().add(new ProcessQuestionSequence(questionIdtoUpdate, sequenceType, new Date(), null, request != null ? request.getHeader("User-Agent") : null));
        loanApplicationDao.updateQuestionSequence(loanApplication.getId(), new Gson().toJson(loanApplication.getQuestionSequence()));
//        double percentage = questionPercentageService.getCurrentPercentage(loanApplication.getCurrentQuestionId(), loanApplication.getCountryId(), loanApplication.getProductCategoryId(), loanApplication.getProduct() == null ? null : loanApplication.getSelectedProductId(), loanApplication.getSelectedEntityId(), loanApplication.getPercentage());

//        int currentQuestionCategoryId = catalogService.getProcessQuestion(loanApplication.getCurrentQuestionId()).getCategory().getId();
//        double previousPercentage = loanApplication.getPercentage();
//        if(originalCurrentQuestionCategory == null || originalCurrentQuestionCategory != currentQuestionCategoryId)
//            previousPercentage = 0.0;
//
//        double percentage = questionPercentageService.getCurrentCategoryPercentage(loanApplication.getCurrentQuestionId(), loanApplication.getCountryId(), loanApplication.getProductCategoryId(), previousPercentage, loanApplication.getId());
//        loanApplicationDao.updatePercentageProgress(loanApplication.getId(), Math.min(percentage, 100.0));

        // update the loan funnel steps
        funnelStepService.registerStep(loanApplication);

        return questionIdtoUpdate;
    }

    @Override
    public Integer forwardById(LoanApplication loanApplication, int processQuestionId, int sequenceType, HttpServletRequest request) throws Exception {

        Integer originalCurrentQuestion = loanApplication.getCurrentQuestionId();
        Integer originalCurrentQuestionCategory = originalCurrentQuestion != null ? catalogService.getProcessQuestion(originalCurrentQuestion).getCategory().getId() : null;

        loanApplicationDao.updateCurrentQuestion(loanApplication.getId(), processQuestionId);
        loanApplication.setCurrentQuestionId(processQuestionId);

        // Update finish date last question
        if (!loanApplication.getQuestionSequence().isEmpty())
            loanApplication.getQuestionSequence().get(loanApplication.getQuestionSequence().size() - 1).setFinishDate(new Date());

        // Update sequence
        loanApplication.getQuestionSequence().add(new ProcessQuestionSequence(processQuestionId, sequenceType, new Date(), null, request != null ? request.getHeader("User-Agent") : null));
        loanApplicationDao.updateQuestionSequence(loanApplication.getId(), new Gson().toJson(loanApplication.getQuestionSequence()));
//        double percentage = questionPercentageService.getCurrentPercentage(loanApplication.getCurrentQuestionId(), loanApplication.getCountryId(), loanApplication.getProductCategoryId(), loanApplication.getProduct() == null ? null : loanApplication.getSelectedProductId(), loanApplication.getSelectedEntityId(), loanApplication.getPercentage());
        int currentQuestionCategoryId = catalogService.getProcessQuestion(loanApplication.getCurrentQuestionId()).getCategory().getId();
        double previousPercentage = loanApplication.getPercentage();
        if (originalCurrentQuestionCategory == null || originalCurrentQuestionCategory != currentQuestionCategoryId)
            previousPercentage = 0.0;

        double percentage = questionPercentageService.getCurrentCategoryPercentage(loanApplication.getCurrentQuestionId(), loanApplication.getCountryId(), loanApplication.getProductCategoryId(), previousPercentage, loanApplication.getId());
        loanApplicationDao.updatePercentageProgress(loanApplication.getId(), Math.min(percentage, 100.0));

        return processQuestionId;
    }

    @Override
    public Integer backward(LoanApplication loanApplication, HttpServletRequest request) throws Exception {

        Integer questionIdtoUpdate = null;

        if (loanApplication.getCurrentQuestionId() == ProcessQuestion.Question.Constants.OFFER && loanApplication.getEntityId() != null && loanApplication.getEntityId() == Entity.AZTECA && loanApplication.getFirstDueDate() != null) {
            question50Service.customMethod("backToFirstDueDate", QuestionFlowService.Type.LOANAPPLICATION, loanApplication.getId(), Configuration.getDefaultLocale(), null);
            return null;
        } else {
            questionIdtoUpdate = this.getBackwardId(loanApplication);
        }

        if (questionIdtoUpdate == null)
            throw new Exception("Cant go back!!");

        loanApplicationDao.updateCurrentQuestion(loanApplication.getId(), questionIdtoUpdate);
        loanApplication.setCurrentQuestionId(questionIdtoUpdate);

        // Update finish date last question
        if (!loanApplication.getQuestionSequence().isEmpty())
            loanApplication.getQuestionSequence().get(loanApplication.getQuestionSequence().size() - 1).setFinishDate(new Date());

        // Update sequence
        loanApplication.getQuestionSequence().add(new ProcessQuestionSequence(questionIdtoUpdate, ProcessQuestionSequence.TYPE_BACKWARD, new Date(), null, request != null ? request.getHeader("User-Agent") : null));
        loanApplicationDao.updateQuestionSequence(loanApplication.getId(), new Gson().toJson(loanApplication.getQuestionSequence()));

        loanApplicationDao.updatePercentageRemoveProgress(loanApplication.getId());

        return questionIdtoUpdate;
    }

    @Override
    public Integer getBackwardId(LoanApplication loanApplication) {
        Integer questionIdtoUpdate = null;

        // If there is no current question, update with the first question in configuration
        if (loanApplication.getQuestionSequence() != null && loanApplication.getQuestionSequence().size() > 1) {
            for (int i = loanApplication.getQuestionSequence().size() - 1; i >= 0; i--) {
                ProcessQuestionSequence question = loanApplication.getQuestionSequence().get(i);
                if (question.getId().intValue() == loanApplication.getCurrentQuestionId() && question.getType() == ProcessQuestionSequence.TYPE_FORWARD && i > 0) {
                    questionIdtoUpdate = loanApplication.getQuestionSequence().get(i - 1).getId();
                    break;
                } else if (question.getId().intValue() == loanApplication.getCurrentQuestionId() && question.getType() == ProcessQuestionSequence.TYPE_SKIPPED) {
                    for (int j = 1; j <= 5; j++) {
                        if (i - j >= 1 && loanApplication.getQuestionSequence().get(i - j).getType() == ProcessQuestionSequence.TYPE_FORWARD) {
                            if (!loanApplication.getQuestionSequence().get(i - j - 1).getId().equals(loanApplication.getCurrentQuestionId())) {
                                questionIdtoUpdate = loanApplication.getQuestionSequence().get(i - j - 1).getId();
                                break;
                            }
                        }
                    }
                    break;
                }
            }
        }

        if (questionIdtoUpdate == null) return null;

        Integer questionToUpdateSection = getQuestionCategoryId(questionIdtoUpdate, loanApplication);
        Integer actualQuestionToUpdateSection = getQuestionCategoryId(loanApplication.getCurrentQuestionId(), loanApplication);

        if (questionToUpdateSection == null || actualQuestionToUpdateSection == null)
            return null;

        if (loanApplication.getCurrentQuestionId() == ProcessQuestion.Question.Constants.RUNNING_PREEVALUATION)
            return null;

        if ((questionToUpdateSection == ProcessQuestionCategory.PRE_INFORMATION && actualQuestionToUpdateSection != ProcessQuestionCategory.PRE_INFORMATION)
                || (actualQuestionToUpdateSection == ProcessQuestionCategory.RESULT && questionToUpdateSection != ProcessQuestionCategory.RESULT)
                || (questionToUpdateSection == ProcessQuestionCategory.OFFER && actualQuestionToUpdateSection != ProcessQuestionCategory.OFFER)
                || actualQuestionToUpdateSection == ProcessQuestionCategory.VERIFICATION
                || actualQuestionToUpdateSection == ProcessQuestionCategory.EVALUATION
                || actualQuestionToUpdateSection == ProcessQuestionCategory.WAITING_APPROVAL
                || Arrays.asList(
                ProcessQuestion.Question.Constants.OFFER,
                ProcessQuestion.Question.Constants.CONSOLIDATION_OFFER,
                ProcessQuestion.Question.Constants.VEHICLE_OFFER,
                ProcessQuestion.Question.Constants.RUNNING_EVALUATION,
                ProcessQuestion.Question.Constants.RUNNING_PREEVALUATION,
                ProcessQuestion.Question.Constants.DISAPPROVE_EVALUATION,
                ProcessQuestion.Question.Constants.OFFER_REJECTION_REASON,
                ProcessQuestion.Question.Constants.VERIFICATION_PHONE_NUMBER_PIN
        ).contains(loanApplication.getCurrentQuestionId())
                || Arrays.asList(
                ProcessQuestion.Question.Constants.ARE_YOU_READY
        ).contains(questionIdtoUpdate))
            return null;

        return questionIdtoUpdate;
    }

    @Override
    public Integer getQuestionCategoryId(int questionId, LoanApplication loanApplication) {
        ProcessQuestion question = catalogService.getProcessQuestion(questionId);
        if (question.getCategory() != null) {
            return question.getCategory().getId();
        }
        return null;

//        JSONObject jsonSections = getQuestionConfiguration(loanApplication).getJSONObject("sections");
//
//        JSONArray jsonPreInformation = jsonSections.getJSONArray("preInformation");
//        for (int i = 0; i < jsonPreInformation.length(); i++) {
//            if (jsonPreInformation.getInt(i) == questionId) {
//                return ProcessQuestionSection.PRELIMINARY_INFORMATION;
//            }
//        }
//        JSONArray jsonPersonalInformation = jsonSections.getJSONArray("personalInformation");
//        for (int i = 0; i < jsonPersonalInformation.length(); i++) {
//            if (jsonPersonalInformation.getInt(i) == questionId) {
//                return ProcessQuestionSection.PERSONAL_INFORMATION;
//            }
//        }
//        JSONArray jsonIncome = jsonSections.getJSONArray("income");
//        for (int i = 0; i < jsonIncome.length(); i++) {
//            if (jsonIncome.getInt(i) == questionId) {
//                return ProcessQuestionSection.INCOME;
//            }
//        }
//        JSONArray jsonOffer = jsonSections.getJSONArray("offer");
//        for (int i = 0; i < jsonOffer.length(); i++) {
//            if (jsonOffer.getInt(i) == questionId) {
//                return ProcessQuestionSection.OFFER;
//            }
//        }
//        JSONArray jsonVerification = jsonSections.getJSONArray("verification");
//        for (int i = 0; i < jsonVerification.length(); i++) {
//            if (jsonVerification.getInt(i) == questionId) {
//                return ProcessQuestionSection.VERIFICATION;
//            }
//        }
//
//        JSONArray jsonResult = jsonSections.getJSONArray("result");
//        for (int i = 0; i < jsonResult.length(); ++i) {
//            if (jsonResult.getInt(i) == questionId) {
//                return ProcessQuestionSection.RESULT;
//            }
//        }
//
//        return null;
    }

    @Override
    public ProcessQuestion getQuestionFromEvaluationProcess(LoanApplication loanApplication, Integer productCategoryId, int questionId, Integer countryId, HttpServletRequest request) throws Exception {
        if (loanApplication != null)
            return getEvaluationProcessByLoanApplication(loanApplication).getQuestions().stream().filter(q -> q.getId().equals(questionId)).findFirst().orElse(null);
        else
            return getEvaluationProcessByProductCategory(productCategoryId, countryId, request).getQuestions().stream().filter(q -> q.getId().equals(questionId)).findFirst().orElse(null);
    }

    @Override
    public boolean isQuestionRepeatedInProces(List<ProcessQuestionSequence> sequence, int questionId) {
        for (int i = 0; i < sequence.size(); i++) {
            ProcessQuestionSequence item = sequence.get(i);
            if (item.getId() == questionId && item.getFinishDate() != null) {
                if (i + 1 < sequence.size()) {
                    ProcessQuestionSequence nextItem = sequence.get(i + 1);
                    if (nextItem.getType() != ProcessQuestionSequence.TYPE_BACKWARD)
                        return true;
                }
            }
        }
        return false;
    }

    @Override
    public List<Integer> getIdsCatalogFromConfigurationInProcessQuestion(Integer loanApplicationId, int questionId, String configurationKey) throws Exception {
        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(loanApplicationId, Configuration.getDefaultLocale());
        ProcessQuestion currentQuestion = getQuestionFromEvaluationProcess(loanApplication, null, questionId, null, null);

        final List<Integer> ids;

        if (currentQuestion.getConfiguration() != null) {
            JSONArray jsonArray = JsonUtil.getJsonArrayFromJson(currentQuestion.getConfiguration(), configurationKey, null);
            if (jsonArray != null) {
                ids = JsonUtil.getListFromJsonArray(jsonArray, (arr, i) -> arr.getInt(i));
            } else {
                ids = null;
            }
        } else {
            ids = null;
        }

        return ids;
    }

}
