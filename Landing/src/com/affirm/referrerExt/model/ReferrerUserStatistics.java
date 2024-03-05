package com.affirm.referrerExt.model;

import com.affirm.common.model.catalog.Bank;
import com.affirm.common.model.catalog.IdentityDocumentType;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

public class ReferrerUserStatistics implements Serializable {

    private Integer initiatedLoans;
    private Integer completedLoans;
    private Integer offerLoans;
    private Integer disbursedCredits;

    public void fillFromDb(JSONObject json) {
        setInitiatedLoans(JsonUtil.getIntFromJson(json, "initiated_loans", null));
        setCompletedLoans(JsonUtil.getIntFromJson(json, "completed_loans", null));
        setOfferLoans(JsonUtil.getIntFromJson(json, "offer_loans", null));
        setDisbursedCredits(JsonUtil.getIntFromJson(json, "disbursed_credits", null));
    }

    public Integer getInitiatedLoans() {
        return initiatedLoans;
    }

    public void setInitiatedLoans(Integer initiatedLoans) {
        this.initiatedLoans = initiatedLoans;
    }

    public Integer getCompletedLoans() {
        return completedLoans;
    }

    public void setCompletedLoans(Integer completedLoans) {
        this.completedLoans = completedLoans;
    }

    public Integer getOfferLoans() {
        return offerLoans;
    }

    public void setOfferLoans(Integer offerLoans) {
        this.offerLoans = offerLoans;
    }

    public Integer getDisbursedCredits() {
        return disbursedCredits;
    }

    public void setDisbursedCredits(Integer disbursedCredits) {
        this.disbursedCredits = disbursedCredits;
    }
}
