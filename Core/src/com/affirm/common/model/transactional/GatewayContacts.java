package com.affirm.common.model.transactional;

import com.affirm.common.model.catalog.ContactResult;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by sTbn on 9/09/16.
 */
public class GatewayContacts implements Serializable {

    private Integer id;
    private Integer personId;
    private Integer creditId;
    private String creditCode;
    private Date registerDate;
    private Integer sysUserdId;
    private ContactResult contactResult;
    private Date promissoryNote;
    private String userPersonName;
    private String userFirstSurname;
    private String userLastSurname;
    private String comment;

    public void fillFromDb(JSONObject json, CatalogService catalog) throws Exception {
        setId(JsonUtil.getIntFromJson(json, "collection_contact_id", null));
        setCreditId(JsonUtil.getIntFromJson(json, "credit_id", null));
        setCreditCode(JsonUtil.getStringFromJson(json, "credit_code", null));
        setPersonId(JsonUtil.getIntFromJson(json, "person_id", null));
        setSysUserdId(JsonUtil.getIntFromJson(json, "sysuser_id", null));
        setUserPersonName(JsonUtil.getStringFromJson(json, "person_name", null));
        setUserFirstSurname(JsonUtil.getStringFromJson(json, "first_surname", null));
        setUserLastSurname(JsonUtil.getStringFromJson(json, "last_surname", null));
        setComment(JsonUtil.getStringFromJson(json, "comment", null));
        setRegisterDate(JsonUtil.getPostgresDateFromJson(json, "register_date", null));
        if (JsonUtil.getIntFromJson(json, "contact_result_id", null) != null) {
            setContactResult(catalog.getContactResult(JsonUtil.getIntFromJson(json, "contact_result_id", null)));
        }
        setPromissoryNote(JsonUtil.getPostgresDateFromJson(json, "promissory_note_date", null));
    }

    public String getFullName() {
        String fullname = "";
        if (userPersonName != null) {
            fullname = fullname + userPersonName + " ";
        }
        if (userFirstSurname != null) {
            fullname = fullname + userFirstSurname + " ";
        }
        if (userLastSurname != null) {
            fullname = fullname + userLastSurname + " ";
        }
        return fullname;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCreditId() {
        return creditId;
    }

    public void setCreditId(Integer creditId) {
        this.creditId = creditId;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public Integer getSysUserdId() {
        return sysUserdId;
    }

    public void setSysUserdId(Integer sysUserdId) {
        this.sysUserdId = sysUserdId;
    }

    public ContactResult getContactResult() {
        return contactResult;
    }

    public void setContactResult(ContactResult contactResult) {
        this.contactResult = contactResult;
    }

    public Date getPromissoryNote() {
        return promissoryNote;
    }

    public void setPromissoryNote(Date promissoryNote) {
        this.promissoryNote = promissoryNote;
    }

    public String getUserPersonName() {
        return userPersonName;
    }

    public void setUserPersonName(String userPersonName) {
        this.userPersonName = userPersonName;
    }

    public String getUserFirstSurname() {
        return userFirstSurname;
    }

    public void setUserFirstSurname(String userFirstSurname) {
        this.userFirstSurname = userFirstSurname;
    }

    public String getUserLastSurname() {
        return userLastSurname;
    }

    public void setUserLastSurname(String userLastSurname) {
        this.userLastSurname = userLastSurname;
    }

    public Integer getPersonId() {
        return personId;
    }

    public void setPersonId(Integer personId) {
        this.personId = personId;
    }

    public String getCreditCode() {
        return creditCode;
    }

    public void setCreditCode(String creditCode) {
        this.creditCode = creditCode;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
