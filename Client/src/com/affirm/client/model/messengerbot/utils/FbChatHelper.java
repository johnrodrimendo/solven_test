package com.affirm.client.model.messengerbot.utils;

import com.affirm.client.model.form.UserRegisterForm;
import com.affirm.client.model.messengerbot.SessionData;
import com.affirm.client.model.messengerbot.contract.Attachment;
import com.affirm.client.model.messengerbot.contract.*;
import com.affirm.client.model.messengerbot.modular.MessengerModule;
import com.affirm.client.model.messengerbot.profile.FbProfile;
import com.affirm.client.service.MessengerSession;
import com.affirm.client.service.UserCLService;
import com.affirm.client.service.impl.MessengerSessionHashMap;
import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.dao.UserDAO;
import com.affirm.common.model.catalog.*;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.LoanApplicationService;
import com.affirm.common.util.*;
import com.affirm.system.configuration.Configuration;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Replies to fb messagaes (or postbacks).<br/>
 * Embed a service that takes input text and gives some output here to make a bot <br/>
 *
 * @author siddharth
 */
//@Component
public class FbChatHelper implements MessengerHelper {//WISHLIST reset CTA on persistent menu
    private static final Logger logger = Logger.getLogger(FbChatHelper.class);
    private static String profileLink = "https://graph.facebook.com/v2.6/SENDER_ID?access_token=" + System.getenv("PAGE_TOKEN");
    public static final boolean debug = !Configuration.hostEnvIsProduction();//extra functionality and logs as message
    private static final boolean reasonOptionCTAs = false;//Use CTAs instead of quickReplyButtons.//WISHLIST MAKE REASON CTA WORK IN ANY STATE
    private static final boolean amountShowRange = true;//insert min and max values when asking for amount

    @Autowired
    LoanApplicationService loanApplicationService;
    @Autowired
    UserCLService userClService;
    @Autowired
    CatalogService catalogService;
    @Autowired
    UserDAO userDao;
    @Autowired
    PersonDAO personDAO;
    @Autowired
    LoanApplicationDAO loanApplicationDAO;
    @Autowired
    MessengerSession messengerSession;
    @Autowired
    private MessageSource messageSource;

    private Gson gson = new Gson();

    /**
     * Postback payloads
     */
    private static String PB_INIT = "_init_";
    private static String PB_CREDITS = "_credits_";
    private static String PB_CONTACT = "_contact_";
    private static String PB_RESET = "_reset_";
    private static String PB_REGEN = "_regen_";

    /**
     * NAVIGATION, sets next state, creates the json reply, optionally adds a localized prefix
     */
    @Override
    public String jsonGoTo(String destinyState, SessionData data) throws Exception {
        String senderId = data.getProfile().getId();
        Message fbMsg;
        data.setCurrentState(destinyState);
        switch (destinyState) {
            //generic
            case SessionData.STATE_INIT://OPTIONS CREDIT OR CONTACT
                Button b1 = MessageFactory.newPostBackButton(messageSource.getMessage("messengerbot.init.credits", null, data.getLocale()), PB_CREDITS);
                Button b2 = MessageFactory.newPostBackButton(messageSource.getMessage("messengerbot.init.contact", null, data.getLocale()), PB_CONTACT);
                //Button b3 = MessageFactory.newShareButton("Compartir");//WISHLIST DELIVER A SHARE MESSAGE (MAYBE NOT HERE...)
                if (!data.isPresentAnswer(SessionData.STATE_INIT)) {//first time
                    data.setCurrentAnswer("seteado");
                    fbMsg = MessageFactory.newElementsMessage(MessageFactory.newImageElement(
                            messageSource.getMessage("messengerbot.init.options", null, data.getLocale()).replace("FIRST_NAME", data.getProfile().getFirstName()),//subtitle
                            null,
                            "http://www.solven.pe/img/landing_bg-min.jpg",
                            b1, b2));//buttons
                } else {//comes back
                    fbMsg = MessageFactory.newQuickReplyMessage(messageSource.getMessage("messengerbot.init.options.retry", null, data.getLocale()),
                            b1, b2);
                }
                return fbMsg.toJson(senderId, gson);
            case SessionData.STATE_SILENT:
                return toJsonLocaleMessage(data, "messengerbot.silent.go");
            //loanApp
            case SessionData.STATE_REASON:
                User user = userDao.getUserByFacebookMessengerId(senderId);
                data.setUser(user);
                if (user != null) {
                    LoanApplication loanApp = loanApplicationDAO.getActiveLoanApplicationByPerson(data.getLocale(), user.getPersonId(), Product.TRADITIONAL);
                    if (loanApp != null) {//tiene loan app activo
                        data.setAnswer(SessionData.STATE_DOC_NUMBER, "");
                        List<Button> buttonList = new ArrayList<>();
                        //if phone exists or loan app is rejected deliver answer without check
                        if (user.getPhoneNumber() != null) {
                            //if it has celphone or you loan is rejected just deliver link
                            return jsonEndLoanApp(data, loanApp, new boolean[]{false});
                        } else {// validate doc to open 30 seconds
                            //Redirect todoc type with custom message
                            for (IdentityDocumentType docType : catalogService.getIdentityDocumentTypes()) {
                                buttonList.add(MessageFactory.newPostBackButton(docType.getName(), docType.getId().toString()));
                            }
                            //ask for document validation
                            data.setCurrentState(SessionData.STATE_DOC_TYPE);
                            return MessageFactory.newQuickReplyMessage(
                                    messageSource.getMessage("messengerbot.loan.active.doctype.go", null, data.getLocale()),
                                    buttonList).toJson(senderId, gson);
                        }
                    }
                }
                //normal reason
                List<Button> bList = catalogService.getLoanApplicationReasonsMini(data.getLocale()).stream()
                        .map(r -> MessageFactory.newPostBackButton(
                                r.getReason(),
                                r.getId() + ";" + r.getDefaultProductId()
                        )).collect(Collectors.toList());
                String sLocal = messageSource.getMessage("messengerbot.loan.reason.go", null, data.getLocale());
                if (reasonOptionCTAs) {
                    fbMsg = MessageFactory.newCTAMessage(sLocal, bList);
                } else {
                    fbMsg = MessageFactory.newQuickReplyMessage(sLocal, bList);
                }
                return fbMsg.toJson(senderId, gson);
            case SessionData.STATE_AMOUNT:
                if (!amountShowRange)
                    return toJsonSimpleMessage(senderId, messageSource.getMessage("messengerbot.loan.amount.go", null, data.getLocale()));
                IntegerFieldValidator aValidator = isAdelanto(data) ?
                        ValidatorUtil.LOANAPPLICATION_AMMOUNT_SHORT_TERM :
                        ValidatorUtil.LOANAPPLICATION_AMMOUNT_TRADITIONAL;
                String min = aValidator.getMinValue().toString();
                String max = aValidator.getMaxValue().toString();
                String[] arr = new String[]{min, max};
                return toJsonSimpleMessage(senderId, messageSource.getMessage("messengerbot.loan.amount.range.go..", arr, data.getLocale()));
            case SessionData.STATE_TERM:
                IntegerFieldValidator termValidator = isAdelanto(data) ?
                        ValidatorUtil.LOANAPPLICATION_DAYS :
                        ValidatorUtil.LOANAPPLICATION_INSTALLMENTS;
                String minT = termValidator.getMinValue().toString();
                String maxT = termValidator.getMaxValue().toString();
                String[] arrT = new String[]{minT, maxT};
                boolean useDays = isAdelanto(data);
                if (useDays) {
                    return toJsonSimpleMessage(senderId, messageSource.getMessage("messengerbot.loan.term.days.go..", arrT, data.getLocale()));
                } else {
                    return toJsonSimpleMessage(senderId, messageSource.getMessage("messengerbot.loan.term.months.go..", arrT, data.getLocale()));
                }
            case SessionData.STATE_DOC_TYPE:
                data.setAnswer(SessionData.STATE_DOC_NUMBER, "");
                List<Button> buttonList = new ArrayList<>();
                for (IdentityDocumentType docType : catalogService.getIdentityDocumentTypes()) {
                    buttonList.add(MessageFactory.newPostBackButton(docType.getName(), docType.getId().toString()));
                }
                return MessageFactory.newQuickReplyMessage(
                        messageSource.getMessage("messengerbot.loan.doctype.go", null, data.getLocale()),
                        buttonList).toJson(senderId, gson);
            case SessionData.STATE_DOC_NUMBER:
                return toJsonSimpleMessage(senderId, messageSource.getMessage("messengerbot.loan.docnumber.go", null, data.getLocale()));
            case SessionData.STATE_DOC_NUMBER_SURE:
                return jsonSureOptions(data);
            default:
                return null;
        }
    }

    @Override
    public void sessionUpdate(SessionData sessionData) {

    }

    /**
     * Handle reply
     */
    public List<String> getReplies(String senderId, String text, SessionData[] dataArray) throws Exception {
        logger.debug("getReplies: " + text);
        if (dataArray[0] == null)
            dataArray[0] = messengerSession.getSessionData(senderId);
        //init session - gets locale
        if (dataArray[0] == null) {
            String link = StringUtils.replace(profileLink, "SENDER_ID", senderId);
            FbProfile profile = getObjectFromUrl(link, FbProfile.class);
            dataArray[0] = messengerSession.initSessionData(senderId, profile, SessionData.STATE_INIT);
        }
        SessionData data = dataArray[0];
        if (Configuration.hostEnvIsStage() && anyMatchCaseInsensitive(text, "clean", "clear", "reset") && debug) {
            messengerSession.clean(senderId);
            return toList(
                    toJsonSimpleMessage(senderId, "Session limpiada."));
        }
        if (Configuration.hostEnvIsStage() && anyMatchCaseInsensitive(text, "cleanOld", "clearAll", "resetAll") && debug) {
            messengerSession = new MessengerSessionHashMap();
            return toList(
                    toJsonSimpleMessage(senderId, "Session limpiada."));
        }
        if (data.getCurrentState().equals(SessionData.STATE_SILENT)) {
            return toList();
        }
        switch (data.getCurrentState()) {
            case SessionData.STATE_INIT:
                String[] creditOptions = messageSource.getMessage("messengerbot.init.creditOptions", null, data.getLocale()).split(";");
                String[] contactOptions = messageSource.getMessage("messengerbot.init.contactOptions", null, data.getLocale()).split(";");
                if (anyContainsCaseInsensitive(text, creditOptions)) {
                    return toList(jsonGoTo(SessionData.STATE_REASON, data));
                } else if (anyContainsCaseInsensitive(text, contactOptions)) {
                    return toList(jsonGoTo(SessionData.STATE_SILENT, data));
                } else {
                    if (!data.isPresentAnswer(data.getCurrentState())) {
                        String mLocaleNotSuported = null;
                        if (!data.isLocaleSupported()) {
                            mLocaleNotSuported = MessageFactory.newSimpleMessage(
                                    "I am sorry. I am not prepared to talk you in your language yet. I will proceed in Spanish")
                                    .toJson(senderId, gson);
                        }
                        return toList(mLocaleNotSuported,
                                jsonGoTo(SessionData.STATE_INIT, data));//first time
                    } else {
                        return toList(jsonGoTo(SessionData.STATE_INIT, data));//comes back
                    }
                }
            case SessionData.STATE_REASON://HandlePostBack
                //looks if you have a existing LoanApp
                return getPostBackReplies(senderId, text, dataArray);
            case SessionData.STATE_AMOUNT:
                boolean useDays = isAdelanto(data);
                if (useDays) {
                    IntegerFieldValidator validator = new IntegerFieldValidator(
                            ValidatorUtil.LOANAPPLICATION_AMMOUNT_SHORT_TERM,
                            Integer.parseInt(text));
                    validator.setMaxValueErrorMsg("bot.max.money.error.");
                    validator.setMinValueErrorMsg("bot.min.money.error.");
                    if (validator.validate(data.getLocale())) {
                        data.setCurrentAnswer(text);
                        return toList(jsonGoTo(SessionData.STATE_TERM, data));
                    }
                    return errorRetry(data, validator.getErrors());
                } else {
                    IntegerFieldValidator validator = new IntegerFieldValidator(ValidatorUtil.LOANAPPLICATION_AMMOUNT_TRADITIONAL, Integer.parseInt(text));
                    validator.setMaxValueErrorMsg("bot.max.money.error.");
                    validator.setMinValueErrorMsg("bot.min.money.error.");
                    if (validator.validate(data.getLocale())) {
                        data.setCurrentAnswer(text.trim());
                        return toList(jsonGoTo(SessionData.STATE_TERM, data));
                    }
                    return errorRetry(data, validator.getErrors());
                }
            case SessionData.STATE_TERM:
                boolean useDaysTerm = isAdelanto(data);
                IntegerFieldValidator termValidator;
                if (useDaysTerm) {
                    termValidator = new IntegerFieldValidator(ValidatorUtil.LOANAPPLICATION_DAYS, Integer.parseInt(text));
                } else {
                    termValidator = new IntegerFieldValidator(ValidatorUtil.LOANAPPLICATION_INSTALLMENTS, Integer.parseInt(text));
                }
                termValidator.setMaxValueErrorMsg("bot.max.error.");
                termValidator.setMinValueErrorMsg("bot.min.error.");
                if (termValidator.validate(data.getLocale())) {
                    data.setCurrentAnswer(text.trim());
                    return toList(jsonGoTo(SessionData.STATE_DOC_TYPE, data));
                }
                return errorRetry(data, termValidator.getErrors());
            case SessionData.STATE_DOC_TYPE:
                boolean validKey = catalogService.getIdentityDocumentTypes()
                        .stream().anyMatch(e -> e.getId().toString().equalsIgnoreCase(text));
                String key = catalogService.getIdentityDocumentTypes().stream()
                        .filter(e -> e.getName().equalsIgnoreCase(text))
                        .map(e -> e.getId().toString()).findAny().orElse(null);
                if (validKey || key != null) {
                    if (validKey)
                        data.setCurrentAnswer(text);
                    if (key != null)
                        data.setCurrentAnswer(key);
                    return toList(jsonGoTo(SessionData.STATE_DOC_NUMBER, data));
                } else {
                    return errorRetryLocale(data, "messengerbot.error.buttons");
                }
            case SessionData.STATE_DOC_NUMBER:
                Integer docTypeId = Integer.parseInt(data.getAnswer(SessionData.STATE_DOC_TYPE));
                StringFieldValidator vDocNumber = null;
                //set validator
                if (docTypeId == IdentityDocumentType.DNI) {
                    vDocNumber = new StringFieldValidator(ValidatorUtil.DOC_NUMBER_DNI, text.trim());
                } else if (docTypeId == IdentityDocumentType.CE) {
                    vDocNumber = new StringFieldValidator(ValidatorUtil.DOC_NUMBER_CE, text.trim());
                } else {
                    return errorRetry(data, "Tipo de documento inválido!");//this should never get to the user
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
                    return toList(jsonGoTo(SessionData.STATE_DOC_NUMBER_SURE, data));//valid loop so it will go to PB
                }
                return errorRetry(data, vDocNumber.getErrors());
            case SessionData.STATE_DOC_NUMBER_SURE:
                return getPostBackReplies(senderId, text, dataArray);//handle yes no
            default:
                return toList(jsonGoTo(SessionData.STATE_INIT, data));
        }
    }

    public String jsonEndLoanApp(SessionData data, LoanApplication newloanApplication, boolean[] esNuevo, LoanApplication... las) throws Exception {
        if (newloanApplication == null)
            newloanApplication = createLoanApp(data, esNuevo);
        String loanLink = getLoanUrl(newloanApplication);
        Locale locale = data.getLocale();
        String senderId = data.getProfile().getId();
        String messageEnd = "messengerbot.loan.active.withnumber.end";
        String json = MessageFactory.newCTAMessage(messageSource.getMessage(messageEnd, null, locale),
                MessageFactory.newWebUrlButton(messageSource.getMessage("messengerbot.loan.active.link", null, locale), loanLink),
                MessageFactory.newWebUrlButton(messageSource.getMessage("messengerbot.loan.active.main", null, locale), Configuration.CLIENT_PRD_URL)
        ).toJson(senderId, gson);
        if (data.getUser().getPhoneNumber() == null) {// open 30 seconds (regen)
            loanApplicationDAO.registerNoAuthLinkExpiration(newloanApplication.getId(), 60);
            messageEnd = "messengerbot.loan.active.nonumber.end";
            json = MessageFactory.newCTAMessage(messageSource.getMessage(messageEnd, null, locale),
                    MessageFactory.newWebUrlButton(messageSource.getMessage("messengerbot.loan.active.link", null, locale), loanLink),
                    MessageFactory.newPostBackButton(messageSource.getMessage("messengerbot.loan.active.nonumber.regen", null, locale), PB_REGEN)
            ).toJson(senderId, gson);
        }
        if (esNuevo[0]) {
            messageEnd = "messengerbot.loan.new.end";//nuevo
            //loanApplicationDAO.updateMessengerLink(newloanApplication.getId(), true);
        }
        messengerSession.clean(senderId);
        messengerSession.cleanOld();//removes old conversations
        return json;
    }

    /**
     * Handle postback replies
     */
    public List<String> getPostBackReplies(String senderId, String text, SessionData[] dataArray) throws Exception {
        logger.debug("getPostBackReplies: " + text);
        if (dataArray[0] == null)
                dataArray[0] = messengerSession.getSessionData(senderId);
        //init session - gets locale
        if (dataArray[0] == null) {
            String link = StringUtils.replace(profileLink, "SENDER_ID", senderId);
            FbProfile profile = getObjectFromUrl(link, FbProfile.class);
            dataArray[0] = messengerSession.initSessionData(senderId, profile, SessionData.STATE_INIT);
        }
        SessionData data = dataArray[0];
        //GENERAL ACTIONS
        if (text.equals(PB_INIT)) {//WELCOME SCREEN
            String mLocale = null;
            if (!data.isLocaleSupported()) {
                mLocale = MessageFactory.newSimpleMessage(
                        "I am sorry. I am not prepared to talk you in your language yet. I will proceed in Spanish")
                        .toJson(senderId, gson);
            }
            return toList(mLocale, jsonGoTo(SessionData.STATE_INIT, data));
        }
        if (text.equals(PB_CREDITS)) {//SEND TO REASON
            data.clearAnswers();
            return toList(jsonGoTo(SessionData.STATE_REASON, data));
        }
        if (text.equals(PB_CONTACT)) {//PERSISTENT SEND A MESSAGE.
            return toList(jsonGoTo(SessionData.STATE_SILENT, data));
        }
        if (text.equals(PB_RESET)) {//PERSISTENT SEND A MESSAGE.
            Locale locale = data.getLocale();
            messengerSession.clean(senderId);
            return toList(toJsonSimpleMessage(senderId, messageSource.getMessage("messengerbot.reset.go", null, locale)));
        }
        if (text.equals(PB_REGEN)) {
            try {
                if (data.getUser() == null) {
                    User user = userDao.getUserByFacebookMessengerId(senderId);
                    data.setUser(user);
                }
                LoanApplication loanApp = loanApplicationDAO.getActiveLoanApplicationByPerson(data.getLocale(), data.getUser().getPersonId(), Product.TRADITIONAL);
                return toList(
                        toJsonSimpleMessage(senderId, messageSource.getMessage("messengerbot.loan.active.nonumber.regen.done", null, data.getLocale())),
                        jsonEndLoanApp(data, loanApp, new boolean[]{false}));
            } catch (Exception e) {
                logger.debug("Error al regenerar el loanApp", e);
                return toList(
                        toJsonSimpleMessage(senderId, messageSource.getMessage("messengerbot.loan.active.nonumber.regen.fail", null, data.getLocale())),
                        jsonGoTo(SessionData.STATE_REASON, data));
            }
        }
        //WISHLIST Handle payloads of postback buttons. OR use QuickReplies//Update currently using quick replies works like charm
        //HANDLE SILENT STATE
        if (data.getCurrentState().equals(SessionData.STATE_SILENT)) {
            return null;
        }
        //ACTIONS PER STATE
        switch (data.getCurrentState()) {
            case SessionData.STATE_REASON:
                LoanApplicationReason reason = catalogService.getLoanApplicationReasonsMini(data.getLocale()).stream()
                        .filter(r -> r.getId().toString().equals(text.split(";")[0]) || r.getReason().equalsIgnoreCase(text))//idReason
                        .findFirst().orElse(null);
                if (reason != null) {
                    data.setCurrentAnswer(reason.getId() + ";" + reason.getDefaultProductId());
                    return toList(jsonGoTo(SessionData.STATE_AMOUNT, data));
                } else {
                    return toList(jsonGoTo(SessionData.STATE_REASON, data));
                }
            case SessionData.STATE_DOC_TYPE:
                data.setAnswer(SessionData.STATE_DOC_NUMBER, "");
                boolean valid = catalogService.getIdentityDocumentTypes()
                        .stream().anyMatch(e -> e.getId().toString().equals(text));
                if (valid) {
                    data.setCurrentAnswer(text);
                    return toList(jsonGoTo(SessionData.STATE_DOC_NUMBER, data));
                } else {
                    return errorRetryLocale(data, "messengerbot.prefix");
                }
            case SessionData.STATE_DOC_NUMBER_SURE://ARE YOU SURE?
                if (isSure(text, data)) {//validate documents
                    User user = data.getUser();//User de messenger maybe
                    //VALIDA AL DE MESSENGER
                    if (user != null) {//Si los datos de documentos no coinciden en MessengerBD y input de usuario lo botas
                        Person person = personDAO.getPerson(catalogService, data.getLocale(), data.getUser().getPersonId(), false);
                        if (!person.getDocumentType().getId().toString().equals(data.getAnswer(SessionData.STATE_DOC_TYPE).trim())
                                || !person.getDocumentNumber().equals(data.getAnswer(SessionData.STATE_DOC_NUMBER).trim())) {
                            return toList(
                                    toJsonLocaleMessage(data, "messengerbot.document.mismatch"),
                                    jsonGoTo(SessionData.STATE_DOC_TYPE, data));
                        }
                    } else {
                        user = getUserByDocumentOrCreate(data);
                    }
                    LoanApplication[] las = new LoanApplication[]{null};
                    List<String> listJson = toList(jsonEndLoanApp(data, null, new boolean[]{false}, las));
                    if (data.getUser().getPhoneNumber() != null)
                        loanApplicationDAO.registerNoAuthLinkExpiration(las[0].getId(), 60);
                    return listJson;
                } else {
                    return toList(jsonGoTo(SessionData.STATE_DOC_TYPE, data));
                }
            default:
                return null;
        }
    }
/*
    private Optional<String> getLoanAppLinkJsonMessage(SessionData data) {
        try {
            String senderId = data.getProfile().getId();
            Integer personId = personDAO.getPersonIdByFacebookId(senderId);
            LoanApplication la = loanApplicationDAO.getActiveLoanApplicationByPerson(data.getLocale(), personId);
            if (la != null) {
                String loanUrl = getLoanUrl(la);
                String linkMessage = messageSource.getMessage("messengerbot.loan.active", null, data.getLocale());
                Button ofertas = MessageFactory.newWebUrlButton(messageSource.getMessage("messengerbot.loan.active.link", null, data.getLocale()), loanUrl);
                Button solvenPage = MessageFactory.newWebUrlButton(messageSource.getMessage("messengerbot.loan.active.main", null, data.getLocale()), Configuration.CLIENT_PRD_URL);
                Message fbMsg = MessageFactory.newCTAMessage(linkMessage, ofertas, solvenPage);
                return Optional.ofNullable(fbMsg.toJson(senderId, gson));
            }
        } catch (Exception e) {
        }
        return Optional.empty();
    }*/

    /**
     * User is null if invalid phone is provided
     */
    private User getUserByDocumentOrCreate(SessionData data) throws Exception {
        User user = null;
        Integer doctype = Integer.parseInt(data.getAnswer(SessionData.STATE_DOC_TYPE));
        String docnumber = data.getAnswer(SessionData.STATE_DOC_NUMBER);
        user = userDao.getUserByDocument(doctype, docnumber);
        if (user == null) {
            //si no existe crearlo
            UserRegisterForm userForm = new UserRegisterForm();
            userForm.setDocNumber(docnumber);
            userForm.setDocType(doctype);
            // Register the user
            user = userClService.registerUserFacebookMessenger(userForm, data.getProfile().getId());
        }
        userDao.registerFacebookMessengerId(user.getId(), data.getProfile().getId());
        data.setUser(user);
        return user;
    }

    private LoanApplication createLoanApp(SessionData data, boolean[] esNuevo) throws Exception {
        LoanApplication newloanApplication = loanApplicationDAO.getActiveLoanApplicationByPerson(data.getLocale(), data.getUser().getPersonId(), Product.TRADITIONAL);
        if (newloanApplication != null)
            return newloanApplication;

        logger.debug(data);
        Integer userId = data.getUser().getId();
        Integer ammount = Integer.parseInt(data.getAnswer(SessionData.STATE_AMOUNT));
        Integer reason = Integer.parseInt(data.getAnswer(SessionData.STATE_REASON).split(";")[0]);
        Integer productId;
        Integer declaredClusterId = 7;//default value
        //set days or months
        Integer monthsOr1 = 1;//default value
        Integer daysOrNull = null;//default value
        if (isAdelanto(data)) {
            //set days
            daysOrNull = Integer.parseInt(data.getAnswer(SessionData.STATE_TERM));
            productId = Product.SHORT_TERM;
        } else {
            // set months
            monthsOr1 = Integer.parseInt(data.getAnswer(SessionData.STATE_TERM));
            productId = Product.TRADITIONAL;
        }
        newloanApplication = loanApplicationDAO.registerLoanApplication(
                userId,
                ammount,
                monthsOr1,
                reason,
                productId,
                daysOrNull,
                declaredClusterId,
                LoanApplication.ORIGIN_MESSENGER, null, LoanApplicationRegisterType.DNI, null, CountryParam.COUNTRY_PERU);

        // Solucion un poco fea :(
        newloanApplication = loanApplicationDAO.getLoanApplication(newloanApplication.getId(), data.getLocale());

        // Check if there is a preliminaryEvaluation
        LoanApplicationPreliminaryEvaluation preEvaluation = loanApplicationService
                .getLastPreliminaryEvaluation(newloanApplication.getId(), data.getLocale(), null);
        if (preEvaluation == null) {

            // TODO Call the evaluation bot
            
            // Call the start preliminary evaluation
//            loanApplicationDAO.startPreliminaryEvaluation(newloanApplication.getId());

            // Execute the evaluation
//            loanApplicationDAO.executePreliminaryEvaluation(newloanApplication.getId());
            esNuevo[0] = true;
        }
        return newloanApplication;
    }

    private String getLoanUrl(LoanApplication la) {
        JSONObject jsonParam = new JSONObject();
        jsonParam.put("loan", la.getId());
        jsonParam.put("user", la.getUserId());
        jsonParam.put("person", la.getPersonId());
        String userLoanCrypto = CryptoUtil.encrypt(jsonParam.toString());
        String baseUrl = Configuration.hostEnvIsProduction() ? Configuration.CLIENT_PRD_URL : Configuration.CLIENT_STG_URL;
        return baseUrl + "/loanapplication/" + userLoanCrypto;
    }

    /**
     * Sure json
     */
    public String jsonSureOptions(SessionData data) {
        String yes = messageSource.getMessage("messengerbot.yes", null, data.getLocale()).split(";")[0];
        String no = messageSource.getMessage("messengerbot.no", null, data.getLocale()).split(";")[0];
        return MessageFactory.newQuickReplyMessage(
                messageSource.getMessage("messengerbot.sure", null, data.getLocale()),
                MessageFactory.newPostBackButton(yes, yes),
                MessageFactory.newPostBackButton(no, no)
        ).toJson(data.getProfile().getId(), gson);
    }

    /**
     * Handle sure json reply
     */
    public boolean isSure(String text, SessionData data) {
        logger.debug("is Sure check. INIT");
        String[] yesOptions = messageSource.getMessage("messengerbot.yes", null, data.getLocale()).split(";");
        String[] noOptions = messageSource.getMessage("messengerbot.no", null, data.getLocale()).split(";");
        boolean sure = true;//I am sure
        sure = !anyContainsCaseInsensitive(text, noOptions);//I may not be sure
        if (!sure)//i I am not sure
            return false;///return I am not sure
        sure = anyContainsCaseInsensitive(text, yesOptions);//I may be sure or not
        logger.debug("is Sure check. END");
        return sure;//return that.
    }

    /**
     * On debug dumps data to messenger
     */
    public List<String> errorRetryLocale(SessionData data, String localeKey) throws Exception {
        String errorText = messageSource.getMessage(localeKey, null, data.getLocale());
        if (Configuration.hostEnvIsStage() && localeKey.equals("messengerbot.oops") && debug)
            return toList(
                    toJsonSimpleMessage(data.getProfile().getId(), data.toString()),//dump data
                    toJsonSimpleMessage(data.getProfile().getId(), errorText),//error message
                    jsonGoTo(data.getCurrentState(), data));//retry
        return errorRetry(data, errorText);
    }

    public List<String> errorRetry(SessionData data, String errorText) throws Exception {
        return toList(
                toJsonSimpleMessage(data.getProfile().getId(), errorText),
                jsonGoTo(data.getCurrentState(), data));
    }

    public boolean isAdelanto(SessionData data) throws Exception {
        String reasonText = data.getAnswer(SessionData.STATE_REASON);
        boolean isAdelantoText = catalogService.getLoanApplicationReasonsMini(data.getLocale()).stream()
                .filter(r -> r.getId() == LoanApplicationReason.ADELANTO && r.getReason().equalsIgnoreCase(reasonText))
                .count() > 0;
        if (isAdelantoText) return isAdelantoText;
        String[] reasonIdAndJsonProducts = data.getAnswer(SessionData.STATE_REASON).split(";");
        if (reasonIdAndJsonProducts.length < 2)
            return false;
        String jsonProductsString = reasonIdAndJsonProducts[1];
        JSONArray array = new JSONArray(jsonProductsString);
        List<Integer> list = JsonUtil.getListFromJsonArray(array, (arr, i) -> arr.getInt(i));
        boolean isAdelantoId = list.stream().filter(p -> !p.equals(Product.SHORT_TERM))
                .count() == 0;//if short term is the only product, use days
        return isAdelantoId;
    }

    //generic
    public List<String> toList(String... vargs) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < vargs.length; i++) {
            if (vargs[i] != null)
                list.add(vargs[i]);
        }
        return list;
    }

    /**
     * Text matching util
     */
    boolean anyMatchCaseInsensitive(String text, String... options) {
        for (int i = 0; i < options.length; i++) {
            if (text.equalsIgnoreCase(options[i]))
                return true;
        }
        return false;
    }

    /**
     * Text matching util
     */
    boolean anyContainsCaseInsensitive(String text, String... options) {
        for (int i = 0; i < options.length; i++) {
            if (text.toUpperCase().contains(options[i].toUpperCase()))
                return true;
        }
        return false;
    }

    public String toJsonLocaleMessage(SessionData data, String key, String... params) {
        String msg = messageSource.getMessage(key, params, data.getLocale());
        return MessageFactory.newSimpleMessage(msg).toJson(data.getProfile().getId(), gson);
    }

    public String toJsonSimpleMessage(String senderId, String s) {
        return MessageFactory.newSimpleMessage(s).toJson(senderId, gson);
    }

    /**
     * Returns object of type clazz from an json api link
     *
     * @param link
     * @param clazz
     * @return
     * @throws Exception
     */
    private <T> T getObjectFromUrl(String link, Class<T> clazz) throws Exception {
        T t = null;
        URL url;
        String jsonString = "";
      //  try {
            url = new URL(link);
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    url.openStream()));

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                jsonString = jsonString + inputLine;
            }
            in.close();
       /* } catch (IOException e) {
            e.printStackTrace();
        }*/
        if (!StringUtils.isEmpty(jsonString)) {
            Gson gson = new Gson();
            t = gson.fromJson(jsonString, clazz);
        }
        return t;
    }

    /**
     * Generic methods to build fb messages
     */
    private static class MessageFactory {//WISHLIST ANOTHER JsonLocaleFactory. To make code easier to read

        private MessageFactory() {
            throw new RuntimeException("Non instantiable class");
        }

        static Message newSimpleMessage(String msg) {
            Message message = new Message();
            message.setText(msg);
            return message;
        }

        static Message newQuickReplyMessage(String questionStr, Button... buttons) {
            return newQuickReplyMessage(questionStr, Arrays.asList(buttons));
        }

        static Message newQuickReplyMessage(String questionStr, List<Button> buttons) {
            Message message = new Message();
            message.setText(questionStr);
            message.setQuickReplies(buttons.stream().map(btn -> {
                QuickReply quickReply = new QuickReply();
                quickReply.setTitle(btn.getTitle());
                quickReply.setContentType("text");
                quickReply.setImageUrl(btn.getUrl());
                quickReply.setPayload(btn.getPayload());
                return quickReply;
            }).collect(Collectors.toList()));
            return message;// newCTAMessage(questionStr, buttons);//WISHLIST IMPLEMENT QUICK REPLIES
        }

        static Message newCTAMessage(String questionStr, Button... buttons) {
            return newCTAMessage(questionStr, Arrays.asList(buttons));
        }

        static Message newCTAMessage(String questionStr, List<Button> buttons) {
            Payload payload = new Payload();
            payload.setText(questionStr);
            payload.setButtons(buttons);
            payload.setTemplateType("button");

            Attachment attachment = new Attachment();
            attachment.setPayload(payload);
            attachment.setType("template");
            Message message = new Message();
            message.setAttachment(attachment);

            return message;
        }

        //button makers
        private static Button newButton(String title, String type) {
            Button button = new Button();
            button.setType(type);
            button.setTitle(title);
            return button;
        }

        static Button newPostBackButton(String title, String payload) {
            Button button = newButton(title, "postback");
            button.setPayload(payload);
            return button;
        }

        static Button newWebUrlButton(String title, String url) {
            Button button = newButton(title, "web_url");
            button.setUrl(url);
            return button;
        }

        static Button newShareButton(String title) {
            return newButton(title, "element_share");
        }

        //Element Message
        static Message newElementsMessage(Element... elements) {
            Payload payload = new Payload();
            payload.setElements(Arrays.asList(elements));
            payload.setTemplateType("generic");
            Attachment attachment = new Attachment();
            attachment.setPayload(payload);
            attachment.setType("template");
            Message message = new Message();
            message.setAttachment(attachment);
            return message;//the templatetype doesnt allow for pictures to be shown
        }

        //element maker
        static Element newImageElement(String title, String subtitle, String urlImage, Button... buttons) {
            Element e = new Element();
            e.setTitle(title);
            e.setSubtitle(subtitle);
            e.setImageUrl(urlImage);
            e.setButtons(Arrays.asList(buttons));
            return e;
        }

        static Message newListMessage(Element... elements) {
            Payload payload = new Payload();
            payload.setElements(Arrays.asList(elements));
            payload.setTemplateType("list");
            payload.setTopElementStyle("compact");
            Attachment attachment = new Attachment();
            attachment.setPayload(payload);
            attachment.setType("template");
            Message message = new Message();
            message.setAttachment(attachment);
            return message;//the templatetype doesnt allow for pictures to be shown
        }
    }

    @Override
    public List<MessengerModule> getModules() {
        throw new RuntimeException("This helper doesn't allow calling this method. This is not a modular helper.");
    }

    @Override
    public void clear(String senderId) {
        messengerSession.clean(senderId);
    }
}