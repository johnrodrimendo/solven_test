package com.affirm.common.service;

import com.affirm.common.model.transactional.Attachment;
import com.amazonaws.services.simpleemail.model.*;
import com.amazonaws.services.textract.model.DetectDocumentTextResult;
import org.json.JSONObject;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface AwsTextractService {

    DetectDocumentTextResult getDetectDocumentTextResult(String bucket, String name);

    List<String> getDetectedLineTexts(String bucket, String name);

    JSONObject analyzeDoc(String documentPath, String bucket);
}
