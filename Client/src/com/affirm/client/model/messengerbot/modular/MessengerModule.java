package com.affirm.client.model.messengerbot.modular;

import com.affirm.client.model.form.UserRegisterForm;
import com.affirm.client.model.messengerbot.SessionData;
import com.affirm.client.service.UserCLService;
import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.UserDAO;
import com.affirm.common.model.catalog.IdentityDocumentType;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.model.transactional.User;
import com.affirm.common.service.LoanApplicationService;
import com.affirm.common.util.Util;
import com.affirm.system.configuration.Configuration;
import com.google.gson.Gson;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.springframework.context.MessageSource;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jarmando on 18/01/17.
 */

public interface MessengerModule {
    String PB_INIT = "_init_";
    String PB_PRODUCT = "_product_";
    String PB_CONTACT = "_contact_";
    String PB_REGEN_TRADITIONAL = "_regenTraditional_";
    String PB_REGEN_SHORT = "_regenShort_";
    String PB_REGEN_ADVANCE = "_regenAdvance_";

    String getModuleName();
    boolean isActive();
    List<String> getReplies(String senderId, String text, SessionData[] dataArray) throws Exception;
    List<String> getPostBackReplies(String senderId, String text, SessionData[] dataArray) throws Exception;
    String jsonGoTo(String destinyState, SessionData data) throws Exception;
    UserCLService getUserCLService();

    default String toJsonLocaleMessage(SessionData data, String key, String... params) {
        String msg = getMessageSource().getMessage(key, params, data.getLocale());
        return MessageFactory.newSimpleMessage(msg).toJson(data.getProfile().getId(), getGson());
    }

    default String toJsonSimpleMessage(String senderId, String s) {
        return MessageFactory.newSimpleMessage(s).toJson(senderId, getGson());
    }

    default LoanApplication getLoanAppByData(SessionData data, int productId) throws Exception{
        if (data.getUser() == null) {
            User user = getUserDao().getUserByFacebookMessengerId(data.getProfile().getId());
            data.setUser(user);
        }
        LoanApplication loanApp = getLoanApplicationDAO().getActiveLoanApplicationByPerson(data.getLocale(), data.getUser().getPersonId(), productId);
        return loanApp;
    }

    default List<String> toList(String... vargs) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < vargs.length; i++) {
            if (vargs[i] != null)
                list.add(vargs[i]);
        }
        return list;
    }

    default List<String> errorRetry(SessionData data, String errorText) throws Exception {
        return toList(
                toJsonSimpleMessage(data.getProfile().getId(), errorText),
                jsonGoTo(data.getCurrentState(), data));
    }

    default List<String> errorRetryLocale(SessionData data, String localeKey) throws Exception {
        String errorText = getMessageSource().getMessage(localeKey, null, data.getLocale());
        return errorRetry(data, errorText);
    }

    default String jsonSureOptions(SessionData data) {
        return jsonSureCorrectionOptions("messengerbot.sure", data);
    }

    default String jsonSureCorrectionOptions(String questionLocale, SessionData data) {
        String yes = getMessageSource().getMessage("messengerbot.is.sure", null, data.getLocale()).split(";")[0];
        String no = getMessageSource().getMessage("messengerbot.need.correction", null, data.getLocale()).split(";")[0];
        return MessageFactory.newQuickReplyMessage(
                getMessageSource().getMessage(questionLocale, null, data.getLocale()),
                MessageFactory.newPostBackButton(yes, yes),
                MessageFactory.newPostBackButton(no, no)
        ).toJson(data.getProfile().getId(), getGson());
    }

    default String jsonYesNoOptions(String questionLocale, SessionData data) {
        String yes = getMessageSource().getMessage("messengerbot.yes", null, data.getLocale()).split(";")[0];
        String no = getMessageSource().getMessage("messengerbot.no", null, data.getLocale()).split(";")[0];
        return MessageFactory.newQuickReplyMessage(
                getMessageSource().getMessage(questionLocale, null, data.getLocale()),
                MessageFactory.newPostBackButton(yes, yes),
                MessageFactory.newPostBackButton(no, no)
        ).toJson(data.getProfile().getId(), getGson());
    }

    default boolean isCurrentAnswerYes(String text, SessionData data) {
        String[] yesOptions = getMessageSource().getMessage("messengerbot.yes", null, data.getLocale()).split(";");
        String[] noOptions = getMessageSource().getMessage("messengerbot.no", null, data.getLocale()).split(";");
        boolean sure = true;//I am sure
        sure = !Util.anyContainsCaseInsensitive(text, noOptions);//I may not be sure
        if (!sure)//i I am not sure
            return false;///return I am not sure
        sure = Util.anyContainsCaseInsensitive(text, yesOptions);//I may be sure or not
        return sure;//return that.
    }

    default String getLoanUrl(LoanApplication la, boolean confirmationLink) {
        String userLoanCrypto;
        if(confirmationLink){
            Map<String, Object> params = new HashMap<>();
            params.put("confirmationlink", true);
            userLoanCrypto = getLoanApplicationService()
                    .generateLoanApplicationToken(
                            la.getUserId(),
                            la.getPersonId(),
                            la.getId(),
                            params);
        }
        else {
            userLoanCrypto = getLoanApplicationService()
                    .generateLoanApplicationToken(
                            la.getUserId(),
                            la.getPersonId(),
                            la.getId());
        }
        String baseUrl = Configuration.getClientDomain();
        return baseUrl + "/loanapplication/" + userLoanCrypto;
    }

    default /** If user is null this messenger is already associated to another user */
    User getUserByDocumentOrCreate(String senderId, int doctype, String docnumber) throws Exception {
        User user;
        user = getUserDao().getUserByDocument(doctype, docnumber);
        if (user == null) {
            UserRegisterForm userForm = new UserRegisterForm();
            userForm.setDocNumber(docnumber);
            userForm.setDocType(doctype);
            // Register the user
            user = getUserCLService().registerUserFacebookMessenger(userForm, senderId);
        }
        //Now I am sure the user does exists. Associate the user with this messenger.
        boolean userAsociated = getUserDao().registerFacebookMessengerId(user.getId(), senderId);
        if(!userAsociated) {
            return null;
        }
        return user;
    }

    default void send(List<String> list) {
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost httppost = new HttpPost("https://graph.facebook.com/v2.6/me/messages?access_token=" + System.getenv("PAGE_TOKEN"));
        httppost.setHeader("Content-Type", "application/json; charset=UTF-8");
        for (String jsonReply: list) {
            try {
                AbstractHttpEntity entity = new ByteArrayEntity(jsonReply.getBytes(StandardCharsets.UTF_8), ContentType.APPLICATION_JSON);
                entity.setContentType(new BasicHeader("Content-Type", "application/json; charset=UTF-8"));
                httppost.setEntity(entity);
                HttpResponse response = client.execute(httppost);
                String result = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            } catch (UnsupportedEncodingException e) {
                System.out.println(e.getMessage());
            } catch (ClientProtocolException e) {
                System.out.println(e.getMessage());
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    default List<IdentityDocumentType> getIdentityDocumentTypes() {

        List identityDocumentTypes = new ArrayList<>();
        identityDocumentTypes.add(new IdentityDocumentType(1, "DNI"));
        identityDocumentTypes.add(new IdentityDocumentType(2, "CE"));

        return identityDocumentTypes;
    }

    MessengerModule getGlobalModule();
    MessageSource getMessageSource();
    Gson getGson();
    UserDAO getUserDao();
    LoanApplicationDAO getLoanApplicationDAO();
    LoanApplicationService getLoanApplicationService();
    boolean isProduct();
    String [] options();
}