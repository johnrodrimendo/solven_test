package com.affirm.common.model.catalog;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by john on 04/01/17.
 */
public class EmployerGroup implements Serializable {

    private Integer id;
    private String groupName;
    private Date registerDate;

    public void fillFromDb(JSONObject json) {
        setId(JsonUtil.getIntFromJson(json, "employer_group_id", null));
        setGroupName(JsonUtil.getStringFromJson(json, "employer_group", null));
        setRegisterDate(JsonUtil.getPostgresDateFromJson(json, "register_date", null));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }
}
