package com.affirm.common.model.transactional;

import com.affirm.common.model.catalog.Entity;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;

public class CustomEntityProfession implements Serializable {

    private Integer id;
    private Entity entity;
    private Long identifier;
    private String description;

    public void fillFromDb(JSONObject json, CatalogService catalogService) {
        setId(JsonUtil.getIntFromJson(json, "custom_entity_profession_id", null));
        if (JsonUtil.getIntFromJson(json, "entity_id", null) != null)
            setEntity(catalogService.getEntity(JsonUtil.getIntFromJson(json, "entity_id", null)));
        setIdentifier(JsonUtil.getLongFromJson(json, "identificador", null));
        setDescription(JsonUtil.getStringFromJson(json, "descripcion", null));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public Long getIdentifier() {
        return identifier;
    }

    public void setIdentifier(Long identifier) {
        this.identifier = identifier;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


}
