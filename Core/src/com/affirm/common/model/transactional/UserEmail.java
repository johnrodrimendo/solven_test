package com.affirm.common.model.transactional;

import com.affirm.common.util.JsonUtil;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by john on 04/01/17.
 */
public class UserEmail implements Serializable {

    public static final int RETRY_COUNTDOWN_SECONDS = 30;

    private Integer id;
    private Integer userId;
    private String email;
    private Date registerDate;
    private Boolean active;
    private Boolean verified;
    private List<EmailTokens> emailTokens;

    public void fillFromDb(JSONObject json) throws Exception {
        setId(JsonUtil.getIntFromJson(json, "email_id", null));
        setUserId(JsonUtil.getIntFromJson(json, "user_id", null));
        setEmail(JsonUtil.getStringFromJson(json, "email", null));
        setActive(JsonUtil.getBooleanFromJson(json, "is_active", null));
        setRegisterDate(JsonUtil.getPostgresDateFromJson(json, "register_date", null));
        setVerified(JsonUtil.getBooleanFromJson(json, "is_verified", null));
        if(JsonUtil.getJsonArrayFromJson(json, "js_tokens", null) != null){
            emailTokens = new ArrayList<>();
            JSONArray tokensJson = JsonUtil.getJsonArrayFromJson(json, "js_tokens", null);
            for (int i = 0; i < tokensJson.length(); i++) {
                EmailTokens token = new Gson().fromJson(tokensJson.getJSONObject(i).toString(),EmailTokens.class);
                emailTokens.add(token);
            }
        }
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public List<EmailTokens> getEmailTokens() {
        return emailTokens;
    }

    public void setEmailTokens(List<EmailTokens> emailTokens) {
        this.emailTokens = emailTokens;
    }

    public static class EmailTokens{
        private Integer triesCount;
        private Date registerDate;
        private Date expirationDate;
        private Integer personInteractionId;
        private String token;
        private Boolean active;

        public Integer getTriesCount() {
            return triesCount;
        }

        public void setTriesCount(Integer triesCount) {
            this.triesCount = triesCount;
        }

        public Date getRegisterDate() {
            return registerDate;
        }

        public void setRegisterDate(Date registerDate) {
            this.registerDate = registerDate;
        }

        public Date getExpirationDate() {
            return expirationDate;
        }

        public void setExpirationDate(Date expirationDate) {
            this.expirationDate = expirationDate;
        }

        public Integer getPersonInteractionId() {
            return personInteractionId;
        }

        public void setPersonInteractionId(Integer personInteractionId) {
            this.personInteractionId = personInteractionId;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public Boolean getActive() {
            return active;
        }

        public void setActive(Boolean active) {
            this.active = active;
        }
    }

    public long getSecondsForNextEmail(){
        if(emailTokens != null && getActiveEmailToken() != null){
            return addSecondsFrom(getActiveEmailToken().getRegisterDate(), RETRY_COUNTDOWN_SECONDS);
        }
        return 0L;
    }

    private long addSecondsFrom(Date from, int seconds) {
        Calendar calendar = Calendar.getInstance();
        long now = new Date().getTime();
        calendar.setTime(from);
        calendar.add(Calendar.SECOND, seconds);
        long milisecondsPassed = calendar.getTimeInMillis() - now;
        return milisecondsPassed < 0 ? 0 : milisecondsPassed;
    }

    public EmailTokens getActiveEmailToken(){
        if(emailTokens != null && !emailTokens.isEmpty()){
            return emailTokens.stream().filter(e -> e.getActive() != null && e.getActive()).findFirst().orElse(null);
        }
        return null;
    }

    public boolean canRetryEmail(){
        return getSecondsForNextEmail() <= 0;
    }

    public int retriesCount(){
        if(emailTokens != null && !emailTokens.isEmpty()){
            return emailTokens.stream().mapToInt(UserEmail.EmailTokens::getTriesCount).sum();
        }
        return 0;
    }


}
