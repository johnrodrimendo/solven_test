package com.affirm.common.dao;

/**
 * Created by dev5 on 16/11/17.
 */
public interface CurrencyDAO {

    void registerExchangeRate(Integer countryId, Double exchangeRate) throws Exception;

}
