package com.affirm.common.dao.impl;

import com.affirm.common.dao.EntityExtranetDAO;
import com.affirm.common.dao.JsonResolverDAO;
import com.affirm.common.model.transactional.BaseCount;
import com.affirm.common.model.transactional.GatewayBaseEvent;
import com.affirm.common.model.transactional.EntityGatewayBaseDetail;
import com.affirm.common.model.transactional.EntityExtranetUser;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import com.affirm.security.dao.SecurityDAO;
import com.affirm.system.configuration.Configuration;
import com.google.gson.Gson;
import org.apache.commons.lang3.tuple.Pair;
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
 * Created by jrodriguez on 26/09/16.
 */

@Repository
public class EntityExtranetDAOImpl extends JsonResolverDAO implements EntityExtranetDAO {


    @Autowired
    private SecurityDAO securityDao;
    @Autowired
    private CatalogService catalogService;

    @Override
    public void registerLogActivity(String permission, int entityExtranetUserId) {
        queryForObjectTrx("select * from security.register_lg_entity_user_action(?, ?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, entityExtranetUserId),
                new SqlParameterValue(Types.INTEGER, securityDao.getPermissionId(permission)),
                new SqlParameterValue(Types.TIMESTAMP, new Date()));
    }

    @Override
    public void registerEntityUserRole(int entiyUserId, List<Integer> roles) {
        JSONArray arrayRoles = new JSONArray();
        roles.forEach(r -> {
            JSONObject json = new JSONObject();
            json.put("roleId", r);
            arrayRoles.put(json);
        });

        queryForObjectTrx("select * from security.register_entity_user_role(?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, entiyUserId),
                new SqlParameterValue(Types.OTHER, arrayRoles.toString()));
    }

    @Override
    public void saveBatchCommissionCluster(Integer entityId, Integer productId, Integer priceId, Integer clusterId, String clustersJson) throws Exception {
        queryForObjectTrx("select * from product.register_rate_commission(?, ?, ?, ?, ?::JSON)", String.class,
                new SqlParameterValue(Types.INTEGER, entityId),
                new SqlParameterValue(Types.INTEGER, productId),
                new SqlParameterValue(Types.INTEGER, priceId),
                new SqlParameterValue(Types.INTEGER, clusterId),
                new SqlParameterValue(Types.OTHER, clustersJson));
    }

    @Override
    public List<GatewayBaseEvent> getCollectionBaseEvent(Integer entityId, Date startDate, Date endDate, Integer offset, Integer limit, Locale locale)  throws Exception{

        JSONArray dbArray = queryForObjectTrx("select * from support.get_collection_base_event(?,?,?,?,?);", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, entityId),
                new SqlParameterValue(Types.DATE, startDate),
                new SqlParameterValue(Types.DATE, endDate),
                new SqlParameterValue(Types.INTEGER, limit),
                new SqlParameterValue(Types.INTEGER, offset)
        );
        if (dbArray == null)
            return null;

        List<GatewayBaseEvent> list = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            GatewayBaseEvent data = new GatewayBaseEvent();
            data.fillFromDb(dbArray.getJSONObject(i), catalogService);
            list.add(data);
        }

        return list;
    }

    @Override
    public Pair<Integer, Double> getCollectionBaseEventCount(Integer entityId, Date startDate, Date endDate, Locale locale) throws Exception {
        JSONObject dbJson = queryForObjectTrx("select * from support.get_collection_base_event_count(?,?,?);", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, entityId),
                new SqlParameterValue(Types.DATE, startDate),
                new SqlParameterValue(Types.DATE, endDate)
        );
        if (dbJson == null)
            return Pair.of(0, 0.0);

        return Pair.of(JsonUtil.getIntFromJson(dbJson, "count", 0), null);
    }

    @Override
    public BaseCount getBaseCount(Integer entityId, Character type, Locale locale) throws Exception {
        String query = "";

        if(Configuration.hostEnvIsProduction()) query = "select * from support.get_cobranza_base_destinations_count(?,?);";
        else query = "select * from support.get_cobranza_base_destinations_count_temp(?,?);";

        JSONObject dbJson = queryForObjectTrx(query, JSONObject.class,
                new SqlParameterValue(Types.INTEGER, entityId),
                new SqlParameterValue(Types.CHAR, type)
        );
        if (dbJson == null)
            return null;
        BaseCount data = new BaseCount();
        data.fillFromDb(dbJson,catalogService,locale);
        return data;
    }

    @Override
    public EntityGatewayBaseDetail getEntityCOllectionBaseDetail(Integer entityId) throws Exception {
        JSONObject dbJson = queryForObjectTrx("select * from support.get_cobranza_base_detail(?);", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, entityId));
        if (dbJson == null)
            return null;
        EntityGatewayBaseDetail data = new EntityGatewayBaseDetail();
        data.fillFromDb(dbJson);
        return data;
    }

    @Override
    public void updateEntityUserInformation(int entityUserId, Integer entityId, List<EntityExtranetUser.MenuEntityProductCategory> entityProductCategories) {
        updateTrx("UPDATE users.tb_entity_users_entity SET js_menu_entity_product_category = ? WHERE entity_user_id = ?  AND entity_id = ?",
                new SqlParameterValue(Types.OTHER, new Gson().toJson(entityProductCategories)),
                new SqlParameterValue(Types.INTEGER, entityUserId),
                new SqlParameterValue(Types.INTEGER, entityId)
        );
    }

}
