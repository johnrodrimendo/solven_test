package com.affirm.bitly.service.impl;

import com.affirm.bitly.model.ShortLinkRequest;
import com.affirm.bitly.model.ShortLinkResult;
import com.affirm.bitly.service.BitlyService;
import com.affirm.common.dao.ExternalWSRecordDAO;
import com.affirm.common.model.ExternalWSRecord;
import com.affirm.intico.model.InticoConfirmSmsRequest;
import com.affirm.intico.model.InticoConfirmSmsResponse;
import com.affirm.intico.model.InticoSendSmsRequest;
import com.affirm.intico.model.InticoSendSmsResponse;
import com.affirm.intico.service.InticoClientService;
import com.affirm.system.configuration.Configuration;
import com.google.gson.Gson;
import okhttp3.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;


@Service("bitlyService")
public class BitlyServiceImpl implements BitlyService {


    private String token;
    private String shortLinkUri;

    private static Logger logger = Logger.getLogger(BitlyServiceImpl.class);

    BitlyServiceImpl(){
        getConfiguration();
    }

    private void getConfiguration(){
        token = System.getenv("BITLY_TOKEN");
        shortLinkUri = System.getenv("BITLY_LINK_URI");
    }

    @Override
    public ShortLinkResult createShortLink(ShortLinkRequest shortLinkRequest){
        org.springframework.http.MediaType mediaType = new MediaType("application", "json");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType);
        headers.set("Authorization","Bearer "+token);
        HttpEntity<ShortLinkRequest> request = new HttpEntity<>(shortLinkRequest, headers);
        RestTemplate restTemplate = new RestTemplate();
        try {
            String response = restTemplate.postForObject(shortLinkUri, request, String.class);
            ShortLinkResult result = new Gson().fromJson(response, ShortLinkResult.class);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
