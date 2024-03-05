package com.affirm.common.model.transactional;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.util.Date;

public class VerificationCallRequest {

    private Integer verificationCallRequestId;
    private Integer loanApplicationId;
    private String phoneNumber;
    private String countryCode;
    private Date registerDate;

    public void fillFromDb(JSONObject json) throws Exception {
        setVerificationCallRequestId(JsonUtil.getIntFromJson(json, "verification_call_request_id", null));
        setLoanApplicationId(JsonUtil.getIntFromJson(json, "loan_application_id", null));
        setPhoneNumber(JsonUtil.getStringFromJson(json, "phone_number", null));
        setCountryCode(JsonUtil.getStringFromJson(json, "country_code", null));
        setRegisterDate(JsonUtil.getPostgresDateFromJson(json, "register_date", null));
    }

    public Integer getVerificationCallRequestId() {
        return verificationCallRequestId;
    }

    public void setVerificationCallRequestId(Integer verificationCallRequestId) {
        this.verificationCallRequestId = verificationCallRequestId;
    }

    public Integer getLoanApplicationId() {
        return loanApplicationId;
    }

    public void setLoanApplicationId(Integer loanApplicationId) {
        this.loanApplicationId = loanApplicationId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }
}
