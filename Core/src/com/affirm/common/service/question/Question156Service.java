package com.affirm.common.service.question;

import com.affirm.common.dao.*;
import com.affirm.common.model.catalog.CountryParam;
import com.affirm.common.model.form.Question156Form;
import com.affirm.common.model.form.Question156Form;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.UtilService;
import com.affirm.system.configuration.Configuration;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service("question156Service")
public class Question156Service extends AbstractQuestionService<Question156Form> {

    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    TranslatorDAO translatorDAO;
    @Autowired
    UserDAO userDAO;

    @Override
    public Map<String, Object> getViewAttributes(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean fillSavedData, Map<String, Object> params) throws Exception {
        Map<String, Object> attributes = new HashMap<>();

        switch (flowType) {
            case LOANAPPLICATION:
                attributes.put("form", new Question156Form());
                break;
        }
        return attributes;
    }

    @Override
    public String getQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Question156Form form) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                return "DEFAULT";
        }
        return null;
    }

    @Override
    protected void validateForm(QuestionFlowService.Type flowType, Integer id, Question156Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                form.getValidator().validate(locale);
                break;
        }
    }

    @Override
    public void saveData(QuestionFlowService.Type flowType, Integer id, Question156Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, locale);
                JSONObject prestamypeData = new JSONObject();
                prestamypeData.put("propertyGuarantee", form.getPropertyGuarantee());
                prestamypeData.put("propertySunarp", form.getPropertySunarp());
                prestamypeData.put("propertyNearHill", form.getPropertyNearHill());
                prestamypeData.put("propertyHasSidewalk", form.getPropertyHasSidewalk());

                JSONObject entityData = loanApplication.getEntityCustomData() != null ? loanApplication.getEntityCustomData() : new JSONObject();
                entityData.put(LoanApplication.EntityCustomDataKeys.PRESTAMYPE_DATA.getKey(), prestamypeData);
                loanApplication.setEntityCustomData(entityData);

                loanApplicationDao.updateEntityCustomData(loanApplication.getId(), entityData);
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
}