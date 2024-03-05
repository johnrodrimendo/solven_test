package com.affirm.common.model.transactional;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by stbn on 14/12/16.
 */
public class MigracionesResult implements Serializable {

    private Integer queryId;
    private String inDocNumber;
    private Date inBirthday;
    private String fullName;
    private String nationality;
    private String residence;
    private String tae;
    private Date documentExpeditionDate;
    private Date documentDueDate;

    public void fillFromDb(JSONObject json) {
        setQueryId(JsonUtil.getIntFromJson(json, "query_id", null));
        setInDocNumber(JsonUtil.getStringFromJson(json, "in_document_number", null));
        setInBirthday(JsonUtil.getPostgresDateFromJson(json, "in_birthday", null));
        setFullName(JsonUtil.getStringFromJson(json, "fullname", null));
        setNationality(JsonUtil.getStringFromJson(json, "nationality", null));
        setResidence(JsonUtil.getStringFromJson(json, "residence", null));
        setTae(JsonUtil.getStringFromJson(json, "tae", null));
        setDocumentExpeditionDate(JsonUtil.getPostgresDateFromJson(json, "doc_expedition_date", null));
        setDocumentDueDate(JsonUtil.getPostgresDateFromJson(json, "doc_due_date", null));
    }

    public Integer getQueryId() {
        return queryId;
    }

    public void setQueryId(Integer queryId) {
        this.queryId = queryId;
    }

    public String getInDocNumber() {
        return inDocNumber;
    }

    public void setInDocNumber(String inDocNumber) {
        this.inDocNumber = inDocNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getResidence() {
        return residence;
    }

    public void setResidence(String residence) {
        this.residence = residence;
    }

    public String getTae() {
        return tae;
    }

    public void setTae(String tae) {
        this.tae = tae;
    }

    public Date getInBirthday() {
        return inBirthday;
    }

    public void setInBirthday(Date inBirthday) {
        this.inBirthday = inBirthday;
    }

    public Date getDocumentExpeditionDate() {
        return documentExpeditionDate;
    }

    public void setDocumentExpeditionDate(Date documentExpeditionDate) {
        this.documentExpeditionDate = documentExpeditionDate;
    }

    public Date getDocumentDueDate() {
        return documentDueDate;
    }

    public void setDocumentDueDate(Date documentDueDate) {
        this.documentDueDate = documentDueDate;
    }

    @Override
    public String toString() {
        return "MigracionesResult{" +
                "queryId=" + queryId +
                ", inDocNumber='" + inDocNumber + '\'' +
                ", inBirthday=" + inBirthday +
                ", fullName='" + fullName + '\'' +
                ", nationality='" + nationality + '\'' +
                ", residence='" + residence + '\'' +
                ", tae='" + tae + '\'' +
                ", documentExpeditionDate=" + documentExpeditionDate +
                ", documentDueDate=" + documentDueDate +
                '}';
    }
}
