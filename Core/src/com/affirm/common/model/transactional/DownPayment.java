package com.affirm.common.model.transactional;

import com.affirm.common.model.catalog.Bank;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by dev5 on 15/08/17.
 */
public class DownPayment {

    private Integer downPaymentId;
    private Integer creditId;
    private String operationCode;
    private Double amount;
    private Date registerDate;
    private Bank bank;

    public void fillFromDb(CatalogService catalogService, JSONObject json) throws Exception {
        setDownPaymentId(JsonUtil.getIntFromJson(json, "down_payment_id", null));
        setOperationCode(JsonUtil.getStringFromJson(json, "operation_id", null));
        setAmount(JsonUtil.getDoubleFromJson(json, "amount", null));
        setCreditId(JsonUtil.getIntFromJson(json, "credit_id", null));
        setRegisterDate(JsonUtil.getPostgresDateFromJson(json, "register_date", null));
        setBank(catalogService.getBank(JsonUtil.getIntFromJson(json, "bank_id", null)));
    }

    public String getOperationCode() {
        return operationCode;
    }

    public void setOperationCode(String operationCode) {
        this.operationCode = operationCode;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Integer getCreditId() {
        return creditId;
    }

    public void setCreditId(Integer creditId) {
        this.creditId = creditId;
    }

    public Integer getDownPaymentId() {
        return downPaymentId;
    }

    public void setDownPaymentId(Integer downPaymentId) {
        this.downPaymentId = downPaymentId;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public Bank getBank() {
        return bank;
    }

    public void setBank(Bank bank) {
        this.bank = bank;
    }
}
