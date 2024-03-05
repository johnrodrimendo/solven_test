package com.affirm.sendgrid.model;

import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dev5 on 13/10/17.
 */
@Repository
public class Contact {

    @Expose
    @SerializedName("email")
    private String email;
    @SerializedName("user_id")
    private Integer userId;
    @SerializedName("must_update")
    private Boolean update;
    @Expose
    @SerializedName(value = "first_name", alternate={"person_name"})
    private String personName;
    @Expose
    @SerializedName(value = "last_name", alternate={"first_surname"})
    private String firstSurname;
    @SerializedName("last_surname")
    private String personLastSurname;
    @Expose
    @SerializedName(value = "cellphone", alternate={"phone_number"})
    private String phoneNumber;
    @SerializedName("sendgrid_id")
    private String sendgridId;
    @SerializedName("status")
    private String status;
    @SerializedName("error_message")
    private String errorMessage;
    private List<ContactList> contactListList;

    public void fillFromDB(JSONObject json, CatalogService catalogService){
        JSONObject jsonContact = json.getJSONObject("js_contact");
        setEmail(JsonUtil.getStringFromJson(jsonContact, "email", null));
        setUserId(JsonUtil.getIntFromJson(jsonContact, "user_id", null));
        setUpdate(JsonUtil.getBooleanFromJson(jsonContact, "must_update", false));
        setPersonName(JsonUtil.getStringFromJson(jsonContact, "person_name", null));
        setFirstSurname(JsonUtil.getStringFromJson(jsonContact, "last_surname", null));
        setPersonLastSurname(JsonUtil.getStringFromJson(jsonContact, "first_surname", null));
        JSONArray jsonList = json.getJSONArray("js_sendgrid_id");
        List<ContactList> contactLists = new ArrayList<>();
        for(int i=0;i<jsonList.length();i++){
            ContactList contactList = new ContactList();
            contactList.fillFromDB(jsonList.getInt(i), catalogService);
            contactLists.add(contactList);
        }
        this.contactListList = contactLists;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getFirstSurname() {
        return firstSurname;
    }

    public void setFirstSurname(String firstSurname) {
        this.firstSurname = firstSurname;
    }

    public String getPersonLastSurname() {
        return personLastSurname;
    }

    public void setPersonLastSurname(String personLastSurname) {
        this.personLastSurname = personLastSurname;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getSendgridId() {
        return sendgridId;
    }

    public void setSendgridId(String sendgridId) {
        this.sendgridId = sendgridId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public List<ContactList> getContactListList() {
        return contactListList;
    }

    public void setContactListList(List<ContactList> contactListList) {
        this.contactListList = contactListList;
    }

    public boolean isUpdate() {
        return update;
    }

    public void setUpdate(boolean update) {
        this.update = update;
    }
}
