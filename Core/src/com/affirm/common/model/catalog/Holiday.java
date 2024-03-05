package com.affirm.common.model.catalog;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by dev5 on 20/06/17.
 */
public class Holiday implements Serializable {

    private Integer id;
    private Date date;
    private Integer countryId;

    public void fillFromDb(JSONObject json) {
        setId(JsonUtil.getIntFromJson(json, "holiday_id", null));
        setDate(JsonUtil.getPostgresDateFromJson(json, "holiday", null));
        setCountryId(JsonUtil.getIntFromJson(json, "country_id", null));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Integer getCountryId() {
        return countryId;
    }

    public void setCountryId(Integer countryId) {
        this.countryId = countryId;
    }
}
