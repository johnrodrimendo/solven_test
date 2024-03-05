/**
 * 
 */
package com.affirm.common.model.transactional;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * @author jrodriguez
 *
 */
public class ExternalUserInfoState implements Serializable {

	private Integer userId;
	private Integer botId;
	private Integer statusId;
	private Boolean result;

	public ExternalUserInfoState() {
	}

	public ExternalUserInfoState(JSONObject json) {
		userId = JsonUtil.getIntFromJson(json, "user_id", null);
		botId = JsonUtil.getIntFromJson(json, "bot_id", null);
		result = JsonUtil.getBooleanFromJson(json, "result", false);
		statusId = JsonUtil.getIntFromJson(json, "status_id", null);
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getBotId() {
		return botId;
	}

	public void setBotId(Integer botId) {
		this.botId = botId;
	}

	public Boolean getResult() {
		return result;
	}

	public void setResult(Boolean result) {
		this.result = result;
	}

	public Integer getStatusId() {
		return statusId;
	}

	public void setStatusId(Integer statusId) {
		this.statusId = statusId;
	}
}
