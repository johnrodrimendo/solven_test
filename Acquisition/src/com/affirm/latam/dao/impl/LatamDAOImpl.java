package com.affirm.latam.dao.impl;

import com.affirm.common.dao.JsonResolverDAO;
import com.affirm.common.service.UtilService;
import com.affirm.common.util.JsonUtil;
import com.affirm.latam.dao.LatamDAO;
import com.affirm.system.configuration.SpringRootConfiguration;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

@Repository
@CacheConfig(cacheNames = SpringRootConfiguration.LATAM_CACHE_NAME, keyGenerator = "cacheKeyGenerator")
public class LatamDAOImpl extends JsonResolverDAO implements LatamDAO {

    private UtilService utilService;

    @Autowired
    public LatamDAOImpl(UtilService utilService) {
        this.utilService = utilService;
    }

    @Override
    @Cacheable
    public JSONObject avgCreditRate() {
        JSONObject json = queryForObjectTrx("select * from support.get_impact_values()", JSONObject.class);

        if (json == null)
            return null;

        JSONObject result = new JSONObject();
        result.put("avg_credit_rate", utilService.percentFormat(Math.abs((JsonUtil.getDoubleFromJson(json , "avg_solven_rate", 0.0) /  JsonUtil.getDoubleFromJson(json , "avg_market_rate", 0.0)) - 1) * 100.0));
        result.put("avg_granted_credits", utilService.percentFormat(JsonUtil.getDoubleFromJson(json , "rcc_calif", 0.0) * 100.0));

        return result;
    }

}
