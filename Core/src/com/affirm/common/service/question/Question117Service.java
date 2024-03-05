package com.affirm.common.service.question;

import com.affirm.common.dao.JsonResolverDAO;
import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.model.catalog.*;
import com.affirm.common.model.form.Question117Form;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.model.transactional.LoanApplicationEvaluation;
import com.affirm.common.model.transactional.Person;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.EvaluationService;
import com.affirm.common.service.LoanApplicationService;
import com.affirm.common.util.AjaxResponse;
import com.affirm.common.util.JsonUtil;
import com.affirm.common.util.ProcessQuestionResponse;
import com.affirm.common.util.ResponseEntityException;
import com.affirm.system.configuration.Configuration;
import org.apache.commons.lang3.ArrayUtils;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service("question117Service")
public class Question117Service extends AbstractQuestionService<Question117Form> {

    public static final String GO_TO_OFFER_TOKEN_KEY = "goToOffer";

    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private PersonDAO personDao;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private EvaluationService evaluationService;
    @Autowired
    private LoanApplicationService loanApplicationService;

    @Override
    public Map<String, Object> getViewAttributes(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean fillSavedData, Map<String, Object> params) throws Exception {
        Map<String, Object> attributes = new HashMap<>();
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, locale);
                if(params != null && params.containsKey(GO_TO_OFFER_TOKEN_KEY)){
                    if((Boolean)params.get(GO_TO_OFFER_TOKEN_KEY)){
                        ProcessQuestionResponse.goToQuestion(evaluationService.forwardByResult(loanApplication,"BACK_TO_OFFERS",null));
                        throw new ResponseEntityException(AjaxResponse.redirect(loanApplicationService.generateLoanApplicationLinkEntity(loanApplication)));
                    }
                }

                Question117Form form = new Question117Form();
                String entityShort = null;
                Integer entityId = loanApplication.getEntityId();

                if (entityId != null) {
                    EntityBranding entityBranding = catalogService.getEntityBranding(entityId);
                    entityShort = entityBranding != null && entityBranding.getBranded() ? entityBranding.getEntity().getShortName() : Configuration.APP_NAME;
                    if(entityId == Entity.AZTECA) entityShort = entityBranding != null && entityBranding.getBranded() ? entityBranding.getEntity().getFullName() : Configuration.APP_NAME;
                }
                Person person = personDao.getPerson(catalogService, locale, loanApplication.getPersonId(), false);
                List<OfferRejectionReason> offerRejectionReasons = new ArrayList<>();
                List<Integer> offerRejectionReasonsId = null;
                ProcessQuestion currentQuestion = evaluationService.getQuestionFromEvaluationProcess(loanApplication, null, ProcessQuestion.Question.Constants.OFFER_REJECTION_REASON, null, null);
                if (currentQuestion.getConfiguration() != null) {
                    JSONArray offerRejectionReasonsIdJsonArray = JsonUtil.getJsonArrayFromJson(currentQuestion.getConfiguration(), "offerRejectionReasons" , null);
                    if(offerRejectionReasonsIdJsonArray != null && offerRejectionReasonsIdJsonArray.length() > 0) offerRejectionReasonsId = JsonUtil.getListFromJsonArray(offerRejectionReasonsIdJsonArray, (arr, i) -> arr.getInt(i));
                }

                if(offerRejectionReasonsId != null && !offerRejectionReasonsId.isEmpty()){
                    List<OfferRejectionReason> finalOfferRejectionReasons = offerRejectionReasons;
                    offerRejectionReasonsId.forEach(e ->
                            {
                                try {
                                    catalogService.getOfferRejectionReasons()
                                            .stream().filter(o -> o.getId().equals(e))
                                            .findFirst()
                                            .ifPresent(finalOfferRejectionReasons::add);
                                } catch (Exception exception) {
                                    exception.printStackTrace();
                                }
                            }
                    );
                }
                else {
                    offerRejectionReasons =  catalogService.getOfferRejectionReasonsByProductCategory(catalogService.getOfferRejectionReasons(loanApplication.getSelectedEntityId()), loanApplication.getProductCategoryId());
                }
                attributes.put("offerRejectionReasons", offerRejectionReasons);
                attributes.put("personFirstName", person.getFirstName());
                attributes.put("entityShort", entityShort);
                attributes.put("reasonGiven", loanApplication.getOfferRejectionId() != null);
                attributes.put("form", form);
                attributes.put("loanApplication", loanApplication);
                attributes.put("showAgent", true);
                break;
        }
        return attributes;
    }

    @Override
    public String getQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Question117Form form) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                if (loanApplication.getCountryId() == CountryParam.COUNTRY_ARGENTINA) {
                    if (ArrayUtils.contains(Configuration.wenanceRejectOfferIds, form.getOfferRejectionReason())) {
                        List<LoanApplicationEvaluation> evaluations = loanApplicationDao.getEvaluations(id, Configuration.getDefaultLocale(), JsonResolverDAO.EVALUATION_FOLLOWER_DB);
                        List<LoanApplicationEvaluation> wenanceEvaluations = evaluations.stream().filter(e -> e.getEntityId() == Entity.WENANCE && e.getApproved()).collect(Collectors.toList());
                        if (wenanceEvaluations != null)
                            return  "WENANCE";
                    }
                }
                return "DEFAULT";
        }
        return null;
    }

    @Override
    protected void validateForm(QuestionFlowService.Type flowType, Integer id, Question117Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                form.getValidator().validate(locale);
                break;
        }
    }

    @Override
    public void saveData(QuestionFlowService.Type flowType, Integer id, Question117Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                break;
        }
    }

    @Override
    public String getSkippedQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean saveData) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                break;
        }
        return null;
    }

    @Override
    public Object customMethod(String path, QuestionFlowService.Type flowType, Integer id, Locale locale, Map<String, Object> params) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                switch (path) {
                    case "backToOffers":
                        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                        return ProcessQuestionResponse.goToQuestion(evaluationService.forwardByResult(
                                loanApplication,
                                "BACK_TO_OFFERS",
                                null));
                }
                break;
        }
        throw new Exception("No method configured");
    }
}
