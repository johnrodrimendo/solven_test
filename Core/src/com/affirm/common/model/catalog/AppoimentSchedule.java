package com.affirm.common.model.catalog;

import com.affirm.common.util.JsonUtil;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by dev5 on 05/06/18.
 */
public class AppoimentSchedule {

    private int id;
    private String name;
    private String startTime;
    private String endTime;
    private JSONArray days;
    private Integer capacity;


    public void fillFromDb(JSONObject json) {
        setId(JsonUtil.getIntFromJson(json, "appointment_schedule_id", null));
        setName(JsonUtil.getStringFromJson(json, "appointment_schedule", null));
        setStartTime(JsonUtil.getStringFromJson(json, "start_time", null));
        setEndTime(JsonUtil.getStringFromJson(json, "end_time", null));
        setDays(JsonUtil.getJsonArrayFromJson(json, "ar_days", null));
        setCapacity(JsonUtil.getIntFromJson(json, "capacity", null));
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public JSONArray getDays() {
        return days;
    }

    public void setDays(JSONArray days) {
        this.days = days;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }
}

