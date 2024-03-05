package com.affirm.common.service.question;

import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.dao.UserDAO;
import com.affirm.common.model.catalog.*;
import com.affirm.common.model.form.Question142Form;
import com.affirm.common.model.form.Question176Form;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.*;
import com.affirm.common.util.AjaxResponse;
import com.affirm.common.util.GoToNextQuestionException;
import com.affirm.common.util.JsonUtil;
import com.affirm.common.util.ResponseEntityException;
import com.affirm.system.configuration.Configuration;
import com.google.gson.Gson;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service("question176Service")
public class Question176Service extends AbstractQuestionService<Question176Form> {

    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private LoanApplicationApprovalValidationService loanApplicationApprovalValidationService;
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private InteractionService interactionService;
    @Autowired
    private PersonDAO personDAO;
    @Autowired
    private EvaluationService evaluationService;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private LoanApplicationService loanApplicationService;

    public static final Integer DEFAULT_MINUTES = 15;
    public static final Integer MAX_RESEND = 1;
    public static final Integer MAX_RETRIES = 3;

    @Override
    public Map<String, Object> getViewAttributes(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean fillSavedData, Map<String, Object> params) throws Exception {

        Map<String, Object> attributes = new HashMap<>();
        Question176Form form = new Question176Form();
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                Person person = personDAO.getPerson(loanApplication.getPersonId(),false,locale);
                UserEmail userEmailToValidate = null;
                 if(loanApplication.getAuxData() != null && loanApplication.getAuxData().getRegisteredEmailId() != null){
                     userEmailToValidate = userDAO.getUserEmailById(loanApplication.getAuxData().getRegisteredEmailId());
                }
                Integer minutesToExpire = DEFAULT_MINUTES;
                Integer maxResend = MAX_RESEND;
                Integer maxRetries = MAX_RETRIES;
                ProcessQuestion currentQuestion = evaluationService.getQuestionFromEvaluationProcess(loanApplication, null, ProcessQuestion.Question.Constants.EMAIL_PIN_VERIFICATION, null, null);
                if (currentQuestion != null && currentQuestion.getConfiguration() != null) {
                    if(JsonUtil.getIntFromJson(currentQuestion.getConfiguration(), "minutesToExpire", null) != null) minutesToExpire = JsonUtil.getIntFromJson(currentQuestion.getConfiguration(), "minutesToExpire", null);
                    if(JsonUtil.getIntFromJson(currentQuestion.getConfiguration(), "maxResend", null) != null) maxResend = JsonUtil.getIntFromJson(currentQuestion.getConfiguration(), "maxResend", null);
                    if(JsonUtil.getIntFromJson(currentQuestion.getConfiguration(), "maxRetries", null) != null) maxRetries = JsonUtil.getIntFromJson(currentQuestion.getConfiguration(), "maxRetries", null);
                }
                if(userEmailToValidate != null){
                    List<UserEmail.EmailTokens> tokens = userEmailToValidate.getEmailTokens();
                    UserEmail.EmailTokens token = null;
                    boolean createNewToken = false;
                    if(tokens == null || tokens.isEmpty()) {
                        tokens = new ArrayList<>();
                        createNewToken = true;
                    }
                    else{
                        token = tokens.stream().filter(e -> e.getActive() != null && e.getActive()).findFirst().orElse(null);
                        if(token == null) createNewToken = true;
                    }
                    if(createNewToken){
                        token = generateNewUserEmailTokenU(minutesToExpire);
                        Integer personInteractionId = sendPersonInteractionEmail(loanApplication,loanApplication.getAuxData().getRegisteredEmail(),token.getToken(),person);
                        token.setPersonInteractionId(personInteractionId);
                        tokens.add(token);
                        userDAO.updateUserEmailJsToken(userEmailToValidate.getId(),tokens);
                    }
                }
                attributes.put("form", form);
                attributes.put("canRetry", true);
                attributes.put("maxResend", maxResend);
                attributes.put("loanApplication", loanApplication);
                attributes.put("retryEmail", (userEmailToValidate != null ? userEmailToValidate.retriesCount() : 0) < maxRetries);
                attributes.put("timeoutEmail", UserEmail.RETRY_COUNTDOWN_SECONDS * 1000);
                boolean canRetry = (userEmailToValidate != null && userEmailToValidate.getEmailTokens() != null ? userEmailToValidate.getEmailTokens().size() : 0) <= maxResend;
                attributes.put("canRetry", canRetry);
                if(loanApplication.getProductCategoryId() != null && Arrays.asList(Category.VALIDACION_DE_IDENTIDAD).contains(loanApplication.getProductCategoryId()) && loanApplication.getStatus() != null && Arrays.asList(LoanApplicationStatus.REJECTED_AUTOMATIC).contains(loanApplication.getStatus().getId())){
                    loanApplicationDao.updateCurrentQuestion(loanApplication.getId(), ProcessQuestion.Question.Constants.RUNNING_PREEVALUATION);
                    throw new ResponseEntityException(AjaxResponse.redirect(loanApplicationService.generateLoanApplicationLinkEntity(loanApplication)));
                }
                break;
        }
        return attributes;
    }

    @Override
    public String getQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Question176Form form) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                Integer maxRetries = MAX_RETRIES;
                UserEmail userEmailToValidate = null;
                if(loanApplication.getAuxData() != null && loanApplication.getAuxData().getRegisteredEmailId() != null){
                    userEmailToValidate = userDAO.getUserEmailById(loanApplication.getAuxData().getRegisteredEmailId());
                }
                ProcessQuestion currentQuestion = evaluationService.getQuestionFromEvaluationProcess(loanApplication, null, ProcessQuestion.Question.Constants.EMAIL_PIN_VERIFICATION, null, null);
                if (currentQuestion != null && currentQuestion.getConfiguration() != null) {
                    if(JsonUtil.getIntFromJson(currentQuestion.getConfiguration(), "maxRetries", null) != null) maxRetries = JsonUtil.getIntFromJson(currentQuestion.getConfiguration(), "maxRetries", null);
                }
                if(userEmailToValidate == null || userEmailToValidate.getEmailTokens() == null || userEmailToValidate.getEmailTokens().isEmpty()) return "RETRY";
                if(userEmailToValidate.retriesCount() >= maxRetries) return "REJECTED";
                return "DEFAULT";
        }
        return null;
    }

    @Override
    protected void validateForm(QuestionFlowService.Type flowType, Integer id, Question176Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                form.getValidator().validate(locale);
                break;
        }
    }

    @Override
    public void saveData(QuestionFlowService.Type flowType, Integer id, Question176Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                UserEmail userEmailToValidate = null;
                if(loanApplication.getAuxData() != null && loanApplication.getAuxData().getRegisteredEmailId() != null){
                    userEmailToValidate = userDAO.getUserEmailById(loanApplication.getAuxData().getRegisteredEmailId());
                }
                ProcessQuestion currentQuestion = evaluationService.getQuestionFromEvaluationProcess(loanApplication, null, ProcessQuestion.Question.Constants.EMAIL_PIN_VERIFICATION, null, null);
                Integer maxRetries = MAX_RETRIES;
                if (currentQuestion != null && currentQuestion.getConfiguration() != null) {
                    if(JsonUtil.getIntFromJson(currentQuestion.getConfiguration(), "maxRetries", null) != null) maxRetries = JsonUtil.getIntFromJson(currentQuestion.getConfiguration(), "maxRetries", null);
                }
                if(userEmailToValidate == null || userEmailToValidate.getEmailTokens() == null || userEmailToValidate.getEmailTokens().isEmpty()) throw new ResponseEntityException(AjaxResponse.errorMessage("No se ha generado un token"));
                UserEmail.EmailTokens emailToken = userEmailToValidate.getEmailTokens().stream().filter(e -> e.getActive()).findFirst().orElse(null);
                if(emailToken == null) throw new ResponseEntityException(AjaxResponse.errorMessage("No posee un PIN de verificación activo"));
                if((!emailToken.getToken().equalsIgnoreCase(form.getPin()) || emailToken.getExpirationDate().before(new Date())) && Configuration.hostEnvIsProduction()){
                    emailToken.setTriesCount(emailToken.getTriesCount() != null ? emailToken.getTriesCount() + 1 : 1);
                    userEmailToValidate.setEmailTokens(userEmailToValidate.getEmailTokens().stream().filter(e-> e.getActive() == null || !e.getActive()).collect(Collectors.toList()));
                    userEmailToValidate.getEmailTokens().add(emailToken);
                    userDAO.updateUserEmailJsToken(userEmailToValidate.getId(),userEmailToValidate.getEmailTokens());
                    if(emailToken.getExpirationDate().before(new Date())) throw new ResponseEntityException(AjaxResponse.errorMessage(messageSource.getMessage("authtoken.user.tokenExpired", null, locale)));
                    if(userEmailToValidate.retriesCount() < maxRetries) throw new ResponseEntityException(AjaxResponse.errorMessage(messageSource.getMessage("authtoken.user.errorEmailToken", null, locale)));
                }
                else{
                    userDAO.enableOrDisableEmailByUserEmail(loanApplication.getUserId(),userEmailToValidate.getId(),true);
                    loanApplication.getAuxData().setEmailValidated(true);
                    loanApplicationDao.updateAuxData(id,loanApplication.getAuxData());
                }
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
                    case "resend_token":
                        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                        UserEmail userEmailToValidate = null;
                        ProcessQuestion currentQuestion = evaluationService.getQuestionFromEvaluationProcess(loanApplication, null, ProcessQuestion.Question.Constants.EMAIL_PIN_VERIFICATION, null, null);
                        Integer maxResend = MAX_RESEND;
                        Integer minutesToExpire = DEFAULT_MINUTES;
                        if (currentQuestion != null && currentQuestion.getConfiguration() != null) {
                            if(JsonUtil.getIntFromJson(currentQuestion.getConfiguration(), "maxResend", null) != null) maxResend = JsonUtil.getIntFromJson(currentQuestion.getConfiguration(), "maxResend", null);
                            if(JsonUtil.getIntFromJson(currentQuestion.getConfiguration(), "minutesToExpire", null) != null) minutesToExpire = JsonUtil.getIntFromJson(currentQuestion.getConfiguration(), "minutesToExpire", null);
                        }
                        if(loanApplication.getAuxData() != null && loanApplication.getAuxData().getRegisteredEmailId() != null)  userEmailToValidate = userDAO.getUserEmailById(loanApplication.getAuxData().getRegisteredEmailId());
                        Person person = personDAO.getPerson(loanApplication.getPersonId(),false,locale);
                        if(userEmailToValidate.getEmailTokens() == null) userEmailToValidate.setEmailTokens(new ArrayList<>());
                        if(userEmailToValidate.getEmailTokens().size() - 1 >= maxResend) return AjaxResponse.errorMessage("Ya no puede generar nuevos PIN de verificación");
                        if(!userEmailToValidate.canRetryEmail()) return AjaxResponse.errorMessage("Aún no puedes reenviar el email");
                        for (UserEmail.EmailTokens emailToken : userEmailToValidate.getEmailTokens()) {
                            emailToken.setActive(false);
                        }
                        UserEmail.EmailTokens token = generateNewUserEmailTokenU(minutesToExpire);
                        Integer personInteractionId = sendPersonInteractionEmail(loanApplication,loanApplication.getAuxData().getRegisteredEmail(),token.getToken(),person);
                        token.setPersonInteractionId(personInteractionId);
                        userEmailToValidate.getEmailTokens().add(token);
                        userDAO.updateUserEmailJsToken(userEmailToValidate.getId(),userEmailToValidate.getEmailTokens());
                        return AjaxResponse.ok(null);
                }
                break;
        }
        throw new Exception("No method configured");
    }

    private  UserEmail.EmailTokens generateNewUserEmailTokenU(Integer minutesToExpire){
        UserEmail.EmailTokens token = new UserEmail.EmailTokens();
        Date registerDate = new Date();
        token.setRegisterDate(registerDate);
        token.setToken(generateToken(1000, 9999));
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, minutesToExpire != null ? minutesToExpire : DEFAULT_MINUTES);
        token.setExpirationDate(calendar.getTime());
        token.setActive(true);
        token.setTriesCount(0);
        return token;
    }

    private String generateToken(int min, int max){
        Random r = new Random();
        int randInt = r.nextInt(max-min) + min;
        return String.valueOf(randInt);
    }

    private Integer sendPersonInteractionEmail(LoanApplication loanApplication, String email, String token, Person person) throws Exception {
        JSONObject jsonVars = new JSONObject();

        jsonVars.put("TOKEN", token);
        jsonVars.put("CLIENT_NAME", person.getName() != null ? person.getName().split(" ")[0] : null);
        jsonVars.put("AGENT_FULLNAME", loanApplication.getAgent() != null ? loanApplication.getAgent().getName() : Configuration.AGENT_FULLNAME_COLLECTION);
        jsonVars.put("AGENT_IMAGE_URL", loanApplication.getAgent() != null ? loanApplication.getAgent().getAvatarUrl() : Configuration.AGENT_IMAGE_URL_COLLECTION);

        int interactionContentId = InteractionContent.EMAIL_VERIFICATION_PIN;

        PersonInteraction interaction = new PersonInteraction();
        interaction.setInteractionType(catalogService.getInteractionType(InteractionType.MAIL));
        interaction.setInteractionContent(catalogService.getInteractionContent(interactionContentId, loanApplication.getCountryId()));
        interaction.setDestination(email);
        interaction.setLoanApplicationId(loanApplication.getId());
        interaction.setPersonId(person.getId());
        if(loanApplication.getEntityId() != null){
            interaction.setSenderName(String.format("%s de %s",
                    loanApplication.getAgent() != null ? loanApplication.getAgent().getName() : Configuration.AGENT_FULLNAME_COLLECTION,
                    catalogService.getEntity(loanApplication.getEntityId()).getShortName()
            ));
        }
        //interactionService.modifyInteractionContent(interaction,loanApplication);
        
        interactionService.sendPersonInteraction(interaction, jsonVars, null);
        return interaction.getId();
    }
}
