package com.affirm.common.service.question;

import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.model.catalog.ProcessQuestion;
import com.affirm.common.model.catalog.ProductCategory;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.CountryContextService;
import com.affirm.common.service.EvaluationService;
import com.affirm.common.service.UtilService;
import com.affirm.common.util.AjaxResponse;
import com.affirm.common.util.CryptoUtil;
import com.affirm.common.util.FormGeneric;
import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Service("question20Service")
public class Question20Service extends AbstractQuestionService<FormGeneric> {

    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private EvaluationService evaluationService;
    @Autowired
    private CountryContextService countryContextService;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private UtilService utilService;
    @Autowired
    private PersonDAO personDao;

    @Override
    public Map<String, Object> getViewAttributes(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean fillSavedData, Map<String, Object> params) throws Exception {

        Map<String, Object> attributes = new HashMap<>();
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = null;
                if(id != null)
                    loanApplication = loanApplicationDao.getLoanApplication(id, locale);

                Integer categoryId = loanApplication != null ? loanApplication.getProductCategoryId() :  ProductCategory.GET_ID_BY_URL((String)params.get("categoryUrl"));
                HttpServletRequest request = loanApplication != null ? null : (HttpServletRequest)params.get("request");
                Integer agent = loanApplication != null ? null : (Integer)params.get("agent");
                String externalParams = loanApplication != null ? null : (String)params.get("externalParams");

                ProcessQuestion currentQuestion;
                if (loanApplication != null) {
                    currentQuestion = evaluationService.getQuestionFromEvaluationProcess(
                            loanApplication,
                            categoryId,
                            20,
                            null,
                            null);

                    if(loanApplication.getVehicle() != null)
                        attributes.put("vehicle", loanApplication.getVehicle().getFullVehicleName());
                    attributes.put("agent", loanApplication.getAgent());
                    attributes.put("category", loanApplication.getProductCategoryId());
                } else {
                    currentQuestion = evaluationService.getQuestionFromEvaluationProcess(
                            null,
                            categoryId,
                            20,
                            countryContextService.getCountryParamsByRequest(request).getId(),
                            request);

                    attributes.put("agent", catalogService.getAgent(agent));
                    attributes.put("category", categoryId);
                    attributes.put("vehicle", "");
                    if (externalParams != null && !externalParams.trim().isEmpty()) {
                        JSONObject jsonExternalParams = new JSONObject(CryptoUtil.decrypt(externalParams));
                        Integer vehicleId = JsonUtil.getIntFromJson(jsonExternalParams, "vehicleId", null);
                        if(vehicleId != null)
                            attributes.put("vehicle", catalogService.getVehicle(vehicleId, locale).getFullVehicleName());
                    }
                }

                if (currentQuestion != null && currentQuestion.getConfiguration() != null) {
                    attributes.put("showSolvenMessage", JsonUtil.getBooleanFromJson(currentQuestion.getConfiguration(), "showSolvenMessage", true));
                    attributes.put("solvenMessage", JsonUtil.getStringFromJson(currentQuestion.getConfiguration(), "solvenMessage", null));
                } else
                    attributes.put("showSolvenMessage", true);


                if(request != null){
                    boolean isInCorrectCountry = utilService.isInCorrectCountry(request);
                    attributes.put("isInCorrectCountry", isInCorrectCountry);
                    if(!isInCorrectCountry)
                        attributes.put("redirectUrl", utilService.getRedirectUrl());
                }else{
                    attributes.put("isInCorrectCountry", true);
                }

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
                    case "illbeback":
                        personDao.registerAbandonedApplication((String) params.get("email"));
                        return AjaxResponse.ok(null);
                }
                break;
        }
        throw new Exception("No method configured");
    }

}

