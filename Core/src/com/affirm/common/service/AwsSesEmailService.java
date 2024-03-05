package com.affirm.common.service;

import com.affirm.common.model.transactional.Attachment;
import com.amazonaws.services.simpleemail.model.*;
import org.json.JSONObject;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface AwsSesEmailService {

    GetTemplateResult getAWSSESTemplate(String templateName);

    ListTemplatesResult getAWSSESTemplates();

    CreateTemplateResult createAWSSESTemplate(Template template);

    UpdateTemplateResult updateAWSSESTemplate(Template template);

    DeleteTemplateResult deleteAWSSESTemplate(String templateName);

    SendEmailResult sendEmail(String from, String to, String[] cc, String subject, String plainMessage, String htmlMessage, JSONObject jsonVariables) throws AmazonSimpleEmailServiceException;

//    SendTemplatedEmailResult sendTemplatedEmail(String from, String to, String[] cc, String templateId, JSONObject jsonVariables) throws AmazonSimpleEmailServiceException;

//    SendBulkTemplatedEmailResult sendBulkTemplatedEmail(String from, String[] toPeople, String templateId, JSONObject jsonVariables) throws AmazonSimpleEmailServiceException;

    SendRawEmailResult sendRawEmail(Integer personInteractionId, String from, String fromName, String to, String[] cc, String subject, String plainMessage, String htmlMessage, String templateId, List<Attachment> attached, JSONObject jsonVariables, String hourOfDay, Map<String, String> mapTags) throws MessagingException, IOException, AmazonSimpleEmailServiceException;

    SendRawEmailResult sendRawEmail(Integer personInteractionId, String from, String fromName, String to, String[] cc, String subject, String plainMessage, String htmlMessage, String templateId, List<Attachment> attached, JSONObject jsonVariables, String hourOfDay, Map<String, String> mapTags, Integer personInteractinContentId) throws MessagingException, IOException, AmazonSimpleEmailServiceException;

    String getVerificationEmailStatus(String email);

    void sendEmailVerification(String emailToVerify);
}
