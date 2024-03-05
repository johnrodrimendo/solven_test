package com.affirm.common.model.transactional;

import com.affirm.common.model.catalog.CountryParam;
import com.affirm.common.model.catalog.Entity;
import com.affirm.common.model.catalog.EntityUserType;
import com.affirm.common.model.catalog.Product;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.*;

public class UserEntity implements Serializable {

    private Integer id;
    private String email;
    private Date registerDate;
    private Boolean active;
    private String name;
    private String firstSurname;
    private String lastSurname;
    private Integer sysUserId;
    private List<Entity> entities;
    private List<Product> products;
    private Map<CountryParam, Boolean> countries;
    private Boolean mustChangePassword;
    private EntityUserType entityUserType;
    private Integer hierarchyLevel1;
    private Integer hierarchyLevel2;
    private Integer hierarchyLevel3;
    private Integer entityUserIdFromEntity;
    private Integer affiliatorId;
    private Integer entityAcquisitionChannelId;

    public void fillFromDb(JSONObject json, CatalogService catalog, Locale locale) {
        setId(JsonUtil.getIntFromJson(json, "entity_user_id", null));
        setEmail(JsonUtil.getStringFromJson(json, "email", null));
        setRegisterDate(JsonUtil.getPostgresDateFromJson(json, "register_date", null));
        setActive(JsonUtil.getBooleanFromJson(json, "is_active", null));
        setName(JsonUtil.getStringFromJson(json, "person_name", null));
        setFirstSurname(JsonUtil.getStringFromJson(json, "first_surname", null));
        setLastSurname(JsonUtil.getStringFromJson(json, "last_surname", null));
        setSysUserId(JsonUtil.getIntFromJson(json, "sysuser_id", null));
        if (JsonUtil.getJsonArrayFromJson(json, "js_entity_id", null) != null) {
            entities = new ArrayList<>();
            JSONArray array = JsonUtil.getJsonArrayFromJson(json, "js_entity_id", null);
            for (int i = 0; i < array.length(); i++) {
                entities.add(catalog.getEntity(array.getInt(i)));
            }
        }
        if (JsonUtil.getJsonArrayFromJson(json, "js_product_id", null) != null) {
            products = new ArrayList<>();
            JSONArray array = JsonUtil.getJsonArrayFromJson(json, "js_product_id", null);
            for (int i = 0; i < array.length(); i++) {
                products.add(catalog.getProduct(array.getInt(i)));
            }
        }
        setMustChangePassword(JsonUtil.getBooleanFromJson(json, "must_change_password", null));
        if (JsonUtil.getJsonArrayFromJson(json, "js_countries", null) != null) {
            countries = new HashMap<>();
            JSONArray array = JsonUtil.getJsonArrayFromJson(json, "js_countries", null);
            for (int i = 0; i < array.length(); i++) {
                JSONObject jsCountry = array.getJSONObject(i);
                countries.put(catalog.getCountryParam(jsCountry.getInt("country_id")), jsCountry.getBoolean("default"));
            }
        }
        if (JsonUtil.getIntFromJson(json, "entity_user_type_id", null) != null)
            setEntityUserType(catalog.getEntityUserType(JsonUtil.getIntFromJson(json, "entity_user_type_id", null)));
        setHierarchyLevel1(JsonUtil.getIntFromJson(json, "level_1", null));
        setHierarchyLevel2(JsonUtil.getIntFromJson(json, "level_2", null));
        setHierarchyLevel3(JsonUtil.getIntFromJson(json, "level_3", null));
        setEntityUserIdFromEntity(JsonUtil.getIntFromJson(json, "entity_user_id_from_entity", null));
        setAffiliatorId(JsonUtil.getIntFromJson(json, "affiliator_id", null));
        setEntityAcquisitionChannelId(JsonUtil.getIntFromJson(json, "entity_acquisition_channel_id", null));
    }

    public boolean containsEntityId(int entityId) {
        if (entities == null)
            return false;
        return entities.stream().anyMatch(e -> e.getId() == entityId);
    }

    public String getFullName() {
        if (name != null && !name.isEmpty()) {
            String fullName;
            fullName = name.concat(" ").concat(firstSurname != null ? firstSurname : "");
            if (lastSurname != null && !lastSurname.isEmpty()) fullName.concat(" ").concat(lastSurname);
            return fullName;
        }

        return "";
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirstSurname() {
        return firstSurname;
    }

    public void setFirstSurname(String firstSurname) {
        this.firstSurname = firstSurname;
    }

    public String getLastSurname() {
        return lastSurname;
    }

    public void setLastSurname(String lastSurname) {
        this.lastSurname = lastSurname;
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public void setEntities(List<Entity> entities) {
        this.entities = entities;
    }

    public Integer getSysUserId() {
        return sysUserId;
    }

    public void setSysUserId(Integer sysUserId) {
        this.sysUserId = sysUserId;
    }

    public Boolean getMustChangePassword() {
        return mustChangePassword;
    }

    public void setMustChangePassword(Boolean mustChangePassword) {
        this.mustChangePassword = mustChangePassword;
    }

    public Map<CountryParam, Boolean> getCountries() {
        return countries;
    }

    public void setCountries(Map<CountryParam, Boolean> countries) {
        this.countries = countries;
    }

    public EntityUserType getEntityUserType() {
        return entityUserType;
    }

    public void setEntityUserType(EntityUserType entityUserType) {
        this.entityUserType = entityUserType;
    }

    public Integer getHierarchyLevel1() {
        return hierarchyLevel1;
    }

    public void setHierarchyLevel1(Integer hierarchyLevel1) {
        this.hierarchyLevel1 = hierarchyLevel1;
    }

    public Integer getHierarchyLevel2() {
        return hierarchyLevel2;
    }

    public void setHierarchyLevel2(Integer hierarchyLevel2) {
        this.hierarchyLevel2 = hierarchyLevel2;
    }

    public Integer getHierarchyLevel3() {
        return hierarchyLevel3;
    }

    public void setHierarchyLevel3(Integer hierarchyLevel3) {
        this.hierarchyLevel3 = hierarchyLevel3;
    }

    public Integer getEntityUserIdFromEntity() {
        return entityUserIdFromEntity;
    }

    public void setEntityUserIdFromEntity(Integer entityUserIdFromEntity) {
        this.entityUserIdFromEntity = entityUserIdFromEntity;
    }

    public Integer getAffiliatorId() {
        return affiliatorId;
    }

    public void setAffiliatorId(Integer affiliatorId) {
        this.affiliatorId = affiliatorId;
    }

    public Integer getEntityAcquisitionChannelId() {
        return entityAcquisitionChannelId;
    }

    public void setEntityAcquisitionChannelId(Integer entityAcquisitionChannelId) {
        this.entityAcquisitionChannelId = entityAcquisitionChannelId;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }



}