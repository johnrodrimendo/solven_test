package com.affirm.common.service.question;

import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.UserDAO;
import com.affirm.common.model.catalog.*;
import com.affirm.common.model.form.Question54Form;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.*;
import com.affirm.common.util.*;
import com.affirm.system.configuration.Configuration;
import com.google.gson.Gson;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("question54Service")
public class Question54Service extends AbstractQuestionService<Question54Form> {

    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private UserService userService;
    @Autowired
    private EvaluationService evaluationService;
    @Autowired
    private UserDAO userDao;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private InteractionService interactionService;
    @Autowired
    private WebscrapperService webscrapperService;

    @Override
    public Map<String, Object> getViewAttributes(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean fillSavedData, Map<String, Object> params) throws Exception {

        Map<String, Object> attributes = new HashMap<>();
        Question54Form form = new Question54Form();
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                User user = userDao.getUser(loanApplication.getUserId());
                PhoneNumber userPhoneNumber = null;
                if(loanApplication.getAuxData() != null && loanApplication.getAuxData().getRegisteredPhoneNumberId() != null){
                    userPhoneNumber = userDao.getUserPhoneNumberById(loanApplication.getAuxData().getRegisteredPhoneNumberId());
                }else{
                    List<PhoneNumber> userPhoneNumbers = userDao.getAllPhoneNumbersByUser(user.getId());
                    userPhoneNumber = userService.getUserPhoneNumberToVerify(userPhoneNumbers);
                }



                if (!loanApplication.getSmsSent()) {
                    ProcessQuestion currentQuestion = evaluationService.getQuestionFromEvaluationProcess(loanApplication, null, ProcessQuestion.Question.Constants.VERIFICATION_PHONE_NUMBER, null, null);
                    boolean sendSms = currentQuestion != null ? JsonUtil.getBooleanFromJson(currentQuestion.getConfiguration(), "sendSms", true) : false;
                    if (loanApplication.getEntityUserId() == null && sendSms) {
                        EntityBranding entityBranding = loanApplication.getEntityId() != null ? catalogService.getEntityBranding(loanApplication.getEntityId()) : null;
                        String entityShort = entityBranding != null && entityBranding.getBranded() ? entityBranding.getEntity().getShortName() : null;
                        userService.sendAuthTokenSms(loanApplication.getUserId(), loanApplication.getPersonId(), loanApplication.getCountry().getId() + "", userPhoneNumber.getPhoneNumber(), null, loanApplication.getId(), entityShort, loanApplication.getCountryId());
                        loanApplicationDao.updateSmsSent(loanApplication.getId());
                    }
                }


                if (params != null && params.containsKey("token")) {
                    String token = (String) params.get("token");
                    JSONObject jsonToken = new JSONObject(CryptoUtil.decrypt(token));
                    Integer assistingPhoneNumberId = JsonUtil.getIntFromJson(jsonToken, "assistingPhoneNumberId", null);
                    if (assistingPhoneNumberId != null && userPhoneNumber != null && assistingPhoneNumberId.equals(userPhoneNumber.getPhoneNumberId())) {
                        if (userPhoneNumber.getPinInteractions() == null)
                            userPhoneNumber.setPinInteractions(new PinInteractions());
                        userPhoneNumber.getPinInteractions().setShowAssistedHelp(true);
                        userDao.updatePhoneNumberJsInteraction(userPhoneNumber.getPhoneNumberId(), new Gson().toJson(userPhoneNumber.getPinInteractions()));
                    }
                }

                Boolean callRequested = null;
                if(userPhoneNumber.getPinInteractions() != null &&
                        userPhoneNumber.getPinInteractions().getShowAssistedHelp() != null &&
                        userPhoneNumber.getPinInteractions().getShowAssistedHelp()){
                    List<VerificationCallRequest> callRequests = interactionService.getVerificationCallRequest(loanApplication.getId(), user.getCountryCode(), userPhoneNumber.getPhoneNumber());
                    callRequested = callRequests == null || callRequests.isEmpty();
                }

                Boolean showCellPhone = loanApplication.getProductCategoryId() == ProductCategory.VALIDACION_IDENTIDAD ?  false :  true;


                attributes.put("showCellPhone", showCellPhone);
                attributes.put("loanApplication", loanApplication);
                attributes.put("retrySms", userPhoneNumber.getNumberSmsResendings() < PhoneNumber.MAX_SMS_RESENDINGS);
                attributes.put("timeoutSms", PhoneNumber.RETRY_COUNTDOWN_SECONDS * 1000);
                attributes.put("retryCall", userPhoneNumber.getNumberCalls() < PhoneNumber.MAX_CALLS);
                attributes.put("timeoutCall", PhoneNumber.AVAILABLE_CALL_COUNTDOWN_SECONDS * 1000);
                attributes.put("timeoutAvailableCall", PhoneNumber.AVAILABLE_CALL_COUNTDOWN_SECONDS * 1000);
                attributes.put("phoneNumber", userPhoneNumber.getPhoneNumber());
                attributes.put("callRequested", callRequested);
                attributes.put("form", form);
                attributes.put("sentSms", userPhoneNumber.isSentSms());

                boolean canRetry = userPhoneNumber.getNumberSmsResendings() < PhoneNumber.MAX_SMS_RESENDINGS && userPhoneNumber.getNumberCalls() < PhoneNumber.MAX_CALLS;




                attributes.put("canRetry", canRetry);

                break;
        }
        return attributes;
    }

    @Override
    public String getQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Question54Form form) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                if(loanApplication.getProductCategoryId().equals(ProductCategory.CUENTA_BANCARIA) && ((loanApplication.getEntityId() != null && loanApplication.getEntityId().equals(Entity.AZTECA)) || (loanApplication.getSelectedEntityId() != null && loanApplication.getSelectedEntityId().equals(Entity.AZTECA))) && loanApplication.getBanTotalApiData() != null && loanApplication.getBanTotalApiData().getClienteUId() != null){
                    return "BANTOTAL_CLIENT";
                }
                if(loanApplication.getProductCategoryId() != null && Arrays.asList(ProductCategory.CONSUMO,ProductCategory.CUENTA_BANCARIA).contains(loanApplication.getProductCategoryId()) && loanApplication.getOrigin() == LoanApplication.ORIGIN_API_REST && loanApplication.getEntityId() != null && loanApplication.getEntityId() == Entity.AZTECA && loanApplication.getAuxData() != null && loanApplication.getAuxData().getApiRestUserId() != null && loanApplication.getAuxData().getApiRestUserId().equals(ApiRestUser.BPEOPLE_USER)){
                    return "BPEOPLE";
                }
                return "DEFAULT";
        }
        return null;
    }

    @Override
    protected void validateForm(QuestionFlowService.Type flowType, Integer id, Question54Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                form.getValidator().validate(locale);
                break;
        }
    }

    @Override
    public void saveData(QuestionFlowService.Type flowType, Integer id, Question54Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                String errorMessage = userService.validateSmsAuthToken(loanApplication.getUserId(), form.getPin(), locale);
                if (errorMessage != null) {
                    throw new ResponseEntityException(AjaxResponse.errorMessage(errorMessage));
                }
                saveLoanAuxDataPinValidated(loanApplication);
                if(loanApplication.getProductCategoryId() != null && !loanApplication.getProductCategoryId().equals(ProductCategory.CONSEJ0) && !Arrays.asList(LoanApplication.ORIGIN_REFERENCED,LoanApplication.ORIGIN_API_REST).contains(loanApplication.getOrigin()) && loanApplication.getEntityId() != null && loanApplication.getEntityId().equals(Entity.AZTECA) && loanApplication.getEntityCustomData() != null && loanApplication.getEntityCustomData().has(LoanApplication.EntityCustomDataKeys.BANCO_AZTECA_BASE_PREAPROBADA.getKey())){
                    Entity entity = catalogService.getEntity(loanApplication.getEntityId());
                    boolean generateBotTConektaCall = false;
                    List<Entity.ClickToCallConfiguration> clickToCallConfiguration =  entity.getClickToCallConfiguration();
                    if(clickToCallConfiguration != null && !clickToCallConfiguration.isEmpty() && clickToCallConfiguration.stream().anyMatch(e -> e.getProductCategoryId() != null && e.getProductCategoryId().equals(loanApplication.getProductCategoryId()))){
                        List<Integer> idCampanias = clickToCallConfiguration.stream().filter(e -> e.getProductCategoryId() != null && e.getProductCategoryId().equals(loanApplication.getProductCategoryId())).findFirst().orElse(null).getIdCampaigns();
                        AztecaPreApprovedBase aztecaPreApprovedBase = new Gson().fromJson(loanApplication.getEntityCustomData().getJSONObject(LoanApplication.EntityCustomDataKeys.BANCO_AZTECA_BASE_PREAPROBADA.getKey()).toString(), AztecaPreApprovedBase.class);
                        if(idCampanias != null && idCampanias.contains(aztecaPreApprovedBase.getIdCampania())) generateBotTConektaCall = true;
                        Boolean botAlreadyInitialized = JsonUtil.getBooleanFromJson(loanApplication.getEntityCustomData(),LoanApplication.EntityCustomDataKeys.TCONEKTA_INFORMATION_BOT_INITIALIZED.getKey(), null);
                        if(botAlreadyInitialized != null && botAlreadyInitialized) generateBotTConektaCall = false;
                        if(generateBotTConektaCall){
                            webscrapperService.callSendTConektaInformation(loanApplication.getId());
                            loanApplication.getEntityCustomData().put(LoanApplication.EntityCustomDataKeys.TCONEKTA_INFORMATION_BOT_INITIALIZED.getKey(), true);
                            loanApplicationDao.updateEntityCustomData(loanApplication.getId(), loanApplication.getEntityCustomData());
                        }
                    }
                }
                break;
        }
    }

    @Override
    public Object customMethod(String path, QuestionFlowService.Type flowType, Integer id, Locale locale, Map<String, Object> params) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());

                if ("wrongPhoneNumber".equals(path)) {
                    return ProcessQuestionResponse.goToQuestion(evaluationService.forwardByResult(
                            loanApplication,
                            "WRONG_PHONE_NUMBER",
                            ProcessQuestionSequence.TYPE_BACKWARD,
                            null));
                } else if(loanApplication.getSmsSent()) {
                    EntityBranding entityBranding = loanApplication.getEntityId() != null ? catalogService.getEntityBranding(loanApplication.getEntityId()) : null;
                    String entityShort = entityBranding != null && entityBranding.getBranded() ? entityBranding.getEntity().getShortName() : null;
                    List<PhoneNumber> userPhoneNumbers = userDao.getAllPhoneNumbersByUser(loanApplication.getUserId());
                    PhoneNumber userPhoneNumber = userService.getUserPhoneNumberToVerify(userPhoneNumbers);

                    switch (path) {
                        case "resendSms":
                            if(!userPhoneNumber.canRetrySms())
                                return AjaxResponse.errorMessage("Aún no puedes reenviar el sms");

                            try {
                                userService.sendAuthTokenInteractionWithProvider(loanApplication.getUserId(), loanApplication.getPersonId(), loanApplication.getCountry().getId() + "", userPhoneNumber.getPhoneNumber(), null, loanApplication.getId(), entityShort, loanApplication.getCountryId(), InteractionType.SMS, InteractionProvider.INFOBIP);
                                loanApplicationDao.updateSmsSent(loanApplication.getId());
                                return AjaxResponse.ok(null);
                            } catch (Exception e) {
                                userService.sendAuthTokenInteractionWithProvider(loanApplication.getUserId(), loanApplication.getPersonId(), loanApplication.getCountry().getId() + "", userPhoneNumber.getPhoneNumber(), null, loanApplication.getId(), entityShort, loanApplication.getCountryId(), InteractionType.SMS, InteractionProvider.AWS);
                                loanApplicationDao.updateSmsSent(loanApplication.getId());
                                return AjaxResponse.ok(null);
                            }

                        case "voiceCall":
                            if(!userPhoneNumber.canRetryCall())
                                return AjaxResponse.errorMessage("Aún no puedes reintentar la llamada");

                            userService.sendAuthTokenInteractionWithProvider(loanApplication.getUserId(), loanApplication.getPersonId(), loanApplication.getCountry().getId() + "", userPhoneNumber.getPhoneNumberForCall(loanApplication.getCountry().getId()), null, loanApplication.getId(), entityShort, loanApplication.getCountryId(), InteractionType.CALL, null);
                            loanApplicationDao.updateSmsSent(loanApplication.getId());
                            return AjaxResponse.ok(null);

                        case "callRequest":
                            userService.sendCallRequest(loanApplication.getId(), loanApplication.getCountry().getId().toString(), userPhoneNumber.getPhoneNumber());
                            return AjaxResponse.ok(null);
                    }
                }

                break;
        }
        throw new Exception("No method configured");
    }

    @Override
    public String getSkippedQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean saveData) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                if(loanApplication.getProductCategoryId() != null && Arrays.asList(ProductCategory.GATEWAY).contains(loanApplication.getProductCategoryId()) && loanApplication.getAuxData() != null && loanApplication.getAuxData().getReferenceLoanApplicationId() != null && Arrays.asList(LoanApplication.ORIGIN_REFERENCED).contains(loanApplication.getOrigin())){
                    return getQuestionResultToGo(flowType, id, null);
                }
                if(loanApplication.getOrigin() == LoanApplication.ORIGIN_API_REST && loanApplication.getEntityId() != null && loanApplication.getEntityId() == Entity.AZTECA && loanApplication.getAuxData() != null && loanApplication.getAuxData().getApiRestUserId() != null && loanApplication.getAuxData().getApiRestUserId().equals(ApiRestUser.BPEOPLE_USER)) {
                    return getQuestionResultToGo(flowType, id, null);
                }
                LoanApplicationAuxData loanApplicationAuxData = loanApplication.getAuxData();
                if(loanApplicationAuxData != null && loanApplicationAuxData.getSkipPinQuestion() != null && loanApplicationAuxData.getSkipPinQuestion()){
                    return "DEFAULT";
                }

                // If pin has been verified skip question. Only if its setted in the configuration
                ProcessQuestion currentQuestion = evaluationService.getQuestionFromEvaluationProcess(loanApplication, null, ProcessQuestion.Question.Constants.VERIFICATION_PHONE_NUMBER_PIN, null, null);
                if (currentQuestion.getConfiguration() != null) {
                    if(JsonUtil.getBooleanFromJson(currentQuestion.getConfiguration(), "canSkipIfValid", false)){
                        List<PhoneNumber> userPhoneNumbers = userDao.getAllPhoneNumbersByUser(loanApplication.getUserId());
                        PhoneNumber phoneNumber = userService.getUserPhoneNumberToVerify(userPhoneNumbers);
                        if (phoneNumber.isVerified()) {
                            saveLoanAuxDataPinValidated(loanApplication);
                            return "DEFAULT";
                        }
                    }
                }

                if (loanApplication.getEntityUserId() != null /*|| !Configuration.SMS_AUTHORIZATION_ACTIVATED()*/) {
                    List<PhoneNumber> userPhoneNumbers = userDao.getAllPhoneNumbersByUser(loanApplication.getUserId());
                    PhoneNumber phoneNumber = userService.getUserPhoneNumberToVerify(userPhoneNumbers);

                    User user = userDao.getUser(loanApplication.getUserId());
                    String newAuthToken = userDao.getNewCellphoneAuthToken(loanApplication.getUserId(), phoneNumber.getPhoneNumber());
                    userDao.validateCellphone(user.getId(), newAuthToken);
                    saveLoanAuxDataPinValidated(loanApplication);
                    return "DEFAULT";
                }
                break;
        }
            return null;
    }

    private void saveLoanAuxDataPinValidated(LoanApplication loanApplication){
        if(loanApplication.getAuxData() == null)
            loanApplication.setAuxData(new LoanApplicationAuxData());
        loanApplication.getAuxData().setPhoneValidated(true);
        loanApplicationDao.updateAuxData(loanApplication.getId(),loanApplication.getAuxData());
    }

}

