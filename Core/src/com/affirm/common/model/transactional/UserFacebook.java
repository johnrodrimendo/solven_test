/**
 *
 */
package com.affirm.common.model.transactional;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

/**
 * @author jrodriguez
 */
public class UserFacebook implements Serializable {

    private String facebookId;
    private String facebookEmail;
    private String facebookName;
    private String facebookFirstName;
    private String facebookLastName;
    private Integer facebookAgeMax;
    private Integer facebookAgeMin;
    private String facebookLink;
    private String facebookGender;
    private String facebookLocale;
    private String facebookPicture;
    private Integer facebookTimeZone;
    private Date facebookUpdatedTime;
    private String facebookVerified;
    private String facebookBirthday;
    private String facebookLocation;
    private Integer facebookFriends;


    public void fillFromDb(JSONObject json) throws Exception {
        setFacebookId(JsonUtil.getStringFromJson(json, "facebook_id", null));
        setFacebookEmail(JsonUtil.getStringFromJson(json, "email", null));
        setFacebookName(JsonUtil.getStringFromJson(json, "full_name", null));
        setFacebookFirstName(JsonUtil.getStringFromJson(json, "first_name", null));
        setFacebookLastName(JsonUtil.getStringFromJson(json, "last_name", null));
        setFacebookAgeMin(JsonUtil.getIntFromJson(json, "age_min", null));
        setFacebookAgeMax(JsonUtil.getIntFromJson(json, "age_max", null));
        setFacebookLink(JsonUtil.getStringFromJson(json, "link", null));
        setFacebookGender(JsonUtil.getStringFromJson(json, "gender", null));
        setFacebookLocale(JsonUtil.getStringFromJson(json, "locale", null));
        setFacebookPicture(JsonUtil.getStringFromJson(json, "picture", null));
        setFacebookTimeZone(JsonUtil.getIntFromJson(json, "timezone", null));
        setFacebookUpdatedTime(JsonUtil.getPostgresDateFromJson(json, "updated_time", null));
        setFacebookVerified(JsonUtil.getStringFromJson(json, "verified", null));
        setFacebookBirthday(JsonUtil.getStringFromJson(json, "birthday", null));
        setFacebookLocation(JsonUtil.getStringFromJson(json, "location", null));
        setFacebookFriends(JsonUtil.getIntFromJson(json, "friends", null));
    }

    public void fillFromApi(JSONObject json) throws Exception {
        setFacebookId(JsonUtil.getStringFromJson(json, "id", null));
        setFacebookEmail(JsonUtil.getStringFromJson(json, "email", null));
        setFacebookName(JsonUtil.getStringFromJson(json, "name", null));
        setFacebookFirstName(JsonUtil.getStringFromJson(json, "first_name", null));
        setFacebookLastName(JsonUtil.getStringFromJson(json, "last_name", null));
        setFacebookAgeMin(JsonUtil.getIntFromJson(JsonUtil.getJsonObjectFromJson(json, "age_range", null), "min", null));
        setFacebookAgeMax(JsonUtil.getIntFromJson(JsonUtil.getJsonObjectFromJson(json, "age_range", null), "max", null));
        setFacebookLink(JsonUtil.getStringFromJson(json, "link", null));
        setFacebookGender(JsonUtil.getStringFromJson(json, "gender", null));
        setFacebookLocale(JsonUtil.getStringFromJson(json, "locale", null));
        setFacebookPicture(JsonUtil.getStringFromJson(JsonUtil.getJsonObjectFromJson(JsonUtil.getJsonObjectFromJson(json, "picture", null), "data", null), "picture", null));
        setFacebookTimeZone(JsonUtil.getIntFromJson(json, "timezone", null));
        setFacebookUpdatedTime(JsonUtil.getPostgresDateFromJson(json, "updated_time", null));
        setFacebookVerified(JsonUtil.getStringFromJson(json, "verified", null));
        setFacebookBirthday(JsonUtil.getStringFromJson(json, "birthday", null));
        setFacebookLocation(JsonUtil.getStringFromJson(json, "location", null));
    }

    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    public String getFacebookEmail() {
        return facebookEmail;
    }

    public void setFacebookEmail(String facebookEmail) {
        this.facebookEmail = facebookEmail;
    }

    public String getFacebookName() {
        return facebookName;
    }

    public void setFacebookName(String facebookName) {
        this.facebookName = facebookName;
    }

    public String getFacebookFirstName() {
        return facebookFirstName;
    }

    public void setFacebookFirstName(String facebookFirstName) {
        this.facebookFirstName = facebookFirstName;
    }

    public String getFacebookLastName() {
        return facebookLastName;
    }

    public void setFacebookLastName(String facebookLastName) {
        this.facebookLastName = facebookLastName;
    }

    public Integer getFacebookAgeMax() {
        return facebookAgeMax;
    }

    public void setFacebookAgeMax(Integer facebookAgeMax) {
        this.facebookAgeMax = facebookAgeMax;
    }

    public Integer getFacebookAgeMin() {
        return facebookAgeMin;
    }

    public void setFacebookAgeMin(Integer facebookAgeMin) {
        this.facebookAgeMin = facebookAgeMin;
    }

    public String getFacebookLink() {
        return facebookLink;
    }

    public void setFacebookLink(String facebookLink) {
        this.facebookLink = facebookLink;
    }

    public String getFacebookGender() {
        return facebookGender;
    }

    public void setFacebookGender(String facebookGender) {
        this.facebookGender = facebookGender;
    }

    public String getFacebookLocale() {
        return facebookLocale;
    }

    public void setFacebookLocale(String facebookLocale) {
        this.facebookLocale = facebookLocale;
    }

    public String getFacebookPicture() {
        return facebookPicture;
    }

    public void setFacebookPicture(String facebookPicture) {
        this.facebookPicture = facebookPicture;
    }

    public Integer getFacebookTimeZone() {
        return facebookTimeZone;
    }

    public void setFacebookTimeZone(Integer facebookTimeZone) {
        this.facebookTimeZone = facebookTimeZone;
    }

    public Date getFacebookUpdatedTime() {
        return facebookUpdatedTime;
    }

    public void setFacebookUpdatedTime(Date facebookUpdatedTime) {
        this.facebookUpdatedTime = facebookUpdatedTime;
    }

    public String getFacebookVerified() {
        return facebookVerified;
    }

    public void setFacebookVerified(String facebookVerified) {
        this.facebookVerified = facebookVerified;
    }

    public String getFacebookBirthday() {
        return facebookBirthday;
    }

    public void setFacebookBirthday(String facebookBirthday) {
        this.facebookBirthday = facebookBirthday;
    }

    public String getFacebookLocation() {
        return facebookLocation;
    }

    public void setFacebookLocation(String facebookLocation) {
        this.facebookLocation = facebookLocation;
    }

    public Integer getFacebookFriends() {
        return facebookFriends;
    }

    public void setFacebookFriends(Integer facebookFriends) {
        this.facebookFriends = facebookFriends;
    }
}
