package com.affirm.common.model.catalog;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by jrodriguez on 20/06/16.
 */
public class Cluster implements Serializable {

    public static final int A = 1;
    public static final int B = 2;
    public static final int C = 4;
    public static final int D = 6;
    public static final int E = 8;
    public static final int F = 11;
    public static final int Z = 14;
    public static final int SE = 21;

    private Integer id;
    private String cluster;
    private String descriptionKey;
    private String description;
    private Boolean active;
    private Integer orderId;
    private String subCluster;
    private Integer entityId;
    private Integer minScore;

    public void fillFromDb(JSONObject json) throws Exception {
        setId(JsonUtil.getIntFromJson(json, "cluster_id", null));
        setCluster(JsonUtil.getStringFromJson(json, "cluster", null));
        setDescriptionKey(JsonUtil.getStringFromJson(json, "description", null));
        setActive(JsonUtil.getBooleanFromJson(json, "is_active", null));
        setOrderId(JsonUtil.getIntFromJson(json, "order_id", null));
        setSubCluster(JsonUtil.getStringFromJson(json, "sub_cluster", null));
        setEntityId(JsonUtil.getIntFromJson(json, "entity_id", null));
        setMinScore(JsonUtil.getIntFromJson(json, "min_score", null));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCluster() {
        return cluster;
    }

    public void setCluster(String cluster) {
        this.cluster = cluster;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getDescriptionKey() {
        return descriptionKey;
    }

    public void setDescriptionKey(String descriptionKey) {
        this.descriptionKey = descriptionKey;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getSubCluster() {
        return subCluster;
    }

    public void setSubCluster(String subCluster) {
        this.subCluster = subCluster;
    }

    public Integer getEntityId() {
        return entityId;
    }

    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }

    public Integer getMinScore() {
        return minScore;
    }

    public void setMinScore(Integer minScore) {
        this.minScore = minScore;
    }
}
