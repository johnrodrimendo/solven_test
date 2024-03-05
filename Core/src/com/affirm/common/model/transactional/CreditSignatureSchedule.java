package com.affirm.common.model.transactional;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by jrodriguez on 14/06/16.
 */
public class CreditSignatureSchedule implements Serializable {

    private Integer id;
    private Date scheduleDate;
    private String scheduleHour;
    private Boolean signed;
    private String address;
    private String comment;
    private Date signatureDate;
    private Date registerDate;

    public void fillFromDb(JSONObject json) throws Exception {
        setId(JsonUtil.getIntFromJson(json, "credit_id", null));
        setScheduleDate(JsonUtil.getPostgresDateFromJson(json, "signature_schedule", null));
        setScheduleHour(JsonUtil.getStringFromJson(json, "signature_hour", null));
        setSigned(JsonUtil.getBooleanFromJson(json, "is_signed", null));
        setAddress(JsonUtil.getStringFromJson(json, "address", null));
        setComment(JsonUtil.getStringFromJson(json, "comment", null));
        setSignatureDate(JsonUtil.getPostgresDateFromJson(json, "signature_date", null));
        setRegisterDate(JsonUtil.getPostgresDateFromJson(json, "register_date", null));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getScheduleDate() {
        return scheduleDate;
    }

    public void setScheduleDate(Date scheduleDate) {
        this.scheduleDate = scheduleDate;
    }

    public String getScheduleHour() {
        return scheduleHour;
    }

    public void setScheduleHour(String scheduleHour) {
        this.scheduleHour = scheduleHour;
    }

    public Boolean getSigned() {
        return signed;
    }

    public void setSigned(Boolean signed) {
        this.signed = signed;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getSignatureDate() {
        return signatureDate;
    }

    public void setSignatureDate(Date signatureDate) {
        this.signatureDate = signatureDate;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }
}
