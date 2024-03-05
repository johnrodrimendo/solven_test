package com.affirm.client.model.form;

import com.affirm.common.model.catalog.ConsolidationAccountType;
import com.affirm.common.model.transactional.ConsolidableDebt;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.*;

import java.io.Serializable;

/**
 * Created by john on 20/01/17.
 */
public class ConsolidableDebtForm extends FormGeneric implements Serializable {

    private Integer consolidationAccountType;
    private String entityCode;
    private Integer balance;
    private Integer balanceDE;
    private Integer balanceLP;
    private Double rate;
    private Double rateDE;
    private Double rateLP;
    private Integer pendingInstallments;

    public ConsolidableDebtForm() {
        this.setValidator(new ConsolidableDebtFormValidator());
    }

    public ConsolidableDebt newInstance() {
        return new ConsolidableDebt();
    }

    public class ConsolidableDebtFormValidator extends FormValidator implements Serializable {

        public IntegerFieldValidator consolidationAccountType;
        public StringFieldValidator entityCode;
        public IntegerFieldValidator balance;
        public IntegerFieldValidator balanceDE;
        public IntegerFieldValidator balanceLP;
        public DoubleFieldValidator rate;
        public DoubleFieldValidator rateDE;
        public DoubleFieldValidator rateLP;
        public IntegerFieldValidator pendingInstallments;

        public ConsolidableDebtFormValidator() {
            addValidator(consolidationAccountType = new IntegerFieldValidator(ValidatorUtil.CONSOLIDABLE_ACCOUNT_TYPE));
            addValidator(entityCode = new StringFieldValidator(ValidatorUtil.RCC_ENTITY_CODE));
            addValidator(balance = new IntegerFieldValidator(ValidatorUtil.CONSOLIDABLE_DEBT_BALANCE));
            addValidator(balanceDE = new IntegerFieldValidator(ValidatorUtil.CONSOLIDABLE_DEBT_BALANCE).setRequired(false));
            addValidator(balanceLP = new IntegerFieldValidator(ValidatorUtil.CONSOLIDABLE_DEBT_BALANCE).setRequired(false));
            addValidator(rate = new DoubleFieldValidator(ValidatorUtil.CONSOLIDABLE_DEBT_RATE));
            addValidator(rateDE = new DoubleFieldValidator(ValidatorUtil.CONSOLIDABLE_DEBT_RATE).setRequired(false));
            addValidator(rateLP = new DoubleFieldValidator(ValidatorUtil.CONSOLIDABLE_DEBT_RATE).setRequired(false));
            addValidator(pendingInstallments = new IntegerFieldValidator(ValidatorUtil.CONSOLIDABLE_DEBT_INSTALLMENTS).setRequired(false).setRequiredErrorMsg("validation.string.required.pendingInstallments"));
        }

        @Override
        protected void setDynamicValidations() {
            if (ConsolidableDebtForm.this.balanceDE != null && ConsolidableDebtForm.this.balanceDE > 0) {
                rateDE.setRequired(true);
            }
            if (ConsolidableDebtForm.this.balanceLP != null && ConsolidableDebtForm.this.balanceLP > 0) {
                rateLP.setRequired(true);
            }
            if (ConsolidableDebtForm.this.consolidationAccountType == ConsolidationAccountType.CREDITO_CONSUMO ||
                    ConsolidableDebtForm.this.consolidationAccountType == ConsolidationAccountType.CREDITO_NEGOCIO) {
                pendingInstallments.setRequired(true);
            } else {
                pendingInstallments.setRequired(false);
            }
        }

        @Override
        protected Object getSubclass() {
            return this;
        }

        @Override
        protected Object getFormClass() {
            return ConsolidableDebtForm.this;
        }
    }

    public ConsolidableDebt getConsolidableDebt(CatalogService catalog) throws Exception {
        ConsolidableDebt debt = new ConsolidableDebt();
        debt.setConsolidationAccounttype(getConsolidationAccountType());
        debt.setEntity(catalog.getRccEntity(getEntityCode()));
        debt.setBalance(getBalance());
        if (getBalanceDE() != null)
            debt.setBalanceDE(getBalanceDE());
        if (getBalanceLP() != null)
            debt.setBalanceLP(getBalanceLP());
        debt.setRate(getRate());
        debt.setRateDE(getRateDE());
        debt.setRateLP(getRateLP());
        debt.setInstallments(getPendingInstallments());
        return debt;
    }

    public Integer getConsolidationAccountType() {
        return consolidationAccountType;
    }

    public void setConsolidationAccountType(Integer consolidationAccountType) {
        this.consolidationAccountType = consolidationAccountType;
    }

    public String getEntityCode() {
        return entityCode;
    }

    public void setEntityCode(String entityCode) {
        this.entityCode = entityCode;
    }

    public Integer getBalance() {
        return balance;
    }

    public void setBalance(Integer balance) {
        this.balance = balance;
    }

    public Integer getBalanceDE() {
        return balanceDE;
    }

    public void setBalanceDE(Integer balanceDE) {
        this.balanceDE = balanceDE;
    }

    public Integer getBalanceLP() {
        return balanceLP;
    }

    public void setBalanceLP(Integer balanceLP) {
        this.balanceLP = balanceLP;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public Double getRateDE() {
        return rateDE;
    }

    public void setRateDE(Double rateDE) {
        this.rateDE = rateDE;
    }

    public Double getRateLP() {
        return rateLP;
    }

    public void setRateLP(Double rateLP) {
        this.rateLP = rateLP;
    }

    public Integer getPendingInstallments() {
        return pendingInstallments;
    }

    public void setPendingInstallments(Integer pendingInstallments) {
        this.pendingInstallments = pendingInstallments;
    }
}
