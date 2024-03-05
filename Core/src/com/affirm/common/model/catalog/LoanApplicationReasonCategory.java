package com.affirm.common.model.catalog;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * @author jrodriguez
 */
public class LoanApplicationReasonCategory implements Serializable {

    public static final int EDUCACION = 1;
    public static final int COMPRAS = 2;
    public static final int GASTOS_PERSONALES = 3;
    public static final int OTROS = 4;

    private Integer id;
    private String category;
    private String messageKey;

    public void fillFromDb(JSONObject json) {
        setId(JsonUtil.getIntFromJson(json, "reason_category_id", null));
        setMessageKey(JsonUtil.getStringFromJson(json, "text_int", null));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public void setMessageKey(String messageKey) {
        this.messageKey = messageKey;
    }
}
