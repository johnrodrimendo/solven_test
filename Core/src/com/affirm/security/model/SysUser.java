package com.affirm.security.model;

import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by jrodriguez on 21/07/16.
 */
public class SysUser implements Serializable{

    private Integer id;
    private String userName;
    private String password;
    private Date regiterDate;
    private Date cessationDate;
    private String name;
    private String firstSurname;
    private String lastSurname;
    private Boolean active;
    private String avatar;
    private String tfaSharedSecret;
    private transient JSONArray tfaScratchCodes;
    private String StringTfaScratchCodes;
    private String email;
//    private List<CountryParam> countries;
    private Integer scheduleId;
    private SysUserSchedule schedule;
    private Date passwordExpirationDate;
    private Boolean activeTfaLogin;
    private Map<Integer, Boolean> countries;

    public void fillFromDb(CatalogService catalogService, JSONObject json) {
        setId(JsonUtil.getIntFromJson(json, "sysuser_id", null));
        setUserName(JsonUtil.getStringFromJson(json, "username", null));
        setRegiterDate(JsonUtil.getPostgresDateFromJson(json, "register_date", null));
        setCessationDate(JsonUtil.getPostgresDateFromJson(json, "cessation_date", null));
        setName(JsonUtil.getStringFromJson(json, "person_name", null));
        setFirstSurname(JsonUtil.getStringFromJson(json, "first_surname", null));
        setLastSurname(JsonUtil.getStringFromJson(json, "last_surname", null));
        setActive(JsonUtil.getBooleanFromJson(json, "is_active", null));
        setAvatar(JsonUtil.getStringFromJson(json, "avatar", null));
        tfaSharedSecret = JsonUtil.getStringFromJson(json, "tfa_shared_secret", null);
        tfaScratchCodes = JsonUtil.getJsonArrayFromJson(json, "tfa_scrath_codes", new JSONArray());
        setEmail(JsonUtil.getStringFromJson(json, "email", null));
        setPassword(JsonUtil.getStringFromJson(json, "password", null));
        JSONArray array = JsonUtil.getJsonArrayFromJson(json, "js_countries", null);
        if(array!=null){
            setCountries(new HashMap<>());
            for(int i=0;i<array.length();i++){
                getCountries().put(array.getJSONObject(i).getInt("country_id"), array.getJSONObject(i).getBoolean("default"));
            }
        }
        setScheduleId(JsonUtil.getIntFromJson(json, "sysuser_schedule_id", null));
        setPasswordExpirationDate(JsonUtil.getPostgresDateFromJson(json, "password_expiration_date", null));
        setActiveTfaLogin(JsonUtil.getBooleanFromJson(json, "tfa_login", null));
        setStringTfaScratchCodes(JsonUtil.getStringFromJson(json, "tfa_scrath_codes", null));
    }

    public List<Integer> getActiveCountries(){
        return getCountries().entrySet()
                .stream()
                .filter(e -> e.getValue())
                .map(e -> e.getKey())
                .collect(Collectors.toList());
    }

    public String getFullName() {
        String fullname = "";
        if (name != null) {
            fullname = fullname + name + " ";
        }
        if (firstSurname != null) {
            fullname = fullname + firstSurname + " ";
        }
        if (lastSurname != null) {
            fullname = fullname + lastSurname + " ";
        }
        return fullname.trim();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirstSurname() {
        return firstSurname;
    }

    public void setFirstSurname(String firstSurname) {
        this.firstSurname = firstSurname;
    }

    public String getLastSurname() {
        return lastSurname;
    }

    public void setLastSurname(String lastSurname) {
        this.lastSurname = lastSurname;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getRegiterDate() {
        return regiterDate;
    }

    public void setRegiterDate(Date regiterDate) {
        this.regiterDate = regiterDate;
    }

    public Date getCessationDate() {
        return cessationDate;
    }

    public void setCessationDate(Date cessationDate) {
        this.cessationDate = cessationDate;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getTfaSharedSecret() {
        return tfaSharedSecret;
    }

    public void setTfaSharedSecret(String tfaSharedSecret) {
        this.tfaSharedSecret = tfaSharedSecret;
    }

    public JSONArray getTfaScratchCodes() {

        if (tfaScratchCodes != null) {
            return tfaScratchCodes;
        } else {
            if (this.getStringTfaScratchCodes() != null){
                JSONObject jsonObject = new JSONObject(this.getStringTfaScratchCodes());
                String[] names = JSONObject.getNames(jsonObject);
                JSONArray jsonArray = jsonObject.toJSONArray(new JSONArray(names));
                return jsonArray;
            } else {
                return null;
            }
        }
    }

    public void setTfaScratchCodes(JSONArray tfaScratchCodes) {
        this.tfaScratchCodes = tfaScratchCodes;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return name;
    }

    public Integer getScheduleId() { return scheduleId; }

    public void setScheduleId(Integer schedule_id) { this.scheduleId = schedule_id; }

    public Date getPasswordExpirationDate() {return passwordExpirationDate; }

    public void setPasswordExpirationDate(Date passwordExpirationDate) { this.passwordExpirationDate = passwordExpirationDate;}

    public Boolean getActiveTfaLogin() { return activeTfaLogin; }

    public void setActiveTfaLogin(Boolean activeTfaLogin) { this.activeTfaLogin = activeTfaLogin; }

    public String getStringTfaScratchCodes() { return StringTfaScratchCodes; }

    public void setStringTfaScratchCodes(String stringTfaScratchCodes) { StringTfaScratchCodes = stringTfaScratchCodes; }

    public Map<Integer, Boolean> getCountries() {
        return countries;
    }

    public void setCountries(Map<Integer, Boolean> countries) {
        this.countries = countries;
    }
}
