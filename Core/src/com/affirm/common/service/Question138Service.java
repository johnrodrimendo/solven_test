package com.affirm.common.service;

import com.affirm.common.dao.CatalogDAO;
import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.model.AfipActivitiy;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.model.transactional.LoanOffer;
import com.affirm.common.model.transactional.PersonDisqualifier;
import com.affirm.common.service.question.AbstractQuestionService;
import com.affirm.common.service.question.QuestionFlowService;
import com.affirm.common.util.FormGeneric;
import com.affirm.common.util.ProcessQuestionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service("question138Service")
public class Question138Service extends AbstractQuestionService<FormGeneric> {

    @Autowired
    private PersonDAO personDao;
    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private EvaluationService evaluationService;
    @Autowired
    private CatalogDAO catalogDao;

    @Override
    public Map<String, Object> getViewAttributes(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean fillSavedData, Map<String, Object> params) throws Exception {

        Map<String, Object> attributes = new HashMap<>();
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, locale);
                List<PersonDisqualifier> personDisqualifierList = personDao.getPersonDisqualifierByPersonId(loanApplication.getPersonId());

                if(loanApplication.getEntityCustomData(LoanApplication.EntityCustomDataKeys.BANCO_DEL_SOL_ACTIVITY_TYPE.getKey()) != null) {
                    String activityId = loanApplication.getEntityCustomData(LoanApplication.EntityCustomDataKeys.BANCO_DEL_SOL_ACTIVITY_TYPE.getKey());
                    AfipActivitiy activity = catalogDao.getAfipActivities().stream().filter(afip -> afip != null && afip.getId() == Integer.valueOf(activityId)).findFirst().orElse(null);
                    attributes.put("personOcupationalInformation", activity != null ?activity.getDescription():null);

                }
                attributes.put("person", personDao.getPerson(catalogService, locale, loanApplication.getPersonId(), false));
                attributes.put("personBankAccount", personDao.getPersonBankAccountInformation(locale, loanApplication.getPersonId()));
                attributes.put("personContactInformation", personDao.getPersonContactInformation(locale, loanApplication.getPersonId()));
                attributes.put("offer", loanApplicationDao.getLoanOffers(loanApplication.getId()).stream().filter(LoanOffer::getSelected).findFirst().orElse(null));
                attributes.put("isPep", personDisqualifierList != null ? personDisqualifierList.stream().filter(p -> PersonDisqualifier.PEP.equals(p.getType())).findFirst().orElse(null).isDisqualified(): false);
                attributes.put("isFatca", personDisqualifierList != null ? personDisqualifierList.stream().filter(p -> PersonDisqualifier.FACTA.equals(p.getType())).findFirst().orElse(null).isDisqualified() : false);
                attributes.put("loanApplication", loanApplication);
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
    public Object customMethod(String path, QuestionFlowService.Type flowType, Integer id, Locale locale, Map<String, Object> params) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                switch (path) {
                    case "modify":
                        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, locale);
                        return ProcessQuestionResponse.goToQuestion(evaluationService.forwardByResult(
                                loanApplication,
                                "MODIFY",
                                null));
                }
                break;
        }
        throw new Exception("No method configured");
    }

    @Override
    public String getSkippedQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean saveData) throws Exception {
        return null;
    }

}

