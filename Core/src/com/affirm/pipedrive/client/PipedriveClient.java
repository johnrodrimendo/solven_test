package com.affirm.pipedrive.client;

import okhttp3.*;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service("pipedriveClient")
public class PipedriveClient {

    private final String BASE_URL = "https://solven.pipedrive.com/v1/";
    private final String TOKEN = "api_token=xxxxxxxxxxxxxxxxxxxxx";
    private final String AMPERSAND = "&";
    private final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final Logger logger = Logger.getLogger(PipedriveClient.class);



    public Boolean insertDeal(Integer personId, Integer companyId) throws IOException {
        boolean created = false;
        OkHttpClient client = new OkHttpClient.Builder().build();
        StringBuilder sb = new StringBuilder();
        sb.append(BASE_URL);
        sb.append("deals?");
        sb.append(TOKEN);

        JSONObject jsonBody = new JSONObject();
        jsonBody.put("title", "R.Solic.Landing");
        jsonBody.put("person_id", personId);
        jsonBody.put("org_id", companyId);
        RequestBody body = RequestBody.create(JSON, jsonBody.toString());
        logger.info(String.format("request insert : %s",jsonBody.toString()));
        Request.Builder serviceRequestBuilder = new Request.Builder()
                .addHeader("Content-Type", "application/json")
                .url(sb.toString())
                .post(body);
        Request serviceRequest = serviceRequestBuilder.build();
        Response response = client.newCall(serviceRequest).execute();
        logger.info(String.format("response : code %d %s",response.code(),response.body().toString()));
        if (response.code() == 201 && response.message().equals("Created"))
            created = true;

        return created;
    }

    public Boolean registerPersonClient(String personName, String email, String phone,String position) throws IOException {

        boolean created = false;
        OkHttpClient client = new OkHttpClient.Builder().build();
        StringBuilder sb = new StringBuilder();
        sb.append(BASE_URL);
        sb.append("persons?");
        sb.append(TOKEN);

        JSONObject json = new JSONObject();
        json.put("name", personName);
        json.put("email", email);
        json.put("phone", phone);
        json.put("f3da3d6dee6f7c25ec57782ee56924c15c527289",position);
        RequestBody body = RequestBody.create(JSON, json.toString());
        logger.info(String.format("request register person : %s",json.toString()));

        Request.Builder serviceRequestBuilder = new Request.Builder()
                .addHeader("Content-Type", "application/json")
                .url(sb.toString())
                .post(body);
        Request serviceRequest = serviceRequestBuilder.build();
        Response response = client.newCall(serviceRequest).execute();
        if (response.code() == 201 && response.message().equals("Created"))
            created = true;
        return created;
    }

    public Boolean registerCompanyClient(final String companyName, String source, String size) throws IOException {

        boolean created = false;
        OkHttpClient client = new OkHttpClient.Builder().build();
        StringBuilder sb = new StringBuilder();
        sb.append(BASE_URL);
        sb.append("organizations?");
        sb.append(TOKEN);

        JSONObject json = new JSONObject();
        json.put("name", companyName);
        json.put("e7c081a078daa9d341938c2b8a9b760ab3ff4f0b", size);
        json.put("99438205064212d3738535636557face3f87f1a0", source);
        logger.info(String.format("request register company : %s",json.toString()));

        RequestBody body = RequestBody.create(JSON, json.toString());
        Request.Builder serviceRequestBuilder = new Request.Builder()
                .addHeader("Content-Type", "application/json")
                .url(sb.toString())
                .post(body);
        Request serviceRequest = serviceRequestBuilder.build();
        Response response = client.newCall(serviceRequest).execute();
        if (response.code() == 201 && response.message().equals("Created"))
            created = true;
        return created;
    }

    public Integer findCompanyIdByNameClient(final String companyName) throws IOException {
        OkHttpClient client = new OkHttpClient.Builder().build();
        StringBuilder sb = new StringBuilder();
        sb.append(BASE_URL);
        sb.append("organizations/find?term=");
        sb.append(companyName);
        sb.append(AMPERSAND);
        sb.append(TOKEN);
        Request.Builder serviceRequestBuilder = new Request.Builder()
                .addHeader("Content-Type", "application/json")
                .url(sb.toString())
                .get();
        Request serviceRequest = serviceRequestBuilder.build();
        Response response = client.newCall(serviceRequest).execute();
        JSONObject jsonObject = new JSONObject(response.body().string());
        logger.info(String.format("request find Company : %s",jsonObject.toString()));

        Integer id = null;
        if (jsonObject.get("success").equals(true) && !jsonObject.get("data").equals(null)) {
            JSONArray jsonArray = (JSONArray) jsonObject.get("data");
            search:
            {
                for (Object obj : jsonArray) {
                    JSONObject json = (JSONObject) obj;
                    if (json.getString("name").equals(companyName)) {
                        id = json.getInt("id");
                        break search;
                    }

                }
            }
        }
        return id;
    }


    public Integer findPersonIdByNameAndEmailClient(String personName, String email) throws IOException {
        OkHttpClient client = new OkHttpClient.Builder().build();
        StringBuilder sb = new StringBuilder();
        sb.append(BASE_URL);
        sb.append("persons/find?term=");
        sb.append(personName);
        sb.append(AMPERSAND);
        sb.append(TOKEN);
        Request.Builder serviceRequestBuilder = new Request.Builder()
                .addHeader("Content-Type", "application/json")
                .url(sb.toString())
                .get();
        Request serviceRequest = serviceRequestBuilder.build();
        Response response = client.newCall(serviceRequest).execute();
        JSONObject jsonObject = new JSONObject(response.body().string());
        logger.info(String.format("request find person by name & email : %s",jsonObject.toString()));

        Integer id = null;
        if (jsonObject.get("success").equals(true) && !jsonObject.get("data").equals(null)) {
            JSONArray jsonArray = (JSONArray) jsonObject.get("data");
            search:
            {
                for (Object obj : jsonArray) {
                    JSONObject json = (JSONObject) obj;
                    if (json.getString("email").equals(email)) {
                        id = json.getInt("id");
                        break search;
                    }

                }
            }
        }
        return id;
    }
}