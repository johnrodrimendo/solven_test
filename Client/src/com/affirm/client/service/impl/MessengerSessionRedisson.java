package com.affirm.client.service.impl;

import com.affirm.client.model.messengerbot.SessionData;
import com.affirm.client.model.messengerbot.profile.FbProfile;
import com.affirm.client.model.messengerbot.utils.RedissonSessionData;
import com.affirm.client.service.MessengerSession;
import com.affirm.system.configuration.Configuration;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by jarmando on 01/12/16.
 */
public class MessengerSessionRedisson implements MessengerSession {
    public static Locale localeEsPE = new Locale("es", "PE");

    @Autowired
    RedissonClient redissonClient;

    /**
     * A map of senderIds as keys and SessionData values
     */
    private RMapCache<String, RedissonSessionData> memory;

    @PostConstruct
    public void init() {
        if (redissonClient == null)
            throw new RuntimeException("CLIENT REDISSON NULL");
        memory = redissonClient.getMapCache("messengerSession");
    }

    @Override
    public SessionData initSessionData(String senderId, FbProfile fbProfile, String initState) {
        RedissonSessionData data = new RedissonSessionData();
        data.profile = fbProfile;
        data.profile.setId(senderId);

        Locale locale = new Locale("es", "PE");
        try {
            locale = new Locale(fbProfile.getLocale().split("\\_")[0], fbProfile.getLocale().split("\\_")[1]);
        } catch (Exception e) {
        }

        data.locale = locale;

        data.setCurrentState(initState);
        data.previousState = initState;
        memory.fastPut(senderId, data, Configuration.getMessengerSessionTimeLife(), TimeUnit.DAYS);
        return data;
    }

    @Override
    public void clean(String senderId) {
        memory.fastRemove(senderId);
    }


    /**
     * Removes every SessionData of old conversations
     */
    @Override
    public void cleanOld() {
        List<String> keys = memory.entrySet().stream().filter(e ->
                        daysBetweenUsingJoda(e.getValue().lastUsed, new Date()) > 3
        ).map(e -> e.getKey()).collect(Collectors.toList());
        keys.forEach(k -> memory.fastRemove(k));
    }

    @Override
    public void cleanEverything() {
        memory.clear();
    }

    @Override
    public SessionData getSessionData(String senderId) {
        RedissonSessionData data = memory.get(senderId);
        return data;
    }

    @Override
    public void override(SessionData data) {
        if (data != null) {
            memory.fastPut(data.getProfile().getId(), (RedissonSessionData) data, Configuration.getMessengerSessionTimeLife(), TimeUnit.DAYS);
        }
    }

    public static int daysBetweenUsingJoda(Date d1, Date d2) {
        return Days.daysBetween(new LocalDate(d1.getTime()), new LocalDate(d2.getTime())).getDays();
    }

}