package com.affirm.common.model.transactional;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by john on 29/09/16.
 */
public class SunatResult implements Serializable {

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
    public static final int DNI_TYPE = 1;
    public static final int RUC_TYPE = 2;
    public static final int CEX_TYPE = 4;

    private Integer queryId;
    private String inRuc;
    private Integer inDocType;
    private String inDocNumber;
    private String ruc;
    private String tradeName;
    private String located;
    private String status;
    private String taxpayerType;
    private Date registerDate;
    private Date startupDate;
    private String taxpayerCondition;
    private String fiscalAddress;
    private String voucherEmittingSystem;
    private String externalCommerceActivity;
    private String accountingSystem;
    private String ocupation;
    private String economicActivities;
    private String voucher;
    private String digitalEmissionSystem;
    private Date digitalEmitterSince;
    private String digitalVouchers;
    private String pleJoinedSince;
    private String padron;
    private String principalCIIU;

    public void fillFromDb(JSONObject json) {
        setQueryId(JsonUtil.getIntFromJson(json, "queryId", null));
        setInRuc(JsonUtil.getStringFromJson(json, "in_ruc", null));
        setInDocType(JsonUtil.getIntFromJson(json, "in_tipodoc", null));
        setInDocNumber(JsonUtil.getStringFromJson(json, "in_numdoc", null));
        setRuc(JsonUtil.getStringFromJson(json, "ruc", null));
        setTradeName(JsonUtil.getStringFromJson(json, "tradename", null));
        setLocated(JsonUtil.getStringFromJson(json, "located", null));
        setStatus(JsonUtil.getStringFromJson(json, "status", null));
        setTaxpayerType(JsonUtil.getStringFromJson(json, "taxpayer_type", null));
        setRegisterDate(JsonUtil.getPostgresDateFromJson(json, "register_date", null));
        setStartupDate(JsonUtil.getPostgresDateFromJson(json, "startup_date", null));
        setTaxpayerCondition(JsonUtil.getStringFromJson(json, "taxpayer_condition", null));
        setFiscalAddress(JsonUtil.getStringFromJson(json, "fiscal_address", null));
        setVoucherEmittingSystem(JsonUtil.getStringFromJson(json, "voucher_emitting_system", null));
        setExternalCommerceActivity(JsonUtil.getStringFromJson(json, "external_commerce_activity", null));
        setAccountingSystem(JsonUtil.getStringFromJson(json, "accounting_system", null));
        setOcupation(JsonUtil.getStringFromJson(json, "ocupation", null));
        setEconomicActivities(JsonUtil.getStringFromJson(json, "economic_activities", null));
        setVoucher(JsonUtil.getStringFromJson(json, "voucher", null));
        setDigitalEmissionSystem(JsonUtil.getStringFromJson(json, "digital_emission_system", null));
        setDigitalEmitterSince(JsonUtil.getPostgresDateFromJson(json, "digital_emitter_since", null));
        setDigitalVouchers(JsonUtil.getStringFromJson(json, "digital_vouchers", null));
        setPleJoinedSince(JsonUtil.getStringFromJson(json, "ple_joined_since", null));
        setPadron(JsonUtil.getStringFromJson(json, "padron", null));
        setPrincipalCIIU(JsonUtil.getStringFromJson(json, "principal_ciiu", null));
    }

    public Integer getQueryId() {
        return queryId;
    }

    public void setQueryId(Integer queryId) {
        this.queryId = queryId;
    }

    public String getInRuc() {
        return inRuc;
    }

    public void setInRuc(String inRuc) {
        this.inRuc = inRuc;
    }

    public Integer getInDocType() {
        return inDocType;
    }

    public void setInDocType(Integer inDocType) {
        this.inDocType = inDocType;
    }

    public String getInDocNumber() {
        return inDocNumber;
    }

    public void setInDocNumber(String inDocNumber) {
        this.inDocNumber = inDocNumber;
    }

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public String getTradeName() {
        return tradeName;
    }

    public void setTradeName(String tradeName) {
        this.tradeName = tradeName;
    }

    public String getLocated() {
        return located;
    }

    public void setLocated(String located) {
        this.located = located;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTaxpayerType() {
        return taxpayerType;
    }

    public void setTaxpayerType(String taxpayerType) {
        this.taxpayerType = taxpayerType;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public Date getStartupDate() {
        return startupDate;
    }

    public void setStartupDate(Date startupDate) {
        this.startupDate = startupDate;
    }

    public String getTaxpayerCondition() {
        return taxpayerCondition;
    }

    public void setTaxpayerCondition(String taxpayerCondition) {
        this.taxpayerCondition = taxpayerCondition;
    }

    public String getFiscalAddress() {
        return fiscalAddress;
    }

    public void setFiscalAddress(String fiscalAddress) {
        this.fiscalAddress = fiscalAddress;
    }

    public String getVoucherEmittingSystem() {
        return voucherEmittingSystem;
    }

    public void setVoucherEmittingSystem(String voucherEmittingSystem) {
        this.voucherEmittingSystem = voucherEmittingSystem;
    }

    public String getExternalCommerceActivity() {
        return externalCommerceActivity;
    }

    public void setExternalCommerceActivity(String externalCommerceActivity) {
        this.externalCommerceActivity = externalCommerceActivity;
    }

    public String getAccountingSystem() {
        return accountingSystem;
    }

    public void setAccountingSystem(String accountingSystem) {
        this.accountingSystem = accountingSystem;
    }

    public String getOcupation() {
        return ocupation;
    }

    public void setOcupation(String ocupation) {
        this.ocupation = ocupation;
    }

    public String getEconomicActivities() {
        return economicActivities;
    }

    public void setEconomicActivities(String economicActivities) {
        this.economicActivities = economicActivities;
    }

    public String getVoucher() {
        return voucher;
    }

    public void setVoucher(String voucher) {
        this.voucher = voucher;
    }

    public String getDigitalEmissionSystem() {
        return digitalEmissionSystem;
    }

    public void setDigitalEmissionSystem(String digitalEmissionSystem) {
        this.digitalEmissionSystem = digitalEmissionSystem;
    }

    public Date getDigitalEmitterSince() {
        return digitalEmitterSince;
    }

    public void setDigitalEmitterSince(Date digitalEmitterSince) {
        this.digitalEmitterSince = digitalEmitterSince;
    }

    public String getDigitalVouchers() {
        return digitalVouchers;
    }

    public void setDigitalVouchers(String digitalVouchers) {
        this.digitalVouchers = digitalVouchers;
    }

    public String getPleJoinedSince() {
        return pleJoinedSince;
    }

    public void setPleJoinedSince(String pleJoinedSince) {
        this.pleJoinedSince = pleJoinedSince;
    }

    public String getPadron() {
        return padron;
    }

    public void setPadron(String padron) {
        this.padron = padron;
    }

    @Override
    public String toString() {
        return "SunatResult{" + "ruc=" + ruc + ", tradeName=" + tradeName + ", located=" + located + ", status=" + status + ", taxpayerType=" + taxpayerType + ", registerDate=" + registerDate + ", startupDate=" + startupDate + ", taxpayerCondition=" + taxpayerCondition + ", fiscalAddress=" + fiscalAddress + ", voucherEmittingSystem=" + voucherEmittingSystem + ", externalCommerceActivity=" + externalCommerceActivity + ", accountingSystem=" + accountingSystem + ", ocupation=" + ocupation + ", economicActivities=" + economicActivities + ", voucher=" + voucher + ", digitalEmissionSystem=" + digitalEmissionSystem + ", digitalEmitterSince=" + digitalEmitterSince + ", digitalVouchers=" + digitalVouchers + ", pleJoinedSince=" + pleJoinedSince + ", padron=" + padron + '}';
    }

    public String getPrincipalCIIU() {
        return principalCIIU;
    }

    public void setPrincipalCIIU(String principalCIIU) {
        this.principalCIIU = principalCIIU;
    }
}
