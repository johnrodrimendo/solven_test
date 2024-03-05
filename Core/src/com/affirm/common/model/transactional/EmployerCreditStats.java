package com.affirm.common.model.transactional;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EmployerCreditStats implements Serializable {

    private Integer currentMonthCredits;
    private Double currentMonthAmount;
    private Integer activeCredits;
    private Double totalPendinAmount;
    private Date firstCreditMonth;

    public void fillFromDb(JSONObject json) throws Exception {
        setCurrentMonthCredits(JsonUtil.getIntFromJson(json, "current_month", null));
        setCurrentMonthAmount(JsonUtil.getDoubleFromJson(json, "current_month_amount", null));
        setActiveCredits(JsonUtil.getIntFromJson(json, "active_credits", null));
        setTotalPendinAmount(JsonUtil.getDoubleFromJson(json, "total_pending_amount", null));
        if (JsonUtil.getStringFromJson(json, "first_credit_month", null) != null)
            setFirstCreditMonth(new SimpleDateFormat("yyyy-MM-dd").parse(JsonUtil.getStringFromJson(json, "first_credit_month", null)));
    }

    public Double getTotalArrearsAmount() {
        return totalPendinAmount - currentMonthAmount;
    }

    public Integer getCurrentMonthCredits() {
        return currentMonthCredits;
    }

    public void setCurrentMonthCredits(Integer currentMonthCredits) {
        this.currentMonthCredits = currentMonthCredits;
    }

    public Double getCurrentMonthAmount() {
        return currentMonthAmount;
    }

    public void setCurrentMonthAmount(Double currentMonthAmount) {
        this.currentMonthAmount = currentMonthAmount;
    }

    public Integer getActiveCredits() {
        return activeCredits;
    }

    public void setActiveCredits(Integer activeCredits) {
        this.activeCredits = activeCredits;
    }

    public Double getTotalPendinAmount() {
        return totalPendinAmount;
    }

    public void setTotalPendinAmount(Double totalPendinAmount) {
        this.totalPendinAmount = totalPendinAmount;
    }

    public Date getFirstCreditMonth() {
        return firstCreditMonth;
    }

    public void setFirstCreditMonth(Date firstCreditMonth) {
        this.firstCreditMonth = firstCreditMonth;
    }
}