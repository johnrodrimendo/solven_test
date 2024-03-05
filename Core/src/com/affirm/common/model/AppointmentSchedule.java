package com.affirm.common.model;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.util.Date;

public class AppointmentSchedule {
    private Integer appointmentScheduleId;
    private Date date;
    private String startTime;
    private String endTime;
    private String appointmentSchedule;

    public void fillFromDb(JSONObject json) {
        setAppointmentScheduleId(JsonUtil.getIntFromJson(json, "appointment_schedule_id", null));
        setDate(JsonUtil.getPostgresDateFromJson(json, "date_available", null));
        setStartTime(JsonUtil.getStringFromJson(json, "start_time", null));
        setEndTime(JsonUtil.getStringFromJson(json, "end_time", null));
        setAppointmentSchedule(JsonUtil.getStringFromJson(json, "appointment_schedule", null));
    }

    public Integer getAppointmentScheduleId() { return appointmentScheduleId; }

    public void setAppointmentScheduleId(Integer appointmentScheduleId) { this.appointmentScheduleId = appointmentScheduleId; }

    public Date getDate() { return date; }

    public void setDate(Date date) { this.date = date; }

    public String getStartTime() { return startTime; }

    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }

    public void setEndTime(String endTime) { this.endTime = endTime; }

    public String getAppointmentSchedule() {return appointmentSchedule; }

    public void setAppointmentSchedule(String appointmentSchedule) {this.appointmentSchedule = appointmentSchedule;}
}
