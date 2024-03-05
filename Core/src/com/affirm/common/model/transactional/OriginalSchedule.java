package com.affirm.common.model.transactional;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by sTbn on 22/06/16.
 */
public class OriginalSchedule implements Serializable {

    public final static int OFFER = 1;
    public final static int CREDIT = 2;

    private Integer id;
    private Integer installmentId;
    private Date dueDate;
    private Double installmentFactor;
    private Double tedFactor;
    private Double remainingCapital;
    private Double installmentCapital;
    private Double interest;
    private Double interestTax;
    private Double installment;
    private Double collectionCommission;
    private Double collectionCommissionTax;
    private Double insurance;
    private Double installmentAmount;
    private Double carInsurance;
    private Date billingDate;
    private Double effectiveDailyRateFactor;

    public void fillFromDb(JSONObject json, int fl_schedule) throws Exception {

        switch (fl_schedule) {
            case OFFER:
                setId(JsonUtil.getIntFromJson(json, "loan_offer_id", null));
                break;
            case CREDIT:
                setId(JsonUtil.getIntFromJson(json, "credit_id", null));
                break;
        }

        setInstallmentId(JsonUtil.getIntFromJson(json, "installment_id", null));
        setDueDate(JsonUtil.getPostgresDateFromJson(json, "due_date", null));
        setInstallmentFactor(JsonUtil.getDoubleFromJson(json, "installment_factor", null));
        setTedFactor(JsonUtil.getDoubleFromJson(json, "effective_daily_rate_factor", null));
        setRemainingCapital(JsonUtil.getDoubleFromJson(json, "remaining_capital", null));
        setInstallmentCapital(JsonUtil.getDoubleFromJson(json, "installment_capital", null));
        setInterest(JsonUtil.getDoubleFromJson(json, "interest", null));
        setInterestTax(JsonUtil.getDoubleFromJson(json, "interest_tax", null));
        setInstallment(JsonUtil.getDoubleFromJson(json, "installment", null));
        setCollectionCommission(JsonUtil.getDoubleFromJson(json, "collection_commission", null));
        setCollectionCommissionTax(JsonUtil.getDoubleFromJson(json, "collection_commission_tax", null));
        setInsurance(JsonUtil.getDoubleFromJson(json, "insurance", null));
        setInstallmentAmount(JsonUtil.getDoubleFromJson(json, "installment_amount", null));
        setCarInsurance(JsonUtil.getDoubleFromJson(json, "car_insurance", null));
        setBillingDate(JsonUtil.getPostgresDateFromJson(json, "billing_date", null));
        setEffectiveDailyRateFactor(JsonUtil.getDoubleFromJson(json, "effective_daily_rate_factor", null));
    }

    public static int getOFFER() {
        return OFFER;
    }

    public static int getCREDIT() {
        return CREDIT;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getInstallmentId() {
        return installmentId;
    }

    public void setInstallmentId(Integer installmentId) {
        this.installmentId = installmentId;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public Double getInstallmentFactor() {
        return installmentFactor;
    }

    public void setInstallmentFactor(Double installmentFactor) {
        this.installmentFactor = installmentFactor;
    }

    public Double getTedFactor() {
        return tedFactor;
    }

    public void setTedFactor(Double tedFactor) {
        this.tedFactor = tedFactor;
    }

    public Double getRemainingCapital() {
        return remainingCapital;
    }

    public void setRemainingCapital(Double remainingCapital) {
        this.remainingCapital = remainingCapital;
    }

    public Double getInstallmentCapital() {
        return installmentCapital;
    }

    public void setInstallmentCapital(Double installmentCapital) {
        this.installmentCapital = installmentCapital;
    }

    public Double getInterest() {
        return interest;
    }

    public void setInterest(Double interest) {
        this.interest = interest;
    }

    public Double getInterestTax() {
        return interestTax;
    }

    public void setInterestTax(Double interestTax) {
        this.interestTax = interestTax;
    }

    public Double getInstallment() {
        return installment;
    }

    public void setInstallment(Double installment) {
        this.installment = installment;
    }

    public Double getCollectionCommission() {
        return collectionCommission;
    }

    public void setCollectionCommission(Double collectionCommission) {
        this.collectionCommission = collectionCommission;
    }

    public Double getCollectionCommissionTax() {
        return collectionCommissionTax;
    }

    public void setCollectionCommissionTax(Double collectionCommissionTax) {
        this.collectionCommissionTax = collectionCommissionTax;
    }

    public Double getInsurance() {
        return insurance;
    }

    public void setInsurance(Double insurance) {
        this.insurance = insurance;
    }

    public Double getInstallmentAmount() {
        return installmentAmount;
    }

    public void setInstallmentAmount(Double installmentAmount) {
        this.installmentAmount = installmentAmount;
    }

    public Double getTotalInterest() {
        return (interest != null ? interest : 0) + (interestTax != null ? interestTax : 0);
    }

    public Double getTotalCollectionCommission() {
        return (collectionCommission != null ? collectionCommission : 0) + (collectionCommissionTax != null ? collectionCommissionTax : 0);
    }

    public Double getCarInsurance() {
        return carInsurance;
    }

    public void setCarInsurance(Double carInsurance) {
        this.carInsurance = carInsurance;
    }

    public Date getBillingDate() {
        return billingDate;
    }

    public void setBillingDate(Date billingDate) {
        this.billingDate = billingDate;
    }

    public Double getEffectiveDailyRateFactor() {
        return effectiveDailyRateFactor;
    }

    public void setEffectiveDailyRateFactor(Double effectiveDailyRateFactor) {
        this.effectiveDailyRateFactor = effectiveDailyRateFactor;
    }

    @Override
    public String toString() {
        return "OriginalSchedule{" +
                "id=" + id +
                ", installmentId=" + installmentId +
                ", dueDate=" + dueDate +
                ", installmentFactor=" + installmentFactor +
                ", tedFactor=" + tedFactor +
                ", remainingCapital=" + remainingCapital +
                ", installmentCapital=" + installmentCapital +
                ", interest=" + interest +
                ", interestTax=" + interestTax +
                ", installment=" + installment +
                ", collectionCommission=" + collectionCommission +
                ", collectionCommissionTax=" + collectionCommissionTax +
                ", insurance=" + insurance +
                ", installmentAmount=" + installmentAmount +
                '}';
    }
}
