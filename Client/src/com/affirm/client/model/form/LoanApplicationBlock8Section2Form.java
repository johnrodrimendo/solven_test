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
public class LoanApplicationBlock8Section2Form extends FormGeneric {

    private Character socialNetwork;
    private String authCode;

    public LoanApplicationBlock8Section2Form() {
        this.setValidator(new LoanApplicationBlock8Section2FormValidator());
    }

    public class LoanApplicationBlock8Section2FormValidator extends FormValidator implements Serializable {

        public CharFieldValidator socialNetwork;
        public StringFieldValidator authCode;

        public LoanApplicationBlock8Section2FormValidator() {
            addValidator(socialNetwork = new CharFieldValidator(ValidatorUtil.NETWORK_PROVIDER));
            addValidator(authCode = new StringFieldValidator(ValidatorUtil.OAUTH_CODE));
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
            return LoanApplicationBlock8Section2Form.this;
        }
    }

    public Configuration.OauthNetwork getOauthNetworkByProvider() {
        switch (socialNetwork) {
            case 'L':
                return Configuration.OauthNetwork.LINKEDIN;
            case 'F':
                return Configuration.OauthNetwork.FACEBOOK;
            case 'M':
                return Configuration.OauthNetwork.MERCADOLIBRE;
            default:
                return null;
        }
    }

    public Character getSocialNetwork() {
        return socialNetwork;
    }

    public void setSocialNetwork(Character socialNetwork) {
        this.socialNetwork = socialNetwork;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }
}
