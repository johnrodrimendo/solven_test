package com.affirm.common.dao.impl;

import com.affirm.common.dao.ConfigDAO;
import com.affirm.common.dao.JsonResolverDAO;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.stereotype.Repository;

import java.sql.Types;

/**
 * Created by jarmando on 09/01/17.
 */

@Repository
@CacheConfig(cacheNames = "catalogCache", keyGenerator = "cacheKeyGenerator")
public class ConfigDAOImpl extends JsonResolverDAO implements ConfigDAO {



    @Override
    public void updateExchangeRateUSDPEN(Double USDPEN) throws Exception {
        updateTrx("UPDATE support.ct_config SET values=? WHERE config_id=11", new SqlParameterValue(Types.VARCHAR, USDPEN.toString()));
    }


}
