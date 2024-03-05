package com.affirm.client.model.interceptor;

import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.model.catalog.CountryParam;
import com.affirm.common.model.catalog.ProductCategory;
import com.affirm.common.model.catalog.ProductCategoryCountry;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.CountryContextService;
import com.affirm.common.service.ProductService;
import com.affirm.system.configuration.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.ModelAndViewDefiningException;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * Created by john on 18/11/16.
 */
@Component
public class ProductCategoryInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private CatalogService catalogService;
    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private CountryContextService countryContextService;
    @Autowired
    private ProductService productService;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        boolean result = doPreHandle(request, response, handler);
        return result;
    }

    public boolean doPreHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception{
        String[] requestPaths = request.getPathInfo().split("/");
        String productCategoryRequested = requestPaths[1];
        boolean isEvaluation = requestPaths.length > 2 && requestPaths[2].equals(Configuration.EVALUATION_CONTROLLER_URL);
        int productCategoryRequestedId = ProductCategory.GET_ID_BY_URL(productCategoryRequested);
        CountryParam requestedCountry = countryContextService.getCountryParamsByRequest(request);

        ProductCategoryCountry countryConfig = productService.getProductCategoryCountry(productCategoryRequestedId, requestedCountry.getId());

        if(isEvaluation){
            if (countryConfig == null || countryConfig.getActive() == null || !countryConfig.getActive())
                throw new ModelAndViewDefiningException(new ModelAndView("forward:/404"));
        }else{
            if (countryConfig == null)
                throw new ModelAndViewDefiningException(new ModelAndView("forward:/404"));

            if(countryConfig.getCategoryId() == ProductCategory.VEHICULO){
                boolean isSearchVehicle = requestPaths.length > 2 && requestPaths[2].equals("busqueda");
                boolean isDetalles = requestPaths.length > 2 && requestPaths[2].equals("detalles");
                if((isSearchVehicle || isDetalles) && (countryConfig.getActive() == null || !countryConfig.getActive())){
                    throw new ModelAndViewDefiningException(new ModelAndView("forward:/404"));
                }
            }
        }

        return true;
    }

}
