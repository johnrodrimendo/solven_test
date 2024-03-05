package com.affirm.common.service.impl;

import com.affirm.common.model.catalog.CountryParam;
import com.affirm.common.model.catalog.ProductCountryDomain;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.CountryContextService;
import com.affirm.common.service.ProductService;
import com.affirm.common.service.UtilService;
import com.affirm.system.configuration.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by jarmando on 26/01/17.
 */
@Service("countryContextService")
public class CountryContextServiceImpl implements CountryContextService {

    public static final String COUNTRY_PARAM_REQUEST_KEY = "COUNTRY_PARAM";
    public static final String COUNTRY_PARAM_REQUEST_NULL = "countryNull";

    @Autowired
    private CatalogService catalogService;
    @Autowired
    private UtilService utilService;
    @Autowired
    private ProductService productService;

    @Override
    public CountryParam getCountryParamsByRequest(HttpServletRequest request) {
        if (request.getAttribute(COUNTRY_PARAM_REQUEST_KEY) != null && request.getAttribute(COUNTRY_PARAM_REQUEST_KEY) instanceof CountryParam)
            return (CountryParam) request.getAttribute(COUNTRY_PARAM_REQUEST_KEY);
        else if (request.getAttribute(COUNTRY_PARAM_REQUEST_KEY) != null && request.getAttribute(COUNTRY_PARAM_REQUEST_KEY) instanceof String && ((String) request.getAttribute(COUNTRY_PARAM_REQUEST_KEY)).equals(COUNTRY_PARAM_REQUEST_NULL))
            return null;

        String requestDomain = utilService.getDomainOfRequest(request);
        if(Configuration.hostEnvIsLocal() && requestDomain.indexOf("ngrok") != -1){
            requestDomain = "localhost";
        }

        String finalRequestDomain = requestDomain;
        CountryParam countryParam = catalogService.getCountryParams().stream().filter(c -> c.getDomains().stream().anyMatch(s -> s.equalsIgnoreCase(finalRequestDomain))).findFirst().orElse(null);

        if(countryParam == null){
            System.out.println("Didnt find domain: " + requestDomain);
            ProductCountryDomain productCountryDomain = productService.getProductDomainByRequest(request);
            if(productCountryDomain != null)
                countryParam = catalogService.getCountryParam(productCountryDomain.getCountryId());
        }

        request.setAttribute(COUNTRY_PARAM_REQUEST_KEY, countryParam != null ? countryParam : COUNTRY_PARAM_REQUEST_NULL);

        return countryParam;
    }

    @Override
    public boolean isCountryContextInPeru(HttpServletRequest request) {
        return getCountryParamsByRequest(request).getId() == CountryParam.COUNTRY_PERU;
    }

    @Override
    public boolean isCountryContextInArgentina(HttpServletRequest request) {
        return getCountryParamsByRequest(request).getId() == CountryParam.COUNTRY_ARGENTINA;
    }

    @Override
    public boolean isCountryContextInColombia(HttpServletRequest request) {
        return getCountryParamsByRequest(request).getId() == CountryParam.COUNTRY_COLOMBIA;
    }

    @Override
    public boolean isContextInCountry(int country, HttpServletRequest request) {
        return getCountryParamsByRequest(request).getId() == country;
    }

    @Override
    public Double getCountryDefaultMapLatitude(HttpServletRequest request) {
        return getCountryDefaultMapLatitude(getCountryParamsByRequest(request).getId());
    }

    @Override
    public Double getCountryDefaultMapLongitude(HttpServletRequest request) {
        return getCountryDefaultMapLongitude(getCountryParamsByRequest(request).getId());
    }

    @Override
    public Double getCountryDefaultMapLatitude(int countryId) {
        switch (countryId){
            case CountryParam.COUNTRY_PERU:
                return -12.046469;
            case CountryParam.COUNTRY_ARGENTINA:
                return -34.608696;
            case CountryParam.COUNTRY_COLOMBIA:
                    return 4.711019;
        }
        return null;
    }

    @Override
    public Double getCountryDefaultMapLongitude(int countryId) {
        switch (countryId){
            case CountryParam.COUNTRY_PERU:
                return -77.042788;
            case CountryParam.COUNTRY_ARGENTINA:
                return -58.435051;
            case CountryParam.COUNTRY_COLOMBIA:
                return -74.072125;
        }
        return null;
    }

    @Override
    public String getCountryStringCode(int countryId) {
        switch (countryId){
            case CountryParam.COUNTRY_PERU:
                return "pe";
            case CountryParam.COUNTRY_ARGENTINA:
                return "ar";
        }
        return null;
    }

    @Override
    public String getTimeZone(int countryId) {
        switch (countryId) {
            case CountryParam.COUNTRY_PERU:
                return "-0500";
            case CountryParam.COUNTRY_ARGENTINA:
                return "-0300";
        }
        return null;
    }
}
