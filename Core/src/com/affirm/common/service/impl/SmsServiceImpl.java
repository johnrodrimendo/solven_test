/**
 *
 */
package com.affirm.common.service.impl;


import com.affirm.common.service.SmsService;
import com.affirm.system.configuration.Configuration;
import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.resource.factory.MessageFactory;
import com.twilio.sdk.resource.instance.Account;
import com.twilio.sdk.resource.instance.Message;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jrodriguez
 */
@Service("smsService")
public class SmsServiceImpl implements SmsService {

    @Override
    public void sendSms(String to, String message, Integer interactionId) throws Exception {
        if (!Configuration.hostEnvIsProduction())
            return;

        TwilioRestClient client = new TwilioRestClient(Configuration.TWILIO_ACCOUNT_SID, Configuration.TWILIO_AUTH_TOKEN);
        Account account = client.getAccount();
        MessageFactory messageFactory = account.getMessageFactory();
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("To", to));
        params.add(new BasicNameValuePair("From", Configuration.TWILIO_FROM_NUMBER));
        params.add(new BasicNameValuePair("Body", message));
        if (interactionId != null)
            params.add(new BasicNameValuePair("StatusCallback", Configuration.TWILIO_WEBHOOK() + interactionId));
        Message sms = messageFactory.create(params);
    }

}
