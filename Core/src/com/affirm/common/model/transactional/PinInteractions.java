package com.affirm.common.model.transactional;

import com.affirm.common.util.JsonUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PinInteractions {

    private List<Integer> smsInteractions;
    private Date lastSmsInteractionSend;
    private List<Integer> automaticCallInteractions;
    private Date lastAutomaticCallInteractionSend;
    private Boolean showAssistedHelp;

    public void fillFromDb(JSONObject json) {
        if(JsonUtil.getJsonArrayFromJson(json, "smsInteractions", null) != null){
            setSmsInteractions(new ArrayList<>());
            JSONArray interactionArray = JsonUtil.getJsonArrayFromJson(json, "smsInteractions", null);
            for(int i=0; i<interactionArray.length(); i++){
                getSmsInteractions().add(interactionArray.getInt(i));
            }
        }
        setLastSmsInteractionSend(JsonUtil.getPostgresDateFromJson(json, "lastSmsInteractionSend", null));
        if(JsonUtil.getJsonArrayFromJson(json, "automaticCallInteractions", null) != null){
            setAutomaticCallInteractions(new ArrayList<>());
            JSONArray interactionArray = JsonUtil.getJsonArrayFromJson(json, "automaticCallInteractions", null);
            for(int i=0; i<interactionArray.length(); i++){
                getAutomaticCallInteractions().add(interactionArray.getInt(i));
            }
        }
        setLastAutomaticCallInteractionSend(JsonUtil.getPostgresDateFromJson(json, "lastAutomaticCallInteractionSend", null));
        setShowAssistedHelp(JsonUtil.getBooleanFromJson(json, "showAssistedHelp", null));
    }

    public List<Integer> getSmsInteractions() {
        return smsInteractions;
    }

    public void setSmsInteractions(List<Integer> smsInteractions) {
        this.smsInteractions = smsInteractions;
    }

    public Date getLastSmsInteractionSend() {
        return lastSmsInteractionSend;
    }

    public void setLastSmsInteractionSend(Date lastSmsInteractionSend) {
        this.lastSmsInteractionSend = lastSmsInteractionSend;
    }

    public List<Integer> getAutomaticCallInteractions() {
        return automaticCallInteractions;
    }

    public void setAutomaticCallInteractions(List<Integer> automaticCallInteractions) {
        this.automaticCallInteractions = automaticCallInteractions;
    }

    public Date getLastAutomaticCallInteractionSend() {
        return lastAutomaticCallInteractionSend;
    }

    public void setLastAutomaticCallInteractionSend(Date lastAutomaticCallInteractionSend) {
        this.lastAutomaticCallInteractionSend = lastAutomaticCallInteractionSend;
    }

    public Boolean getShowAssistedHelp() {
        return showAssistedHelp;
    }

    public void setShowAssistedHelp(Boolean showAssistedHelp) {
        this.showAssistedHelp = showAssistedHelp;
    }
}
