package com.affirm.entityExt.models;

import com.affirm.common.model.BankAccountOfferData;
import com.affirm.common.model.catalog.Product;
import com.affirm.common.model.transactional.BanbifPreApprovedBase;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.model.transactional.LoanOffer;
import com.affirm.common.service.UtilService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class SummaryLoanExtranetPainter {

    private LoanApplication loan;
    private List<LoanOffer> offers;
    private Boolean fixedOffer;

    public LoanApplication getLoan() {
        return loan;
    }

    public void setLoan(LoanApplication loan) {
        this.loan = loan;
    }

    public List<LoanOffer> getOffers() {
        return offers;
    }

    public void setOffers(List<LoanOffer> offers) {
        this.offers = offers;
    }

    public Boolean getFixedOffer() {
        return fixedOffer;
    }

    public void setFixedOffer(Boolean fixedOffer) {
        this.fixedOffer = fixedOffer;
    }


    public String getSummaryOffer(LoanOffer loanOffer, UtilService utilService, Integer countryCode) {

        String mesagge = "";
        Character offerDataType = null;

        if (loanOffer.getProduct().getId() == Product.SAVINGS_ACCOUNT) {
            if (loanOffer.getBankAccountOfferData() != null) {
                offerDataType = loanOffer.getBankAccountOfferData().getType();
                if (offerDataType != null) {
                    switch (offerDataType) {
                        case BankAccountOfferData.TRADITIONAL_TYPE:
                            mesagge = BankAccountOfferData.TRADITIONAL_NAME;
                            break;
                        case BankAccountOfferData.HIGH_PROFITABILITY_TYPE:
                            mesagge = BankAccountOfferData.HIGH_PROFITABILITY_NAME;
                            break;
                    }
                }
            }
        } else if (loanOffer.getProduct().getId() == Product.TARJETA_CREDITO) {
            BanbifPreApprovedBase preApprovedBase = new Gson().fromJson(loan.getEntityCustomData().getJSONObject(LoanApplication.EntityCustomDataKeys.BANBIF_BASE_PREAPROBADA.getKey()).toString(), BanbifPreApprovedBase.class);

            if (preApprovedBase != null) {
                mesagge = utilService.getWordLetter(preApprovedBase.getPlastico(), 0, 2) + "\t@ " + preApprovedBase.getLinea() + "\t@ " + preApprovedBase.getPromocionAceptacion();
            }
        } else {
            mesagge = loanOffer.getLoanOfferDescription(utilService, countryCode);
        }

        return mesagge;

    }


}
