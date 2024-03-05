package com.affirm.wavy.service.impl;

import com.affirm.system.configuration.Configuration;
import com.affirm.wavy.model.Destination;
import com.affirm.wavy.model.HSMMessage;
import com.affirm.wavy.model.Message;
import com.affirm.wavy.service.WavyService;
import com.google.gson.Gson;
import okhttp3.*;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WavyServiceImpl implements WavyService {

    private static final Logger logger = Logger.getLogger(WavyServiceImpl.class);

    private static final String WAVY_URI = "https://api-messaging.movile.com";
    private static final String WAVY_WHATSAPP_SEND_MESSAGE_ENDPOINT = WAVY_URI + "/v1/whatsapp/send";

    @Override
    public void sendTextMessage(List<Destination> destinations, Message message, String campaignAlias) throws Exception {
        sendTextMessage(destinations, message, campaignAlias, null);
    }

    @Override
    public void sendTextMessage(List<Destination> destinations, Message message, String campaignAlias, String defaultExtraInfo) throws Exception {
        JSONObject jsonBody = new JSONObject()
                .put("destinations", destinations)
                .put("message", new JSONObject(new Gson().toJson(message)))
                .put("campaignAlias", campaignAlias)
                .put("defaultExtraInfo", defaultExtraInfo);

        sendMessage(WAVY_WHATSAPP_SEND_MESSAGE_ENDPOINT, jsonBody);
    }

    @Override
    public void sendHSMMessage(List<Destination> destinations, HSMMessage message, String campaignAlias) throws Exception {
        sendHSMMessage(destinations, message, campaignAlias, null);
    }

    @Override
    public void sendHSMMessage(List<Destination> destinations, HSMMessage message, String campaignAlias, String defaultExtraInfo) throws Exception {
        JSONObject jsonBody = new JSONObject()
                .put("destinations", destinations)
                .put("message", new JSONObject(new Gson().toJson(message)))
                .put("campaignAlias", campaignAlias)
                .put("defaultExtraInfo", defaultExtraInfo);

        sendMessage(WAVY_WHATSAPP_SEND_MESSAGE_ENDPOINT, jsonBody);
    }

    private void sendMessage(String endpoint, JSONObject jsonBody) throws Exception {
        OkHttpClient client = new OkHttpClient.Builder().build();

        System.out.println(jsonBody.toString());

        String jsonContentType = org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
        RequestBody body = RequestBody.create(MediaType.parse(jsonContentType), jsonBody.toString());
        Request.Builder serviceRequestBuilder = new Request.Builder()
                .addHeader("Content-Type", jsonContentType)
                .addHeader("username", Configuration.WAVY_USERNAME)
                .addHeader("authenticationToken", Configuration.WAVY_AUTH_TOKEN)
                .url(endpoint)
                .post(body);
        Request serviceRequest = serviceRequestBuilder.build();
        Response response = client.newCall(serviceRequest).execute();

        if (response != null) {
            logger.debug("Status code " + response.code());
            if (response.isSuccessful() && response.body() != null) {
                String result = response.body().string();
                logger.debug(result);
//                System.out.println(result);
            } else {
                String result = response.body() != null ? response.body().string() : response.message();
                logger.error(result);
//                System.out.println(result);
                throw new Exception(result);
            }
            response.body().close();
        } else {
            String errorMessage = "No fue posible llamar a " + endpoint;
            logger.error(errorMessage);
//            System.out.println(errorMessage);
            throw new Exception(errorMessage);
        }
    }
}
