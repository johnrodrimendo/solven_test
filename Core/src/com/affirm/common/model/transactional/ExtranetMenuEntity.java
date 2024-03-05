package com.affirm.common.model.transactional;

import com.affirm.common.model.catalog.ExtranetMenu;
import com.affirm.common.model.catalog.ProductCategory;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import com.affirm.system.configuration.Configuration;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExtranetMenuEntity implements Serializable {

    private Integer id;
    private ExtranetMenu extranetMenu;
    private Integer entityId;
    private Boolean active;
    private Date registerDate;
    private List<ExtranetMenuRoleGroup> roleGroups;


    public void fillFromDb(JSONObject json, CatalogService catalog) {
        setId(JsonUtil.getIntFromJson(json, "extranet_menu_entity_id", null));
        setExtranetMenu(catalog.getExtranetMenu(JsonUtil.getIntFromJson(json, "extranet_menu_id", null)));
        setEntityId(JsonUtil.getIntFromJson(json, "entity_id", null));
        setActive(JsonUtil.getBooleanFromJson(json, "is_active", null));
        setRegisterDate(JsonUtil.getPostgresDateFromJson(json, "register_date", null));
        if (JsonUtil.getJsonArrayFromJson(json, "js_role_groups", null) != null) {
            JSONArray roleGroupsArray = JsonUtil.getJsonArrayFromJson(json, "js_role_groups", null);
            roleGroups = new ArrayList<>();
            for (int i = 0; i < roleGroupsArray.length(); i++) {
                ExtranetMenuRoleGroup group = new ExtranetMenuRoleGroup();
                group.fillFromDb(roleGroupsArray.getJSONObject(i), catalog);
                roleGroups.add(group);
            }
        }
    }

    public List<ExtranetMenuRoleGroup> getroleGroupToShow() {
        return roleGroups != null ? roleGroups : extranetMenu.getDefaultRoleGroups();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ExtranetMenu getExtranetMenu() {
        return extranetMenu;
    }

    public void setExtranetMenu(ExtranetMenu extranetMenu) {
        this.extranetMenu = extranetMenu;
    }

    public Integer getEntityId() {
        return entityId;
    }

    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public List<ExtranetMenuRoleGroup> getRoleGroups() {
        return roleGroups;
    }

    public void setRoleGroups(List<ExtranetMenuRoleGroup> roleGroups) {
        this.roleGroups = roleGroups;
    }

}
