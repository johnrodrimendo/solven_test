package com.affirm.preapprovedbase.service;

import com.affirm.preapprovedbase.model.PreApprovedBaseProcessed;
import org.springframework.web.multipart.MultipartFile;

public interface PreApprovedBaseService {

    void uploadFileToBucket(MultipartFile file, Integer entityId, Integer entityUserId) throws Exception;

    void uploadData(String url, Integer entityId) throws Exception;

    PreApprovedBaseProcessed createUploadProcessPreApprovedBase(String url, Integer entityId, Integer entityUserId) throws Exception;
}
