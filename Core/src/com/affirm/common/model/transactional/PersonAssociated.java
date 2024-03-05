package com.affirm.common.model.transactional;

import com.affirm.common.model.catalog.Entity;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by john on 04/01/17.
 */
public class PersonAssociated implements Serializable {

    private Integer personId;
    private Entity entity;
    private String associatedId;
    private String passbookNumber;
    private Boolean validated;


    public void fillFromDb(JSONObject json, CatalogService catalog) throws Exception {
        setPersonId(JsonUtil.getIntFromJson(json, "person_id", null));
        if (JsonUtil.getIntFromJson(json, "entity_id", null) != null) {
            setEntity(catalog.getEntity(JsonUtil.getIntFromJson(json, "entity_id", null)));
        }
        setAssociatedId(JsonUtil.getStringFromJson(json, "associated_id", null));
        setPassbookNumber(JsonUtil.getStringFromJson(json, "passbook_number", null));
        setValidated(JsonUtil.getBooleanFromJson(json, "is_validated", null));
    }

    public Integer getPersonId() {
        return personId;
    }

    public void setPersonId(Integer personId) {
        this.personId = personId;
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public String getAssociatedId() {
        return associatedId;
    }

    public void setAssociatedId(String associatedId) {
        this.associatedId = associatedId;
    }

    public String getPassbookNumber() {
        return passbookNumber;
    }

    public void setPassbookNumber(String passbookNumber) {
        this.passbookNumber = passbookNumber;
    }

    public Boolean getValidated() {
        return validated;
    }

    public void setValidated(Boolean validated) {
        this.validated = validated;
    }
}
