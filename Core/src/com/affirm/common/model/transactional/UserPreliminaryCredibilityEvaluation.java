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
 *
 */
public class UserPreliminaryCredibilityEvaluation implements Serializable {

	private Integer id;
	private Integer userId;
	private Date evaluationDate;
	private Integer credibilityId;
	private String credibilityMessage;
	private Boolean approved;

	public void fillFromDb(JSONObject json) throws Exception {
		setId(JsonUtil.getIntFromJson(json, "preliminary_credibility_evaluation_id", null));
		setUserId(JsonUtil.getIntFromJson(json, "user_id", null));
		setEvaluationDate(JsonUtil.getPostgresDateFromJson(json, "preliminary_credibility_evaluation_date", null));
		setCredibilityId(JsonUtil.getIntFromJson(json, "credibility_id", null));
		setCredibilityMessage(JsonUtil.getStringFromJson(json, "credibility_message", null));
		setApproved(JsonUtil.getBooleanFromJson(json, "is_approved", null));
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Date getEvaluationDate() {
		return evaluationDate;
	}

	public void setEvaluationDate(Date evaluationDate) {
		this.evaluationDate = evaluationDate;
	}

	public String getCredibilityMessage() {
		return credibilityMessage;
	}

	public void setCredibilityMessage(String credibilityMessage) {
		this.credibilityMessage = credibilityMessage;
	}

	public Integer getCredibilityId() {
		return credibilityId;
	}

	public void setCredibilityId(Integer credibilityId) {
		this.credibilityId = credibilityId;
	}

	public Boolean getApproved() {
		return approved;
	}

	public void setApproved(Boolean approved) {
		this.approved = approved;
	}

}
