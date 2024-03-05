package com.affirm.sendgrid;

import com.affirm.common.util.JsonUtil;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by dev5 on 13/10/17.
 */
public class SendgridUtilCall {

    public static final OkHttpClient client = new OkHttpClient.Builder()
            .build();

    public static JSONObject callPOST(String request, String serviceUrl, String apiKey) throws Exception {

        OkHttpClient clientCall = client.newBuilder()
                .connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100, TimeUnit.SECONDS)
                .writeTimeout(100, TimeUnit.SECONDS)
                .build();
        okhttp3.MediaType mediaType = okhttp3.MediaType.parse("application/json");
        okhttp3.RequestBody body = okhttp3.RequestBody.create(mediaType, request);
        Date iniDate = new Date();
        System.out.println("[Sendgrid Call " + "{" + serviceUrl + "}] : " + request);
        Request serviceRequest = new Request.Builder()
                .url(serviceUrl)
                .post(body)
                .addHeader("Authorization", "Bearer ".concat(apiKey))
                .build();
        Response response = clientCall.newCall(serviceRequest).execute();
        String responseReturn = response.body().string();
        System.out.println("[Sendgrid Service Response [" + serviceUrl + "][" + (new Date().getTime() - iniDate.getTime()) + "]] : " + responseReturn);
        if(responseReturn != null && !responseReturn.isEmpty())
            return new JSONObject(responseReturn);
        return null;
    }

    public static boolean callDELETE(String serviceUrl, String apiKey) throws Exception {

        OkHttpClient clientCall = client.newBuilder()
                .connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100, TimeUnit.SECONDS)
                .writeTimeout(100, TimeUnit.SECONDS)
                .build();
        System.out.println("[Sendgrid Call " + "{" + serviceUrl + "}]");
        Request serviceRequest = new Request.Builder()
                .url(serviceUrl)
                .delete()
                .addHeader("Authorization", "Bearer ".concat(apiKey))
                .build();
        Response response = clientCall.newCall(serviceRequest).execute();
        if(response.isSuccessful()){
            System.out.println("[Sendgrid Service Response [" + serviceUrl + "]] : " + "Success");
            return true;
        }else{
            System.out.println("[Sendgrid Service Response [" + serviceUrl + "]] : " + response.body().string());
            return false;
        }

    }

    public static void main(String [] args) {
        try {
            JSONObject jsonResponse = callPOST("{\"name\": \"test\"}", "https://api.sendgrid.com/v3/contactdb/lists", "xxxxxxxxxxxxxxxxxxxxx");
            if(JsonUtil.getJsonArrayFromJson(jsonResponse, "errors", null) == null){
                Integer listCreated = JsonUtil.getIntFromJson(jsonResponse, "id", null);
                if(callDELETE("https://api.sendgrid.com/v3/contactdb/lists/".concat(String.valueOf(listCreated)), "xxxxxxxxxxxxxxxxxxxxx")){
                    jsonResponse = callPOST("[{\"email\": \"miberico@solven.pe\", \"first_name\": \"Martin\",\"last_name\": \"Iberico\",\"cellphone\": \"988153580\"}]", "https://api.sendgrid.com/v3/contactdb/recipients", "xxxxxxxxxxxxxxxxxxxxx");

                }
            } else{
                JSONArray errorArray = JsonUtil.getJsonArrayFromJson(jsonResponse, "errors", null);
                for(int i = 0; i < errorArray.length();i++){
                    System.out.println(JsonUtil.getStringFromJson(errorArray.getJSONObject(i), "message", null));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
