package com.affirm.common.model.transactional;

import com.affirm.common.model.catalog.CountryParam;
import com.affirm.common.util.JsonUtil;
import com.google.gson.Gson;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class PhoneNumber {

    public static final int RETRY_COUNTDOWN_SECONDS = 30;
    public static final int AVAILABLE_CALL_COUNTDOWN_SECONDS = 30;
    public static final int MAX_SMS_RESENDINGS = 2;
    public static final int MAX_CALLS = 1;

    private Integer phoneNumberId;
    private Integer userId;
    private String countryCode;
    private String phoneNumber;
    private Date registerDate;
    private String smsToken;
    private boolean isActive;
    private boolean isVerified;
    private boolean toVerify;
    private Integer tries;
    private String phoneNumberType;
    private PinInteractions pinInteractions;

    public void fillFromDb(JSONObject json) {
        setPhoneNumberId(JsonUtil.getIntFromJson(json, "phone_number_id", null));
        setUserId(JsonUtil.getIntFromJson(json, "user_id", null));
        setCountryCode(JsonUtil.getStringFromJson(json, "country_code", null));
        setPhoneNumber(JsonUtil.getStringFromJson(json, "phone_number", null));
        setRegisterDate(JsonUtil.getPostgresDateFromJson(json, "register_date", null));
        setSmsToken(JsonUtil.getStringFromJson(json, "sms_token", null));
        setActive(JsonUtil.getBooleanFromJson(json, "is_active", false));
        setVerified(JsonUtil.getBooleanFromJson(json, "is_verified", false));
        setToVerify(JsonUtil.getBooleanFromJson(json, "to_verify", false));
        setTries(JsonUtil.getIntFromJson(json, "tries", 0));
        setPhoneNumberType(JsonUtil.getStringFromJson(json, "phone_number_type", null));
        if (JsonUtil.getJsonObjectFromJson(json, "js_person_interaction_id", null) != null) {
            setPinInteractions(new Gson().fromJson(JsonUtil.getJsonObjectFromJson(json, "js_person_interaction_id", null).toString(), PinInteractions.class));
        }
    }

    public long getSecondsForNextSms(){
        if(pinInteractions != null && pinInteractions.getLastSmsInteractionSend() != null){
            return addSecondsFrom(pinInteractions.getLastSmsInteractionSend(), RETRY_COUNTDOWN_SECONDS);
        }
        return 0L;
    }

    public boolean isSentSms() {
        return pinInteractions != null && pinInteractions.getSmsInteractions() != null && pinInteractions.getSmsInteractions().size() > 0;
    }

    public int getNumberSmsResendings() {
        if (pinInteractions != null && pinInteractions.getSmsInteractions() != null)
            return pinInteractions.getSmsInteractions().size();
        else
            return 0;
    }

    public int getNumberCalls() {
        if (pinInteractions != null && pinInteractions.getAutomaticCallInteractions() != null)
            return pinInteractions.getAutomaticCallInteractions().size();
        else
            return 0;
    }

    public boolean canRetrySms(){
        return getSecondsForNextSms() <= 0;
    }

    public long getSecondsForNextCall(){
        if(pinInteractions != null && pinInteractions.getLastAutomaticCallInteractionSend() != null){
            return addSecondsFrom(pinInteractions.getLastAutomaticCallInteractionSend(), RETRY_COUNTDOWN_SECONDS);
        }
        return 0L;
    }

    public boolean canRetryCall(){
        if(!canRetrySms() || getSecondsForNextCall() > 0)
            return false;
        return true;
    }

    public long getSecondsForAvailableCall() {
        if(pinInteractions != null && pinInteractions.getLastSmsInteractionSend() != null){
            return addSecondsFrom(pinInteractions.getLastSmsInteractionSend(), AVAILABLE_CALL_COUNTDOWN_SECONDS);
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

    public void addSmsInteractionSend(int interactionId, Date interactionSendDate) {
        if (getPinInteractions() == null)
            setPinInteractions(new PinInteractions());
        if (getPinInteractions().getSmsInteractions() == null)
            getPinInteractions().setSmsInteractions(new ArrayList<>());

        getPinInteractions().getSmsInteractions().add(interactionId);
        getPinInteractions().setLastSmsInteractionSend(interactionSendDate);
    }

    public void addCallInteractionSend(int interactionId, Date interactionSendDate) {
        if (getPinInteractions() == null)
            setPinInteractions(new PinInteractions());
        if (getPinInteractions().getAutomaticCallInteractions() == null)
            getPinInteractions().setAutomaticCallInteractions(new ArrayList<>());

        getPinInteractions().getAutomaticCallInteractions().add(interactionId);
        getPinInteractions().setLastAutomaticCallInteractionSend(interactionSendDate);
    }

    public String getPhoneNumberForCall(int countryId) {
        if (phoneNumber != null) {
            switch (countryId) {
                case CountryParam.COUNTRY_ARGENTINA:
                    return "9" + phoneNumber;
                case CountryParam.COUNTRY_COLOMBIA:
                    return "3" + phoneNumber;
            }
        }
        return phoneNumber;
    }

    public Integer getPhoneNumberId() {
        return phoneNumberId;
    }

    public void setPhoneNumberId(Integer phoneNumberId) {
        this.phoneNumberId = phoneNumberId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public String getSmsToken() {
        return smsToken;
    }

    public void setSmsToken(String smsToken) {
        this.smsToken = smsToken;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public boolean isToVerify() {
        return toVerify;
    }

    public void setToVerify(boolean toVerify) {
        this.toVerify = toVerify;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }

    public Integer getTries() {
        return tries;
    }

    public void setTries(Integer tries) {
        this.tries = tries;
    }

    public String getPhoneNumberType() {
        return phoneNumberType;
    }

    public void setPhoneNumberType(String phoneNumberType) {
        this.phoneNumberType = phoneNumberType;
    }

    public PinInteractions getPinInteractions() {
        return pinInteractions;
    }

    public void setPinInteractions(PinInteractions pinInteractions) {
        this.pinInteractions = pinInteractions;
    }
}
