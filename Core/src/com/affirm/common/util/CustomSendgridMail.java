package com.affirm.common.util;

import com.sendgrid.Content;
import com.sendgrid.Email;
import com.sendgrid.Mail;

/**
 * Created by john on 08/03/17.
 */
public class CustomSendgridMail extends Mail {

    public CustomSendgridMail() {
    }

    public CustomSendgridMail(Email from, String subject, Email to, Content content) {
        super(from, subject, to, content);
    }

    public void setCC(String[] cc) {
        if (personalization != null && !personalization.isEmpty()) {
            for (int i = 0; i < cc.length; i++){
                Email ecc = new Email(cc[i]);
                personalization.get(0).addCc(ecc);
            }
        }
    }

}
