package com.affirm.common.service.question;

import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.SelfEvaluationDAO;
import com.affirm.common.model.catalog.*;
import com.affirm.common.model.form.Question6Form;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.model.transactional.SelfEvaluation;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.ProductService;
import com.affirm.system.configuration.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Service("question6Service")
public class Question6Service extends AbstractQuestionService<Question6Form> {

    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private ProductService productService;
    @Autowired
    private SelfEvaluationDAO selfEvaluationDao;

    @Override
    public Map<String, Object> getViewAttributes(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean fillSavedData, Map<String, Object> params) throws Exception {

        Map<String, Object> attributes = new HashMap<>();
        Question6Form form = new Question6Form();
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, locale);
                EntityBranding entityBranding = null;
                if (loanApplication.getEntityId() != null) {
                    EntityBranding entityBrandingAux = catalogService.getEntityBranding(loanApplication.getEntityId());
                    if (entityBrandingAux.getBranded())
                        entityBranding = entityBrandingAux;
                }

                ((Question6Form.Validator)form.getValidator()).installments.setMaxValue(getMaxInstalments(loanApplication, entityBranding));
                ((Question6Form.Validator)form.getValidator()).installments.setMinValue(getMinInstalments(loanApplication, entityBranding));
                ((Question6Form.Validator)form.getValidator()).amount.setMaxValue(getMaxAmount(loanApplication, entityBranding));
                ((Question6Form.Validator)form.getValidator()).amount.setMinValue(getMinAmount(loanApplication, entityBranding));

                if (fillSavedData) {
                    if (loanApplication.getAmount() != null)
                        form.setAmount(loanApplication.getAmount());
                    if (loanApplication.getInstallments() != null)
                        form.setInstallments(loanApplication.getInstallments());
                }

                attributes.put("maxInstallments", getMaxInstalments(loanApplication, entityBranding));
                attributes.put("minInstallments", getMinInstalments(loanApplication, entityBranding));
                attributes.put("maxAmount", getMaxAmount(loanApplication, entityBranding));
                attributes.put("minAmount", getMinAmount(loanApplication, entityBranding));
                attributes.put("loanApplication", loanApplication);
                attributes.put("form", form);
                break;
            case SELFEVALUATION:

                SelfEvaluation selfEvaluation = selfEvaluationDao.getSelfEvaluation(id, locale);
                if (fillSavedData) {
                    if (selfEvaluation.getAmount() != null)
                        form.setAmount(selfEvaluation.getAmount().intValue());
                    if (selfEvaluation.getInstallments() != null)
                        form.setInstallments(selfEvaluation.getInstallments());
                }
                attributes.put("selfEvaluation", selfEvaluation);
                attributes.put("form", form);
                break;
        }
        return attributes;
    }

    @Override
    public String getQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Question6Form form) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
            case SELFEVALUATION:
                return "DEFAULT";
        }
        return null;
    }

    @Override
    protected void validateForm(QuestionFlowService.Type flowType, Integer id, Question6Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, locale);
                EntityBranding entityBranding = null;
                if (loanApplication.getEntityId() != null) {
                    EntityBranding entityBrandingAux = catalogService.getEntityBranding(loanApplication.getEntityId());
                    if (entityBrandingAux.getBranded())
                        entityBranding = entityBrandingAux;
                }

                ((Question6Form.Validator)form.getValidator()).installments.setMaxValue(getMaxInstalments(loanApplication, entityBranding));
                ((Question6Form.Validator)form.getValidator()).installments.setMinValue(getMinInstalments(loanApplication, entityBranding));
                ((Question6Form.Validator)form.getValidator()).amount.setMaxValue(getMaxAmount(loanApplication, entityBranding));
                ((Question6Form.Validator)form.getValidator()).amount.setMinValue(getMinAmount(loanApplication, entityBranding));

                form.getValidator().validate(locale);
                break;
            case SELFEVALUATION:
                SelfEvaluation selfEvaluation = selfEvaluationDao.getSelfEvaluation(id, Configuration.getDefaultLocale());
                ((Question6Form.Validator)form.getValidator()).installments.setMaxValue(productService.getMaxInstalments(ProductCategory.CONSUMO, selfEvaluation.getCountryParam().getId()));
                ((Question6Form.Validator)form.getValidator()).installments.setMinValue(productService.getMinInstalments(ProductCategory.CONSUMO, selfEvaluation.getCountryParam().getId()));
                ((Question6Form.Validator)form.getValidator()).amount.setMaxValue(productService.getMaxAmount(ProductCategory.CONSUMO, selfEvaluation.getCountryParam().getId()));
                ((Question6Form.Validator)form.getValidator()).amount.setMinValue(productService.getMinAmount(ProductCategory.CONSUMO, selfEvaluation.getCountryParam().getId()));
                form.getValidator().validate(locale);
                break;
        }
    }

    @Override
    public void saveData(QuestionFlowService.Type flowType, Integer id, Question6Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                loanApplicationDao.updateAmount(id, form.getAmount());
                loanApplicationDao.updateInstallments(id, form.getInstallments());
                break;
            case SELFEVALUATION:
                selfEvaluationDao.updateAmount(id, form.getAmount());
                selfEvaluationDao.updateInstallments(id, form.getInstallments());
                break;
        }
    }

    @Override
    public String getSkippedQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean saveData) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, locale);
                EntityBranding entityBranding = null;

                if (loanApplication.getEntityId() != null) {
                    EntityBranding entityBrandingAux = catalogService.getEntityBranding(loanApplication.getEntityId());
                    if (entityBrandingAux.getBranded())
                        entityBranding = entityBrandingAux;
                }

                if((entityBranding != null && entityBranding.getEntity().getId() == Entity.ACCESO) || (loanApplication.getProduct() != null && loanApplication.getProduct().getId() == Product.GUARANTEED)) {
                    loanApplicationDao.updateAmount(loanApplication.getId(), 15000);

                    int maxInstalments = catalogService.getProduct(Product.GUARANTEED).getProductParams(CountryParam.COUNTRY_PERU).getMaxInstallments();
                    loanApplicationDao.updateInstallments(loanApplication.getId(), maxInstalments);
                    return "DEFAULT";
                }
            case SELFEVALUATION:
                break;
        }
        return null;
    }

    private Integer getMaxInstalments(LoanApplication loanApplication, EntityBranding entityBranding) throws Exception {
        if (entityBranding != null) {
            return productService.getMaxInstalmentsEntity(loanApplication.getProductCategoryId(), loanApplication.getCountryId(), entityBranding.getEntity().getId());
        }

        return productService.getMaxInstalments(loanApplication.getProductCategoryId(), loanApplication.getCountryId());
    }

    private Integer getMinInstalments(LoanApplication loanApplication, EntityBranding entityBranding) throws Exception {
        if (entityBranding != null) {
            return productService.getMinInstalmentsEntity(loanApplication.getProductCategoryId(), loanApplication.getCountryId(), entityBranding.getEntity().getId());
        }

        return productService.getMinInstalments(loanApplication.getProductCategoryId(), loanApplication.getCountryId());
    }

    private Integer getMaxAmount(LoanApplication loanApplication, EntityBranding entityBranding) throws Exception {
        if (entityBranding != null) {
            return productService.getMaxAmountEntity(loanApplication.getProductCategoryId(), loanApplication.getCountryId(), entityBranding.getEntity().getId());
        }

        return productService.getMaxAmount(loanApplication.getProductCategoryId(), loanApplication.getCountryId());
    }

    private Integer getMinAmount(LoanApplication loanApplication, EntityBranding entityBranding) throws Exception {
        if (entityBranding != null) {
            return productService.getMinAmountEntity(loanApplication.getProductCategoryId(), loanApplication.getCountryId(), entityBranding.getEntity().getId());
        }

        return productService.getMinAmount(loanApplication.getProductCategoryId(), loanApplication.getCountryId());
    }

}

