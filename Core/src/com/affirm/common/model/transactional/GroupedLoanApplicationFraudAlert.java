package com.affirm.common.model.transactional;

import com.affirm.common.model.catalog.FraudAlert;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import com.google.gson.Gson;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GroupedLoanApplicationFraudAlert {
    private FraudAlert fraudAlert;
    private List<Integer> loanApplicationFraudAlertIds;

    public String getLoanFraudAlertIdsAsJsonArray(){
        if(loanApplicationFraudAlertIds == null)
            return "[]";
        return new Gson().toJson(loanApplicationFraudAlertIds);
    }

    public FraudAlert getFraudAlert() {
        return fraudAlert;
    }

    public void setFraudAlert(FraudAlert fraudAlert) {
        this.fraudAlert = fraudAlert;
    }

    public List<Integer> getLoanApplicationFraudAlertIds() {
        return loanApplicationFraudAlertIds;
    }

    public void setLoanApplicationFraudAlertIds(List<Integer> loanApplicationFraudAlertIds) {
        this.loanApplicationFraudAlertIds = loanApplicationFraudAlertIds;
    }
}
