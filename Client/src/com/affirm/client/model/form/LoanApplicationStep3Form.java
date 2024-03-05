/**
 *
 */
package com.affirm.client.model.form;

import com.affirm.common.model.catalog.ActivityType;
import com.affirm.common.util.*;

import java.io.Serializable;

/**
 * @author jrodriguez
 */
public class LoanApplicationStep3Form extends FormGeneric implements Serializable {

    private Integer principalActivityType;
    private String principalDependentRuc;
    private String principalDependentCompany;
    private String principalDependentTime;
    private Integer principalDependentGrossIncome;
    private Integer principalDependentNetIncome;
    private String principalDependentPhoneNumber;
    private String principalIndependentRuc;
    private String principalIndependentRegime;
    private Integer principalIndependentVouchers;
    private String principalIndependentTime;
    private Integer principalIndependentGrossIncome;
    private Integer principalIndependentNetIncome;
    private String principalRentierRuc;
    private Integer principalRentierBelonging;
    private String principalRentierTime;
    private Integer principalRentierGrossIncome;
    private Integer principalRentierNetIncome;
    private String principalShareholderRuc;
    private String principalShareholderCompany;
    private String principalShareholderShareholding;
    private Integer principalShareholderResultU12M;
    private Integer principalShareholderNetIncome;
    private Integer principalPensionerFrom;
    private Integer principalPensionerNetIncome;
    private String principalHousekeeperEmployer;
    private String principalHousekeeperTime;
    private Integer principalHousekeeperNetIncome;
    private Integer secundaryActivityType;
    private String secundaryDependentRuc;
    private String secundaryDependentCompany;
    private String secundaryDependentTime;
    private Integer secundaryDependentGrossIncome;
    private Integer secundaryDependentNetIncome;
    private String secundaryDependentPhoneNumber;
    private String secundaryIndependentRuc;
    private String secundaryIndependentRegime;
    private Integer secundaryIndependentVouchers;
    private String secundaryIndependentTime;
    private Integer secundaryIndependentGrossIncome;
    private Integer secundaryIndependentNetIncome;
    private String secundaryRentierRuc;
    private Integer secundaryRentierBelonging;
    private String secundaryRentierTime;
    private Integer secundaryRentierGrossIncome;
    private Integer secundaryRentierNetIncome;
    private String secundaryShareholderRuc;
    private String secundaryShareholderCompany;
    private String secundaryShareholderShareholding;
    private Integer secundaryShareholderResultU12M;
    private Integer secundaryShareholderNetIncome;
    private Integer secundaryPensionerFrom;
    private Integer secundaryPensionerNetIncome;
    private String secundaryHousekeeperEmployer;
    private String secundaryHousekeeperTime;
    private Integer secundaryHousekeeperNetIncome;
    private Integer otherIncomes;

    public LoanApplicationStep3Form() {
        this.setValidator(new LoanApplicationStep3FormValidator());
    }

    public class LoanApplicationStep3FormValidator extends FormValidator implements Serializable {

        public IntegerFieldValidator principalActivityType;
        public StringFieldValidator principalDependentRuc;
        public StringFieldValidator principalDependentCompany;
        public StringFieldValidator principalDependentTime;
        public IntegerFieldValidator principalDependentGrossIncome;
        public IntegerFieldValidator principalDependentNetIncome;
        public StringFieldValidator principalDependentPhoneNumber;
        public StringFieldValidator principalIndependentRuc;
        public StringFieldValidator principalIndependentRegime;
        public IntegerFieldValidator principalIndependentVouchers;
        public StringFieldValidator principalIndependentTime;
        public IntegerFieldValidator principalIndependentGrossIncome;
        public IntegerFieldValidator principalIndependentNetIncome;
        public StringFieldValidator principalRentierRuc;
        public IntegerFieldValidator principalRentierBelonging;
        public StringFieldValidator principalRentierTime;
        public IntegerFieldValidator principalRentierGrossIncome;
        public IntegerFieldValidator principalRentierNetIncome;
        public StringFieldValidator principalShareholderRuc;
        public StringFieldValidator principalShareholderCompany;
        public DoubleFieldValidator principalShareholderShareholding;
        public IntegerFieldValidator principalShareholderResultU12M;
        public IntegerFieldValidator principalShareholderNetIncome;
        public IntegerFieldValidator principalPensionerFrom;
        public IntegerFieldValidator principalPensionerNetIncome;
        public StringFieldValidator principalHousekeeperEmployer;
        public StringFieldValidator principalHousekeeperTime;
        public IntegerFieldValidator principalHousekeeperNetIncome;
        public IntegerFieldValidator secundaryActivityType;
        public StringFieldValidator secundaryDependentRuc;
        public StringFieldValidator secundaryDependentCompany;
        public StringFieldValidator secundaryDependentTime;
        public IntegerFieldValidator secundaryDependentGrossIncome;
        public IntegerFieldValidator secundaryDependentNetIncome;
        public StringFieldValidator secundaryDependentPhoneNumber;
        public StringFieldValidator secundaryIndependentRuc;
        public StringFieldValidator secundaryIndependentRegime;
        public IntegerFieldValidator secundaryIndependentVouchers;
        public StringFieldValidator secundaryIndependentTime;
        public IntegerFieldValidator secundaryIndependentGrossIncome;
        public IntegerFieldValidator secundaryIndependentNetIncome;
        public StringFieldValidator secundaryRentierRuc;
        public IntegerFieldValidator secundaryRentierBelonging;
        public StringFieldValidator secundaryRentierTime;
        public IntegerFieldValidator secundaryRentierGrossIncome;
        public IntegerFieldValidator secundaryRentierNetIncome;
        public StringFieldValidator secundaryShareholderRuc;
        public StringFieldValidator secundaryShareholderCompany;
        public DoubleFieldValidator secundaryShareholderShareholding;
        public IntegerFieldValidator secundaryShareholderResultU12M;
        public IntegerFieldValidator secundaryShareholderNetIncome;
        public IntegerFieldValidator secundaryPensionerFrom;
        public IntegerFieldValidator secundaryPensionerNetIncome;
        public StringFieldValidator secundaryHousekeeperEmployer;
        public StringFieldValidator secundaryHousekeeperTime;
        public IntegerFieldValidator secundaryHousekeeperNetIncome;
        public IntegerFieldValidator otherIncomes;

        public LoanApplicationStep3FormValidator() {
            addValidator(principalActivityType = new IntegerFieldValidator(ValidatorUtil.ACTIVITY_TYPE_ID));
            addValidator(principalDependentRuc = new StringFieldValidator(ValidatorUtil.RUC));
            addValidator(principalDependentCompany = new StringFieldValidator(ValidatorUtil.COMPANY_NAME));
            addValidator(principalDependentTime = new StringFieldValidator(ValidatorUtil.ACTIVITY_TIME));
            addValidator(principalDependentGrossIncome = new IntegerFieldValidator(ValidatorUtil.GROSS_INCOME));
            addValidator(principalDependentNetIncome = new IntegerFieldValidator(ValidatorUtil.NET_INCOME));
            addValidator(principalDependentPhoneNumber = new StringFieldValidator(ValidatorUtil.PHONE_OR_CELLPHONE));
            addValidator(principalIndependentRuc = new StringFieldValidator(ValidatorUtil.RUC));
            addValidator(principalIndependentRegime = new StringFieldValidator(ValidatorUtil.REGIME));
            addValidator(principalIndependentVouchers = new IntegerFieldValidator(ValidatorUtil.VOUCHER_TYPE_ID));
            addValidator(principalIndependentTime = new StringFieldValidator(ValidatorUtil.ACTIVITY_TIME));
            addValidator(principalIndependentGrossIncome = new IntegerFieldValidator(ValidatorUtil.GROSS_INCOME));
            addValidator(principalIndependentNetIncome = new IntegerFieldValidator(ValidatorUtil.NET_INCOME));
            addValidator(principalRentierRuc = new StringFieldValidator(ValidatorUtil.RUC));
            addValidator(principalRentierBelonging = new IntegerFieldValidator(ValidatorUtil.RENTIER_BELONGING));
            addValidator(principalRentierTime = new StringFieldValidator(ValidatorUtil.ACTIVITY_TIME));
            addValidator(principalRentierGrossIncome = new IntegerFieldValidator(ValidatorUtil.GROSS_INCOME));
            addValidator(principalRentierNetIncome = new IntegerFieldValidator(ValidatorUtil.NET_INCOME));
            addValidator(principalShareholderRuc = new StringFieldValidator(ValidatorUtil.RUC));
            addValidator(principalShareholderCompany = new StringFieldValidator(ValidatorUtil.COMPANY_NAME));
            addValidator(principalShareholderShareholding = new DoubleFieldValidator(ValidatorUtil.SHAREHOLDING));
            addValidator(principalShareholderResultU12M = new IntegerFieldValidator(ValidatorUtil.RESULT_U12M));
            addValidator(principalShareholderNetIncome = new IntegerFieldValidator(ValidatorUtil.NET_INCOME));
            addValidator(principalPensionerFrom = new IntegerFieldValidator(ValidatorUtil.PENSIONER_FROM_ID));
            addValidator(principalPensionerNetIncome = new IntegerFieldValidator(ValidatorUtil.NET_INCOME));
            addValidator(principalHousekeeperEmployer = new StringFieldValidator(ValidatorUtil.HOUSEKEEPER_EMPLOYER));
            addValidator(principalHousekeeperTime = new StringFieldValidator(ValidatorUtil.ACTIVITY_TIME));
            addValidator(principalHousekeeperNetIncome = new IntegerFieldValidator(ValidatorUtil.NET_INCOME));
            addValidator(secundaryActivityType = new IntegerFieldValidator(ValidatorUtil.ACTIVITY_TYPE_ID).setRequired(false));
            addValidator(secundaryDependentRuc = new StringFieldValidator(ValidatorUtil.RUC));
            addValidator(secundaryDependentCompany = new StringFieldValidator(ValidatorUtil.COMPANY_NAME));
            addValidator(secundaryDependentTime = new StringFieldValidator(ValidatorUtil.ACTIVITY_TIME));
            addValidator(secundaryDependentGrossIncome = new IntegerFieldValidator(ValidatorUtil.GROSS_INCOME));
            addValidator(secundaryDependentNetIncome = new IntegerFieldValidator(ValidatorUtil.NET_INCOME));
            addValidator(secundaryDependentPhoneNumber = new StringFieldValidator(ValidatorUtil.PHONE_OR_CELLPHONE));
            addValidator(secundaryIndependentRuc = new StringFieldValidator(ValidatorUtil.RUC));
            addValidator(secundaryIndependentRegime = new StringFieldValidator(ValidatorUtil.REGIME));
            addValidator(secundaryIndependentVouchers = new IntegerFieldValidator(ValidatorUtil.VOUCHER_TYPE_ID));
            addValidator(secundaryIndependentTime = new StringFieldValidator(ValidatorUtil.ACTIVITY_TIME));
            addValidator(secundaryIndependentGrossIncome = new IntegerFieldValidator(ValidatorUtil.GROSS_INCOME));
            addValidator(secundaryIndependentNetIncome = new IntegerFieldValidator(ValidatorUtil.NET_INCOME));
            addValidator(secundaryRentierRuc = new StringFieldValidator(ValidatorUtil.RUC));
            addValidator(secundaryRentierBelonging = new IntegerFieldValidator(ValidatorUtil.RENTIER_BELONGING));
            addValidator(secundaryRentierTime = new StringFieldValidator(ValidatorUtil.ACTIVITY_TIME));
            addValidator(secundaryRentierGrossIncome = new IntegerFieldValidator(ValidatorUtil.GROSS_INCOME));
            addValidator(secundaryRentierNetIncome = new IntegerFieldValidator(ValidatorUtil.NET_INCOME));
            addValidator(secundaryShareholderRuc = new StringFieldValidator(ValidatorUtil.RUC));
            addValidator(secundaryShareholderCompany = new StringFieldValidator(ValidatorUtil.COMPANY_NAME));
            addValidator(secundaryShareholderShareholding = new DoubleFieldValidator(ValidatorUtil.SHAREHOLDING));
            addValidator(secundaryShareholderResultU12M = new IntegerFieldValidator(ValidatorUtil.RESULT_U12M));
            addValidator(secundaryShareholderNetIncome = new IntegerFieldValidator(ValidatorUtil.NET_INCOME));
            addValidator(secundaryPensionerFrom = new IntegerFieldValidator(ValidatorUtil.PENSIONER_FROM_ID));
            addValidator(secundaryPensionerNetIncome = new IntegerFieldValidator(ValidatorUtil.NET_INCOME));
            addValidator(secundaryHousekeeperEmployer = new StringFieldValidator(ValidatorUtil.HOUSEKEEPER_EMPLOYER));
            addValidator(secundaryHousekeeperTime = new StringFieldValidator(ValidatorUtil.ACTIVITY_TIME));
            addValidator(secundaryHousekeeperNetIncome = new IntegerFieldValidator(ValidatorUtil.NET_INCOME));
            addValidator(otherIncomes = new IntegerFieldValidator(ValidatorUtil.OTHER_INCOME).setRequired(false));
        }

        @Override
        protected void setDynamicValidations() {
            principalDependentRuc.setRequired(LoanApplicationStep3Form.this.principalActivityType == ActivityType.DEPENDENT);
            principalDependentCompany.setRequired(LoanApplicationStep3Form.this.principalActivityType == ActivityType.DEPENDENT);
            principalDependentTime.setRequired(LoanApplicationStep3Form.this.principalActivityType == ActivityType.DEPENDENT);
            principalDependentPhoneNumber.setRequired(LoanApplicationStep3Form.this.principalActivityType == ActivityType.DEPENDENT);
            principalDependentGrossIncome.setRequired(LoanApplicationStep3Form.this.principalActivityType == ActivityType.DEPENDENT);
            principalDependentNetIncome.setRequired(LoanApplicationStep3Form.this.principalActivityType == ActivityType.DEPENDENT);
            if (LoanApplicationStep3Form.this.principalActivityType == ActivityType.DEPENDENT) {
                principalDependentNetIncome.setMaxValue(LoanApplicationStep3Form.this.principalDependentGrossIncome);
            }
            principalIndependentRuc.setRequired(LoanApplicationStep3Form.this.principalActivityType == ActivityType.INDEPENDENT);
            principalIndependentRegime.setRequired(LoanApplicationStep3Form.this.principalActivityType == ActivityType.INDEPENDENT);
            principalIndependentVouchers.setRequired(LoanApplicationStep3Form.this.principalActivityType == ActivityType.INDEPENDENT);
            principalIndependentTime.setRequired(LoanApplicationStep3Form.this.principalActivityType == ActivityType.INDEPENDENT);
            principalIndependentGrossIncome.setRequired(LoanApplicationStep3Form.this.principalActivityType == ActivityType.INDEPENDENT);
            principalIndependentNetIncome.setRequired(LoanApplicationStep3Form.this.principalActivityType == ActivityType.INDEPENDENT);
            if (LoanApplicationStep3Form.this.principalActivityType == ActivityType.INDEPENDENT) {
                principalIndependentNetIncome.setMaxValue(LoanApplicationStep3Form.this.principalIndependentGrossIncome);
            }
            principalRentierRuc.setRequired(LoanApplicationStep3Form.this.principalActivityType == ActivityType.RENTIER);
            principalRentierBelonging.setRequired(LoanApplicationStep3Form.this.principalActivityType == ActivityType.RENTIER);
            principalRentierTime.setRequired(LoanApplicationStep3Form.this.principalActivityType == ActivityType.RENTIER);
            principalRentierGrossIncome.setRequired(LoanApplicationStep3Form.this.principalActivityType == ActivityType.RENTIER);
            principalRentierNetIncome.setRequired(LoanApplicationStep3Form.this.principalActivityType == ActivityType.RENTIER);
            if (LoanApplicationStep3Form.this.principalActivityType == ActivityType.RENTIER) {
                principalRentierNetIncome.setMaxValue(LoanApplicationStep3Form.this.principalRentierGrossIncome);
            }
            principalShareholderRuc.setRequired(LoanApplicationStep3Form.this.principalActivityType == ActivityType.STOCKHOLDER);
            principalShareholderCompany.setRequired(LoanApplicationStep3Form.this.principalActivityType == ActivityType.STOCKHOLDER);
            principalShareholderShareholding.setRequired(LoanApplicationStep3Form.this.principalActivityType == ActivityType.STOCKHOLDER);
            principalShareholderResultU12M.setRequired(LoanApplicationStep3Form.this.principalActivityType == ActivityType.STOCKHOLDER);
            principalShareholderNetIncome.setRequired(LoanApplicationStep3Form.this.principalActivityType == ActivityType.STOCKHOLDER);

            principalPensionerFrom.setRequired(LoanApplicationStep3Form.this.principalActivityType == ActivityType.PENSIONER);
            principalPensionerNetIncome.setRequired(LoanApplicationStep3Form.this.principalActivityType == ActivityType.PENSIONER);

            principalHousekeeperEmployer.setRequired(LoanApplicationStep3Form.this.principalActivityType == ActivityType.HOUSEKEEPER);
            principalHousekeeperTime.setRequired(LoanApplicationStep3Form.this.principalActivityType == ActivityType.HOUSEKEEPER);
            principalHousekeeperNetIncome.setRequired(LoanApplicationStep3Form.this.principalActivityType == ActivityType.HOUSEKEEPER);

            boolean secundaryActivityExist = LoanApplicationStep3Form.this.secundaryActivityType != null;

            secundaryDependentRuc.setRequired(secundaryActivityExist && LoanApplicationStep3Form.this.secundaryActivityType == ActivityType.DEPENDENT);
            secundaryDependentCompany.setRequired(secundaryActivityExist && LoanApplicationStep3Form.this.secundaryActivityType == ActivityType.DEPENDENT);
            secundaryDependentTime.setRequired(secundaryActivityExist && LoanApplicationStep3Form.this.secundaryActivityType == ActivityType.DEPENDENT);
            secundaryDependentPhoneNumber.setRequired(secundaryActivityExist && LoanApplicationStep3Form.this.secundaryActivityType == ActivityType.DEPENDENT);
            secundaryDependentGrossIncome.setRequired(secundaryActivityExist && LoanApplicationStep3Form.this.secundaryActivityType == ActivityType.DEPENDENT);
            secundaryDependentNetIncome.setRequired(secundaryActivityExist && LoanApplicationStep3Form.this.secundaryActivityType == ActivityType.DEPENDENT);
            if (secundaryActivityExist && LoanApplicationStep3Form.this.secundaryActivityType == ActivityType.DEPENDENT) {
                secundaryDependentNetIncome.setMaxValue(LoanApplicationStep3Form.this.secundaryDependentGrossIncome);
            }
            secundaryIndependentRuc.setRequired(secundaryActivityExist && LoanApplicationStep3Form.this.secundaryActivityType == ActivityType.INDEPENDENT);
            secundaryIndependentRegime.setRequired(LoanApplicationStep3Form.this.secundaryActivityType == ActivityType.INDEPENDENT);
            secundaryIndependentVouchers.setRequired(secundaryActivityExist && LoanApplicationStep3Form.this.secundaryActivityType == ActivityType.INDEPENDENT);
            secundaryIndependentTime.setRequired(secundaryActivityExist && LoanApplicationStep3Form.this.secundaryActivityType == ActivityType.INDEPENDENT);
            secundaryIndependentGrossIncome.setRequired(secundaryActivityExist && LoanApplicationStep3Form.this.secundaryActivityType == ActivityType.INDEPENDENT);
            secundaryIndependentNetIncome.setRequired(secundaryActivityExist && LoanApplicationStep3Form.this.secundaryActivityType == ActivityType.INDEPENDENT);
            if (secundaryActivityExist && LoanApplicationStep3Form.this.secundaryActivityType == ActivityType.INDEPENDENT) {
                secundaryIndependentNetIncome.setMaxValue(LoanApplicationStep3Form.this.secundaryIndependentGrossIncome);
            }
            secundaryRentierRuc.setRequired(secundaryActivityExist && LoanApplicationStep3Form.this.secundaryActivityType == ActivityType.RENTIER);
            secundaryRentierBelonging.setRequired(secundaryActivityExist && LoanApplicationStep3Form.this.secundaryActivityType == ActivityType.RENTIER);
            secundaryRentierTime.setRequired(secundaryActivityExist && LoanApplicationStep3Form.this.secundaryActivityType == ActivityType.RENTIER);
            secundaryRentierGrossIncome.setRequired(secundaryActivityExist && LoanApplicationStep3Form.this.secundaryActivityType == ActivityType.RENTIER);
            secundaryRentierNetIncome.setRequired(secundaryActivityExist && LoanApplicationStep3Form.this.secundaryActivityType == ActivityType.RENTIER);
            if (secundaryActivityExist && LoanApplicationStep3Form.this.secundaryActivityType == ActivityType.RENTIER) {
                secundaryRentierNetIncome.setMaxValue(LoanApplicationStep3Form.this.secundaryRentierGrossIncome);
            }
            secundaryShareholderRuc.setRequired(secundaryActivityExist && LoanApplicationStep3Form.this.secundaryActivityType == ActivityType.STOCKHOLDER);
            secundaryShareholderCompany.setRequired(secundaryActivityExist && LoanApplicationStep3Form.this.secundaryActivityType == ActivityType.STOCKHOLDER);
            secundaryShareholderShareholding.setRequired(secundaryActivityExist && LoanApplicationStep3Form.this.secundaryActivityType == ActivityType.STOCKHOLDER);
            secundaryShareholderResultU12M.setRequired(secundaryActivityExist && LoanApplicationStep3Form.this.secundaryActivityType == ActivityType.STOCKHOLDER);
            secundaryShareholderNetIncome.setRequired(secundaryActivityExist && LoanApplicationStep3Form.this.secundaryActivityType == ActivityType.STOCKHOLDER);

            secundaryPensionerFrom.setRequired(secundaryActivityExist && LoanApplicationStep3Form.this.secundaryActivityType == ActivityType.PENSIONER);
            secundaryPensionerNetIncome.setRequired(secundaryActivityExist && LoanApplicationStep3Form.this.secundaryActivityType == ActivityType.PENSIONER);

            secundaryHousekeeperEmployer.setRequired(secundaryActivityExist && LoanApplicationStep3Form.this.secundaryActivityType == ActivityType.HOUSEKEEPER);
            secundaryHousekeeperTime.setRequired(secundaryActivityExist && LoanApplicationStep3Form.this.secundaryActivityType == ActivityType.HOUSEKEEPER);
            secundaryHousekeeperNetIncome.setRequired(secundaryActivityExist && LoanApplicationStep3Form.this.secundaryActivityType == ActivityType.HOUSEKEEPER);

        }

        @Override
        protected Object getSubclass() {
            return this;
        }

        @Override
        protected Object getFormClass() {
            return LoanApplicationStep3Form.this;
        }

        @Override
        public String toString() {
            return "LoanApplicationStep3FormValidator{" +
                    "principalActivityType=" + principalActivityType +
                    ", principalDependentRuc=" + principalDependentRuc +
                    ", principalDependentCompany=" + principalDependentCompany +
                    ", principalDependentTime=" + principalDependentTime +
                    ", principalDependentGrossIncome=" + principalDependentGrossIncome +
                    ", principalDependentNetIncome=" + principalDependentNetIncome +
                    ", principalDependentPhoneNumber=" + principalDependentPhoneNumber +
                    ", principalIndependentRuc=" + principalIndependentRuc +
                    ", principalIndependentRegime=" + principalIndependentRegime +
                    ", principalIndependentVouchers=" + principalIndependentVouchers +
                    ", principalIndependentTime=" + principalIndependentTime +
                    ", principalIndependentGrossIncome=" + principalIndependentGrossIncome +
                    ", principalIndependentNetIncome=" + principalIndependentNetIncome +
                    ", principalRentierRuc=" + principalRentierRuc +
                    ", principalRentierBelonging=" + principalRentierBelonging +
                    ", principalRentierTime=" + principalRentierTime +
                    ", principalRentierGrossIncome=" + principalRentierGrossIncome +
                    ", principalRentierNetIncome=" + principalRentierNetIncome +
                    ", principalShareholderRuc=" + principalShareholderRuc +
                    ", principalShareholderCompany=" + principalShareholderCompany +
                    ", principalShareholderShareholding=" + principalShareholderShareholding +
                    ", principalShareholderResultU12M=" + principalShareholderResultU12M +
                    ", principalShareholderNetIncome=" + principalShareholderNetIncome +
                    ", principalPensionerFrom=" + principalPensionerFrom +
                    ", principalPensionerNetIncome=" + principalPensionerNetIncome +
                    ", principalHousekeeperEmployer=" + principalHousekeeperEmployer +
                    ", principalHousekeeperTime=" + principalHousekeeperTime +
                    ", principalHousekeeperNetIncome=" + principalHousekeeperNetIncome +
                    ", secundaryActivityType=" + secundaryActivityType +
                    ", secundaryDependentRuc=" + secundaryDependentRuc +
                    ", secundaryDependentCompany=" + secundaryDependentCompany +
                    ", secundaryDependentTime=" + secundaryDependentTime +
                    ", secundaryDependentGrossIncome=" + secundaryDependentGrossIncome +
                    ", secundaryDependentNetIncome=" + secundaryDependentNetIncome +
                    ", secundaryDependentPhoneNumber=" + secundaryDependentPhoneNumber +
                    ", secundaryIndependentRuc=" + secundaryIndependentRuc +
                    ", secundaryIndependentRegime=" + secundaryIndependentRegime +
                    ", secundaryIndependentVouchers=" + secundaryIndependentVouchers +
                    ", secundaryIndependentTime=" + secundaryIndependentTime +
                    ", secundaryIndependentGrossIncome=" + secundaryIndependentGrossIncome +
                    ", secundaryIndependentNetIncome=" + secundaryIndependentNetIncome +
                    ", secundaryRentierRuc=" + secundaryRentierRuc +
                    ", secundaryRentierBelonging=" + secundaryRentierBelonging +
                    ", secundaryRentierTime=" + secundaryRentierTime +
                    ", secundaryRentierGrossIncome=" + secundaryRentierGrossIncome +
                    ", secundaryRentierNetIncome=" + secundaryRentierNetIncome +
                    ", secundaryShareholderRuc=" + secundaryShareholderRuc +
                    ", secundaryShareholderCompany=" + secundaryShareholderCompany +
                    ", secundaryShareholderShareholding=" + secundaryShareholderShareholding +
                    ", secundaryShareholderResultU12M=" + secundaryShareholderResultU12M +
                    ", secundaryShareholderNetIncome=" + secundaryShareholderNetIncome +
                    ", secundaryPensionerFrom=" + secundaryPensionerFrom +
                    ", secundaryPensionerNetIncome=" + secundaryPensionerNetIncome +
                    ", secundaryHousekeeperEmployer=" + secundaryHousekeeperEmployer +
                    ", secundaryHousekeeperTime=" + secundaryHousekeeperTime +
                    ", secundaryHousekeeperNetIncome=" + secundaryHousekeeperNetIncome +
                    ", otherIncomes=" + otherIncomes +
                    '}';
        }
    }

    public Integer getPrincipalActivityType() {
        return principalActivityType;
    }

    public void setPrincipalActivityType(Integer principalActivityType) {
        this.principalActivityType = principalActivityType;
    }

    public String getPrincipalDependentRuc() {
        return principalDependentRuc;
    }

    public void setPrincipalDependentRuc(String principalDependentRuc) {
        this.principalDependentRuc = principalDependentRuc;
    }

    public String getPrincipalDependentCompany() {
        return principalDependentCompany;
    }

    public void setPrincipalDependentCompany(String principalDependentCompany) {
        this.principalDependentCompany = principalDependentCompany;
    }

    public String getPrincipalDependentTime() {
        return principalDependentTime;
    }

    public void setPrincipalDependentTime(String principalDependentTime) {
        this.principalDependentTime = principalDependentTime;
    }

    public Integer getPrincipalDependentGrossIncome() {
        return principalDependentGrossIncome;
    }

    public void setPrincipalDependentGrossIncome(Integer principalDependentGrossIncome) {
        this.principalDependentGrossIncome = principalDependentGrossIncome;
    }

    public Integer getPrincipalDependentNetIncome() {
        return principalDependentNetIncome;
    }

    public void setPrincipalDependentNetIncome(Integer principalDependentNetIncome) {
        this.principalDependentNetIncome = principalDependentNetIncome;
    }

    public String getPrincipalIndependentRuc() {
        return principalIndependentRuc;
    }

    public void setPrincipalIndependentRuc(String principalIndependentRuc) {
        this.principalIndependentRuc = principalIndependentRuc;
    }

    public String getPrincipalIndependentRegime() {
        return principalIndependentRegime;
    }

    public void setPrincipalIndependentRegime(String principalIndependentRegime) {
        this.principalIndependentRegime = principalIndependentRegime;
    }

    public Integer getPrincipalIndependentVouchers() {
        return principalIndependentVouchers;
    }

    public void setPrincipalIndependentVouchers(Integer principalIndependentVouchers) {
        this.principalIndependentVouchers = principalIndependentVouchers;
    }

    public String getPrincipalIndependentTime() {
        return principalIndependentTime;
    }

    public void setPrincipalIndependentTime(String principalIndependentTime) {
        this.principalIndependentTime = principalIndependentTime;
    }

    public Integer getPrincipalIndependentGrossIncome() {
        return principalIndependentGrossIncome;
    }

    public void setPrincipalIndependentGrossIncome(Integer principalIndependentGrossIncome) {
        this.principalIndependentGrossIncome = principalIndependentGrossIncome;
    }

    public Integer getPrincipalIndependentNetIncome() {
        return principalIndependentNetIncome;
    }

    public void setPrincipalIndependentNetIncome(Integer principalIndependentNetIncome) {
        this.principalIndependentNetIncome = principalIndependentNetIncome;
    }

    public String getPrincipalRentierRuc() {
        return principalRentierRuc;
    }

    public void setPrincipalRentierRuc(String principalRentierRuc) {
        this.principalRentierRuc = principalRentierRuc;
    }

    public Integer getPrincipalRentierBelonging() {
        return principalRentierBelonging;
    }

    public void setPrincipalRentierBelonging(Integer principalRentierBelonging) {
        this.principalRentierBelonging = principalRentierBelonging;
    }

    public String getPrincipalRentierTime() {
        return principalRentierTime;
    }

    public void setPrincipalRentierTime(String principalRentierTime) {
        this.principalRentierTime = principalRentierTime;
    }

    public Integer getPrincipalRentierGrossIncome() {
        return principalRentierGrossIncome;
    }

    public void setPrincipalRentierGrossIncome(Integer principalRentierGrossIncome) {
        this.principalRentierGrossIncome = principalRentierGrossIncome;
    }

    public Integer getPrincipalRentierNetIncome() {
        return principalRentierNetIncome;
    }

    public void setPrincipalRentierNetIncome(Integer principalRentierNetIncome) {
        this.principalRentierNetIncome = principalRentierNetIncome;
    }

    public String getPrincipalShareholderRuc() {
        return principalShareholderRuc;
    }

    public void setPrincipalShareholderRuc(String principalShareholderRuc) {
        this.principalShareholderRuc = principalShareholderRuc;
    }

    public String getPrincipalShareholderCompany() {
        return principalShareholderCompany;
    }

    public void setPrincipalShareholderCompany(String principalShareholderCompany) {
        this.principalShareholderCompany = principalShareholderCompany;
    }

    public String getPrincipalShareholderShareholding() {
        return principalShareholderShareholding;
    }

    public void setPrincipalShareholderShareholding(String principalShareholderShareholding) {
        this.principalShareholderShareholding = principalShareholderShareholding;
    }

    public Integer getPrincipalShareholderResultU12M() {
        return principalShareholderResultU12M;
    }

    public void setPrincipalShareholderResultU12M(Integer principalShareholderResultU12M) {
        this.principalShareholderResultU12M = principalShareholderResultU12M;
    }

    public Integer getPrincipalShareholderNetIncome() {
        return principalShareholderNetIncome;
    }

    public void setPrincipalShareholderNetIncome(Integer principalShareholderNetIncome) {
        this.principalShareholderNetIncome = principalShareholderNetIncome;
    }

    public Integer getPrincipalPensionerFrom() {
        return principalPensionerFrom;
    }

    public void setPrincipalPensionerFrom(Integer principalPensionerFrom) {
        this.principalPensionerFrom = principalPensionerFrom;
    }

    public Integer getPrincipalPensionerNetIncome() {
        return principalPensionerNetIncome;
    }

    public void setPrincipalPensionerNetIncome(Integer principalPensionerNetIncome) {
        this.principalPensionerNetIncome = principalPensionerNetIncome;
    }

    public String getPrincipalHousekeeperEmployer() {
        return principalHousekeeperEmployer;
    }

    public void setPrincipalHousekeeperEmployer(String principalHousekeeperEmployer) {
        this.principalHousekeeperEmployer = principalHousekeeperEmployer;
    }

    public String getPrincipalHousekeeperTime() {
        return principalHousekeeperTime;
    }

    public void setPrincipalHousekeeperTime(String principalHousekeeperTime) {
        this.principalHousekeeperTime = principalHousekeeperTime;
    }

    public Integer getPrincipalHousekeeperNetIncome() {
        return principalHousekeeperNetIncome;
    }

    public void setPrincipalHousekeeperNetIncome(Integer principalHousekeeperNetIncome) {
        this.principalHousekeeperNetIncome = principalHousekeeperNetIncome;
    }

    public Integer getSecundaryActivityType() {
        return secundaryActivityType;
    }

    public void setSecundaryActivityType(Integer secundaryActivityType) {
        this.secundaryActivityType = secundaryActivityType;
    }

    public String getSecundaryDependentRuc() {
        return secundaryDependentRuc;
    }

    public void setSecundaryDependentRuc(String secundaryDependentRuc) {
        this.secundaryDependentRuc = secundaryDependentRuc;
    }

    public String getSecundaryDependentCompany() {
        return secundaryDependentCompany;
    }

    public void setSecundaryDependentCompany(String secundaryDependentCompany) {
        this.secundaryDependentCompany = secundaryDependentCompany;
    }

    public String getSecundaryDependentTime() {
        return secundaryDependentTime;
    }

    public void setSecundaryDependentTime(String secundaryDependentTime) {
        this.secundaryDependentTime = secundaryDependentTime;
    }

    public Integer getSecundaryDependentGrossIncome() {
        return secundaryDependentGrossIncome;
    }

    public void setSecundaryDependentGrossIncome(Integer secundaryDependentGrossIncome) {
        this.secundaryDependentGrossIncome = secundaryDependentGrossIncome;
    }

    public Integer getSecundaryDependentNetIncome() {
        return secundaryDependentNetIncome;
    }

    public void setSecundaryDependentNetIncome(Integer secundaryDependentNetIncome) {
        this.secundaryDependentNetIncome = secundaryDependentNetIncome;
    }

    public String getSecundaryIndependentRuc() {
        return secundaryIndependentRuc;
    }

    public void setSecundaryIndependentRuc(String secundaryIndependentRuc) {
        this.secundaryIndependentRuc = secundaryIndependentRuc;
    }

    public String getSecundaryIndependentRegime() {
        return secundaryIndependentRegime;
    }

    public void setSecundaryIndependentRegime(String secundaryIndependentRegime) {
        this.secundaryIndependentRegime = secundaryIndependentRegime;
    }

    public Integer getSecundaryIndependentVouchers() {
        return secundaryIndependentVouchers;
    }

    public void setSecundaryIndependentVouchers(Integer secundaryIndependentVouchers) {
        this.secundaryIndependentVouchers = secundaryIndependentVouchers;
    }

    public String getSecundaryIndependentTime() {
        return secundaryIndependentTime;
    }

    public void setSecundaryIndependentTime(String secundaryIndependentTime) {
        this.secundaryIndependentTime = secundaryIndependentTime;
    }

    public Integer getSecundaryIndependentGrossIncome() {
        return secundaryIndependentGrossIncome;
    }

    public void setSecundaryIndependentGrossIncome(Integer secundaryIndependentGrossIncome) {
        this.secundaryIndependentGrossIncome = secundaryIndependentGrossIncome;
    }

    public Integer getSecundaryIndependentNetIncome() {
        return secundaryIndependentNetIncome;
    }

    public void setSecundaryIndependentNetIncome(Integer secundaryIndependentNetIncome) {
        this.secundaryIndependentNetIncome = secundaryIndependentNetIncome;
    }

    public String getSecundaryRentierRuc() {
        return secundaryRentierRuc;
    }

    public void setSecundaryRentierRuc(String secundaryRentierRuc) {
        this.secundaryRentierRuc = secundaryRentierRuc;
    }

    public Integer getSecundaryRentierBelonging() {
        return secundaryRentierBelonging;
    }

    public void setSecundaryRentierBelonging(Integer secundaryRentierBelonging) {
        this.secundaryRentierBelonging = secundaryRentierBelonging;
    }

    public String getSecundaryRentierTime() {
        return secundaryRentierTime;
    }

    public void setSecundaryRentierTime(String secundaryRentierTime) {
        this.secundaryRentierTime = secundaryRentierTime;
    }

    public Integer getSecundaryRentierGrossIncome() {
        return secundaryRentierGrossIncome;
    }

    public void setSecundaryRentierGrossIncome(Integer secundaryRentierGrossIncome) {
        this.secundaryRentierGrossIncome = secundaryRentierGrossIncome;
    }

    public Integer getSecundaryRentierNetIncome() {
        return secundaryRentierNetIncome;
    }

    public void setSecundaryRentierNetIncome(Integer secundaryRentierNetIncome) {
        this.secundaryRentierNetIncome = secundaryRentierNetIncome;
    }

    public String getSecundaryShareholderRuc() {
        return secundaryShareholderRuc;
    }

    public void setSecundaryShareholderRuc(String secundaryShareholderRuc) {
        this.secundaryShareholderRuc = secundaryShareholderRuc;
    }

    public String getSecundaryShareholderCompany() {
        return secundaryShareholderCompany;
    }

    public void setSecundaryShareholderCompany(String secundaryShareholderCompany) {
        this.secundaryShareholderCompany = secundaryShareholderCompany;
    }

    public String getSecundaryShareholderShareholding() {
        return secundaryShareholderShareholding;
    }

    public void setSecundaryShareholderShareholding(String secundaryShareholderShareholding) {
        this.secundaryShareholderShareholding = secundaryShareholderShareholding;
    }

    public Integer getSecundaryShareholderResultU12M() {
        return secundaryShareholderResultU12M;
    }

    public void setSecundaryShareholderResultU12M(Integer secundaryShareholderResultU12M) {
        this.secundaryShareholderResultU12M = secundaryShareholderResultU12M;
    }

    public Integer getSecundaryShareholderNetIncome() {
        return secundaryShareholderNetIncome;
    }

    public void setSecundaryShareholderNetIncome(Integer secundaryShareholderNetIncome) {
        this.secundaryShareholderNetIncome = secundaryShareholderNetIncome;
    }

    public Integer getSecundaryPensionerFrom() {
        return secundaryPensionerFrom;
    }

    public void setSecundaryPensionerFrom(Integer secundaryPensionerFrom) {
        this.secundaryPensionerFrom = secundaryPensionerFrom;
    }

    public Integer getSecundaryPensionerNetIncome() {
        return secundaryPensionerNetIncome;
    }

    public void setSecundaryPensionerNetIncome(Integer secundaryPensionerNetIncome) {
        this.secundaryPensionerNetIncome = secundaryPensionerNetIncome;
    }

    public String getSecundaryHousekeeperEmployer() {
        return secundaryHousekeeperEmployer;
    }

    public void setSecundaryHousekeeperEmployer(String secundaryHousekeeperEmployer) {
        this.secundaryHousekeeperEmployer = secundaryHousekeeperEmployer;
    }

    public String getSecundaryHousekeeperTime() {
        return secundaryHousekeeperTime;
    }

    public void setSecundaryHousekeeperTime(String secundaryHousekeeperTime) {
        this.secundaryHousekeeperTime = secundaryHousekeeperTime;
    }

    public Integer getSecundaryHousekeeperNetIncome() {
        return secundaryHousekeeperNetIncome;
    }

    public void setSecundaryHousekeeperNetIncome(Integer secundaryHousekeeperNetIncome) {
        this.secundaryHousekeeperNetIncome = secundaryHousekeeperNetIncome;
    }

    public Integer getOtherIncomes() {
        return otherIncomes;
    }

    public void setOtherIncomes(Integer otherIncomes) {
        this.otherIncomes = otherIncomes;
    }

    public String getPrincipalDependentPhoneNumber() {
        return principalDependentPhoneNumber;
    }

    public void setPrincipalDependentPhoneNumber(String principalDependentPhoneNumber) {
        this.principalDependentPhoneNumber = principalDependentPhoneNumber;
    }

    public String getSecundaryDependentPhoneNumber() {
        return secundaryDependentPhoneNumber;
    }

    public void setSecundaryDependentPhoneNumber(String secundaryDependentPhoneNumber) {
        this.secundaryDependentPhoneNumber = secundaryDependentPhoneNumber;
    }
}


