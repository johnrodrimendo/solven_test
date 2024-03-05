package com.affirm.common.model.transactional;

import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.util.Date;
import java.util.Locale;

public class EntityGatewayBaseDetail {

    private Date registerDateBase;
    private Integer countBase;
    private Integer countActive;
    private Integer countPayed;
    private Integer sentMonthSms;
    private Integer sentMonthEmail;

    public void fillFromDb(JSONObject json) throws Exception {
        setRegisterDateBase(JsonUtil.getPostgresDateFromJson(json, "register_date_base", null));
        setCountBase(JsonUtil.getIntFromJson(json, "count_base", null));
        setCountActive(JsonUtil.getIntFromJson(json, "count_active", null));
        setCountPayed(JsonUtil.getIntFromJson(json, "count_payed", null));
        setSentMonthSms(JsonUtil.getIntFromJson(json, "sent_month_sms", null));
        setSentMonthEmail(JsonUtil.getIntFromJson(json, "sent_month_email", null));
    }

    public Date getRegisterDateBase() {
        return registerDateBase;
    }

    public void setRegisterDateBase(Date registerDateBase) {
        this.registerDateBase = registerDateBase;
    }

    public Integer getCountBase() {
        return countBase;
    }

    public void setCountBase(Integer countBase) {
        this.countBase = countBase;
    }

    public Integer getCountActive() {
        return countActive;
    }

    public void setCountActive(Integer countActive) {
        this.countActive = countActive;
    }

    public Integer getCountPayed() {
        return countPayed;
    }

    public void setCountPayed(Integer countPayed) {
        this.countPayed = countPayed;
    }

    public Integer getSentMonthSms() {
        return sentMonthSms;
    }

    public void setSentMonthSms(Integer sentMonthSms) {
        this.sentMonthSms = sentMonthSms;
    }

    public Integer getSentMonthEmail() {
        return sentMonthEmail;
    }

    public void setSentMonthEmail(Integer sentMonthEmail) {
        this.sentMonthEmail = sentMonthEmail;
    }
}
