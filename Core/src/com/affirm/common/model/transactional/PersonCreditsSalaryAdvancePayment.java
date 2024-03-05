package com.affirm.common.model.transactional;

import com.affirm.common.model.catalog.IdentityDocumentType;
import org.apache.commons.lang3.mutable.MutableDouble;

import java.util.List;

/**
 * Created by jrodriguez on 08/06/16.
 */
public class PersonCreditsSalaryAdvancePayment {

    private Integer personId;
    private double amountToPay;
    private IdentityDocumentType docType;
    private String docNumber;
    private String fullName;
    private List<Credit> credits;

    public Double getCreditsTotalAmount() {
        if (credits == null)
            return null;

        MutableDouble creditsTotal = new MutableDouble();
        credits.stream().forEach(c -> creditsTotal.add(c.getAmount()));
        return creditsTotal.doubleValue();
    }

    public Double getCreditsTotalPendingAmount() {
        if (credits == null)
            return null;

        MutableDouble creditsTotal = new MutableDouble();
        credits.stream().forEach(c -> creditsTotal.add(c.getPendingInstallmentAmount()));
        return creditsTotal.doubleValue();
    }

    public Integer getPersonId() {
        return personId;
    }

    public void setPersonId(Integer personId) {
        this.personId = personId;
    }

    public double getAmountToPay() {
        return amountToPay;
    }

    public void setAmountToPay(double amountToPay) {
        this.amountToPay = amountToPay;
    }

    public IdentityDocumentType getDocType() {
        return docType;
    }

    public void setDocType(IdentityDocumentType docType) {
        this.docType = docType;
    }

    public String getDocNumber() {
        return docNumber;
    }

    public void setDocNumber(String docNumber) {
        this.docNumber = docNumber;
    }

    public List<Credit> getCredits() {
        return credits;
    }

    public void setCredits(List<Credit> credits) {
        this.credits = credits;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
