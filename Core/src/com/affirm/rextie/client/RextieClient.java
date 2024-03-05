package com.affirm.rextie.client;

import com.affirm.rextie.model.Rextie;
import com.affirm.system.configuration.Configuration;
import okhttp3.*;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

@Service("rextieClient")
public class RextieClient {

    private static final Logger logger = Logger.getLogger(RextieClient.class);

    private final String REXTIE_RATE_ENDPOINT =  "/api/v1/fxrates/rate/";

    private final String REXTIE_URL = Configuration.getRextieDomain() + REXTIE_RATE_ENDPOINT + "?utm_source=solven";

    private final MediaType JSON = MediaType.parse(org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE);

    public Rextie exchangeRateQuote(String sourceCurrencyType, String targetCurrencyType, Double sourceAmount, Double targetAmount) throws Exception {
        OkHttpClient client = new OkHttpClient.Builder().build();

        JSONObject jsonBody = new JSONObject();
        jsonBody.put("source_currency", sourceCurrencyType);
        jsonBody.put("target_currency", targetCurrencyType);
        jsonBody.put("source_amount", sourceAmount);
        if (null == sourceAmount) {
            jsonBody.put("target_amount", targetAmount);
        }

        RequestBody body = RequestBody.create(JSON, jsonBody.toString());
        Request.Builder serviceRequestBuilder = new Request.Builder()
                .addHeader("Content-Type", "application/json")
                .url(REXTIE_URL)
                .post(body);
        Request serviceRequest = serviceRequestBuilder.build();
        Response response = client.newCall(serviceRequest).execute();

        if (response != null) {
            if (response.code() == 201) {
                Rextie rextie = new Rextie();
                JSONObject json = new JSONObject(response.body().string());

                rextie.setBuyRate(json.getDouble("fx_rate_buy"));
                rextie.setSellRate(json.getDouble("fx_rate_sell"));

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
                Date date = sdf.parse(json.getString("valid_until"));

                rextie.setQuoteExpiringDate(date);
                rextie.setSourceCurrency(json.getString("source_currency"));
                rextie.setTargetCurrency(json.getString("target_currency"));
                rextie.setSourceCurrencyAmount(json.getDouble("source_amount"));
                rextie.setTargetCurrencyAmount(json.getDouble("target_amount"));
                rextie.setPotentialSavings(json.getDouble("saved_bank_amount"));

                return rextie;
            } else {
                logger.error(String.format("response : code : %d %s", response.code(), "Error en peticion"));
                return null;
            }
        } else {
            logger.error("No se obtuvo respuesta");
            return null;
        }
    }
}
