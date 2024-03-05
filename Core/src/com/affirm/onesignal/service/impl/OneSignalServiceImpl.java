package com.affirm.onesignal.service.impl;

import com.affirm.onesignal.service.OneSignalService;
import com.affirm.system.configuration.Configuration;
import com.google.gson.Gson;
import okhttp3.*;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class OneSignalServiceImpl implements OneSignalService {

    private static final Logger logger = Logger.getLogger(OneSignalServiceImpl.class);

    private static final String ONESIGNAL_URI = "https://onesignal.com";
    private static final String ONESIGNAL_NOTIFICATION_ENDPOINT = ONESIGNAL_URI + "/api/v1/notifications";

    private static final String ONESIGNAL_FILTER_TAG_LOAN_ID = "loan_id";
    private static final String ONESIGNAL_FILTER_TAG_LOAN_OFFERS = "loan_offers";
    private static final String ONESIGNAL_FILTER_TAG_CURRENT_QUESTION = "current_question";
    private static final String ONESIGNAL_FILTER_TAG_ENTITY_NAME = "entity_name";
//    private static final String ONESIGNAL_FILTER_TAG_USER_NAME = "user_name";// SE USA PARA REEMPLAZAR EN TEMPLATE DE MENSAJE

    public static final String ONESIGNAL_PROVIDER = "onesignal";

    public enum NotificationTemplate {
        WITH_OFFER,
        WITH_ACCEPTED_OFFER,
        CALL_INTERACTION_NO_RESPONSE
    }

    public enum RelationFilter {
        LESS_THAN("<"),
        GREAT_THAN(">"),
        EQUALS("="),
        NOT_EQUALS("!=");

        private String symbol;

        RelationFilter(String symbol) {
            this.symbol = symbol;
        }
    }

    @Override
    public String sendNotification(Integer entityId, Integer countryId, String[] notificationMessage, List<String> includedPlayerIds, List<String> includedExternalUserIds, JSONObject filters) throws Exception {
        OkHttpClient client = new OkHttpClient.Builder().build();

        if (filters == null) {
            filters = new JSONObject();
        }

        filters.put("app_id", Configuration.getOneSignalAppId(entityId, countryId));// REQUIRED
        filters.put("headings", new JSONObject().put("en", notificationMessage[0]).put("es", notificationMessage[0]));
        filters.put("contents", new JSONObject().put("en", notificationMessage[1]).put("es", notificationMessage[1]));
        filters.put("chrome_web_icon", "https://s3.amazonaws.com/solven-public/img/icons/logo_200x200-notifications.png");// GENERAL. INCLUYE FF
//        filters.put("chrome_web_badge", "");// ICONO MOVIL. APARECE EN LA BARRA DE NOTIFICACIONES. COLOR ALPHA


        if (includedPlayerIds != null && includedPlayerIds.size() > 0) {
            filters.put("include_player_ids", includedPlayerIds);
        }
        if (includedExternalUserIds != null && includedExternalUserIds.size() > 0) {
            filters.put("include_external_user_ids", includedExternalUserIds);
        }

        String jsonContentType = org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
        System.out.println(filters.toString());
        RequestBody body = RequestBody.create(MediaType.parse(jsonContentType), filters.toString());
        Request.Builder serviceRequestBuilder = new Request.Builder()
                .addHeader("Content-Type", jsonContentType)
                .addHeader("Authorization", "Basic " + Configuration.getOneSignalAPIAuthKey(entityId, countryId))
                .url(ONESIGNAL_NOTIFICATION_ENDPOINT)
                .post(body);
        Request serviceRequest = serviceRequestBuilder.build();
        Response response = client.newCall(serviceRequest).execute();

        if (response != null) {
            if (response.isSuccessful() && response.body() != null) {
                String result = response.body().string();
                logger.info(result);
                JSONObject json = new JSONObject(result);

                if(!json.has("recipients") || json.getInt("recipients") == 0) {
                    throw new Exception("Sent to no one");
                }

                response.body().close();
                return json.getString("id");
            } else {
                String result = response.body() != null ? response.body().string() : response.message();
                logger.error(result);
                return null;
            }
        } else {
            logger.error("No fue posible llamar a " + ONESIGNAL_NOTIFICATION_ENDPOINT);
            return null;
        }
    }

    @Override
    public boolean cancelScheduledNotification(Integer entityId, Integer countryId, String notificationId) throws Exception {
        OkHttpClient client = new OkHttpClient.Builder().build();

        Request.Builder serviceRequestBuilder = new Request.Builder()
                .addHeader("Authorization", "Basic " + Configuration.getOneSignalAPIAuthKey(entityId, countryId))
                .url(ONESIGNAL_NOTIFICATION_ENDPOINT + "/" + notificationId + "?app_id=" + Configuration.getOneSignalAppId(entityId, countryId))
                .delete();
        Request serviceRequest = serviceRequestBuilder.build();
        Response response = client.newCall(serviceRequest).execute();

        if (response != null && response.body() != null) {
            response.body().close();
            return response.isSuccessful();
        } else {
            logger.error("No fue posible llamar a " + ONESIGNAL_NOTIFICATION_ENDPOINT);
            return false;
        }
    }

    @Override
    public JSONObject applyNotificationsSegmentFilters(String[] includedSegments, String[] excludedSegments) {
        JSONObject filters = new JSONObject();

        filters.put("included_segments", new Gson().toJson(includedSegments));
        filters.put("excluded_segments", new Gson().toJson(excludedSegments));

        return filters;
    }

    @Override
    public JSONObject applyNotificationsUserBasedFilters(Integer sessionCountFilter, RelationFilter sessionCountRelationFilter, String countryFilter, Map<String, String> tagFilters) {
        JSONObject filters = new JSONObject();

        JSONArray filtersArray = new JSONArray();
        if (sessionCountFilter != null && sessionCountRelationFilter != null) {
            filtersArray.put(new JSONObject().put("field", "session_count").put("relation", sessionCountRelationFilter.symbol).put("value", sessionCountFilter.toString()));
        }

        if (countryFilter != null && "".equals(countryFilter.trim())) {
            filtersArray.put(new JSONObject().put("field", "country").put("relation", RelationFilter.EQUALS.symbol).put("value", countryFilter.toUpperCase()));
        }

        if (tagFilters != null) {
            for (Map.Entry<String, String> entry : tagFilters.entrySet()) {
                filtersArray.put(new JSONObject().put("field", "tag").put("key", entry.getKey()).put("relation", RelationFilter.EQUALS.symbol).put("value", entry.getValue()));
            }
        }

        return filters.put("filters", filtersArray);
    }

    @Override
    public JSONObject applyNotificationsScheduleDelivery(String sendAfter, String deliveryTimeOfDate, Integer daysToLive) {
        JSONObject filters = new JSONObject();

        filters.put("send_after", sendAfter);// https://documentation.onesignal.com/reference#section-delivery
        if (deliveryTimeOfDate != null) {
            filters.put("delayed_option", "timezone");
            filters.put("delivery_time_of_day", deliveryTimeOfDate);
        }
        filters.put("ttl", (daysToLive == null || daysToLive > 28 ? 28 : daysToLive) * 24 * 60 * 60);
        filters.put("priority", 10);

        return filters;
    }
}
