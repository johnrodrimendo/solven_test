package com.affirm.common.model.transactional;

import com.affirm.common.model.catalog.Cluster;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Locale;

public class ExperianResult implements Serializable{

    private Integer entityId;
    private Integer entityWSId;
    private Integer score;
    private Integer incomes;
    private Integer clusterId;
    private Cluster cluster;
    private Cluster partnerCluster;


    public void fillFromDb(JSONObject json, CatalogService catalogService, Locale locale) throws Exception{
        setEntityId(JsonUtil.getIntFromJson(json, "entity_id", null));
        setEntityWSId(JsonUtil.getIntFromJson(json, "entity_ws_id", null));
        setScore(JsonUtil.getIntFromJson(json, "experian_score", null));
        setIncomes(JsonUtil.getIntFromJson(json, "incomes", null));
        setClusterId(JsonUtil.getIntFromJson(json, "cluster_id", null));
        setCluster(catalogService.getCluster(JsonUtil.getIntFromJson(json, "cluster_id", null), locale));
        if(JsonUtil.getIntFromJson(json, "partner_cluster_id", null) != null)
            setPartnerCluster(catalogService.getCluster(JsonUtil.getIntFromJson(json, "partner_cluster_id", null), locale));
    }

    public Integer getEntityId() {
        return entityId;
    }

    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }

    public Integer getEntityWSId() {
        return entityWSId;
    }

    public void setEntityWSId(Integer entityWSId) {
        this.entityWSId = entityWSId;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Integer getIncomes() {
        return incomes;
    }

    public void setIncomes(Integer incomes) {
        this.incomes = incomes;
    }

    public Integer getClusterId() {
        return clusterId;
    }

    public void setClusterId(Integer clusterId) {
        this.clusterId = clusterId;
    }

    public Cluster getCluster() {
        return cluster;
    }

    public void setCluster(Cluster cluster) {
        this.cluster = cluster;
    }

    public Cluster getPartnerCluster() {
        return partnerCluster;
    }

    public void setPartnerCluster(Cluster partnerCluster) {
        this.partnerCluster = partnerCluster;
    }
}
