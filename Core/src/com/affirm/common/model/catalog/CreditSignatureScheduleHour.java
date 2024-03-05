package com.affirm.common.model.catalog;

import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

/**
 * Created by dev5 on 02/08/17.
 */
public class CreditSignatureScheduleHour {

    private Integer id;
    private String hour;

    public void fillFromDb(JSONObject json, CatalogService catalogService) {
        setId(JsonUtil.getIntFromJson(json, "signature_hour_id", null));
        setHour(JsonUtil.getStringFromJson(json, "signature_hour", null));
    }

    public CreditSignatureScheduleHour(){}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getStartHour(){
        return hour.replaceAll(" ", "").split("-")[0];
    }

    public String getFinishHour(){
        return hour.replaceAll(" ", "").split("-")[1];
    }
}
