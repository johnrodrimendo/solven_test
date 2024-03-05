package com.affirm.common.service.question;

import com.affirm.common.dao.*;
import com.affirm.common.model.catalog.Entity;
import com.affirm.common.model.catalog.EntityProductParams;
import com.affirm.common.model.form.Question165Form;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.model.transactional.LoanApplicationPreliminaryEvaluation;
import com.affirm.system.configuration.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service("question165Service")
public class Question165Service extends AbstractQuestionService<Question165Form> {

    public static final Integer AZTECA_AGENCY_PROCESS = 1;
    public static final Integer AZTECA_ONLINE_PROCESS = 2;

    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private PreliminaryEvaluationDAO preliminaryEvaluationDao;

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
    public String getQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Question165Form form) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                if(loanApplication.getBanTotalApiData() != null && loanApplication.getBanTotalApiData().getClienteUId() != null){
                    return "BANTOTAL_CLIENT";
                }
                return "DEFAULT";
        }
        return null;
    }

    @Override
    protected void validateForm(QuestionFlowService.Type flowType, Integer id, Question165Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                form.getValidator().validate(locale);
                break;
        }
    }

    @Override
    public void saveData(QuestionFlowService.Type flowType, Integer id, Question165Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                List<LoanApplicationPreliminaryEvaluation> preEvaluationsApproved = loanApplicationDao.getPreliminaryEvaluations(id, Configuration.getDefaultLocale())
                        .stream().filter(p -> p.getApproved() !=null && p.getApproved()).collect(Collectors.toList());
                // If product is 1, then approve only BAZ_TRAD, el approve only BAZ_ONLINE
                Integer preEvaluationTODissaprove = null;
                if(form.getProduct() == AZTECA_AGENCY_PROCESS){
                    preEvaluationTODissaprove = preEvaluationsApproved.stream().filter(p -> p.getEntityProductParameterId() == EntityProductParams.ENT_PROD_PARAM_AZTECA_ONLINE).findFirst().map(e -> e.getId()).orElse(null);
                }else{
                    preEvaluationTODissaprove = preEvaluationsApproved.stream().filter(p -> p.getEntityProductParameterId() == EntityProductParams.ENT_PROD_PARAM_AZTECA).findFirst().map(e -> e.getId()).orElse(null);
                }
                if(preEvaluationTODissaprove != null){
                    preliminaryEvaluationDao.updateIsApproved(preEvaluationTODissaprove, false);
                    //ONLY SEND IF ENT_PROD_PARAM_AZTECA
                    if(form.getProduct() == 1) {
                        // If its AZTECA, send the report}
                    }
                }
                loanApplication.getEntityCustomData().put(LoanApplication.EntityCustomDataKeys.AZTECA_CREDIT_PROCESS_OPTION.getKey(), form.getProduct());
                loanApplicationDao.updateEntityCustomData(loanApplication.getId(), loanApplication.getEntityCustomData());
                break;
        }
    }

    @Override
    public String getSkippedQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean saveData) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                List<LoanApplicationPreliminaryEvaluation> preEvaluationsApproved = loanApplicationDao.getPreliminaryEvaluations(id, Configuration.getDefaultLocale())
                        .stream().filter(p -> p.getApproved() !=null && p.getApproved()).collect(Collectors.toList());
                if(preEvaluationsApproved.size() == 1){
                    return getQuestionResultToGo(flowType, id, null);
                }
                // Saltar pregunta seleccionando desembolso fisico
                Integer preEvaluationTODissaprove = null;
                // List<LoanApplicationPreliminaryEvaluation> preEvaluationsApproved = loanApplicationDao.getPreliminaryEvaluations(id, Configuration.getDefaultLocale())
                //  .stream().filter(p -> p.getApproved() !=null && p.getApproved()).collect(Collectors.toList());
                preEvaluationTODissaprove = preEvaluationsApproved.stream().filter(p -> p.getEntityProductParameterId() == EntityProductParams.ENT_PROD_PARAM_AZTECA).findFirst().map(e -> e.getId()).orElse(null);
                if(preEvaluationTODissaprove != null){
                    preliminaryEvaluationDao.updateIsApproved(preEvaluationTODissaprove, false);
                    loanApplication.getEntityCustomData().put(LoanApplication.EntityCustomDataKeys.AZTECA_CREDIT_PROCESS_OPTION.getKey(), AZTECA_ONLINE_PROCESS);
                    loanApplicationDao.updateEntityCustomData(loanApplication.getId(), loanApplication.getEntityCustomData());
                    return getQuestionResultToGo(flowType, id, null);
                }
//                if(loanApplication.getEntityCustomData(LoanApplication.EntityCustomDataKeys.BANCO_AZTECA_LANDING_AB_TESTING.getKey()).equals("B")){
//                    // Saltar pregunta seleccionando desembolso fisico
//                    Integer preEvaluationTODissaprove = null;
//                   // List<LoanApplicationPreliminaryEvaluation> preEvaluationsApproved = loanApplicationDao.getPreliminaryEvaluations(id, Configuration.getDefaultLocale())
//                          //  .stream().filter(p -> p.getApproved() !=null && p.getApproved()).collect(Collectors.toList());
//                    preEvaluationTODissaprove = preEvaluationsApproved.stream().filter(p -> p.getEntityProductParameterId() == EntityProductParams.ENT_PROD_PARAM_AZTECA_ONLINE).findFirst().map(e -> e.getId()).orElse(null);
//                    preliminaryEvaluationDao.updateIsApproved(preEvaluationTODissaprove, false);
//                    loanApplication.getEntityCustomData().put(LoanApplication.EntityCustomDataKeys.AZTECA_CREDIT_PROCESS_OPTION.getKey(), AZTECA_AGENCY_PROCESS);
//                    loanApplicationDao.updateEntityCustomData(loanApplication.getId(), loanApplication.getEntityCustomData());
//                    return getQuestionResultToGo(flowType, id, null);
//                }
        }
        return null;
    }
}

