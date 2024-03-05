package com.affirm.client.model.messengerbot.modular;

import com.affirm.client.model.messengerbot.SessionData;
import com.affirm.client.service.MessengerSession;
import com.affirm.client.service.UserCLService;
import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.UserDAO;
import com.affirm.common.model.catalog.CountryParam;
import com.affirm.common.model.catalog.LoanApplicationReason;
import com.affirm.common.model.catalog.Product;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.model.transactional.LoanApplicationPreliminaryEvaluation;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.LoanApplicationService;
import com.affirm.common.util.JsonUtil;
import com.affirm.system.configuration.Configuration;
import com.google.gson.Gson;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by jarmando on 18/01/17.
 */
@Component
public class TraditionalModule implements MessengerModule {
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
    LoanApplicationService loanApplicationService;
    @Autowired
    MessageSource messageSource;
    @Autowired
    UserCLService userCLService;

    Gson gson = new Gson();
    @Autowired
    GlobalModule globalModule;

    @Override
    public MessengerModule getGlobalModule() {
        return globalModule;
    }

    @Override
    public String getModuleName() {
        return SessionData.M_TRADITIONAL;
    }

    @Override
    public boolean isActive() {
        return getProduct().getActive();
    }

    public List<String> getRegenerateLinkPostBackReply(String senderId, String text, SessionData[] dataArray) throws Exception {
        return null;
    }

    @Override
    public List<String> getReplies(String senderId, String text, SessionData[] dataArray) throws Exception {
        SessionData data = dataArray[0];
        switch (data.getCurrentState()) {
            case SessionData.TRADITIONAL_REGEN:
                LoanApplication loanApp = getLoanAppByData(data, Product.TRADITIONAL);
                if (loanApp != null)
                    return toList(
                            toJsonSimpleMessage(senderId, messageSource.getMessage("messengerbot.loan.active.nonumber.regen.done", null, data.getLocale())),
                            jsonEndLoanApp(data, loanApp, new boolean[]{false}));
                else
                    return toList(
                            toJsonSimpleMessage(senderId, messageSource.getMessage("messengerbot.loan.active.nonumber.regen.fail", null, data.getLocale())),
                            jsonGoTo(SessionData.TRADITIONAL_REASON, data));
        }
        return null;
    }

    @Override
    public List<String> getPostBackReplies(String senderId, String text, SessionData[] dataArray) throws Exception {
        SessionData data = dataArray[0];
        switch (data.getCurrentState()) {
            case SessionData.TRADITIONAL_REGEN:
                LoanApplication loanApp = getLoanAppByData(data, Product.TRADITIONAL);
                if (loanApp != null)
                    return toList(
                            toJsonSimpleMessage(senderId, messageSource.getMessage("messengerbot.loan.active.nonumber.regen.done", null, data.getLocale())),
                            jsonEndLoanApp(data, loanApp, new boolean[]{false}));
                else
                    return toList(
                            toJsonSimpleMessage(senderId, messageSource.getMessage("messengerbot.loan.active.nonumber.regen.fail", null, data.getLocale())),
                            jsonGoTo(SessionData.TRADITIONAL_REASON, data));
            default:
                return getGlobalModule().getPostBackReplies(senderId, text, dataArray);
        }
    }

    @Override
    public String jsonGoTo(String destinyState, SessionData data) throws Exception {
        String senderId = data.getProfile().getId();
        switch (destinyState) {
            case SessionData.TRADITIONAL_REGEN:
        }
        return null;
    }

    public List<String> toList(String... vargs) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < vargs.length; i++) {
            if (vargs[i] != null)
                list.add(vargs[i]);
        }
        return list;
    }

    public String jsonEndLoanApp(SessionData data, LoanApplication newloanApplication, boolean[] esNuevo, LoanApplication... las) throws Exception {
        if (newloanApplication == null)
            newloanApplication = createLoanApp(data, esNuevo);
        String loanLink = getLoanUrl(newloanApplication, false);
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
                    MessageFactory.newPostBackButton(messageSource.getMessage("messengerbot.loan.active.nonumber.regen", null, locale), MessengerModule.PB_REGEN_TRADITIONAL)
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

    public boolean isAdelanto(SessionData data) throws Exception {
        String reasonText = data.getAnswer(SessionData.TRADITIONAL_REASON);
        boolean isAdelantoText = catalogService.getLoanApplicationReasonsMini(data.getLocale()).stream()
                .filter(r -> r.getId() == LoanApplicationReason.ADELANTO && r.getReason().equalsIgnoreCase(reasonText))
                .count() > 0;
        if (isAdelantoText) return isAdelantoText;
        String[] reasonIdAndJsonProducts = data.getAnswer(SessionData.TRADITIONAL_REASON).split(";");
        if (reasonIdAndJsonProducts.length < 2)
            return false;
        String jsonProductsString = reasonIdAndJsonProducts[1];
        JSONArray array = new JSONArray(jsonProductsString);
        List<Integer> list = JsonUtil.getListFromJsonArray(array, (arr, i) -> arr.getInt(i));
        boolean isAdelantoId = list.stream().filter(p -> !p.equals(Product.SHORT_TERM))
                .count() == 0;//if short term is the only product, use days
        return isAdelantoId;
    }

    private LoanApplication createLoanApp(SessionData data, boolean[] esNuevo) throws Exception {
        LoanApplication newloanApplication = loanApplicationDAO.getActiveLoanApplicationByPerson(data.getLocale(), data.getUser().getPersonId(), Product.TRADITIONAL);
        if (newloanApplication != null)
            return newloanApplication;

        logger.debug(data);
        Integer userId = data.getUser().getId();
        Integer ammount = Integer.parseInt(data.getAnswer(SessionData.TRADITIONAL_AMOUNT));
        Integer reason = Integer.parseInt(data.getAnswer(SessionData.TRADITIONAL_REASON).split(";")[0]);
        Integer productId;
        Integer declaredClusterId = 7;//default value
        //set days or months
        Integer monthsOr1 = 1;//default value
        Integer daysOrNull = null;//default value
        if (isAdelanto(data)) {
            //set days
            daysOrNull = Integer.parseInt(data.getAnswer(SessionData.TRADITIONAL_TERM));
            productId = Product.SHORT_TERM;
        } else {
            // set months
            monthsOr1 = Integer.parseInt(data.getAnswer(SessionData.TRADITIONAL_TERM));
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
                LoanApplication.ORIGIN_MESSENGER, null, 1, null, CountryParam.COUNTRY_PERU);

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

    Product getProduct() {
        try { return catalogService.getProduct(Product.TRADITIONAL);
        } catch (Exception e) { throw new RuntimeException(e); }
    }

    @Override
    public boolean isProduct() {
        return getProduct() != null;
    }

    @Override
    public String[] options() {
        return (toString()+";tradicional;traditional").split(";");
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