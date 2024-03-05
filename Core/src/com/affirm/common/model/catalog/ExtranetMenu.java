package com.affirm.common.model.catalog;

import com.affirm.common.model.transactional.ExtranetMenuEntity;
import com.affirm.common.model.transactional.ExtranetMenuRoleGroup;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import com.affirm.system.configuration.Configuration;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ExtranetMenu implements Serializable {

    public final static int USERS_MENU = 2;
    public final static int TO_VERIFY_CREDITS_MENU = 2;
    public final static int TO_UPLOAD_CREDITS_MENU = 3;
    public final static int TO_DISBURSE_CREDITS_MENU = 4;
    public final static int DISBURSEMENT_CREDITS_MENU = 5;
    public final static int REPORT_MENU = 6;
    public final static int TO_DELIVER_TC_MENU = 7;
    public final static int DELIVERED_TC_MENU = 8;
    public final static int MONITOR_MENU = 9;
    public final static int FUNNEL_BANBIF_MENU = 10;
    public final static int TO_DELIVER_LEADS_MENU = 11;
    public final static int DELIVERED_LEADS_MENU = 12;
    public final static int UPLOAD_PREAPPROVED_BASE_MENU = 13;
    public final static int BEING_PROCESSED_MENU = 14;
    public final static int UPLOAD_NEGATIVE_BAASE_MENU = 15;
    public final static int FUNNEL_MENU = 16;
    public final static int UPLOAD_DISBURSEMENTS_MENU = 17;
    public final static int REPORTS_MENU = 18;
    public final static int CALL_CENTER_MENU = 19;
    public final static int REJECTED_MENU = 20;
    public final static int EVALUATION_MENU = 21;
    public final static int PAYMENT_COMMITMENT_MENU = 23;
    public final static int COMMUNICATIONS_MENU = 24;
    public final static int FUNNEL_COLLECTIONS_MENU = 25;
    public final static int MARKETING_CAMPAIGN_MENU = 26;

    private Integer id;
    private String name;
    private List<ExtranetMenuRoleGroup> defaultRoleGroups;
    private String url;
    private String icon;
    private Integer productCategoryId;
    private List<MenuEntityProductCategory> menuEntityProductCategories;

    public void fillFromDb(JSONObject json, CatalogService catalogService) {
        setId(JsonUtil.getIntFromJson(json, "extranet_menu_id", null));
        setName(JsonUtil.getStringFromJson(json, "name", null));
        setUrl(JsonUtil.getStringFromJson(json, "url", null));
        setIcon(JsonUtil.getStringFromJson(json, "icon", null));
        if (JsonUtil.getJsonArrayFromJson(json, "js_default_role_groups", null) != null) {
            JSONArray roleGroupsArray = JsonUtil.getJsonArrayFromJson(json, "js_default_role_groups", null);
            defaultRoleGroups = new ArrayList<>();
            for (int i = 0; i < roleGroupsArray.length(); i++) {
                ExtranetMenuRoleGroup group = new ExtranetMenuRoleGroup();
                group.fillFromDb(roleGroupsArray.getJSONObject(i), catalogService);
                defaultRoleGroups.add(group);
            }
        }
        setProductCategoryId(JsonUtil.getIntFromJson(json, "product_categroy_id", null));
        if (JsonUtil.getJsonArrayFromJson(json, "js_entity_product_category_configuration", null) != null) {
            menuEntityProductCategories = new ArrayList<>();
            JSONArray entityProductConfigurationArray = JsonUtil.getJsonArrayFromJson(json, "js_entity_product_category_configuration", null);
            for (int i = 0; i < entityProductConfigurationArray.length(); i++) {
                MenuEntityProductCategory data = new Gson().fromJson(entityProductConfigurationArray.getJSONObject(i).toString(), MenuEntityProductCategory.class);
                menuEntityProductCategories.add(data);
            }
            for (MenuEntityProductCategory menuEntityProductCategory : menuEntityProductCategories) {
                if(menuEntityProductCategory.getProductCategoryId() != null) menuEntityProductCategory.setProductCategory(catalogService.getCatalogById(ProductCategory.class, menuEntityProductCategory.getProductCategoryId(), Configuration.getDefaultLocale()));
            }
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ExtranetMenuRoleGroup> getDefaultRoleGroups() {
        return defaultRoleGroups;
    }

    public void setDefaultRoleGroups(List<ExtranetMenuRoleGroup> defaultRoleGroups) {
        this.defaultRoleGroups = defaultRoleGroups;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Integer getProductCategoryId() {
        return productCategoryId;
    }

    public void setProductCategoryId(Integer productCategoryId) {
        this.productCategoryId = productCategoryId;
    }

    public List<MenuEntityProductCategory> getMenuEntityProductCategories() {
        return menuEntityProductCategories;
    }

    public void setMenuEntityProductCategories(List<MenuEntityProductCategory> menuEntityProductCategories) {
        this.menuEntityProductCategories = menuEntityProductCategories;
    }

    public static class MenuEntityProductCategory implements Serializable{

        private Integer productCategoryId;
        private ProductCategory productCategory;
        private MenuEntityProductCategoryConfiguration configuration;

        public Integer getProductCategoryId() {
            return productCategoryId;
        }

        public void setProductCategoryId(Integer productCategoryId) {
            this.productCategoryId = productCategoryId;
        }

        public MenuEntityProductCategoryConfiguration getConfiguration() {
            return configuration;
        }

        public void setConfiguration(MenuEntityProductCategoryConfiguration configuration) {
            this.configuration = configuration;
        }

        public ProductCategory getProductCategory() {
            return productCategory;
        }

        public void setProductCategory(ProductCategory productCategory) {
            this.productCategory = productCategory;
        }
    }

    public static class MenuEntityProductCategoryConfiguration implements Serializable{
        private Boolean editableFields;

        public Boolean getEditableFields() {
            return editableFields;
        }

        public void setEditableFields(Boolean editableFields) {
            this.editableFields = editableFields;
        }
    }
}
