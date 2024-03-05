package com.affirm.referrerExt.interceptor;

import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.model.catalog.CountryParam;
import com.affirm.common.model.catalog.ProductCategory;
import com.affirm.common.model.catalog.ProductCategoryCountry;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.CountryContextService;
import com.affirm.common.service.ProductService;
import com.affirm.referrerExt.controller.ReferrerExtLandingController;
import com.affirm.referrerExt.service.ReferralExtranetAuthService;
import com.affirm.referrerExt.util.LoggedReferrerUser;
import com.affirm.system.configuration.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.ModelAndViewDefiningException;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by john on 18/11/16.
 */
@Component
public class ReferrerExtSessionInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private ReferralExtranetAuthService referralExtranetAuthService;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        LoggedReferrerUser loggedReferrerUser = referralExtranetAuthService.getSessionReferralUser();
        if (loggedReferrerUser == null) {
            ModelAndView model = new ModelAndView("redirect:/" + ReferrerExtLandingController.BASE_URI_EXT_REFERRAL);
            throw new ModelAndViewDefiningException(model);
        }

        return true;
    }

}
