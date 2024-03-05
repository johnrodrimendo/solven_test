package com.affirm.backoffice.service;

import com.affirm.common.model.security.OldPassword;
import com.affirm.security.model.SysUser;

import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by jrodriguez on 23/08/16.
 */
public interface SysUserService {

    SysUser getSessionSysuser() throws Exception;

    String getCommaSeparatedRoleNames(Integer userId) throws Exception;

    String onLogout(int boLoginId, Date from);

    String onLogin(Integer id, Date date);

    SysUser getSysUserById(Integer sysUserId);

    List<SysUser> getUsers()throws Exception;

    SysUser getSysUserByEmail(String email) throws Exception;

    Boolean updateResetPassword(String token, String email, String password) throws Exception;

    void activateSysUserById(Integer sysUserId) throws Exception;

    String generateResetPassword(String email) throws Exception;

    String generateResetLink(String email) throws Exception;

    Boolean validPassword(Integer userEntityId, String newPassword) throws Exception;

    Boolean validLoginDate(Integer scheduleId, String countryCode);

    Boolean isActive(Integer sysUserId);

    String generatePasswordResetEmail(String username)throws Exception;

    void disableUser(String username)throws Exception;
}
