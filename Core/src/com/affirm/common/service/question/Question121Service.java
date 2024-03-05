package com.affirm.common.service.question;

import com.affirm.common.dao.EntityProductEvaluationProcessDAO;
import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PreliminaryEvaluationDAO;
import com.affirm.common.model.catalog.*;
import com.affirm.common.model.form.Question121Form;
import com.affirm.common.model.transactional.EntityProductEvaluationsProcess;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.model.transactional.LoanApplicationPreliminaryEvaluation;
import com.affirm.common.service.*;
import com.affirm.common.util.AjaxResponse;
import com.affirm.common.util.ProcessQuestionResponse;
import com.affirm.system.configuration.Configuration;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service("question121Service")
public class Question121Service extends AbstractQuestionService<Question121Form> {

    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private EntityProductEvaluationProcessDAO entityProductEvaluationProcessDao;
    @Autowired
    private Question122Service question122Service;
    @Autowired
    private LoanApplicationService loanApplicationService;
    @Autowired
    private EvaluationService evaluationService;
    @Autowired
    WebscrapperService webscrapperService;
    @Autowired
    private PreliminaryEvaluationService preliminaryEvaluationService;
    @Autowired
    private PreliminaryEvaluationDAO preliminaryEvaluationDao;

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
                attributes.put("questionId", 121);
                break;
        }
        return attributes;
    }

    @Override
    public String getQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Question121Form form) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                if (loanApplication.getQuestionSequence().stream().anyMatch(s -> s.getId().equals(ProcessQuestion.Question.Constants.RUNNING_EVALUATION))) {
                    return "EVALUATION";
                } else {
                    return "PRELIMINARY_EVALUATION";
                }
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
                }

                // Run the bot again
                EntityProductEvaluationsProcess guaranteedProcess = question122Service.getAskForGuaranteed(id);
                if (guaranteedProcess != null) {
                    entityProductEvaluationProcessDao.updateIsReady(true, id, guaranteedProcess.getEntityId(), guaranteedProcess.getProductId());
                    loanApplicationService.runEvaluationBot(id, false);
                }
                /*LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                if(loanApplication.getCountryId()  == CountryParam.COUNTRY_PERU){
                    webscrapperService.callSatPlateBot(loanApplication.getUserId(), form.getPlate());
                    webscrapperService.callSoatBot(loanApplication.getUserId(), form.getPlate());
                }*/
                break;
        }
    }

    @Override
    public String getSkippedQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean saveData) throws Exception {
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
                        List<LoanApplicationPreliminaryEvaluation> preEvaluations = preliminaryEvaluationDao.getPreliminaryEvaluationsWithHardFilters(id, Configuration.getDefaultLocale());
                        Map<String, Object> cachedSources = new HashMap<>();
                        for (LoanApplicationPreliminaryEvaluation preEvaluation :
                                preEvaluations.stream().filter(p -> p.getApproved() == null && p.getProduct().getId().equals(Product.GUARANTEED)).collect(Collectors.toList())) {
                            preliminaryEvaluationService.runPreliminaryEvaluation(preEvaluation, loanApplication, cachedSources);
                        }

                        loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                        return ProcessQuestionResponse.goToQuestion(evaluationService.forwardByResult(
                                loanApplication,
                                "NOT_INTERESTED",
                                null));
                }
                break;
        }
        throw new Exception("No method configured");
    }
}
