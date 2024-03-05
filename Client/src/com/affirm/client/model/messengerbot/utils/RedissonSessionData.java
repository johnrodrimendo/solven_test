package com.affirm.client.model.messengerbot.utils;

import com.affirm.client.model.messengerbot.SessionData;
import com.affirm.client.model.messengerbot.profile.FbProfile;
import com.affirm.client.service.impl.MessengerSessionRedisson;
import com.affirm.common.model.transactional.User;

import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by jarmando on 30/01/17.
 */
//TODO check if it is night or day with the timezone provided, parse info about gender. (fbProfile)
public class RedissonSessionData implements SessionData {
    User user;
    public Locale locale;
    public FbProfile profile;
    String currentState;
    public String previousState;
    public boolean isGlobal;
    public Date lastUsed = new Date();
    Map<String, String> stateAnswerMap = new ConcurrentHashMap<>();

    @Override
    public void clearAnswers() {
        stateAnswerMap.clear();
    }

    @Override
    public User getUser() {
        return user;
    }

    @Override
    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean isPresentAnswer(String state) {
        String answer = getAnswer(state);
        if(answer == null)
            return false;//empty
        return !answer.equals("");
    }

    @Override
    public boolean isLocaleSupported() {
        if("es".equalsIgnoreCase(locale.getLanguage())){
            return true;
        }//TODO ADD MORE LOCALES HERE WHEN THE LOCALE IS SUPPORTED.
        return false;
    }

    @Override
    public Locale getLocale() {
        if(isLocaleSupported())
            return locale;
        return MessengerSessionRedisson.localeEsPE;
    }

    @Override
    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    @Override
    public FbProfile getProfile() {
        return profile;
    }

    @Override
    public String getAnswer(String state) {
        return stateAnswerMap.get(state);
    }

    @Override
    public void setCurrentAnswer(String answer) {
        setAnswer(currentState, answer);
    }

    @Override
    public void setAnswer(String state, String answer) {
        stateAnswerMap.put(state, answer);
    }

    @Override
    public String getCurrentState() {
        return currentState;
    }

    @Override
    public void setCurrentState(String newState) {
        previousState = currentState;
        currentState = newState;
        lastUsed = new Date();
    }

    @Override
    public String getPreviousState() {
        return previousState;
    }

    @Override
    public void setIsGlobal(boolean isGlobal) {
        this.isGlobal = isGlobal;
    }

    @Override
    public boolean isGlobal() {
        return this.isGlobal;
    }

    @Override
    public String toString() {
        return "RedissonSessionData{" + "\n" +
                "user=" + user + ",\n" +
                "locale=" + locale == null ? "" : locale.getDisplayName() + ",\n" +
                "profile=" + profile == null ? "" : profile.getFirstName() + ",\n" +
                "currentState=" + currentState + ",\n" +
                "lastUsed=" + lastUsed + ",\n" +
                "isGlobal=" + isGlobal + ",\n" +
                "previousState=" + previousState + ",\n" +
                "stateAnswerMap= [\n" + stateAnswerMap.entrySet().stream()
                .map(e -> e.getKey() + ": " + e.getValue())
                .reduce((s1, s2) -> s1 + ";\n" + s2).orElse("") + "]" +
                '}';
    }
}
