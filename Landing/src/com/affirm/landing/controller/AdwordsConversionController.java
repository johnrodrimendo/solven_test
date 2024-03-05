package com.affirm.landing.controller;

import com.affirm.client.model.form.EarlyAccessForm;
import com.affirm.client.model.form.TraditionalRegisterForm;
import com.affirm.common.model.annotation.ErrorControllerAnnotation;
import com.affirm.common.model.catalog.Product;
import com.affirm.common.model.catalog.ProductMaxMinParameter;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.OfflineConversionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

@Controller
@Scope("request")
public class AdwordsConversionController {

    @Autowired
    private OfflineConversionService offlineConversionService;


    @RequestMapping(value = "/conversions/adwords/{token}", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.OTHER)
    public void getAdwordsConversions(ModelMap model, Locale locale, HttpServletRequest req, HttpServletResponse res,
            @PathVariable("token") String token) throws Exception {

        byte[] file = offlineConversionService.getAdwordsOfflineConversionExcel(token);

        MediaType contentType = MediaType.valueOf("application/vnd.ms-excel");
        res.setHeader("Content-disposition", "attachment; filename=conversions.xlsx");
        res.setContentType(contentType.getType());
        res.getOutputStream().write(file);
    }
}
