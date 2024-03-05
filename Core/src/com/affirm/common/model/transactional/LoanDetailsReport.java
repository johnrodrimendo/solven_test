package com.affirm.common.model.transactional;

import com.affirm.common.model.catalog.Product;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

/**
 * Created by dev5 on 22/03/18.
 */
public class LoanDetailsReport {

    private String creditCode;
    private int creditId;
    private int installmentId;
    private String dueDate;
    private String paymentDate;
    private int installmentStatus;
    private Product product;
    private double installmentCapital;
    private double interest;
    private double interestTax;
    private double moratoriumInterest;
    private double moratoriumInterestTax;
    private double collectionCommision;
    private double collectionCommisionTax;
    private double insurance;
    private double installmentAmount;
    private double pendingInstallmentCapital;
    private double pendingInterest;
    private double pendingInterestTax;
    private double pendingMoratoriumInterest;
    private double pendingMoratoriumInterestTax;
    private double pendingCollectionCommision;
    private double pendingCollectionCommisionTax;
    private double pendingInsurance;
    private double pendingInstallmentAmount;
    private boolean inArrears;
    private String updatedDate;
    private int daysInArrears;
    private String cancellationDate;
    private int tranche;
    private double moratoriumCharge;
    private double moratoriumChargeTax;
    private double pendingMoratoriumCharge;
    private double pendingMoratoriumChargeTax;
    private double remainingCapital;
    private double carInsurance;

    public void fillFromDb(JSONObject json, CatalogService catalogService) throws Exception {
        setCreditCode(JsonUtil.getStringFromJson(json, "credit_code", null));
        setCreditId(JsonUtil.getIntFromJson(json, "credit_id", null));
        if(JsonUtil.getIntFromJson(json, "product_id", null) != null)
            setProduct(catalogService.getProduct(JsonUtil.getIntFromJson(json, "product_id", null)));
        setInstallmentId(JsonUtil.getIntFromJson(json, "installment_id", null));
        setDueDate(JsonUtil.getStringFromJson(json, "due_date", null));
        setPaymentDate(JsonUtil.getStringFromJson(json, "payment_date", null));
        setInstallmentStatus(JsonUtil.getIntFromJson(json, "installment_status_id", null));
        setInstallmentCapital(JsonUtil.getDoubleFromJson(json, "installment_capital", null));
        setInterest(JsonUtil.getDoubleFromJson(json, "interest", null));
        setInterestTax(JsonUtil.getDoubleFromJson(json, "interest_tax", null));
        setMoratoriumInterest(JsonUtil.getDoubleFromJson(json, "moratorium_interest", null));
        setMoratoriumInterestTax(JsonUtil.getDoubleFromJson(json, "moratorium_interest_tax", null));
        setCollectionCommision(JsonUtil.getDoubleFromJson(json, "collection_commission", null));
        setCollectionCommisionTax(JsonUtil.getDoubleFromJson(json, "collection_commission_tax", null));
        setInsurance(JsonUtil.getDoubleFromJson(json, "insurance", null));
        setInstallmentAmount(JsonUtil.getDoubleFromJson(json, "installment_amount", null));
        setPendingInstallmentCapital(JsonUtil.getDoubleFromJson(json, "pending_installment_capital", null));
        setPendingInterest(JsonUtil.getDoubleFromJson(json, "pending_interest", null));
        setPendingInterestTax(JsonUtil.getDoubleFromJson(json, "pending_interest_tax", null));
        setPendingMoratoriumInterest(JsonUtil.getDoubleFromJson(json, "pending_moratorium_interest", null));
        setPendingMoratoriumInterestTax(JsonUtil.getDoubleFromJson(json, "pending_moratorium_interest_tax", null));
        setPendingCollectionCommision(JsonUtil.getDoubleFromJson(json, "pending_collection_commission", null));
        setPendingCollectionCommisionTax(JsonUtil.getDoubleFromJson(json, "pending_collection_commission_tax", null));
        setPendingInsurance(JsonUtil.getDoubleFromJson(json, "pending_insurance", null));
        setPendingInstallmentAmount(JsonUtil.getDoubleFromJson(json, "pending_installment_amount", null));
        setInArrears(JsonUtil.getBooleanFromJson(json, "in_arrears", null));
        setUpdatedDate(JsonUtil.getStringFromJson(json, "updated_date", null));
        setDaysInArrears(JsonUtil.getIntFromJson(json, "days_in_arrears", null));
        setCancellationDate(JsonUtil.getStringFromJson(json, "cancellation_date", null));
        if(JsonUtil.getIntFromJson(json, "tranche_id", null) != null)
            setTranche(JsonUtil.getIntFromJson(json, "tranche_id", null));
        setMoratoriumCharge(JsonUtil.getDoubleFromJson(json, "moratorium_charge", null));
        setMoratoriumChargeTax(JsonUtil.getDoubleFromJson(json, "moratorium_charge_tax", null));
        setPendingMoratoriumCharge(JsonUtil.getDoubleFromJson(json, "pending_moratorium_charge", null));
        setPendingMoratoriumChargeTax(JsonUtil.getDoubleFromJson(json, "pending_moratorium_charge_tax", null));
        setRemainingCapital(JsonUtil.getDoubleFromJson(json, "remaining_capital", null));
        if(JsonUtil.getDoubleFromJson(json, "car_insurance", null) != null)
            setCarInsurance(JsonUtil.getDoubleFromJson(json, "car_insurance", null));
    }

    public String getCreditCode() {
        return creditCode;
    }

    public void setCreditCode(String creditCode) {
        this.creditCode = creditCode;
    }

    public int getCreditId() {
        return creditId;
    }

    public void setCreditId(int creditId) {
        this.creditId = creditId;
    }

    public int getInstallmentId() {
        return installmentId;
    }

    public void setInstallmentId(int installmentId) {
        this.installmentId = installmentId;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public int getInstallmentStatus() {
        return installmentStatus;
    }

    public void setInstallmentStatus(int installmentStatus) {
        this.installmentStatus = installmentStatus;
    }

    public double getInstallmentCapital() {
        return installmentCapital;
    }

    public void setInstallmentCapital(double installmentCapital) {
        this.installmentCapital = installmentCapital;
    }

    public double getInterest() {
        return interest;
    }

    public void setInterest(double interest) {
        this.interest = interest;
    }

    public double getInterestTax() {
        return interestTax;
    }

    public void setInterestTax(double interestTax) {
        this.interestTax = interestTax;
    }

    public double getMoratoriumInterest() {
        return moratoriumInterest;
    }

    public void setMoratoriumInterest(double moratoriumInterest) {
        this.moratoriumInterest = moratoriumInterest;
    }

    public double getMoratoriumInterestTax() {
        return moratoriumInterestTax;
    }

    public void setMoratoriumInterestTax(double moratoriumInterestTax) {
        this.moratoriumInterestTax = moratoriumInterestTax;
    }

    public double getCollectionCommision() {
        return collectionCommision;
    }

    public void setCollectionCommision(double collectionCommision) {
        this.collectionCommision = collectionCommision;
    }

    public double getCollectionCommisionTax() {
        return collectionCommisionTax;
    }

    public void setCollectionCommisionTax(double collectionCommisionTax) {
        this.collectionCommisionTax = collectionCommisionTax;
    }

    public double getInsurance() {
        return insurance;
    }

    public void setInsurance(double insurance) {
        this.insurance = insurance;
    }

    public double getInstallmentAmount() {
        return installmentAmount;
    }

    public void setInstallmentAmount(double installmentAmount) {
        this.installmentAmount = installmentAmount;
    }

    public double getPendingInstallmentCapital() {
        return pendingInstallmentCapital;
    }

    public void setPendingInstallmentCapital(double pendingInstallmentCapital) {
        this.pendingInstallmentCapital = pendingInstallmentCapital;
    }

    public double getPendingInterest() {
        return pendingInterest;
    }

    public void setPendingInterest(double pendingInterest) {
        this.pendingInterest = pendingInterest;
    }

    public double getPendingInterestTax() {
        return pendingInterestTax;
    }

    public void setPendingInterestTax(double pendingInterestTax) {
        this.pendingInterestTax = pendingInterestTax;
    }

    public double getPendingMoratoriumInterest() {
        return pendingMoratoriumInterest;
    }

    public void setPendingMoratoriumInterest(double pendingMoratoriumInterest) {
        this.pendingMoratoriumInterest = pendingMoratoriumInterest;
    }

    public double getPendingMoratoriumInterestTax() {
        return pendingMoratoriumInterestTax;
    }

    public void setPendingMoratoriumInterestTax(double pendingMoratoriumInterestTax) {
        this.pendingMoratoriumInterestTax = pendingMoratoriumInterestTax;
    }

    public double getPendingCollectionCommision() {
        return pendingCollectionCommision;
    }

    public void setPendingCollectionCommision(double pendingCollectionCommision) {
        this.pendingCollectionCommision = pendingCollectionCommision;
    }

    public double getPendingCollectionCommisionTax() {
        return pendingCollectionCommisionTax;
    }

    public void setPendingCollectionCommisionTax(double pendingCollectionCommisionTax) {
        this.pendingCollectionCommisionTax = pendingCollectionCommisionTax;
    }

    public double getPendingInsurance() {
        return pendingInsurance;
    }

    public void setPendingInsurance(double pendingInsurance) {
        this.pendingInsurance = pendingInsurance;
    }

    public double getPendingInstallmentAmount() {
        return pendingInstallmentAmount;
    }

    public void setPendingInstallmentAmount(double pendingInstallmentAmount) {
        this.pendingInstallmentAmount = pendingInstallmentAmount;
    }

    public boolean isInArrears() {
        return inArrears;
    }

    public void setInArrears(boolean inArrears) {
        this.inArrears = inArrears;
    }

    public String getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(String updatedDate) {
        this.updatedDate = updatedDate;
    }

    public int getDaysInArrears() {
        return daysInArrears;
    }

    public void setDaysInArrears(int daysInArrears) {
        this.daysInArrears = daysInArrears;
    }

    public String getCancellationDate() {
        return cancellationDate;
    }

    public void setCancellationDate(String cancellationDate) {
        this.cancellationDate = cancellationDate;
    }

    public double getMoratoriumCharge() {
        return moratoriumCharge;
    }

    public void setMoratoriumCharge(double moratoriumCharge) {
        this.moratoriumCharge = moratoriumCharge;
    }

    public double getMoratoriumChargeTax() {
        return moratoriumChargeTax;
    }

    public void setMoratoriumChargeTax(double moratoriumChargeTax) {
        this.moratoriumChargeTax = moratoriumChargeTax;
    }

    public double getPendingMoratoriumCharge() {
        return pendingMoratoriumCharge;
    }

    public void setPendingMoratoriumCharge(double pendingMoratoriumCharge) {
        this.pendingMoratoriumCharge = pendingMoratoriumCharge;
    }

    public double getPendingMoratoriumChargeTax() {
        return pendingMoratoriumChargeTax;
    }

    public void setPendingMoratoriumChargeTax(double pendingMoratoriumChargeTax) {
        this.pendingMoratoriumChargeTax = pendingMoratoriumChargeTax;
    }

    public double getRemainingCapital() {
        return remainingCapital;
    }

    public void setRemainingCapital(double remainingCapital) {
        this.remainingCapital = remainingCapital;
    }

    public double getCarInsurance() {
        return carInsurance;
    }

    public void setCarInsurance(double carInsurance) {
        this.carInsurance = carInsurance;
    }

    public int getTranche() {
        return tranche;
    }

    public void setTranche(int tranche) {
        this.tranche = tranche;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
