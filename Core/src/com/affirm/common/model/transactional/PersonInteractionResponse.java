package com.affirm.common.model.transactional;

import com.affirm.common.util.JsonUtil;
import com.affirm.wavy.model.ReceivedMessage;
import org.json.JSONObject;

import java.util.Date;

public class PersonInteractionResponse {

    private Integer id;
    private Integer personInteractionId;
    private Date registerDate;
    private Date receivedDate;
    private String message;
    private ReceivedMessage response;

    public void fillFromDb(JSONObject json) {
        setId(JsonUtil.getIntFromJson(json, "person_interaction_response_id", null));
        setPersonInteractionId(JsonUtil.getIntFromJson(json, "person_interaction_id", null));
        setRegisterDate(JsonUtil.getPostgresDateFromJson(json, "register_date", null));
        setReceivedDate(JsonUtil.getPostgresDateFromJson(json, "received_date", null));
        setMessage(JsonUtil.getStringFromJson(json, "message", null));

        JSONObject responseJson = JsonUtil.getJsonObjectFromJson(json, "js_response", null);
        if (responseJson != null) {
            response = new ReceivedMessage();
            response.fillFromJson(responseJson);
        }
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

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public Date getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(Date receivedDate) {
        this.receivedDate = receivedDate;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ReceivedMessage getResponse() {
        return response;
    }

    public void setResponse(ReceivedMessage response) {
        this.response = response;
    }
}
