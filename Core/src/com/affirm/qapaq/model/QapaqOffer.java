package com.affirm.qapaq.model;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

public class QapaqOffer {

    public enum SouceIncome {
        SALARIO("SALARIO"),
        MICROEMPRESAS("MICROEMPRESAS"),
        RENTAS("RENTAS");

        private String key;

        SouceIncome(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }
    }

    private String documentNumber;
    private String personName;
    private String productName;
    private String clientType;
    private String campaign;
    private int campaignAmount;
    private int installment;
    private double rate;
    private double quota;
    private double debtBalance;
    private double tea;
    private double tem;
    private String slaControl;

    public QapaqOffer() {

    }

    public void fillFromJson(JSONObject json) throws Exception {
        setDocumentNumber(JsonUtil.getStringFromJson(json, "Identificacion", null));
        setPersonName(JsonUtil.getStringFromJson(json, "NombreCliente", null));
        setProductName(JsonUtil.getStringFromJson(json, "Producto", null));
        setClientType(JsonUtil.getStringFromJson(json, "TipoCliente", null));
        setCampaign(JsonUtil.getStringFromJson(json, "Campania", null));
        setCampaignAmount(JsonUtil.getIntFromJson(json, "MontoCampania", null));
        setInstallment(JsonUtil.getIntFromJson(json, "Plazo", null));
        setRate(JsonUtil.getDoubleFromJson(json, "Tasa", null));
        setQuota(JsonUtil.getDoubleFromJson(json, "Cuota", null));
        setDebtBalance(JsonUtil.getDoubleFromJson(json, "SaldoDeuda", null));
        setTea(JsonUtil.getDoubleFromJson(json, "TEA", null));
        setTem(JsonUtil.getDoubleFromJson(json, "TEM", null));
        setSlaControl(JsonUtil.getStringFromJson(json, "ControlSla", null));
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getClientType() {
        return clientType;
    }

    public void setClientType(String clientType) {
        this.clientType = clientType;
    }

    public String getCampaign() {
        return campaign;
    }

    public void setCampaign(String campaign) {
        this.campaign = campaign;
    }

    public int getCampaignAmount() {
        return campaignAmount;
    }

    public void setCampaignAmount(int campaignAmount) {
        this.campaignAmount = campaignAmount;
    }

    public int getInstallment() {
        return installment;
    }

    public void setInstallment(int installment) {
        this.installment = installment;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public double getQuota() {
        return quota;
    }

    public void setQuota(double quota) {
        this.quota = quota;
    }

    public double getDebtBalance() {
        return debtBalance;
    }

    public void setDebtBalance(double debtBalance) {
        this.debtBalance = debtBalance;
    }

    public double getTea() {
        return tea;
    }

    public void setTea(double tea) {
        this.tea = tea;
    }

    public double getTem() {
        return tem;
    }

    public void setTem(double tem) {
        this.tem = tem;
    }

    public String getSlaControl() {
        return slaControl;
    }

    public void setSlaControl(String slaControl) {
        this.slaControl = slaControl;
    }

    @Override
    public String toString() {
        return "QapaqOffer{" +
                "documentNumber='" + documentNumber + '\'' +
                ", personName='" + personName + '\'' +
                ", productName='" + productName + '\'' +
                ", clientType='" + clientType + '\'' +
                ", campaign='" + campaign + '\'' +
                ", campaignAmount=" + campaignAmount +
                ", installment=" + installment +
                ", rate=" + rate +
                ", quota=" + quota +
                ", debtBalance=" + debtBalance +
                ", tea=" + tea +
                ", tem=" + tem +
                ", slaControl='" + slaControl + '\'' +
                '}';
    }
}
