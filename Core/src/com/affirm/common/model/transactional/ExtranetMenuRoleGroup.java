package com.affirm.common.model.transactional;

import com.affirm.common.model.catalog.Role;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ExtranetMenuRoleGroup implements Serializable {

    private String code;
    private String label;
    private List<Role> roles;

    public void fillFromDb(JSONObject json, CatalogService catalogService) {
        setCode(JsonUtil.getStringFromJson(json, "code", null));
        setLabel(JsonUtil.getStringFromJson(json, "label", null));
        if (JsonUtil.getJsonArrayFromJson(json, "roles", null) != null) {
            JSONArray roleIdsArray = JsonUtil.getJsonArrayFromJson(json, "roles", null);
            roles = new ArrayList<>();
            for (int i = 0; i < roleIdsArray.length(); i++) {
                roles.add(catalogService.getRole(roleIdsArray.getInt(i)));
            }
        }
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }
}
