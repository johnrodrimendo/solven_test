package com.affirm.security.model;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

public class SysUserSchedule {

    private Integer id;
    private String schedule;
    private String startTime;
    private String endTime;
    private boolean inactiveOnHolidays;

    public void fillFromDb(JSONObject json) {
        setId(JsonUtil.getIntFromJson(json, "sysuser_schedules_id", null));
        setSchedule(JsonUtil.getStringFromJson(json, "sysuser_schedule", null));
        setInactiveOnHolidays(JsonUtil.getBooleanFromJson(json, "inactive_on_holidays", false));
        setStartTime(JsonUtil.getStringFromJson(json, "start_time", null));
        setEndTime(JsonUtil.getStringFromJson(json, "end_time", null));
    }

    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id; }

    public String getSchedule() { return schedule; }

    public void setSchedule(String schedule) { this.schedule = schedule; }

    public void setInactiveOnHolidays(boolean inactiveOnHolidays) { this.inactiveOnHolidays = inactiveOnHolidays; }

    public boolean getInactiveOnHolidays() { return inactiveOnHolidays; }

    public String getStartTime() { return startTime; }

    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }

    public void setEndTime(String endTime) { this.endTime = endTime; }
}
