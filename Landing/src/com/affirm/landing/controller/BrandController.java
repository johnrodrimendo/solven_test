package com.affirm.landing.controller;

/**
 * Created by renzodiaz on 3/3/17.
 */

import com.affirm.client.dao.PersonCLDAO;
import com.affirm.client.model.form.EarlyAccessForm;
import com.affirm.client.model.form.TraditionalRegisterForm;
import com.affirm.common.model.annotation.ErrorControllerAnnotation;
import com.affirm.common.model.catalog.Product;
import com.affirm.common.model.catalog.ProductMaxMinParameter;
import com.affirm.common.service.CatalogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

@Controller
@Scope("request")
public class BrandController {

    @Autowired
    private CatalogService catalogService;
    @Autowired
    private PersonCLDAO personCLDAO;

//    TODO
    @RequestMapping(value = "/brand", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public String showIndex(ModelMap model, Locale locale, HttpServletRequest req) throws Exception {
        // create form with default values
        TraditionalRegisterForm form = new TraditionalRegisterForm();
        form.setLoanApplicationAmmount(5000);
        form.setLoanApplicationInstallemnts(18);

        model.addAttribute("userRegisterForm", form);
        model.addAttribute("earlyAccessForm", new EarlyAccessForm());
        Product traditionalProduct = catalogService.getProduct(Product.TRADITIONAL);
        ProductMaxMinParameter maxMinParameter = traditionalProduct.getProductParams(51);
        model.addAttribute("product", maxMinParameter);
        return "/brandPages/home";
    }

}
