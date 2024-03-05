package com.affirm.common.service.impl;

import com.affirm.common.model.transactional.Attachment;
import com.affirm.common.service.AwsSesEmailService;
import com.affirm.common.service.EmailService;
import com.affirm.common.util.JsonUtil;
import com.affirm.system.configuration.Configuration;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.*;
import org.apache.log4j.Logger;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.detect.Detector;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;

@Service
public class AwsSesEmailServiceImpl implements AwsSesEmailService {

    private static Logger logger = Logger.getLogger(AwsSesEmailServiceImpl.class);

    private static final String AWS_ACCESSKEY = System.getenv("AWS_ACCESS_KEY_ID");
    private static final String AWS_SECRETKEY = System.getenv("AWS_SECRET_ACCESS_KEY");
    private static final Regions AWS_SES_REGION = Regions.US_EAST_1;
    private static final boolean REDIRECT_SENDGRID = false;

    @Autowired
    private EmailService emailService;

    private AmazonSimpleEmailService ses;

//    https://www.cloudtechpro.com/sending-simple-email-using-simple-email-service

    @PostConstruct
    public void init() {
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(AWS_ACCESSKEY, AWS_SECRETKEY);
        AWSStaticCredentialsProvider initialCredentialsProvider = new AWSStaticCredentialsProvider(awsCredentials);
        ses = AmazonSimpleEmailServiceClientBuilder.standard()
                .withCredentials(initialCredentialsProvider)
                .withRegion(AWS_SES_REGION)
                .build();
    }

    @Override
    public GetTemplateResult getAWSSESTemplate(String templateName) {
        GetTemplateResult result;

        try {
            GetTemplateRequest request = new GetTemplateRequest();
            request.setTemplateName(templateName);

            result = ses.getTemplate(request);

        } catch (TemplateDoesNotExistException e) {
            logger.error(e.getMessage());
            result = null;
        }

        return result;
    }

    @Override
    public ListTemplatesResult getAWSSESTemplates() {
        ListTemplatesResult result;

        try {
            ListTemplatesRequest request = new ListTemplatesRequest();
            request.setMaxItems(50);

            result = ses.listTemplates(request);

        } catch (TemplateDoesNotExistException e) {
            logger.error(e.getMessage());
            result = null;
        }

        return result;
    }

    @Override
    public CreateTemplateResult createAWSSESTemplate(Template template) {

        try {
            CreateTemplateRequest request = new CreateTemplateRequest();
            request.setTemplate(template);

            return ses.createTemplate(request);
        } catch (AlreadyExistsException | InvalidTemplateException | LimitExceededException e) {
            logger.error(e.getMessage());
            return null;
        }

    }

    @Override
    public UpdateTemplateResult updateAWSSESTemplate(Template template) {

        try {
            UpdateTemplateRequest request = new UpdateTemplateRequest();
            request.setTemplate(template);

            return ses.updateTemplate(request);
        } catch (InvalidTemplateException | TemplateDoesNotExistException e) {
            logger.error(e.getMessage());
            return null;
        }

    }

    @Override
    public DeleteTemplateResult deleteAWSSESTemplate(String templateName) {

        try {
            DeleteTemplateRequest request = new DeleteTemplateRequest();
            request.setTemplateName(templateName);

            return ses.deleteTemplate(request);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }

    }

    @Override
    public SendEmailResult sendEmail(String from, String to, String[] cc, String subject, String plainMessage, String htmlMessage, JSONObject jsonVariables) throws AmazonSimpleEmailServiceException {

        if(REDIRECT_SENDGRID){
            emailService.sendPlainMail(from, null, to, cc, subject, htmlMessage != null ? htmlMessage : plainMessage, null, null, null, null);
            return null;
        }

//        REEMPLAZO DE VARIABLES MANUAL. NO HAY REEMPLAZO DE DATA POR TEMPLATE
        subject = replaceVariables(subject, jsonVariables);
        plainMessage = replaceVariables(plainMessage, jsonVariables);
        htmlMessage = replaceVariables(htmlMessage, jsonVariables);

        Destination destination = new Destination().withToAddresses(Collections.singletonList(Configuration.hostEnvIsProduction() ? to : Configuration.EMAIL_DEV_TO));
        if (cc != null && Configuration.hostEnvIsProduction()) {
            destination.withCcAddresses(cc);
        }

        SendEmailRequest request = new SendEmailRequest();
        request.withDestination(destination);
        request.withMessage(new Message()
                .withSubject(new Content(subject))
                .withBody(new Body().withText(new Content().withData(plainMessage)).withHtml(new Content().withData(htmlMessage)))
        );
        request.withSource(from);

        return ses.sendEmail(request);
    }

    @Override
    public SendRawEmailResult sendRawEmail(Integer personInteractionId, String from, String fromName, String to, String[] cc, String subject, String plainMessage, String htmlMessage, String templateId, List<Attachment> attached, JSONObject jsonVariables, String hourOfDay, Map<String, String> mapTags) throws MessagingException, IOException, AmazonSimpleEmailServiceException {
        return sendRawEmail(personInteractionId, from, fromName, to, cc, subject, plainMessage, htmlMessage, templateId, attached,jsonVariables,hourOfDay,mapTags, null);
    }

    @Override
    public SendRawEmailResult sendRawEmail(Integer personInteractionId, String from, String fromName, String to, String[] cc, String subject, String plainMessage, String htmlMessage, String templateId, List<Attachment> attached, JSONObject jsonVariables, String hourOfDay, Map<String, String> mapTags, Integer personInteractinContentId) throws MessagingException, IOException, AmazonSimpleEmailServiceException {

        if(REDIRECT_SENDGRID){
            emailService.sendHtmlMail(from, fromName, to, cc, subject, htmlMessage != null ? htmlMessage : plainMessage, templateId, attached, null, null, jsonVariables);
            return null;
        }


//        SUBJECT SIEMPRE ES REEMPLAZADO
//        CONTENIDO ES INSERTADO AL TEMPLATE
        if (templateId != null) {

            // Validate that all the variables to replace exists
            if(jsonVariables != null){
                if(JsonUtil.getStringFromJson(jsonVariables, "unsubscribe_display", null) == null){
                    jsonVariables.put("unsubscribe_display", "none");
                }
            }else{
                jsonVariables = new JSONObject();
                jsonVariables.put("unsubscribe_display", "none");
            }

            Template template = getAWSSESTemplate(templateId).getTemplate();
            plainMessage = replaceVariables(template.getTextPart(), jsonVariables);
            htmlMessage = replaceVariables(template.getHtmlPart(), jsonVariables);
        } else {
            if(jsonVariables != null &&  JsonUtil.getStringFromJson(jsonVariables, "body", null) != null){
                plainMessage = replaceVariables(jsonVariables.getString("body"), jsonVariables);
                htmlMessage = replaceVariables(jsonVariables.getString("body"), jsonVariables);
            }
        }

        MimeMessage rawEmail = buildRawEmail(subject, from, fromName, to, cc, plainMessage, htmlMessage, attached);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        rawEmail.writeTo(outputStream);

        RawMessage rawMessage = new RawMessage(ByteBuffer.wrap(outputStream.toByteArray()));
        SendRawEmailRequest request = new SendRawEmailRequest(rawMessage);


        MessageTag personInteractionTag = new MessageTag();
        personInteractionTag.setName("person_interaction_id");
        personInteractionTag.setValue(String.valueOf(personInteractionId));

        MessageTag hostEnvTag = new MessageTag();
        hostEnvTag.setName("host_env");
        hostEnvTag.setValue(Configuration.getEnvironmmentName());

        MessageTag interactionContentTag = new MessageTag();
            interactionContentTag.setName("interaction_content_id");
        interactionContentTag.setValue(String.valueOf(personInteractinContentId));

        List<MessageTag> tags = new ArrayList<MessageTag>();
        tags.add(personInteractionTag);
        tags.add(hostEnvTag);

        if (mapTags != null && mapTags.size() > 0) {
            for (Map.Entry<String, String> entry : mapTags.entrySet()) {
                MessageTag customTag = new MessageTag();
                customTag.setName(entry.getKey());
                customTag.setValue(entry.getValue());
                tags.add(customTag);
            }
        }

        request.withTags(tags);
        request.setConfigurationSetName("mailing-events-conf");
        return ses.sendRawEmail(request);
    }

    private MimeMessage buildRawEmail(String subject, String from, String fromName, String to, String[] toPeople, String plainMessage, String htmlMessage, List<Attachment> attachments) throws MessagingException, IOException {
        Session session = Session.getInstance(new Properties());

        MimeMessage mimeMessage = new MimeMessage(session);

        if (!Configuration.hostEnvIsProduction())
            subject = subject + " " + Configuration.hostEnvName();

        mimeMessage.setSubject(subject, "UTF-8");
        if (fromName == null || "".equals(fromName)) {
            mimeMessage.setFrom(new InternetAddress(from));
        } else {
            mimeMessage.setFrom(new InternetAddress(String.format("%s <%s>", fromName, from)));
        }

        mimeMessage.setRecipients(javax.mail.Message.RecipientType.TO, InternetAddress.parse(Configuration.hostEnvIsProduction() ? to : Configuration.EMAIL_DEV_TO));

        if (toPeople != null && toPeople.length > 0 && Configuration.hostEnvIsProduction()) {
            InternetAddress[] ccArray = new InternetAddress[toPeople.length];
            for (int i=0; i<toPeople.length; i++) {
                ccArray[i] = new InternetAddress(toPeople[i]);
            }

            mimeMessage.setRecipients(javax.mail.Message.RecipientType.CC, ccArray);
        }

        MimeMultipart messageBody = new MimeMultipart("alternative");
        MimeBodyPart wrap = new MimeBodyPart();
        MimeMultipart msg = new MimeMultipart("mixed");

        MimeBodyPart textPart = new MimeBodyPart();
        textPart.setContent(plainMessage, "text/plain; charset=UTF-8");
        messageBody.addBodyPart(textPart);

        MimeBodyPart htmlPart = new MimeBodyPart();
        htmlPart.setContent(htmlMessage, "text/html; charset=UTF-8");
        messageBody.addBodyPart(htmlPart);

        wrap.setContent(messageBody);
        msg.addBodyPart(wrap);

        TikaConfig config = TikaConfig.getDefaultConfig();
        Detector detector = config.getDetector();

        if (attachments != null) {
            for (Attachment attachment : attachments) {
                byte[] attach = Base64.getDecoder().decode(attachment.getContent());
                TikaInputStream stream = TikaInputStream.get(attach);

                Metadata metadata = new Metadata();
                metadata.add(Metadata.RESOURCE_NAME_KEY, attachment.getFilename());

                MimeBodyPart att = new MimeBodyPart();
                DataSource source = new ByteArrayDataSource(attach, detector.detect(stream, metadata).toString());
                att.setDataHandler(new DataHandler(source));
                att.setFileName(attachment.getFilename());
                msg.addBodyPart(att);
            }
        }

        mimeMessage.setContent(msg);

        return mimeMessage;
    }

    private String replaceVariables(String message, JSONObject jsonReplace) {
        if (message == null) {
            return null;
        }
        if (jsonReplace == null) {
            return message;
        }

        Iterator<?> keys = jsonReplace.keys();
        while (keys.hasNext()) {
            String key = (String) keys.next();
//            SENDGRID VARIABLES
            message = message.replaceAll("%" + key.toLowerCase() + "%", jsonReplace.getString(key));
            message = message.replaceAll("%" + key.toUpperCase() + "%", jsonReplace.getString(key));
            message = message.replaceAll("%" + key.trim() + "%", jsonReplace.getString(key));
//            AWS SES VARIABLES
            message = message.replaceAll("(\\{\\{ |\\{\\{)" + key.toLowerCase() + "( \\}\\}|\\}\\})", jsonReplace.getString(key));
            message = message.replaceAll("(\\{\\{ |\\{\\{)" + key.toUpperCase() + "( \\}\\}|\\}\\})", jsonReplace.getString(key));
        }
        return message;
    }

    @Override
    public String getVerificationEmailStatus(String email) {
        GetIdentityVerificationAttributesRequest request = new GetIdentityVerificationAttributesRequest().withIdentities(email);
        GetIdentityVerificationAttributesResult result = ses.getIdentityVerificationAttributes(request);
        if (result.getVerificationAttributes() != null && result.getVerificationAttributes().containsKey(email)) {
            IdentityVerificationAttributes attributes = result.getVerificationAttributes().get(email);
            return  attributes.getVerificationStatus();
        }
        return null;
    }

    @Override
    public void sendEmailVerification(String emailToVerify) {
        VerifyEmailIdentityRequest verifyEmailIdentityRequest = new VerifyEmailIdentityRequest();
        verifyEmailIdentityRequest.setEmailAddress(emailToVerify);
        VerifyEmailIdentityResult result = ses.verifyEmailIdentity(verifyEmailIdentityRequest);
        if (result.getSdkHttpMetadata() == null || result.getSdkHttpMetadata().getHttpStatusCode() != 200) {
            throw new RuntimeException("Error to send verification email");
        }
    }

}
