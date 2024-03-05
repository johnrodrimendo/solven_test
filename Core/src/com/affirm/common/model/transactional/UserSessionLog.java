package com.affirm.common.model.transactional;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;
import java.time.Duration;
import java.time.ZoneId;
import java.util.Date;

/**
 * Created by john on 26/12/16.
 */
public class UserSessionLog implements Serializable {

    private Integer id;
    private Integer userId;
    private String ip;
    private String browserMetadata;
    private Date signInDate;
    private Date signOutDate;

    public void fillFromDb(JSONObject json) throws Exception {
        setId(JsonUtil.getIntFromJson(json, "session_id", null));
        setUserId(JsonUtil.getIntFromJson(json, "user_id", null));
        setIp(JsonUtil.getStringFromJson(json, "session_ip", null));
        setBrowserMetadata(JsonUtil.getStringFromJson(json, "metadata", null));
        setSignInDate(JsonUtil.getPostgresDateFromJson(json, "sign_in_date", null));
        setSignOutDate(JsonUtil.getPostgresDateFromJson(json, "sign_out_date", null));
    }

    public Long getDurationInMinutes() {
        if (signInDate != null && signOutDate != null) {
            return Duration.between(signInDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(), signOutDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()).toMinutes();
        }
        return null;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getBrowserMetadata() {
        return browserMetadata;
    }

    public void setBrowserMetadata(String browserMetadata) {
        this.browserMetadata = browserMetadata;
    }

    public Date getSignInDate() {
        return signInDate;
    }

    public void setSignInDate(Date signInDate) {
        this.signInDate = signInDate;
    }

    public Date getSignOutDate() {
        return signOutDate;
    }

    public void setSignOutDate(Date signOutDate) {
        this.signOutDate = signOutDate;
    }
}
