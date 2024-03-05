package com.affirm.client.dao.impl;

import com.affirm.client.dao.ExtranetPartnerClientDAO;
import com.affirm.client.model.ExtranetPartnerClient;
import com.affirm.common.dao.JsonResolverDAO;
import com.affirm.common.service.CatalogService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ExtranetPartnerClientDAOImpl extends JsonResolverDAO implements ExtranetPartnerClientDAO {

    private CatalogService catalogService;

    @Autowired
    public ExtranetPartnerClientDAOImpl(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @Override
    public void registerPartnerClient(Integer entityId, JSONArray records, String listType) throws Exception {
        queryForObjectTrx("select * from person.register_associated(?, ?::JSON, ?);", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, entityId),
                new SqlParameterValue(Types.OTHER, records),
                new SqlParameterValue(Types.VARCHAR, listType));
    }

    @Override
    public List<ExtranetPartnerClient> getPartnerClients(Integer entityId) throws Exception {
        JSONArray dbArray = queryForObjectTrx("select * from person.get_raw_associateds(?)", JSONArray.class, new SqlParameterValue(Types.INTEGER, entityId));
        if (dbArray == null) {
            return new ArrayList<>();
        }

        List<ExtranetPartnerClient> partnerClientList = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            ExtranetPartnerClient extranetPartnerClient = new ExtranetPartnerClient();
            extranetPartnerClient.fillFromDb(dbArray.getJSONObject(i), catalogService);
            partnerClientList.add(extranetPartnerClient);
        }
        return partnerClientList;
    }

}
