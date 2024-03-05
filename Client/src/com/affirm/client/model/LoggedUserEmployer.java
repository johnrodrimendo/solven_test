package com.affirm.client.model;

import com.affirm.common.model.catalog.Employer;
import com.affirm.common.model.transactional.UserEmployer;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;
import java.util.Locale;

public class LoggedUserEmployer extends UserEmployer implements Serializable {

    private Integer sessionId;
    private Date lastSigninDate;
    private Employer activeCompany;

    public void fillFromDb(JSONObject json, CatalogService catalog, Locale locale) {
        setSessionId(JsonUtil.getIntFromJson(json, "employer_session_id", null));
        setLastSigninDate(JsonUtil.getPostgresDateFromJson(json, "last_sign_in_date", null));
        super.fillFromDb(json, catalog, locale);

    }

    public Integer getSessionId() {
        return sessionId;
    }

    public void setSessionId(Integer sessionId) {
        this.sessionId = sessionId;
    }

    public Date getLastSigninDate() {
        return lastSigninDate;
    }

    public void setLastSigninDate(Date lastSigninDate) {
        this.lastSigninDate = lastSigninDate;
    }

    public Employer getActiveCompany() {
        if (activeCompany == null) {
            activeCompany = getCompanies().get(0);
        }
        return activeCompany;
    }

    public void setActiveCompany(Employer activeCompany) {
        this.activeCompany = activeCompany;
    }
}