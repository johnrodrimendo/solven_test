package com.affirm.client.model.messengerbot.modular;

import com.affirm.client.model.messengerbot.SessionData;
import com.affirm.client.model.messengerbot.contract.Button;
import com.affirm.client.model.messengerbot.contract.Message;
import com.affirm.client.model.messengerbot.utils.MessengerHelper;
import com.affirm.client.service.MessengerSession;
import com.affirm.client.service.UserCLService;
import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.UserDAO;
import com.affirm.common.service.LoanApplicationService;
import com.affirm.common.util.Util;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by jarmando on 18/01/17.
 */
@Component
public class GlobalModule implements MessengerModule {

    @Autowired
    UserDAO userDao;
    @Autowired
    LoanApplicationDAO loanApplicationDAO;
    @Autowired
    LoanApplicationService loanApplicationService;
    @Autowired
    MessageSource messageSource;
    @Autowired
    UserCLService userCLService;
    @Autowired
    MessengerSession messengerSession;

    MessengerHelper helper;
    List<MessengerModule> modules;
    Gson gson = new Gson();

    @Override
    public MessengerModule getGlobalModule() {
        return null;
    }

    public void setHelper(MessengerHelper helper) {
        this.helper = helper;
        this.modules = helper.getModules();
    }

    @Override
    public String getModuleName() {
        return SessionData.M_GLOBAL;
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public List<String> getReplies(String senderId, String text, SessionData[] dataArray) throws Exception {
        SessionData  data = dataArray[0];
        Message fbMsg;
        switch (data.getCurrentState()) {
            case SessionData.GLOBAL_NEW:
                String[] productOptions = messageSource.getMessage("messengerbot.init.productOptions", null, data.getLocale()).split(";");
                String[] contactOptions = messageSource.getMessage("messengerbot.init.contactOptions", null, data.getLocale()).split(";");
                if (Util.anyContainsCaseInsensitive(text, productOptions)) {
                    return toList(jsonGoTo(SessionData.GLOBAL_PRODUCT, data));
                } else if (Util.anyContainsCaseInsensitive(text, contactOptions)) {
                    return toList(jsonGoTo(SessionData.GLOBAL_CONTACT, data));
                } else {
                    if(data.isPresentAnswer(data.getCurrentState())){
                        return toList(jsonGoTo(SessionData.GLOBAL_NEW, data));
                    }
                    String mLocaleNotSuported = null;
                    if (!data.isLocaleSupported()) {
                        mLocaleNotSuported = MessageFactory.newSimpleMessage(
                                "I am sorry. I am not prepared to talk you in your language yet. I will proceed in Spanish")
                                .toJson(senderId, gson);
                    }
                    return toList(mLocaleNotSuported, jsonGoTo(SessionData.GLOBAL_NEW, data));
                }
            case SessionData.GLOBAL_PRODUCT:
                //This call helper's getPostBackReplies to find the INIT of any module
                return getPostBackReplies(senderId, text, dataArray);
        }
        return null;
    }

    @Override
    public List<String> getPostBackReplies(String senderId, String text, SessionData[] dataArray) throws Exception {
        SessionData data = dataArray[0];
        // POST BACK COMMANDS
        //FIRST TIME VISIT POSTBACK
        if (text.equals(MessengerModule.PB_INIT)) {//WELCOME SCREEN
            String mLocale = null;
            if (!data.isLocaleSupported()) {
                mLocale = MessageFactory.newSimpleMessage(
                        "I am sorry. I am not prepared to talk you in your language yet. I will proceed in Spanish")
                        .toJson(senderId, gson);
            }
            return toList(mLocale, jsonGoTo(SessionData.GLOBAL_NEW, data));
        }
        // CHOOSE SERVICE
        if (text.equals(MessengerModule.PB_PRODUCT)) {//SEND TO REASON
            data.clearAnswers();
            return toList(jsonGoTo(SessionData.GLOBAL_PRODUCT, data));
        }
        // CHOOSE CONTACT
        if (text.equals(MessengerModule.PB_CONTACT)) {//PERSISTENT SEND A MESSAGE.
            return toList(jsonGoTo(SessionData.GLOBAL_CONTACT, data));
        }
        // CTA REGEN TRADITIONAL
        if (text.equals(MessengerModule.PB_REGEN_TRADITIONAL)) {
            return toList(jsonGoTo(SessionData.TRADITIONAL_REGEN, data));
        }
        // CTA REGEN ADVANCE
        //if (text.equals(MessengerModule.PB_REGEN_ADVANCE)) {
        //    return toList(jsonGoTo(SessionData.SALARY_ADVANCE_REGEN, data));
        //}
        // CTA REGEN SHORT
        if (text.equals(MessengerModule.PB_REGEN_SHORT)) {
            return toList(jsonGoTo(SessionData.SHORT_TERM_REGEN, data));
        }
        switch (data.getCurrentState()) {
            case SessionData.GLOBAL_PRODUCT://IT IS HANDLED BY HELPER'S POSTBACK TO REDIRECT ANY MODULE
                for (MessengerModule module: modules)
                    if(module.isActive() && module.isProduct())
                        if(Util.anyMatchCaseInsensitive(text.toLowerCase().trim(), module.options()))
                            return toList(helper.jsonGoTo(module.getModuleName() + SessionData.INIT, data));
                return toList(
                        toJsonLocaleMessage(data, "messengerbot.init.options.retry", null),
                        jsonGoTo(SessionData.GLOBAL_PRODUCT, data));
        }
        throw new RuntimeException("this module getPostbackReplies cannot handle current state: " + data.getCurrentState());
    }

    @Override
    public String jsonGoTo(String destinyState, SessionData data) throws Exception {
        String senderId = data.getProfile().getId();
        Message fbMsg;
        switch (destinyState) {
            case SessionData.GLOBAL_NEW:
                Button b1 = MessageFactory.newPostBackButton(messageSource.getMessage("messengerbot.init.product", null, data.getLocale()), MessengerModule.PB_PRODUCT);
                Button b2 = MessageFactory.newPostBackButton(messageSource.getMessage("messengerbot.init.contact", null, data.getLocale()), MessengerModule.PB_CONTACT);
                if (!data.isPresentAnswer(SessionData.GLOBAL_NEW)) {//first time, deliver image
                    data.setCurrentAnswer("seteado");
                    String name = data.getProfile().getFirstName();
                    if(name == null)
                        name = data.getProfile().getLastName();
                    fbMsg = MessageFactory.newElementsMessage(MessageFactory.newImageElement(
                            messageSource.getMessage("messengerbot.init.options", null, data.getLocale()).replace("FIRST_NAME", name),//subtitle
                            null,
                            "http://www.solven.pe/img/landing_bg-min.jpg",
                            b1, b2));//buttons
                } else {//comes back
                    fbMsg = MessageFactory.newQuickReplyMessage(messageSource.getMessage("messengerbot.init.options.retry", null, data.getLocale()),
                            b1, b2);
                }
                data.setCurrentState(destinyState);
                return fbMsg.toJson(senderId, gson);
            case SessionData.GLOBAL_CONTACT:
                data.setCurrentState(destinyState);
                return toJsonLocaleMessage(data, "messengerbot.silent.go");
            case SessionData.GLOBAL_PRODUCT:
                List<Button> bList = modules.stream()
                        .filter(r -> r.isProduct() && r.isActive())
                        .map(r -> MessageFactory.newPostBackButton(
                                r.toString(), r.toString()//TODO LOCALE product name
                        )).collect(Collectors.toList());
                //normal
                if(!data.isPresentAnswer(SessionData.GLOBAL_PRODUCT)) {
                    String sLocal = messageSource.getMessage("messengerbot.product.choose", null, data.getLocale());
                    data.setCurrentState(destinyState);
                    return MessageFactory.newQuickReplyMessage(sLocal, bList).toJson(senderId, gson);
                }
                //sorry i didnt understand
                fbMsg = MessageFactory.newQuickReplyMessage(messageSource.getMessage("messengerbot.init.options.retry", null, data.getLocale()), bList);
                data.setCurrentState(destinyState);
                return fbMsg.toJson(senderId, gson);
        }
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
        return userDao;
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
    public UserCLService getUserCLService() {
        return userCLService;
    }

    @Override
    public String[] options() {
        return new String[0];
    }
}