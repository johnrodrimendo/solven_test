package com.affirm.client.model.messengerbot.modular;

import com.affirm.client.model.messengerbot.SessionData;
import com.affirm.client.model.messengerbot.profile.FbProfile;
import com.affirm.client.model.messengerbot.utils.MessengerHelper;
import com.affirm.client.service.MessengerSession;
import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.UserDAO;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.Util;
import com.affirm.system.configuration.Configuration;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jarmando on 18/01/17.
 */
@Component
public class MessengerHelperByProduct implements MessengerHelper {
    private static final Logger logger = Logger.getLogger(MessengerHelperByProduct.class);
    private static String profileLink = "https://graph.facebook.com/v2.6/SENDER_ID?access_token=" + System.getenv("PAGE_TOKEN");

    private List<MessengerModule> modules;
    private Gson gson = new Gson();

    @Autowired
    CatalogService catalogService;
    @Autowired
    UserDAO userDao;
    @Autowired
    LoanApplicationDAO loanApplicationDAO;
    @Autowired
    MessageSource messageSource;
    @Autowired
    MessengerSession messengerSession;
    @Autowired
    GlobalModule globalModule;
    @Autowired
    ShortTermModule shortTermModule;
    @Autowired
    AdvanceModule advanceModule;
    @Autowired
    ConsumerCreditModule consumerCreditModule;
    @Autowired
    GeneralModule generalModule;

    @PostConstruct
    public void init() throws Exception {
        modules = new ArrayList<>();
        modules.add(generalModule);
        modules.add(consumerCreditModule);
        consumerCreditModule.setHelper(this);
    }

    @Override
    public List<String> getReplies(String senderId, String text, SessionData[] dataArray) throws Exception {
        logger.debug("getReplies: " + senderId + " " + text);
        // NOT-PRODUCTION AND DEBUG WRITTEN COMMANDS
        if (!Configuration.hostEnvIsProduction() && MessengerHelper.debug){
            if (Util.anyMatchCaseInsensitive(text, "clean", "clear", "reset")) {
                messengerSession.clean(senderId);
                return toList(toJsonSimpleMessage(senderId, "Conversación actual olvidada."));
            }
            if (Util.anyMatchCaseInsensitive(text, "cleanAll", "clearAll", "resetAll")) {
                messengerSession.cleanEverything();
                return toList(toJsonSimpleMessage(senderId, "Las conversaciones han sido olvidadas."));
            }
        }
        SessionData data = initSession(senderId, dataArray[0]);
        dataArray[0] = data;
        if(SessionData.GLOBAL_CONTACT.equals(data.getCurrentState())){
            return toList();
        }

        String moduleName = SessionData.getModule(data.getCurrentState());
        logger.debug("got sessiondata " + moduleName);

        if(data.isGlobal()) {
            return generalModule.getReplies(senderId,text,dataArray);
        }
        else {
            return consumerCreditModule.getReplies(senderId,text,dataArray);
        }
    }

    @Override
    public List<String> getPostBackReplies(String senderId, String text, SessionData[] dataArray) throws Exception {
        logger.debug("getPostBackReplies: " + senderId + " " + text);
        SessionData data = initSession(senderId, dataArray[0]);
        dataArray[0] = data;

        String moduleName = SessionData.getModule(data.getCurrentState());
        logger.debug("getPostBackReplies from module: " + moduleName);
        logger.debug("Will loop through modules (no Menu/CTA/command detected)");
        logger.debug("Is global: " + (data.isGlobal() ? "YES" : "NO"));

        if(text.trim().equals("_get_started_") || text.trim().equals("_leave_message_") || text.trim().equals("_credit_")) {
            data.setIsGlobal(true);
            logger.debug("Changed to global");
        }

        if(data.isGlobal()) {
            logger.debug("Check response in General Module");
            return generalModule.getPostBackReplies(senderId,text,dataArray);
        }
        else {
            logger.debug("Check response in Consumer Credit Module");
            return consumerCreditModule.getPostBackReplies(senderId,text,dataArray);
        }
    }

    /**
     * NAVIGATION, sets next state, creates the json reply, optionally adds a localized prefix
     */
    @Override
    public String jsonGoTo(String destinyState, SessionData data) throws Exception {
        //PREPARE DATA
        String m_senderId = data.getProfile().getId();
        String moduleName = SessionData.getModule(destinyState);
        //LOOP AND PROCESS
        for (MessengerModule module: modules) {//TRY EACH MODULE
            if(module.getModuleName().equals(moduleName)){//IF THIS IS THE MODULE NAME
                if(!module.isActive()){
                    logger.warn("jsonGoTo: MODULE NOT ACTIVE IN MODULE LIST!!");
                    data.setCurrentState(SessionData.GLOBAL_NEW);
                    return jsonGoTo(SessionData.GLOBAL_NEW, data);
                }
                String botAnswer = module.jsonGoTo(destinyState, data);//PROCESS METHOD DATA.
                return botAnswer;//RETURN ANSWER
            }
        }
        //MODULE NOT FOUND return to beginning TODO SEND MAIL
        logger.error("jsonGoTo: MODULE NOT PRESENT IN MODULE LIST!!");
        data.setCurrentState(SessionData.GLOBAL_NEW);
        return jsonGoTo(SessionData.GLOBAL_NEW, data);
    }

    private SessionData initSession(String senderId, SessionData sessionData) throws Exception {
        if (sessionData == null)
            sessionData = messengerSession.getSessionData(senderId);
        //init session - gets locale
        while (sessionData == null) {
            String link = StringUtils.replace(profileLink, "SENDER_ID", senderId);
            FbProfile profile = Util.getObjectFromUrl(link, FbProfile.class);
            sessionData = messengerSession.initSessionData(senderId, profile, SessionData.CONSUMER_CREDIT_NEW);
            sessionData.setIsGlobal(true);
        }

        return sessionData;
    }

    @Override
    public List<String> errorRetryLocale(SessionData data, String localeKey) throws Exception {
        String errorText = messageSource.getMessage(localeKey, null, data.getLocale());
        if (Configuration.hostEnvIsStage() && localeKey.equals("messengerbot.oops") && debug)
            return toList(
                    toJsonSimpleMessage(data.getProfile().getId(), data.toString()),//dump data
                    toJsonSimpleMessage(data.getProfile().getId(), errorText),//error message
                    jsonGoTo(data.getCurrentState(), data));//retry
        return errorRetry(data, errorText);
    }

    private List<String> errorRetry(SessionData data, String errorText) throws Exception {
        return toList(
                toJsonSimpleMessage(data.getProfile().getId(), errorText),
                jsonGoTo(data.getCurrentState(), data));
    }

    @Override
    public List<String> toList(String... vargs) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < vargs.length; i++) {
            if (vargs[i] != null)
                list.add(vargs[i]);
        }
        return list;
    }

    @Override
    public String toJsonSimpleMessage(String senderId, String s) {
        return MessageFactory.newSimpleMessage(s).toJson(senderId, gson);
    }

    @Override
    public List<MessengerModule> getModules() {
        return modules;
    }

    @Override
    public void sessionUpdate(SessionData sessionData) {
        messengerSession.override(sessionData);
    }

    @Override
    public void clear(String senderId) {
        messengerSession.clean(senderId);
    }
}