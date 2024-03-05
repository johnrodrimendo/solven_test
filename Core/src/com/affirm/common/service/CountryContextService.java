package com.affirm.common.service;

import com.affirm.common.model.catalog.CountryParam;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by jarmando on 26/01/17.
 */
public interface CountryContextService {
    CountryParam getCountryParamsByRequest(HttpServletRequest request);

    boolean isCountryContextInPeru(HttpServletRequest request);

    boolean isCountryContextInArgentina(HttpServletRequest request);

    boolean isCountryContextInColombia(HttpServletRequest request);

    boolean isContextInCountry(int country, HttpServletRequest request);

    Double getCountryDefaultMapLatitude(HttpServletRequest request);

    Double getCountryDefaultMapLongitude(HttpServletRequest request);

    Double getCountryDefaultMapLatitude(int countryId);

    Double getCountryDefaultMapLongitude(int countryId);

    String getCountryStringCode(int countryId);

    String getTimeZone(int countryId);
}
