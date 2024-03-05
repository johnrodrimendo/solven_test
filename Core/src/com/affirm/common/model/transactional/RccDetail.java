package com.affirm.common.model.transactional;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by sTbn on 1/08/16.
 */
public class RccDetail implements Serializable {

    private Character accountType;
    private String accountCode;
    private String accountNumber;
    private Double solesAmount;
    private Double dollarAmount;
    private Integer arrears;
    private Date rccDate;
    private boolean showDate;


    public void fillFromDb(JSONObject json) throws Exception {
        setAccountType(JsonUtil.getCharacterFromJson(json,"ti_cuenta", null));
        setAccountCode(JsonUtil.getStringFromJson(json,"co_cuenta", null));
        setAccountNumber(JsonUtil.getStringFromJson(json,"no_cuenta", null));
        setSolesAmount(JsonUtil.getDoubleFromJson(json,"im_cuesol", null));
        setDollarAmount(JsonUtil.getDoubleFromJson(json,"im_cuedol", null));
        setArrears(JsonUtil.getIntFromJson(json,"ca_diamor", null));
        setRccDate(JsonUtil.getPostgresDateFromJson(json,"fe_repsbs", null));
    }

    public Character getAccountType() {
        return accountType;
    }

    public void setAccountType(char accountType) {
        this.accountType = accountType;
    }

    public String getAccountCode() {
        return accountCode;
    }

    public void setAccountCode(String accountCode) {
        this.accountCode = accountCode;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public Double getSolesAmount() {
        return solesAmount;
    }

    public void setSolesAmount(Double solesAmount) {
        this.solesAmount = solesAmount;
    }

    public Double getDollarAmount() {
        return dollarAmount;
    }

    public void setDollarAmount(Double dollarAmount) {
        this.dollarAmount = dollarAmount;
    }

    public Integer getArrears() {
        return arrears;
    }

    public void setArrears(Integer arrears) {
        this.arrears = arrears;
    }

    public Date getRccDate() {
        return rccDate;
    }

    public void setRccDate(Date rcc_date) {
        this.rccDate = rcc_date;
    }

    public boolean isShowDate() {
        return showDate;
    }

    public void setShowDate(boolean showDate) {
        this.showDate = showDate;
    }

    @Override
    public String toString() {
        return "RccDetail{" +
                "accountType=" + accountType +
                ", accountCode='" + accountCode + '\'' +
                ", accountNumber='" + accountNumber + '\'' +
                ", solesAmount=" + solesAmount +
                ", dollarAmount=" + dollarAmount +
                ", arrears=" + arrears +
                ", rccDate=" + rccDate +
                '}';
    }
}





