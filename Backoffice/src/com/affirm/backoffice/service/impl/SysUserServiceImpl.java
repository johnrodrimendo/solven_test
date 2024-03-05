/**
 *
 */
package com.affirm.backoffice.service.impl;


import com.affirm.backoffice.model.ResetPasswordBackoffice;
import com.affirm.backoffice.service.SysUserService;
import com.affirm.common.dao.SysUserDAO;
import com.affirm.common.model.catalog.InteractionContent;
import com.affirm.common.model.catalog.InteractionType;
import com.affirm.common.model.security.OldPasswordBackoffice;
import com.affirm.common.model.transactional.PersonInteraction;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.InteractionService;
import com.affirm.common.util.CryptoUtil;
import com.affirm.common.util.JsonUtil;
import com.affirm.security.model.SysUser;
import com.affirm.system.configuration.Configuration;
import com.google.gson.Gson;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author jrodriguez
 */
@Service("sysUserService")
public class SysUserServiceImpl implements SysUserService {

    private static final Logger logger = Logger.getLogger(SysUserServiceImpl.class);

    @Autowired
    SysUserDAO sysUserDAO;

    @Autowired
    CatalogService catalogService;

    @Autowired
    InteractionService interactionService;

    @Override
    public SysUser getSessionSysuser() throws Exception {
        if (SecurityUtils.getSubject().isAuthenticated()) {
            return (SysUser) SecurityUtils.getSubject().getPrincipal();
        }
        return null;
    }

    @Override
    public String getCommaSeparatedRoleNames(Integer userId) throws Exception {
        JSONArray rolesArr = sysUserDAO.getActiveRoleNames(userId);
        List<String> rolesList = JsonUtil.getListFromJsonArray(rolesArr, (js, i) -> JsonUtil.getStringFromJson(js.getJSONObject(i), "role", null));
        String roles = String.join(", ", rolesList);
        return roles;
    }

    @Override
    public String onLogout(int boLoginId, Date from) {
        return sysUserDAO.registerSignOut(boLoginId, from);
    }

    @Override
    public String onLogin(Integer boLoginId, Date date) {
        return sysUserDAO.registerSignIn(boLoginId, date);
    }

    @Override
    public SysUser getSysUserById(Integer sysUserId) {
        return sysUserDAO.getSysUserById(sysUserId);
    }

    @Override
    public List<SysUser> getUsers() throws Exception {
        List<SysUser> users = null;
        users = sysUserDAO.getSysUsers();
        if (users == null) {
            users = new ArrayList<>();
        }
        return users;
    }

    @Override
    public SysUser getSysUserByEmail(String email) throws Exception {
        return sysUserDAO.getSysUserByEmail(email);
    }

    @Override
    public Boolean updateResetPassword(String token, String email, String password) throws Exception {
        return sysUserDAO.updateResetPassword(token, email, password);
    }

    @Override
    public void activateSysUserById(Integer sysUserId) throws Exception {
        sysUserDAO.setActiveSysUser(sysUserId, true);
    }

    @Override
    public String generateResetPassword(String email) throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        ResetPasswordBackoffice resetPassword = new ResetPasswordBackoffice(email, calendar.getTime().toString());
        Gson gson = new Gson();
        String token = CryptoUtil.encrypt(gson.toJson(resetPassword));
        sysUserDAO.registerResetToken(email, token);
        return token;
    }

    @Override
    public String generateResetLink(String email) throws Exception {
        return Configuration.getBackofficeDomain().concat("/resetPassword/").concat(generateResetPassword(email));
    }

    @Override
    public Boolean validPassword(Integer sysUserId, String newPassword) throws Exception {
        List<OldPasswordBackoffice> passwords = sysUserDAO.getSysUserPasswords(sysUserId, Configuration.getDefaultLocale());

        if (passwords == null || passwords.isEmpty())
            return true;

        for (OldPasswordBackoffice password : passwords) {
            if (CryptoUtil.validatePassword(newPassword, password.getPassword())) return false;
        }
        return true;
    }

    @Override
    public Boolean validLoginDate(Integer scheduleId, String countryCode) {
        return sysUserDAO.getValidWorkingHours(scheduleId, countryCode);
    }

    @Override
    public Boolean isActive(Integer sysUserId) {
        return sysUserDAO.sysUserIsActive(sysUserId);
    }

    @Override
    public String generatePasswordResetEmail(String user) throws Exception {

        String email = null;
        SysUser sysUser = sysUserDAO.getSysUserByUsername(user);
        if (sysUser != null) {
            email = sysUser.getEmail();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String link = generateResetLink(sysUser.getEmail());
                        JSONObject jsonVars = new JSONObject();
                        jsonVars.put("USER_NAME", user);
                        jsonVars.put("RESET_LINK", link);
                        jsonVars.put("AGENT_IMAGE_URL", Configuration.AGENT_IMAGE_URL_COLLECTION);
                        jsonVars.put("AGENT_FULLNAME", Configuration.AGENT_FULLNAME_COLLECTION);

                        PersonInteraction interaction = new PersonInteraction();
                        interaction.setInteractionType(catalogService.getInteractionType(InteractionType.MAIL));
                        interaction.setInteractionContent(catalogService.getInteractionContent(InteractionContent.RESET_PASSWORD, sysUser.getActiveCountries().get(0)));
                        interaction.setDestination(sysUser.getEmail());
                        interactionService.sendPersonInteraction(interaction, jsonVars, null);
                    } catch (Exception ex) {
                        logger.error("Error sending email", ex);
                    }
                }
            }).start();
        }
        return email;
    }

    @Override
    public void disableUser(String username) throws Exception {
        int id = sysUserDAO.getSysUserByUsername(username).getId();
        sysUserDAO.setActiveSysUser(id, false);
    }
}