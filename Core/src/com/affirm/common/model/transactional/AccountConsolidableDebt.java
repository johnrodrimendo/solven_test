package com.affirm.common.model.transactional;

import com.affirm.common.model.catalog.RccAccount;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by john on 12/01/17.
 */
public class AccountConsolidableDebt implements Serializable {

    public static final int[] CONSUME_ACCOUNTS = new int[]{45, 13, 14, 15, 16, 20, 21, 22, 23};
    public static final int[] CREDITCARD_ACCOUNTS = new int[]{42};
    public static final int CREDITCARD_SOBREGIRO = 44;
    public static final int CREDITCARD_REVOLVENTE = 43;
    public static final int CREDITCARD_LINEA = 47;

    private RccAccount account;
    private Double balance;
    private Double rate;

    public void fillFromDb(JSONObject json, CatalogService catalog) throws Exception {
        if (JsonUtil.getIntFromJson(json, "account_id", null) != null) {
            setAccount(catalog.getRccAccount(JsonUtil.getIntFromJson(json, "account_id", null)));
        }
        setBalance(JsonUtil.getDoubleFromJson(json, "balance", null));
        setRate(JsonUtil.getDoubleFromJson(json, "rate", null));
    }

    public RccAccount getAccount() {
        return account;
    }

    public void setAccount(RccAccount account) {
        this.account = account;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }
}
