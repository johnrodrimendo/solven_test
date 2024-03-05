package com.affirm.common.model.catalog;

import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

/**
 * Created by john on 12/01/17.
 */
public class RccEntity {

    private String code;
    private String fullName;
    private String shortName;
    private Bank bank;

    public void fillFromDb(JSONObject json, CatalogService catalog) throws Exception {
        setCode(JsonUtil.getStringFromJson(json, "entity_code", null));
        setFullName(JsonUtil.getStringFromJson(json, "full_name", null));
        setShortName(JsonUtil.getStringFromJson(json, "short_name", null));
        setShortName(JsonUtil.getStringFromJson(json, "short_name", null));
        if (JsonUtil.getIntFromJson(json, "bank_id", null) != null)
            setBank(catalog.getBank(JsonUtil.getIntFromJson(json, "bank_id", null)));
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public Bank getBank() {
        return bank;
    }

    public void setBank(Bank bank) {
        this.bank = bank;
    }
}
