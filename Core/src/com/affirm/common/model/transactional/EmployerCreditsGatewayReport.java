package com.affirm.common.model.transactional;

import com.affirm.common.model.catalog.Employer;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

/**
 * Created by dev5 on 19/06/17.
 */
public class EmployerCreditsGatewayReport {

    private Employer employer;
    private Double amount;
    private Double loanCommission;
    private Double loanCommissionWithoutIgv;
    private Double pendingInstallmentAmount;
    private Double pendingInstallmentAmountWithoutIgv;
    private Double totalAmount;
    private Double igv;

    public void fillFromDb(JSONObject json, CatalogService catalogService){
        if(JsonUtil.getIntFromJson(json, "employer_id", null) != null)
            setEmployer(catalogService.getEmployer(JsonUtil.getIntFromJson(json, "employer_id", null)));
        setAmount(JsonUtil.getDoubleFromJson(json, "amount", 0.0));
        setLoanCommission(JsonUtil.getDoubleFromJson(json, "loan_commission", 0.0));
        setLoanCommissionWithoutIgv(JsonUtil.getDoubleFromJson(json, "loan_commission_wo_igv", 0.0));
        setPendingInstallmentAmount(JsonUtil.getDoubleFromJson(json, "pending_installment_amount", 0.0));
        setPendingInstallmentAmountWithoutIgv(JsonUtil.getDoubleFromJson(json, "pending_installment_amount_wo_igv", 0.0));
        setTotalAmount(JsonUtil.getDoubleFromJson(json, "total_amount", 0.0));
        setIgv(JsonUtil.getDoubleFromJson(json, "igv", 0.0));
    }

    public Employer getEmployer() {
        return employer;
    }

    public void setEmployer(Employer employer) {
        this.employer = employer;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getLoanCommission() {
        return loanCommission;
    }

    public void setLoanCommission(Double loanCommission) {
        this.loanCommission = loanCommission;
    }

    public Double getLoanCommissionWithoutIgv() {
        return loanCommissionWithoutIgv;
    }

    public void setLoanCommissionWithoutIgv(Double loanCommissionWithoutIgv) {
        this.loanCommissionWithoutIgv = loanCommissionWithoutIgv;
    }

    public Double getPendingInstallmentAmount() {
        return pendingInstallmentAmount;
    }

    public void setPendingInstallmentAmount(Double pendingInstallmentAmount) {
        this.pendingInstallmentAmount = pendingInstallmentAmount;
    }

    public Double getPendingInstallmentAmountWithoutIgv() {
        return pendingInstallmentAmountWithoutIgv;
    }

    public void setPendingInstallmentAmountWithoutIgv(Double pendingInstallmentAmountWithoutIgv) {
        this.pendingInstallmentAmountWithoutIgv = pendingInstallmentAmountWithoutIgv;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Double getIgv() {
        return igv;
    }

    public void setIgv(Double igv) {
        this.igv = igv;
    }
}
