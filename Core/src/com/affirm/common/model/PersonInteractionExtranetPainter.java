package com.affirm.common.model;

import java.util.Date;
import java.util.List;

/**
 * Created by jrodriguez on 08/06/16.
 */
public class PersonInteractionExtranetPainter {


    private Integer creditId;
    private Integer loanApplicationId;
    private String creditCode;
    private String loanApplicationCode;
    private Date registerDate;
    private List<Object> interactions;

    public Integer getCreditId() {
        return creditId;
    }

    public void setCreditId(Integer creditId) {
        this.creditId = creditId;
    }

    public Integer getLoanApplicationId() {
        return loanApplicationId;
    }

    public void setLoanApplicationId(Integer loanApplicationId) {
        this.loanApplicationId = loanApplicationId;
    }

    public String getCreditCode() {
        return creditCode;
    }

    public void setCreditCode(String creditCode) {
        this.creditCode = creditCode;
    }

    public String getLoanApplicationCode() {
        return loanApplicationCode;
    }

    public void setLoanApplicationCode(String loanApplicationCode) {
        this.loanApplicationCode = loanApplicationCode;
    }

    public List<Object> getInteractions() {
        return interactions;
    }

    public void setInteractions(List<Object> interactions) {
        this.interactions = interactions;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }
}
