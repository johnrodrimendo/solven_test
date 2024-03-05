package com.affirm.client.service.impl;

import com.affirm.client.model.messengerbot.SessionData;
import com.affirm.client.model.messengerbot.profile.FbProfile;
import com.affirm.client.service.MessengerSession;
import com.affirm.common.model.transactional.User;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Created by jarmando on 01/12/16.
 */
public class MessengerSessionHashMap implements MessengerSession {
    static Locale localeEsPE = new Locale("es", "PE");
    /** A map of senderIds as keys and SessionData values*/
    private Map<String, SessionDataMap> memory = new ConcurrentHashMap<>();

    @Override
    public SessionData initSessionData(String senderId, FbProfile fbProfile, String initState) {
        SessionDataMap data = new SessionDataMap();
        data.profile = fbProfile;
        data.profile.setId(senderId);

        Locale locale = new Locale("es", "PE");
        try {
            locale = new Locale(fbProfile.getLocale().split("\\_")[0], fbProfile.getLocale().split("\\_")[1]) ;
        } catch (Exception e) {}

        data.locale = locale;

        data.setCurrentState(initState);
        data.previousState = initState;
        memory.put(senderId, data);
        return data;
    }

    @Override
    public void clean(String senderId) {
        memory.remove(senderId);
    }

    /** Removes every SessionData of old conversations*/
    @Override
    public void cleanOld() {
        List<String> keys = memory.entrySet().stream().filter(e ->
                ChronoUnit.DAYS.between(e.getValue().lastUsed, LocalDateTime.now()) > 3
        ).map(e -> e.getKey()).collect(Collectors.toList());
        keys.forEach(k -> memory.remove(k));
    }

    @Override
    public void cleanEverything() {
        memory.clear();
    }

    @Override
    public SessionData getSessionData(String senderId) {
        return memory.get(senderId);
    }

    @Override
    public void override(SessionData sessionData) {
        memory.put(sessionData.getProfile().getId(), (SessionDataMap)sessionData);
    }

    //TODO check if it is night or day with the timezone provided, parse info about gender. (fbProfile)
    private class SessionDataMap implements SessionData {
        User user;
        Locale locale;
        FbProfile profile;
        String currentState;
        String previousState;
        boolean isGlobal;
        LocalDateTime lastUsed = LocalDateTime.now();
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
            return localeEsPE;
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
            lastUsed = LocalDateTime.now();
        }

        @Override
        public String getCurrentState() {
            return currentState;
        }

        @Override
        public void setCurrentState(String newState) {
            previousState = currentState;
            currentState = newState;
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
            return isGlobal;
        }

        @Override
        public String toString() {
            return "SessionDataMap{" + "\n" +
                    "user=" + user + ",\n" +
                    "locale=" + locale == null ? "" : locale.getDisplayName() + ",\n" +
                    "profile=" + profile == null ? "" : profile.getFirstName() + ",\n" +
                    "currentState=" + currentState + ",\n" +
                    "lastUsed=" + lastUsed + ",\n" +
                    "previousState=" + previousState + ",\n" +
                    "stateAnswerMap= [\n" + stateAnswerMap.entrySet().stream()
                        .map(e -> e.getKey() + ": " + e.getValue())
                        .reduce((s1, s2) -> s1 + ";\n" + s2).orElse("") + "]" +
                    '}';
        }
    }
}