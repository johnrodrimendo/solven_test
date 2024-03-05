package com.affirm.client.model;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

public class ExtranetDate implements Serializable {
    private Date startDate;
    private Date endDate;
    private String name;
    private String firstSurname;
    private String lastSurname;
    private String docNumber;
    private String phone;
    private String vehiculeModel;

    public void fillFromDb(JSONObject json) {
        setStartDate(JsonUtil.getPostgresDateFromJson(json, "start_time", null));
        setEndDate(JsonUtil.getPostgresDateFromJson(json, "end_time", null));
        setName(JsonUtil.getStringFromJson(json, "person_name", null));
        setFirstSurname(JsonUtil.getStringFromJson(json, "first_surname", null));
        setLastSurname(JsonUtil.getStringFromJson(json, "last_surname", null));
        setPhone(JsonUtil.getStringFromJson(json, "phone_number", null));
        setDocNumber(JsonUtil.getStringFromJson(json, "document_number", null));
        setVehiculeModel(JsonUtil.getStringFromJson(json, "model", null));
    }

    public Date getStartDate() { return startDate; }

    public void setStartDate(Date startDate) { this.startDate = startDate; }

    public Date getEndDate() { return endDate; }

    public void setEndDate(Date endDate) { this.endDate = endDate; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getFirstSurname() { return firstSurname; }

    public void setFirstSurname(String firstSurname) { this.firstSurname = firstSurname; }

    public String getLastSurname() { return lastSurname; }

    public void setLastSurname(String lastSurname) { this.lastSurname = lastSurname; }

    public String getDocNumber() { return docNumber; }

    public void setDocNumber(String docNumber) { this.docNumber = docNumber; }

    public String getPhone() { return phone; }

    public void setPhone(String phone) { this.phone = phone; }

    public String getVehiculeModel() { return vehiculeModel; }

    public void setVehiculeModel(String vehiculeModel) { this.vehiculeModel = vehiculeModel; }
}
