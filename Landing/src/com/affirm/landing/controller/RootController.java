package com.affirm.landing.controller;

import com.affirm.client.dao.PersonCLDAO;
import com.affirm.client.model.form.*;
import com.affirm.client.service.EmailCLService;
import com.affirm.client.service.SampleService;
import com.affirm.common.dao.NewDAO;
import com.affirm.common.dao.UserDAO;
import com.affirm.common.model.New;
import com.affirm.common.model.annotation.ErrorControllerAnnotation;
import com.affirm.common.model.catalog.*;
import com.affirm.common.model.form.CreateRemarketingLoanApplicationForm;
import com.affirm.common.model.form.PartnerForm;
import com.affirm.common.model.form.ShortProcessQuestion1Form;
import com.affirm.common.service.*;
import com.affirm.common.util.AjaxResponse;
import com.affirm.common.util.CryptoUtil;
import com.affirm.common.util.StringFieldValidator;
import com.affirm.common.util.ValidatorUtil;
import com.affirm.rextie.client.RextieClient;
import com.affirm.security.service.ReCaptchaService;
import com.affirm.system.configuration.Configuration;
import net.sf.ehcache.CacheManager;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author jrodriguez
 */
@Controller
@Scope("request")
public class RootController {

    private static Logger logger = Logger.getLogger(RootController.class);

    @Autowired
    private UserDAO userDao;
    @Autowired
    SampleService sampleService;
    @Autowired
    CatalogService catalogService;
    @Autowired
    private EmailCLService emailCLService;
    @Autowired
    CacheManager ehCacheManager;
    @Autowired
    private ReCaptchaService reCaptchaService;
    @Autowired
    private UtilService utilService;
    @Autowired
    private CreditService creditService;
    @Autowired
    private CountryContextService countryContextService;
    @Autowired
    private PersonCLDAO personCLDAO;
    @Autowired
    private ProductService productService;
    @Autowired
    private NewDAO newDao;
    @Autowired
    private BrandingService brandingService ;
    @Autowired
    private LoanApplicationController loanApplicationController;
    @Autowired
    private RextieClient rextieClient;
    @Autowired
    private ServletContext servletContext;

    // -------------------- Landings
    @RequestMapping(value = {"/", "/AW9lgiio1rtiz5,WA8y*hdvSxvEMiHJG"}, method = RequestMethod.GET)
    public String homepage(
            ModelMap model, Locale locale, HttpServletRequest request) throws Exception {

        ProductCountryDomain productCountryDomain = productService.getProductDomainByRequest(request);
        if(productCountryDomain != null){
            if(productCountryDomain.getProductId() == Product.LEADS)
                return "forward:"+EfectivoAlToqueController.EFECTIVO_AL_TOQUE;
        }

        UserRegisterForm form = new UserRegisterForm();
        EarlyAccessForm formSooner = new EarlyAccessForm();
        ContactForm contactForm = new ContactForm();
        CountryParam currentCountry = countryContextService.getCountryParamsByRequest(request);

        List<New> news = newDao.getNews(currentCountry.getId());

        model.addAttribute("soon", true);
        model.addAttribute("userRegisterForm", form);
        model.addAttribute("contactForm", contactForm);
        model.addAttribute("earlyAccessForm", formSooner);
        model.addAttribute("news", news);

        if (currentCountry.getId().equals(CountryParam.COUNTRY_ARGENTINA)) {
            Integer maxAmount = productService.getMaxAmount(ProductCategory.CONSUMO, CountryParam.COUNTRY_ARGENTINA);
            Integer minAmount = productService.getMinAmount(ProductCategory.CONSUMO, CountryParam.COUNTRY_ARGENTINA);;

            model.addAttribute("maxAmount", maxAmount);
            model.addAttribute("minAmount", minAmount);
            return "/landingArgentina";
        } else if (currentCountry.getId().equals(CountryParam.COUNTRY_PERU)) {
            //model.addAttribute("currencyRates", rextieClient.exchangeRateQuote(Rextie.CurrencyType.USD,Rextie.CurrencyType.PEN,100.00,null));
            model.addAttribute("isExchangeActive",false);
            return "/landingPeru";
        } else if (currentCountry.getId().equals(CountryParam.COUNTRY_COLOMBIA)) {
            model.addAttribute("isExchangeActive",false);
            return "/landingColombia";
        }

        return "/home";
    }

    @RequestMapping(value = "/credito-consumo", method = RequestMethod.GET) //credito de consumo - debería ser
    public String getCreditoConsumo(
            ModelMap model, Locale locale, HttpServletRequest request) throws Exception {
        CountryParam currentCountry = countryContextService.getCountryParamsByRequest(request);
        List<New> news = newDao.getNews(currentCountry.getId());

        model.addAttribute("contactForm", new ContactForm());
        model.addAttribute("news", news);

        return "/page-credito-consumo";
    }

    @RequestMapping(value = "/verificacion-email", method = RequestMethod.GET) //credito de consumo - debería ser
    public String getVerficacionEmail(ModelMap model, Locale locale, HttpServletRequest request) throws Exception {
        CountryParam currentCountry = countryContextService.getCountryParamsByRequest(request);

        return "/page-verificacion-email";
    }


    @RequestMapping(value = "/" + ProductCategory.VEHICULO_CATEGORY_URL, method = RequestMethod.GET) //Crédito de Vehicular
    public String getCreditoVehicular(
            ModelMap model, Locale locale, HttpServletRequest request) throws Exception {
        CountryParam currentCountry = countryContextService.getCountryParamsByRequest(request);
        List<New> news = newDao.getNews(currentCountry.getId());

        model.addAttribute("contactForm", new ContactForm());
        model.addAttribute("productCategory", productService.getProductCategoryCountry(ProductCategory.VEHICULO, countryContextService.getCountryParamsByRequest(request).getId()));
        model.addAttribute("news", news);

        return "/page-credito-vehicular";
    }


    @RequestMapping(value = "/calculadora", method = RequestMethod.GET) //Calculadora
    public String getCalculadora(
            ModelMap model, Locale locale, HttpServletRequest request) throws Exception {
        model.addAttribute("contactForm", new ContactForm());
        return "/page-calculadora";
    }

    @RequestMapping(value = "/" + ProductCategory.VEHICULO_CATEGORY_URL + "/{type:busqueda|resultados}", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public String carFinancingBrandLists(
            ModelMap model, Locale locale, HttpServletRequest request,
            @PathVariable("type") String type,
            @RequestParam(value = "brands", required = false) String brands,
            @RequestParam(value = "priceFrom", required = false) Integer priceFrom,
            @RequestParam(value = "priceTo", required = false) Integer priceTo,
            @RequestParam(value = "order", required = false) String order,
            @RequestParam(value = "gasType[]", required = false) String gasType,
            @RequestParam(value = "transmition[]", required = false) String transmition) throws Exception {

        List<Vehicle> vehicles = catalogService.getVehicles(locale);
        if (brands != null) {
            List<Integer> brandsIds = new ArrayList<>();
            JSONArray jsonBrands = new JSONArray(brands);
            for (int i = 0; i < jsonBrands.length(); i++) {
                brandsIds.add(jsonBrands.getInt(i));
            }
            vehicles = vehicles.stream().filter(v -> brandsIds.contains(v.getBrand().getId())).collect(Collectors.toList());
        } else {
            brands = Arrays.toString(vehicles.stream().map(v -> v.getBrand()).distinct().mapToInt(b -> b.getId()).toArray());
        }
        if (priceFrom != null) {
            vehicles = vehicles.stream().filter(v -> v.getListPrice() >= priceFrom).collect(Collectors.toList());
        }
        if (priceTo != null) {
            vehicles = vehicles.stream().filter(v -> v.getListPrice() <= priceTo).collect(Collectors.toList());
        }
        if (gasType != null) {
            String[] gasTypes;
            gasTypes = gasType.split(",");
            List gasTypesList = new ArrayList();
            Collections.addAll(gasTypesList, gasTypes);

            if(gasTypesList.size() > 0) vehicles = vehicles.stream().filter(v -> v.getTransmission() == null || gasTypesList.contains(String.valueOf(v.getGasType().getId()))).collect(Collectors.toList());
        }
        if (transmition != null && transmition.length() > 0) {
            String[] transmitions;
            transmitions = transmition.split(",");
            List transmitionsList = new ArrayList();
            Collections.addAll(transmitionsList, transmitions);

            if(transmitionsList.size() > 0) vehicles = vehicles.stream().filter(v -> v.getTransmission() == null || transmitionsList.contains(v.getTransmission().toString())).collect(Collectors.toList());
        }

        if (order != null) {
            switch (order) {
                case "lower_price":
                    vehicles = vehicles.stream().sorted(Comparator.comparingInt(Vehicle::getListPrice)).collect(Collectors.toList());
                    break;
                case "higher_price":
                    vehicles = vehicles.stream().sorted(Comparator.comparingInt(Vehicle::getListPrice).reversed()).collect(Collectors.toList());
                    break;
            }
        } else {
            vehicles = vehicles.stream().sorted(Comparator.comparingInt(Vehicle::getListPrice)).collect(Collectors.toList());
        }

        if (type.equals("busqueda")) {
            ProcessContactForm processContactForm = new ProcessContactForm();
            model.addAttribute("processContactForm", processContactForm);
            model.addAttribute("results", vehicles);
            model.addAttribute("brands", brands);
            model.addAttribute("priceFrom", priceFrom);
            model.addAttribute("priceTo", priceTo);
            model.addAttribute("order", order);
            model.addAttribute("gasType", gasType);
            model.addAttribute("transmition", transmition);
            return "/products/carFinancing/searchVehicles";
        } else {
            model.addAttribute("results", vehicles);
            return "/products/carFinancing/searchVehicles :: result";
        }
    }

    @RequestMapping(value = "/" + ProductCategory.VEHICULO_CATEGORY_URL + "/detalles/{groupId}", method = RequestMethod.GET)
    public String carFinancingDetailsLists(
            ModelMap model, Locale locale, HttpServletRequest request,
            @PathVariable("groupId") int groupId) throws Exception {
        ProcessContactForm processContactForm = new ProcessContactForm();
        Vehicle vehicle = catalogService.getVehicle(groupId);

        model.addAttribute("processContactForm", processContactForm);
        model.addAttribute("vehicle", vehicle);
        model.addAttribute("vehicleDetails", vehicle.getVehicleDetails().get(0));
        return "/products/carFinancing/carDetails";
    }

    @RequestMapping(value = "/" + ProductCategory.VEHICULO_CATEGORY_URL + "/detalles", method = RequestMethod.POST)
    public Object carFinancingDetailsGetIt(
            ModelMap model, Locale locale, HttpServletRequest request,
            @RequestParam("vehicleId") int vehicleId) throws Exception {
        ProcessContactForm processContactForm = new ProcessContactForm();
        model.addAttribute("processContactForm", processContactForm);
        JSONObject jsonParams = new JSONObject();
        jsonParams.put("vehicleId", vehicleId);
        String externalParams = CryptoUtil.encrypt(jsonParams.toString());
        return AjaxResponse.ok(request.getContextPath() + "/" + ProductCategory.VEHICULO_CATEGORY_URL + "/" + Configuration.EVALUATION_CONTROLLER_URL + "?externalParams=" + externalParams);
    }

    @RequestMapping(value = "/" + ProductCategory.VEHICULO_CATEGORY_URL + "/pickColor", method = RequestMethod.POST)
    public Object changeCostsList(
            ModelMap model, Locale locale,
            @RequestParam(value = "groupId", required = false) Integer groupId,
            @RequestParam(value = "vehicleId", required = false) Integer vehicleId) throws Exception {

        Vehicle vehicle = catalogService.getVehicle(groupId);
        model.addAttribute("vehicle", vehicle);

        if (vehicleId != null) {
            VehicleDetails vehicleDetails = vehicle.getVehicleDetails().stream().filter(j->j.getId().equals(vehicleId)).findFirst().orElse(null);

            if (vehicleDetails != null) {
                vehicleDetails.setImage(vehicleDetails.getImage().stream().filter(
                        a->a.contains(translateColor(vehicleDetails.getColor().toLowerCase())) || a.contains("all")).collect(Collectors.toList())
                );
                model.addAttribute("vehicleDetails", vehicleDetails);
            }
        } else {
            model.addAttribute("vehicleDetails", vehicle.getVehicleDetails().get(0));
        }

        return "products/carFinancing/carDetails :: slider";
    }

    private String translateColor(String color){
        switch(color){
            case "negro" : return "black";
            case "rojo" : return "red";
            case "gris" : return "gray";
            default: return "all";
        }
    }


    @RequestMapping(value = "/{categoryUrl:" + ProductCategory.CONSUMO_CATEGORY_URL + "|" + ProductCategory.TARJETA_CREDITO_CATEGORY_URL+ "|" + ProductCategory.GATEWAY_URL + "|" + ProductCategory.CUENTA_BANCARIA_URL + "|" + ProductCategory.VALIDACION_IDENTIDAD_URL + "|" + ProductCategory.CONSEJ0_URL + "}", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public String showPersonalLoan(ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response,
                                   @PathVariable("categoryUrl") String categoryUrl,
                                   @RequestParam(value = "externalParams", required = false) String externalParams) throws Exception {

        model.addAttribute("productCategoryId", ProductCategory.GET_ID_BY_URL(categoryUrl));
        return loanApplicationController.showPersonalLoan5(model, locale, request, response, categoryUrl, externalParams, ProductCategory.GET_ID_BY_URL(categoryUrl));

//        TraditionalRegisterForm form = new TraditionalRegisterForm();
//        ContactForm contactForm = new ContactForm();
//        CountryParam currentCountry = countryContextService.getCountryParamsByRequest(request);
//        List<New> news = newDao.getNews(currentCountry.getId());
//
//        form.setLoanApplicationAmmount(5000);
//        form.setLoanApplicationInstallemnts(18);
//
//        model.addAttribute("userRegisterForm", form);
//        model.addAttribute("contactForm", contactForm);
//        model.addAttribute("earlyAccessForm", new EarlyAccessForm());
//        model.addAttribute("product", catalogService.getProduct(Product.TRADITIONAL));
//        model.addAttribute("news", news);
//        return "/page-credito-consumo";
    }

    @RequestMapping(value = "/credito-de-consumo-1" , method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public String showPersonalLoan1(ModelMap model, Locale locale, HttpServletRequest request) throws Exception {

        TraditionalRegisterForm form = new TraditionalRegisterForm();
        ContactForm contactForm = new ContactForm();
        CountryParam currentCountry = countryContextService.getCountryParamsByRequest(request);
        List<New> news = newDao.getNews(currentCountry.getId());

        form.setLoanApplicationAmmount(5000);
        form.setLoanApplicationInstallemnts(18);

        model.addAttribute("userRegisterForm", form);
        model.addAttribute("contactForm", contactForm);
        model.addAttribute("earlyAccessForm", new EarlyAccessForm());
        model.addAttribute("product", catalogService.getProduct(Product.TRADITIONAL));
        model.addAttribute("news", news);
        //return "products/personalloan/personalLoan";
        return "/page-credito-consumo-1";
    }

    @RequestMapping(value = "/credito-de-consumo-2" , method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public String showPersonalLoan2(ModelMap model, Locale locale, HttpServletRequest request) throws Exception {

        TraditionalRegisterForm form = new TraditionalRegisterForm();
        ContactForm contactForm = new ContactForm();
        CountryParam currentCountry = countryContextService.getCountryParamsByRequest(request);
        List<New> news = newDao.getNews(currentCountry.getId());

        form.setLoanApplicationAmmount(5000);
        form.setLoanApplicationInstallemnts(18);

        model.addAttribute("userRegisterForm", form);
        model.addAttribute("contactForm", contactForm);
        model.addAttribute("earlyAccessForm", new EarlyAccessForm());
        model.addAttribute("product", catalogService.getProduct(Product.TRADITIONAL));
        model.addAttribute("news", news);
        //return "products/personalloan/personalLoan";
        return "/page-credito-consumo-2";
    }

    @RequestMapping(value = "/credito-de-consumo-3" , method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public String showPersonalLoan3(ModelMap model, Locale locale, HttpServletRequest request) throws Exception {

        TraditionalRegisterForm form = new TraditionalRegisterForm();
        ContactForm contactForm = new ContactForm();
        CountryParam currentCountry = countryContextService.getCountryParamsByRequest(request);
        List<New> news = newDao.getNews(currentCountry.getId());

        form.setLoanApplicationAmmount(5000);
        form.setLoanApplicationInstallemnts(18);

        model.addAttribute("userRegisterForm", form);
        model.addAttribute("contactForm", contactForm);
        model.addAttribute("earlyAccessForm", new EarlyAccessForm());
        model.addAttribute("product", catalogService.getProduct(Product.TRADITIONAL));
        model.addAttribute("news", news);
        //return "products/personalloan/personalLoan";
        return "/page-credito-consumo-3";
    }

    @RequestMapping(value = "/credito-de-consumo-4" , method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public Object showPersonalLoan4(ModelMap model, Locale locale, HttpServletRequest request) throws Exception {
        return new ModelAndView("redirect:/credito-de-consumo");

//        TraditionalRegisterForm form = new TraditionalRegisterForm();
//        ContactForm contactForm = new ContactForm();
//        CountryParam currentCountry = countryContextService.getCountryParamsByRequest(request);
//        List<New> news = newDao.getNews(currentCountry.getId());
//
//        form.setLoanApplicationAmmount(5000);
//        form.setLoanApplicationInstallemnts(18);
//
//        model.addAttribute("userRegisterForm", form);
//        model.addAttribute("contactForm", contactForm);
//        model.addAttribute("earlyAccessForm", new EarlyAccessForm());
//        model.addAttribute("product", catalogService.getProduct(Product.TRADITIONAL));
//        model.addAttribute("news", news);
//        //return "products/personalloan/personalLoan";
//        return "/page-credito-consumo-4";
    }

    @RequestMapping(value = "/stores", method = RequestMethod.GET)
    public String stores(
            ModelMap model, Locale locale, HttpServletRequest request) throws Exception {
        ProcessContactForm processContactForm = new ProcessContactForm();
        model.addAttribute("processContactForm", processContactForm);
        return "/stores";
    }

    // -------------------------------


    // ------------------------------- Static pages.
    @RequestMapping(value = "/nosotros", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public String aboutUsPath(ModelMap model, Locale locale) {
        ContactForm contactForm = new ContactForm();
        model.addAttribute("contactForm", contactForm);
        return "/page-nosotros";
    }

    @RequestMapping(value = "/politica-de-privacidad", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public String showPrivacy(ModelMap model, HttpServletRequest request,Locale locale)  throws Exception {

        if(utilService.isEfectivoAlToqueBrand(request)){
            return "/efectivoaltoque/privacy";
        }else{
            EntityBranding entityBranding=brandingService.getEntityBranding(request);
            String brandedPrivacyPolicy=null;
            if(entityBranding!=null && entityBranding.getBranded() && entityBranding.getPrivacyPolicy()!=null && entityBranding.getPrivacyPolicy().length()!=0){
                brandedPrivacyPolicy=entityBranding.getPrivacyPolicy();
            }
            ContactForm contactForm = new ContactForm();
            ProcessContactForm processContactForm = new ProcessContactForm();
            model.addAttribute("brandedPrivacyPolicy", brandedPrivacyPolicy);
            model.addAttribute("processContactForm", processContactForm);
            model.addAttribute("contactForm", contactForm);
            model.addAttribute("contactPhone", true);
            return "privacy";
        }
    }

    @RequestMapping(value = "/contact", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public Object sendContactEmail(HttpServletRequest req, ContactForm contactForm, Locale locale) throws Exception {

        contactForm.getValidator().validate(locale);
        if (contactForm.getValidator().isHasErrors()) {
            return AjaxResponse.errorFormValidation(contactForm.getValidator().getErrorsJson());
        }
        emailCLService.sendContactMail(contactForm);
        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = "/processContact", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public Object sendProcessContactEmail(HttpServletRequest req, ProcessContactForm processContactForm, Locale locale) throws Exception {

        processContactForm.getValidator().validate(locale);

        if (processContactForm.getValidator().isHasErrors()) {
            return AjaxResponse.errorFormValidation(processContactForm.getValidator().getErrorsJson());
        }

        emailCLService.sendProcessContactMail(processContactForm);

        return AjaxResponse.ok(null);
    }


    @RequestMapping(value = "/terminos-y-condiciones", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public String showTos(ModelMap model, HttpServletRequest request, Locale locale) throws Exception {

        if(utilService.isEfectivoAlToqueBrand(request)){
            return "/efectivoaltoque/tos";
        }else{
            EntityBranding entityBranding=brandingService.getEntityBranding(request);
            String brandedTermsAndConditons=null;
            if(entityBranding!=null && entityBranding.getBranded() && entityBranding.getTermsAndConditions()!=null && entityBranding.getTermsAndConditions().length()!=0){
                brandedTermsAndConditons=entityBranding.getTermsAndConditions();
            }
            ProcessContactForm processContactForm = new ProcessContactForm();
            ContactForm contactForm = new ContactForm();
            model.addAttribute("brandedTermsAndConditons", brandedTermsAndConditons);
            model.addAttribute("processContactForm", processContactForm);
            model.addAttribute("contactForm", contactForm);
            model.addAttribute("contactPhone", true);
            return "tos";
        }
    }

    @RequestMapping(value = "/robots.txt", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public String robots(HttpServletRequest request) {

        CountryParam currentCountry = countryContextService.getCountryParamsByRequest(request);
        if (currentCountry.getId().equals(CountryParam.COUNTRY_ARGENTINA)) {
            return "forward:/static/robots_AR.txt";
        } else if (currentCountry.getId().equals(CountryParam.COUNTRY_PERU)) {
            return "forward:/static/robots.txt";
        }

        return "forward:/static/robots.txt";
    }

    @RequestMapping(value = "/sitemap.xml", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public String sitemap(HttpServletRequest request) {
        CountryParam currentCountry = countryContextService.getCountryParamsByRequest(request);
        if (currentCountry.getId().equals(CountryParam.COUNTRY_ARGENTINA)) {
            return "forward:/static/sitemap_AR.xml";
        } else if (currentCountry.getId().equals(CountryParam.COUNTRY_PERU)) {
            return "forward:/static/sitemap.xml";
        }

        return "forward:/static/sitemap.xml";
    }

    @RequestMapping(value = "/404", method = RequestMethod.GET)
    public String notFoundPage(
            ModelMap model, Locale locale, HttpServletRequest request) throws Exception {
        ProcessContactForm processContactForm = new ProcessContactForm();
        model.addAttribute("processContactForm", processContactForm);
        return "404";
    }

    @RequestMapping(value = "/500", method = RequestMethod.GET)
    public String erorPage(
            ModelMap model, Locale locale, HttpServletRequest request) throws Exception {
        ProcessContactForm processContactForm = new ProcessContactForm();
        model.addAttribute("processContactForm", processContactForm);
        return "500";
    }

    @RequestMapping(value = "/en-mantenimiento", method = RequestMethod.GET)
    public String maintenancePage(
            ModelMap model, Locale locale, HttpServletRequest request) throws Exception {
        return "maintenance";
    }

    @RequestMapping(value = "/advanceContact/{product:adelanto|convenio}", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public Object sendContactEmail(
            HttpServletRequest req, AdvanceContactForm advanceContactForm, Locale locale,
            @PathVariable("product") String product) throws Exception {

        if (!reCaptchaService.contactCheckSuccess(req)) {
            return AjaxResponse.errorMessage("Captcha no válido. Por favor, vuelve a intentarlo.");
        }

        advanceContactForm.getValidator().validate(locale);
        if (advanceContactForm.getValidator().isHasErrors()) {
            return AjaxResponse.errorFormValidation(advanceContactForm.getValidator().getErrorsJson());
        }

        int productId = 0;
        switch (product) {
            case "adelanto":
                productId = Product.SALARY_ADVANCE;
                break;
            case "convenio":
                productId = Product.AGREEMENT;
                break;
        }
        emailCLService.sendAdvanceContactMail(advanceContactForm, productId);
        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = "/early/{productId}", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public Object sendEarlyEmail(
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "loanReasonId", required = false) Integer loanReasonId,
            @PathVariable("productId") Integer productId,
            Locale locale) throws Exception {

        StringFieldValidator validator = new StringFieldValidator(ValidatorUtil.EMAIL, email);
        validator.validate(locale);
        if (validator.isHasErrors()) {
            return AjaxResponse.errorMessage("Por favor, ingresa un email válido.");
        }
        userDao.registerEarlyAccess(IdentityDocumentType.DNI, "", email, loanReasonId, productId);

        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = "/vehicle/registerDiscovery", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object registerDiscovery(
            ModelMap model, Locale locale, ValidateEmailForm form, HttpServletRequest request,
            @RequestParam("groupId") Integer groupId) throws Exception {

        form.getValidator().validate(locale);
        if(form.getValidator().isHasErrors())
            return AjaxResponse.errorMessage("El email es inválido");

        personCLDAO.registerVehicleDiscovery(form.getEmail(), catalogService.getVehicle(groupId).getVehicleDetails().get(0).getId());
        request.getSession().setAttribute("emailVehicleDiscovery", form.getEmail());
        return AjaxResponse.ok("");
    }

    @RequestMapping(value = "/noticias", method = RequestMethod.GET)
    public String news(
            ModelMap model, Locale locale, HttpServletRequest request) throws Exception {

        CountryParam currentCountry = countryContextService.getCountryParamsByRequest(request);

        List<New> news = newDao.getNews();
        news=news.stream().filter(n -> n.getCountry().getId()==currentCountry.getId()).collect(Collectors.toList());

        List<CountryParam> countries = catalogService.getCountryParams();
        List<String> pressMediums = news.stream().map(pm -> pm.getPressMedium()).distinct().collect(Collectors.toList());

        model.addAttribute("news", news);
        model.addAttribute("countries", countries);
        model.addAttribute("pressMediums", pressMediums);
        model.addAttribute("contactForm", new CompanyContactForm());
        return "page-noticias";
    }

    @RequestMapping(value = "/efectivo-con-garantia", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public String showEfectivoConGarantia(ModelMap model, Locale locale, HttpServletRequest request) throws Exception {

//        JSONObject jsonParams = new JSONObject();
//        jsonParams.put("forcedProduct", Product.GUARANTEED);
//        String externalParams = CryptoUtil.encrypt(jsonParams.toString());
//        model.addAttribute("processUrl", request.getContextPath()+"/"+ProductCategory.CONSUMO_CATEGORY_URL+"/"+Configuration.EVALUATION_CONTROLLER_URL+"?externalParams=" + externalParams);


        ShortProcessQuestion1Form form = new ShortProcessQuestion1Form();
        form.setCountryId(countryContextService.getCountryParamsByRequest(request).getId());
        ((ShortProcessQuestion1Form.Validator) form.getValidator()).amount.setMaxValue(productService.getMaxAmount(ProductCategory.CONSUMO, form.getCountryId()));
        ((ShortProcessQuestion1Form.Validator) form.getValidator()).amount.setMinValue(productService.getMinAmount(ProductCategory.CONSUMO, form.getCountryId()));
        ((ShortProcessQuestion1Form.Validator) form.getValidator()).configValidator(form.getCountryId());

        boolean isBranded = brandingService.isBranded(request);
        model.addAttribute("isBranded", isBranded);

        model.addAttribute("clientCountry", form.getCountryId());
        model.addAttribute("userRegisterForm", form);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -75);
        model.addAttribute("yearFrom", calendar.get(Calendar.YEAR));
        model.addAttribute("yearTo", Calendar.getInstance().get(Calendar.YEAR));

        if (!isBranded) {
            model.addAttribute("news", newDao.getNews(form.getCountryId()));
        }

        model.addAttribute("contactForm", new ContactForm());





        return "/page-efectivo-con-garantia";
    }

    @RequestMapping(value = "/saca-tu-credito", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public String remarketingLanding(ModelMap model, Locale locale, HttpServletRequest request) throws Exception {
        List<New> news = newDao.getNews(countryContextService.getCountryParamsByRequest(request).getId());

        CreateRemarketingLoanApplicationForm form = new CreateRemarketingLoanApplicationForm();
        ((CreateRemarketingLoanApplicationForm.Validator)form.getValidator()).configValidator(countryContextService.getCountryParamsByRequest(request).getId());
        model.addAttribute("form", form);
        model.addAttribute("contactForm", new ContactForm());
        model.addAttribute("earlyAccessForm", new EarlyAccessForm());
        model.addAttribute("news", news);
        return "/page-remarketing";
    }

    @RequestMapping(value = "/productos-para-ti", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public String productosParaTiLanding(ModelMap model, Locale locale, HttpServletRequest request) throws Exception {
        ShortProcessQuestion1Form form = new ShortProcessQuestion1Form();
        form.setCountryId(countryContextService.getCountryParamsByRequest(request).getId());

        ((ShortProcessQuestion1Form.Validator)form.getValidator()).reason.setRequired(false);
        ((ShortProcessQuestion1Form.Validator)form.getValidator()).amount.setRequired(false);
        ((ShortProcessQuestion1Form.Validator)form.getValidator()).configValidator(form.getCountryId());

//        if(countryId == CountryParam.COUNTRY_ARGENTINA) {
//            ((ShortProcessQuestion1Form.Validator)form.getValidator()).birthday.setRequired(false);
//            ((ShortProcessQuestion1Form.Validator)form.getValidator()).surname.setRequired(false);
//            ((ShortProcessQuestion1Form.Validator)form.getValidator()).name.setRequired(false);
//        }

        model.addAttribute("form", form);
        model.addAttribute("contactForm", new ContactForm());
        return "/page-products";
    }

    @RequestMapping(value = "/googleca9b0a9f80a77e8a.html", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public String googleSiteVerification(ModelMap model, Locale locale, HttpServletRequest request) throws Exception {
        return "/googleca9b0a9f80a77e8a";
    }

    @RequestMapping(value = "/software", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public String partnerController(ModelMap model, Locale locale, HttpServletRequest request) throws Exception {
        PartnerForm form = new PartnerForm();
        form.setCountryId(countryContextService.getCountryParamsByRequest(request).getId());

        ((PartnerForm.Validator) form.getValidator()).configValidator(form.getCountryId());

        model.addAttribute("form", form);
        model.addAttribute("contactForm", new ContactForm());

        return "/lendingService";
    }
}