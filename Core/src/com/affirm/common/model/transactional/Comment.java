package com.affirm.common.model.transactional;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

public class Comment implements Serializable {

    public static final String COMMENT_EVALUATION = "E";
    public static final String COMMENT_REASSIGN = "E";

    private Integer id;
    private Integer loanApplicationId;
    private String comment;
    private Date registerDate;
    private Integer sysUserId;
    private String commentType;
    private Integer entityUserId;

    public void fillFromDb(JSONObject json) {
        setId(JsonUtil.getIntFromJson(json, "loan_application_comment_id", null));
        setLoanApplicationId(JsonUtil.getIntFromJson(json, "loan_application_id", null));
        setComment(JsonUtil.getStringFromJson(json, "comment", null));
        setRegisterDate(JsonUtil.getPostgresDateFromJson(json, "register_date", null));
        setSysUserId(JsonUtil.getIntFromJson(json, "sysuser_id", null));
        setCommentType(JsonUtil.getStringFromJson(json, "comment_type", null));
        setEntityUserId(JsonUtil.getIntFromJson(json, "entity_user_id", null));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getLoanApplicationId() {
        return loanApplicationId;
    }

    public void setLoanApplicationId(Integer loanApplicationId) {
        this.loanApplicationId = loanApplicationId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public Integer getSysUserId() {
        return sysUserId;
    }

    public void setSysUserId(Integer sysUserId) {
        this.sysUserId = sysUserId;
    }

    public String getCommentType() {
        return commentType;
    }

    public void setCommentType(String commentType) {
        this.commentType = commentType;
    }

    public Integer getEntityUserId() {
        return entityUserId;
    }

    public void setEntityUserId(Integer entityUserId) {
        this.entityUserId = entityUserId;
    }
}
