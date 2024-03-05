/**
 *
 */
package com.affirm.common.model.catalog;

import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author jrodriguez
 */
public class SubActivityType implements Serializable, ICatalog, ICatalogIntegerId  {

    public static final int PROFESSIONAL_SERVICE = 1;
    public static final int OWN_BUSINESS = 2;
    public static final int RENT = 3;
    public static final int SHAREHOLDER = 4;

    private Integer id;
    private String type;
    private String messageKey;
    private ActivityType activityType;
    private List<Integer> requiredDocuments;
    private Boolean active = true;

    public void fillFromDb(JSONObject json, CatalogService catalogService, Locale locale) {
        setId(JsonUtil.getIntFromJson(json, "sub_activity_type_id", null));
        setMessageKey(JsonUtil.getStringFromJson(json, "text_int", null));
        setType(catalogService.getMessageSource().getMessage(getMessageKey(), null, locale));
        if (JsonUtil.getIntFromJson(json, "activity_type_id", null) != null)
            setActivityType(catalogService.getActivityType(locale, JsonUtil.getIntFromJson(json, "activity_type_id", null)));
        if (JsonUtil.getJsonArrayFromJson(json, "js_required_filetype", null) != null) {
            JSONArray requireds = JsonUtil.getJsonArrayFromJson(json, "js_required_filetype", null);
            List<Integer> requiedFiles = new ArrayList<>();
            for (int j = 0; j < requireds.length(); j++) {
                requiedFiles.add(requireds.getInt(j));
            }
            setRequiredDocuments(requiedFiles);
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public void setMessageKey(String messageKey) {
        this.messageKey = messageKey;
    }

    public ActivityType getActivityType() {
        return activityType;
    }

    public void setActivityType(ActivityType activityType) {
        this.activityType = activityType;
    }

    public List<Integer> getRequiredDocuments() {
        return requiredDocuments;
    }

    public void setRequiredDocuments(List<Integer> requiredDocuments) {
        this.requiredDocuments = requiredDocuments;
    }

    @Override
    public Boolean getActive() {
        return active;
    }

    @Override
    public void setActive(Boolean active) {
        this.active = active;
    }
}
