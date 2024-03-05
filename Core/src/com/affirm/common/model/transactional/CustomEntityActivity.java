package com.affirm.common.model.transactional;

import com.affirm.common.model.catalog.Entity;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CustomEntityActivity implements Serializable {

    private Integer id;
    private Entity entity;
    private Integer identifier;
    private String description;
    private Long regulatoryEntityIdentifier;

    public void fillFromDb(JSONObject json, CatalogService catalogService) {
        setId(JsonUtil.getIntFromJson(json, "custom_activity_id", null));
        if (JsonUtil.getIntFromJson(json, "entity_id", null) != null)
            setEntity(catalogService.getEntity(JsonUtil.getIntFromJson(json, "entity_id", null)));
        setIdentifier(JsonUtil.getIntFromJson(json, "identificador", null));
        setRegulatoryEntityIdentifier(JsonUtil.getLongFromJson(json, "identificador_entidad_reguladora", null));
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

    public Integer getIdentifier() {
        return identifier;
    }

    public void setIdentifier(Integer identifier) {
        this.identifier = identifier;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getRegulatoryEntityIdentifier() {
        return regulatoryEntityIdentifier;
    }

    public void setRegulatoryEntityIdentifier(Long regulatoryEntityIdentifier) {
        this.regulatoryEntityIdentifier = regulatoryEntityIdentifier;
    }
}
