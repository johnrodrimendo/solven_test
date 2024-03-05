package com.affirm.client.model.messengerbot.utils;

import com.affirm.client.model.messengerbot.SessionData;
import com.affirm.client.model.messengerbot.modular.MessengerModule;

import java.util.List;

/**
 * Created by jarmando on 18/01/17.
 */
public interface MessengerHelper {
    boolean debug = true;

    List<String> getReplies(String senderId, String text, SessionData[] dataArray) throws Exception;
    List<String> getPostBackReplies(String senderId, String text, SessionData[] dataArray) throws Exception;
    List<String> errorRetryLocale(SessionData data, String localeKey) throws Exception;
    List<String> toList(String... vargs);
    String toJsonSimpleMessage(String senderId, String s);
    List<MessengerModule> getModules();
    String jsonGoTo(String destinyState, SessionData data) throws Exception;
    void sessionUpdate(SessionData sessionData);
    void clear(String senderId);
}
