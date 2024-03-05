package com.affirm.common.model.transactional;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Romulo Galindo Tanta
 */
public class LogSmsBulkSend implements Serializable {

    private Integer id;
    private Date registerDate;
    private Integer failed;
    private Integer success;
    private Integer entityId;
    private Integer productId;
    private Integer SysUserId;
    private Integer queryBotId;

    public void fillFromDb(JSONObject json) {
        setId(JsonUtil.getIntFromJson(json, "lg_sms_approved_id", null));
        setRegisterDate(JsonUtil.getPostgresDateFromJson(json, "register_date", null));
        setFailed(JsonUtil.getIntFromJson(json, "failed", null));
        setSuccess(JsonUtil.getIntFromJson(json, "success", null));
        setEntityId(JsonUtil.getIntFromJson(json, "entity_id", null));
        setProductId(JsonUtil.getIntFromJson(json, "product_id", null));
        setSysUserId(JsonUtil.getIntFromJson(json, "sysuser_id", null));
        setQueryBotId(JsonUtil.getIntFromJson(json, "query_id", null));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public Integer getFailed() {
        return failed;
    }

    public void setFailed(Integer failed) {
        this.failed = failed;
    }

    public Integer getSuccess() {
        return success;
    }

    public void setSuccess(Integer success) {
        this.success = success;
    }

    public Integer getEntityId() {
        return entityId;
    }

    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getSysUserId() {
        return SysUserId;
    }

    public void setSysUserId(Integer sysUserId) {
        SysUserId = sysUserId;
    }

    public Integer getQueryBotId() {
        return queryBotId;
    }

    public void setQueryBotId(Integer queryBotId) {
        this.queryBotId = queryBotId;
    }
}
