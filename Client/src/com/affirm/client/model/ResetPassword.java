package com.affirm.client.model;

/**
 * Created by dev5 on 16/08/17.
 */
public class ResetPassword {

    private String email;
    private String expireTime;

    public ResetPassword(String email) {
        this.email = email;
    }

    public ResetPassword(String email, String expireTime){
        setEmail(email);
        setExpireTime(expireTime);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(String expireTime) {
        this.expireTime = expireTime;
    }

}
