package com.affirm.common.service.question;

import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PreliminaryEvaluationDAO;
import com.affirm.common.model.catalog.ApprovalValidation;
import com.affirm.common.model.catalog.Entity;
import com.affirm.common.model.catalog.EntityProductParams;
import com.affirm.common.model.form.Question165Form;
import com.affirm.common.model.form.Question174Form;
import com.affirm.common.model.transactional.ApiRestUser;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.model.transactional.LoanApplicationPreliminaryEvaluation;
import com.affirm.common.service.LoanApplicationApprovalValidationService;
import com.affirm.common.util.AjaxResponse;
import com.affirm.common.util.JsonUtil;
import com.affirm.system.configuration.Configuration;
import com.google.gson.Gson;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service("question174Service")
public class Question174Service extends AbstractQuestionService<Question174Form> {

    public static final Integer AZTECA_CREATE_ACCOUNT_OPTION = 1;
    public static final Integer AZTECA_SELECT_ACCOUNT_OPTION = 2;
    public static final Integer AZTECA_SKIPPED_ACCOUNT_OPTION = 3;

    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private LoanApplicationApprovalValidationService loanApplicationApprovalValidationService;

    @Override
    public Map<String, Object> getViewAttributes(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean fillSavedData, Map<String, Object> params) throws Exception {

        Map<String, Object> attributes = new HashMap<>();
        Question165Form form = new Question165Form();
        switch (flowType) {
            case LOANAPPLICATION:
                attributes.put("form", form);
                break;
        }
        return attributes;
    }

    @Override
    public String getQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Question174Form form) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                Integer option = JsonUtil.getIntFromJson(loanApplication.getEntityCustomData(), LoanApplication.EntityCustomDataKeys.AZTECA_DISBUSERMENT_OPTION.getKey(), null);
                if(option != null){
                    if(option.equals(AZTECA_CREATE_ACCOUNT_OPTION) || option.equals(AZTECA_SKIPPED_ACCOUNT_OPTION)) return "CREATE_BANK_ACCOUNT";
                    else if(option.equals(AZTECA_SELECT_ACCOUNT_OPTION)) return "REGISTER_BANK_ACCOUNT";
                }
                return "DEFAULT";
        }
        return null;
    }

    @Override
    protected void validateForm(QuestionFlowService.Type flowType, Integer id, Question174Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                form.getValidator().validate(locale);
                break;
        }
    }

    @Override
    public void saveData(QuestionFlowService.Type flowType, Integer id, Question174Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                if(!Arrays.asList(AZTECA_CREATE_ACCOUNT_OPTION, AZTECA_SELECT_ACCOUNT_OPTION).contains(form.getDisbursementOption())) throw new Exception("Option not available");
                if(form.getDisbursementOption() == AZTECA_CREATE_ACCOUNT_OPTION){
                    loanApplicationApprovalValidationService.validateAndUpdate(id, ApprovalValidation.CCI,true);
                }
                loanApplication.getEntityCustomData().put(LoanApplication.EntityCustomDataKeys.AZTECA_DISBUSERMENT_OPTION.getKey(), form.getDisbursementOption());
                loanApplicationDao.updateEntityCustomData(loanApplication.getId(), loanApplication.getEntityCustomData());
                break;
        }
    }

    @Override
    public String getSkippedQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean saveData) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                boolean canSkip = true;
                if(loanApplication.getOrigin() == LoanApplication.ORIGIN_API_REST && loanApplication.getEntityId() != null && loanApplication.getEntityId() == Entity.AZTECA){
                    if(loanApplication.getAuxData() != null && loanApplication.getAuxData().getApiRestUserId() != null && loanApplication.getAuxData().getApiRestUserId().equals(ApiRestUser.BPEOPLE_USER)){
                        canSkip = true;
                    }
                }
                if(loanApplication.getBanTotalApiData() != null && loanApplication.getBanTotalApiData().getClienteUId() != null){
                    canSkip = true;
                }
                if(canSkip){
                    loanApplication.getEntityCustomData().put(LoanApplication.EntityCustomDataKeys.AZTECA_DISBUSERMENT_OPTION.getKey(), AZTECA_SKIPPED_ACCOUNT_OPTION);
                    loanApplicationDao.updateEntityCustomData(loanApplication.getId(), loanApplication.getEntityCustomData());
                    loanApplicationApprovalValidationService.validateAndUpdate(id, ApprovalValidation.CCI,true);
                    return getQuestionResultToGo(flowType,id,null);
                }
        }
        return null;
    }
}

