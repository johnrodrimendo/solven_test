package com.affirm.heroku;

import com.affirm.heroku.model.Dyno;
import com.google.gson.Gson;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class HerokuServiceCall {

    private static final Gson gson = new Gson();
    private static final String TOKEN = System.getenv("HEROKU_TOKEN");

    public Dyno[] getDynos(String url) {
        HttpEntity<?> entity = new HttpEntity<>(getHeaders());
        RestTemplate restTemplate = new RestTemplate();

        HttpEntity<String> response = null;
        try {
            response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (response != null) {
            return gson.fromJson(response.getBody(), Dyno[].class);
        }

        return null;
    }

    private static HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", TOKEN));
        headers.add(HttpHeaders.ACCEPT, "application/vnd.heroku+json; version=3");
        return headers;
    }

}
