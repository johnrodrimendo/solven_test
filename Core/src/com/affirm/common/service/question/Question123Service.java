package com.affirm.common.service.question;

import com.affirm.common.dao.JsonResolverDAO;
import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.dao.WebServiceDAO;
import com.affirm.common.model.catalog.Entity;
import com.affirm.common.model.catalog.EntityWebService;
import com.affirm.common.model.catalog.LoanApplicationStatus;
import com.affirm.common.model.catalog.Product;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.EvaluationService;
import com.affirm.common.util.AjaxResponse;
import com.affirm.common.util.FormGeneric;
import com.affirm.common.util.GoToNextQuestionException;
import com.affirm.common.util.ProcessQuestionResponse;
import com.affirm.system.configuration.Configuration;
import com.affirm.wenance.WenanceServiceCall;
import com.affirm.wenance.model.LeadCreateResponse;
import com.affirm.wenance.model.LeadScoreResponse;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class Question123Service extends AbstractQuestionService<FormGeneric> {

    private static final Logger logger = Logger.getLogger(Question123Service.class);

    private final LoanApplicationDAO loanApplicationDao;
    private final PersonDAO personDao;
    private final CatalogService catalogService;
    private final EvaluationService evaluationService;
    private final WenanceServiceCall wenanceServiceCall;
    private final WebServiceDAO webServiceDAO;

    public Question123Service(LoanApplicationDAO loanApplicationDao, PersonDAO personDao, CatalogService catalogService, EvaluationService evaluationService, WenanceServiceCall wenanceServiceCall, WebServiceDAO webServiceDAO) {
        this.loanApplicationDao = loanApplicationDao;
        this.personDao = personDao;
        this.catalogService = catalogService;
        this.evaluationService = evaluationService;
        this.wenanceServiceCall = wenanceServiceCall;
        this.webServiceDAO = webServiceDAO;
    }

    @Override
    public Map<String, Object> getViewAttributes(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean fillSavedData, Map<String, Object> params) throws Exception {

        Map<String, Object> attributes = new HashMap<>();
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, locale);
                Person person = personDao.getPerson(catalogService, locale, loanApplication.getPersonId(), false);
                List<EntityWebServiceLog> webServiceScoreLogList = webServiceDAO.getEntityWebServiceLog(loanApplication.getId(), EntityWebService.WENANCE_SCORE);
                List<EntityWebServiceLog> webServiceResponseLogList = webServiceDAO.getEntityWebServiceLog(loanApplication.getId(), EntityWebService.WENANCE_CREAR_LEAD);
                EntityWebServiceLog<JSONObject> webServiceScoreLog = webServiceScoreLogList != null ? webServiceScoreLogList.stream().findFirst().orElse(null) : null;
                EntityWebServiceLog<JSONObject> webServiceResponseLog = webServiceResponseLogList != null ? webServiceResponseLogList.stream().findFirst().orElse(null) : null;
                LeadScoreResponse previousCalledLeadScoreResponse = null;
                LeadCreateResponse previousCalledLeadCreateResponse = null;

                if (webServiceScoreLog != null) {
                    previousCalledLeadScoreResponse = new LeadScoreResponse();
                    previousCalledLeadScoreResponse.fillFromJson(new JSONObject(webServiceScoreLog.getResponse()));
                }

                if (webServiceResponseLog != null) {
                    previousCalledLeadCreateResponse = new LeadCreateResponse();
                    previousCalledLeadCreateResponse.fillFromJson(new JSONObject(webServiceResponseLog.getResponse()));
                }

                if (previousCalledLeadScoreResponse == null) {
                    try {
                        wenanceServiceCall.getLeadScore(loanApplication);
                        LeadCreateResponse leadCreateResponse = wenanceServiceCall.createLead(loanApplication, (ZoneId) params.get("zoneId"));

                        if (!loanApplication.getStatus().getId().equals(LoanApplicationStatus.CROSS_SELLING_OFFER)) {
                            loanApplicationDao.updateLoanApplicationStatus(loanApplication.getId(), LoanApplicationStatus.CROSS_SELLING_OFFER, null);
                            loanApplication = loanApplicationDao.getLoanApplication(id, locale);
                        }

                        attributes.put("url_wenance", leadCreateResponse.getRedirectUrl());
                    } catch (Exception e) {
                        logger.error(e.getMessage());

                        loanApplicationDao.updateLoanApplicationStatus(loanApplication.getId(), LoanApplicationStatus.REJECTED_AUTOMATIC, null);

                        throw new GoToNextQuestionException("NO_ME_INTERESA", ProcessQuestionSequence.TYPE_SKIPPED);
                    }
                } else {
                    attributes.put("url_wenance", previousCalledLeadCreateResponse != null ? previousCalledLeadCreateResponse.getRedirectUrl() : null);
                }

                attributes.put("notInterested", (loanApplication.getOfferRejectionId() != null && loanApplication.getStatus().getId() == LoanApplicationStatus.REJECTED));
                attributes.put("personFirstName", person.getFirstName());
                attributes.put("loanApplication", loanApplication);
                attributes.put("reason", loanApplication.getOfferRejectionId() != null ? 'R' : 'A');// R - REJECTED | A - APPROVED

                break;
        }
        return attributes;
    }

    @Override
    public String getQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, FormGeneric form) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                return "DEFAULT";
        }
        return null;
    }

    @Override
    protected void validateForm(QuestionFlowService.Type flowType, Integer id, FormGeneric form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                break;
        }
    }

    @Override
    public void saveData(QuestionFlowService.Type flowType, Integer id, FormGeneric form, Locale locale) throws Exception {
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
                    case "notInterested": {
                        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());

                        if (loanApplication.getOfferRejectionId() != null) {
                            return AjaxResponse.ok("");
                        }

                        List<LoanApplicationEvaluation> evaluations = loanApplicationDao.getEvaluations(loanApplication.getId(), locale, JsonResolverDAO.EVALUATION_FOLLOWER_DB);

                        if (!evaluations.isEmpty()) {
                            Calendar expirationDate = Calendar.getInstance();
                            expirationDate.add(Calendar.DATE, 1);

                            List<LoanApplicationEvaluation> wenanceEvaluations = evaluations.stream().filter(e -> e.getEntityId() == Entity.WENANCE && e.getApproved()).collect(Collectors.toList());
                            for (LoanApplicationEvaluation evaluation : wenanceEvaluations)
                                loanApplicationDao.updateEvaluationStep(evaluation.getId(), 69, expirationDate.getTime());
                        } else {
                            List<LoanApplicationPreliminaryEvaluation> preevaluations = loanApplicationDao.getPreliminaryEvaluations(loanApplication.getId(), locale, JsonResolverDAO.EVALUATION_FOLLOWER_DB);

                            if (!preevaluations.isEmpty()) {
                                Calendar expirationDate = Calendar.getInstance();
                                expirationDate.add(Calendar.DATE, 1);

                                List<LoanApplicationPreliminaryEvaluation> wenanceEvaluations = preevaluations.stream().filter(e -> e.getEntityId() == Entity.WENANCE && e.getApproved()).collect(Collectors.toList());
                                for (LoanApplicationPreliminaryEvaluation evaluation : wenanceEvaluations)
                                    loanApplicationDao.updatePreliminaryEvaluationStep(evaluation.getId(), 55, "bd.preliminaryEvaluation.closedPlatform", null, expirationDate.getTime());
                            }
                        }

                        return ProcessQuestionResponse.goToQuestion(evaluationService.forwardByResult(
                                loanApplication,
                                "NO_ME_INTERESA",
                                ProcessQuestionSequence.TYPE_SKIPPED,
                                null));
                    }
                    case "redirectToWenance":
                        loanApplicationDao.updateLoanApplicationStatus(id, LoanApplicationStatus.LEAD_REFERRED, null);

                        List<ReferredLead> referredLeads = loanApplicationDao.getReferredLeadByLoanApplicationId(id);
                        if (referredLeads != null) {
                            List<ReferredLead> wenanceReferred = referredLeads.stream()
                                    .filter(item -> item.getEntity().getId() == Entity.WENANCE)
                                    .collect(Collectors.toList());
                            //verify if already exists a wenance referred
                            if (wenanceReferred == null) {
                                loanApplicationDao.registerReferredLead(id, Entity.WENANCE);
                            }
                        } else {
                            loanApplicationDao.registerReferredLead(id, Entity.WENANCE);
                        }
                        return AjaxResponse.ok("");
                }
                break;
        }
        throw new Exception("No method configured");
    }

    public EntityProductEvaluationsProcess getAskForGuaranteed(int loanApplicationId) {
        List<EntityProductEvaluationsProcess> entitiesProcess = loanApplicationDao.getEntityProductEvaluationProceses(loanApplicationId);
        EntityProductEvaluationsProcess entityProcess = entitiesProcess
                .stream()
                .filter(p -> p.getProductId().equals(Product.GUARANTEED) && !p.getReadyForProcess())
                .findFirst().orElse(null);
        return entityProcess;
    }
}
