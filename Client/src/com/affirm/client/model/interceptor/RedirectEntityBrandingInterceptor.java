package com.affirm.client.model.interceptor;

import com.affirm.common.model.catalog.EntityBranding;
import com.affirm.common.model.catalog.EntityProduct;
import com.affirm.common.model.catalog.ProductCategory;
import com.affirm.common.service.BrandingService;
import com.affirm.common.service.CatalogService;
import com.affirm.system.configuration.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.ModelAndViewDefiningException;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class RedirectEntityBrandingInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    BrandingService brandingService;
    @Autowired
    CatalogService catalogService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        boolean result = doPreHandle(request, response, handler);
        return result;
    }

    public boolean doPreHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception{
        EntityBranding branding = brandingService.getEntityBranding(request);

        if (branding == null)
            return true;

//        String urlToRedirect = null;
        List<EntityProduct> entityProducts = catalogService.getEntityProductsByEntity(branding.getEntity().getId());
        List<String> urlsToRedirect = new ArrayList<>();
        if (entityProducts != null && !entityProducts.isEmpty()) {
            if (entityProducts.stream().anyMatch(ep -> ep.getProduct().getProductCategoryId() == ProductCategory.CONSUMO))
                urlsToRedirect.add("/" + ProductCategory.CONSUMO_CATEGORY_URL);
            if (entityProducts.stream().anyMatch(ep -> ep.getProduct().getProductCategoryId() == ProductCategory.GATEWAY))
                urlsToRedirect.add("/" + ProductCategory.GATEWAY_URL);
            if (entityProducts.stream().anyMatch(ep -> ep.getProduct().getProductCategoryId() == ProductCategory.CUENTA_BANCARIA))
                urlsToRedirect.add("/" + ProductCategory.CUENTA_BANCARIA_URL);
            if (entityProducts.stream().anyMatch(ep -> ep.getProduct().getProductCategoryId() == ProductCategory.CONSEJ0))
                urlsToRedirect.add("/" + ProductCategory.CONSEJ0_URL);
            if (entityProducts.stream().anyMatch(ep -> ep.getProduct().getProductCategoryId() == ProductCategory.TARJETA_CREDITO))
                urlsToRedirect.add("/" + ProductCategory.TARJETA_CREDITO_CATEGORY_URL);
            if (entityProducts.stream().anyMatch(ep -> ep.getProduct().getProductCategoryId() == ProductCategory.VALIDACION_IDENTIDAD))
                urlsToRedirect.add("/" + ProductCategory.VALIDACION_IDENTIDAD_URL);
            if(urlsToRedirect.isEmpty())
                urlsToRedirect.add("/" + ProductCategory.GET_URL_BY_ID(entityProducts.get(0).getProduct().getProductCategoryId()) + "/" + Configuration.EVALUATION_CONTROLLER_URL);
        }

        String pathInfo = request.getPathInfo() != null ? request.getPathInfo() : "";
        if (!urlsToRedirect.isEmpty() && urlsToRedirect.stream().anyMatch(u -> pathInfo.startsWith(u))) {
            return true;
        } else if (!urlsToRedirect.isEmpty() && urlsToRedirect.stream().noneMatch(u -> pathInfo.startsWith(u))) {
            throw new ModelAndViewDefiningException(new ModelAndView("redirect:" + urlsToRedirect.get(0)));
        } else {
            throw new ModelAndViewDefiningException(new ModelAndView("forward:/404"));
        }
    }
}
