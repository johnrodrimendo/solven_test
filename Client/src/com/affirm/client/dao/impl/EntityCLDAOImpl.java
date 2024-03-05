package com.affirm.client.dao.impl;

import com.affirm.client.dao.EntityCLDAO;
import com.affirm.client.model.LoggedUserEntity;
import com.affirm.client.util.MaxUserSessionReachedException;
import com.affirm.client.util.NoUserFoundException;
import com.affirm.common.dao.JsonResolverDAO;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.shiro.authc.AuthenticationException;
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

@Repository("entityClDao")
public class EntityCLDAOImpl extends JsonResolverDAO implements EntityCLDAO {

    @Autowired
    private CatalogService catalogService;

    @Override
    public String getHashedPassword(String email) {
        return queryForObjectTrx("select * from users.get_entity_user_password(?)",
                String.class,
                new SqlParameterValue(Types.VARCHAR, email));
    }

    @Override
    public LoggedUserEntity registerSessionEntity(String email, String ip, String metadata, Date signinDate, Locale locale, Integer entityId) throws AuthenticationException {
        JSONObject dbJson = queryForObjectTrx("select * from users.entity_sign_in(?, ?, ?, ?, ?)", JSONObject.class,
                new SqlParameterValue(Types.VARCHAR, email),
                new SqlParameterValue(Types.VARCHAR, ip),
                new SqlParameterValue(Types.VARCHAR, metadata),
                new SqlParameterValue(Types.TIMESTAMP, signinDate),
                new SqlParameterValue(Types.INTEGER, entityId));
        if (dbJson == null)
            throw new NoUserFoundException();

        if(dbJson.has("max_entity_user_sessions")) {
            throw new MaxUserSessionReachedException();
        }

        LoggedUserEntity entityUser = new LoggedUserEntity();
        entityUser.fillFromDb(dbJson, catalogService, locale);
        return entityUser;
    }

    @Override
    public void registerSessionLogout(int extranetSessionId, Date signoutDate) throws Exception {
        queryForObjectTrx("select * from users.entity_sign_out(?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, extranetSessionId),
                new SqlParameterValue(Types.TIMESTAMP, signoutDate));
    }

    @Override
    public JSONArray getCreditFullInfo(int entityId) throws Exception {
        JSONArray dbJson = queryForObjectTrx("select * from credit.get_credit_person_full(?)", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, entityId));

        return dbJson;
    }

    @Override
    public void activateTfaLogin(int entityId, boolean activate) {
        queryForObjectTrx("select * from security.activate_entity_tfa_login(?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, entityId),
                new SqlParameterValue(Types.BOOLEAN, activate));
    }

    @Override
    public void updateSharedSecret(int entityUserId, JSONArray tfaScratchCode, String tfaSharedSecret) {
        queryForObjectTrx("select * from security.update_entity_user_tfa(?, ?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, entityUserId),
                new SqlParameterValue(Types.OTHER, tfaScratchCode.toString()),
                new SqlParameterValue(Types.VARCHAR, tfaSharedSecret));
    }

    @Override
    public String registerEntityUser(int entityId, String name, String firstSurname, String email) {
        JSONObject dbJson = queryForObjectTrx("select * from users.register_entity_user(?, ?, ?, ?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, entityId),
                new SqlParameterValue(Types.VARCHAR, name),
                new SqlParameterValue(Types.VARCHAR, firstSurname),
                new SqlParameterValue(Types.VARCHAR, email));

        String message = JsonUtil.getStringFromJson(dbJson, "message", null);

        return message;
    }

    @Override
    public List<Integer> getRolesByEntityUser(int entityUserId) {
        JSONArray dbJson = queryForObjectTrx("select * from security.get_entity_user_roles(?)", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, entityUserId));

        if (dbJson == null)
            return  null;

        List<Integer> roles = new Gson().fromJson(dbJson.toString(), new TypeToken<ArrayList<Integer>>() {
            }.getType());

        return roles;
    }
}
