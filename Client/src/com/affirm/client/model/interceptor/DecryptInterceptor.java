package com.affirm.client.model.interceptor;

import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.model.catalog.Agent;
import com.affirm.common.model.catalog.LoanApplicationStatus;
import com.affirm.common.model.catalog.ProductCategory;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.CountryContextService;
import com.affirm.common.service.RequestScoped;
import com.affirm.common.util.AjaxResponse;
import com.affirm.common.util.CryptoUtil;
import com.affirm.system.configuration.Configuration;
import com.google.gson.Gson;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.ModelAndViewDefiningException;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by john on 18/11/16.
 */
@Component
public class DecryptInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private CatalogService catalogService;
    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private CountryContextService countryContextService;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private Provider<RequestScoped> requestScoped;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        boolean result = doPreHandle(request, response, handler);
        return result;

    }

    public boolean doPreHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception{
        this.decryptRequest(request, null);
        return true;
    }

    public static String decryptRequest(HttpServletRequest request, String field) throws Exception {
        return decryptRequest(request, field, false);
    }

    public static String decryptRequest(HttpServletRequest request, String field, boolean forceDecrypt) throws Exception {
        if(field == null) field = "sOTRIWxTDVs";
        if("POST".equalsIgnoreCase(request.getMethod())){
            if(forceDecrypt || (request.getHeader("x-request-encrypted-type") != null && request.getHeader("x-request-encrypted-type").equalsIgnoreCase("true"))){
                String parameter = request.getParameter(field);
                if (parameter != null) {
                    String newBody = CryptoUtil.aesDecryptRequest(parameter, Configuration.getAESEncrypt());
                    if(newBody != null){
                        HashMap<String, Object> objectBody = new Gson().fromJson(newBody, HashMap.class);
                        for(Map.Entry<String, Object> entry : objectBody.entrySet()) {
                            String key = entry.getKey();
                            Object value = entry.getValue();
                            request.setAttribute(key,value);
                        }
                    }
                    return newBody;
                }
            }
        }
        return null;
    }

}
