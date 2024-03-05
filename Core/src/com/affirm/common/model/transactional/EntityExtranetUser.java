package com.affirm.common.model.transactional;

import com.affirm.common.model.catalog.ExtranetMenu;
import com.affirm.common.model.catalog.ProductCategory;
import com.affirm.common.model.catalog.Role;
import com.affirm.common.model.form.ExtranetRoleActionForm;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class EntityExtranetUser implements Serializable {

    private Integer userId;
    private String userName;
    private String email;
    private Boolean active;
    private List<Integer> roles;
    private ExtranetRoleActionForm form;
    private List<MenuEntityProductCategory> menuEntityProductCategories;

    private Integer bandeja1Role;
    private Integer bandeja2Role;
    private Integer bandeja3Role;
    private Integer bandeja4Role;
    private Integer bandeja5Role;
    private Integer bandeja6Role;
    private Integer bandeja7Role;

    public void fillFromDb(JSONObject json, CatalogService catalogService) throws Exception {
        setUserId(JsonUtil.getIntFromJson(json, "entity_user_id", null));
        setUserName(JsonUtil.getStringFromJson(json, "entity_user_name", null));
        setEmail(JsonUtil.getStringFromJson(json, "email", null));
        setActive(JsonUtil.getBooleanFromJson(json, "is_active", null));
        if (JsonUtil.getJsonArrayFromJson(json, "roles", null) != null) {
            JSONArray arrayRoles = JsonUtil.getJsonArrayFromJson(json, "roles", null);
            setRoles(new ArrayList<>());
            for (int i = 0; i < arrayRoles.length(); i++) {
                getRoles().add(arrayRoles.getInt(i));
            }
        }
        if (JsonUtil.getStringFromJson(json, "js_menu_entity_product_category", null) != null && !JsonUtil.getStringFromJson(json, "js_menu_entity_product_category", null) .isEmpty()) {
            menuEntityProductCategories = new ArrayList<>();
            JSONArray array = new JSONArray(JsonUtil.getStringFromJson(json, "js_menu_entity_product_category", null));
            for (int i = 0; i < array.length(); i++) {
                MenuEntityProductCategory menuEntityProductCategory = new Gson().fromJson(array.getJSONObject(i).toString(), MenuEntityProductCategory.class);
                menuEntityProductCategories.add(menuEntityProductCategory);
            }
            for (MenuEntityProductCategory menuEntityProductCategory : menuEntityProductCategories) {
                menuEntityProductCategory.setExtranetMenu(catalogService.getExtranetMenu(menuEntityProductCategory.getMenuEntityId()));
            }
        }
    }

    public boolean containsExtranetMenuRoleGroup(ExtranetMenuRoleGroup roleGroup){
        if(roles == null)
            return false;
        for(Role role : roleGroup.getRoles()){
            if(!roles.contains(role.getId()))
                return false;
        }
        return true;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Integer> getRoles() {
        return roles;
    }

    public Boolean isActive() { return active; }

    public void setActive(Boolean active) { this.active = active; }

    public void setRoles(List<Integer> roles) {
        this.roles = roles;
    }

    public Integer getBandeja1Role() {
        return bandeja1Role;
    }

    public void setBandeja1Role(Integer bandeja1Role) {
        this.bandeja1Role = bandeja1Role;
    }

    public Integer getBandeja2Role() {
        return bandeja2Role;
    }

    public void setBandeja2Role(Integer bandeja2Role) {
        this.bandeja2Role = bandeja2Role;
    }

    public Integer getBandeja3Role() {
        return bandeja3Role;
    }

    public void setBandeja3Role(Integer bandeja3Role) {
        this.bandeja3Role = bandeja3Role;
    }

    public Integer getBandeja4Role() {
        return bandeja4Role;
    }

    public void setBandeja4Role(Integer bandeja4Role) {
        this.bandeja4Role = bandeja4Role;
    }

    public Integer getBandeja5Role() {
        return bandeja5Role;
    }

    public void setBandeja5Role(Integer bandeja5Role) {
        this.bandeja5Role = bandeja5Role;
    }

    public Integer getBandeja6Role() { return bandeja6Role; }

    public void setBandeja6Role(Integer bandeja6Role) { this.bandeja6Role = bandeja6Role; }

    public Integer getBandeja7Role() {
        return bandeja7Role;
    }

    public void setBandeja7Role(Integer bandeja7Role) {
        this.bandeja7Role = bandeja7Role;
    }

    public ExtranetRoleActionForm getForm() {
        return form;
    }

    public void setForm(ExtranetRoleActionForm form) {
        this.form = form;
    }

    public List<MenuEntityProductCategory> getMenuEntityProductCategories() {
        return menuEntityProductCategories;
    }

    public void setMenuEntityProductCategories(List<MenuEntityProductCategory> menuEntityProductCategories) {
        this.menuEntityProductCategories = menuEntityProductCategories;
    }

    public static class MenuEntityProductCategory implements Serializable{
        private Integer menuEntityId;
        private List<Integer> productCategories;
        private ExtranetMenu extranetMenu;
        private List<String> roleGroups;

        public Integer getMenuEntityId() {
            return menuEntityId;
        }

        public void setMenuEntityId(Integer menuEntityId) {
            this.menuEntityId = menuEntityId;
        }

        public List<Integer> getProductCategories() {
            return productCategories;
        }

        public void setProductCategories(List<Integer> productCategories) {
            this.productCategories = productCategories;
        }

        public List<String> getRoleGroups() {
            return roleGroups;
        }

        public void setRoleGroups(List<String> roleGroups) {
            this.roleGroups = roleGroups;
        }

        public ExtranetMenu getExtranetMenu() {
            return extranetMenu;
        }

        public void setExtranetMenu(ExtranetMenu extranetMenu) {
            this.extranetMenu = extranetMenu;
        }
    }
}
