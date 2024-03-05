package com.affirm.common.dao;

import com.affirm.sendgrid.model.Contact;
import com.affirm.sendgrid.model.ContactList;

import java.util.List;

/**
 * Created by dev5 on 13/10/17.
 */
public interface SendgridDAO {

    List<Contact> getContactList();

    void updateSendGridList(Integer id, Integer sendGridId) throws Exception;

    void updateSendGridContactId(Integer id, String sendGridId) throws Exception;

    List<ContactList> getContactListList();

}
