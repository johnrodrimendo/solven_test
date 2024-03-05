package com.affirm.negativebase.service;

import com.affirm.negativebase.model.NegativeBaseProcessed;
import com.affirm.preapprovedbase.model.PreApprovedBaseProcessed;
import org.springframework.web.multipart.MultipartFile;

public interface NegativeBaseService {

    void uploadFileToBucket(MultipartFile file, Integer entityId, Integer entityUserId, Character type) throws Exception;

    void uploadData(String url, Integer entityId, Character type) throws Exception;

    NegativeBaseProcessed createUploadProcessNegativeBase(String url, Integer entityId, Integer entityUserId, Character type) throws Exception;
}
