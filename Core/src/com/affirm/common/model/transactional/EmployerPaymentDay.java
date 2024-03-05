package com.affirm.common.model.transactional;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.time.LocalDate;

/**
 * Created by jarmando on 13/02/17.
 */
public class EmployerPaymentDay {

    private Integer id;
    private Integer employerId;
    private LocalDate month;
    private LocalDate paymentDay;

    public void fillFromDb(JSONObject json){
        setId(JsonUtil.getIntFromJson(json, "employer_payment_day_id", null));
        setEmployerId(JsonUtil.getIntFromJson(json, "employer_id", null));
        if (JsonUtil.getStringFromJson(json, "month", null) != null)
            setMonth(LocalDate.parse(JsonUtil.getStringFromJson(json, "month", null)));
        if (JsonUtil.getStringFromJson(json, "payment_day", null) != null)
            setPaymentDay(LocalDate.parse(JsonUtil.getStringFromJson(json, "payment_day", null)));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getEmployerId() {
        return employerId;
    }

    public void setEmployerId(Integer employerId) {
        this.employerId = employerId;
    }

    public LocalDate getMonth() {
        return month;
    }

    public void setMonth(LocalDate month) {
        this.month = month;
    }

    public LocalDate getPaymentDay() {
        return paymentDay;
    }

    public void setPaymentDay(LocalDate paymentDay) {
        this.paymentDay = paymentDay;
    }
}