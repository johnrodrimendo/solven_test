package com.affirm.sendgrid.service.impl;

import com.affirm.common.dao.SendgridDAO;
import com.affirm.common.model.catalog.SendGridList;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import com.affirm.sendgrid.SendgridUtilCall;
import com.affirm.sendgrid.model.Contact;
import com.affirm.sendgrid.model.ContactList;
import com.affirm.sendgrid.service.SendgridService;
import com.affirm.system.configuration.Configuration;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by dev5 on 14/10/17.
 */
@Service("sendGridService")
public class SendgridServiceImpl implements SendgridService {

    @Autowired
    private SendgridDAO sendgridDAO;
    @Autowired
    private net.sf.ehcache.CacheManager ehCacheManager;
    @Autowired
    private CatalogService catalogService;

    public void processContactList() throws Exception {
        ehCacheManager.clearAll();

        String deleteListURL = Configuration.DELETE_LIST;
        String createList = Configuration.CREATE_LIST;
        String createContacts = Configuration.CREATE_CONTACTS;
        String addContactToList = Configuration.ADD_CONTACT;
        String apiKey = Configuration.getsendgridApikey();

        Gson gsonToJson;
        gsonToJson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().disableHtmlEscaping().create();

        /* ACTUALIZAR LISTA - BORRAR Y CREAR*/
        List<SendGridList> sendGridLists = catalogService.getSendGridLists()/*.stream().filter(e->e.getSendgridId() == null || !e.getActive()).collect(Collectors.toList())*/;
        for(int i=0; i < sendGridLists.size(); i++){
            if(!sendGridLists.get(i).getActive() && sendGridLists.get(i).getSendgridId() != null){
                SendgridUtilCall.callDELETE(deleteListURL.concat(String.valueOf(sendGridLists.get(i).getSendgridId())), apiKey);
                sendGridLists.get(i).setSendgridId(null);
                sendgridDAO.updateSendGridList(sendGridLists.get(i).getId(), null);
            }else if(sendGridLists.get(i).getActive() && sendGridLists.get(i).getSendgridId() == null){
                ContactList contactList = new ContactList(sendGridLists.get(i));
                JSONObject jsonResponse = SendgridUtilCall.callPOST(gsonToJson.toJson(contactList), createList, apiKey);
                if(JsonUtil.getJsonArrayFromJson(jsonResponse, "errors", null) == null){
                    Integer listCreated = JsonUtil.getIntFromJson(jsonResponse, "id", null);
                    sendgridDAO.updateSendGridList(sendGridLists.get(i).getId(), listCreated);
                    System.out.println("Lista " + contactList.getName() + " con ID : " +  String.valueOf(listCreated) + " creada con éxito");
                }else{
                    JSONArray errorArray = JsonUtil.getJsonArrayFromJson(jsonResponse, "errors", null);
                    for(int x = 0; x < errorArray.length();x++){
                        System.out.println(JsonUtil.getStringFromJson(errorArray.getJSONObject(x), "message", null));
                    }
                }
            }
        }

        ehCacheManager.clearAll();

        /* ACTUALIZAR CONTACTOS - CREAR O ACTUALIZAR*/
        List<Contact> contacts = sendgridDAO.getContactList().stream().filter(e->e.isUpdate()).collect(Collectors.toList());
        List<Contact> errorContactList;
        errorContactList = insertContact(contacts, createContacts, apiKey);
        errorContactList = insertContact(errorContactList, createContacts, apiKey);
        if(errorContactList.size() > 0){
            System.out.println("[Sendgrid ERROR] : Se han encontrado errores en la insercion de contactos");
            for(int i=0;i<errorContactList.size();i++){
                System.out.println(errorContactList.get(i).getSendgridId());
            }
        }

        ehCacheManager.clearAll();

        /*INSERTAR CONTACTOS EN LISTAS*/
        List<ContactList> contactListList = sendgridDAO.getContactListList();
        List<ContactList> errorContactListList;
        errorContactListList = insertContactOnList(contactListList, addContactToList, apiKey);
        errorContactListList = insertContactOnList(errorContactListList, addContactToList, apiKey);
        if(errorContactListList.size() > 0){
            System.out.println("[Sendgrid ERROR] : Se han encontrado errores en la insercion de contactos a listas");
            for(int i=0;i<errorContactListList.size();i++){
                System.out.println(errorContactListList.get(i).getSendGridId());
            }
        }

    }

    private List<Contact> insertContact(List<Contact> contacts, String createContacts, String apiKey) throws Exception {
        Gson gsonToJson;
        gsonToJson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().disableHtmlEscaping().create();
        List<Contact> errorContactList = new ArrayList<>();
        for(int i=0;i<contacts.size();i++){
            Thread.sleep(500);
            List<Contact> contactsTemp = new ArrayList<>();
            contactsTemp.add(contacts.get(i));
            JSONObject jsonResponse = SendgridUtilCall.callPOST(gsonToJson.toJson(contactsTemp), createContacts, apiKey);
            if(jsonResponse.has("new_count") && jsonResponse.getInt("error_count") == 0) {
                if(jsonResponse.getInt("new_count") != 0  || jsonResponse.getInt("updated_count") != 0){
                    contacts.get(i).setSendgridId(jsonResponse.getJSONArray("persisted_recipients").getString(0));
                    sendgridDAO.updateSendGridContactId(contacts.get(i).getUserId(), contacts.get(i).getSendgridId());
                    System.out.println(jsonResponse.getJSONArray("persisted_recipients").getString(0));
                }else if(jsonResponse.getJSONArray("unmodified_indices").length() == 1){
                    System.out.println("Contacto actualizado");
                }
            }else{
                errorContactList.add(contacts.get(i));
            }
        }

        return errorContactList;
    }

    private List<ContactList> insertContactOnList(List<ContactList> contactList, String addContactToList, String apiKey) throws Exception {
        Gson gsonToJson;
        gsonToJson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().disableHtmlEscaping().create();
        List<ContactList> errorContactListList = new ArrayList<>();
        for(int i=0;i<contactList.size();i++){
            Thread.sleep(500);
            List<String> contactsIds = new ArrayList<>();
            for(int j=0;j<contactList.get(i).getContacts().size();j++){
                contactsIds.add(contactList.get(i).getContacts().get(j).getSendgridId());
            }
            JSONObject jsonResponse = SendgridUtilCall.callPOST(gsonToJson.toJson(contactsIds), addContactToList.replace("{list_id}", String.valueOf(contactList.get(i).getSendGridId())), apiKey);
            if(jsonResponse != null){
                jsonResponse.getJSONArray("errors").get(0);
                errorContactListList.add(contactList.get(i));
            }
        }
        return errorContactListList;
    }

}
