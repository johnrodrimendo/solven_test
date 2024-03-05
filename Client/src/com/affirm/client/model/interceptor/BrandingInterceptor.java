package com.affirm.client.model.interceptor;

import com.affirm.common.service.BrandingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class BrandingInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    BrandingService brandingService;
//
//    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
//        if (modelAndView == null)
//            return;
//
//        brandingService.getCountryParamsByRequest(request);
////
////        if (entityBranding != null) {
////
////            EntityBranding entityBranding = catalogService.getEntityBranding(subdomain);
////
////            if (entityBranding != null) {
////                modelAndView.addObject("logoUrl", entityBranding.getEntity().getLogoUrl());
////                modelAndView.addObject("logoFooterUrl", entityBranding.getLogoFooterUrl());
////                modelAndView.addObject("primaryColor", entityBranding.getEntityPrimaryColor());
////                modelAndView.addObject("secundaryColor", entityBranding.getEntitySecundaryColor());
////                modelAndView.addObject("lightColor", entityBranding.getEntityLightColor());
////            } else {
////                modelAndView.addObject("isBranded", false);
////            }
////        } else {
////            modelAndView.addObject("isBranded", false);
////        }
//    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        brandingService.getEntityBranding(request);
        return true;
    }
}
