package com.affirm.common.model.transactional;

import com.affirm.common.model.catalog.Entity;
import com.affirm.common.model.catalog.ExtranetMenu;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GatewayBaseEvent implements Serializable {

    public final static char STATUS_SUCCESS = 'S';
    public final static char STATUS_FAILED= 'F';
    public final static char STATUS_PENDING = 'P';

    public final static char TYPE_BASE= 'B';
    public final static char TYPE_SMS= 'S';

    private Integer id;
    private Entity entity;
    private Date registerDate;
    private Date finishDate;
    private Integer successCount;
    private Integer failedCount;
    private JSONObject jsAuxData;
    private Character type;
    private Character status;


    public void fillFromDb(JSONObject json, CatalogService catalog) {
        setId(JsonUtil.getIntFromJson(json, "collection_base_id", null));
        if(JsonUtil.getIntFromJson(json, "entity_id", null) != null) setEntity(catalog.getEntity(JsonUtil.getIntFromJson(json, "entity_id", null)));
        setRegisterDate(JsonUtil.getPostgresDateFromJson(json, "register_date", null));
        setFinishDate(JsonUtil.getPostgresDateFromJson(json, "finish_date", null));
        setSuccessCount(JsonUtil.getIntFromJson(json, "success_count", null));
        setFailedCount(JsonUtil.getIntFromJson(json, "failed_count", null));
        setJsAuxData(JsonUtil.getJsonObjectFromJson(json, "js_aux_data", null));
        setStatus(JsonUtil.getCharacterFromJson(json, "status", null));
        setType(JsonUtil.getCharacterFromJson(json, "type", null));
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

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public Date getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(Date finishDate) {
        this.finishDate = finishDate;
    }

    public Integer getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(Integer successCount) {
        this.successCount = successCount;
    }

    public Integer getFailedCount() {
        return failedCount;
    }

    public void setFailedCount(Integer failedCount) {
        this.failedCount = failedCount;
    }

    public JSONObject getJsAuxData() {
        return jsAuxData;
    }

    public void setJsAuxData(JSONObject jsAuxData) {
        this.jsAuxData = jsAuxData;
    }

    public Character getType() {
        return type;
    }

    public void setType(Character type) {
        this.type = type;
    }

    public Character getStatus() {
        return status;
    }

    public void setStatus(Character status) {
        this.status = status;
    }

    public String getEventName(){
        if(type != null){
            switch (type){
                case TYPE_BASE:
                    return "Base de campaña";
                case TYPE_SMS:
                    return "Envío de SMS";
            }
        }
        return null;
    }
}
