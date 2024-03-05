package com.affirm.common.model.transactional;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

public class Conversion implements Serializable {

    public static final  String ENTITY_FACEBOOK = "";

    private Integer id;
    private Integer loanApplicationId;
    private String entity;
    private Date registerDate;
    private String instance;

    public void fillFromDb(JSONObject json) {
        setId(JsonUtil.getIntFromJson(json, "application_pixel_conversion_id", null));
        setLoanApplicationId(JsonUtil.getIntFromJson(json, "loan_application_id", null));
        setEntity(JsonUtil.getStringFromJson(json, "pixel", null));
        setRegisterDate(JsonUtil.getPostgresDateFromJson(json, "register_date", null));
        setInstance(JsonUtil.getStringFromJson(json, "instance", null));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getLoanApplicationId() {
        return loanApplicationId;
    }

    public void setLoanApplicationId(Integer loanApplicationId) {
        this.loanApplicationId = loanApplicationId;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }


    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public String getInstance() {
        return instance;
    }

    public void setInstance(String instance) {
        this.instance = instance;
    }
}
