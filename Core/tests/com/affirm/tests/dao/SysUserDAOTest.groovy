package com.affirm.tests.dao

import com.affirm.common.dao.SysUserDAO
import com.affirm.common.model.security.OldPasswordBackoffice
import com.affirm.security.model.SysUser
import com.affirm.tests.BaseConfig
import org.json.JSONArray
import org.junit.Assert
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class SysUserDAOTest extends BaseConfig {

    @Autowired
    SysUserDAO sysUserDAO

    static final int USER_ID = 1633
    static final String HASH_SECRET = "ASDF"
    static final JSONArray SCRATCHES = new JSONArray()
    static final int SYS_USER_ID = 14
    static final String FINAL_FILENAME = "filename"
    static final int BO_LOGIN_ID = 14
    static final Date CURRENT_DATE = new Date()
    static final Date FROM = CURRENT_DATE
    static final String EMAIL = "occoa@solven.pe"
    static final String USERNAME = "occoa"
    static final Locale LOCALE = Locale.US
    static final String TOKEN = "Lddm7cBIAnUEeZKrE2wJS6BEL1wkKPK*wRKw3WekzVp5uEzyS7HWNs0rT*Ln9Zr7GL83UYH6ktwwRpmQiSharPQX,RmBa3iv29LG8QyIckY="
    static final String PASSWORD = "123"
    static final boolean IS_ACTIVE = true
    static final Integer SCHEDULE_ID = 111
    static final String COUNTRY_CODE = "51"

    @Test
    void getDisbursementSignersFromSysUserDAO() {
        List<SysUser> result = sysUserDAO.getDisbursementSigners()
        Assert.assertNotNull(result)
    }

    @Test
    void getActiveRoleNamesFromSysUserDAO() {
        JSONArray result = sysUserDAO.getActiveRoleNames(USER_ID)
        Assert.assertNotNull(result)
    }

    @Test
    void updateSharedSecretFromSysUserDAO() {
        sysUserDAO.updateSharedSecret(USER_ID, HASH_SECRET, SCRATCHES)
    }

    @Test
    void updateAvatarFromSysUserDAO() {
        sysUserDAO.updateAvatar(SYS_USER_ID, FINAL_FILENAME)
    }

    @Test
    void registerSignOutFromSysUserDAO() {
        sysUserDAO.registerSignOut(BO_LOGIN_ID, FROM)
    }

    @Test
    void registerSignInFromSysUserDAO() {
        String result = sysUserDAO.registerSignIn(BO_LOGIN_ID, CURRENT_DATE)
        Assert.assertNotNull(result)
    }

    @Test
    void getSysUserByIdFromSysUserDAO() {
        SysUser result = sysUserDAO.getSysUserById(SYS_USER_ID)
        Assert.assertNotNull(result)
    }

    @Test
    void getSysUsersFromSysUserDAO() {
        List<SysUser> result = sysUserDAO.getSysUsers()
        Assert.assertNotNull(result)
    }

    @Test
    void getSysUserByEmailFromSysUserDAO() {
        SysUser result = sysUserDAO.getSysUserByEmail(EMAIL)
        Assert.assertNotNull(result)
    }

    @Test
    void getSysUserByUsernameFromSysUserDAO() {
        SysUser result = sysUserDAO.getSysUserByUsername(USERNAME)
        Assert.assertNotNull(result)
    }

    @Test
    void getSysUserPasswordsFromSysUserDAO() {
        List<OldPasswordBackoffice> result = sysUserDAO.getSysUserPasswords(SYS_USER_ID, LOCALE)
        Assert.assertNotNull(result)
    }

    @Test
    void updateResetPasswordFromSysUserDAO() {
        sysUserDAO.updateResetPassword(TOKEN, EMAIL, PASSWORD)
    }

    @Test
    void setActiveSysUserFromSysUserDAO() {
        List<SysUser> result = sysUserDAO.setActiveSysUser(SYS_USER_ID, IS_ACTIVE)
        Assert.assertNull(result)
    }

    @Test
    void registerResetTokenFromSysUserDAO() {
        sysUserDAO.registerResetToken(EMAIL, TOKEN)
    }

    @Test
    void getValidWorkingHoursFromSysUserDAO() {
        Boolean result = sysUserDAO.getValidWorkingHours(SCHEDULE_ID, COUNTRY_CODE)
        Assert.assertNotNull(result)
    }

    @Test
    void sysUserIsActiveFromSysUserDAO() {
        Boolean result = sysUserDAO.sysUserIsActive(SYS_USER_ID)
        Assert.assertNotNull(result)
    }

    @Test
    void isResetPasswordTokenUsedFromSysUserDAO() {
        Boolean result = sysUserDAO.isResetPasswordTokenUsed(TOKEN)
        Assert.assertNotNull(result)
    }

    @Test
    void getPasswordByUsernameFromSysUserDAO() {
        String result = sysUserDAO.getPasswordByUsername(USERNAME)
        Assert.assertNotNull(result)
    }

    @Test
    void getIdByUsernameFromSysUserDAO() {
        Integer result = sysUserDAO.getIdByUsername(USERNAME)
        Assert.assertNotNull(result)
    }
}
