package com.affirm.common.dao.impl;

import com.affirm.common.dao.JsonResolverDAO;
import com.affirm.common.dao.SysUserDAO;
import com.affirm.common.model.security.OldPasswordBackoffice;
import com.affirm.common.service.CatalogService;
import com.affirm.security.model.SysUser;
import com.affirm.system.configuration.Configuration;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by jrodriguez on 08/06/16.
 */

@Repository("sysUserDao")
public class SysUserDAOImpl extends JsonResolverDAO implements SysUserDAO {


    @Autowired
    private CatalogService catalogService;

    @Override
    public List<SysUser> getDisbursementSigners() throws Exception {
        String query = "select * from security.get_credit_signers()";
        JSONArray dbArray = queryForObjectTrx(query, JSONArray.class);
        if (dbArray == null) {
            return null;
        }

        List<SysUser> users = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            SysUser user = new SysUser();
            user.fillFromDb(catalogService, dbArray.getJSONObject(i));
            users.add(user);
        }
        return users;
    }

    @Override
    public JSONArray getActiveRoleNames(Integer userId) throws Exception {
        JSONArray jsonArray = queryForObjectTrx("select * from security.get_roles(?)", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, userId));
        if (jsonArray == null)
            jsonArray = new JSONArray("[]");
        return jsonArray;
    }

    @Override
    public void updateSharedSecret(Integer userId, String hashSecret, JSONArray scratchs) throws Exception {
        updateTrx("UPDATE security.tb_sysuser set tfa_shared_secret = ?, tfa_scratch_codes = ? where sysuser_id = ?",
                new SqlParameterValue(Types.VARCHAR, hashSecret),
                new SqlParameterValue(Types.OTHER, scratchs.toString()),
                new SqlParameterValue(Types.INTEGER, userId)
        );
    }

    @Override
    public void updateAvatar(int sysuserId, String finalFilename) throws Exception {
        updateTrx("UPDATE security.tb_sysuser SET avatar = ? WHERE sysuser_id = ?",
                new SqlParameterValue(Types.VARCHAR, finalFilename),
                new SqlParameterValue(Types.INTEGER, sysuserId)
        );
    }

    @Override
    public String registerSignOut(int boLoginId, Date from) {
        return queryForObjectTrx("select * from security.register_sysuser_sign_out(?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, boLoginId),
                new SqlParameterValue(Types.TIMESTAMP, from)
        );
    }

    @Override
    public String registerSignIn(Integer boLoginId, Date date) {
        return queryForObjectTrx("select * from security.register_sysuser_sign_in(?, ?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, boLoginId),
                new SqlParameterValue(Types.TIMESTAMP, date),
                new SqlParameterValue(Types.INTEGER, Configuration.hostEnvIsProduction() ? Configuration.MAX_ACTIVE_SESSIONS : 99999)
        );
    }

    @Override
    public SysUser getSysUserById(Integer sysUserId) {
        JSONObject dbJson = queryForObjectTrx("select * from security.get_sysuser(?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, sysUserId));
        if (dbJson == null)
            return null;

        SysUser sysUser = new SysUser();
        sysUser.fillFromDb(catalogService, dbJson);
        return sysUser;
    }



    @Override
    public List<SysUser> getSysUsers() {
        //JSONArray jsonArray = queryForObjectTrx("select * from security.tb_sysuser",JSONArray.class);
        JSONArray jsonArray = queryForObjectTrx("select * from security.get_sysusers()",JSONArray.class);
        List<SysUser> users = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            SysUser user = new SysUser();
            user.fillFromDb(catalogService, jsonArray.getJSONObject(i));
            users.add(user);
        }
        return users;
    }

    @Override
    public SysUser getSysUserByEmail(String email) throws Exception{
        JSONObject dbjson = queryForObjectTrx("select * from security.get_sysuser_by_email(?)", JSONObject.class,
                new SqlParameterValue(Types.VARCHAR, email));
        if (dbjson == null) {
            return null;
        }
        SysUser user = new SysUser();
        user.fillFromDb(catalogService, dbjson);
        return user;
    }

    @Override
    public SysUser getSysUserByUsername(String username) {
        JSONObject dbjson = queryForObjectTrx("select * from security.get_sysuser_by_username(?)", JSONObject.class,
                new SqlParameterValue(Types.VARCHAR, username));
        if (dbjson == null) {
            return null;
        }
        SysUser user = new SysUser();
        user.fillFromDb(catalogService, dbjson);
        return user;
    }

    @Override
    public List<OldPasswordBackoffice> getSysUserPasswords(Integer sysUserId, Locale locale) throws Exception{
        JSONArray dbArray = queryForObjectTrx("select * from security.get_sysuser_passwords(?,?)", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, sysUserId),
                new SqlParameterValue(Types.INTEGER, Configuration.OLD_PASSWORDS_BACKOFFICE));
        if (dbArray == null) {
            return null;
        }

        List<OldPasswordBackoffice> passwords = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            OldPasswordBackoffice password = new OldPasswordBackoffice();
            password.fillFromDb(dbArray.getJSONObject(i));
            passwords.add(password);
        }

        return passwords;
    }

    @Override
    public Boolean updateResetPassword(String token, String email, String password) throws Exception {
        return queryForObjectTrx("select * from security.update_sysuser_password_by_token(?, ?, ?, ?)", Boolean.class,
                new SqlParameterValue(Types.VARCHAR, token),
                new SqlParameterValue(Types.VARCHAR, email),
                new SqlParameterValue(Types.VARCHAR, password),
                new SqlParameterValue(Types.INTEGER, Configuration.PASSWORD_EXPIRATION_PERIOD)
                );
    }

    @Override
    public void setActiveSysUser(Integer sysUserId,boolean value) throws Exception{
        updateTrx("UPDATE security.tb_sysuser SET is_active = ? WHERE sysuser_id = ?",
                new SqlParameterValue(Types.BOOLEAN, value),
                new SqlParameterValue(Types.INTEGER, sysUserId)
        );
    }

    @Override
    public void registerResetToken(String email, String token) throws Exception {
        queryForObjectTrx("select * from security.register_sysuser_password_token(?, ?)", String.class,
                new SqlParameterValue(Types.VARCHAR, email),
                new SqlParameterValue(Types.VARCHAR, token)
                );
    }

    @Override
    public Boolean getValidWorkingHours(Integer scheduleId, String countryCode){
        return queryForObjectTrx("select * from security.check_working_hours(?, ?)", Boolean.class,
                new SqlParameterValue(Types.INTEGER, scheduleId),
                new SqlParameterValue(Types.VARCHAR, countryCode));
    }

    @Override
    public Boolean sysUserIsActive(Integer sysUserId) {
        return queryForObjectTrx("select is_active from security.tb_sysuser where sysuser_id = ?", Boolean.class,
                new SqlParameterValue(Types.INTEGER, sysUserId));
    }

    @Override
    public Boolean isResetPasswordTokenUsed(String token) throws Exception {
        return queryForObjectTrx("select is_used from security.tb_sysusers_password_tokens where token = ?", Boolean.class,
                new SqlParameterValue(Types.VARCHAR, token));
    }

    @Override
    public String getPasswordByUsername(String username) {
        return queryForObjectTrx("select password from security.tb_sysuser where username = ?", String.class,
                new SqlParameterValue(Types.VARCHAR, username));
    }

    @Override
    public Integer getIdByUsername(String username) {
        return queryForObjectTrx("select sysuser_id from security.tb_sysuser where username = ?", Integer.class,
                new SqlParameterValue(Types.VARCHAR, username));
    }

}
