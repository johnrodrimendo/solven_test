package com.affirm.bantotalrest.model.authentication;
import com.affirm.bantotalrest.model.common.BtRequestData;

public class AuthenticationRequest extends BtRequestData {

    private String Userid;
    private String Userpassword;

    public String getUserid() {
        return Userid;
    }

    public void setUserid(String userid) {
        Userid = userid;
    }

    public String getUserpassword() {
        return Userpassword;
    }

    public void setUserpassword(String userpassword) {
        Userpassword = userpassword;
    }
}
