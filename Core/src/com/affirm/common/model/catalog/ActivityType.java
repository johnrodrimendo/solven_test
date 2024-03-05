
/**
 *
 */
package com.affirm.common.model.catalog;

import com.affirm.common.util.JsonUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jrodriguez
 */
public class ActivityType implements Serializable {

    public static final int DEPENDENT = 1;
    public static final int INDEPENDENT = 2;
    public static final int HOUSEKEEPER = 3;
    public static final int RENTIER = 4;
    public static final int STOCKHOLDER = 5;
    public static final int PENSIONER = 6;
    public static final int STUDENT = 7;
    public static final int UNEMPLOYED = 8;
    public static final int MONOTRIBUTISTA = 9;

    private Integer id;
    private String type;
    private String messageKey;
    private Boolean principal;
    private Boolean secundary;
    private Integer minIncome;
    private List<Integer> requiredDocuments; //TODO Utilizar estos para saber que documentos se debe subir
    private JSONArray jsDefaults;

    public void fillFromDb(JSONObject json) {
        setId(JsonUtil.getIntFromJson(json, "activity_type_id", null));
        setMessageKey(JsonUtil.getStringFromJson(json, "text_int", null));
        setPrincipal(JsonUtil.getBooleanFromJson(json, "is_principal", null));
        setSecundary(JsonUtil.getBooleanFromJson(json, "is_secondary", null));
        setMinIncome(JsonUtil.getIntFromJson(json, "minimum_income", null));
        if (JsonUtil.getJsonArrayFromJson(json, "js_required_filetype", null) != null) {
            JSONArray requireds = JsonUtil.getJsonArrayFromJson(json, "js_required_filetype", null);
            List<Integer> requiedFiles = new ArrayList<>();
            for (int j = 0; j < requireds.length(); j++) {
                requiedFiles.add(requireds.getInt(j));
            }
            setRequiredDocuments(requiedFiles);
        }
        setJsDefaults(JsonUtil.getJsonArrayFromJson(json, "js_defaults", null));
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

    public Boolean getPrincipal() {
        return principal;
    }

    public void setPrincipal(Boolean principal) {
        this.principal = principal;
    }

    public Boolean getSecundary() {
        return secundary;
    }

    public void setSecundary(Boolean secundary) {
        this.secundary = secundary;
    }

    public List<Integer> getRequiredDocuments() {
        return requiredDocuments;
    }

    public void setRequiredDocuments(List<Integer> requiredDocuments) {
        this.requiredDocuments = requiredDocuments;
    }

    public Integer getMinIncome() {
        return minIncome;
    }

    public void setMinIncome(Integer minIncome) {
        this.minIncome = minIncome;
    }

    public JSONArray getJsDefaults() {
        return jsDefaults;
    }

    public void setJsDefaults(JSONArray jsDefaults) {
        this.jsDefaults = jsDefaults;
    }
}
