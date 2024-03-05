package com.affirm.common.model.catalog;

import com.affirm.common.util.JsonUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class EntityUserType implements Serializable {

    public static final int BDS_USUARIO = 1;
    public static final int BDS_ADMIN = 2;
    public static final int BDS_SEGURIDAD = 3;
    public static final int BDS_VISTA = 4;
    public static final int BDS_ORGANIZADOR = 5;
    public static final int BDS_PRODUCTOR = 6;

    private Integer id;
    private String entityUserType;
    private Integer entityId;
    private List<Integer> roles;

    public void fillFromDb(JSONObject json) {
        setId(JsonUtil.getIntFromJson(json, "entity_user_type_id", null));
        setEntityUserType(JsonUtil.getStringFromJson(json, "entity_user_type", null));
        setEntityId(JsonUtil.getIntFromJson(json, "entity_id", null));
        if(JsonUtil.getJsonArrayFromJson(json, "ar_user_roles", null) != null){
            setRoles(new ArrayList<>());
            JSONArray array = JsonUtil.getJsonArrayFromJson(json, "ar_user_roles", null);
            for (int i = 0; i < array.length(); i++) {
                getRoles().add(array.getInt(i));
            }
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEntityUserType() {
        return entityUserType;
    }

    public void setEntityUserType(String entityUserType) {
        this.entityUserType = entityUserType;
    }

    public Integer getEntityId() {
        return entityId;
    }

    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }

    public List<Integer> getRoles() {
        return roles;
    }

    public void setRoles(List<Integer> roles) {
        this.roles = roles;
    }
}