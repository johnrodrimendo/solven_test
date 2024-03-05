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
public class LeadActivityType implements Serializable, ICatalogIntegerId {

    public static final int LEAD_DEPENDENT_REGISTERED = 1;
    public static final int LEAD_DEPENDENT_UNREGISTERED = 2;
    public static final int LEAD_RUC_SELF_EMPLOYED_REGISTERED = 3;
    public static final int LEAD_NO_RUC_SELF_EMPLOYED_REGISTERED = 4;
    public static final int LEAD_INCOMELESS = 5;


    private Integer id;
    private String activityType;
    private String activityPropertyKey;


    public void fillFromDb(JSONObject json) {
        setId(JsonUtil.getIntFromJson(json, "activity_type_id", null));
        setActivityType(JsonUtil.getStringFromJson(json, "activity_type", null));
        setActivityPropertyKey(JsonUtil.getStringFromJson(json, "text_int", null));
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    public String getActivityPropertyKey() {
        return activityPropertyKey;
    }

    public void setActivityPropertyKey(String activityPropertyKey) {
        this.activityPropertyKey = activityPropertyKey;
    }
}
