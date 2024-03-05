package com.affirm.client.dao.impl;

import com.affirm.client.dao.UserCLDAO;
import com.affirm.client.model.LoggedUserClient;
import com.affirm.common.dao.JsonResolverDAO;
import com.affirm.common.service.CatalogService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.util.Date;

/**
 * Created by jrodriguez on 08/06/16.
 */

@Repository("userClDao")
public class UserCLDAOImpl extends JsonResolverDAO implements UserCLDAO {

    @Autowired
    private CatalogService catalogService;

    @Override
    public LoggedUserClient registerSessionLogin(int userId, String ip, String browserMetadata, Date signinDate, Integer sysUserId) throws Exception {
        JSONObject dbJson = queryForObjectTrx("select * from users.sign_in(?, ?, ?, ?, ?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, userId),
                new SqlParameterValue(Types.VARCHAR, ip),
                new SqlParameterValue(Types.VARCHAR, browserMetadata),
                new SqlParameterValue(Types.TIMESTAMP, signinDate),
                new SqlParameterValue(Types.INTEGER, sysUserId));
        if (dbJson == null)
            return null;

        LoggedUserClient client = new LoggedUserClient();
        client.fillFromDb(dbJson);
        return client;
    }
}
