package com.affirm.client.model;

import com.affirm.common.model.transactional.User;
import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

public class LoggedUserClient extends User implements Serializable {

    private Integer sessionId;
    private Date lastSigninDate;

    public void fillFromDb(JSONObject json) {
        setSessionId(JsonUtil.getIntFromJson(json, "entity_session_id", null));
        setLastSigninDate(JsonUtil.getPostgresDateFromJson(json, "last_sign_in_date", null));
        super.fillFromDb(json);
    }

    public Integer getSessionId() {
        return sessionId;
    }

    public void setSessionId(Integer sessionId) {
        this.sessionId = sessionId;
    }

    public Date getLastSigninDate() {
        return lastSigninDate;
    }

    public void setLastSigninDate(Date lastSigninDate) {
        this.lastSigninDate = lastSigninDate;
    }
}