/**
 *
 */
package com.affirm.common.dao;

import com.affirm.common.model.security.OldPasswordBackoffice;
import com.affirm.security.model.SysUser;
import org.json.JSONArray;

import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @author jrodriguez
 */
public interface SysUserDAO {

    /**
     * Returns the Disbursement Signers
     *
     * @return
     * @throws Exception
     */
    List<SysUser> getDisbursementSigners() throws Exception;

    JSONArray getActiveRoleNames(Integer userId) throws Exception;

    void updateSharedSecret(Integer id, String hashSecret, JSONArray scratchs) throws Exception;

    void updateAvatar(int sysuserId, String finalFilename) throws Exception;

    String registerSignOut(int boLoginId, Date from);

    String registerSignIn(Integer boLoginId, Date date);

    SysUser getSysUserById(Integer sysUserId);

    List<SysUser> getSysUsers() throws Exception;

    SysUser getSysUserByEmail(String email) throws Exception;

    SysUser getSysUserByUsername(String username);

    List<OldPasswordBackoffice> getSysUserPasswords(Integer sysUserId, Locale locale) throws Exception;

    Boolean updateResetPassword(String token, String email, String password) throws Exception;

    void setActiveSysUser(Integer sysUserId,boolean value) throws Exception;

    void registerResetToken(String email, String token) throws Exception;

    Boolean getValidWorkingHours(Integer scheduleId, String countryCode);

    Boolean sysUserIsActive(Integer sysUserId);

    Boolean isResetPasswordTokenUsed(String token) throws Exception;

    String getPasswordByUsername(String username);

    Integer getIdByUsername(String username);
}
