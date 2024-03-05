package com.affirm.client.model.messengerbot.modular;

import com.affirm.client.model.messengerbot.SessionData;
import com.affirm.client.model.messengerbot.utils.MessengerHelper;
import com.affirm.client.service.MessengerSession;
import com.affirm.client.service.UserCLService;
import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.dao.UserDAO;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.EvaluationService;
import com.affirm.common.service.LoanApplicationService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GeneralModule implements MessengerModule {

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
    UserCLService userClService;

    @Autowired
    UserDAO userDAO;

    @Autowired
    EvaluationService evaluationService;

    @Autowired
    LoanApplicationService loanApplicationService;

    @Autowired
    ConsumerCreditModule consumerCreditModule;

    MessengerHelper helper;
    Gson gson = new Gson();

    @Override
    public String getModuleName() {
        return SessionData.M_GENERAL;
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public List<String> getReplies(String senderId, String text, SessionData[] dataArray) throws Exception {
        return toList();
    }

    @Override
    public List<String> getPostBackReplies(String senderId, String text, SessionData[] dataArray) throws Exception {
        SessionData data = dataArray[0];

        switch (text.trim()) {
            case "_get_started_":
                data.setIsGlobal(true);
                return toList(toJsonLocaleMessage(data, "messengerbot.get.started" ));
            case "_leave_message_":
                data.setIsGlobal(true);
                return toList(toJsonLocaleMessage(data, "messengerbot.leave.message" ));
            case "_credit_":
                data.setIsGlobal(false);
                send(toList(toJsonLocaleMessage(data, "messengerbot.ask.credit")));
                data.setCurrentState(SessionData.CONSUMER_CREDIT_NEW);
                if(data.getCurrentState().equals(SessionData.CONSUMER_CREDIT_NEW)) {
                    return toList(consumerCreditModule.jsonGoTo(SessionData.CONSUMER_PICK_COUNTRY, data));
                }
                return toList(consumerCreditModule.jsonGoTo(data.getCurrentState(), data));
            default:
                return toList();
        }
    }

    @Override
    public String jsonGoTo(String destinyState, SessionData data) throws Exception {
        return null;
    }

    @Override
    public UserCLService getUserCLService() {
        return userClService;
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
