package com.affirm.common.service.question;

import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.model.catalog.LoanApplicationReason;
import com.affirm.common.model.catalog.ProductCategory;
import com.affirm.common.model.form.Question107Form;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.system.configuration.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Service("question107Service")
public class Question107Service extends AbstractQuestionService<Question107Form> {

    @Autowired
    private LoanApplicationDAO loanApplicationDao;

    @Override
    public Map<String, Object> getViewAttributes(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean fillSavedData, Map<String, Object> params) throws Exception {

        Map<String, Object> attributes = new HashMap<>();
        Question107Form form = new Question107Form();
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                if (fillSavedData) {
                    if(loanApplication.getReason() != null){
                        form.setProductCategory(loanApplication.getReason().getId() == LoanApplicationReason.CONSOLIDAR_CREDITOS ? ProductCategory.CONSOLIDAR_CREDITOS : ProductCategory.CONSUMO);
                    }
                }

                attributes.put("loanApplication", loanApplication);
                attributes.put("form", form);
                break;
        }
        return attributes;
    }

    @Override
    public String getQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Question107Form form) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                if (form.getProductCategory().equals(ProductCategory.CONSOLIDAR_CREDITOS)) {
                    return "CONSOLIDACION";
                } else {
                    return "DEFAULT";
                }
        }
        return null;
    }

    @Override
    protected void validateForm(QuestionFlowService.Type flowType, Integer id, Question107Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                form.getValidator().validate(locale);
                break;
        }
    }

    @Override
    public void saveData(QuestionFlowService.Type flowType, Integer id, Question107Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                if (form.getProductCategory().equals(ProductCategory.CONSOLIDAR_CREDITOS)) {
                    loanApplicationDao.updateReason(id, LoanApplicationReason.CONSOLIDAR_CREDITOS);
                }
                break;
        }
    }

    @Override
    public String getSkippedQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean saveData) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                return "DEFAULT";
        }
        return null;
    }

}

