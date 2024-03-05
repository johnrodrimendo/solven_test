package com.affirm.tests.dao

import com.affirm.client.dao.UserCLDAO
import com.affirm.client.model.LoggedUserClient
import com.affirm.tests.BaseAcquisitionConfig
import org.junit.Assert
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class UserCLDAOTest extends BaseAcquisitionConfig {

    @Autowired
    UserCLDAO userCLDAO

    static final int USER_ID = 5002
    static final String IP = "127.0.0.1"
    static final String BROWSER_METADATA = ""
    static final Date SIGNIN_DATE = new Date()
    static final Integer SYS_USER_ID = 3

    @Test
    void registerSessionLoginFromUserCLDAO() {
        LoggedUserClient result = userCLDAO.registerSessionLogin(USER_ID, IP, BROWSER_METADATA, SIGNIN_DATE, SYS_USER_ID)
        Assert.assertNotNull(result)
    }
}
