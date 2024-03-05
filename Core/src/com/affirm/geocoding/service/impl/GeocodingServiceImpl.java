package com.affirm.geocoding.service.impl;

import com.affirm.acceso.model.Direccion;
import com.affirm.common.dao.*;
import com.affirm.common.model.ExternalWSRecord;
import com.affirm.common.model.catalog.Ubigeo;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.ErrorService;
import com.affirm.common.service.UtilService;
import com.affirm.geocoding.model.GeocodingResponse;
import com.affirm.geocoding.model.GeocodingResult;
import com.affirm.geocoding.service.GeocodingService;
import com.affirm.pagolink.service.impl.PagoLinkClientServiceImpl;
import com.affirm.system.configuration.Configuration;
import com.google.gson.Gson;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.http.HttpHeaders;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service("geocodingService")
public class GeocodingServiceImpl implements GeocodingService {

    private static Logger logger = Logger.getLogger(GeocodingServiceImpl.class);

    public final OkHttpClient client = new OkHttpClient.Builder().build();

    @Autowired
    private LoanApplicationDAO loanApplicationDAO;
    @Autowired
    private PersonDAO personDAO;
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private ExternalWSRecordDAO externalWSRecordDAO;
    @Autowired
    private CatalogService catalogService;

    @Override
    public GeocodingResponse getGeoLocation(int loanApplicationId, String urlParams) {
        OkHttpClient.Builder clientBuilder = client.newBuilder()
                .connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100, TimeUnit.SECONDS)
                .writeTimeout(100, TimeUnit.SECONDS);

        OkHttpClient clientCall = clientBuilder.build();


        okhttp3.MediaType mediaType = okhttp3.MediaType.parse(org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE);

        urlParams = urlParams.replaceAll("\\s+" , " ").replaceAll("\\s" , "+");

        String requestUrl = String.format("https://maps.googleapis.com/maps/api/geocode/json?address=%s&key=%s", urlParams, Configuration.GOOGLE_API_KEY);

        Request.Builder serviceRequestBuilder = new Request.Builder()
                .url(requestUrl)
                .get();
        Request serviceRequest = serviceRequestBuilder.build();

        try {
            Response response = clientCall.newCall(serviceRequest).execute();
            ExternalWSRecord externalWSRecord = new ExternalWSRecord(loanApplicationId ,new Date(),null,requestUrl,null,null,null);
            String result = response != null && response.body() != null ? response.body().string() : null;
            externalWSRecord.setResponse(result);
            externalWSRecord.setResponseHttpCode(response != null && response.networkResponse() != null ? response.networkResponse().code() : null);
            externalWSRecordDAO.insertExternalWSRecord(externalWSRecord);
            return new Gson().fromJson(result, GeocodingResponse.class);
        } catch (Exception ex) {
            ex.printStackTrace();
            return  null;
        }

    }

    @Override
    public GeocodingResult getGeoLocationResult(int loanApplicationId, Direccion direccion) throws Exception {
        Ubigeo ubigeo = catalogService.getUbigeo(direccion.getUbigeo());
        GeocodingResult geocodingResult = null;
        if(ubigeo != null){
            String firstUrlParams = String.format("%s+%s+%s+%s+%s+%s", ubigeo.getDepartment().getName(), ubigeo.getProvince().getName(), ubigeo.getDistrict().getName(), direccion.getNombreZona(), direccion.getNombreVia(), direccion.getNumeroVia());
            GeocodingResponse response = getGeoLocation(loanApplicationId, firstUrlParams);
            if(response != null && response.getResults() != null && !response.getResults().isEmpty()){
                geocodingResult = response.getResults().get(0);
            }
            if(geocodingResult == null){
                String secondUrlParams = String.format("%s+%s+%s+%s", ubigeo.getDepartment().getName(), ubigeo.getProvince().getName(), ubigeo.getDistrict().getName(), direccion.getNombreZona());
                GeocodingResponse secondResponse = getGeoLocation(loanApplicationId, secondUrlParams);
                if(secondResponse != null && secondResponse.getResults() != null && !secondResponse.getResults().isEmpty()){
                    geocodingResult = secondResponse.getResults().get(0);
                }
            }
            if(geocodingResult == null){
                String thirdUrlParams = String.format("%s+%s+%s", ubigeo.getDepartment().getName(), ubigeo.getProvince().getName(), ubigeo.getDistrict().getName());
                GeocodingResponse thirdResponse = getGeoLocation(loanApplicationId, thirdUrlParams);
                if(thirdResponse != null && thirdResponse.getResults() != null && !thirdResponse.getResults().isEmpty()){
                    geocodingResult = thirdResponse.getResults().get(0);
                }
            }
        }
        return geocodingResult;
    }
}
