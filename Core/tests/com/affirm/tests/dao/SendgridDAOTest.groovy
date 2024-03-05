package com.affirm.tests.dao

import com.affirm.common.dao.SendgridDAO
import com.affirm.sendgrid.model.Contact
import com.affirm.sendgrid.model.ContactList
import com.affirm.tests.BaseConfig
import org.junit.Assert
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class SendgridDAOTest extends BaseConfig {

    @Autowired
    SendgridDAO sendgridDAO

    static final Integer CONTACT_LIST_ID = 1
    static final String SENDGRID_ID = "2"
    static final Integer SENDGRID_ID_INTEGER = 2
    static final Integer USER_ID = 3

    @Test
    void getContactListFromSendgridDAO() {
        List<Contact> result = sendgridDAO.getContactList()
        Assert.assertNotNull(result)
    }

    @Test
    void updateSendGridListFromSendgridDAO() {
        sendgridDAO.updateSendGridList(CONTACT_LIST_ID, SENDGRID_ID_INTEGER)
    }

    @Test
    void updateSendGridContactIdFromSendgridDAO() {
        sendgridDAO.updateSendGridContactId(USER_ID, SENDGRID_ID)
    }

    @Test
    void getContactListListFromSendgridDAO() {
        List<ContactList> result = sendgridDAO.getContactListList()
        Assert.assertNotNull(result)
    }


}
