package com.affirm.client.controller;

import com.affirm.client.model.messengerbot.SessionData;
import com.affirm.client.model.messengerbot.contract.Postback;
import com.affirm.client.model.messengerbot.inbound.Entry;
import com.affirm.client.model.messengerbot.inbound.Message;
import com.affirm.client.model.messengerbot.inbound.Messaging;
import com.affirm.client.model.messengerbot.inbound.MessengerData;
import com.affirm.client.model.messengerbot.modular.MessengerHelperByProduct;
import com.affirm.client.model.messengerbot.utils.FbChatHelper;
import com.affirm.common.model.annotation.ErrorControllerAnnotation;
import com.affirm.common.util.AjaxResponse;
import com.affirm.common.util.SqlErrorMessageException;
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
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by jarmando on 01/12/16.
 */

@Controller
@Scope("request")
public class MessengerController {
    private static final Logger logger = Logger.getLogger(MessengerController.class);

    @Autowired
    MessengerHelperByProduct helper;
    @Autowired
    private MessageSource messageSource;

    @RequestMapping(value = "/" + Configuration.WEBHOOK_PATH + "/bot/messenger", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public ResponseEntity<String> messengerChallenge(
            @RequestParam Map<String, String> params) {
        try {
            logger.debug("messenger get");
            // /webhook/messenger?hub.challenge=123456&hub.verify_token=$$$$$$$$$$$$$$&hub.mode=subscribe
            String sysVerifyToken = System.getenv("VERIFY_TOKEN");
            String hubMode = params.get("hub.mode");
            String challenge = params.get("hub.challenge");
            String hubVerifyToken = params.get("hub.verify_token");
            if (sysVerifyToken.equals(hubVerifyToken) && "subscribe".equalsIgnoreCase(hubMode)) {
                return AjaxResponse.ok(challenge);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return AjaxResponse.ok("Invalid verify token");
    }

    @RequestMapping(value = "/" + Configuration.WEBHOOK_PATH + "/bot/messenger", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public void messengerHandle(@RequestBody String jsonData) {
        try {
            logger.debug("messenger POST");
            logger.debug(jsonData);
            MessengerData data = new Gson().fromJson(jsonData, MessengerData.class);
            logger.debug(data);
            if (data == null)
                return;
            if (data.isPage()) {
                if (data.getEntry() != null && data.getEntry().size() > 0) {
                    Entry entry = data.getEntry().get(data.getEntry().size() - 1);
                    if (entry.getMessaging() != null && entry.getMessaging().size() > 0) {
                        Messaging messaging = entry.getMessaging().get(entry.getMessaging().size() - 1);
                        Message message = messaging.getMessage();
                        Postback postback = messaging.getPostback();
                        String quickReplyPayload = null;
                        if (message != null && message.getQuick_reply() != null) {
                            quickReplyPayload = message.getQuick_reply().getPayload();
                        }
                        if (quickReplyPayload != null) {
                            String sender = messaging.getSender().getId();
                            sendPost(sender, quickReplyPayload, true);
                        } else if (postback != null) {
                            String sender = messaging.getSender().getId();
                            sendPost(sender, postback.getPayload(), true);
                        } else if (message != null && message.getText() != null) {  //someone sent a message
                            String text = message.getText();
                            String sender = messaging.getSender().getId();
                            //String recipient = messaging.getRecipient().getId();
                            sendPost(sender, text, false);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error no controlado en messengerHandle", e);
        }
    }

    public void sendPost(String senderId, String text, boolean onlyPostBack) throws Exception {
        logger.debug("send post by " + senderId + " text: " + text + " " + onlyPostBack);

        List<String> jsonReplies = null;
        SessionData[] dataArray = new SessionData[]{null};
        Locale locale = new Locale("es", "PE");
        try {
            try {
                if (onlyPostBack) {
                    logger.debug("Current state: " + (dataArray[0] == null ? "need created": dataArray[0].getCurrentState()));
                    jsonReplies = helper.getPostBackReplies(senderId, text, dataArray);
                    logger.debug("To state: " + (dataArray[0] == null ? "need created": dataArray[0].getCurrentState()));
                    helper.sessionUpdate(dataArray[0]);
                } else {
                    logger.debug("Current state: " + (dataArray[0] == null ? "need created": dataArray[0].getCurrentState()));
                    jsonReplies = helper.getReplies(senderId, text, dataArray);
                    logger.debug("To state: " + (dataArray[0] == null ? "need created": dataArray[0].getCurrentState()));
                    helper.sessionUpdate(dataArray[0]);
                }
            } catch (NumberFormatException nfe) {
                logger.error("", nfe);
                if (dataArray[0] != null) {
                    locale = dataArray[0].getLocale() == null ? locale : dataArray[0].getLocale();
                    dataArray[0].setLocale(locale);
                    jsonReplies = helper.errorRetryLocale(dataArray[0], "messengerbot.error.number");
                }
            } catch (SqlErrorMessageException sqlMessageEx) {
                logger.error("", sqlMessageEx);
                if (dataArray[0] != null) {
                    locale = dataArray[0].getLocale() == null ? locale : dataArray[0].getLocale();
                    dataArray[0].setLocale(locale);
                    jsonReplies = helper.errorRetryLocale(dataArray[0], sqlMessageEx.getMessageKey());//TODO error mail
                }
            } catch (NoSuchMessageException localeMessageEx) {
                logger.error("", localeMessageEx);
                if (dataArray[0] != null) {
                    locale = dataArray[0].getLocale() == null ? locale : dataArray[0].getLocale();
                    dataArray[0].setLocale(locale);
                    jsonReplies = helper.errorRetryLocale(dataArray[0], "messengerbot.error.locale");//TODO error mail
                }
            } catch (Exception e) {
                logger.error("", e);
                if (dataArray[0] != null) {
                    if (dataArray[0] != null) {
                        locale = dataArray[0].getLocale() == null ? locale : dataArray[0].getLocale();
                        dataArray[0].setLocale(locale);
                        jsonReplies = helper.errorRetryLocale(dataArray[0], "messengerbot.oops");//TODO error mail
                    }
                }
            }
            if (dataArray[0] == null) {//TODO error mail
                jsonReplies = helper.toList("{\"recipient\":{\"id\":\"" + senderId + "\"},\"timestamp\":0,\"message\":{\"text\":\"Error fatal. No he podido obtener tu data para poder conversar. :c\"}}");
            }
        } catch (Exception e) {
            logger.error("", e);
            jsonReplies = helper.toList("{\"recipient\":{\"id\":\"" + senderId + "\"},\"timestamp\":0,\"message\":{\"text\":\"Error fatal. Hubo un error y no me dijeron como reaccionar a él. :c\"}}");
            //throw new RuntimeException(e);
        }
        //solo enviar el log al que tenga dicho sender id.
        if (FbChatHelper.debug && dataArray[0] != null && (senderId.equals("929578140477748") || senderId.equals("1177390952339288"))) {
            List<String> newList = new ArrayList<>();
            String log = helper.toJsonSimpleMessage(dataArray[0].getProfile().getId(), dataArray[0].toString());
            newList.add(log);
            newList.addAll(jsonReplies);
            jsonReplies = newList;
        }

        if (dataArray[0] != null) {
            if(SessionData.GLOBAL_NEW.equals(dataArray[0].getCurrentState())) {
                helper.clear(senderId);
            }
        }


        HttpClient client = HttpClientBuilder.create().build();
        HttpPost httppost = new HttpPost("https://graph.facebook.com/v2.6/me/messages?access_token=" + System.getenv("PAGE_TOKEN"));
        httppost.setHeader("Content-Type", "application/json; charset=UTF-8");//charset=UTF-8
        for (String jsonReply : jsonReplies) {
            if (jsonReply == null)
                continue;
            try {
                AbstractHttpEntity entity = new ByteArrayEntity(jsonReply.getBytes(StandardCharsets.UTF_8), ContentType.APPLICATION_JSON);
                entity.setContentType(new BasicHeader("Content-Type", "application/json; charset=UTF-8"));
                httppost.setEntity(entity);
                HttpResponse response = client.execute(httppost);
                String result = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                logger.debug(result);
            } catch (UnsupportedEncodingException e) {
                logger.error("", e);
            } catch (ClientProtocolException e) {
                logger.error("", e);
            } catch (IOException e) {
                logger.error("", e);
            }
        }
    }
}