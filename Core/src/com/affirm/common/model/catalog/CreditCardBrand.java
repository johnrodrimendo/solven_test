package com.affirm.common.model.catalog;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

/**
 * Created by dev5 on 23/06/17.
 */
public class CreditCardBrand {

    public static final char VISA = '1';
    public static final char MARTERCARD = '2';

    private int id;
    private String name;

    public void fillFromDb(JSONObject json){
        setId(JsonUtil.getIntFromJson(json, "brand_id", null));
        setName(JsonUtil.getStringFromJson(json, "brand", null));
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
