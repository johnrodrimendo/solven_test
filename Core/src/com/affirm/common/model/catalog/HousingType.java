package com.affirm.common.model.catalog;

import com.affirm.common.util.JsonUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jrodriguez on 12/07/16.
 */
public class HousingType implements Serializable {

    public static final int OWN = 1;
    public static final int RENTED = 3;
    public static final int FAMILY = 4;
    public static final int OWN_FINANCED = 2;
    public static final int ROOM_RENTED = 5;
    public static final int FAMILY_2 = 6;
    public static final int OTHER = 7;

    private Integer id;
    private String type;
    private Boolean active;
    private JSONArray requiredDocuments;
    private String textInt;
    private String description;

    public void fillFromDb(JSONObject json) {
        setId(JsonUtil.getIntFromJson(json, "housing_type_id", null));
        setType(JsonUtil.getStringFromJson(json, "housing_type", null));
        setActive(JsonUtil.getBooleanFromJson(json, "is_active", null));
        setRequiredDocuments(JsonUtil.getJsonArrayFromJson(json, "js_required_filetype", null));
        setTextInt(JsonUtil.getStringFromJson(json, "text_int", null));
        setDescription(JsonUtil.getStringFromJson(json, "description", null));
    }

    public List<Integer> getRequiredDocumentsByEntityProduct(int entityId, int productId) {
        if (requiredDocuments != null)
            for (int i = 0; i < requiredDocuments.length(); i++) {
                Integer jsonEntityId = JsonUtil.getIntFromJson(requiredDocuments.getJSONObject(i), "entity_id", null);
                Integer jsonProductId = JsonUtil.getIntFromJson(requiredDocuments.getJSONObject(i), "product_id", null);
                if (jsonEntityId != null && jsonProductId != null && jsonEntityId == entityId && jsonProductId == productId) {
                    JSONArray arrayDocs = requiredDocuments.getJSONObject(i).getJSONArray("filetype_id");
                    List<Integer> results = new ArrayList<>();
                    for (int j = 0; j < arrayDocs.length(); j++)
                        results.add(arrayDocs.getInt(j));

                    return results;
                }
            }

        return new ArrayList<>();
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

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public JSONArray getRequiredDocuments() {
        return requiredDocuments;
    }

    public void setRequiredDocuments(JSONArray requiredDocuments) {
        this.requiredDocuments = requiredDocuments;
    }

    public String getTextInt() {
        return textInt;
    }

    public void setTextInt(String textInt) {
        this.textInt = textInt;
    }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }
}
