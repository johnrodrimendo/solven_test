package com.affirm.common.model.catalog;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

/**
 * Created by john on 12/01/17.
 */
public class RccAccount {

    private Integer id;
    private String name;
    private Boolean consolidable;

    public void fillFromDb(JSONObject json) throws Exception {
        setId(JsonUtil.getIntFromJson(json, "account_id", null));
        setName(JsonUtil.getStringFromJson(json, "account", null));
        setConsolidable(JsonUtil.getBooleanFromJson(json, "is_consolidable", null));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getConsolidable() {
        return consolidable;
    }

    public void setConsolidable(Boolean consolidable) {
        this.consolidable = consolidable;
    }
}
