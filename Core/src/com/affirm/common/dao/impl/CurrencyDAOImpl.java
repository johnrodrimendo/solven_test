package com.affirm.common.dao.impl;

import com.affirm.common.dao.CurrencyDAO;
import com.affirm.common.dao.JsonResolverDAO;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.util.Date;

/**
 * Created by dev5 on 16/11/17.
 */
@Repository("currencyDao")
public class CurrencyDAOImpl extends JsonResolverDAO implements CurrencyDAO{



    @Override
    public void registerExchangeRate(Integer currencyId, Double exchangeRate) throws Exception {
        queryForObjectTrx("select * from support.register_exchange_rate(?, ?, ?)", String.class,
                new SqlParameterValue(Types.TIMESTAMP, new Date()),
                new SqlParameterValue(Types.INTEGER, currencyId),
                new SqlParameterValue(Types.NUMERIC, exchangeRate));
    }



}
