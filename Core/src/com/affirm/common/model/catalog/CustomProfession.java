package com.affirm.common.model.catalog;

import com.affirm.common.util.JsonUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CustomProfession {

    public static final int PROFESSIONAL_DRIVER = 1;
    public static final int TEACHER = 2;
    public static final int PRIVATE_SECTOR = 3;
    public static final int PUBLIC_SERVANT = 4;
    public static final int BUSINNES_OWNER = 5;
    public static final int ENGINEER = 6;
    public static final int DOCTOR = 7;
    public static final int POLICEMAN = 8;
    public static final int SALESMAN = 9;
    public static final int OTHERS = 10;

    private Integer id;
    private String custom_profession;
    private Integer entityId;
    private List<Integer> activityTypeIds;
    private List<Integer> subActivityTypeIds;
    private Boolean isActive;

    public void fillFromDb(JSONObject json, Locale locale) {
        setId(JsonUtil.getIntFromJson(json, "custom_profession_id", null));
        setEntityId(JsonUtil.getIntFromJson(json, "custom_entity_id", null));
        setCustom_profession(JsonUtil.getStringFromJson(json, "custom_profession", null));
        setEntityId(JsonUtil.getIntFromJson(json, "entity_id", null));


        if (JsonUtil.getJsonArrayFromJson(json, "ar_activity_type_id", null) != null) {
            List<Integer> atids = new ArrayList<>();
            JSONArray arrayResults = JsonUtil.getJsonArrayFromJson(json, "ar_activity_type_id", null);
            for (int i = 0; i < arrayResults.length(); i++) {
                atids.add(Integer.valueOf(arrayResults.get(i).toString()));
            }
            setActivityTypeIds(atids);
        }

        if (JsonUtil.getJsonArrayFromJson(json, "ar_sub_activity_type_id", null) != null) {
            List<Integer> satids = new ArrayList<>();
            JSONArray arrayResults = JsonUtil.getJsonArrayFromJson(json, "ar_sub_activity_type_id", null);
            for (int i = 0; i < arrayResults.length(); i++) {
                satids.add(Integer.valueOf(arrayResults.get(i).toString()));
            }
            setSubActivityTypeIds(satids);
        }
        setActive(JsonUtil.getBooleanFromJson(json, "is_active", null));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCustom_profession() {
        return custom_profession;
    }

    public void setCustom_profession(String custom_profession) {
        this.custom_profession = custom_profession;
    }

    public Integer getEntityId() {
        return entityId;
    }

    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }

    public List<Integer> getActivityTypeIds() {
        return activityTypeIds;
    }

    public void setActivityTypeIds(List<Integer> activityTypeIds) {
        this.activityTypeIds = activityTypeIds;
    }

    public List<Integer> getSubActivityTypeIds() {
        return subActivityTypeIds;
    }

    public void setSubActivityTypeIds(List<Integer> subActivityTypeIds) {
        this.subActivityTypeIds = subActivityTypeIds;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }
}

