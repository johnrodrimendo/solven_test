package com.affirm.common.model;

import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ExtranetNote {

    public final static String PUB_TYPE = "P";
    public final static String DEV_TYPE = "D";

    private Integer id;
    private Integer entityId;
    private Integer extranetMenuId;
    private Integer entityUserId;
    private Date registerDate;
    private String type;
    private String note;
    private String username;

    public void fillFromDb(JSONObject json, CatalogService catalogService) {
        setId(JsonUtil.getIntFromJson(json, "extranet_note_id", null));
        setRegisterDate(JsonUtil.getPostgresDateFromJson(json, "register_date", null));
        setEntityUserId(JsonUtil.getIntFromJson(json, "entity_user_id", null));
        setEntityId(JsonUtil.getIntFromJson(json, "entity_id", null));
        setExtranetMenuId(JsonUtil.getIntFromJson(json, "extranet_menu_id", null));
        setUsername(JsonUtil.getStringFromJson(json, "user_name", null));
        setType(JsonUtil.getStringFromJson(json, "type", null));
        setNote(JsonUtil.getStringFromJson(json, "note", null));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getEntityId() {
        return entityId;
    }

    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }

    public Integer getExtranetMenuId() {
        return extranetMenuId;
    }

    public void setExtranetMenuId(Integer extranetMenuId) {
        this.extranetMenuId = extranetMenuId;
    }

    public Integer getEntityUserId() {
        return entityUserId;
    }

    public void setEntityUserId(Integer entityUserId) {
        this.entityUserId = entityUserId;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTypeDescription(){
        if(type != null){
            switch (type){
                case PUB_TYPE:
                    return "Publicidad";
                case DEV_TYPE:
                    return "Desarrollo";
            }
        }
        return type;
    }

    public String getDataToRow(){
        JSONObject object = new JSONObject();
        object.put("type_description", getTypeDescription());
        object.put("user",getUsername());
        object.put("id",getId());
        object.put("note",getNote());
        object.put("type",getType());
        if(registerDate != null){
            SimpleDateFormat sdf = new SimpleDateFormat(com.affirm.system.configuration.Configuration.BACKOFFICE_FRONT_SHORT_DATE_FORMAT);
            object.put("date",sdf.format(registerDate));
        }
        return object.toString();
    }
}
