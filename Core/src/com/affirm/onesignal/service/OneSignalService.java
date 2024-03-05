package com.affirm.onesignal.service;

import com.affirm.onesignal.service.impl.OneSignalServiceImpl;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

public interface OneSignalService {

    String sendNotification(Integer entityId, Integer countryId, String[] notificationMessage, List<String> includedPlayerIds, List<String> includedExternalUserIds, JSONObject filters) throws Exception;

    boolean cancelScheduledNotification(Integer entityId, Integer countryId, String notificationId) throws Exception;

    JSONObject applyNotificationsSegmentFilters(String[] includedSegments, String[] excludedSegments);

    JSONObject applyNotificationsUserBasedFilters(Integer sessionCountFilter, OneSignalServiceImpl.RelationFilter sessionCountRelationFilter, String countryFilter, Map<String, String> tagFilters);

    JSONObject applyNotificationsScheduleDelivery(String sendAfter, String deliveryTimeOfDate, Integer daysToLive);

}
