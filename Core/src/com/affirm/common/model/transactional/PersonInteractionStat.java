package com.affirm.common.model.transactional;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by jrodriguez on 31/08/16.
 */
public class PersonInteractionStat implements Serializable {

    private Integer id;
    private Integer personInteractionId;
    private Date eventDate;
    private String event;

    public void fillFromDb(JSONObject json) throws Exception {
        setId(JsonUtil.getIntFromJson(json, "person_interaction_stats_id", null));
        setPersonInteractionId(JsonUtil.getIntFromJson(json, "person_interaction_id", null));
        setEventDate(JsonUtil.getPostgresDateFromJson(json, "interaction_timestamp", null));
        setEvent(JsonUtil.getStringFromJson(json, "event", null));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPersonInteractionId() {
        return personInteractionId;
    }

    public void setPersonInteractionId(Integer personInteractionId) {
        this.personInteractionId = personInteractionId;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }
}
