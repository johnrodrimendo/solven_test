/**
 *
 */
package com.affirm.common.model.transactional;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

/**
 * @author jrodriguez
 */
public class QueryBot implements Serializable {

    public static final int STATUS_QUEUE = 1;
    public static final int STATUS_RUNNING = 2;
    public static final int STATUS_SUCCESS = 3;
    public static final int STATUS_FAIL = 4;
    public static final int STATUS_CANCELLED = 5;
    public static final int STATUS_SCHEDULED = 6;

    private Integer id;
    private Integer botId;
    private Integer statusId;
    private Date startTime;
    private Date finishTime;
    private Date registerDate;
    private JSONObject parameters;
    private Integer userId;
    private Date scheduledDate;


    public void fillFromDb(JSONObject json) {
        setId(JsonUtil.getIntFromJson(json, "query_id", null));
        setBotId(JsonUtil.getIntFromJson(json, "bot_id", null));
        setStatusId(JsonUtil.getIntFromJson(json, "status_id", null));
        setStartTime(JsonUtil.getPostgresDateFromJson(json, "start_time", null));
        setFinishTime(JsonUtil.getPostgresDateFromJson(json, "finish_time", null));
        setRegisterDate(JsonUtil.getPostgresDateFromJson(json, "register_date", null));
        setParameters(JsonUtil.getJsonObjectFromJson(json, "parameters", null));
        setUserId(JsonUtil.getIntFromJson(json, "user_id", null));
        setScheduledDate(JsonUtil.getPostgresDateFromJson(json, "scheduled_date", null));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getBotId() {
        return botId;
    }

    public void setBotId(Integer botId) {
        this.botId = botId;
    }

    public Integer getStatusId() {
        return statusId;
    }

    public void setStatusId(Integer statusId) {
        this.statusId = statusId;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public JSONObject getParameters() {
        return parameters;
    }

    public void setParameters(JSONObject parameters) {
        this.parameters = parameters;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Date getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(Date scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    @Override
    public String toString() {
        return "QueryBot{" +
                "id=" + id +
                ", botId=" + botId +
                ", statusId=" + statusId +
                ", startTime=" + startTime +
                ", finishTime=" + finishTime +
                ", registerDate=" + registerDate +
                ", parameters=" + parameters +
                ", userId=" + userId +
                ", scheduledDate=" + scheduledDate +
                '}';
    }
}
