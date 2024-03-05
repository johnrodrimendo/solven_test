package com.affirm.efl.model;

import com.affirm.common.model.transactional.PersonOcupationalInformation;

/**
 * Created by dev5 on 04/07/17.
 */
public class LoanObject {

    private Integer amount;
    private Integer businessIncome;
    private String currency;
    private Integer personalIncome;
    private Number term;

    public LoanObject(PersonOcupationalInformation ocupationalInfo){
        if(ocupationalInfo != null)
            this.personalIncome =ocupationalInfo.getFixedGrossIncome().intValue();
        else
            this.personalIncome = null;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Integer getBusinessIncome() {
        return businessIncome;
    }

    public void setBusinessIncome(Integer businessIncome) {
        this.businessIncome = businessIncome;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Integer getPersonalIncome() {
        return personalIncome;
    }

    public void setPersonalIncome(Integer personalIncome) {
        this.personalIncome = personalIncome;
    }

    public Number getTerm() {
        return term;
    }

    public void setTerm(Number term) {
        this.term = term;
    }
}
