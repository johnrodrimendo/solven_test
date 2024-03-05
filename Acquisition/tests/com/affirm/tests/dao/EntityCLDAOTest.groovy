package com.affirm.tests.dao

import com.affirm.client.dao.EntityCLDAO
import com.affirm.client.model.LoggedUserEntity
import com.affirm.tests.BaseAcquisitionConfig
import org.json.JSONArray
import org.junit.Assert
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class EntityCLDAOTest extends BaseAcquisitionConfig {

    @Autowired
    EntityCLDAO entityCLDAO

    static final String EMAIL = "edavalos@solven.pe"
    static final String IP = "127.0.0.1"
    static final String METADATA = ""
    static final CURRENT_DATE = new Date()
    static final SIGIN_DATE = CURRENT_DATE
    static final Locale LOCALE = Locale.US
    static final int EXTRANET_SESSION_ID = 78968
    static final Date SIGNOUT_DATE = CURRENT_DATE
    static final Integer ENTITY_ID = 11111
    static final boolean ACTIVE = true
    static final int ENTITY_USER_ID = 77777
    static final JSONArray TFA_SCRATCH_CODE = new JSONArray()
    static final String TFA_SHARED_SECRET = ""
    static final String NAME = "Omar"
    static final String FIRST_SURNAME = "Ccoa"

    @Test
    void getHashedPasswordFromEntityCLDAO() {
        String result = entityCLDAO.getHashedPassword(EMAIL)
        Assert.assertNotNull(result)
    }

    @Test
    void registerSessionEntityFromEntityCLDAO() {
        LoggedUserEntity result = entityCLDAO.registerSessionEntity(EMAIL, IP, METADATA, SIGIN_DATE, LOCALE)
        Assert.assertNotNull(result)
    }

    @Test
    void registerSessionLogoutFromEntityCLDAO() {
        entityCLDAO.registerSessionLogout(EXTRANET_SESSION_ID, SIGNOUT_DATE)
    }

    @Test
    void getCreditFullInfoFromEntityCLDAO() {
        JSONArray result = entityCLDAO.getCreditFullInfo(ENTITY_ID)
        Assert.assertNull(result)
    }

    @Test
    void activateTfaLoginFromEntityCLDAO() {
        entityCLDAO.activateTfaLogin(ENTITY_ID, ACTIVE)
    }

    @Test
    void updateSharedSecretFromEntityCLDAO() {
        entityCLDAO.updateSharedSecret(ENTITY_USER_ID, TFA_SCRATCH_CODE, TFA_SHARED_SECRET)
    }

    @Test
    void registerEntityUserFromEntityCLDAO() {
        String result = entityCLDAO.registerEntityUser(ENTITY_ID, NAME, FIRST_SURNAME, EMAIL)
        Assert.assertNotNull(result)
    }

    @Test
    void getRolesByEntityUserFromEntityCLDAO() {
        List<Integer> result = entityCLDAO.getRolesByEntityUser(ENTITY_USER_ID)
        Assert.assertNull(result)
    }

}
