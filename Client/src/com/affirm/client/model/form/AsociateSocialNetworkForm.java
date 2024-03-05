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
public class AsociateSocialNetworkForm extends FormGeneric {

    private Character socialNetwork;
    private String authCode;

    public AsociateSocialNetworkForm() {
        this.setValidator(new AsociateSocialNetworkFormValidator());
    }

    public class AsociateSocialNetworkFormValidator extends FormValidator implements Serializable {

        public CharFieldValidator socialNetwork;
        public StringFieldValidator authCode;

        public AsociateSocialNetworkFormValidator() {
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
            return AsociateSocialNetworkForm.this;
        }
    }

    public Configuration.OauthNetwork getOauthNetworkByProvider() {
        switch (socialNetwork) {
            case 'G':
                return Configuration.OauthNetwork.GOOGLE;
            case 'W':
                return Configuration.OauthNetwork.WINDOWS;
            case 'Y':
                return Configuration.OauthNetwork.YAHOO;
            case 'F':
                return Configuration.OauthNetwork.FACEBOOK;
            case 'L':
                return Configuration.OauthNetwork.LINKEDIN;
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
