package com.affirm.backoffice.service.impl;

import com.affirm.backoffice.service.BackofficeService;
import com.affirm.backoffice.service.CurrencyService;
import com.affirm.common.model.catalog.Currency;
import com.affirm.common.service.CatalogService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by dev5 on 15/11/17.
 */
@Service("currencyService")
public class CurrencyServiceImpl implements CurrencyService {

    @Autowired
    BackofficeService backofficeService;
    @Autowired
    CatalogService catalogService;

    public String getCurrencySymbolReport(){
        Integer[] coutriesArr = getCountries();
        if(coutriesArr.length > 1) return Currency.USD_CURRENCY;
        if(coutriesArr.length > 0)
            return catalogService.getCountryParam(coutriesArr[0]).getCurrency().getSymbol();
        else return "";
    }

    public Boolean showInternationalCurrency(){
        Integer[] coutriesArr = getCountries();
        if(coutriesArr.length > 1) return true;
        return false;
    }

    private Integer[] getCountries(){
        String countries = backofficeService.getCountryActiveSysuser();
        Gson gson = new Gson();
        return gson.fromJson(countries, Integer[].class);
    }

}
