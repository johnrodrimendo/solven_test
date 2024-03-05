package com.affirm.common.service.question;

import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.model.catalog.*;
import com.affirm.common.model.form.Question121Form;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.EvaluationService;
import com.affirm.common.service.WebscrapperService;
import com.affirm.common.util.AjaxResponse;
import com.affirm.common.util.ProcessQuestionResponse;
import com.affirm.system.configuration.Configuration;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("question124Service")
public class Question124Service extends AbstractQuestionService<Question121Form> {

    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    WebscrapperService webscrapperService;

    @Autowired
    private EvaluationService evaluationService;

    @Override
    public Map<String, Object> getViewAttributes(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean fillSavedData, Map<String, Object> params) throws Exception {
        Map<String, Object> attributes = new HashMap<>();
        Question121Form form = new Question121Form();
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                List<MaintainedCarBrand> a = catalogService.getMaintainedCarBrands();
                List<Integer> yearList = catalogService.getGuaranteedVehicleYears();
                Integer minYear = Collections.min(yearList);

                List<String> yearListString = new ArrayList<>();

                for (Integer year : yearList) {
                    if ((year).equals(minYear)) {
                        yearListString.add(minYear + " o menos");
                    } else {
                        yearListString.add(String.valueOf(year));
                    }

                }

                attributes.put("yearList", yearListString);
                List<String> modelList = catalogService.getGuaranteedVehicleModels();
                // attributes.put("modelList", modelList);
                attributes.put("loanApplication", loanApplication);
                attributes.put("form", form);
                attributes.put("questionId", 124);
                break;
        }
        return attributes;
    }

    @Override
    public String getQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Question121Form form) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                return "DEFAULT";
        }
        return null;
    }

    @Override
    protected void validateForm(QuestionFlowService.Type flowType, Integer id, Question121Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                form.getValidator().validate(locale);
                break;
        }
    }

    @Override
    public void saveData(QuestionFlowService.Type flowType, Integer id, Question121Form form, Locale locale) throws Exception {
        GuaranteedVehicle guaranteedVehicle = catalogService.getGuaranteedVehicle(
                form.getBrand(), form.getYear(), form.getMileage(), form.getModel());
        switch (flowType) {
            case LOANAPPLICATION:

                if (guaranteedVehicle != null) {
                    loanApplicationDao.registerGuaranteedVehicle(id, guaranteedVehicle.getId(), form.getPlate());

                    LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                    EntityBranding entityBranding = null;
                    if (loanApplication.getEntityId() != null) {
                        EntityBranding entityBrandingAux = catalogService.getEntityBranding(loanApplication.getEntityId());
                        if (entityBrandingAux.getBranded())
                            entityBranding = entityBrandingAux;
                    }
                    if ((entityBranding != null && entityBranding.getEntity().getId() == Entity.ACCESO) || (loanApplication.getProduct() != null && loanApplication.getProduct().getId() == Product.GUARANTEED)) {
                        double maxProductAmount = catalogService.getProduct(Product.GUARANTEED).getProductParams(CountryParam.COUNTRY_PERU).getMaxAmount();
                        double amountToShow = loanApplication.getGuaranteedVehiclePrice() > maxProductAmount ? maxProductAmount : loanApplication.getGuaranteedVehiclePrice();
                        loanApplicationDao.updateAmount(loanApplication.getId(), (int) amountToShow);
                    }
                    /*if(loanApplication.getCountryId()  == CountryParam.COUNTRY_PERU){
                        webscrapperService.callSatPlateBot(loanApplication.getUserId(), form.getPlate());
                        webscrapperService.callSoatBot(loanApplication.getUserId(), form.getPlate());
                    }*/
                }
                break;
        }
    }

    @Override
    public String getSkippedQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean saveData) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, locale);
                boolean showQuestion = false;
                if (loanApplication.getEntityId() != null && catalogService.getEntityProductsByEntity(loanApplication.getEntityId()).stream().anyMatch(ep -> ep.getProduct().getId().equals(Product.GUARANTEED)))
                    showQuestion = true;
                if (loanApplication.getProduct() != null && loanApplication.getProduct().getId().equals(Product.GUARANTEED))
                    showQuestion = true;

                if (!showQuestion)
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
                    case "brand":
                        return AjaxResponse.ok(new Gson().toJson(catalogService.getBrands(
                                (String) params.get("model"))));
                    case "modelyear":
                        return AjaxResponse.ok(new Gson().toJson(catalogService.getYears(
                                (String) params.get("model"))));
                    case "model":
                        return AjaxResponse.ok(new Gson().toJson(catalogService.getGuaranteedVehicleModels(
                                (Integer) params.get("brandId"))));
                    case "year":
                        return AjaxResponse.ok(new Gson().toJson(catalogService.getGuaranteedVehicleYears(
                                (Integer) params.get("brandId"),
                                (String) params.get("model"))));
                    case "brandByYear":
                        return AjaxResponse.ok(new Gson().toJson(catalogService.getBrandsByYear(
                                (Integer) params.get("year"))));
                    case "modelByYear":
                        return AjaxResponse.ok(new Gson().toJson(catalogService.getModelByYears(
                                (Integer) params.get("year"))));
                    case "mileage":
                        return AjaxResponse.ok(new Gson().toJson(catalogService.getGuaranteedVehicleMileages(
                                (Integer) params.get("brandId"),
                                (String) params.get("model"),
                                (Integer) params.get("year"))));

                    case "notInterested":
                        // Call the preliminary evaluation for the guaranteed pre-evaluations.

                        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                        return ProcessQuestionResponse.goToQuestion(evaluationService.forwardByResult(
                                loanApplication,
                                "DEFAULT",
                                null));
                }
                break;
        }
        throw new Exception("No method configured");
    }
}
