package com.affirm.sendgrid.model;

import com.affirm.common.model.catalog.SendGridList;
import com.affirm.common.service.CatalogService;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by dev5 on 13/10/17.
 */
@Repository
public class ContactList {

    @SerializedName("sendgrid_id")
    private Integer sendGridId;
    @Expose
    @SerializedName(value = "name", alternate={"contact_list"})
    private String name;
    @SerializedName("contact_list_id")
    private Integer id;
    @SerializedName("js_content")
    private List<Contact> contacts;

    public ContactList(){};

    public ContactList(SendGridList sendGridList){
        setName(sendGridList.getName());
    }

    public void fillFromDB(Integer sendGridId, CatalogService catalogService){
        setSendGridId(sendGridId);
        SendGridList sendGridList = catalogService.getSendGridLists().stream().filter(e->e.getSendgridId() != null && e.getSendgridId().equals(getSendGridId())).findFirst().orElse(null);
        if(sendGridList != null){
            setName(sendGridList.getName());
        }
    }

    public Integer getSendGridId() {
        return sendGridId;
    }

    public void setSendGridId(Integer sendGridId) {
        this.sendGridId = sendGridId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }
}
