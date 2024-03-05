package com.affirm.client.model.messengerbot.modular;

import com.affirm.client.model.messengerbot.SessionData;
import com.affirm.client.model.messengerbot.contract.Button;
import com.affirm.client.service.MessengerSession;
import com.affirm.client.service.UserCLService;
import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.dao.UserDAO;
import com.affirm.common.model.catalog.CountryParam;
import com.affirm.common.model.catalog.IdentityDocumentType;
import com.affirm.common.model.catalog.Product;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.LoanApplicationService;
import com.affirm.common.service.impl.SyncBotService;
import com.affirm.common.util.IntegerFieldValidator;
import com.affirm.common.util.StringFieldValidator;
import com.affirm.common.util.Util;
import com.affirm.common.util.ValidatorUtil;
import com.affirm.system.configuration.Configuration;
import com.google.gson.Gson;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Created by jarmando on 18/01/17.
 */
@Component
public class ShortTermModule implements MessengerModule {
    private static final Logger logger = Logger.getLogger(TraditionalModule.class);

    @Autowired
    UserDAO userDao;
    @Autowired
    LoanApplicationDAO loanApplicationDAO;
    @Autowired
    MessengerSession messengerSession;
    @Autowired
    CatalogService catalogService;
    @Autowired
    PersonDAO personDAO;
    @Autowired
    UserCLService userCLService;
    @Autowired
    LoanApplicationService loanApplicationService;
    @Autowired
    MessageSource messageSource;

    Gson gson = new Gson();
    @Autowired
    GlobalModule globalModule;
    @Autowired
    SyncBotService syncBotService;


    @Override
    public MessengerModule getGlobalModule() {
        return globalModule;
    }

    @Override
    public String getModuleName() {
        return SessionData.M_SHORT_TERM;
    }

    Product getProduct() {
        try { return catalogService.getProduct(Product.SHORT_TERM);
        } catch (Exception e) { throw new RuntimeException(e); }
    }

    @Override
    public boolean isActive() {
        return getProduct().getActive();
    }

    public List<String> getRegenerateLinkPostBackReply(String senderId, String text, SessionData[] dataArray) throws Exception {
        SessionData data = dataArray[0];

        return null;
    }

    @Override
    public List<String> getReplies(String senderId, String text, SessionData[] dataArray) throws Exception {
        SessionData data = dataArray[0];
        switch (data.getCurrentState()){
            case SessionData.SHORT_TERM_AMOUNT:
                    IntegerFieldValidator validator = new IntegerFieldValidator(
                            ValidatorUtil.LOANAPPLICATION_AMMOUNT_SHORT_TERM,
                            Integer.parseInt(text));
                    validator.setMaxValueErrorMsg("bot.max.money.error.");
                    validator.setMinValueErrorMsg("bot.min.money.error.");
                    if (validator.validate(data.getLocale())) {
                        data.setCurrentAnswer(text);
                        return toList(jsonGoTo(SessionData.SHORT_TERM_DOC_TYPE, data));
                    }
                    return errorRetry(data, validator.getErrors());
            case SessionData.SHORT_TERM_TERM:
                IntegerFieldValidator termValidator;
                termValidator = new IntegerFieldValidator(ValidatorUtil.LOANAPPLICATION_DAYS, Integer.parseInt(text));
                termValidator.setMaxValueErrorMsg("bot.max.error.");
                termValidator.setMinValueErrorMsg("bot.min.error.");
                if (termValidator.validate(data.getLocale())) {
                    data.setCurrentAnswer(text.trim());
                    return toList(jsonGoTo(SessionData.SHORT_TERM_DOC_TYPE, data));
                }
                return errorRetry(data, termValidator.getErrors());
            case SessionData.SHORT_TERM_DOC_TYPE:
                String key = catalogService.getIdentityDocumentTypes().stream()
                        .filter(e -> e.getName().trim().equalsIgnoreCase(text.trim()))
                        .map(e -> e.getId().toString()).findFirst().orElse(null);
                if (key != null) {
                    data.setCurrentAnswer(key);
                    return toList(jsonGoTo(SessionData.SHORT_TERM_DOC_NUMBER, data));
                }
                else
                    return errorRetryLocale(data, "messengerbot.error.buttons");
            case SessionData.SHORT_TERM_DOC_NUMBER:
                Integer docTypeId = Integer.parseInt(data.getAnswer(SessionData.SHORT_TERM_DOC_TYPE));
                StringFieldValidator vDocNumber = null;
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
                    logger.error("getReplies SessionData.SHORT_TERM_DOC_NUMBER: the current doc type doesnt have a validator.");
                    return toList(jsonGoTo(SessionData.SHORT_TERM_DOC_TYPE, data));
                }
                //validate
                if (vDocNumber.validate(data.getLocale())) {
                    if (docTypeId == IdentityDocumentType.DNI) {
                        Reniec reniec = personDAO.getReniecDBData(text.trim());
                        if (reniec == null) {
                            return errorRetryLocale(data, "bot.error.document.notfound");
                        }
                    }
                    //TODO CE VALIDATION
                    data.setCurrentAnswer(text.trim());
                    return toList(jsonGoTo(SessionData.SHORT_TERM_DOC_NUMBER_SURE, data));
                }
                return errorRetry(data, vDocNumber.getErrors());
            case SessionData.SHORT_TERM_DOC_NUMBER_SURE:
                return getPostBackReplies(senderId, text, dataArray);
            case SessionData.SHORT_TERM_CE_BIRTHDAY:
                Date birthdate = Util.parseDate(text.trim(), "dd/MM/yyyy") != null ?
                        Util.parseDate(text.trim(), "dd/MM/yyyy") :
                        Util.parseDate(text.trim(), "dd-MM-yyyy");
                if(birthdate == null)
                    return errorRetryLocale(data, "bot.error.date");
                Integer queryId = syncBotService.callMigraciones(data.getAnswer(SessionData.SHORT_TERM_DOC_NUMBER), birthdate);
                Configuration.MIGRACION_EXECUTOR.execute(() -> {
                    try{ Thread.sleep(25000); } catch(Exception e) {}
                    boolean correct = syncBotService.migracionesSuccess(queryId);
                    data.setCurrentState(SessionData.GLOBAL_NEW);
                    try {
                        if (correct) {
                            send(endByDocument(data));//LINK!
                        } else {
                            data.setCurrentState(SessionData.SHORT_TERM_DOC_TYPE);
                            send(errorRetryLocale(data, "bot.ce.date.mismatch"));//INCORRECT DATE
                        }
                    } catch (Exception e) {
                        send(toList(toJsonLocaleMessage(data, "system.error.default")));//CONNECTION ERROR.
                    }
                });
                return toList(toJsonLocaleMessage(data, "bot.ce.sync.validation"));
            default:
                logger.error("no case handled the state in prefixed module.");
                throw new Exception("get Replies State not handled in prefixed module");
        }
    }

    @Override
    public List<String> getPostBackReplies(String senderId, String text, SessionData[] dataArray) throws Exception {
        SessionData data = dataArray[0];
        switch (data.getCurrentState()){
            case SessionData.SHORT_TERM_DOC_TYPE:
                data.setAnswer(SessionData.SHORT_TERM_DOC_NUMBER, "");
                boolean valid = catalogService.getIdentityDocumentTypes()
                        .stream().anyMatch(e -> e.getId().toString().trim().equals(text.trim()));
                if (valid) {
                    data.setCurrentAnswer(text.trim());
                    return toList(jsonGoTo(SessionData.SHORT_TERM_DOC_NUMBER, data));
                } else
                    return errorRetryLocale(data, "messengerbot.error.buttons");
            case SessionData.SHORT_TERM_DOC_NUMBER_SURE:
                if (isCurrentAnswerYes(text, data)) {
                    if(data.getAnswer(SessionData.SHORT_TERM_DOC_TYPE).equals(String.valueOf(IdentityDocumentType.CE))) {
                        return toList(jsonGoTo(SessionData.SHORT_TERM_CE_BIRTHDAY, data));
                    }
                    else {//dni end
                        return endByDocument(data);
                    }
                } else {
                    return toList(jsonGoTo(SessionData.SHORT_TERM_DOC_TYPE, data));
                }
            default:
                return getGlobalModule().getPostBackReplies(senderId, text, dataArray);
        }
    }

    private List<String> endByDocument(SessionData data) throws Exception {
        ///init
        String senderId = data.getProfile().getId();
        Integer docType = Util.intOrNull(data.getAnswer(SessionData.SHORT_TERM_DOC_TYPE));
        String docNumber = data.getAnswer(SessionData.SHORT_TERM_DOC_NUMBER);
        //init user
        User userMessenger = userDao.getUserByFacebookMessengerId(senderId);
        User user = null;
        //this messenger exists
        if(userMessenger != null) {
            Person person = personDAO.getPerson(catalogService, data.getLocale(), userMessenger.getPersonId(), false);
            String docNumberMessenger = person.getDocumentNumber();
            Integer docTypeMessenger = person.getDocumentType().getId();
            if(!docNumber.equals(docNumberMessenger) || !docType.equals(docTypeMessenger)){
                return toList(toJsonLocaleMessage(data, "messengerbot.document.mismatch"));
            }
        } else {
            Integer doctype = Integer.parseInt(data.getAnswer(SessionData.SHORT_TERM_DOC_TYPE));
            String docnumber = data.getAnswer(SessionData.SHORT_TERM_DOC_NUMBER);
            user = getUserByDocumentOrCreate(senderId, doctype, docnumber);
            if(user == null){
                logger.warn("Este facebook messenger ya está asociado a otro usuario");
                data.setCurrentState(SessionData.GLOBAL_NEW);
                return errorRetryLocale(data, "bot.error.takenmessenger");
            }
        }
        LoanApplication[] las = new LoanApplication[]{null};
        List<String> listJson = toList(jsonEndLoanApp(data, null, new boolean[]{false}, las));
        //if (data.getUser().getPhoneNumber() != null)
        //    loanApplicationDAO.registerNoAuthLinkExpiration(las[0].getId(), 60);
        return listJson;
    }

    @Override
    public String jsonGoTo(String destinyState, SessionData data) throws Exception {
        switch (destinyState){
            case SessionData.SHORT_TERM_REGEN:
                return renewLink(data);
            case SessionData.SHORT_TERM_AMOUNT:
                User user = getUserDao().getUserByFacebookMessengerId(data.getProfile().getId());
                data.setUser(user);
                if (user != null) {
                    LoanApplication loanApp = getLoanApplicationDAO().getActiveLoanApplicationByPerson(data.getLocale(), user.getPersonId(), Product.SHORT_TERM);
                    if (loanApp != null) {//tiene loan app activo
                        data.setAnswer(SessionData.SHORT_TERM_DOC_NUMBER, "");
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
                            data.setCurrentState(SessionData.SHORT_TERM_DOC_TYPE);
                            return MessageFactory.newQuickReplyMessage(
                                    getMessageSource().getMessage("messengerbot.loan.active.doctype.go", null, data.getLocale()),
                                    buttonList).toJson(data.getProfile().getId(), getGson());
                        }
                    }
                }
                IntegerFieldValidator aValidator = //isAdelanto(data) ?
                        ValidatorUtil.LOANAPPLICATION_AMMOUNT_SHORT_TERM;
                        //ValidatorUtil.LOANAPPLICATION_AMMOUNT_TRADITIONAL;
                String min = aValidator.getMinValue().toString();
                String max = aValidator.getMaxValue().toString();
                String[] arr = new String[]{min, max};
                data.setCurrentState(destinyState);
                return toJsonSimpleMessage(data.getProfile().getId(), messageSource.getMessage("messengerbot.loan.amount.range.go..", arr, data.getLocale()));
            case SessionData.SHORT_TERM_TERM:
                IntegerFieldValidator termValidator = //isAdelanto(data) ?
                        ValidatorUtil.LOANAPPLICATION_DAYS;
                        //ValidatorUtil.LOANAPPLICATION_INSTALLMENTS;
                String minT = termValidator.getMinValue().toString();
                String maxT = termValidator.getMaxValue().toString();
                String[] arrT = new String[]{minT, maxT};
                //boolean useDays = isAdelanto(data);
                //if (useDays) {
                data.setCurrentState(destinyState);
                return toJsonSimpleMessage(data.getProfile().getId(), messageSource.getMessage("messengerbot.loan.term.days.go..", arrT, data.getLocale()));
                // } else {
                //   return toJsonSimpleMessage(senderId, messageSource.getMessage("messengerbot.loan.term.months.go..", arrT, data.getLocale()));
                //}
            case SessionData.SHORT_TERM_DOC_TYPE:
                data.setAnswer(SessionData.SHORT_TERM_DOC_NUMBER, "");
                List<Button> buttonList = //new ArrayList<>();
                catalogService.getIdentityDocumentTypes().stream().map(
                        d -> MessageFactory.newPostBackButton(d.getName(), d.getId().toString())
                ).collect(Collectors.toList());
                data.setCurrentState(destinyState);
                return MessageFactory.newQuickReplyMessage(
                        messageSource.getMessage("messengerbot.loan.doctype.go", null, data.getLocale()),
                        buttonList).toJson(data.getProfile().getId(), gson);
            case SessionData.SHORT_TERM_DOC_NUMBER:
                data.setCurrentState(destinyState);
                return toJsonSimpleMessage(data.getProfile().getId(), messageSource.getMessage("messengerbot.loan.docnumber.go", null, data.getLocale()));
            case SessionData.SHORT_TERM_DOC_NUMBER_SURE:
                data.setCurrentState(destinyState);
                return jsonSureOptions(data);
            case SessionData.SHORT_TERM_CE_BIRTHDAY:
                data.setCurrentState(destinyState);
                return toJsonLocaleMessage(data, "bot.birthday.go", null);
        }
        return null;
    }

    public String jsonEndLoanApp(SessionData data, LoanApplication newloanApplication, boolean[] esNuevo, LoanApplication... las) throws Exception {
        User user = userDao.getUserByFacebookMessengerId(data.getProfile().getId());
        if(user == null){
            return toJsonSimpleMessage(
                    data.getProfile().getId(),
                    "Lo sentimos, tu messenger ya no está asociado a solven.");
        }
        if (newloanApplication == null)
            newloanApplication = createLoanApp(data, user, esNuevo);
        String loanLink = getLoanUrl(newloanApplication, false);
        Locale locale = data.getLocale();
        String senderId = data.getProfile().getId();
        String messageEnd = "messengerbot.loan.active.withnumber.end";
        String json = MessageFactory.newCTAMessage(messageSource.getMessage(messageEnd, null, locale),
                MessageFactory.newWebUrlButton(messageSource.getMessage("messengerbot.loan.active.link", null, locale), loanLink),
                MessageFactory.newWebUrlButton(messageSource.getMessage("messengerbot.loan.active.main", null, locale), Configuration.CLIENT_PRD_URL)
        ).toJson(senderId, gson);
        if (user.getPhoneNumber() == null) {// open 30 seconds (regen)
            loanApplicationDAO.registerNoAuthLinkExpiration(newloanApplication.getId(), 60);
            messageEnd = "messengerbot.loan.active.nonumber.end";
            json = MessageFactory.newCTAMessage(messageSource.getMessage(messageEnd, null, locale),
                    MessageFactory.newWebUrlButton(messageSource.getMessage("messengerbot.loan.active.link", null, locale), loanLink),
                    MessageFactory.newPostBackButton(messageSource.getMessage("messengerbot.loan.active.nonumber.regen", null, locale), MessengerModule.PB_REGEN_SHORT)
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

    public String renewLink(SessionData data) throws Exception {
        User user = userDao.getUserByFacebookMessengerId(data.getProfile().getId());
        if(user == null){
            return toJsonSimpleMessage(
                    data.getProfile().getId(),
                    "Lo sentimos, tu messenger ya no está asociado a solven.");
        }
        Integer personId = user.getPersonId();
        LoanApplication active = loanApplicationDAO.getActiveLoanApplicationByPerson(data.getLocale(), personId, Product.SHORT_TERM);
        if(active == null) {
            return toJsonSimpleMessage(
                    data.getProfile().getId(),
                    "Lo sentimos, actualmente no tienes una solicitud de crédito a corto plazo activa.");
        }
        String loanLink = getLoanUrl(active, false);
        Locale locale = data.getLocale();
        String senderId = data.getProfile().getId();
        String messageEnd = "messengerbot.loan.active.withnumber.end";
        String json = MessageFactory.newCTAMessage(messageSource.getMessage(messageEnd, null, locale),
                MessageFactory.newWebUrlButton(messageSource.getMessage("messengerbot.loan.active.link", null, locale), loanLink),
                MessageFactory.newWebUrlButton(messageSource.getMessage("messengerbot.loan.active.main", null, locale), Configuration.CLIENT_PRD_URL)
        ).toJson(senderId, gson);
        if (user.getPhoneNumber() == null) {// open 30 seconds (regen)
            loanApplicationDAO.registerNoAuthLinkExpiration(active.getId(), 60);
            messageEnd = "messengerbot.loan.active.nonumber.end";
            json = MessageFactory.newCTAMessage(messageSource.getMessage(messageEnd, null, locale),
                    MessageFactory.newWebUrlButton(messageSource.getMessage("messengerbot.loan.active.link", null, locale), loanLink),
                    MessageFactory.newPostBackButton(messageSource.getMessage("messengerbot.loan.active.nonumber.regen", null, locale), MessengerModule.PB_REGEN_SHORT)
            ).toJson(senderId, gson);
        }
        messengerSession.clean(senderId);
        messengerSession.cleanOld();//removes old conversations
        return json;
    }

    private LoanApplication createLoanApp(SessionData data, User user, boolean[] esNuevo) throws Exception {
        LoanApplication newloanApplication = loanApplicationDAO.getActiveLoanApplicationByPerson(data.getLocale(), user.getPersonId(), Product.SHORT_TERM);
        if (newloanApplication != null)
            return newloanApplication;

        logger.debug(data);
        Integer userId = user.getId();
        Integer ammount = Integer.parseInt(data.getAnswer(SessionData.SHORT_TERM_AMOUNT));
        Integer reason = null;
        Integer productId;
        Integer declaredClusterId = 7;//default value
        //set days or months
        Integer monthsOr1 = 1;//default value
        Integer daysOrNull = null;//default value
        //if (true) {
            //set days
            daysOrNull = 2;//null;//Integer.parseInt(data.getAnswer(SessionData.SHORT_TERM_TERM));//TODO
            productId = Product.SHORT_TERM;
   /*     } else {
            // set months
            monthsOr1 = Integer.parseInt(data.getAnswer(SessionData.TERM));
            productId = Product.TRADITIONAL;
        }*/
        newloanApplication = loanApplicationDAO.registerLoanApplication(
                userId,
                ammount,
                monthsOr1,
                reason,
                productId,
                daysOrNull,
                declaredClusterId,
                LoanApplication.ORIGIN_MESSENGER, null, null, null, CountryParam.COUNTRY_PERU);

        // Solucion un poco fea :(
        newloanApplication = loanApplicationDAO.getLoanApplication(newloanApplication.getId(), data.getLocale());

        // Check if there is a preliminaryEvaluation
        LoanApplicationPreliminaryEvaluation preEvaluation = loanApplicationService
                .getLastPreliminaryEvaluation(newloanApplication.getId(), data.getLocale(), null);
        if (preEvaluation == null) {
            // TODO Call the evaluation bot

            // Call the start preliminary evaluation
//            loanApplicationDAO.startPreliminaryEvaluation(newloanApplication.getId());

            // Execute the evaluatino
//            loanApplicationDAO.executePreliminaryEvaluation(newloanApplication.getId());
            esNuevo[0] = true;
        }
        return newloanApplication;
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
    public LoanApplicationDAO getLoanApplicationDAO() {
        return loanApplicationDAO;
    }

    @Override
    public LoanApplicationService getLoanApplicationService() {
        return loanApplicationService;
    }

    @Override
    public UserDAO getUserDao() {
        return userDao;
    }

    @Override
    public boolean isProduct() {
        return getProduct() != null;
    }

    @Override
    public String[] options() {
        return (toString()+";corto;short;credito a corto plazo;credito corto; crédito corto;crédito a corto;crédito a corto plazo;corto plazo").split(";");
    }

    @Override
    public UserCLService getUserCLService() {
        return userCLService;
    }

    @Override
    public String toString() {
        return getProduct().getShortName();
    }
}