package com.affirm.mati.service;

import com.affirm.common.dao.ExternalWSRecordDAO;
import com.affirm.common.dao.UserDAO;
import com.affirm.common.model.ExternalWSRecord;
import com.affirm.common.model.transactional.UserFile;
import com.affirm.common.service.FileService;
import com.affirm.common.util.ImageUtil;
import com.affirm.common.util.JsonUtil;
import com.affirm.mati.model.CreateVerificationResponse;
import com.affirm.mati.model.MatiValidationError;
import com.affirm.rextie.client.RextieClient;
import com.affirm.rextie.model.Rextie;
import com.affirm.system.configuration.Configuration;
import com.google.gson.Gson;
import okhttp3.*;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@Service
public class MatiService {

    private static final Logger logger = Logger.getLogger(MatiService.class);

    private String ENCODED_CLIENT_ID_SECRET = "xxxxxxxxxxxxxxxxxxxx";
    private String FLOW_ID = "xxxxxxxxxxxxxxxxxxxx";
    private String WEBHOOK_SECRET = "xxxxxxxxxxxxxxxxxxxx";

    @Autowired
    private FileService fileService;
    @Autowired
    private UserDAO userDao;
    @Autowired
    private ExternalWSRecordDAO externalWSRecordDAO;


    MatiService(){
        if(!Configuration.hostEnvIsProduction()){
            FLOW_ID = "xxxxxxxxxxxxxxxxxxxx";
            WEBHOOK_SECRET = "xxxxxxxxxxxxxxxxxxxx";
        }
    }

    public String getOauthToken() throws Exception {
        OkHttpClient client = new OkHttpClient.Builder().build();
        RequestBody formBody = new FormBody.Builder()
                .add("grant_type", "client_credentials")
                .build();
        Request.Builder serviceRequestBuilder = new Request.Builder()
                .addHeader("Authorization", "Basic " + ENCODED_CLIENT_ID_SECRET)
                .url("https://api.getmati.com/oauth")
                .post(formBody);
        Request serviceRequest = serviceRequestBuilder.build();
        Response response = client.newCall(serviceRequest).execute();

        if (response != null) {
            if (response.code() == 200) {
                JSONObject json = new JSONObject(response.body().string());
                return JsonUtil.getStringFromJson(json, "access_token", null);
            } else {
                throw new Exception(String.format("Mati response : code : %d %s", response.code(), "Error en peticion"));
            }
        } else {
            throw new Exception("Mati service return no response");
        }
    }

    public CreateVerificationResponse createVerification(int loanApplicationId, int matiResultId, String authToken) throws Exception {
        OkHttpClient client = new OkHttpClient.Builder().build();
        JSONObject jsonMetadata = new JSONObject();
        jsonMetadata.put("loanId", loanApplicationId);
        jsonMetadata.put("matiResultId", matiResultId);
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("flowId", FLOW_ID);
        jsonBody.put("metadata", jsonMetadata);

        RequestBody body = RequestBody.create(MediaType.parse(org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE), jsonBody.toString());
        Request.Builder serviceRequestBuilder = new Request.Builder()
                .addHeader("Authorization", "Bearer " + authToken)
                .url("https://api.getmati.com/v2/verifications")
                .post(body);
        Request serviceRequest = serviceRequestBuilder.build();
        Response response = client.newCall(serviceRequest).execute();

        String result = response != null && response.body() !=null ? response.body().string() : null;
        ExternalWSRecord externalWSRecord = new ExternalWSRecord(loanApplicationId,new Date(),null,"https://api.getmati.com/v2/verifications",new Gson().toJson(jsonMetadata),
                result,
                response != null && response.networkResponse() != null ? response.networkResponse().code() : null);
        externalWSRecordDAO.insertExternalWSRecord(externalWSRecord);

        if (response != null) {
            if (response.code() == 200) {
                CreateVerificationResponse verificationResponse = new Gson().fromJson(result, CreateVerificationResponse.class);
                return verificationResponse;
            } else {
                throw new Exception(String.format("Mati response : code : %d %s", response.code(), "Error en peticion"));
            }
        } else {
            throw new Exception("Mati service return no response");
        }
    }

    public List<MatiValidationError> sendDocumentation(int loanApplicationId, String matiIdentityId, Integer selfieEntityUserFileId, Integer documentFrontUserFileId, Integer documentBackUserFileId, String authToken) throws Exception {
        UserFile selfieUserFile = userDao.getUserFile(selfieEntityUserFileId);
        UserFile docFrontUserFile = userDao.getUserFile(documentFrontUserFileId);
        UserFile docBackUserFile = userDao.getUserFile(documentBackUserFileId);

        byte[] selfieBytes = fileService.getUserFile(selfieUserFile.getUserId(), selfieUserFile.getFileName(), false);
        byte[] docFrontBytes = fileService.getUserFile(docFrontUserFile.getUserId(), docFrontUserFile.getFileName(), false);
        byte[] docBackBytes = fileService.getUserFile(docBackUserFile.getUserId(), docBackUserFile.getFileName(), false);

        ImageUtil iu = new ImageUtil();
        List<MatiValidationError> incorrectSizeFiles = new ArrayList<>();
/*        if(!iu.isValidImageSize(selfieBytes, ImageUtil.IMG_WIDTH_MIN_MATI, ImageUtil.IMG_WIDTH_MIN_MATI)){
            incorrectSizeFiles.add(new MatiValidationError(selfieEntityUserFileId, MatiValidationError.MATI_IMAGE_SIZE_ERROR));
        }*/
        if(!iu.isValidImageSize(docFrontBytes, ImageUtil.IMG_WIDTH_MIN_MATI, ImageUtil.IMG_HEIGHT_MIN_MATI)){
            incorrectSizeFiles.add(new MatiValidationError(documentFrontUserFileId, MatiValidationError.MATI_IMAGE_SIZE_ERROR));
        }
        if(!iu.isValidImageSize(docBackBytes, ImageUtil.IMG_WIDTH_MIN_MATI, ImageUtil.IMG_HEIGHT_MIN_MATI)){
            incorrectSizeFiles.add(new MatiValidationError(documentBackUserFileId, MatiValidationError.MATI_IMAGE_SIZE_ERROR));
        }
        if(!incorrectSizeFiles.isEmpty()) return incorrectSizeFiles;

        JSONArray inputs = new JSONArray();
        inputs.put(new JSONObject("{\"inputType\":\"document-photo\",\"group\":0,\"data\":{\"type\":\"national-id\",\"country\":\"PE\",\"page\":\"front\",\"filename\":\"" + docFrontUserFile.getFileName() + "\"}}"));
        inputs.put(new JSONObject("{\"inputType\":\"document-photo\",\"group\":0,\"data\":{\"type\":\"national-id\",\"country\":\"PE\",\"page\":\"back\",\"filename\":\"" + docBackUserFile.getFileName() + "\"}}"));
        inputs.put(new JSONObject("{\"inputType\":\"selfie-photo\",\"data\":{\"type\":\"selfie-photo\",\"filename\":\"" + selfieUserFile.getFileName() + "\"}}"));

        OkHttpClient client = new OkHttpClient.Builder().build();
        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("inputs", inputs.toString())
                .addFormDataPart("document", docFrontUserFile.getFileName(),
                        RequestBody.create(MediaType.parse("application/octet-stream"),
                                docFrontBytes))
                .addFormDataPart("document", docBackUserFile.getFileName(),
                        RequestBody.create(MediaType.parse("application/octet-stream"),
                                docBackBytes))
                .addFormDataPart("document", selfieUserFile.getFileName(),
                        RequestBody.create(MediaType.parse("application/octet-stream"),
                                selfieBytes))
                .build();
        Request.Builder serviceRequestBuilder = new Request.Builder()
                .addHeader("Authorization", "Bearer " + authToken)
                .url("https://api.getmati.com/v2/identities/" + matiIdentityId + "/send-input")
                .post(body);
        Request serviceRequest = serviceRequestBuilder.build();
        Response response = client.newCall(serviceRequest).execute();

        String resultString = response != null && response.body() !=null ? response.body().string() : null;
        ExternalWSRecord externalWSRecord = new ExternalWSRecord(loanApplicationId,new Date(),null,"https://api.getmati.com/v2/identities/" + matiIdentityId + "/send-input",new Gson().toJson(inputs),
                resultString,
                response != null && response.networkResponse() != null ? response.networkResponse().code() : null);
        externalWSRecordDAO.insertExternalWSRecord(externalWSRecord);

        if (response != null) {
            if (response.code() == 201) {
                List<MatiValidationError> failedUserFileIds = new ArrayList<>();
                JSONArray array = new JSONArray(resultString);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject result = array.getJSONObject(i);
                    if(result.has("error")){
                        if(i == 0){
                            failedUserFileIds.add(new MatiValidationError(documentFrontUserFileId, result.getJSONObject("error").toString()));
                        }else if(i == 1){
                            failedUserFileIds.add(new MatiValidationError(documentBackUserFileId, result.getJSONObject("error").toString()));
                        }else{
                            failedUserFileIds.add(new MatiValidationError(selfieEntityUserFileId, result.getJSONObject("error").toString()));
                        }
                    }
                }
                return failedUserFileIds;
            } else {
                throw new Exception(String.format("Mati response : code : %d %s", response.code(), "Error en peticion"));
            }
        } else {
            throw new Exception("Mati service return no response");
        }
    }

    public String getVerification(String matiVerificationId, String authToken) throws Exception {
        OkHttpClient client = new OkHttpClient.Builder().build();
        Request.Builder serviceRequestBuilder = new Request.Builder()
                .addHeader("Authorization", "Bearer " + authToken)
                .url("https://api.getmati.com/v2/verifications/" + matiVerificationId)
                .get();
        Request serviceRequest = serviceRequestBuilder.build();
        Response response = client.newCall(serviceRequest).execute();

        String result = response != null && response.body() !=null ? response.body().string() : null;
        ExternalWSRecord externalWSRecord = new ExternalWSRecord(null,new Date(),null,"https://api.getmati.com/v2/verifications/" + matiVerificationId, null,
                result,
                response != null && response.networkResponse() != null ? response.networkResponse().code() : null);
        externalWSRecordDAO.insertExternalWSRecord(externalWSRecord);

        if (response != null) {
            if (response.code() == 200) {
                return result;
            } else {
                throw new Exception(String.format("Mati response : code : %d %s", response.code(), "Error en peticion"));
            }
        } else {
            throw new Exception("Mati service return no response");
        }
    }
}
