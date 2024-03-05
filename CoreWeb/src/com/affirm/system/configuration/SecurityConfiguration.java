package com.affirm.system.configuration;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by jarmando on 16/11/16.
 */
public class SecurityConfiguration {

    public enum Attack {
        CSRF("A possible CSRF attack has been avoided.","The request has made a conflict with the server resources. Your session has been invalidated."),
        MIM("A possible MIM attack has been avoided.",""),
        XSS("A possible XSS attack has been avoided.","");

        String message;
        String userMessage;

        Attack(String message, String userMessage){
            this.message = message;
            this.userMessage = userMessage;
        }

        public String getMessage() {
            return message;
        }

        public String getUserMessage() {
            return userMessage;
        }
    }
}