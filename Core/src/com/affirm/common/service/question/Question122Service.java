package com.affirm.common.service.question;

import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.dao.PreliminaryEvaluationDAO;
import com.affirm.common.model.catalog.LoanApplicationStatus;
import com.affirm.common.model.catalog.Product;
import com.affirm.common.model.catalog.ProductMaxMinParameter;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.EvaluationService;
import com.affirm.common.service.PreliminaryEvaluationService;
import com.affirm.common.util.FormGeneric;
import com.affirm.common.util.ProcessQuestionResponse;
import com.affirm.system.configuration.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service("question122Service")
public class Question122Service extends AbstractQuestionService<FormGeneric> {

    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private PersonDAO personDao;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private EvaluationService evaluationService;
    @Autowired
    private PreliminaryEvaluationService preliminaryEvaluationService;
    @Autowired
    private PreliminaryEvaluationDAO preliminaryEvaluationDao;

    @Override
    public Map<String, Object> getViewAttributes(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean fillSavedData, Map<String, Object> params) throws Exception {

        Map<String, Object> attributes = new HashMap<>();
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, locale);
                if(!loanApplication.getStatus().getId().equals(LoanApplicationStatus.CROSS_SELLING_OFFER)){
                    loanApplicationDao.updateLoanApplicationStatus(loanApplication.getId(), LoanApplicationStatus.CROSS_SELLING_OFFER, null);
                    loanApplication = loanApplicationDao.getLoanApplication(id, locale);
                }
                Person person = personDao.getPerson(catalogService, locale, loanApplication.getPersonId(), false);
                Product guaranteedProduct = catalogService.getProduct(Product.GUARANTEED);
                ProductMaxMinParameter maxMinParameter = guaranteedProduct.getProductParams(loanApplication.getCountryId());
                attributes.put("personFirstName", person.getFirstName());
                attributes.put("loanApplication", loanApplication);
                attributes.put("maxMinParameter", maxMinParameter);
                break;
        }
        return attributes;
    }

    @Override
    public String getQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, FormGeneric form) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                return "GUARANTEED";
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
                EntityProductEvaluationsProcess guaranteedProcess = getAskForGuaranteed(id);
                if (guaranteedProcess == null)
                    return "DEFAULT";
                break;
        }
        return null;
    }

    @Override
    public Object customMethod(String path, QuestionFlowService.Type flowType, Integer id, Locale locale, Map<String, Object> params) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                switch (path) {
                    case "notInterested":

                        // Call the preliminary evaluation for the guaranteed pre-evaluations.
                        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                        List<LoanApplicationPreliminaryEvaluation> preEvaluations = preliminaryEvaluationDao.getPreliminaryEvaluationsWithHardFilters(id, Configuration.getDefaultLocale());
                        Map<String, Object> cachedSources = new HashMap<>();
                        for(LoanApplicationPreliminaryEvaluation preEvaluation :
                                preEvaluations.stream().filter(p -> p.getApproved() == null && p.getProduct().getId().equals(Product.GUARANTEED)).collect(Collectors.toList())){
                            preliminaryEvaluationService.runPreliminaryEvaluation(preEvaluation, loanApplication, cachedSources);
                        }

                        return ProcessQuestionResponse.goToQuestion(evaluationService.forwardByResult(
                                loanApplication,
                                "DEFAULT",
                                ProcessQuestionSequence.TYPE_SKIPPED,
                                null));
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

