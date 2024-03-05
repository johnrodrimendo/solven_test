package com.affirm.common.service.question;

import com.affirm.common.dao.*;
import com.affirm.common.model.catalog.*;
import com.affirm.common.model.form.Question95Form;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.model.transactional.LoanApplicationEvaluationsProcess;
import com.affirm.common.model.transactional.LoanApplicationPreliminaryEvaluation;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.EvaluationService;
import com.affirm.common.service.LoanApplicationService;
import com.affirm.common.service.UserService;
import com.affirm.common.service.UtilService;
import com.affirm.common.util.AjaxResponse;
import com.affirm.common.util.JsonUtil;
import com.affirm.common.util.ResponseEntityException;
import com.affirm.common.util.Util;
import com.affirm.system.configuration.Configuration;
import org.apache.commons.lang3.LocaleUtils;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service("question95Service")
public class Question95Service extends AbstractQuestionService<Question95Form> {

    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private LoanApplicationService loanApplicationService;
    @Autowired
    TranslatorDAO translatorDAO;
    @Autowired
    WebServiceDAO webServiceDao;
    @Autowired
    MessageSource messageSource;
    @Autowired
    UtilService utilService;
    @Autowired
    PersonDAO personDAO;
    @Autowired
    CatalogService catalogService;
    @Autowired
    EvaluationService evaluationService;
    @Autowired
    UserService userService;

    @Override
    public Map<String, Object> getViewAttributes(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean fillSavedData, Map<String, Object> params) throws Exception {

        Map<String, Object> attributes = new HashMap<>();
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, locale);

                // Run the preliminary evaluation if not run yet. Also Run the evaluation if its needed
                Boolean runEvaluation = false;
                if(loanApplication.getEntityId() != null && loanApplication.getEntityId() == Entity.AZTECA && loanApplication.getProductCategoryId() == ProductCategory.GATEWAY)
                    runEvaluation = true;
                runPreEvaluationBotIfNoRunYet(loanApplication.getId(), runEvaluation);

                // Fill the model for the rendering
                LoanApplicationEvaluationsProcess evaluationsProcess = loanApplicationDao.getLoanApplicationEvaluationsProcess(loanApplication.getId());
                List<LoanApplicationPreliminaryEvaluation> preEvaluations = loanApplicationDao.getPreliminaryEvaluations(loanApplication.getId(), locale, JsonResolverDAO.EVALUATION_FOLLOWER_DB);
                String message = messageToShow(loanApplication, evaluationsProcess, null, locale, preEvaluations);
                attributes.put("message", loanApplication.getEntityId() == null || loanApplication.getEntityId() != Entity.FUNDACION_DE_LA_MUJER ? message : null);
                attributes.put("completed", message != null);
                attributes.put("loanApplication", loanApplication);
                break;
        }
        return attributes;
    }

    @Override
    public String getQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Question95Form form) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                LoanApplicationEvaluationsProcess evaluationsProcess = loanApplicationDao.getLoanApplicationEvaluationsProcess(loanApplication.getId());
                LoanApplicationPreliminaryEvaluation loanPreEvaluation = loanApplicationService.getLastPreliminaryEvaluation(loanApplication.getId(), Configuration.getDefaultLocale(), null, true);
                List<Integer> peruEntities = catalogService.getEntities().stream().filter(e -> e.getCountryId() == CountryParam.COUNTRY_PERU).map(Entity::getId).collect(Collectors.toList());

                List <Integer> entitiesWhoWaitForFinishedPreevaluation = new ArrayList<>();
                entitiesWhoWaitForFinishedPreevaluation.add(Entity.FUNDACION_DE_LA_MUJER);
                entitiesWhoWaitForFinishedPreevaluation.add(Entity.BANBIF);
                entitiesWhoWaitForFinishedPreevaluation.addAll(peruEntities);

                boolean mustWaitForEvaluation = (loanApplication.getEntityId() != null && entitiesWhoWaitForFinishedPreevaluation.contains(loanApplication.getEntityId())) || loanApplication.getCountryId() == CountryParam.COUNTRY_PERU;

                if(mustWaitForEvaluation){
                    List<LoanApplicationPreliminaryEvaluation> preEvaluations = loanApplicationDao.getPreliminaryEvaluations(loanApplication.getId(), Configuration.getDefaultLocale(), JsonResolverDAO.EVALUATION_FOLLOWER_DB);
                    String result = null;
                    if(preEvaluations != null && !preEvaluations.isEmpty()){
                        if(preEvaluations.stream().anyMatch(p -> p.getStatus() != null && p.getStatus() == 'S' && p.getApproved() != null && p.getApproved())){
                            result = "APROBADO";
                        }else if(preEvaluations.stream().allMatch(p ->
                                (p.getStatus() != null && p.getStatus() == 'S' && p.getApproved() != null && !p.getApproved()) ||
                                (p.getStatus() != null && p.getStatus() == 'F' && p.getApproved() != null && !p.getApproved())
                        )){
                            result = "DESAPROBADO";
                        }
                    }

                    if(result != null){
                        Thread.sleep(1000);

                        preEvaluations = loanApplicationDao.getPreliminaryEvaluations(loanApplication.getId(), Configuration.getDefaultLocale(), JsonResolverDAO.EVALUATION_FOLLOWER_DB);
                        String result2 = null;
                        if(preEvaluations != null && !preEvaluations.isEmpty()){
                            if(preEvaluations.stream().anyMatch(p -> p.getStatus() != null && p.getStatus() == 'S' && p.getApproved() != null && p.getApproved())){
                                result2 = "APROBADO";
                            }else if(preEvaluations.stream().allMatch(p ->
                                    (p.getStatus() != null && p.getStatus() == 'S' && p.getApproved() != null && !p.getApproved()) ||
                                            (p.getStatus() != null && p.getStatus() == 'F' && p.getApproved() != null && !p.getApproved())
                            )){
                                result2 = "DESAPROBADO";
                            }
                        }

                        if(result.equalsIgnoreCase(result2)){
                            return result;
                        }
                    }

                }else{
                    if (loanPreEvaluation != null && loanPreEvaluation.getApproved() != null) {
                        if (loanPreEvaluation.getApproved()) {
                            return "APROBADO";
                        } else {
                            return "DESAPROBADO";
                        }
                    } else if (evaluationsProcess.getPreEvaluationStatus() != null &&
                            (evaluationsProcess.getPreEvaluationStatus() == LoanApplicationEvaluationsProcess.STATUS_FINISHED ||
                                    (evaluationsProcess.getPreEvaluationStatus() == LoanApplicationEvaluationsProcess.STATUS_RUNNING_DELAYED && loanApplication.getProductCategoryId() != ProductCategory.LEADS))) {

                        List<LoanApplicationPreliminaryEvaluation> preEvaluations = loanApplicationDao.getPreliminaryEvaluations(loanApplication.getId(), Configuration.getDefaultLocale(), JsonResolverDAO.EVALUATION_FOLLOWER_DB);
                        String message = messageToShow(loanApplication, evaluationsProcess, form.getCompleted(), LocaleUtils.toLocale(loanApplication.getCountry().getLocale()), preEvaluations);
                        if (message != null)
                            throw new ResponseEntityException(AjaxResponse.ok(message));

                        return "APROBADO";
                    }  else if(loanPreEvaluation == null){
                        return "APROBADO"; // Fix to not wait for the pre evaluation to run. Just pass as if it was pre approbed
                    }
                }

                throw new ResponseEntityException(AjaxResponse.ok(null));
        }
        return null;
    }

    @Override
    protected void validateForm(QuestionFlowService.Type flowType, Integer id, Question95Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                break;
        }
    }

    @Override
    public void saveData(QuestionFlowService.Type flowType, Integer id, Question95Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                break;
        }
    }

    @Override
    public String getSkippedQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean saveData) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplicationEvaluationsProcess evaluationsProcess = loanApplicationDao.getLoanApplicationEvaluationsProcess(id);
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id,locale);
                if(loanApplication.getProductCategoryId().equals(ProductCategory.VALIDACION_IDENTIDAD)) break;
                // If the preevaluation has alredy run, then go to the next question
                LoanApplicationPreliminaryEvaluation loanPreEvaluation = loanApplicationService.getLastPreliminaryEvaluation(id, locale, null, true);
                if (loanPreEvaluation != null) {
                    if (loanPreEvaluation.getApproved()) {
                        return "APROBADO";
                    } else {
                        return "DESAPROBADO";
                    }
                } else if (evaluationsProcess.getPreEvaluationStatus() != null &&
                        (evaluationsProcess.getPreEvaluationStatus() == LoanApplicationEvaluationsProcess.STATUS_FINISHED ||
                                evaluationsProcess.getPreEvaluationStatus() == LoanApplicationEvaluationsProcess.STATUS_RUNNING_DELAYED)) {
                    return "APROBADO";
                }
                break;
        }
        return null;
    }

    private String messageToShow(LoanApplication loanApplication, LoanApplicationEvaluationsProcess evaluationsProcess, String completed, Locale locale, List<LoanApplicationPreliminaryEvaluation> preEvaluations) throws Exception {
        if (completed == null && loanApplication != null && ((Integer) Entity.BANCO_DEL_SOL).equals(loanApplication.getEntityId())) {

            if (loanApplication != null && ((Integer) Entity.BANCO_DEL_SOL).equals(loanApplication.getEntityId()))
                return "<span><strong>Estamos preparando la oferta.</strong></span>";
            else
                return messageSource.getMessage("questions.block.foundYourOfferWithName", new String[]{personDAO.getPerson(catalogService, Configuration.getDefaultLocale(), loanApplication.getPersonId(), false).getFirstName()}, locale);

        } else if (completed == null && loanApplication != null && ((Integer) Entity.BANBIF).equals(loanApplication.getEntityId())) {
            return messageSource.getMessage("questions.block.foundYourTC", null, locale);
        } else if (completed == null && (loanApplication != null && loanApplication.getProduct() != null && loanApplication.getProduct().getId() == Product.LEADS)) {
            return messageSource.getMessage("questions.block.notYetFoundYourOfferTimeless", null, locale);
        } else if (completed == null && loanApplication != null && loanApplication.getProductCategoryId() == ProductCategory.GATEWAY) {
            return messageSource.getMessage("questions.block.foundYourCollection", null, locale);
        }
        else if (completed == null && loanApplication != null && loanApplication.getProductCategoryId() == ProductCategory.CUENTA_BANCARIA) {
            return messageSource.getMessage("questions.block.foundSavingMessage", null, locale);
        }
        else if (completed == null && loanApplication != null && loanApplication.getProductCategoryId() == ProductCategory.VALIDACION_IDENTIDAD) {
            return messageSource.getMessage("questions.block.foundIdentityValidationOffer", null, locale);
        }
        else if (completed == null && loanApplication != null && loanApplication.getProductCategoryId() == ProductCategory.CONSEJ0) {
            return messageSource.getMessage("questions.block.foundAdviserRole", null, locale);
        }
        else if (completed == null) {
            return (preEvaluations != null && preEvaluations.stream().anyMatch(j -> j.getStatus() != null && j.getStatus().equals('S'))) ?
                    messageSource.getMessage("questions.block.foundYourOffer", null, locale) :
                    messageSource.getMessage("questions.block.notYetFoundYourOffer", null, locale);
        }
        return null;
    }

    public void runPreEvaluationBotIfNoRunYet(int loanApplicationId, Boolean runEvaluation) throws Exception {
        LoanApplicationEvaluationsProcess evaluationsProcess = loanApplicationDao.getLoanApplicationEvaluationsProcess(loanApplicationId);
        if (evaluationsProcess.getReadyForPreEvaluation() == null) {
            loanApplicationDao.updateEvaluationProcessReadyPreEvaluation(loanApplicationId, true);
            if(runEvaluation != null && runEvaluation) loanApplicationDao.updateEvaluationProcessReadyEvaluation(loanApplicationId, true);
            loanApplicationService.runEvaluationBot(loanApplicationId, false);
        }
    }

}

