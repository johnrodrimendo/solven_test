package com.affirm.common.model.transactional;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

public class RucRccCalification implements Serializable {

    private Date rccDate;
    private Integer entities;
    private Double normal;
    private Double cpp;
    private Double defficient;
    private Double doubtful;
    private Double loss;
    private String calification;


    public void fillFromDb(JSONObject json) throws Exception {
        setRccDate(JsonUtil.getPostgresDateFromJson(json, "fe_repsbs", null));
        setEntities(JsonUtil.getIntFromJson(json, "ca_entida", null));
        setCpp(JsonUtil.getDoubleFromJson(json, "pr_calcpp", null));
        setNormal(JsonUtil.getDoubleFromJson(json, "pr_calnor", null));
        setDefficient(JsonUtil.getDoubleFromJson(json, "pr_caldef", null));
        setDoubtful(JsonUtil.getDoubleFromJson(json, "pr_caldud", null));
        setLoss(JsonUtil.getDoubleFromJson(json, "pr_calper", null));
        setCalification(JsonUtil.getStringFromJson(json, "calificacion", null));
    }


    public Date getRccDate() {
        return rccDate;
    }

    public void setRccDate(Date rccDate) {
        this.rccDate = rccDate;
    }

    public Integer getEntities() {
        return entities;
    }

    public void setEntities(Integer entities) {
        this.entities = entities;
    }

    public Double getNormal() {
        return normal;
    }

    public void setNormal(Double normal) {
        this.normal = normal;
    }

    public Double getCpp() {
        return cpp;
    }

    public void setCpp(Double cpp) {
        this.cpp = cpp;
    }

    public Double getDefficient() {
        return defficient;
    }

    public void setDefficient(Double defficient) {
        this.defficient = defficient;
    }

    public Double getDoubtful() {
        return doubtful;
    }

    public void setDoubtful(Double doubtful) {
        this.doubtful = doubtful;
    }

    public Double getLoss() {
        return loss;
    }

    public void setLoss(Double loss) {
        this.loss = loss;
    }

    public String getCalification() {
        return calification;
    }

    public void setCalification(String calification) {
        this.calification = calification;
    }

}
