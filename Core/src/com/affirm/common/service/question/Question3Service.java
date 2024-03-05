package com.affirm.common.service.question;

import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.SelfEvaluationDAO;
import com.affirm.common.model.catalog.LoanApplicationReason;
import com.affirm.common.model.catalog.Product;
import com.affirm.common.model.catalog.ProductCategory;
import com.affirm.common.model.form.Question3Form;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.model.transactional.SelfEvaluation;
import com.affirm.common.service.CatalogService;
import com.affirm.system.configuration.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Service("question3Service")
public class Question3Service extends AbstractQuestionService<Question3Form> {

    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private SelfEvaluationDAO selfEvaluationDao;
    @Autowired
    private CatalogService catalogService;

    @Override
    public Map<String, Object> getViewAttributes(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean fillSavedData, Map<String, Object> params) throws Exception {

        Map<String, Object> attributes = new HashMap<>();
        Question3Form form = new Question3Form();
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, locale);

                // If have selected entity, show only the reasons for the product activated for that entity
                if (loanApplication.getEntityId() != null) {
                    attributes.put("selectedEntity", loanApplication.getEntityId());
                }

                if (fillSavedData) {
                    form.setLoanReasonId(loanApplication.getReason() != null ? loanApplication.getReason().getId() : null);
                }

                attributes.put("isSelfEvaluation", false);
                attributes.put("isEvaluation", true);
                attributes.put("form", form);
                break;
            case SELFEVALUATION:

                if (fillSavedData) {
                    SelfEvaluation selfEvaluation = selfEvaluationDao.getSelfEvaluation(id, locale);
                    form.setLoanReasonId(selfEvaluation.getReason() != null ? selfEvaluation.getReason().getId() : null);
                }

                attributes.put("isSelfEvaluation", true);
                attributes.put("isEvaluation", false);
                attributes.put("form", form);
                break;
        }
        return attributes;
    }

    @Override
    public String getQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Question3Form form) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                return "DEFAULT";
            case SELFEVALUATION:
                LoanApplicationReason reason = catalogService.getLoanApplicationReason(Configuration.getDefaultLocale(), form.getLoanReasonId());
                if (reason.containsProduct(Product.AUTOS))
                    return "AUTO";

                return "DEFAULT";
        }
        return null;
    }

    @Override
    protected void validateForm(QuestionFlowService.Type flowType, Integer id, Question3Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                form.getValidator().validate(locale);
                break;
        }
    }

    @Override
    public void saveData(QuestionFlowService.Type flowType, Integer id, Question3Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                loanApplicationDao.updateReason(id, form.getLoanReasonId());
                break;
            case SELFEVALUATION:
                selfEvaluationDao.updateLoanReason(id, form.getLoanReasonId());
                break;
        }
    }

    @Override
    public String getSkippedQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean saveData) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                if (loanApplication.getProductCategoryId() == ProductCategory.VEHICULO) {
                    if (saveData)
                        loanApplicationDao.updateReason(loanApplication.getId(), LoanApplicationReason.AUTO);
                    return "AUTO";
                }
                break;
        }
        return null;
    }

}

