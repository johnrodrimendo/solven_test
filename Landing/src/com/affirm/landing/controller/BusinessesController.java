package com.affirm.landing.controller;

import com.affirm.client.model.form.CompanyContactForm;
import com.affirm.client.model.form.SalaryAdvanceForm;
import com.affirm.common.dao.NewDAO;
import com.affirm.common.model.New;
import com.affirm.common.model.annotation.ErrorControllerAnnotation;
import com.affirm.common.model.catalog.CountryParam;
import com.affirm.common.model.catalog.ProductCategory;
import com.affirm.common.model.form.BusinessesContactForm;
import com.affirm.common.model.form.ShortProcessQuestion1Form;
import com.affirm.common.service.BrandingService;
import com.affirm.common.service.CountryContextService;
import com.affirm.common.service.PipedriveService;
import com.affirm.common.service.ProductService;
import com.affirm.common.util.AjaxResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

@Controller
@Scope("request")
public class BusinessesController {

    @Autowired
    private CountryContextService countryContextService;

    @Autowired
    private BrandingService brandingService;

    @Autowired
    private ProductService productService;

    @Autowired
    private NewDAO newDao;

    @Autowired
    private PipedriveService pipedriveService;

    @RequestMapping( value= "/colaboradores",method = RequestMethod.GET)
    public String getColaboradores(
            ModelMap model, Locale locale, HttpServletRequest request) throws Exception {

        int countryId = countryContextService.getCountryParamsByRequest(request).getId();

        if(countryId == CountryParam.COUNTRY_ARGENTINA)
            return "redirect:/";

        ShortProcessQuestion1Form consumoForm = new ShortProcessQuestion1Form();
        consumoForm.setCountryId(countryId);
        ((ShortProcessQuestion1Form.Validator) consumoForm.getValidator()).amount.setMaxValue(productService.getMaxAmount(ProductCategory.CONSUMO, consumoForm.getCountryId()));
        ((ShortProcessQuestion1Form.Validator) consumoForm.getValidator()).amount.setMinValue(productService.getMinAmount(ProductCategory.CONSUMO, consumoForm.getCountryId()));
        ((ShortProcessQuestion1Form.Validator) consumoForm.getValidator()).configValidator(consumoForm.getCountryId());
        model.addAttribute("consumoForm", consumoForm);

        model.addAttribute("contactForm", new CompanyContactForm());
        model.addAttribute("clientCountry", countryId);

        boolean isBranded = brandingService.isBranded(request);
        model.addAttribute("isBranded", isBranded);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -75);
        model.addAttribute("yearFrom", calendar.get(Calendar.YEAR));
        model.addAttribute("yearTo", Calendar.getInstance().get(Calendar.YEAR));

        if (!isBranded) {
            List<New> news = newDao.getNews(consumoForm.getCountryId());
            model.addAttribute("news", news);
        }

        model.addAttribute("adelantoForm", new SalaryAdvanceForm());
        model.addAttribute("showZendesk", false);
        //return "/landingColaboradores";
        return "forward:/404";
    }

    @RequestMapping( value= "/empresas",method = RequestMethod.GET)
    public String getEmpresas(
            ModelMap model, Locale locale, HttpServletRequest request) throws Exception {

        int countryId = countryContextService.getCountryParamsByRequest(request).getId();
        if(countryId == CountryParam.COUNTRY_ARGENTINA)
            return "redirect:/";

        CountryParam currentCountry = countryContextService.getCountryParamsByRequest(request);
        model.addAttribute("businessContactForm", new BusinessesContactForm());
        model.addAttribute("news", newDao.getNews(currentCountry.getId()));
        model.addAttribute("showZendesk", false);
        return "forward:/404";
    }

    @RequestMapping(value = "/empresas", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public Object sendCompanyContactEmail(HttpServletRequest req, BusinessesContactForm businessesContactForm, Locale locale) throws Exception {

        businessesContactForm.getValidator().validate(locale);

        if (businessesContactForm.getValidator().isHasErrors()) {
            return AjaxResponse.errorFormValidation(businessesContactForm.getValidator().getErrorsJson());
        }
        pipedriveService.submitLandingForm(businessesContactForm.getName(), businessesContactForm.getCompany(), businessesContactForm.getEmail(), businessesContactForm.getPhone(), businessesContactForm.getPosition(), businessesContactForm.getSize(), businessesContactForm.getSource());
        Thread.sleep(1800);
        return AjaxResponse.ok(null);
    }
}
