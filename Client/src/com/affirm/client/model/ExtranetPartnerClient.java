package com.affirm.client.model;

import com.affirm.common.model.catalog.Entity;
import com.affirm.common.model.catalog.IdentityDocumentType;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

public class ExtranetPartnerClient {

    public enum ListType {
        WHITELIST("w", "Socio / Cliente"),
        BLACKLIST("b", "Lista negra");

        private String code;
        private String description;

        ListType(String code, String description) {
            this.code = code;
            this.description = description;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    private Integer rawAssociatedId;
    private Entity entityId;
    private String associatedId;
    private IdentityDocumentType identityDocumentType;
    private String documentNumber;
    private String listType;

    public void fillFromDb(JSONObject json, CatalogService catalog) {
        setRawAssociatedId(JsonUtil.getIntFromJson(json, "raw_associated_id", null));
        setEntityId(catalog.getEntity(JsonUtil.getIntFromJson(json, "entity_id", null)));
        setAssociatedId(JsonUtil.getStringFromJson(json, "associated_id", null));
        setIdentityDocumentType(catalog.getIdentityDocumentType(JsonUtil.getIntFromJson(json, "document_type_id", null)));
        setDocumentNumber(JsonUtil.getStringFromJson(json, "document_number", null));
        setListType(JsonUtil.getStringFromJson(json, "list_type", null));
    }

    public Integer getRawAssociatedId() {
        return rawAssociatedId;
    }

    public void setRawAssociatedId(Integer rawAssociatedId) {
        this.rawAssociatedId = rawAssociatedId;
    }

    public Entity getEntityId() {
        return entityId;
    }

    public void setEntityId(Entity entityId) {
        this.entityId = entityId;
    }

    public String getAssociatedId() {
        return associatedId;
    }

    public void setAssociatedId(String associatedId) {
        this.associatedId = associatedId;
    }

    public IdentityDocumentType getIdentityDocumentType() {
        return identityDocumentType;
    }

    public void setIdentityDocumentType(IdentityDocumentType identityDocumentType) {
        this.identityDocumentType = identityDocumentType;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getListType() {
        return listType;
    }

    public void setListType(String listType) {
        this.listType = listType;
    }

}
