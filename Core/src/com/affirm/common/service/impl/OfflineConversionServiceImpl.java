package com.affirm.common.service.impl;

import com.affirm.banbif.service.impl.BanBifServiceImpl;
import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.dao.UserDAO;
import com.affirm.common.model.catalog.Currency;
import com.affirm.common.model.catalog.Entity;
import com.affirm.common.model.catalog.Product;
import com.affirm.common.model.catalog.ProductCategory;
import com.affirm.common.model.transactional.Credit;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.model.transactional.Person;
import com.affirm.common.model.transactional.User;
import com.affirm.common.service.*;
import com.affirm.common.util.CryptoUtil;
import com.affirm.system.configuration.Configuration;
import okhttp3.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service("offlineConversionService")
public class OfflineConversionServiceImpl implements OfflineConversionService {


    private static final String FACEBOOK_OFFLINE_EVENT_ID_PRD = "451598935646899";
    private static final String FACEBOOK_OFFLINE_EVENT_ID_TEST = "301524200743852";
    private static final String FACEBOOK_TOKEN = "EAADSbhUz9KMBALF3hFphOKEj3YhZBWquEbkXCK8SQb8xMqYKW9TWwuvzlyMvjU1JsbMT1KFEEmuFhy1W68IWpBYOaZA6MavFlYfY2ZAkDFwbyePliQK6B7h5nXpK4WC8NZCD6h9unLtWZBR6R0aJkfQW7WtxxfZAOWExKbCepff49npoyXyTuZB";
    private static final String GOOGLE_CLIENT_ID = "370467815349-1h462q9k15vs0t818u0lgtqtmbo5gcnn.apps.googleusercontent.com";
    private static final String GOOGLE_CLIENT_SECRET = "QV10P9zQzoRPa14JmWkli-U6";
    private static final String GOOGLE_REFRESH_TOKEN = "1/xdb_Hl9CgelGSn3Exr_JXBCPw2A0DQsSym0ixRYNCrQ";

    @Autowired
    private ErrorService errorService;
    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private UserDAO userDao;
    @Autowired
    private UtilService utilService;
    @Autowired
    private PersonDAO personDao;
    @Autowired
    private FileService fileService;

    @Override
    public void sendOfflineConversion(Credit credit) throws Exception {
        if(Configuration.hostEnvIsProduction()){
            LoanApplication loanApplication = loanApplicationDao.getLoanApplication(credit.getLoanApplicationId(), Configuration.getDefaultLocale());
            User user = userDao.getUser(loanApplication.getUserId());
            Person person = personDao.getPerson(loanApplication.getPersonId(), false, Configuration.getDefaultLocale());
            // Offline conversion Solven
            sendOfflineConversion(credit, loanApplication, user, person);
            // Ofline conversion to entity
            if (credit.getEntity().getId() == Entity.AZTECA) {
                callGA4Event(loanApplication, "step8", null, null);
                callFacebookConversion(credit, user, loanApplication, person, null, "Lead", null);
            }
            if (credit.getEntity().getId() == Entity.BANBIF) {
                callUniversalAnalyticsEvent(loanApplication, "conversion", PixelConversionService.BANBIF_STEP8_CONVERSION, BanBifServiceImpl.GA_TRACKING_ID);
            }
        }
    }

    @Override
    public void sendOfflineConversion(Credit credit, LoanApplication loanApplication, User user, Person person) {
        // Only send if it's marketplace, not salary advance and not convenio
        if (loanApplication.getEntityId() == null && loanApplication.getProductCategoryId() != ProductCategory.ADELANTO && credit.getProduct().getId() != Product.AGREEMENT) {
            callFacebookOfflineConversion(credit, user, loanApplication, person, FACEBOOK_TOKEN, "Purchase", (Configuration.hostEnvIsProduction() ? FACEBOOK_OFFLINE_EVENT_ID_PRD : FACEBOOK_OFFLINE_EVENT_ID_TEST));
            if (loanApplication.getGclid() != null && !loanApplication.getGclid().isEmpty())
                callGoogleOfflineConversion(credit, user, loanApplication);
        }
    }

    @Override
    public byte[] getAdwordsOfflineConversionExcel(String token) throws Exception {
        JSONObject tokenJson = new JSONObject(CryptoUtil.decrypt(token));
        int entityId = tokenJson.getInt("entityId");
        // Get the new conversions

        // Get the template excel
        byte[] templateFile = fileService.getPublicFile("docs/", "conversion-import-template.xls");
        // Fill theh template with the data

        // Update in the table that this data was alredy delivered

        return templateFile;
    }

    private void callFacebookOfflineConversion(Credit credit, User user, LoanApplication loanApplication, Person person, String token, String eventName, String pixelId) {
        try {

            HttpClient httpclient;
            HttpPost httppost;
            ArrayList<NameValuePair> postParameters;
            httpclient = HttpClients.createDefault();
            httppost = new HttpPost("https://graph.facebook.com/v5.0/" + pixelId + "/events");

            JSONArray dataParam = new JSONArray();
            JSONObject jsonDesembolso = new JSONObject();
            jsonDesembolso.put("currency", credit.getCurrency().getCurrency());
            jsonDesembolso.put("value", credit.getEntityCommision());
            jsonDesembolso.put("event_name", eventName);

            Date currentDate = new Date();
            long daysBetween = java.util.concurrent.TimeUnit.DAYS.convert(currentDate.getTime() - loanApplication.getRegisterDate().getTime(), TimeUnit.MILLISECONDS);
            long dateToSend;
            if (daysBetween >= 28) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(loanApplication.getRegisterDate());
                cal.add(Calendar.DATE, 27);
                dateToSend = cal.getTime().getTime() / 1000;
            } else {
                dateToSend = new Date().getTime() / 1000;
            }
            jsonDesembolso.put("event_time", dateToSend);

            JSONObject customData = new JSONObject();
            customData.put("loan_app_id", credit.getLoanApplicationId());
            jsonDesembolso.put("custom_data", customData);
            JSONObject matchKeys = new JSONObject();
            matchKeys.put("phone", new JSONArray("[\"" + DigestUtils.sha256Hex("+" + user.getCountryCode() + user.getPhoneNumber()) + "\"]"));
            matchKeys.put("email", new JSONArray("[\"" + DigestUtils.sha256Hex(user.getEmail()) + "\"]"));
            if (person.getGender() != null)
                matchKeys.put("gen", DigestUtils.sha256Hex(person.getGender() + ""));
            if (person.getCountry() != null)
                matchKeys.put("country", DigestUtils.sha256Hex(person.getCountry().getCountryCode()));
            if (person.getBirthday() != null)
                matchKeys.put("dob", DigestUtils.sha256Hex(new SimpleDateFormat("dd/MM/yyyy").format(person.getBirthday())));
            jsonDesembolso.put("match_keys", matchKeys);
            dataParam.put(jsonDesembolso);

            postParameters = new ArrayList<NameValuePair>();
            postParameters.add(new BasicNameValuePair("access_token", token));
            postParameters.add(new BasicNameValuePair("upload_tag", "desembolsos"));
            postParameters.add(new BasicNameValuePair("data", dataParam.toString()));

            httppost.setEntity(new UrlEncodedFormEntity(postParameters, "UTF-8"));

            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            String responseString = EntityUtils.toString(entity, "UTF-8");
        } catch (Exception ex) {
            errorService.onError(ex);
        }
    }

    private void callGoogleOfflineConversion(Credit credit, User user, LoanApplication loanApplication) {
        if (!Configuration.hostEnvIsProduction()) {
            return;
        }

        try {
            HttpClient httpclient;
            HttpPost httppost;
            ArrayList<NameValuePair> postParameters;
            httpclient = HttpClients.createDefault();
            httppost = new HttpPost("https://script.google.com/macros/s/AKfycbwpv4--ixVCFXlRz8FOOWpCB_yfAHCH-0gL5vl5Vccj27a9-WFN/exec");

            Date currentDate = new Date();
            long daysBetween = java.util.concurrent.TimeUnit.DAYS.convert(currentDate.getTime() - loanApplication.getRegisterDate().getTime(), TimeUnit.MILLISECONDS);
            Date dateToSend;
            if (daysBetween >= 90) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(loanApplication.getRegisterDate());
                cal.add(Calendar.DATE, 89);
                dateToSend = cal.getTime();
            } else {
                dateToSend = new Date();
            }

            postParameters = new ArrayList<NameValuePair>();
            postParameters.add(new BasicNameValuePair("Google Click ID", loanApplication.getGclid()));
            postParameters.add(new BasicNameValuePair("Conversion Name", "Solven offline disbursement"));
            postParameters.add(new BasicNameValuePair("Conversion Time", new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(dateToSend)));
            if (credit.getCurrency().getId() == Currency.PEN) {
                postParameters.add(new BasicNameValuePair("Conversion Value", (credit.getEntityCommision() / utilService.getUsdPenExchangeRate()) + ""));
                postParameters.add(new BasicNameValuePair("Conversion Currency", "USD"));
            } else {
                postParameters.add(new BasicNameValuePair("Conversion Value", credit.getEntityCommision() + ""));
                postParameters.add(new BasicNameValuePair("Conversion Currency", credit.getCurrency().getCurrency()));
            }

            httppost.setEntity(new UrlEncodedFormEntity(postParameters, "UTF-8"));

            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            String responseString = EntityUtils.toString(entity, "UTF-8");

        } catch (Exception ex) {
            errorService.onError(ex);
        }
    }

    @Override
    public void callGA4Event(LoanApplication loanApplication, String eventName, String ga4MeasurementId, String ga4ApiSecret) {
        if (loanApplication.getAuxData() == null || loanApplication.getAuxData().getGaClientId() == null)
            return;

        try {
            HttpClient httpclient;
            HttpPost httppost;
            ArrayList<NameValuePair> postParameters;
            httpclient = HttpClients.createDefault();
            httppost = new HttpPost("https://www.google-analytics.com/mp/collect?measurement_id=" + ga4MeasurementId + "&api_secret=" + ga4ApiSecret);

            JSONObject event = new JSONObject();
            event.put("name", eventName);
            JSONArray events = new JSONArray();
            events.put(event);
            JSONObject body = new JSONObject();
            body.put("client_id", loanApplication.getAuxData().getGaClientId());
            body.put("events", events);
            httppost.setEntity(new StringEntity(body.toString(), ContentType.APPLICATION_JSON));

            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
        } catch (Exception ex) {
            errorService.onError(ex);
        }
    }

    @Override
    public void callUniversalAnalyticsEvent(LoanApplication loanApplication, String eventCategory, String eventName, String trackingId) {
        try {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse("text/plain");
            RequestBody body = RequestBody.create(mediaType, "");
            Request request = new Request.Builder()
                    .url("https://www.google-analytics.com/collect?v=1&tid=" + trackingId + "&cid="+loanApplication.getUserId()+"&t=event&ec=" + eventCategory + "&ea=" + eventName)
                    .method("POST", body)
                    .header("User-Agent", "Java User Agent")
                    .build();
            Response response = client.newCall(request).execute();
        } catch (Exception ex) {
            errorService.onError(ex);
        }
    }

    @Override
    public void callFacebookConversion(Credit credit, User user, LoanApplication loanApplication, Person person, String token, String eventName, String pixelId) {
        try {

            HttpClient httpclient;
            HttpPost httppost;
            ArrayList<NameValuePair> postParameters;
            httpclient = HttpClients.createDefault();
            httppost = new HttpPost("https://graph.facebook.com/v10.0/" + pixelId + "/events");

            JSONArray dataParam = new JSONArray();
            JSONObject jsonDesembolso = new JSONObject();
            jsonDesembolso.put("event_name", eventName);

            Date currentDate = new Date();
            long daysBetween = java.util.concurrent.TimeUnit.DAYS.convert(currentDate.getTime() - loanApplication.getRegisterDate().getTime(), TimeUnit.MILLISECONDS);
            long dateToSend;
            if (daysBetween >= 28) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(loanApplication.getRegisterDate());
                cal.add(Calendar.DATE, 27);
                dateToSend = cal.getTime().getTime() / 1000;
            } else {
                dateToSend = new Date().getTime() / 1000;
            }
            jsonDesembolso.put("event_time", dateToSend);

            JSONObject customData = new JSONObject();
            jsonDesembolso.put("custom_data", customData);
            JSONObject matchKeys = new JSONObject();
            matchKeys.put("phone", new JSONArray("[\"" + DigestUtils.sha256Hex("+" + user.getCountryCode() + user.getPhoneNumber()) + "\"]"));
            matchKeys.put("email", new JSONArray("[\"" + DigestUtils.sha256Hex(user.getEmail()) + "\"]"));
            if (person.getGender() != null)
                matchKeys.put("gen", DigestUtils.sha256Hex(person.getGender() + ""));
            if (person.getCountry() != null)
                matchKeys.put("country", DigestUtils.sha256Hex(person.getCountry().getCountryCode()));
            if (person.getBirthday() != null)
                matchKeys.put("dob", DigestUtils.sha256Hex(new SimpleDateFormat("dd/MM/yyyy").format(person.getBirthday())));
            jsonDesembolso.put("match_keys", matchKeys);
            dataParam.put(jsonDesembolso);

            postParameters = new ArrayList<NameValuePair>();
            postParameters.add(new BasicNameValuePair("access_token", token));
            postParameters.add(new BasicNameValuePair("data", dataParam.toString()));

            httppost.setEntity(new UrlEncodedFormEntity(postParameters, "UTF-8"));

            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            String responseString = EntityUtils.toString(entity, "UTF-8");
            System.out.println(responseString);
        } catch (Exception ex) {
            errorService.onError(ex);
        }
    }

}
