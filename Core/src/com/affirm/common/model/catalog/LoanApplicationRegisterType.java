package com.affirm.common.model.catalog;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

/**
 * Created by miberico on 20/03/17.
 */
public class LoanApplicationRegisterType {

    public static final int DNI = 1;
    public static final int PHONE = 2;
    public static final int EMAIL = 3;

    private Integer registerTypeId;
    private String registerTypeName;

    public void fillFromDb(JSONObject json) {
        setRegisterTypeId(JsonUtil.getIntFromJson(json, "register_type_id", null));
        setRegisterTypeName(JsonUtil.getStringFromJson(json, "register_type", null));
    }

    public Integer getRegisterTypeId() {
        return registerTypeId;
    }

    public void setRegisterTypeId(Integer registerTypeId) {
        this.registerTypeId = registerTypeId;
    }

    public String getRegisterTypeName() {
        return registerTypeName;
    }

    public void setRegisterTypeName(String registerTypeName) {
        this.registerTypeName = registerTypeName;
    }
}
