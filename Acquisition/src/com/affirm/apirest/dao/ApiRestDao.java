package com.affirm.apirest.dao;

import com.affirm.apirest.model.ApiRestToken;
import com.affirm.common.dao.JsonResolverDAO;
import com.affirm.common.model.transactional.ApiRestUser;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.model.transactional.ReportProces;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import com.jcabi.aspects.Cacheable;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.stereotype.Repository;

import java.sql.Types;

@Repository("apiRestDao")
public class ApiRestDao  extends JsonResolverDAO {

    @Autowired
    private CatalogService catalogService;

    public ApiRestToken registerApiRestToken(ApiRestToken apiRestToken) throws Exception {
        Long tokenId = queryForObjectTrx("insert into support.tb_api_rest_token (token, api_rest_user_id, register_date, validity_date) values (?, ?, ?, ?) returning id;", Long.class,
                new SqlParameterValue(Types.VARCHAR, apiRestToken.getToken()),
                new SqlParameterValue(Types.INTEGER, apiRestToken.getApiRestUserId()),
                new SqlParameterValue(Types.TIMESTAMP, apiRestToken.getRegisterDate()),
                new SqlParameterValue(Types.TIMESTAMP, apiRestToken.getValidityDate()));

        apiRestToken.setId(tokenId);
        return apiRestToken;
    }

    public ApiRestUser getApiRestUserByCredentials(String user, String password) throws Exception {
        JSONObject dbJson = queryForObjectTrx("select * from support.get_api_rest_user_by_credentials(?,?)", JSONObject.class,
                new SqlParameterValue(Types.VARCHAR, user),
                new SqlParameterValue(Types.VARCHAR, password)
                );
        if (dbJson == null) {
            return null;
        }

        ApiRestUser data = new ApiRestUser();
        data.fillFromDb(dbJson,catalogService);
        return data;
    }

    public ApiRestUser getApiRestUserById(Integer userId) throws Exception {
        JSONObject dbJson = queryForObjectTrx("select * from support.get_api_rest_user_by_id(?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, userId)
        );
        if (dbJson == null) {
            return null;
        }

        ApiRestUser data = new ApiRestUser();
        data.fillFromDb(dbJson,catalogService);
        return data;
    }


    public ApiRestToken getApiRestToken(String token) throws Exception {
        JSONObject dbJson = queryForObjectTrx("select * from support.get_api_rest_token(?)", JSONObject.class,
                new SqlParameterValue(Types.VARCHAR, token)
        );
        if (dbJson == null) {
            return null;
        }
        ApiRestToken data = new ApiRestToken();
        data.fillFromDb(dbJson);
        return data;
    }


}
