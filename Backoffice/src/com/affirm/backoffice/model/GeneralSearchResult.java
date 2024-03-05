package com.affirm.backoffice.model;

import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by jrodriguez on 04/08/16.
 */
public class GeneralSearchResult {

    private List<LoanApplicationSummaryBoPainter> loanApplications;
    private List<CreditBoPainter> credits;

    public void fillFromDb(JSONArray array, CatalogService catalog, Locale locale) throws Exception {
        for (int i = 0; i < array.length(); i++) {
            JSONObject json = array.getJSONObject(i);
            JSONArray jsContent = JsonUtil.getJsonArrayFromJson(json, "js_content", new JSONArray());
            if (json.getString("js_identifier").equals("loan_applications") && jsContent.length() > 0) {
                loanApplications = new ArrayList<>();
                for (int j = 0; j < json.getJSONArray("js_content").length(); j++) {
                    LoanApplicationSummaryBoPainter application = new LoanApplicationSummaryBoPainter();
                    application.fillFromDb(json.getJSONArray("js_content").getJSONObject(j), catalog,null, locale);
                    loanApplications.add(application);
                }
            } else if (json.getString("js_identifier").equals("credits") && jsContent.length() > 0) {
                credits = new ArrayList<>();
                for (int j = 0; j < json.getJSONArray("js_content").length(); j++) {
                    CreditBoPainter credit = new CreditBoPainter();
                    credit.fillFromDb(json.getJSONArray("js_content").getJSONObject(j), catalog, locale);
                    credits.add(credit);
                }
            }
        }
    }

    public List<LoanApplicationSummaryBoPainter> getLoanApplications() {
        return loanApplications;
    }

    public void setLoanApplications(List<LoanApplicationSummaryBoPainter> loanApplications) {
        this.loanApplications = loanApplications;
    }

    public List<CreditBoPainter> getCredits() {
        return credits;
    }

    public void setCredits(List<CreditBoPainter> credits) {
        this.credits = credits;
    }
}
