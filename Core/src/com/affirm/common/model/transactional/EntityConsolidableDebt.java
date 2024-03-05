package com.affirm.common.model.transactional;

import com.affirm.common.model.catalog.RccEntity;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by john on 12/01/17.
 */
public class EntityConsolidableDebt implements Serializable {

    private RccEntity entity;
    private List<AccountConsolidableDebt> accounts;


    public void fillFromDb(JSONObject json, CatalogService catalog) throws Exception {
        if (JsonUtil.getStringFromJson(json, "entity_code", null) != null) {
            setEntity(catalog.getRccEntity(JsonUtil.getStringFromJson(json, "entity_code", null)));
        }
        if (JsonUtil.getJsonArrayFromJson(json, "accounts", null) != null) {
            accounts = new ArrayList<>();
            JSONArray accountsArray = JsonUtil.getJsonArrayFromJson(json, "accounts", null);
            for (int i = 0; i < accountsArray.length(); i++) {
                AccountConsolidableDebt account = new AccountConsolidableDebt();
                account.fillFromDb(accountsArray.getJSONObject(i), catalog);
                accounts.add(account);
            }
        }
    }

    public RccEntity getEntity() {
        return entity;
    }

    public void setEntity(RccEntity entity) {
        this.entity = entity;
    }

    public List<AccountConsolidableDebt> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<AccountConsolidableDebt> accounts) {
        this.accounts = accounts;
    }
}
