/**
 *
 */
package com.affirm.client.model.form;

import com.affirm.common.util.*;
import com.affirm.system.configuration.Configuration;

import java.io.Serializable;

/**
 * @author jrodriguez
 */
public class LoanApplicationBlock4Section2Form extends FormGeneric {

    private Character emailProvider;
    private String authCode;
//    private String email;

    public LoanApplicationBlock4Section2Form() {
        this.setValidator(new LoanApplicationBlock4Section2FormValidator());
    }

    public class LoanApplicationBlock4Section2FormValidator extends FormValidator implements Serializable {

        public CharFieldValidator emailProvider;
        public StringFieldValidator authCode;
//        public StringFieldValidator email;

        public LoanApplicationBlock4Section2FormValidator() {
            addValidator(emailProvider = new CharFieldValidator(ValidatorUtil.NETWORK_PROVIDER));
            addValidator(authCode = new StringFieldValidator(ValidatorUtil.OAUTH_CODE));
//            addValidator(email = new StringFieldValidator(ValidatorUtil.EMAIL));
        }

        @Override
        protected void setDynamicValidations() {
        }

        @Override
        protected Object getSubclass() {
            return this;
        }

        @Override
        protected Object getFormClass() {
            return LoanApplicationBlock4Section2Form.this;
        }
    }

    public Configuration.OauthNetwork getOauthNetworkByProvider() {
        switch (emailProvider) {
            case 'G':
                return Configuration.OauthNetwork.GOOGLE;
            case 'W':
                return Configuration.OauthNetwork.WINDOWS;
            case 'Y':
                return Configuration.OauthNetwork.YAHOO;
            default:
                return null;
        }
    }

    public Character getEmailProvider() {
        return emailProvider;
    }

    public void setEmailProvider(Character emailProvider) {
        this.emailProvider = emailProvider;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

//    public String getEmail() {
//        return email;
//    }
//
//    public void setEmail(String email) {
//        this.email = email;
//    }
}
