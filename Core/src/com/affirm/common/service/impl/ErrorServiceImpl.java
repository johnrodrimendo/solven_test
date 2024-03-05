/**
 *
 */
package com.affirm.common.service.impl;


import com.affirm.common.model.SlackMessage;
import com.affirm.common.service.AwsSesEmailService;
import com.affirm.common.service.ErrorService;
import com.affirm.security.dao.SecurityDAO;
import com.affirm.system.configuration.Configuration;
import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.resource.factory.MessageFactory;
import com.twilio.sdk.resource.instance.Account;
import com.twilio.sdk.resource.instance.Message;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.postgresql.util.PSQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author jrodriguez
 */
@Service("errorService")
public class ErrorServiceImpl implements ErrorService {

    private static final Logger logger = Logger.getLogger(ErrorServiceImpl.class);


    @Autowired
    private AwsSesEmailService awsSesEmailService;
    @Autowired
    private SecurityDAO securityDao;

    private static ErrorService errorServiceStatic;
    private ExecutorService executor = Executors.newFixedThreadPool(20);
    private long lastErrorSmsSent;

    @PostConstruct
    public void init() {
        ErrorServiceImpl.errorServiceStatic = this;
    }

    @Override
    public void onError(Throwable throwable) {
        String appName = System.getProperty("application");
        try {
            // Print log
            logger.error("Error", throwable);

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            throwable.printStackTrace(pw);

            // Blacklist of errors
            List<String> blacklistErrors = new ArrayList<>();
            blacklistErrors.add("org.openqa.selenium");
            blacklistErrors.add("java.lang.IllegalStateException: org.apache.shiro.session.UnknownSessionException: There is no session with id");
            boolean isBlackListed = blacklistErrors.stream().anyMatch(b -> sw.toString().contains(b));

            if(!isBlackListed){
                // Send email
                String errorType = throwable instanceof PSQLException ? "BD" : "APP";
                if (Configuration.hostEnvIsProduction()) {
                    sendErrorMail(appName, sw.toString(), errorType);
                    sendErrorSms(errorType);
                }

                // Send slack
                try{
                    sendGeneralErrorSlack(sw.toString());
                }catch (Exception ex){
                    logger.error("Cant send error t slack. This is bad!");
                }
            }

            //Register Exception on DB
            try {
                securityDao.registerException("Error en applicacion " + appName + "\n" + sw.toString());
            } catch (Throwable e) {
                logger.error("Cant connect to DB to register the exception. This is bad!");
            }

        } catch (Exception ex) {
            logger.error("Error on onError action", ex);
        }
    }

    public static void onErrorStatic(Throwable throwable) {
        errorServiceStatic.onError(throwable);
    }

    private void sendErrorMail(String appName, String exception, String errorType) throws Exception {
        awsSesEmailService.sendEmail(
                "error@solven.pe",
                Configuration.EMAIL_ERROR_TO(),
                null,
                "Error in system [" + appName + "] type: [" + errorType + "]",
                exception,
                null,
                null
        );
    }

    private void sendErrorSms(String errorType) throws Exception {
        // Onnly sent sms if the last error was sent more than 30 sec
        if (false && new Date().getTime() - lastErrorSmsSent > 10 * 60 * 1000) {
            lastErrorSmsSent = new Date().getTime();

            String[] tos = Configuration.SMS_ERROR_TO();
            if (tos == null) {
                return;
            }
            for (int i = 0; i < tos.length; i++) {
                final String number = tos[i];
                executor.execute(() -> {
                    try {
                        TwilioRestClient client = new TwilioRestClient(Configuration.TWILIO_ACCOUNT_SID, Configuration.TWILIO_AUTH_TOKEN);
                        Account account = client.getAccount();
                        MessageFactory messageFactory = account.getMessageFactory();
                        List<NameValuePair> params = new ArrayList<>();
                        System.out.println("Enviando a : " + number);
                        params.add(new BasicNameValuePair("To", number));
                        params.add(new BasicNameValuePair("From", Configuration.TWILIO_FROM_NUMBER));
                        params.add(new BasicNameValuePair("Body", "Error no controlado en Solven! - [" + errorType + "] - " + new SimpleDateFormat(Configuration.BACKOFFICE_FRONT_SHORT_DATE_FORMAT).format(new Date())));
                        Message sms = messageFactory.create(params);
                    } catch (Exception ex) {
                        logger.error("Error sending twilio error sms", ex);
                    }
                });
            }
        }
    }

    @Override
    public void sendErrorCriticSlack(String message){
        sendMessageToSlackChannel(message, Configuration.hostEnvIsProduction() ? SlackMessage.PRD_WEBHOOK_ERROR_CRITIC_URL : SlackMessage.STG_WEBHOOK_ERROR_CRITIC_URL);
    }

    @Override
    public void sendErrorSlack(String message){
        sendMessageToSlackChannel(message, Configuration.hostEnvIsProduction() ? SlackMessage.PRD_WEBHOOK_URL : SlackMessage.STG_WEBHOOK_URL);
    }

    @Override
    public void sendGeneralErrorSlack(String message){
        if (Configuration.hostEnvIsProduction()) {
            sendMessageToSlackChannel(message, SlackMessage.PRD_WEBHOOK_ERROR_GENERAL_URL);
        }else{
            sendMessageToSlackChannel(message, SlackMessage.STG_WEBHOOK_ERROR_GENERAL_URL);
        }
    }

    private void sendMessageToSlackChannel(String message, String webhookUrl){
        MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType);
        SlackMessage slackMessage = new SlackMessage();
        slackMessage.setText(message);
        HttpEntity<SlackMessage> request = new HttpEntity<>(slackMessage, headers);
        RestTemplate restTemplate = new RestTemplate();
        try {
            String response = restTemplate.postForObject(webhookUrl, request, String.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
