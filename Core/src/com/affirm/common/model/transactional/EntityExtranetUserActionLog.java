package com.affirm.common.model.transactional;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

public class EntityExtranetUserActionLog implements Serializable {

    private Integer entityUserId;
    private String entityUserName;
    private Integer permissionId;
    private String permission;
    private Date actionDate;

    public void fillFromDb(JSONObject json) throws Exception {
        setEntityUserId(JsonUtil.getIntFromJson(json, "entity_user_id", null));
        setEntityUserName(JsonUtil.getStringFromJson(json, "entity_user_name", null));
        setPermissionId(JsonUtil.getIntFromJson(json, "permission_id", null));
        setPermission(JsonUtil.getStringFromJson(json, "permission", null));
        setDate(JsonUtil.getPostgresDateFromJson(json, "action_date", null));
    }

    public Integer getEntityUserId() { return entityUserId; }

    public void setEntityUserId(Integer id) { this.entityUserId = id; }

    public Date getDate() { return getActionDate(); }

    public void setDate(Date date) { this.setActionDate(date); }

    public String getEntityUserName() { return entityUserName; }

    public void setEntityUserName(String entityUserName) { this.entityUserName = entityUserName; }

    public String getPermission() { return permission; }

    public void setPermission(String permission) { this.permission = permission; }

    public Integer getPermissionId() { return permissionId; }

    public void setPermissionId(Integer permissionId) { this.permissionId = permissionId; }

    public Date getActionDate() {
        return actionDate;
    }

    public void setActionDate(Date actionDate) {
        this.actionDate = actionDate;
    }
}
