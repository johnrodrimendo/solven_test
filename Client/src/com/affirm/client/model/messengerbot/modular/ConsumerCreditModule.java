package com.affirm.client.model.messengerbot.modular;

import com.affirm.client.model.messengerbot.SessionData;
import com.affirm.client.model.messengerbot.contract.Button;
import com.affirm.client.model.messengerbot.utils.MessengerHelper;
import com.affirm.client.service.MessengerSession;
import com.affirm.client.service.UserCLService;
import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.dao.UserDAO;
import com.affirm.common.model.catalog.*;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.EvaluationService;
import com.affirm.common.service.LoanApplicationService;
import com.affirm.common.service.UserService;
import com.affirm.common.util.IntegerFieldValidator;
import com.affirm.common.util.StringFieldValidator;
import com.affirm.common.util.Util;
import com.affirm.common.util.ValidatorUtil;
import com.affirm.system.configuration.Configuration;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class ConsumerCreditModule implements MessengerModule {

    @Autowired
    CatalogService catalogService;

    @Autowired
    PersonDAO personDAO;

    @Autowired
    MessageSource messageSource;

    @Autowired
    MessengerSession messengerSession;

    @Autowired
    LoanApplicationDAO loanApplicationDAO;

    @Autowired
    UserService userService;

    @Autowired
    UserCLService userCLService;

    @Autowired
    UserDAO userDAO;

    @Autowired
    EvaluationService evaluationService;

    @Autowired
    LoanApplicationService loanApplicationService;

    @Autowired
    GeneralModule generalModule;

    MessengerHelper helper;
    Gson gson = new Gson();

    @Override
    public String getModuleName() {
        return SessionData.M_CONSUMER_CREDIT;
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public List<String> getReplies(String senderId, String text, SessionData[] dataArray) throws Exception {
        SessionData data = dataArray[0];
        List<Product> activeProductsByCategory = catalogService.getActiveProductsByCategory(ProductCategory.CONSUMO);

        switch (data.getCurrentState()) {
            case SessionData.CONSUMER_CREDIT_NEW:
                return toList(jsonGoTo(SessionData.CONSUMER_PICK_COUNTRY, data));
            case SessionData.CONSUMER_PICK_COUNTRY:
                if(text.trim().equals("_argentina_")) {
                    data.setCurrentState(SessionData.CONSUMER_CREDIT_NEW);

                    String json = MessageFactory.newCTAMessage(
                            messageSource.getMessage("messengerbot.loan.location.missing.arg",null,data.getLocale()),
                            MessageFactory.newWebUrlButton(messageSource.getMessage("messengerbot.loan.running.finished", null, data.getLocale()), "https://www.solven.com.ar/autoevaluacion"))
                            .toJson(senderId, gson);

                    return toList(json);
                }

                if(text.trim().equals("_peru_")) {
                    return toList(jsonGoTo(SessionData.CONSUMER_CREDIT_DOCUMENT_TYPE, data));
                }

                if(text.trim().equals("_other")) {
                    data.setCurrentState(SessionData.CONSUMER_CREDIT_NEW);

                    String jsonMissing = MessageFactory.newCTAMessage(
                            messageSource.getMessage("messengerbot.loan.location.missing", null, data.getLocale()),
                            MessageFactory.newWebUrlButton(messageSource.getMessage("messengerbot.loan.running.finished", null, data.getLocale()), "http://www.solven.la/"))
                            .toJson(senderId, gson);

                    return toList(jsonMissing);
                }

                return errorRetryLocale(data, "messengerbot.error.buttons");

            case SessionData.CONSUMER_CREDIT_DOCUMENT_NUMBER:
                Integer docTypeId = Integer.parseInt(data.getAnswer(SessionData.CONSUMER_CREDIT_DOCUMENT_TYPE));
                StringFieldValidator vDocNumber;
                //set validator
                if (docTypeId == IdentityDocumentType.DNI)
                    vDocNumber = new StringFieldValidator(ValidatorUtil.DOC_NUMBER_DNI, text.trim())
                            .setMaxCharactersErrorMsg("bot.string.maxCharacters.")
                            .setMinCharactersErrorMsg("bot.string.minCharacters.");
                else if (docTypeId == IdentityDocumentType.CE)
                    vDocNumber = new StringFieldValidator(ValidatorUtil.DOC_NUMBER_CE, text.trim())
                            .setMaxCharactersErrorMsg("bot.string.maxCharacters.")
                            .setMinCharactersErrorMsg("bot.string.minCharacters.");
                else {
                    return toList(jsonGoTo(SessionData.CONSUMER_CREDIT_DOCUMENT_TYPE, data));
                }
                //validate
                if (vDocNumber.validate(data.getLocale())) {
                    if (docTypeId == IdentityDocumentType.DNI) {
                        Reniec reniec = personDAO.getReniecDBData(text.trim());
                        if (reniec == null) {
                            return errorRetryLocale(data, "bot.error.document.notfound");
                        }
                    }
                    data.setCurrentAnswer(text.trim());
                    return toList(jsonGoTo(SessionData.CONSUMER_CREDIT_DOCUMENT_NUMBER_SURE, data));
                }
                return errorRetry(data, vDocNumber.getErrors());
            case SessionData.CONSUMER_CREDIT_AMOUNT:
                Integer amount = Integer.valueOf(text.trim());

                Integer maxAmount = activeProductsByCategory.stream()
                        .map(c -> c.getProductParams(CountryParam.COUNTRY_PERU))
                        .max(Comparator.comparingInt(c -> c.getMaxAmount()))
                        .get().getMaxAmount();
                Integer minAmount = activeProductsByCategory.stream()
                        .map(c -> c.getProductParams(CountryParam.COUNTRY_PERU))
                        .min(Comparator.comparingInt(c -> c.getMinAmount()))
                        .get().getMinAmount();

                IntegerFieldValidator amountValidator = new IntegerFieldValidator(ValidatorUtil.SELF_EVALUATION_AMOUNT, amount)
                                                .setMaxValue(maxAmount)
                                                .setMinValue(minAmount)
                                                .setMaxValueErrorMsg("validation.int.maxValueMoney")
                                                .setMinValueErrorMsg("validation.int.minValueMoney");

                if(amountValidator.validate(data.getLocale())) {
                    data.setAnswer(SessionData.CONSUMER_CREDIT_AMOUNT, text.trim());
                    return toList(jsonGoTo(SessionData.CONSUMER_CREDIT_TERM, data));
                }

                return errorRetry(data, amountValidator.getErrors());
            case SessionData.CONSUMER_CREDIT_TERM:
                Integer term = Integer.valueOf(text.trim());


                Integer maxInstallments = activeProductsByCategory.stream()
                        .map(c -> c.getProductParams(CountryParam.COUNTRY_PERU))
                        .max(Comparator.comparingInt(c -> c.getMaxInstallments()))
                        .get().getMaxInstallments();
                Integer minInstallments = activeProductsByCategory.stream()
                        .map(c -> c.getProductParams(CountryParam.COUNTRY_PERU))
                        .min(Comparator.comparingInt(c -> c.getMinInstallments()))
                        .get().getMinInstallments();

                IntegerFieldValidator termValidator = new IntegerFieldValidator(term)
                                                        .setRestricted(true)
                                                        .setRequired(true)
                                                        .setMinValue(minInstallments)
                                                        .setMaxValue(maxInstallments);

                if(termValidator.validate(data.getLocale())) {
                    data.setAnswer(SessionData.CONSUMER_CREDIT_TERM, text.trim());
                    send(toList(toJsonLocaleMessage(data, "messengerbot.loan.running.bd" )));
                    return registerUser(data);
                }

                return errorRetry(data, termValidator.getErrors());
            case SessionData.CONSUMER_CREDIT_DOCUMENT_TYPE:
                data.setAnswer(SessionData.CONSUMER_CREDIT_DOCUMENT_NUMBER, "");
                boolean valid = getIdentityDocumentTypes()
                        .stream().anyMatch(e -> e.getId().toString().trim().equals(text.trim()));
                if (valid) {
                    data.setCurrentAnswer(text.trim());
                    return toList(jsonGoTo(SessionData.CONSUMER_CREDIT_DOCUMENT_NUMBER, data));
                } else{
                    return errorRetryLocale(data, "messengerbot.error.buttons");
                }
            case SessionData.CONSUMER_CREDIT_DOCUMENT_NUMBER_SURE:
                if (isCurrentAnswerYes(text, data)) {
                    User user = this.getUser(data);
                    LoanApplication loanApplication = loanApplicationDAO.getActiveLoanApplicationByPerson(data.getLocale(), user.getPersonId(), ProductCategory.CONSUMO);

                    if(loanApplication != null) {
                        String loanLink = getLoanUrlCon(loanApplication, true);
                        String json = MessageFactory.newCTAMessage(messageSource.getMessage("messengerbot.loan.already.active", null, data.getLocale()),
                                MessageFactory.newWebUrlButton(messageSource.getMessage("messengerbot.loan.running.finished", null, data.getLocale()), loanLink)
                        ).toJson(senderId, gson);

                        data.setCurrentState(SessionData.CONSUMER_CREDIT_FINISHED);

                        return toList(json);
                    }

                    return toList(jsonGoTo(SessionData.CONSUMER_CREDIT_REASON, data));
                } else {
                    return toList(jsonGoTo(SessionData.CONSUMER_CREDIT_DOCUMENT_TYPE, data));
                }
            case SessionData.CONSUMER_CREDIT_REASON:
                data.setAnswer(SessionData.CONSUMER_CREDIT_REASON, "");
                boolean validReason = catalogService.getLoanApplicationReasons(data.getLocale())
                        .stream().anyMatch(e -> e.getId().toString().trim().equals(text.trim()));

                if(validReason) {
                    data.setCurrentAnswer(text.trim());
                    return toList(jsonGoTo(SessionData.CONSUMER_CREDIT_AMOUNT, data));
                } else {
                    return toList(jsonGoTo(SessionData.CONSUMER_CREDIT_REASON, data));
                }
            case SessionData.CONSUMER_CREDIT_FINISHED:
                User user = this.getUser(data);
                LoanApplication loanApplication = loanApplicationDAO.getActiveLoanApplicationByPerson(data.getLocale(), user.getPersonId(), ProductCategory.CONSUMO);

                String loanLink = getLoanUrlCon(loanApplication, true);
                String json = MessageFactory.newCTAMessage(messageSource.getMessage("messengerbot.loan.already.active", null, data.getLocale()),
                        MessageFactory.newWebUrlButton(messageSource.getMessage("messengerbot.loan.running.finished", null, data.getLocale()), loanLink)
                ).toJson(senderId, gson);

                data.setCurrentState(SessionData.CONSUMER_CREDIT_FINISHED);

                return toList(json);
            default:
                return generalModule.getPostBackReplies(senderId,text,dataArray);
        }
    }

    @Override
    public List<String> getPostBackReplies(String senderId, String text, SessionData[] dataArray) throws Exception {
        return this.getReplies(senderId,text,dataArray);
    }

    @Override
    public String jsonGoTo(String destinyState, SessionData data) throws Exception {
        switch (destinyState) {
            case SessionData.CONSUMER_PICK_COUNTRY:
                List<Button> buttonListCountry = new ArrayList<>();

                buttonListCountry.add(MessageFactory.newPostBackButton("Perú","_peru_"));
                buttonListCountry.add(MessageFactory.newPostBackButton("Argentina","_argentina_"));
                buttonListCountry.add(MessageFactory.newPostBackButton("Otro","_other_"));
                data.setCurrentState(SessionData.CONSUMER_PICK_COUNTRY);
                return MessageFactory.newQuickReplyMessage(
                        messageSource.getMessage("messengerbot.loan.location", null, data.getLocale())
                        ,buttonListCountry).toJson(data.getProfile().getId(),gson);

            case SessionData.CONSUMER_CREDIT_DOCUMENT_TYPE:
                data.setAnswer(SessionData.CONSUMER_CREDIT_DOCUMENT_NUMBER, "");
                List<Button> buttonList = //new ArrayList<>();
                        getIdentityDocumentTypes().stream().map(
                                d -> MessageFactory.newPostBackButton(d.getName(), d.getId().toString())
                        ).collect(Collectors.toList());
                data.setCurrentState(destinyState);
                return MessageFactory.newQuickReplyMessage(
                        messageSource.getMessage("messengerbot.loan.doctype.go", null, data.getLocale()),
                        buttonList).toJson(data.getProfile().getId(), gson);
            case SessionData.CONSUMER_CREDIT_DOCUMENT_NUMBER:
                data.setCurrentState(destinyState);
                return toJsonLocaleMessage(data, "messengerbot.loan.docnumber.go");
            case SessionData.CONSUMER_CREDIT_DOCUMENT_NUMBER_SURE:
                data.setCurrentState(destinyState);
                return jsonSureOptions(data);
            case SessionData.CONSUMER_CREDIT_REASON:
                List<Button> buttonListReason = getLoanApplicationReasons(data.getLocale()).stream().map(
                                r -> MessageFactory.newPostBackButton(messageSource.getMessage(r.getTextInt(), null,data.getLocale()), r.getId().toString())
                ).collect(Collectors.toList());
                data.setCurrentState(destinyState);
                return MessageFactory.newQuickReplyMessage(messageSource.getMessage("messengerbot.loan.reason.go", null,data.getLocale()),
                        buttonListReason).toJson(data.getProfile().getId(), gson);
            case SessionData.CONSUMER_CREDIT_TERM:
                data.setCurrentState(destinyState);
                return toJsonLocaleMessage(data, "messengerbot.loan.term.go");
            case SessionData.CONSUMER_CREDIT_AMOUNT:
                data.setCurrentState(destinyState);
                return toJsonLocaleMessage(data, "messengerbot.loan.amount.go.range");
            case SessionData.CONSUMER_CREDIT_FINISHED:
                User user = this.getUser(data);
                LoanApplication loanApplication = loanApplicationDAO.getActiveLoanApplicationByPerson(data.getLocale(), user.getPersonId(), ProductCategory.CONSUMO);

                String loanLink = getLoanUrlCon(loanApplication, true);
                String json = MessageFactory.newCTAMessage(messageSource.getMessage("messengerbot.loan.running.finished.title", null, data.getLocale()),
                        MessageFactory.newWebUrlButton(messageSource.getMessage("messengerbot.loan.running.finished", null, data.getLocale()), loanLink)
                ).toJson(data.getProfile().getId(), gson);

                data.setCurrentState(SessionData.CONSUMER_CREDIT_FINISHED);

                return json;
        }

        return null;
    }

    private User getUser(SessionData data) throws Exception {
        User user = userService.getOrRegisterUser(Integer.valueOf(data.getAnswer(SessionData.CONSUMER_CREDIT_DOCUMENT_TYPE)), data.getAnswer(SessionData.CONSUMER_CREDIT_DOCUMENT_NUMBER), null, null, null);
        return user;
    }

    List<LoanApplicationReason> getLoanApplicationReasons(Locale locale) throws Exception{
        return catalogService.getLoanApplicationReasons(locale).stream().filter(e->e.isMessengerShown()).collect(Collectors.toList());
    }

    private List<String> registerUser(SessionData data) throws Exception{
        String senderId = data.getProfile().getId();

        User user = null;
        User userMessenger = userDAO.getUserByFacebookMessengerId(senderId);

        Integer documentType = Util.intOrNull(data.getAnswer(SessionData.CONSUMER_CREDIT_DOCUMENT_TYPE));
        String documentNumber = data.getAnswer(SessionData.CONSUMER_CREDIT_DOCUMENT_NUMBER);

        if(userMessenger != null) {
            Person person = personDAO.getPerson(catalogService, data.getLocale(), userMessenger.getPersonId(), false);
            String docNumberMessenger = person.getDocumentNumber();
            Integer docTypeMessenger = person.getDocumentType().getId();
            if(!documentNumber.equals(docNumberMessenger) || !documentType.equals(docTypeMessenger)){
                send(toList(toJsonLocaleMessage(data, "messengerbot.document.mismatch")));
                return toList(jsonGoTo(SessionData.CONSUMER_CREDIT_DOCUMENT_TYPE, data));
            }
            user = userMessenger;
        }

        if(user == null) {
            user = userService.getOrRegisterUser(Integer.valueOf(data.getAnswer(SessionData.CONSUMER_CREDIT_DOCUMENT_TYPE)), data.getAnswer(SessionData.CONSUMER_CREDIT_DOCUMENT_NUMBER), null, null, null);
            userDAO.registerFacebookMessengerId(user.getId(), senderId);
        }

        LoanApplication loanApplication = loanApplicationDAO.registerLoanApplication(user.getId(), null, null, null, null, null, null, LoanApplication.ORIGIN_MESSENGER, null, null,
                null, CountryParam.COUNTRY_PERU);
        loanApplicationDAO.updateProductCategory(loanApplication.getId(), ProductCategory.CONSUMO);
        loanApplicationDAO.updateAmount(loanApplication.getId(), Integer.valueOf(data.getAnswer(SessionData.CONSUMER_CREDIT_AMOUNT)));
        loanApplicationDAO.updateInstallments(loanApplication.getId(), Integer.valueOf(data.getAnswer(SessionData.CONSUMER_CREDIT_TERM)));
        loanApplicationDAO.updateReason(loanApplication.getId(), Integer.valueOf(data.getAnswer(SessionData.CONSUMER_CREDIT_REASON)));

        loanApplication = loanApplicationDAO.getLoanApplication(loanApplication.getId(), data.getLocale());

        int firstProcessQuestion = evaluationService.getEvaluationProcessByLoanApplication(loanApplication).getFromSelfEvaluationQuestionId();
        loanApplication.getQuestionSequence().add(new ProcessQuestionSequence(firstProcessQuestion, ProcessQuestionSequence.TYPE_FORWARD, new Date(), new Date(), null));
        loanApplicationDAO.updateQuestionSequence(loanApplication.getId(), new Gson().toJson(loanApplication.getQuestionSequence()));
        loanApplicationDAO.updateCurrentQuestion(loanApplication.getId(), ProcessQuestion.Question.Constants.RUN_IOVATION);
        loanApplicationDAO.updateSourceMediumCampaign(loanApplication.getId(), "facebook", "messenger", null);

        List<Agent> agents = catalogService.getFormAssistantsAgents(null);

        int randomIndex = (int)Math.random() * agents.size();

        Agent agent = agents.get(randomIndex);

        loanApplicationDAO.updateFormAssistant(loanApplication.getId(), agent.getId());

        String loanLink = getLoanUrlCon(loanApplication, true);
        String json = MessageFactory.newCTAMessage(messageSource.getMessage("messengerbot.loan.running.finished.title", null, data.getLocale()),
                MessageFactory.newWebUrlButton(messageSource.getMessage("messengerbot.loan.running.finished", null, data.getLocale()), loanLink)
        ).toJson(senderId, gson);

        data.setCurrentState(SessionData.CONSUMER_CREDIT_FINISHED);

        return toList(json);
    }

    String getLoanUrlCon(LoanApplication la, boolean confirmationLink) {
        String userLoanCrypto;
        if (confirmationLink) {
            Map<String, Object> params = new HashMap<>();
            params.put("confirmationlink", true);
            userLoanCrypto = getLoanApplicationService()
                    .generateLoanApplicationToken(
                            la.getUserId(),
                            la.getPersonId(),
                            la.getId(),
                            params);
        } else {
            userLoanCrypto = getLoanApplicationService()
                    .generateLoanApplicationToken(
                            la.getUserId(),
                            la.getPersonId(),
                            la.getId());
        }
        String token = evaluationService.generateEvaluationToken(la.getUserId(), la.getPersonId(), la.getId());
        String baseUrl = Configuration.getClientDomain();
        return baseUrl + "/credito-de-consumo/evaluacion/" + token;
    }

    @Override
    public UserCLService getUserCLService() {
        return userCLService;
    }

    @Override
    public MessengerModule getGlobalModule() {
        return null;
    }

    @Override
    public MessageSource getMessageSource() {
        return messageSource;
    }

    @Override
    public Gson getGson() {
        return gson;
    }

    @Override
    public UserDAO getUserDao() {
        return userDAO;
    }

    @Override
    public LoanApplicationDAO getLoanApplicationDAO() {
        return loanApplicationDAO;
    }

    @Override
    public LoanApplicationService getLoanApplicationService() {
        return loanApplicationService;
    }

    @Override
    public boolean isProduct() {
        return false;
    }

    @Override
    public String[] options() {
        return new String[0];
    }

    public MessengerHelper getHelper() {
        return helper;
    }

    public void setHelper(MessengerHelper helper) {
        this.helper = helper;
    }
}
