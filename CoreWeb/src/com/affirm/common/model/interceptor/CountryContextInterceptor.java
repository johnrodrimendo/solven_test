package com.affirm.common.model.interceptor;

import com.affirm.common.model.catalog.CountryParam;
import com.affirm.common.service.CountryContextService;
import com.affirm.system.configuration.Configuration;
import com.google.common.net.InternetDomainName;
import org.apache.commons.lang3.LocaleUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.ModelAndViewDefiningException;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by john on 18/08/17.
 *
 * This interceptor has 2 functinos:
 * - Set the request param COUNTRY_PARAM by domain
 * - Set the default locale by country
 */
@Component
public class CountryContextInterceptor extends HandlerInterceptorAdapter {

    private static Logger logger = Logger.getLogger(CountryContextInterceptor.class);

    @Autowired
    private CountryContextService countryContextService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if(Configuration.isBackoffice())
            return true;

        // Get and/or set the CountryParam by domain
        CountryParam countryParam = countryContextService.getCountryParamsByRequest(request);
        if(countryParam == null){
            ModelAndView model = new ModelAndView("forward:/404");
            throw new ModelAndViewDefiningException(model);
        }

        // Set the locale by domain
        LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
        if (localeResolver != null) {
            localeResolver.setLocale(request, response, LocaleUtils.toLocale(countryParam.getLocale()));
        }

        return true;
    }
}
