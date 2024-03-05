package com.affirm.common.service.impl;

import com.affirm.common.dao.*;
import com.affirm.common.model.SenderMailConfiguration;
import com.affirm.common.model.catalog.*;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.EmailService;
import com.affirm.common.service.FileService;
import com.affirm.common.service.InteractionService;
import com.affirm.common.util.CryptoUtil;
import com.affirm.common.util.JsonUtil;
import com.affirm.intico.model.InticoSendSmsResponse;
import com.affirm.intico.service.InticoClientService;
import com.affirm.system.configuration.Configuration;
import com.affirm.wavy.model.Destination;
import com.affirm.wavy.model.HSM;
import com.affirm.wavy.model.HSMMessage;
import com.affirm.wavy.service.WavyService;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.infobip.ApiClient;
import com.infobip.ApiException;
import com.infobip.api.SendSmsApi;
import com.infobip.model.SmsAdvancedTextualRequest;
import com.infobip.model.SmsDestination;
import com.infobip.model.SmsResponse;
import com.infobip.model.SmsTextualMessage;
import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.resource.factory.CallFactory;
import com.twilio.sdk.resource.factory.MessageFactory;
import com.twilio.sdk.resource.instance.Account;
import com.twilio.sdk.resource.instance.Call;
import com.twilio.sdk.resource.instance.Message;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author jrodriguez
 * <p>
 * SmsService adapted for twilio
 */
@Service
public class InteractionServiceImpl implements InteractionService {

    private static Logger logger = Logger.getLogger(InteractionServiceImpl.class);
    private static final String DEFAULT_PHONE_NUMBER = "+51976364877";

    @Autowired
    private InteractionDAO interactionDao;
    @Autowired
    private FileService fileService;
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private EmailService emailService;
    @Autowired
    private AwsSesEmailServiceImpl awsSesEmailService;
    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private CreditDAO creditDao;
    @Autowired
    private PersonDAO personDAO;
    @Autowired
    private WavyService wavyService;
    @Autowired
    private InticoClientService inticoClientService;

    @Override
    public void sendPersonInteraction(PersonInteraction interaction, JSONObject jsonVars, Map<String, String> tags) throws Exception {
        sendPersonInteraction(interaction, jsonVars, tags, null);
    }

    @Override
    public void sendPersonInteraction(PersonInteraction interaction, JSONObject jsonVars, Map<String, String> tags, Map<String, String> templateVars) throws Exception {
        // Prepare body and subject
        /*interaction.setSubject(replaceVariables(interaction.getInteractionContent().getSubject(), jsonVars));
        interaction.setBody(replaceVariables(interaction.getInteractionContent().getBody(), jsonVars));*/

        boolean sendMessage = true;

        if (interaction.getInteractionType().getId() == InteractionType.MAIL) {

            String templateToUse = interaction.getInteractionContent().getTemplate() != null ? ""+interaction.getInteractionContent().getTemplate() : null;
            String entityShortToUse = catalogService.getEntity(Entity.AFFIRM).getShortName();
            String buttonColorToUse = Configuration.DEFAULT_BUTTON_COLOR;

            if (interaction.getLoanApplicationId() != null || interaction.getCreditId() != null) {

                LoanApplication loanApplication = null;
                if (interaction.getLoanApplicationId() != null) {
                    loanApplication = loanApplicationDao.getLoanApplication(interaction.getLoanApplicationId(), Configuration.getDefaultLocale());
                } else if (interaction.getCreditId() != null) {
                    Credit credit = creditDao.getCreditByID(interaction.getCreditId(), Configuration.getDefaultLocale(), false, Credit.class);
                    loanApplication = loanApplicationDao.getLoanApplication(credit.getLoanApplicationId(), Configuration.getDefaultLocale());
                }

                if (loanApplication.getEntityUserId() != null) {
                    UserEntity userEntity = userDAO.getUserEntityById(loanApplication.getEntityUserId(), Configuration.getDefaultLocale());
                    if (userEntity != null && userEntity.getEmail() != null)
                        interaction.addCcMail(userEntity.getEmail());
                }

                // Set the credit id if the loan is credit
                if (loanApplication.getCreditId() != null)
                    interaction.setCreditId(loanApplication.getCreditId());

                // Set the branded configurations
                if (loanApplication.getEntityId() != null) {
                    EntityBranding entityBranding = catalogService.getEntityBranding(loanApplication.getEntityId());
                    if (entityBranding != null && entityBranding.getBranded() != null && entityBranding.getBranded()) {
                        EntityBranding entityBrandingToUse = entityBranding;
                        if(interaction.getInteractionContent() == null || (interaction.getInteractionContent() != null && !Arrays.asList(InteractionContent.ALFIN_BANTOTAL_COBRANZA_RECOVERY_PAGO_DE_CUOTA).contains(interaction.getInteractionContent().getId()))) templateToUse = entityBrandingToUse.getTemplate();
                        if(entityBranding.getEntity().getId().equals(Entity.BANBIF) && interaction.getInteractionContent() != null && interaction.getInteractionContent().getCategory() != null && interaction.getInteractionContent().getCategory().equalsIgnoreCase("Commercial")) templateToUse = interaction.getInteractionContent().getTemplate();
                        entityShortToUse = entityBrandingToUse.getEntity().getShortName();
                        buttonColorToUse = Arrays.asList(Entity.COMPARTAMOS, Entity.ACCESO).contains(entityBrandingToUse.getEntity().getId()) ? entityBrandingToUse.getEntitySecundaryColor() : entityBrandingToUse.getEntityPrimaryColor();

                        if (loanApplication.getCreditId() != null && loanApplication.getSelectedEntityProductParameterId() != null) {
                            EntityProductParams entityProductParamSelected = catalogService.getEntityProductParamById(loanApplication.getSelectedEntityProductParameterId());
                            if (entityProductParamSelected != null && entityProductParamSelected.getTemplate() != null)
                                templateToUse = entityProductParamSelected.getTemplate();
                        }
                    }
                }

                modifyInteractionContent(interaction, loanApplication);
            }
            if(interaction.getInteractionContent().getId() == InteractionContent.AZTECA_CUENTA_SOLICITUD_APROBADA){
                templateToUse = interaction.getInteractionContent().getTemplate();
            }

            interaction.getInteractionContent().setTemplate(templateToUse);
            if (jsonVars == null)
                jsonVars = new JSONObject();

            if (JsonUtil.getStringFromJson(jsonVars, "ENTITY_SHORT", null) == null) {
                jsonVars.put("ENTITY_SHORT", entityShortToUse);
            }
            jsonVars.put("BUTTON_COLOR", buttonColorToUse);
        }

        // replace the variables with values in the json
        if (interaction.getInteractionType().getId() == InteractionType.MAIL) {
            interaction.setSubject(replaceVariables(interaction.getSubject() != null ? interaction.getSubject() : interaction.getInteractionContent().getSubject(), jsonVars));
        }
        interaction.setBody(replaceVariables(interaction.getBody() != null ? interaction.getBody() : interaction.getInteractionContent().getBody(), jsonVars));

        if (interaction.getAutomatic() == null || !interaction.getAutomatic()) {
            Integer personInteractionId = interactionDao.insertPersonInteraction(interaction, interaction.getQueryBotId());
            interaction.setId(personInteractionId);
        }

        if (interaction.getInteractionType().getId() == InteractionType.MAIL && sendMessage) {

            if (interaction.getInteractionProvider() != null) {

                switch (interaction.getInteractionProvider().getId()) {
                    case InteractionProvider.AWS:
                        sendEmailAWS(interaction, tags, templateVars);
                        break;
                    case InteractionProvider.SENGRID:
                        sedEmailSendgrid(interaction, templateVars);
                        break;
                    default:
                        sendEmailAWS(interaction, tags, templateVars);
                        break;
                }

            } else {
                sendEmailAWS(interaction, tags, templateVars);
                interaction.setInteractionProvider(catalogService.getInteractionProvider(InteractionProvider.AWS));
            }

            interactionDao.updateInteraction(interaction);
        } else if (interaction.getInteractionType().getId() == InteractionType.SMS) {

            if (interaction.getInteractionProvider() != null) {

                switch (interaction.getInteractionProvider().getId()) {
                    case InteractionProvider.AWS:
                        sendSmsAWS(interaction);
                        break;
                    case InteractionProvider.TWILIO:
                        sendSmsTwilio(interaction);
                        break;
                    case InteractionProvider.INTICO:
                        sendSmsIntico(interaction);
                        break;
                    case InteractionProvider.INFOBIP:
                        sendSmsInfobip(interaction);
                        break;
                    default:
                        sendSmsInfobip(interaction);
                        break;
                }

            } else {
                sendSmsAWS(interaction);
            }

        } else if (interaction.getInteractionType().getId() == InteractionType.CALL) {
            makeCallTwilio(interaction);
        } else if (interaction.getInteractionType().getId() == InteractionType.CHAT) {
            sendWhatsappTemplateMessage(interaction, templateVars);
        }

        if(sendMessage){
            if (interaction.getSent()) {
                interactionDao.updateSentPersonInteraction(interaction.getId());
                if(interaction.getProviderData() != null) interactionDao.updateProviderDataPersonInteraction(interaction.getId(), interaction.getProviderData());
            }
        }
    }

    @Override
    public void resendPersonInteraction(PersonInteraction interaction) throws Exception {
        // Reset the object
        interaction.setId(null);
        interaction.setRegisterDate(null);
        interaction.setSent(null);

        sendPersonInteraction(interaction, null, null);
    }

    @Override
    public Integer registerCallRequestPersonInteraction(Integer loanApplicationId, String countryCode, String phoneNumber) throws Exception {
        return interactionDao.insertCallRequestInteraction(loanApplicationId, countryCode, phoneNumber);
    }

    @Override
    public List<VerificationCallRequest> getVerificationCallRequest(int loanApplicationId, String countryCode, String phoneNumber) throws Exception {
        return interactionDao.getVerificationCallRequestInteraction(loanApplicationId, countryCode, phoneNumber);
    }

    @Override
    public void registerUnsubscription(String encryptedEmail) throws Exception {
        String email = CryptoUtil.decrypt(encryptedEmail);
        personDAO.registerEmailToBlacklist(email);
    }

    private void sendSmsTwilio(PersonInteraction interaction) {
        try {
            if (!Configuration.hostEnvIsProduction()) {
                interaction.setSent(true);
                return;
            }
            TwilioRestClient client = new TwilioRestClient(Configuration.TWILIO_ACCOUNT_SID, Configuration.TWILIO_AUTH_TOKEN);
            Account account = client.getAccount();
            MessageFactory messageFactory = account.getMessageFactory();
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("To", interaction.getDestination()));
            params.add(new BasicNameValuePair("From", Configuration.TWILIO_FROM_NUMBER));
            //params.add(new BasicNameValuePair("Body", interaction.getInteractionContent().getBody()));
            params.add(new BasicNameValuePair("Body", interaction.getBody()));
            params.add(new BasicNameValuePair("StatusCallback", Configuration.TWILIO_WEBHOOK() + interaction.getId()));
            Message sms = messageFactory.create(params);

            interaction.setSent(true);
        } catch (Exception e) {
            logger.error("Error sending sms", e);
            interaction.setSent(false);
        }
    }

    private void sendSmsInfobip(PersonInteraction interaction) {
        try {
            String urlWebHook = String.format("https://solven.pe/webhook/infobip/%s", interaction.getId());
            if (!Configuration.hostEnvIsProduction()) {
                urlWebHook = String.format("https://stg.solven.pe/webhook/infobip/%s", interaction.getId());
            }

            if (!Configuration.IS_AUTHORIZED_PHONENUMBER(interaction.getDestination())) {
                interaction.setSent(true);
                return;
            }
            ApiClient apiClient = new ApiClient();
            apiClient.setApiKeyPrefix(Configuration.INFOBIP_API_KEY_PREFIX);
            apiClient.setApiKey(Configuration.INFOBIP_API_KEY);
            apiClient.setBasePath(Configuration.INFOBIP_BASE_URL);

            com.infobip.Configuration.setDefaultApiClient(apiClient);

            SendSmsApi sendSmsApi = new SendSmsApi();
            SmsTextualMessage smsMessage = new SmsTextualMessage()
                    .from("InfoSMS")
                    .addDestinationsItem(new SmsDestination().to(interaction.getDestination()))
                    .text(interaction.getBody())
                    .notifyUrl(urlWebHook);

            SmsAdvancedTextualRequest smsMessageRequest = new SmsAdvancedTextualRequest().messages(
                    Collections.singletonList(smsMessage)
            );

            SmsResponse smsResponse = sendSmsApi.sendSmsMessage(smsMessageRequest);
            System.out.println("Response body: " + smsResponse);
            interaction.setSent(true);
            } catch (ApiException e) {
                logger.error("HTTP status code: " + e.getCode());
                logger.error("Error sending Response body: " + e.getResponseBody());
                interaction.setSent(false);
            } catch (Exception e) {
                logger.error("Error sending sms", e);
                interaction.setSent(false);
            }
    }

    private void sendSmsIntico(PersonInteraction interaction) {
        try {
            if (!Configuration.IS_AUTHORIZED_PHONENUMBER_INTICO(interaction.getDestination())) {
                interaction.setSent(true);
                return;
            }
            InticoSendSmsResponse inticoSendSmsResponse = inticoClientService.sendInticoSms(interaction.getDestination(), interaction.getBody());
            if(interaction.getProviderData() == null) interaction.setProviderData(new JSONObject());
            if(inticoSendSmsResponse != null && inticoSendSmsResponse.getCodigo() != null) interaction.getProviderData().put("inticoSmsCode",inticoSendSmsResponse.getCodigo());
            interaction.setSent(true);
        } catch (Exception ex) {
            logger.error("ERROR sending AWS sms", ex);
            ex.printStackTrace();

            interaction.setSent(false);
        }
    }

    private void sendSmsAWS(PersonInteraction interaction) {
        try {
            if (!Configuration.IS_AUTHORIZED_PHONENUMBER(interaction.getDestination())) {
                interaction.setSent(true);
                return;
            }
            Map<String, MessageAttributeValue> smsAttributes = new HashMap<>();
            smsAttributes.put("AWS.SNS.SMS.SenderID", new MessageAttributeValue()
                    .withStringValue("mySenderID") //The sender ID shown on the device.
                    .withDataType("String"));
            smsAttributes.put("AWS.SNS.SMS.MaxPrice", new MessageAttributeValue()
                    .withStringValue("0.50") //Sets the max price to 0.50 USD.
                    .withDataType("Number"));
            smsAttributes.put("AWS.SNS.SMS.SMSType", new MessageAttributeValue()
                    .withStringValue("Transactional") //Sets the type to promotional.
                    .withDataType("String"));

            AmazonSNSClientBuilder builder = AmazonSNSClientBuilder.standard();
            builder.setCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(System.getenv("AWS_ACCESS_KEY_ID"), System.getenv("AWS_SECRET_ACCESS_KEY"))));
            builder.setRegion("us-west-2");
            AmazonSNS snsClient = builder.build();

            PublishResult result = snsClient.publish(new PublishRequest()
                    .withMessage(interaction.getBody())
                    .withPhoneNumber(Configuration.hostEnvIsProduction() ? interaction.getDestination() : DEFAULT_PHONE_NUMBER)
                    .withMessageAttributes(smsAttributes));

            interaction.setSent(true);
        } catch (Exception ex) {
            logger.error("ERROR sending AWS sms", ex);
            ex.printStackTrace();

            interaction.setSent(false);
        }
    }

    private void makeCallTwilio(PersonInteraction interaction) {
        try {
            if (!Configuration.hostEnvIsProduction()) {
                interaction.setSent(true);
                return;
            }
            TwilioRestClient client = new TwilioRestClient(Configuration.TWILIO_ACCOUNT_SID, Configuration.TWILIO_AUTH_TOKEN);
            Account account = client.getAccount();
            CallFactory callFactory = account.getCallFactory();

            String XMLActionsURL = String.format("%s/%s/twilio/voicecall/%s",
                    Configuration.getClientDomain(),
                    Configuration.WEBHOOK_PATH,
                    CryptoUtil.encrypt(interaction.getId().toString()));

            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("To", Configuration.hostEnvIsProduction() ? interaction.getDestination() : DEFAULT_PHONE_NUMBER));
            params.add(new BasicNameValuePair("From", Configuration.TWILIO_FROM_NUMBER));
            params.add(new BasicNameValuePair("Url", XMLActionsURL));
            params.add(new BasicNameValuePair("Method", "POST"));
            params.add(new BasicNameValuePair("StatusCallback", Configuration.TWILIO_WEBHOOK() + interaction.getId()));
            Call call = callFactory.create(params);

            interaction.setSent(true);
        } catch (Exception ex) {
            logger.error("Error sending twilio call", ex);
            interaction.setSent(false);
        }
    }

    private void sedEmailSendgrid(PersonInteraction interaction, Map<String, String> templateVars) {
        try {
            //Prepare Attachments
            List<Attachment> attached = new ArrayList<>();
            if (interaction.getAttachments() != null) {
                for (PersonInteractionAttachment att : interaction.getAttachments()) {
                    Attachment attachment = new Attachment();
                    attachment.setFilename(att.getUserFileId() != null ? userDAO.getUserFile(att.getUserFileId()).getFileName() : att.getFilename());
                    if (att.getBytes() != null) {
                        attachment.setContent(new Base64().encodeAsString(att.getBytes()));
                    } else {
                        attachment.setContent(new Base64().encodeAsString(fileService.getUserFileById(att.getUserFileId(), false)));
                    }
                    attached.add(attachment);
                }
            }

            // Prepare the email
            JSONObject jsonVariables = new JSONObject();
            jsonVariables.put("body", interaction.getBody());
            boolean shouldShowUnsubscribe = false;
            if (interaction.getInteractionContent().getCategory() != null) {
                shouldShowUnsubscribe = Arrays.asList(
                        "commercial".toLowerCase(),
                        "Commercial Self Evaluation".toLowerCase()
                ).contains(interaction.getInteractionContent().getCategory().toLowerCase());
            }

            jsonVariables.put("unsubscribe_display", shouldShowUnsubscribe ? "block" : "none");
            if (shouldShowUnsubscribe && interaction.getPersonId() != null) {
                jsonVariables.put("unsubscribe_link", Configuration.getClientDomain() + "/fup/unsubscribe/" + CryptoUtil.encrypt(interaction.getDestination()));
            }
            if (templateVars != null) {
                for (Map.Entry<String, String> entry : templateVars.entrySet()) {
                    jsonVariables.put(entry.getKey(), entry.getValue());
                }
            }

            //sendMail
            emailService.sendHtmlMail(
                    interaction.getInteractionContent().getFromMail(),
                    interaction.getSenderName() == null ? Configuration.DEFAULT_FROM_NAME : interaction.getSenderName(),
                    Configuration.hostEnvIsProduction() ? interaction.getDestination() : Configuration.EMAIL_DEV_TO,
                    Configuration.hostEnvIsProduction() ? interaction.getCcMails() : null,
                    interaction.getSubject(),
                    interaction.getBody(),
                    interaction.getInteractionContent().getTemplate(),
                    attached,
                    null,
                    null,
                    jsonVariables
            );
            interaction.setSent(true);
        } catch (Exception e) {
            logger.error("Error sending email", e);
            interaction.setSent(false);
        }
    }

    private void sendEmailAWS(PersonInteraction interaction, Map<String, String> mapTags, Map<String, String> templateVars) {
        try {
            List<Attachment> attached = new ArrayList<>();
            if (interaction.getAttachments() != null) {
                for (PersonInteractionAttachment att : interaction.getAttachments()) {
                    Attachment attachment = new Attachment();
                    attachment.setFilename(att.getUserFileId() != null ? userDAO.getUserFile(att.getUserFileId()).getFileName() : att.getFilename());
                    if (att.getBytes() != null) {
                        attachment.setContent(new Base64().encodeAsString(att.getBytes()));
                    } else {
                        attachment.setContent(new Base64().encodeAsString(fileService.getUserFileById(att.getUserFileId(), false)));
                    }
                    attached.add(attachment);
                }
            }

            JSONObject jsonVariables = new JSONObject();
            jsonVariables.put("body", interaction.getBody());
            boolean shouldShowUnsubscribe = false;
            if (interaction.getInteractionContent().getCategory() != null) {
                shouldShowUnsubscribe = Arrays.asList(
                        "commercial".toLowerCase(),
                        "Commercial Self Evaluation".toLowerCase()
                ).contains(interaction.getInteractionContent().getCategory().toLowerCase());
            }

            jsonVariables.put("unsubscribe_display", shouldShowUnsubscribe ? "block" : "none");
            if (interaction.getPersonId() != null) {
                jsonVariables.put("unsubscribe_link", Configuration.getClientDomain() + "/fup/unsubscribe/" + CryptoUtil.encrypt(interaction.getDestination()));
            }
            if (templateVars != null) {
                for (Map.Entry<String, String> entry : templateVars.entrySet()) {
                    jsonVariables.put(entry.getKey(), entry.getValue());
                }
            }

            awsSesEmailService.sendRawEmail(
                    interaction.getId(),
                    interaction.getInteractionContent().getFromMail(),
                    interaction.getSenderName() == null ? Configuration.DEFAULT_FROM_NAME : interaction.getSenderName(),
                    Configuration.hostEnvIsProduction() ? interaction.getDestination() : Configuration.EMAIL_DEV_TO,
                    Configuration.hostEnvIsProduction() ? interaction.getCcMails() : null,
                    interaction.getSubject(),
                    null,
                    null,
                    interaction.getInteractionContent().getTemplate(),
                    attached,
                    jsonVariables,
                    interaction.getInteractionContent().getHourOfDay(),
                    mapTags,
                    interaction.getInteractionContent() != null ? interaction.getInteractionContent().getId() : null
            );
            interaction.setSent(true);
        } catch (Exception e) {
            logger.error("Error sending email", e);
            interaction.setSent(false);
        }
    }

    private void sendWhatsappTemplateMessage(PersonInteraction personInteraction, Map<String, String> templateVars) throws Exception {
        if (!Configuration.IS_AUTHORIZED_PHONENUMBER(personInteraction.getDestination())) {
            personInteraction.setSent(true);
            return;
        }

        List<Destination> destinations = new ArrayList<>();

        Destination destination = new Destination();
        destination.setCorrelationId(personInteraction.getId() != null ? personInteraction.getId().toString() : null);
        destination.setDestination(personInteraction.getDestination());
        destinations.add(destination);

        String additionalInfo = new JSONObject()
                .put("loanApplicationId", personInteraction.getLoanApplicationId())
                .put("creditId", personInteraction.getCreditId())
                .put("personInteractionId", personInteraction.getId()).toString();

        try {
            if (templateVars != null && !templateVars.isEmpty()) {
                HSM hsm = new HSM();
                hsm.setElementName(personInteraction.getInteractionContent().getTemplate());
                hsm.setLanguageCode("es");

                List<String> params = new ArrayList<>();
                for (String key : templateVars.keySet()) {
                    params.add(templateVars.get(key));
                }
                hsm.setParameters(params);

                HSMMessage hsmMessage = new HSMMessage();
                hsmMessage.setHsm(hsm);

                wavyService.sendHSMMessage(destinations, hsmMessage, null, additionalInfo);
            } else {
                com.affirm.wavy.model.Message message = new com.affirm.wavy.model.Message();
                message.setMessageText(personInteraction.getBody());

                wavyService.sendTextMessage(destinations, message, null, additionalInfo);
            }

            personInteraction.setSent(true);
        } catch (Exception e) {
            logger.error("Error sending chat message", e);
            personInteraction.setSent(false);
        }
    }

    /**
     * Replace the message vars eith the values in the json
     *
     * @param message
     * @param jsonReplace with the format {VAR_TO_REPLACE = value} {CLIENT_NAME = "John Rodriguez", AGENT_NAME = "John Smith"}
     * @return
     */
    private static String replaceVariables(String message, JSONObject jsonReplace) {
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
            message = message.replaceAll("%" + key + "%", jsonReplace.getString(key));
//            AWS SES VARIABLES
            message = message.replaceAll("(\\{\\{ |\\{\\{)" + key + "( \\}\\}|\\}\\})", jsonReplace.getString(key));
        }
        return message;
    }


    private String joinBodyWithTemplate(String body) {
        return null;

    }

    @Override
    public void modifyInteractionContent(PersonInteraction personInteraction, LoanApplication loanApplication){
        if(loanApplication == null) return;
        SenderMailConfiguration senderMailConfiguration = SenderMailConfiguration.getSenderMailConfiguration(catalogService, loanApplication, personInteraction.getInteractionContent() != null ? personInteraction.getInteractionContent().getId() : null);
        if(senderMailConfiguration != null){
            if(senderMailConfiguration.getName() != null && !senderMailConfiguration.getName().isEmpty()) personInteraction.setSenderName(senderMailConfiguration.getName());
            if(senderMailConfiguration.getEmail() != null && !senderMailConfiguration.getEmail().isEmpty()) personInteraction.getInteractionContent().setFromMail(senderMailConfiguration.getEmail());
        }
    }

}
