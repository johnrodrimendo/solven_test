package com.affirm.livenessapi.service.impl;

import com.affirm.common.dao.*;
import com.affirm.common.model.catalog.EntityWebService;
import com.affirm.common.model.transactional.EntityWebServiceLog;
import com.affirm.common.model.transactional.UserFile;
import com.affirm.common.service.AwsSesEmailService;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.ErrorService;
import com.affirm.common.service.FileService;
import com.affirm.livenessapi.model.response.LivenessApiResponse;
import com.affirm.livenessapi.service.LivenessApiRestService;
import com.affirm.livenessapi.util.LivenessApiUtilCall;
import com.affirm.security.dao.SecurityDAO;
import com.google.gson.Gson;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;


@Service("livenessApiRestService")
public class LivenessApiRestServiceImpl implements LivenessApiRestService {

    @Autowired
    private CatalogService catalogService;
    @Autowired
    private LivenessApiUtilCall livenessApiUtilCall;
    @Autowired
    private ErrorService errorService;
    @Autowired
    private LoanApplicationDAO loanApplicationDAO;
    @Autowired
    private PersonDAO personDAO;
    @Autowired
    private TranslatorDAO translatorDAO;
    @Autowired
    private WebServiceDAO webServiceDAO;
    @Autowired
    private AwsSesEmailService awsSesEmailService;
    @Autowired
    private ExternalDAO externalDAO;
    @Autowired
    private SecurityDAO securityDAO;
    @Autowired
    private UserDAO userDao;
    @Autowired
    private FileService fileService;


    @Override
    public LivenessApiResponse getLivenessResponse(int loanApplicationId, Integer selfieUserFileId, Integer recordingUserFileId) throws Exception {

        UserFile selfieUserFile = userDao.getUserFile(selfieUserFileId);
        UserFile recordingUserFile = userDao.getUserFile(recordingUserFileId);

        byte[] selfieBytes = fileService.getUserFile(selfieUserFile.getUserId(), selfieUserFile.getFileName(), false);
        byte[] recordingBytes = fileService.getUserFile(recordingUserFile.getUserId(), recordingUserFile.getFileName(), false);

        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("video", recordingUserFile.getFileName(),
                        RequestBody.create(MediaType.parse("application/octet-stream"),
                                recordingBytes))
                .addFormDataPart("selfie", selfieUserFile.getFileName(),
                        RequestBody.create(MediaType.parse("application/octet-stream"),
                                selfieBytes))
                .build();

        okhttp3.MediaType mediaType = okhttp3.MediaType.parse(org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE);

        EntityWebServiceLog<JSONObject> webServiceResponse = null;
        try {
            webServiceResponse = livenessApiUtilCall.call(catalogService.getEntityWebService(EntityWebService.LIVENESS_API), new JSONObject().toString(), loanApplicationId, body, mediaType);
            if(Arrays.asList(EntityWebServiceLog.STATUS_FAILED).contains(webServiceResponse.getStatus())) throw new Exception("LIVENESS WS - SERVICE FAILED");
            LivenessApiResponse response = new Gson().fromJson(webServiceResponse.getRestResponse().toString(),LivenessApiResponse.class);
            return response;
        }
        catch (Exception ex){
            errorService.onError(ex);
        }
        return null;
    }
}
