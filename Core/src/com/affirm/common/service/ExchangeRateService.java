package com.affirm.common.service;

import org.json.JSONObject;

/**
 * Created by dev5 on 16/11/17.
 */
public interface ExchangeRateService {

    JSONObject registerExchangeRate() throws Exception;

    Double getExchangeRate(Integer countryId) throws Exception;

}
