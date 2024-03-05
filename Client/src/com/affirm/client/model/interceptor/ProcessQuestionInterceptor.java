package com.affirm.client.model.interceptor;

import com.affirm.common.dao.*;
import com.affirm.common.model.catalog.*;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.*;
import com.affirm.common.service.external.LeadGidService;
import com.affirm.common.service.external.SalesDoublerService;
import com.affirm.common.service.external.ToroLeadService;
import com.affirm.common.util.AjaxResponse;
import com.affirm.common.util.JsonUtil;
import com.affirm.common.util.ProcessQuestionResponse;
import com.affirm.system.configuration.Configuration;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * Created by john on 18/11/16.
 */
@Component
public class ProcessQuestionInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private EvaluationService evaluationService;
    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private SelfEvaluationDAO selfEvaluationDao;
    @Autowired
    private SelfEvaluationService selfEvaluationService;
    @Autowired
    private ComparisonDAO comparisonDao;
    @Autowired
    private ComparisonService comparisonService;
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private PersonDAO personDAO;
    @Autowired
    private GoogleAnalyticsService googleAnalyticsService;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private LoanApplicationService loanApplicationService;
    @Autowired
    private ConversionDAO conversionDAO;
    @Autowired
    private UtilService utilService;
    @Autowired
    private CountryContextService countryContextService;
    @Autowired
    private SalesDoublerService salesDoublerService;
    @Autowired
    private LeadGidService leadGidService;
    @Autowired
    private PixelConversionService pixelConversionService;
    @Autowired
    private Provider<RequestScoped> requestScoped;
    @Autowired
    private ToroLeadService toroLeadService;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

        doPostHandle(request, response, handler, modelAndView);
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        boolean result = doPreHandle(request, response, handler);
        return result;
    }

    public void doPostHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception{

        if (modelAndView == null)
            return;

        if (request.getMethod().equalsIgnoreCase("GET")) {
            int questionId = Integer.parseInt(request.getPathInfo().split("/")[3]);
            String token = request.getParameter("token");
            switch (request.getPathInfo().split("/")[1]) {
                case Configuration.EVALUATION_CONTROLLER_URL:
                    LoanApplication loanApplication = null;
                    if (token != null)
                        loanApplication = loanApplicationDao.getLoanApplication(evaluationService.getIdFromToken(token), Configuration.getDefaultLocale());
                    Integer sectionId = evaluationService.getQuestionCategoryId(questionId, loanApplication);

                    // Send configuration by question section
                    Integer modelSectionId = sectionId;
                    boolean showAgent;
                    boolean showLoanApplicationUpdateModal;
                    boolean showLoanOfferUpdateModal;
                    boolean showGoBack;
                    boolean showPreApproved = false;
                    boolean isCustomDocumentTitle = false;

                    if(loanApplication.getEntityId() == null || loanApplication.getEntityId() != Entity.BANBIF){
                        if(Arrays.asList(ProcessQuestion.Question.Constants.HOME_ADDRESS_DISGREGATED,ProcessQuestion.Question.Constants.WORKPLACE_ADDRESS_DISGREGATED).contains(questionId)){
                            if(questionId == ProcessQuestion.Question.Constants.HOME_ADDRESS_DISGREGATED) sectionId = ProcessQuestionCategory.PERSONAL_INFORMATION;
                            else if(questionId == ProcessQuestion.Question.Constants.WORKPLACE_ADDRESS_DISGREGATED) sectionId = ProcessQuestionCategory.INCOME;
                        }
                    }

                    if(loanApplication != null && loanApplication.getProductCategoryId() != null && Arrays.asList(ProcessQuestion.Question.Constants.AZTECA_FINAL_MESSAGE).contains(questionId) && Arrays.asList(ProductCategory.CONSUMO,ProductCategory.CUENTA_BANCARIA).contains(loanApplication.getProductCategoryId())){
                        sectionId = ProcessQuestionCategory.VERIFICATION;
                    }

                    boolean showEmailFullName = false;
                    String email = null;
                    String fullName = null;

                    if (sectionId == null) {
                        showAgent = true;
                        showLoanApplicationUpdateModal = false;
                        showLoanOfferUpdateModal = false;
                    } else {
                        switch (sectionId) {
                            case ProcessQuestionCategory.PRE_INFORMATION:
                                modelSectionId = null;
                                showAgent = true;
                                showLoanApplicationUpdateModal = loanApplication != null && loanApplication.getProductCategory().getId() == ProductCategory.VEHICULO;
                                showLoanOfferUpdateModal = false;
                                break;
                            case ProcessQuestionCategory.PERSONAL_INFORMATION:
                                showAgent = true;
                                if(loanApplication != null && loanApplication.getEntityId() != null && Arrays.asList(Entity.FUNDACION_DE_LA_MUJER, Entity.AZTECA).contains(loanApplication.getEntityId())){
                                    showLoanApplicationUpdateModal = false;
                                }else{
                                    showLoanApplicationUpdateModal = true;
                                }
                                showLoanOfferUpdateModal = false;
                                break;
                            case ProcessQuestionCategory.INCOME:
                                showAgent = true;
                                if(loanApplication != null && loanApplication.getEntityId() != null && Arrays.asList(Entity.FUNDACION_DE_LA_MUJER, Entity.AZTECA).contains(loanApplication.getEntityId())){
                                    showLoanApplicationUpdateModal = false;
                                }else{
                                    showLoanApplicationUpdateModal = true;
                                }
                                showLoanOfferUpdateModal = false;
                                break;
                            case ProcessQuestionCategory.OFFER:
                                showAgent = false;
                                if(loanApplication != null && loanApplication.getEntityId() != null && Arrays.asList(Entity.FUNDACION_DE_LA_MUJER, Entity.AZTECA).contains(loanApplication.getEntityId())){
                                    showLoanApplicationUpdateModal = false;
                                    showLoanOfferUpdateModal = false;
                                }else{
                                    showLoanApplicationUpdateModal = false;
                                    showLoanOfferUpdateModal = true;
                                }
                                break;
                            case ProcessQuestionCategory.VERIFICATION:
                                showAgent = true;
                                showLoanApplicationUpdateModal = false;
                                showLoanOfferUpdateModal = false;
                                break;
                            case ProcessQuestionCategory.RESULT:
                                showAgent = true;
                                showLoanApplicationUpdateModal = false;
                                showLoanOfferUpdateModal = false;
                                showGoBack = false;
                                break;
                            default:
                                showAgent = true;
                                showLoanApplicationUpdateModal = false;
                                showLoanOfferUpdateModal = false;
                                break;
                        }
                    }

                    // banco del sol should not show the update banner
                    if (loanApplication != null && ((Integer) Entity.BANCO_DEL_SOL).equals(loanApplication.getEntityId())) {
                        showLoanApplicationUpdateModal = false;
                        showLoanOfferUpdateModal = false;
                        isCustomDocumentTitle = true;
                    }
                    if (loanApplication != null && Objects.equals(Entity.CREDIGOB, loanApplication.getEntityId())) {
                        showLoanApplicationUpdateModal = false;
                        showLoanOfferUpdateModal = false;
                    }
                    modelAndView.addObject("isCustomDocumentTitle", isCustomDocumentTitle);

                    if (loanApplication == null) {
                        showGoBack = false;
                    } else {
                        Integer backwardId = evaluationService.getBackwardId(loanApplication);
                        showGoBack = backwardId != null && backwardId != 1;
                    }

                    // Send percentage of question process
                    if (loanApplication != null) {
                        loanApplicationDao.updateUserAgent(loanApplication.getId(), request.getHeader("User-Agent"));
                        String gaClientId = loanApplication.getGaClientId();

                        if (gaClientId == null) {
                            gaClientId = loanApplication.getCode();
                        }

                        googleAnalyticsService.Configure(
                                request.getRemoteAddr(),
                                gaClientId,
                                request.getHeader("User-Agent"),
                                loanApplication.getSource(),
                                loanApplication.getMedium(),
                                loanApplication.getCampaign(),
                                loanApplication
                                        .getCountryId()
                        );

                        if (sectionId != null) {
                            String urlGA = ProcessQuestionCategory.getKeyAnalyticsPage(sectionId);
                            googleAnalyticsService.sendPageView(urlGA);
                        }

                        modelAndView.addObject("progressPercentage", loanApplication.getPercentage());
                        modelAndView.addObject("questionCategory", sectionId);

//                        Person person = personDAO.getPerson(catalogService, Configuration.getDefaultLocale(), loanApplication.getPersonId(), false);
//                        User user = userDAO.getUser(loanApplication.getUserId());

//                        email = user.getEmail();
//                        fullName = person.getFullName();
//
//                        if (email != null || fullName != null) {
//                            showEmailFullName = true;
//                        }
                    }

                    if (loanApplication != null) {
                        String evaluationURLType = "";
                        boolean approved = false;

                        // Logic for the showPreApproved
                        switch (sectionId) {
                            case ProcessQuestionCategory.PRE_INFORMATION:
                            case ProcessQuestionCategory.PERSONAL_INFORMATION:
                            case ProcessQuestionCategory.INCOME:
                            case ProcessQuestionCategory.EVALUATION:
                                showPreApproved = true;
                                break;
                            default:
                                showPreApproved = false;
                                break;
                        }
                        String otherMessage = null;
                        String banbifCurrency = "$";
                        if (showPreApproved) {
                            showPreApproved = loanApplicationService.hasAnyApprovedPreEvaluation(loanApplication.getId(), Arrays.asList(Entity.TARJETAS_PERUANAS, Entity.BANCO_DEL_SOL));
                            Integer maxPreapprovedAmount = loanApplicationDao.getMaxPreapprovedAmount(loanApplication.getId());
                            BanbifPreApprovedBase banbifPreApprovedBase = null;
                            if(loanApplication.getEntityId() != null && loanApplication.getEntityId() == Entity.BANBIF){
                                JSONObject data = JsonUtil.getJsonObjectFromJson(loanApplication.getEntityCustomData(), LoanApplication.EntityCustomDataKeys.BANBIF_BASE_PREAPROBADA.getKey(), null);
                                if(data != null && loanApplication.getEntityCustomData(LoanApplication.EntityCustomDataKeys.BANBIF_LANDING_AB_TESTING.getKey()) != null){
                                    banbifPreApprovedBase = new Gson().fromJson(data.toString(),BanbifPreApprovedBase.class);
                                    if(banbifPreApprovedBase != null && banbifPreApprovedBase.getPlastico() != null && banbifPreApprovedBase.getPlastico().equalsIgnoreCase(BanbifPreApprovedBase.BANBIF_MAS_EFECTIVO_CARD)) banbifCurrency = "S/";
                                    if(questionId == ProcessQuestion.Question.Constants.EMAIL_AND_CELLPHONE) {
                                        otherMessage = "<p class='congrats'>¡Felicitaciones! Tenemos una Tarjeta de Crédito para tí.<span></span></p>";
                                        maxPreapprovedAmount = 1;
                                    }
                                    else if(loanApplication.getEntityCustomData(LoanApplication.EntityCustomDataKeys.BANBIF_LANDING_AB_TESTING.getKey()).equalsIgnoreCase("B")){
                                            if(banbifPreApprovedBase != null && banbifPreApprovedBase.getPlastico() != null && banbifPreApprovedBase.getLinea() != null) maxPreapprovedAmount = banbifPreApprovedBase.getLinea().intValue();
                                    }
                                    else showPreApproved = false;
                                }
                                else showPreApproved = false;
                            }
                            if (showPreApproved && maxPreapprovedAmount > 0) {

                                if (loanApplication.getProductCategoryId() != ProductCategory.LEADS) {
                                    CountryParam countryParam = countryContextService.getCountryParamsByRequest(request);
                                    String message = null;
                                    switch (countryParam.getId()) {
                                        case CountryParam.COUNTRY_PERU:
                                            if(loanApplication.getEntityId() != null && banbifPreApprovedBase != null && loanApplication.getEntityId().equals(Entity.BANBIF)) {
                                                if(otherMessage != null) message = otherMessage;
                                                else message = "<p class='congrats' style='text-align:start !important;'>Tienes una Tarjeta "+banbifPreApprovedBase.getPlastico()+" con %s de línea.<span></span></p>";
                                            }
                                            else message = "<p class='congrats'>¡Felicitaciones! Tenemos<br/>hasta %s para ti.<span></span></p>";
                                            break;
                                        case CountryParam.COUNTRY_COLOMBIA:
                                            if(loanApplication.getEntityId() == null || loanApplication.getEntityId() != Entity.FUNDACION_DE_LA_MUJER){
                                                message = "<p class='approved' >¡Está Pre Aprobado! <span></span><p>";
                                            }
                                            break;
//                                    case CountryParam.COUNTRY_ARGENTINA: message = "<p class='congrats'>¡Felicitaciones! Tenemos<br/>hasta %s para vos.<span></span></p>";break;
                                        default:
                                            message = "<p class='approved' >¡Estás Pre Aprobado! <span></span><p>";
                                            break;
                                    }

                                    if (message != null && countryContextService.isCountryContextInPeru(request)) {

                                        message = String.format(message, utilService.integerMoneyFormat(maxPreapprovedAmount,(loanApplication.getEntityId() != null && banbifPreApprovedBase != null && loanApplication.getEntityId() == Entity.BANBIF) ? banbifCurrency : "S/"));
                                    }
                                    modelAndView.addObject("showPreApprovedMessage", message);
                                }

                            }
                        }
                        modelAndView.addObject("showOfferMedal", modelAndView.getModel().get("showOfferMedal") != null ?
                                modelAndView.getModel().get("showOfferMedal") : questionId == ProcessQuestion.Question.OFFER.id);


                        JSONArray jsonConversions = pixelConversionService.getConversionToSend(loanApplication);
                        modelAndView.addObject("pixelConversion", jsonConversions != null ? jsonConversions.toString() : null);


                        //If has evaluation approved and the loan is a lead from sales doubler, call its postback
                        if (loanApplication.getSource() != null && loanApplication.getSource().equalsIgnoreCase(LoanApplication.LEAD_SALESDOUBLER)) {
                            if (jsonConversions != null) {
                                for (int i = 0; i < jsonConversions.length(); i++) {
                                    if (JsonUtil.getStringFromJson(jsonConversions.getJSONObject(i), "type", "").equalsIgnoreCase(PixelConversionService.OFFER_SHOWED_CONVERSION)) {
                                        salesDoublerService.callCPLPostback(
                                                loanApplication.getJsLeadParam() != null ? JsonUtil.getStringFromJson(loanApplication.getJsLeadParam(), "aff_sub", null) : null,
                                                loanApplication.getId() + "");
                                        break;
                                    }
                                }
                            }
                        }
                        //If has evaluation approved and the loan is a lead from Leadgid, call its postback
                        if (loanApplication.getSource() != null && loanApplication.getSource().equalsIgnoreCase(LoanApplication.LEAD_LEADGID)) {
                            if (jsonConversions != null) {
                                for (int i = 0; i < jsonConversions.length(); i++) {
                                    if (JsonUtil.getStringFromJson(jsonConversions.getJSONObject(i), "type", "").equalsIgnoreCase(PixelConversionService.OFFER_SHOWED_CONVERSION)) {
                                        leadGidService.callCPLPostback(
                                                loanApplication.getJsLeadParam() != null ? JsonUtil.getStringFromJson(loanApplication.getJsLeadParam(), "click_id", null) : null);
                                        break;
                                    }
                                }
                            }
                        }
                        //If has evaluation approved and the loan is a lead from ToroLead, call its postback
                        if (loanApplication.getSource() != null && loanApplication.getSource().equalsIgnoreCase(LoanApplication.LEAD_TORO)) {
                            if (jsonConversions != null) {
                                for (int i = 0; i < jsonConversions.length(); i++) {
                                    if (JsonUtil.getStringFromJson(jsonConversions.getJSONObject(i), "type", "").equalsIgnoreCase(PixelConversionService.OFFER_SHOWED_CONVERSION)) {
                                        toroLeadService.callCPLPostback(
                                                loanApplication.getJsLeadParam() != null ? JsonUtil.getStringFromJson(loanApplication.getJsLeadParam(), "toro_sid", null) : null);
                                        break;
                                    }
                                }
                            }
                        }
                    }

                    modelAndView.addObject("showNameEmail", showEmailFullName);
                    modelAndView.addObject("userEmail", email);
                    modelAndView.addObject("userFullName", fullName);
                    // Add attributes to the request
                    modelAndView.addObject("section", modelSectionId);
                    modelAndView.addObject("showAgent", modelAndView.getModel().get("showAgent") != null ?
                            modelAndView.getModel().get("showAgent") : showAgent);
                    modelAndView.addObject("showLoanApplicationUpdateModal", modelAndView.getModel().get("showLoanApplicationUpdateModal") != null ?
                            modelAndView.getModel().get("showLoanApplicationUpdateModal") : showLoanApplicationUpdateModal);
                    modelAndView.addObject("showLoanOfferUpdateModal", modelAndView.getModel().get("showLoanOfferUpdateModal") != null ?
                            modelAndView.getModel().get("showLoanOfferUpdateModal") : showLoanOfferUpdateModal);
                    //modelAndView.addObject("showToky", modelAndView.getModel().get("showToky") != null ? modelAndView.getModel().get("showToky") : true);
                    modelAndView.addObject("showGoBack", modelAndView.getModel().get("showGoBack") != null ? modelAndView.getModel().get("showGoBack") : showGoBack);
                    modelAndView.addObject("showPreApproved", showPreApproved);
                    if (loanApplication != null && loanApplication.getProductCategory().getId() == ProductCategory.VEHICULO && loanApplication.getVehicle() == null)
                        modelAndView.addObject("forceVehicleModalShow", true);
                    else
                        modelAndView.addObject("forceVehicleModalShow", false);
                    if (loanApplication != null && loanApplication.getCode() != null)
                        modelAndView.addObject("hotjarId", loanApplication.getCode());
                    if (loanApplication != null && loanApplication.getEntityId() != null && Entity.BANBIF == loanApplication.getEntityId())
                        modelAndView.addObject("isBanBif", true);
                    modelAndView.addObject("showCustomOffer", showCustomOfferProgress(loanApplication));
                    modelAndView.addObject("showLabelInformation", showLabelInformation(loanApplication));
                    break;
                case Configuration.SELF_EVALUATION_CONTROLLER_URL:

                    SelfEvaluation selfEvaluation = null;
                    if (token != null)
                        selfEvaluation = selfEvaluationDao.getSelfEvaluation(selfEvaluationService.getIdFromToken(token), Configuration.getDefaultLocale());
                    modelAndView.addObject("showLabelInformation", true);
                    // Send percentage of question process
                    if (selfEvaluation != null) {
                        List<Integer> questionFlow = new ArrayList<>();

                        // Fill the flow with the current progress of the loanapplication
                        boolean toContinue = true;
                        while (toContinue) {
                            if (questionFlow.isEmpty()) {
                                questionFlow.add(selfEvaluation.getQuestionSequence().get(0).getId());
                            } else {
                                int lastQuestionFlow = questionFlow.get(questionFlow.size() - 1);

                                selfEvaluation.getQuestionSequence().removeIf(q -> q.getType() == ProcessQuestionSequence.TYPE_BACKWARD);
                                int lastIndex = 0;
                                for (int i = 0; i < selfEvaluation.getQuestionSequence().size(); i++) {
                                    if (selfEvaluation.getQuestionSequence().get(i).getId() == lastQuestionFlow)
                                        lastIndex = i;
                                }
                                if (lastIndex < selfEvaluation.getQuestionSequence().size() - 1) {
                                    questionFlow.add(selfEvaluation.getQuestionSequence().get(lastIndex + 1).getId());
                                } else {
                                    toContinue = false;
                                }
                            }
                        }

                        // Fill the flow with the rest of the process
                        ProcessQuestionsConfiguration processQuestion = selfEvaluationService.getSelfEvaluationProcess(selfEvaluation, request);
                        toContinue = true;
                        while (toContinue) {
                            ProcessQuestion question = processQuestion.getQuestions().stream().filter(s -> s.getId().intValue() == questionFlow.get(questionFlow.size() - 1)).findFirst().orElse(null);
                            if (question != null && question.getResults() != null) {
                                Iterator<String> jsonKeys = question.getResults().keys();
                                if (jsonKeys.hasNext()) {
                                    int nextQuestionId = question.getResults().optInt(question.getResults().keys().next(), 0);
                                    if (nextQuestionId != 0) {
                                        questionFlow.add(nextQuestionId);
                                        continue;
                                    }
                                }
                            }
                            toContinue = false;
                        }

                        // calculate and send the percentage
                        int currentIndex = questionFlow.indexOf(selfEvaluation.getCurrentQuestionId());
                        modelAndView.addObject("progressPercentage", (currentIndex * 100) / (questionFlow.size() - 1));
                    }

                    modelAndView.addObject("showNameEmail", false);
                    modelAndView.addObject("userEmail", null);
                    modelAndView.addObject("userFullName", null);
                    modelAndView.addObject("showAgent", true);
                    modelAndView.addObject("showLoanApplicationUpdateModal", false);
                    modelAndView.addObject("showLoanOfferUpdateModal", false);
                    modelAndView.addObject("section", null);
                    // modelAndView.addObject("showToky", modelAndView.getModel().get("showToky") != null ? modelAndView.getModel().get("showToky") : true);
                    break;
                case Configuration.COMPARISON_CONTROLLER_URL:

                    Comparison comparison = null;
                    if (token != null)
                        comparison = comparisonDao.getComparison(comparisonService.getIdFromToken(token), Configuration.getDefaultLocale());

                    // Send percentage of question process
                    if (comparison != null && !comparison.getQuestionSequence().isEmpty()) {
                        List<Integer> questionFlow = new ArrayList<>();

                        // Fill the flow with the current progress of the loanapplication
                        boolean toContinue = true;
                        while (toContinue) {
                            if (questionFlow.isEmpty()) {
                                questionFlow.add(comparison.getQuestionSequence().get(0).getId());
                            } else {
                                int lastQuestionFlow = questionFlow.get(questionFlow.size() - 1);

                                comparison.getQuestionSequence().removeIf(q -> q.getType() == ProcessQuestionSequence.TYPE_BACKWARD);
                                int lastIndex = 0;
                                for (int i = 0; i < comparison.getQuestionSequence().size(); i++) {
                                    if (comparison.getQuestionSequence().get(i).getId() == lastQuestionFlow)
                                        lastIndex = i;
                                }
                                if (lastIndex < comparison.getQuestionSequence().size() - 1) {
                                    questionFlow.add(comparison.getQuestionSequence().get(lastIndex + 1).getId());
                                } else {
                                    toContinue = false;
                                }
                            }
                        }

                        // Fill the flow with the rest of the process
                        ProcessQuestionsConfiguration processQuestion = comparisonService.getComparisonProcess(comparison);
                        toContinue = true;
                        while (toContinue) {
                            ProcessQuestion question = processQuestion.getQuestions().stream().filter(s -> s.getId().intValue() == questionFlow.get(questionFlow.size() - 1)).findFirst().orElse(null);
                            if (question != null && question.getResults() != null) {
                                Iterator<String> jsonKeys = question.getResults().keys();
                                if (jsonKeys.hasNext()) {
                                    int nextQuestionId = question.getResults().optInt(question.getResults().keys().next(), 0);
                                    if (nextQuestionId != 0) {
                                        questionFlow.add(nextQuestionId);
                                        continue;
                                    }
                                }
                            }
                            toContinue = false;
                        }

                        // calculate and send the percentage
                        int currentIndex = questionFlow.indexOf(comparison.getCurrentQuestionId());
                        modelAndView.addObject("progressPercentage", (currentIndex * 100) / (questionFlow.size() - 1));
                    } else {
                        modelAndView.addObject("progressPercentage", 0);
                    }

                    modelAndView.addObject("showAgent", true);
                    modelAndView.addObject("showLoanApplicationUpdateModal", false);
                    modelAndView.addObject("showLoanOfferUpdateModal", false);
                    modelAndView.addObject("section", null);
                    // modelAndView.addObject("showToky", modelAndView.getModel().get("showToky") != null ? modelAndView.getModel().get("showToky") : true);
                    break;
            }
        }
    }

    public boolean doPreHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception{
        Locale locale = RequestContextUtils.getLocaleResolver(request).resolveLocale(request);
        if (request.getMethod().equalsIgnoreCase("POST")) {
            int questionId = Integer.parseInt(request.getPathInfo().split("/")[3]);
            String token = request.getParameter("token");
            switch (request.getPathInfo().split("/")[1]) {
                case Configuration.EVALUATION_CONTROLLER_URL:
                    LoanApplication loanApplication = null;
                    if (token != null)
                        loanApplication = requestScoped.get().getLoanApplicationLite();

                    // If the current process quetion is diferent from the question to post, deny it!
                    if (loanApplication != null && loanApplication.getCurrentQuestionId() != questionId) {
                        AjaxResponse.writeErrorMessageToResponse(messageSource.getMessage("system.error.differentProcessQuestion", null, locale), response, "recharge");
                        return false;
                    }
                    break;
                case Configuration.SELF_EVALUATION_CONTROLLER_URL:

                    SelfEvaluation selfEvaluation = null;
                    if (token != null)
                        selfEvaluation = requestScoped.get().getSelfEvaluation();

                    // If the current process quetion is diferent from the question to post, deny it!
                    if (selfEvaluation != null && selfEvaluation.getCurrentQuestionId() != questionId) {
                        AjaxResponse.writeErrorMessageToResponse(messageSource.getMessage("system.error.differentProcessQuestion", null, locale), response);
                        return false;
                    }
                    break;
                case Configuration.COMPARISON_CONTROLLER_URL:

                    Comparison comparison = null;
                    if (token != null)
                        comparison = comparisonDao.getComparison(comparisonService.getIdFromToken(token), Configuration.getDefaultLocale());

                    // If the current process quetion is diferent from the question to post, deny it!
                    if (comparison != null && comparison.getCurrentQuestionId() != questionId) {
                        AjaxResponse.writeErrorMessageToResponse(messageSource.getMessage("system.error.differentProcessQuestion", null, locale), response);
                        return false;
                    }
                    break;
            }
        } else if (request.getMethod().equalsIgnoreCase("GET")) {
            int questionId = Integer.parseInt(request.getPathInfo().split("/")[3]);
            String token = request.getParameter("token");
            switch (request.getPathInfo().split("/")[1]) {
                case Configuration.EVALUATION_CONTROLLER_URL:
                    LoanApplication loanApplication = null;
                    if (token != null)
                        loanApplication = requestScoped.get().getLoanApplicationLite();

                    // Validate that if the question to GET has the skip property, skip the question
                    if (loanApplication != null) {
                        ProcessQuestionsConfiguration processQuestion = evaluationService.getEvaluationProcessByLoanApplication(loanApplication);
                        ProcessQuestion questionToGet = processQuestion.getQuestions().stream().filter(q -> q.getId().equals(questionId)).findFirst().orElse(null);
                        if (questionToGet != null && questionToGet.getSkip() != null && questionToGet.getSkip() && questionToGet.getId() != ProcessQuestion.Question.Constants.PRINCIPAL_INCOME_TYPE) {
                            ProcessQuestionResponse.writeGoToQuestionToResponse(evaluationService.forwardByResult(loanApplication, "DEFAULT", ProcessQuestionSequence.TYPE_SKIPPED, request), response);
                            return false;
                        }
                    }
                    break;
            }

        }

        //TODO Validate that only do get to the curent process question

        return true;

    }

    public boolean showCustomOfferProgress(LoanApplication loanApplication){
        if(loanApplication != null && loanApplication.getEntityId() != null){
            switch (loanApplication.getEntityId()){
                case Entity.AZTECA:
                    if(loanApplication.getProductCategoryId() != null){
                        switch (loanApplication.getProductCategoryId()){
                            case ProductCategory.VALIDACION_IDENTIDAD:
                            case ProductCategory.GATEWAY:
                            case ProductCategory.CONSEJ0:
                                return true;
                        }
                    }
                    break;
            }
        }
        return false;
    }

    public boolean showLabelInformation(LoanApplication loanApplication){
        if(loanApplication != null && loanApplication.getEntityId() != null){
            switch (loanApplication.getEntityId()){
                case Entity.AZTECA:
                    if(loanApplication.getProductCategoryId() != null){
                        switch (loanApplication.getProductCategoryId()){
                            case ProductCategory.VALIDACION_IDENTIDAD:
                                return false;
                        }
                    }
                    break;
            }
        }
        return true;
    }

}
