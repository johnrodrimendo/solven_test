package com.affirm.common.model.transactional;

import com.affirm.common.model.catalog.Ubigeo;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by jrodriguez on 14/06/16.
 */
public class RipleySefReport implements Serializable {

    private Date originated;
    private String docNumber;
    private String fullName;
    private Integer loanCapital;
    private Integer installments;
    private Double effectiveAnnualRate;
    private Double effectiveMonthlyRate;
    private Boolean insurance;
    private String phoneNumber;
    private String bank;
    private String bankAccount;
    private String cciCode;
    private String streetName;
    private Date approvalDate;
    private Date disbursementDate;
    private String cardType;
    private String cardNumber;
    private Ubigeo ubigeo;
    private String contract;



    public void fillFromDb(JSONObject json, CatalogService catalogService) throws Exception {
        setOriginated(JsonUtil.getPostgresDateFromJson(json, "approval_date", null));
        setDocNumber(JsonUtil.getStringFromJson(json, "document_number", null));
        setFullName(JsonUtil.getStringFromJson(json, "full_name", null));
        setLoanCapital(JsonUtil.getIntFromJson(json, "loan_capital", null));
        setInstallments(JsonUtil.getIntFromJson(json, "installments", null));
        setEffectiveAnnualRate(JsonUtil.getDoubleFromJson(json, "effective_annual_rate", null));
        setEffectiveMonthlyRate(JsonUtil.getDoubleFromJson(json, "effective_monthly_rate", null));
        setInsurance(JsonUtil.getBooleanFromJson(json, "insurance", null));
        setPhoneNumber(JsonUtil.getStringFromJson(json, "phone_number", null));
        setBank(JsonUtil.getStringFromJson(json, "bank", null));
        setBankAccount(JsonUtil.getStringFromJson(json, "bank_account", null));
        setCciCode(JsonUtil.getStringFromJson(json, "cci_code", null));
        setStreetName(JsonUtil.getStringFromJson(json, "street_name", null));
        setApprovalDate(JsonUtil.getPostgresDateFromJson(json, "approval_date", null));
        setDisbursementDate(JsonUtil.getPostgresDateFromJson(json, "disbursement_date", null));
        setCardType(JsonUtil.getStringFromJson(json, "card_type", null));
        setCardNumber(JsonUtil.getStringFromJson(json, "card_number", null));
        setUbigeo(catalogService.getUbigeo(JsonUtil.getStringFromJson(json, "ubigeo_id", null)));
        setContract(JsonUtil.getStringFromJson(json,"contract",null));
    }

    public Date getOriginated() {
        return originated;
    }

    public void setOriginated(Date originated) {
        this.originated = originated;
    }

    public String getDocNumber() {
        return docNumber;
    }

    public void setDocNumber(String docNumber) {
        this.docNumber = docNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Integer getLoanCapital() {
        return loanCapital;
    }

    public void setLoanCapital(Integer loanCapital) {
        this.loanCapital = loanCapital;
    }

    public Integer getInstallments() {
        return installments;
    }

    public void setInstallments(Integer installments) {
        this.installments = installments;
    }

    public Double getEffectiveAnnualRate() {
        return effectiveAnnualRate;
    }

    public void setEffectiveAnnualRate(Double effectiveAnnualRate) {
        this.effectiveAnnualRate = effectiveAnnualRate;
    }

    public Boolean getInsurance() {
        return insurance;
    }

    public void setInsurance(Boolean insurance) {
        this.insurance = insurance;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getCciCode() {
        return cciCode;
    }

    public void setCciCode(String cciCode) {
        this.cciCode = cciCode;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public Date getApprovalDate() {
        return approvalDate;
    }

    public void setApprovalDate(Date approvalDate) {
        this.approvalDate = approvalDate;
    }

    public Date getDisbursementDate() {
        return disbursementDate;
    }

    public void setDisbursementDate(Date disbursementDate) {
        this.disbursementDate = disbursementDate;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public Double getEffectiveMonthlyRate() {
        return effectiveMonthlyRate;
    }

    public void setEffectiveMonthlyRate(Double effectiveMonthlyRate) {
        this.effectiveMonthlyRate = effectiveMonthlyRate;
    }

    public Ubigeo getUbigeo() {
        return ubigeo;
    }

    public void setUbigeo(Ubigeo ubigeo) {
        this.ubigeo = ubigeo;
    }

    public String getContract() {
        return contract;
    }

    public void setContract(String contract) {
        this.contract = contract;
    }
}
