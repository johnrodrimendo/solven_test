package com.affirm.pagolink.model;

public class PagoLinkConfiguration {

    private String urlGenerateAuthToken;
    private String urlAuthorizationEcommerce;
    private String urlSessionToken;
    private String urlCreateLink;
    private String urlOrderDetail;
    private String username;
    private String password;
    private String merchantId;

    public String getUrlGenerateAuthToken() {
        return urlGenerateAuthToken;
    }

    public void setUrlGenerateAuthToken(String urlGenerateAuthToken) {
        this.urlGenerateAuthToken = urlGenerateAuthToken;
    }

    public String getUrlAuthorizationEcommerce() {
        return urlAuthorizationEcommerce;
    }

    public void setUrlAuthorizationEcommerce(String urlAuthorizationEcommerce) {
        this.urlAuthorizationEcommerce = urlAuthorizationEcommerce;
    }

    public String getUrlSessionToken() {
        return urlSessionToken;
    }

    public void setUrlSessionToken(String urlSessionToken) {
        this.urlSessionToken = urlSessionToken;
    }

    public String getUrlCreateLink() {
        return urlCreateLink;
    }

    public void setUrlCreateLink(String urlCreateLink) {
        this.urlCreateLink = urlCreateLink;
    }

    public String getUrlOrderDetail() {
        return urlOrderDetail;
    }

    public void setUrlOrderDetail(String urlOrderDetail) {
        this.urlOrderDetail = urlOrderDetail;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }
}
