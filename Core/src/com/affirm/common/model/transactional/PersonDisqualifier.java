package com.affirm.common.model.transactional;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

public class PersonDisqualifier implements Serializable {


    public static final String PEP = "P";
    public static final String OFAC = "O";
    public static final String FACTA = "F";

    private Integer id;
    private Integer personId;
    private String type;
    boolean isDisqualified;
    private Integer userFileId;
    private String detail;
    private Date registerDate;
    private String fileUrl;


    public void fillFromDb(JSONObject json) throws Exception {
        setId(JsonUtil.getIntFromJson(json, "disqualifier_id", null));
        setPersonId(JsonUtil.getIntFromJson(json, "person_id", null));
        setType(JsonUtil.getStringFromJson(json, "type", null));
        setDisqualified(JsonUtil.getBooleanFromJson(json, "disqualifier", null));
        setDetail(JsonUtil.getStringFromJson(json, "detail", null));
        setUserFileId(JsonUtil.getIntFromJson(json, "user_files_id", null));
        setRegisterDate(JsonUtil.getPostgresDateFromJson(json, "register_date", null));
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPersonId() {
        return personId;
    }

    public void setPersonId(Integer personId) {
        this.personId = personId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isDisqualified() {
        return isDisqualified;
    }

    public void setDisqualified(boolean disqualified) {
        isDisqualified = disqualified;
    }

    public Integer getUserFileId() {
        return userFileId;
    }

    public void setUserFileId(Integer userFileId) {
        this.userFileId = userFileId;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }
}
