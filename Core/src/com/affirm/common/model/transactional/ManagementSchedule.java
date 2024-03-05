package com.affirm.common.model.transactional;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * Created by sTbn on 5/08/16.
 */
public class ManagementSchedule implements Serializable {

    public static final int STATUS_PENDING = 1;
    public static final int STATUS_PARCIAL = 2;
    public static final int STATUS_MORA = 3;
    public static final int STATUS_PAYED = 4;

    private Integer creditId;
    private Integer installmentId;
    private Date dueDate;
    private Integer installmentStatusId;
    private Double installmentCapital;
    private Double interest;
    private Double interestTax;
    private Double moratoriumInterest;
    private Double moratoriumInterestTax;
    private Double moratoriumCharge;
    private Double moratoriumChargeTax;
    private Double collectionCommission;
    private Double collectionCommissionTax;
    private Double insurance;
    private Double carInsurance;
    private Double installmentAmount;
    private Double pendingInstallmentCapital;
    private Double pendingInterest;
    private Double pendingInterestTax;
    private Double pendingMoratoriumInterest;
    private Double pendingMoratoriumInterestTax;
    private Double pendingMoratoriumCharge;
    private Double pendingMoratoriumChargeTax;
    private Double pendingCollectionCommission;
    private Double pendingCollectionCommissionTax;
    private Double pendingInsurance;
    private Double pendingInstallmentAmount;
    private Double remainingCapital;
    private Boolean inArrears;
    private Date updatedDate;
    private Integer daysInArrears;
    private Integer daysAhead;


    public void fillFromDb(JSONObject json) throws Exception {
        setCreditId(JsonUtil.getIntFromJson(json, "credit_id", null));
        setInstallmentId(JsonUtil.getIntFromJson(json, "installment_id", null));
        setDueDate(JsonUtil.getPostgresDateFromJson(json, "due_date", null));
        setInstallmentStatusId(JsonUtil.getIntFromJson(json, "installment_status_id", null));
        setInstallmentCapital(JsonUtil.getDoubleFromJson(json, "installment_capital", null));
        setInterest(JsonUtil.getDoubleFromJson(json, "interest", null));
        setInterestTax(JsonUtil.getDoubleFromJson(json, "interest_tax", null));
        setMoratoriumInterest(JsonUtil.getDoubleFromJson(json, "moratorium_interest", null));
        setMoratoriumInterestTax(JsonUtil.getDoubleFromJson(json, "moratorium_interest_tax", null));
        setCollectionCommission(JsonUtil.getDoubleFromJson(json, "collection_commission", null));
        setCollectionCommissionTax(JsonUtil.getDoubleFromJson(json, "collection_commission_tax", null));
        setInsurance(JsonUtil.getDoubleFromJson(json, "insurance", null));
        setInstallmentAmount(JsonUtil.getDoubleFromJson(json, "installment_amount", null));
        setPendingInstallmentCapital(JsonUtil.getDoubleFromJson(json, "pending_installment_capital", null));
        setPendingInterest(JsonUtil.getDoubleFromJson(json, "pending_interest", null));
        setPendingInterestTax(JsonUtil.getDoubleFromJson(json, "pending_interest_tax", null));
        setPendingMoratoriumInterest(JsonUtil.getDoubleFromJson(json, "pending_moratorium_interest", null));
        setPendingMoratoriumInterestTax(JsonUtil.getDoubleFromJson(json, "pending_moratorium_interest_tax", null));
        setPendingCollectionCommission(JsonUtil.getDoubleFromJson(json, "pending_collection_commission", null));
        setPendingCollectionCommissionTax(JsonUtil.getDoubleFromJson(json, "pending_collection_commission_tax", null));
        setPendingInsurance(JsonUtil.getDoubleFromJson(json, "pending_insurance", null));
        setPendingInstallmentAmount(JsonUtil.getDoubleFromJson(json, "pending_installment_amount", null));
        setInArrears(JsonUtil.getBooleanFromJson(json, "in_arrears", null));
        setUpdatedDate(JsonUtil.getPostgresDateFromJson(json, "updated_date", null));
        setDaysInArrears(JsonUtil.getIntFromJson(json, "days_in_arrears", null));
        setDaysAhead(JsonUtil.getIntFromJson(json, "days_ahead", null));
        setMoratoriumCharge(JsonUtil.getDoubleFromJson(json, "moratorium_charge", null));
        setMoratoriumChargeTax(JsonUtil.getDoubleFromJson(json, "moratorium_charge_tax", null));
        setPendingMoratoriumCharge(JsonUtil.getDoubleFromJson(json, "pending_moratorium_charge", null));
        setPendingMoratoriumChargeTax(JsonUtil.getDoubleFromJson(json, "pending_moratorium_charge_tax", null));
        setRemainingCapital(JsonUtil.getDoubleFromJson(json, "remaining_capital", null));
        setCarInsurance(JsonUtil.getDoubleFromJson(json, "car_insurance", null));
    }

    public long daysLeftToDueDate() {
        return ChronoUnit.DAYS.between(LocalDate.now(), dueDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
    }

    public String getDueDateInReadableFormat() {
        return new SimpleDateFormat("dd 'de' MMMM").format(dueDate);
    }

    public Integer getCreditId() {
        return creditId;
    }

    public void setCreditId(Integer creditId) {
        this.creditId = creditId;
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

    public Integer getInstallmentStatusId() {
        return installmentStatusId;
    }

    public void setInstallmentStatusId(Integer installmentStatusId) {
        this.installmentStatusId = installmentStatusId;
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

    public Double getMoratoriumInterest() {
        return moratoriumInterest;
    }

    public void setMoratoriumInterest(Double moratoriumInterest) {
        this.moratoriumInterest = moratoriumInterest;
    }

    public Double getMoratoriumInterestTax() {
        return moratoriumInterestTax;
    }

    public void setMoratoriumInterestTax(Double moratoriumInterestTax) {
        this.moratoriumInterestTax = moratoriumInterestTax;
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

    public Double getPendingInstallmentCapital() {
        return pendingInstallmentCapital;
    }

    public void setPendingInstallmentCapital(Double pendingInstallmentCapital) {
        this.pendingInstallmentCapital = pendingInstallmentCapital;
    }

    public Double getPendingInterest() {
        return pendingInterest;
    }

    public void setPendingInterest(Double pendingInterest) {
        this.pendingInterest = pendingInterest;
    }

    public Double getPendingInterestTax() {
        return pendingInterestTax;
    }

    public void setPendingInterestTax(Double pendingInterestTax) {
        this.pendingInterestTax = pendingInterestTax;
    }

    public Double getPendingMoratoriumInterest() {
        return pendingMoratoriumInterest;
    }

    public void setPendingMoratoriumInterest(Double pendingMoratoriumInterest) {
        this.pendingMoratoriumInterest = pendingMoratoriumInterest;
    }

    public Double getPendingMoratoriumInterestTax() {
        return pendingMoratoriumInterestTax;
    }

    public void setPendingMoratoriumInterestTax(Double pendingMoratoriumInterestTax) {
        this.pendingMoratoriumInterestTax = pendingMoratoriumInterestTax;
    }

    public Double getPendingCollectionCommission() {
        return pendingCollectionCommission;
    }

    public void setPendingCollectionCommission(Double pendingCollectionCommission) {
        this.pendingCollectionCommission = pendingCollectionCommission;
    }

    public Double getPendingCollectionCommissionTax() {
        return pendingCollectionCommissionTax;
    }

    public void setPendingCollectionCommissionTax(Double pendingCollectionCommissionTax) {
        this.pendingCollectionCommissionTax = pendingCollectionCommissionTax;
    }

    public Double getPendingInsurance() {
        return pendingInsurance;
    }

    public void setPendingInsurance(Double pendingInsurance) {
        this.pendingInsurance = pendingInsurance;
    }

    public Double getPendingInstallmentAmount() {
        return pendingInstallmentAmount;
    }

    public void setPendingInstallmentAmount(Double pendingInstallmentAmount) {
        this.pendingInstallmentAmount = pendingInstallmentAmount;
    }

    public Boolean getInArrears() {
        return inArrears;
    }

    public void setInArrears(Boolean inArrears) {
        this.inArrears = inArrears;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    public Integer getDaysInArrears() {
        return daysInArrears;
    }

    public void setDaysInArrears(Integer daysInArrears) {
        this.daysInArrears = daysInArrears;
    }

    public Double getTotalInterest() {
        return interest + interestTax;
    }

    public Double getTotalPendingInterest() {
        return pendingInterest + pendingInterestTax;
    }

    public Double getTotalMoratoriumInterest() {
        return moratoriumInterest + moratoriumInterestTax;
    }

    public Double getTotalPendingMoratoriumInterest() { return pendingMoratoriumInterest + pendingMoratoriumInterestTax; }

    public Double getTotalCollectionCommission() {
        return collectionCommission + collectionCommissionTax;
    }

    public Double getTotalPendingCollectionCommission() {
        return pendingCollectionCommission + getPendingCollectionCommissionTax();
    }

    public Integer getDaysAhead() {
        return daysAhead;
    }

    public void setDaysAhead(Integer daysAhead) {
        this.daysAhead = daysAhead;
    }

    public Double getMoratoriumCharge() {
        return moratoriumCharge;
    }

    public void setMoratoriumCharge(Double moratoriumCharge) {
        this.moratoriumCharge = moratoriumCharge;
    }

    public Double getMoratoriumChargeTax() {
        return moratoriumChargeTax;
    }

    public void setMoratoriumChargeTax(Double moratoriumChargeTax) {
        this.moratoriumChargeTax = moratoriumChargeTax;
    }

    public Double getPendingMoratoriumCharge() {
        return pendingMoratoriumCharge;
    }

    public void setPendingMoratoriumCharge(Double pendingMoratoriumCharge) {
        this.pendingMoratoriumCharge = pendingMoratoriumCharge;
    }

    public Double getPendingMoratoriumChargeTax() {
        return pendingMoratoriumChargeTax;
    }

    public void setPendingMoratoriumChargeTax(Double pendingMoratoriumChargeTax) {
        this.pendingMoratoriumChargeTax = pendingMoratoriumChargeTax;
    }

    public Double getRemainingCapital() {
        return remainingCapital;
    }

    public void setRemainingCapital(Double remainingCapital) {
        this.remainingCapital = remainingCapital;
    }

    public Double getTotalMoratoriumCharge() { return moratoriumCharge + moratoriumChargeTax; }

    public Double getTotalPendingMoratoriumCharge() { return pendingMoratoriumCharge + pendingMoratoriumChargeTax; }

    public Double getCarInsurance() {
        return carInsurance;
    }

    public void setCarInsurance(Double carInsurance) {
        this.carInsurance = carInsurance;
    }
}
