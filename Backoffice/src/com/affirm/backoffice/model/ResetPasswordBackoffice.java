package com.affirm.backoffice.model;

public class ResetPasswordBackoffice {

    private String email;
    private String expireTime;

    public ResetPasswordBackoffice(String email, String expireTime){
        setEmail(email);
        setExpireTime(expireTime);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getExpireTime() { return expireTime; }

    public void setExpireTime(String expireTime) {
        this.expireTime = expireTime;
    }
}
