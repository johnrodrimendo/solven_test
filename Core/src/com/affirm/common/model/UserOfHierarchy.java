package com.affirm.common.model;

import com.affirm.common.util.JsonUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UserOfHierarchy implements Serializable {

    private Integer entityUserId;
    private String personName;
    private String firstSurname;
    private List<UserOfHierarchy> producers;

    public void fillFromDb(JSONObject json) {
        setEntityUserId(JsonUtil.getIntFromJson(json, "entity_user_id", null));
        setPersonName(JsonUtil.getStringFromJson(json, "person_name", null));
        setFirstSurname(JsonUtil.getStringFromJson(json, "first_surname", null));
        JSONArray jsonArray = JsonUtil.getJsonArrayFromJson(json, "producers", null);

        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                if (!jsonArray.isNull(i)) {

                    if (producers == null)
                        producers = new ArrayList<>();

                    UserOfHierarchy userOfHierarchy = new UserOfHierarchy();
                    userOfHierarchy.fillFromDb(jsonArray.getJSONObject(i));
                    producers.add(userOfHierarchy);
                }
            }
        }
    }

    public Integer getEntityUserId() {
        return entityUserId;
    }

    public void setEntityUserId(Integer entityUserId) {
        this.entityUserId = entityUserId;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getFirstSurname() {
        return firstSurname;
    }

    public void setFirstSurname(String firstSurname) {
        this.firstSurname = firstSurname;
    }

    public List<UserOfHierarchy> getProducers() {
        return producers;
    }

    public void setProducers(List<UserOfHierarchy> producers) {
        this.producers = producers;
    }
}
