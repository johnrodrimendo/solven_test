/**
 *
 */
package com.affirm.common.model.catalog;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * @author jrodriguez
 */
public class CreditUsage implements Serializable {

    private Integer id;
    private String usage;
    private String messageKey;

    public static final String PARTICULAR = "PARTICULAR";

    public void fillFromDb(JSONObject json){
        setId(JsonUtil.getIntFromJson(json, "usage_id", null));
        setMessageKey(JsonUtil.getStringFromJson(json, "text_int", null));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsage() {
        return usage;
    }

    public void setUsage(String usage) {
        this.usage = usage;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public void setMessageKey(String messageKey) {
        this.messageKey = messageKey;
    }
}
