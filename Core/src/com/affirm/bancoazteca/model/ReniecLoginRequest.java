package com.affirm.bancoazteca.model;

public class ReniecLoginRequest {

    private String UserLogin;
    private String Password;
    private Integer ApplicationId;

    public String getUserLogin() {
        return UserLogin;
    }

    public void setUserLogin(String userLogin) {
        UserLogin = userLogin;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public Integer getApplicationId() {
        return ApplicationId;
    }

    public void setApplicationId(Integer applicationId) {
        ApplicationId = applicationId;
    }
}
