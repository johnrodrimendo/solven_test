package com.affirm.common.dao.impl;

import com.affirm.common.dao.JsonResolverDAO;
import com.affirm.common.dao.SendgridDAO;
import com.affirm.common.service.CatalogService;
import com.affirm.sendgrid.model.Contact;
import com.affirm.sendgrid.model.ContactList;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dev5 on 13/10/17.
 */
@Repository("sendgridDAO")
public class SendgridDAOImpl extends JsonResolverDAO implements SendgridDAO{


    @Autowired
    private CatalogService catalogService;

    @Override
    public List<Contact> getContactList() {
        JSONArray dbArray =  queryForObjectInteraction("select * from interaction.get_contact_list()", JSONArray.class);
        if (dbArray == null)
            return null;

        List<Contact> contacts= new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            Contact contact = new Contact();
            contact.fillFromDB(dbArray.getJSONObject(i), catalogService);
            contacts.add(contact);
        }
        return contacts;
    }

    @Override
    public void updateSendGridList(Integer id, Integer sendGridId) throws Exception {
        update("UPDATE interaction.ct_contact_list set sendgrid_id = ? WHERE contact_list_id = ?",
                INTERACTION_DB,
                new SqlParameterValue(Types.INTEGER, sendGridId),
                new SqlParameterValue(Types.INTEGER, id));
    }

    @Override
    public void updateSendGridContactId(Integer id, String sendGridId) throws Exception {
        updateTrx("UPDATE users.tb_user SET sendgrid_id = ? WHERE user_id = ?",
                new SqlParameterValue(Types.VARCHAR, sendGridId),
                new SqlParameterValue(Types.INTEGER, id));
    }

    @Override
    public List<ContactList> getContactListList() {
        Gson gson = new Gson();
        JSONArray dbArray =  queryForObjectInteraction("select * from interaction.get_contact_list_by_list()", JSONArray.class);
        if (dbArray == null)
            return null;

        List<ContactList> contactListList= new ArrayList<>();
        contactListList = gson.fromJson(dbArray.toString(), new TypeToken<List<ContactList>>() {}.getType());
        return contactListList;
    }



}
