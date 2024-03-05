package com.affirm.common.service.impl;

import com.affirm.common.dao.CurrencyDAO;
import com.affirm.common.model.catalog.CountryParam;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.ExchangeRateService;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dev5 on 16/11/17.
 */
@Service("exchangeRateService")
public class ExchangeRateServiceImpl implements ExchangeRateService {

    private final String API_LAYER_ACCESS_KEY = "ecb98736fc6ac7975251d8d7d24cff58";

    private final CurrencyDAO currencyDAO;
    private final CatalogService catalogService;

    public ExchangeRateServiceImpl(CurrencyDAO currencyDAO, CatalogService catalogService) {
        this.currencyDAO = currencyDAO;
        this.catalogService = catalogService;
    }

    @Override
    public JSONObject registerExchangeRate() throws Exception {
        List<String> isoCurrencies = new ArrayList<>();
        isoCurrencies.add("USD");
        isoCurrencies.add("PEN");
        isoCurrencies.add("ARS");
        isoCurrencies.add("COP");

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(String.format("http://www.apilayer.net/api/live?access_key=%s&currencies=%s&format=1", API_LAYER_ACCESS_KEY, String.join("%2C", isoCurrencies)))
                .get()
                .addHeader("cache-control", "no-cache")
                .build();

        Response response = client.newCall(request).execute();
        String responseReturn = response.body().string();

        JSONObject obj = new JSONObject(responseReturn);
        JSONObject rates = obj.getJSONObject("quotes");

        currencyDAO.registerExchangeRate(catalogService.getCountryParam(CountryParam.COUNTRY_ARGENTINA).getCurrency().getId(), rates.getDouble("USDARS"));
        currencyDAO.registerExchangeRate(catalogService.getCountryParam(CountryParam.COUNTRY_PERU).getCurrency().getId(), rates.getDouble("USDPEN"));
        currencyDAO.registerExchangeRate(catalogService.getCountryParam(CountryParam.COUNTRY_COLOMBIA).getCurrency().getId(), rates.getDouble("USDCOP"));

        return obj;
    }

    @Override
    public Double getExchangeRate(Integer countryId) throws Exception {
        return catalogService.getCurrency(catalogService.getCountryParam(countryId).getCurrency().getId()).getExchangeRate();
    }
}
