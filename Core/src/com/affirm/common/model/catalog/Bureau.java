package com.affirm.common.model.catalog;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;

public class Bureau implements Serializable{
    public static final int EQUIFAX = 1;
    public static final int EXPERIAN = 2;
    public static final int NOSIS = 3;
    public static final int NOSIS_BDS = 4;
    public static final int PYP = 5;
    public static final int EQUIFAX_RUC = 6;
    public static final int SENTINEL = 7;
    public static final int SENTINEL_INP_PRI = 8;
    public static final int VERAZ_BDS = 9;
    public static final int VERAZ_REST_BDS = 10;

    private Integer id;
    private String bureau;

    public void fillFromDb(JSONObject json) {
        setId(JsonUtil.getIntFromJson(json, "bureau_id", null));
        setBureau(JsonUtil.getStringFromJson(json, "bureau", null));
    }

    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id; }

    public String getBureau() { return bureau; }

    public void setBureau(String bureau) { this.bureau = bureau; }
}
