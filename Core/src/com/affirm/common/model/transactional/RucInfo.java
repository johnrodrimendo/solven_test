package com.affirm.common.model.transactional;

import com.affirm.common.util.JsonUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;

public class RucInfo implements Serializable{

    private String ruc;
    private String numdoc;
    private String fiscalAddress;
    private String registerDate;
    private String ciiu;
    private String principalActivity;

    public void fillFromDB(JSONObject json) {
        setNumdoc(JsonUtil.getStringFromJson(json, "in_numdoc", null));
        setRuc(JsonUtil.getStringFromJson(json, "ruc", null));
        setFiscalAddress(JsonUtil.getStringFromJson(json, "fiscal_address", null));
        setRegisterDate(JsonUtil.getStringFromJson(json, "register_date", null));
        JSONArray activities = JsonUtil.getJsonArrayFromJson(json, "economic_activities", null);
        String principal = activities.get(0).toString();
        setCiiu(principal.split("-").length > 1 ? principal.split("-")[1].replace("CIIU", "").trim() : null);
        setPrincipalActivity(principal.split("-").length > 2 ? principal.split("-")[2].trim() : null);
    }


    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public String getFiscalAddress() {
        return fiscalAddress;
    }

    public void setFiscalAddress(String fiscalAddress) {
        this.fiscalAddress = fiscalAddress;
    }

    public String getNumdoc() {
        return numdoc;
    }

    public void setNumdoc(String numdoc) {
        this.numdoc = numdoc;
    }

    public String getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(String registerDate) {
        this.registerDate = registerDate;
    }

    public String getCiiu() {
        return ciiu;
    }

    public void setCiiu(String ciiu) {
        this.ciiu = ciiu;
    }

    public String getPrincipalActivity() { return principalActivity; }

    public void setPrincipalActivity(String principalActivity) { this.principalActivity = principalActivity; }
}
