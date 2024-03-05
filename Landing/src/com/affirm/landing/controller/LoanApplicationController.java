package com.affirm.landing.controller;

import com.affirm.client.model.form.ContactForm;
import com.affirm.client.model.form.LoanApplicationLoginForm;
import com.affirm.client.model.form.ValidateCellphoneForm;
import com.affirm.client.model.interceptor.DecryptInterceptor;
import com.affirm.client.service.LoanApplicationClService;
import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.NewDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.dao.UserDAO;
import com.affirm.common.model.New;
import com.affirm.common.model.annotation.ErrorControllerAnnotation;
import com.affirm.common.model.annotation.OneTimeTokenAnnotation;
import com.affirm.common.model.catalog.*;
import com.affirm.common.model.form.CreateRemarketingLoanApplicationForm;
import com.affirm.common.model.form.ShortProcessQuestion1Form;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.*;
import com.affirm.common.service.impl.FunnelStepService;
import com.affirm.common.service.question.Question95Service;
import com.affirm.common.service.question.QuestionFlowService;
import com.affirm.common.util.*;
import com.affirm.onesignal.service.OneSignalService;
import com.affirm.system.configuration.Configuration;
import com.google.gson.Gson;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.thymeleaf.spring4.SpringTemplateEngine;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * @author jrodriguez
 */

@Controller
@Scope("request")
public class LoanApplicationController {

    private static final Logger logger = Logger.getLogger(LoanApplicationController.class);

    @Autowired
    private UserDAO userDao;
    @Autowired
    private PersonDAO personDao;
    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private UtilService utilService;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private SpringTemplateEngine templateEngine;
    @Autowired
    private CreditService creditService;
    @Autowired
    private LoanApplicationClService loanApplicationClService;
    @Autowired
    private UserService userService;
    @Autowired
    private CountryContextService countryContextService;
    @Autowired
    private NewDAO newDao;
    @Autowired
    private EvaluationService evaluationService;
    @Autowired
    private LoanApplicationService loanApplicationService;
    @Autowired
    private BrandingService brandingService;
    @Autowired
    private WebscrapperService webscrapperService;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private ProductService productService;
    @Autowired
    private OneSignalService oneSignalService;
    @Autowired
    private BureauService bureauService;
    @Autowired
    private Question95Service question95Service;
    @Autowired
    private FunnelStepService funnelStepService;
    @Autowired
    private CookieManagementService cookieManagementService;

    @RequestMapping(value = "/loanapplicationlogin/{loanApplicationToken}", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public String showLoanApplicationLogin(
            ModelMap model, Locale locale, HttpServletRequest request,
            @PathVariable("loanApplicationToken") String loanApplicationToken) throws Exception {

        String decrypted = CryptoUtil.decrypt(loanApplicationToken);
        JSONObject jsonDecrypted = new JSONObject(decrypted);

        model.addAttribute("loanApplicationLoginForm", new LoanApplicationLoginForm());
        model.addAttribute("validateCellphoneForm", new ValidateCellphoneForm());
        model.addAttribute("loanApplicationToken", loanApplicationToken);
        model.addAttribute("userId", jsonDecrypted.getInt("user"));

        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(jsonDecrypted.getInt("loan"), locale);

        if (loanApplication.getMessengerLink() != null && loanApplication.getMessengerLink()) {
            // This is the facebook link shims solution for the noauth link that the bot send through messenger
            loanApplicationDao.updateMessengerLink(loanApplication.getId(), false);
        } else if (loanApplication.getNoAuthLinkExpiration() != null) {
            // The loanApplication have noAuth link. You shall pass!
            loanApplicationDao.registerNoAuthLinkExpiration(jsonDecrypted.getInt("loan"), null);

            logger.debug("Fecha actual: " + new Date() + " - fecha noauth: " + loanApplication.getNoAuthLinkExpiration());
            logger.debug("valido: " + new Date().before(loanApplication.getNoAuthLinkExpiration()));
            logger.debug("loanToken: " + loanApplicationToken);

            // If the date is valid, let it pass!
            if (new Date().before(loanApplication.getNoAuthLinkExpiration())) {

                // Login LoanApplicationProcess
                loanApplicationClService.loginLoanApplicationProcess(loanApplicationToken);

                // Response
                return "redirect:/loanapplication/" + loanApplicationToken;
            }
        }

        Person person = personDao.getPerson(catalogService, locale, jsonDecrypted.getInt("person"), false);
        model.addAttribute("name", person.getFirstName());

        String userPhone = userDao.getUser(jsonDecrypted.getInt("user")).getPhoneNumber();
        if (userPhone != null) {
            model.addAttribute("userPhone", utilService.maskCellphone(userPhone));
        }

        return "loanApplicationLogin";
    }

    @RequestMapping(value = "/loanapplicationlogin/{loanApplicationToken}", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object loanApplicationLogin(
            ModelMap model, Locale locale, HttpServletRequest request,
            LoanApplicationLoginForm loanApplicationLoginForm,
            @PathVariable("loanApplicationToken") String loanApplicationToken) throws Exception {

        String decrypted = CryptoUtil.decrypt(loanApplicationToken);
        JSONObject jsonDecrypted = new JSONObject(decrypted);

        // Validate the form
        loanApplicationLoginForm.getValidator().validate(locale);
        if (loanApplicationLoginForm.getValidator().isHasErrors()) {
            return AjaxResponse.errorFormValidation(loanApplicationLoginForm.getValidator().getErrorsJson());
        }

        // Validate is the same cellphone number
        User user = userDao.getUser(jsonDecrypted.getInt("user"));
        if (user.getPhoneNumber() == null || !user.getPhoneNumber().equals(loanApplicationLoginForm.getCellPhoneNumber())) {
            return AjaxResponse.errorMessage("El número de teléfono ingresado es incorrecto");
        }

        // Send sms auth token
        userService.sendAuthTokenSms(jsonDecrypted.getInt("user"), jsonDecrypted.getInt("person"), user.getCountryCode(), user.getPhoneNumber(), user.getSimpleName(), jsonDecrypted.getInt("loan"), null, CountryParam.COUNTRY_PERU);

        // Response
        return AjaxResponse.ok(null);
    }

    @OneTimeTokenAnnotation
    @RequestMapping(value = "/{landingUrl:credito-de-consumo-5|recomienda|pago-de-cuota|tarjeta-de-credito-masefectivo}", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public String showPersonalLoan5(ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response,
                                    @PathVariable("landingUrl") String landingUrl,
                                    @RequestParam(value = "externalParams", required = false) String externalParams, Integer productCategoryId) throws Exception {

        if (landingUrl.equals("pago-de-cuota")) {
            productCategoryId = ProductCategory.GATEWAY;
        }
        if (landingUrl.equals("tarjeta-de-credito-masefectivo")) {
            productCategoryId = ProductCategory.TARJETA_CREDITO;
        }

        if (productCategoryId == null)
            productCategoryId = ProductCategory.CONSUMO;

        EntityBranding entity = brandingService.getEntityBranding(request);
        if (null != entity && entity.getEntity().getId().equals(Entity.BANCO_DEL_SOL)) {
            return "redirect:/funcionarios";
        }

        // Redirect to Alfin if its consumo of alfin
//        if(Configuration.hostEnvIsProduction())
//            if(entity != null && entity.getEntity().getId().equals(Entity.AZTECA))
//                if(productCategoryId == ProductCategory.CONSUMO || productCategoryId == ProductCategory.CONSEJ0)
//                    return "redirect:https://www.alfinbanco.pe/";


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
//            model.addAttribute("product", catalogService.getProduct(Product.TRADITIONAL));

            List<New> news = newDao.getNews(form.getCountryId());
            model.addAttribute("news", news);

        }

        model.addAttribute("contactForm", new ContactForm());
        model.addAttribute("externalParams", externalParams);
//        model.addAttribute("earlyAccessForm", new EarlyAccessForm());

        /*
         * AUTHENTICATION FORM
         * It's a auxiliar form for entities that needs to validate email and phoneNumber
         * */

        ShortProcessQuestion1Form authenticationForm = new ShortProcessQuestion1Form();
        authenticationForm.setCountryId(countryContextService.getCountryParamsByRequest(request).getId());
        ((ShortProcessQuestion1Form.Validator) authenticationForm.getValidator()).configValidator(authenticationForm.getCountryId());
        ((ShortProcessQuestion1Form.Validator) authenticationForm.getValidator()).amount.setRequired(false);
        ((ShortProcessQuestion1Form.Validator) authenticationForm.getValidator()).reason.setRequired(false);
        ((ShortProcessQuestion1Form.Validator) authenticationForm.getValidator()).docType.setRequired(false);
        ((ShortProcessQuestion1Form.Validator) authenticationForm.getValidator()).acceptAgreement.setRequired(false);
        ((ShortProcessQuestion1Form.Validator) authenticationForm.getValidator()).acceptAgreement2.setRequired(false);
        ((ShortProcessQuestion1Form.Validator) authenticationForm.getValidator()).conditionsPolicy.setRequired(false);
        model.addAttribute("authenticationForm", authenticationForm);

        if (isBranded)
            if (null != entity && entity.getEntity().getId().equals(Entity.BANBIF)) {

                ((ShortProcessQuestion1Form.Validator) form.getValidator()).amount.setRequired(false);
                ((ShortProcessQuestion1Form.Validator) form.getValidator()).reason.setRequired(false);
                ((ShortProcessQuestion1Form.Validator) form.getValidator()).docType.setRequired(false);
                ((ShortProcessQuestion1Form.Validator) form.getValidator()).acceptAgreement.setRequired(true);
                ((ShortProcessQuestion1Form.Validator) form.getValidator()).acceptAgreement2.setRequired(false);
                model.addAttribute("userRegisterForm", form);

                // AB Testing - If cookie alredy exist, then use it, else create it
                final String cookieBanbifName = "BANBIF_ABTEST";
                String abtestingValue = null;
                if (request.getCookies() != null) {
                    for (int i = 0; i < request.getCookies().length; i++) {
                        Cookie cookie = request.getCookies()[i];
                        if (cookie.getName().equalsIgnoreCase(cookieBanbifName))
                            abtestingValue = cookie.getValue();
                    }
                }
                if (abtestingValue == null) {
                    Random random = new Random();
                    abtestingValue = random.nextInt(2) == 1 ? "A" : "B";
//                    abtestingValue = "B";
                    Cookie bazAbtestCookie = new Cookie(cookieBanbifName, abtestingValue);
                    bazAbtestCookie.setVersion(1);
                    bazAbtestCookie.setHttpOnly(false);
                    bazAbtestCookie.setPath(request.getContextPath().equals("") ? "/" : request.getContextPath());
                    bazAbtestCookie.setMaxAge(600);
                    bazAbtestCookie.setComment(";SameSite=NONE");
                    if (!com.affirm.system.configuration.Configuration.hostEnvIsLocal()) {
                        bazAbtestCookie.setSecure(true);
                    }
                    cookieManagementService.createSetCookieHeader(response, bazAbtestCookie, "None");
//                    response.addCookie(bazAbtestCookie);
                }
                if (landingUrl.equals("tarjeta-de-credito-masefectivo")){
                    ((ShortProcessQuestion1Form.Validator) form.getValidator()).acceptAgreement.setRequired(true);
                    ((ShortProcessQuestion1Form.Validator) form.getValidator()).acceptAgreement2.setRequired(false);
                    model.addAttribute("backgroundUrl", "https://solven-public.s3.amazonaws.com/img/banbif/landing_25/landing_banbif_masefectivo_18_11_23.png");
                    model.addAttribute("userRegisterForm", form);
                    return "/page-credito-consumo-banbif-masefectivo";
                }else if (abtestingValue.equalsIgnoreCase("A")) {
                    model.addAttribute("backgroundUrl", "https://solven-public.s3.amazonaws.com/img/banbif/landing_25/banbif_landing_recortado.jpg");
//                    ((ShortProcessQuestion1Form.Validator) form.getValidator()).amount.setRequired(false);
//                    ((ShortProcessQuestion1Form.Validator) form.getValidator()).phone.setRequired(false);
//                    ((ShortProcessQuestion1Form.Validator) form.getValidator()).email.setRequired(false);
                    model.addAttribute("userRegisterForm", form);
                    return "/page-credito-consumo-banbif-champions";
                } else {
                    model.addAttribute("backgroundUrl", "https://solven-public.s3.amazonaws.com/img/banbif/landing_25/banbif_landing_recortado.jpg");
                    model.addAttribute("userRegisterForm", form);
                    return "/page-credito-consumo-banbif";
                }
            } else if (null != entity && entity.getEntity().getId().equals(Entity.AZTECA)) {
                if (productCategoryId == ProductCategory.GATEWAY) {
                    ((ShortProcessQuestion1Form.Validator) form.getValidator()).amount.setRequired(false);
                    ((ShortProcessQuestion1Form.Validator) form.getValidator()).phone.setRequired(false);
                    ((ShortProcessQuestion1Form.Validator) form.getValidator()).email.setRequired(false);
                    ((ShortProcessQuestion1Form.Validator) form.getValidator()).acceptAgreement.setRequired(true);
                    ((ShortProcessQuestion1Form.Validator) form.getValidator()).conditionsPolicy.setRequired(true);
                    ((ShortProcessQuestion1Form.Validator) form.getValidator()).conditionsPolicy.setRequiredErrorMsg("Necesitamos que aceptes las condiciones.");
                    model.addAttribute("userRegisterForm", form);
                    if (landingUrl.equals("pago-de-cuota")) {
                        return "/page-pago-cuota-azteca";
                    }
                    return "/page-pagos-azteca";
                }
                if (productCategoryId == ProductCategory.CONSEJ0) {
                    ((ShortProcessQuestion1Form.Validator) form.getValidator()).amount.setRequired(false);
                    ((ShortProcessQuestion1Form.Validator) form.getValidator()).phone.setRequired(false);
                    ((ShortProcessQuestion1Form.Validator) form.getValidator()).email.setRequired(false);
                    ((ShortProcessQuestion1Form.Validator) form.getValidator()).acceptAgreement.setRequired(true);
                    ((ShortProcessQuestion1Form.Validator) form.getValidator()).conditionsPolicy.setRequired(false);
                    ((ShortProcessQuestion1Form.Validator) form.getValidator()).conditionsPolicy.setRequiredErrorMsg("Necesitamos que aceptes las condiciones.");
                    model.addAttribute("userRegisterForm", form);
                    return "/page-consejero-azteca";
                }
                if (productCategoryId == ProductCategory.CUENTA_BANCARIA) {
                    ((ShortProcessQuestion1Form.Validator) form.getValidator()).amount.setRequired(false);
                    ((ShortProcessQuestion1Form.Validator) form.getValidator()).reason.setRequired(false);
                    ((ShortProcessQuestion1Form.Validator) form.getValidator()).phone.setRequired(false);
                    ((ShortProcessQuestion1Form.Validator) form.getValidator()).email.setRequired(false);
                    model.addAttribute("userRegisterForm", form);
                    return "/page-cuenta-ahorro";
                }
                if (productCategoryId == ProductCategory.VALIDACION_IDENTIDAD) {
                    ((ShortProcessQuestion1Form.Validator) form.getValidator()).amount.setRequired(false);
                    ((ShortProcessQuestion1Form.Validator) form.getValidator()).reason.setRequired(false);
                    ((ShortProcessQuestion1Form.Validator) form.getValidator()).phone.setRequired(true);
                    ((ShortProcessQuestion1Form.Validator) form.getValidator()).email.setRequired(true);
                    ((ShortProcessQuestion1Form.Validator) form.getValidator()).acceptAgreement.setRequired(true);
                    ((ShortProcessQuestion1Form.Validator) form.getValidator()).conditionsPolicy.setRequired(true);
                    model.addAttribute("userRegisterForm", form);
                    return "/page-azteca-identidad";
                }

                ((ShortProcessQuestion1Form.Validator) form.getValidator()).reason.setRequired(false);

                // AB Testing - If cookie alredy exist, then use it, else create it
                final String cookieName = "BAZ_ABTEST";
                String abtestingValue = null;
                if (request.getCookies() != null) {
                    for (int i = 0; i < request.getCookies().length; i++) {
                        Cookie cookie = request.getCookies()[i];
                        if (cookie.getName().equalsIgnoreCase(cookieName))
                            abtestingValue = cookie.getValue();
                    }
                }
                if (abtestingValue == null) {
                    //probability 100% : 'A' (select product)
                    //probability 0% : 'B' (go to offline)
//                    Random random = new Random();
//                    int randon = random.nextInt(4);
//                    switch (randon){
//                        case 0: abtestingValue = "A";
//                            break;
//                        case 1: abtestingValue = "B";
//                            break;
//                        case 2: abtestingValue = "B";
//                            break;
//                        case 3: abtestingValue = "B";
//                            break;
//                    }
                    abtestingValue = "A";
                    //   System.out.println(abtestingValue);
//                    abtestingValue = random.nextInt(2) == 1 ? "A" : "B";
//                    abtestingValue = "A";
                    Cookie bazAbtestCookie = new Cookie(cookieName, abtestingValue);
                    bazAbtestCookie.setVersion(1);
                    bazAbtestCookie.setHttpOnly(false);
                    bazAbtestCookie.setPath(request.getContextPath().equals("") ? "/" : request.getContextPath());
                    bazAbtestCookie.setMaxAge(600);
                    if (!com.affirm.system.configuration.Configuration.hostEnvIsLocal()) {
                        bazAbtestCookie.setSecure(true);
                    }
                    cookieManagementService.createSetCookieHeader(response, bazAbtestCookie, "None");
//                    response.addCookie(bazAbtestCookie);
                }

               /* if(abtestingValue.equalsIgnoreCase("B")){
                    model.addAttribute("userRegisterForm", form);
                    return "/page-credito-consumo-branded";
                }else{*/
                ((ShortProcessQuestion1Form.Validator) form.getValidator()).amount.setRequired(false);
                ((ShortProcessQuestion1Form.Validator) form.getValidator()).phone.setRequired(false);
                ((ShortProcessQuestion1Form.Validator) form.getValidator()).email.setRequired(false);
                model.addAttribute("userRegisterForm", form);
                model.addAttribute("abtestingValue", abtestingValue);
                return "/page-credito-consumo-azteca-challenger";
                /* }*/
            } else {
                if (null != entity && entity.getEntity().getId().equals(Entity.FUNDACION_DE_LA_MUJER))
                    return "/page-credito-consumo-branded-fdlm";
                if (null != entity && entity.getEntity().getId().equals(Entity.ACCESO) && productCategoryId == ProductCategory.CONSUMO)
                    return "/page-credito-consumo-acceso";
                return "/page-credito-consumo-branded";
            }
        else
            return "/page-credito-consumo-5";
    }

    @RequestMapping(value = "/credito-de-consumo-5/synthesized", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object registerPersonalLoan5(ModelMap model, Locale locale, HttpServletRequest request,
                                        @RequestParam(value = "documentNumber", required = false) String documentNumber) throws Exception {

        DecryptInterceptor.decryptRequest(request, null);

        documentNumber = request.getParameter("documentNumber");

        // IF Peru, start creating the synthesized so doesnt take too long
        if (documentNumber != null && countryContextService.getCountryParamsByRequest(request).getId() == CountryParam.COUNTRY_PERU) {
            //if(documentType)
            if ((documentNumber.length() == 8 || documentNumber.length() == 9) && documentNumber.matches("^[0-9]+$")) {
//                webscrapperService.callRunSynthesized(documentNumber, null);
            }
        }
        return AjaxResponse.ok(null);
    }

    @OneTimeTokenAnnotation
    @RequestMapping(value = "/{productType:credito-de-consumo-5|efectivo-con-garantia|tarjeta-de-credito|tarjeta-de-credito-masefectivo|productos-para-ti|pagos|cuenta|consejero|indetidad}", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object getPersonalLoan5(ModelMap model, Locale locale, HttpServletRequest request, QuestionFlowService.Type flowType,

                                   @PathVariable("productType") String productType) throws Exception {

        String decryptedBody = DecryptInterceptor.decryptRequest(request, null);
        JSONObject requestJson = new JSONObject(decryptedBody);
        ShortProcessQuestion1Form form = new Gson().fromJson(decryptedBody, ShortProcessQuestion1Form.class);

        String externalParams = form.getExternalParams();
        String source = form.getSource();
        String medium = form.getMedium();
        String campaign = form.getCampaign();
        String marketingCampaign = requestJson.optString("marketing_campaign");
        String term = form.getTerm();
        String content = form.getContent();
        String gclid = form.getGclid();
        String gaClientID = form.getGaClientID();

/*      ShortProcessQuestion1Form form,
        @RequestParam(value = "token", required = false) String token,
        @RequestParam(value = "externalParams", required = false) String externalParams,
        @RequestParam(value = "agent", required = false) Integer agentId,
        @RequestParam(value = "source", required = false) String source,
        @RequestParam(value = "medium", required = false) String medium,
        @RequestParam(value = "campaign", required = false) String campaign,
        @RequestParam(value = "marketing_campaign", required = false) String marketingCampaign,
        @RequestParam(value = "term", required = false) String term,
        @RequestParam(value = "content", required = false) String content,
        @RequestParam(value = "gclid", required = false) String gclid,
        @RequestParam(value = "gaClientID", required = false) String gaClientID*/


        String cookieName = "BAZ_ABTEST";
        int productCategory = ProductCategory.CONSUMO;
        Integer marketingCampaignId = null;
        if ("tarjeta-de-credito".equalsIgnoreCase(productType) || "tarjeta-de-credito-masefectivo".equalsIgnoreCase(productType)) {
            productCategory = ProductCategory.TARJETA_CREDITO;
            cookieName = "BANBIF_ABTEST";
        }
        if ("pagos".equalsIgnoreCase(productType)) {
            productCategory = ProductCategory.GATEWAY;
            // TODO Que es necesario para landing cobranza
        }
        if ("cuenta".equalsIgnoreCase(productType)) {
            productCategory = ProductCategory.CUENTA_BANCARIA;
        }
        if ("indetidad".equalsIgnoreCase(productType)) {
            productCategory = ProductCategory.VALIDACION_IDENTIDAD;
        }
        if ("consejero".equalsIgnoreCase(productType)) {
            productCategory = ProductCategory.CONSEJ0;
        }

        if ("productos-para-ti".equalsIgnoreCase(productType)) {
            form.setReason(catalogService.getLoanApplicationReasonsVisible(locale).get(0).getId());
            form.setAmount(productService.getMaxAmount(ProductCategory.CONSUMO, countryContextService.getCountryParamsByRequest(request).getId()));
            form.setAcceptAgreement(true);
        }


        String abtestingValue = null;
        if (request.getCookies() != null) {
            for (int i = 0; i < request.getCookies().length; i++) {
                Cookie cookie = request.getCookies()[i];
                if (cookie.getName().equalsIgnoreCase(cookieName))
                    abtestingValue = cookie.getValue();
            }
        }
        if (abtestingValue == null) abtestingValue = "A";

        List<Agent> agentsList;
        boolean isBranded = brandingService.isBranded(request);

        if (isBranded) {
            Entity entity = brandingService.getEntityBranding(request).getEntity();
            agentsList = catalogService.getFormAssistantsAgents(entity.getId());
        } else {
            agentsList = catalogService.getFormAssistantsAgents(null);
        }
        int randomIndexWithinRange = (int) Math.floor(Math.random() * agentsList.size());

        form.setAgentId(agentsList.get(randomIndexWithinRange).getId());
        form.setExternalParams(externalParams);
        //form.setCategoryUrl(categoryUrl);
        form.setCountryId(countryContextService.getCountryParamsByRequest(request).getId());
        form.setRequest(request);
        form.setSource(source);
        form.setMedium(medium);
        form.setCampaign(campaign);
        form.setTerm(term);
        form.setContent(content);
        form.setGclid(gclid);
        form.setGaClientID(gaClientID);
        form.setABTesting(abtestingValue);
        Object answer = isValidForm(locale, form, request, productCategory);

        if (answer != null) {
            return answer;
        }
        //register new Loan Application
        if (form.getCountryId().equals(CountryParam.COUNTRY_ARGENTINA)) {
            String fullName = personDao.getPadronAfipFullName(form.getDocumentNumber());
            String[] nameSplitted = fullName != null ? fullName.split(" ") : null;

            form.setName(nameSplitted != null ? nameSplitted[nameSplitted.length - 1] : null);
            form.setSurname(nameSplitted != null ? nameSplitted[0] : null);
        }

        EntityBranding brandingEntity = brandingService.getEntityBranding(request);
        User user = userService.getOrRegisterUser(form.getDocType(), form.getDocumentNumber(), null, form.getName(), form.getSurname());
        Person person = personDao.getPerson(catalogService, Configuration.getDefaultLocale(), user.getPersonId(), false);

        // Register the pre loan application
        PreLoanApplication preLoanApplication = loanApplicationDao.insertPreLoanApplication(person.getDocumentType().getId(), person.getDocumentNumber(), brandingEntity != null ? brandingEntity.getEntity().getId() : null, productCategory);

        //VALIDAR SI SE REQUIERE AUTENTICACION SEGUN ENTIDAD - SI YA TIENE CELULAR/EMAIL
        if(brandingEntity != null && brandingEntity.getEntity().getId() != Entity.AZTECA){
            if (brandingEntity.getLandingConfiguration() != null && brandingEntity.getLandingConfiguration().getAuthenticationRequiredByCategory() != null && !brandingEntity.getLandingConfiguration().getAuthenticationRequiredByCategory().isEmpty()) {
                if (user.getEmail() != null && user.getPhoneNumber() != null) {
                    boolean isAuthenticationRequired = brandingEntity.getLandingConfiguration().getAuthenticationRequiredByCategory().contains(productCategory);
                    if (isAuthenticationRequired && (form.getPhone() == null || form.getEmail() == null))
                        return AjaxResponse.errorMessage("AUTHENTICATION_REQUIRED");
                }
            }
        }

        if (form.getCountryId().equals(CountryParam.COUNTRY_ARGENTINA)) {
            personDao.updateBirthday(person.getId(), utilService.parseDate(form.getBirthday(), "dd/MM/yyyy", locale));
            switch (form.getDocumentNumber().substring(0, 2)) {
                case "20":
                case "23":
                    personDao.updateGender(person.getId(), 'M');
                    break;
                case "27":
                    personDao.updateGender(person.getId(), 'F');
                    break;
            }
        }

        boolean shouldSaveEmailPhoneInAuxData = false;
        if (brandingEntity != null && brandingEntity.getEntity().getId() == Entity.AZTECA && productCategory == ProductCategory.VALIDACION_IDENTIDAD) {
            shouldSaveEmailPhoneInAuxData = true;
        }

        // Insert the email or respond that the email is invalid
        if (!shouldSaveEmailPhoneInAuxData) {
            if (form.getEmail() != null) {
                userService.createEmailPassword(form.getEmail(), user.getId());
            }
        }

        LoanApplication loanApplication = null;
        if (brandingEntity != null) {
            loanApplication = loanApplicationDao.getActiveLoanApplicationByPerson(locale, user.getPersonId(), productCategory, brandingEntity.getEntity().getId());
        } else {
            loanApplication = loanApplicationDao.getActiveLoanApplicationByPerson(locale, user.getPersonId(), productCategory);
        }


        if (loanApplication != null) {
            // Ift its an loan from IDNETITY VALIDATION, expire it and crete a new one
            if (productCategory == ProductCategory.VALIDACION_IDENTIDAD) {
                loanApplicationDao.expireLoanApplication(loanApplication.getId());
            } else {
                // IF its an loan from Azteca - Bpeople, it should not be accessed from this landing
                if (loanApplication.getOrigin() == LoanApplication.ORIGIN_API_REST && loanApplication.getEntityId() != null && loanApplication.getEntityId() == Entity.AZTECA) {
                    return AjaxResponse.errorMessageWithLink("Inicia sesión en el home banking", "https://tubancaenlinea.alfinbanco.pe/Alfin/login/");
                }

                String phoneNUmber = form.getCode() != null ? "(" + form.getCode() + ") " + form.getPhone() : form.getPhone();
                if (phoneNUmber != null)
                    if (!shouldSaveEmailPhoneInAuxData)
                        userDao.registerPhoneNumber(loanApplication.getUserId(), loanApplication.getCountry().getId() + "", phoneNUmber);

                //VALIDAR SI SE REQUIERE AUTENTICACION SEGUN ENTIDAD
                if (brandingEntity != null && brandingEntity.getLandingConfiguration() != null && brandingEntity.getLandingConfiguration().getAuthenticationRequiredByCategory() != null && !brandingEntity.getLandingConfiguration().getAuthenticationRequiredByCategory().isEmpty()) {
                    boolean isAuthenticationRequired = brandingEntity.getLandingConfiguration().getAuthenticationRequiredByCategory().contains(productCategory);
                    if (isAuthenticationRequired && (form.getPhone() == null || form.getEmail() == null)){
                        if (user.getEmail() != null && user.getPhoneNumber() != null) {
                            return AjaxResponse.errorMessage("AUTHENTICATION_REQUIRED");
                        }
                    }
                }
                return AjaxResponse.redirect(loanApplicationService.generateLoanApplicationLinkEntity(loanApplication));
            }
        }

        //        REGISTER PHONENUMBER OR RESPOND THAT PHONENUMBER IS INVALID
        String phoneNUmber = form.getCode() != null ? "(" + form.getCode() + ") " + form.getPhone() : form.getPhone();
        if (phoneNUmber != null)
            if (!shouldSaveEmailPhoneInAuxData)
                userDao.registerPhoneNumber(user.getId(), form.getCountryId() + "", phoneNUmber);

        Integer installemnts = Optional.ofNullable(form.getTimeLimit()).orElse(Configuration.DEFAULT_INSTALLMENTS);
        if (brandingEntity != null && Arrays.asList(Entity.FINANSOL, Entity.PRISMA).contains(brandingEntity.getEntity().getId()))
            installemnts = 12;
        //Save values in loanApplication Database
        loanApplication = loanApplicationDao.registerLoanApplication(
                user.getId(),
                form.getAmount(),
                installemnts,
                form.getReason(),
                null,
                null,
                null,
                "productos-para-ti".equalsIgnoreCase(productType) ? LoanApplication.ORIGIN_LANDING_LEADS : LoanApplication.ORIGIN_ALTERNATIVE_LANDING,
                null,
                null,
                null,
                form.getCountryId());
        if (productType.equals("efectivo-con-garantia")) {
            loanApplicationDao.updateProductId(loanApplication.getId(), Product.GUARANTEED);
        }

        if (marketingCampaign != null && !marketingCampaign.isEmpty()) {
            try {
                marketingCampaignId = Integer.valueOf(marketingCampaign);
            } catch (Exception e) {
            }
        }

        userService.registerIpUbication(Util.getClientIpAddres(form.getRequest()), loanApplication.getId());

        loanApplicationDao.updateProductCategory(loanApplication.getId(), productCategory);
        loanApplicationDao.updateFormAssistant(loanApplication.getId(), form.getAgentId());
        loanApplicationDao.updateSourceMediumCampaign(loanApplication.getId(), form.getSource(), form.getMedium(), form.getCampaign());
        loanApplicationDao.updateTermContent(loanApplication.getId(), form.getTerm(), form.getContent());
        loanApplicationDao.updateGoogleClickId(loanApplication.getId(), form.getGclid());
        loanApplicationDao.updateGAClientID(loanApplication.getId(), form.getGaClientID());
        loanApplicationDao.updateUserAgent(loanApplication.getId(), form.getRequest().getHeader("User-Agent"));
        if (loanApplication.getAuxData() == null) loanApplication.setAuxData(new LoanApplicationAuxData());
        loanApplication.getAuxData().setAcceptAgreement(form.getAcceptAgreement());
        loanApplication.getAuxData().setAcceptAgreement2(form.getAcceptAgreement2());
        loanApplication.getAuxData().setAbTestingValue(abtestingValue != null ? abtestingValue.charAt(0) : null);
        loanApplication.getAuxData().setMarketingCampaignId(marketingCampaignId);
        loanApplication.getAuxData().setConditionsPolicy(form.getConditionsPolicy());
        if (shouldSaveEmailPhoneInAuxData) {
            if (form.getEmail() != null) {
                int emailId = userDao.insertEmail(user.getId(), form.getEmail(), false, false);
                loanApplication.getAuxData().setRegisteredEmail(form.getEmail());
                loanApplication.getAuxData().setRegisteredEmailId(emailId);
            }
            if (form.getPhone() != null) {
                Random rand = new Random();
                String smsToken = String.format("%04d%n", rand.nextInt(10000));
                int phoneNumberId = userDao.insertPhoneNumber(user.getId(), form.getCountryId() + "", phoneNUmber, smsToken, false, false);
                loanApplication.getAuxData().setRegisteredPhoneNumber(phoneNUmber);
                loanApplication.getAuxData().setRegisteredPhoneNumberId(phoneNumberId);
            }
        }
        loanApplicationDao.updateAuxData(loanApplication.getId(), loanApplication.getAuxData());

        // Update the expiration Date
        if (brandingEntity != null && brandingEntity.getEntity().getId() == Entity.AZTECA) {
            Date newExpirationDate = loanApplicationService.generateExpirationDateByLoan(brandingEntity.getEntity().getId(), productCategory);
            if(newExpirationDate != null) loanApplicationDao.updateExpirationDate(loanApplication.getId(), newExpirationDate);
        }


        if (brandingEntity != null && brandingEntity.getEntity().getId() == Entity.AZTECA && productCategory == ProductCategory.CONSEJ0) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.HOUR, 24);
            loanApplicationDao.updateExpirationDate(loanApplication.getId(), cal.getTime());
        }

        if (brandingEntity != null && Entity.BANBIF == brandingEntity.getEntity().getId()) {
            if (loanApplication.getEntityCustomData() == null) {
                loanApplication.setEntityCustomData(new JSONObject());
            }
            loanApplication.getEntityCustomData().put(LoanApplication.EntityCustomDataKeys.BANBIF_LANDING_AB_TESTING.getKey(), form.getABTesting());
            loanApplication.getEntityCustomData().put(LoanApplication.EntityCustomDataKeys.BANBIF_TC_MASEFECTIVO_LANDING.getKey(), "tarjeta-de-credito-masefectivo".equalsIgnoreCase(productType));

            loanApplicationDao.updateEntityCustomData(loanApplication.getId(), loanApplication.getEntityCustomData());
        } else if (brandingEntity != null && Entity.AZTECA == brandingEntity.getEntity().getId()) {
            if (loanApplication.getEntityCustomData() == null) {
                loanApplication.setEntityCustomData(new JSONObject());
            }

            loanApplicationDao.updateEntityCustomData(loanApplication.getId(),
                    loanApplication.getEntityCustomData().put(LoanApplication.EntityCustomDataKeys.BANCO_AZTECA_LANDING_AB_TESTING.getKey(), abtestingValue));
        }

        if (form.getSource() != null) {
            if (form.getSource().equalsIgnoreCase(LoanApplication.LEAD_SALESDOUBLER) && request.getCookies() != null) {
                String salesDoublerAffSub = null;
                for (int i = 0; i < request.getCookies().length; i++) {
                    Cookie cookie = request.getCookies()[i];
                    if (cookie.getName().equalsIgnoreCase("aff_sub"))
                        salesDoublerAffSub = cookie.getValue();
                }
                JSONObject jsonLeadValues = new JSONObject();
                jsonLeadValues.put("aff_sub", salesDoublerAffSub);
                loanApplicationDao.updateLeadParams(loanApplication.getId(), jsonLeadValues);
            } else if (form.getSource().equalsIgnoreCase(LoanApplication.LEAD_LEADGID) && request.getCookies() != null) {
                String leadgidClickId = null;
                for (int i = 0; i < request.getCookies().length; i++) {
                    Cookie cookie = request.getCookies()[i];
                    if (cookie.getName().equalsIgnoreCase("click_id"))
                        leadgidClickId = cookie.getValue();
                }
                JSONObject jsonLeadValues = new JSONObject();
                jsonLeadValues.put("click_id", leadgidClickId);
                loanApplicationDao.updateLeadParams(loanApplication.getId(), jsonLeadValues);
            } else if (form.getSource().equalsIgnoreCase(LoanApplication.LEAD_TORO) && request.getCookies() != null) {
                String leadgidClickId = null;
                String affiliateId = null;
                for (int i = 0; i < request.getCookies().length; i++) {
                    Cookie cookie = request.getCookies()[i];
                    if (cookie.getName().equalsIgnoreCase("toro_sid"))
                        leadgidClickId = cookie.getValue();
                    else if (cookie.getName().equalsIgnoreCase("toro_affiliate_id"))
                        affiliateId = cookie.getValue();
                }
                JSONObject jsonLeadValues = new JSONObject();
                jsonLeadValues.put("toro_sid", leadgidClickId);
                jsonLeadValues.put("toro_affiliate_id", affiliateId);
                loanApplicationDao.updateLeadParams(loanApplication.getId(), jsonLeadValues);
            }
        }

        loanApplication = loanApplicationDao.getLoanApplication(loanApplication.getId(), locale);

        if (brandingEntity != null && Entity.AZTECA == brandingEntity.getEntity().getId() && productCategory == ProductCategory.GATEWAY) {
            loanApplicationDao.updateLoanApplicationCode(loanApplication.getId(), loanApplication.getCode().replace("LA", "CA"));
        }

        if (productCategory == ProductCategory.CUENTA_BANCARIA) {
            loanApplicationDao.updateLoanApplicationCode(loanApplication.getId(), loanApplication.getCode().replace("LA", "AA"));
        }

        EntityBranding branding = brandingService.getEntityBranding(request);

        if (branding != null && branding.getLandingConfiguration() != null && branding.getLandingConfiguration().getQuestionToGo() != null)
            loanApplicationDao.updateCurrentQuestion(loanApplication.getId(), branding.getLandingConfiguration().getQuestionToGo());
        else if (productType.equals("efectivo-con-garantia"))
            loanApplicationDao.updateCurrentQuestion(loanApplication.getId(), ProcessQuestion.Question.Constants.GUARANTEED_BEFORE_PRE_EVALUATION);
        else {
            if (person != null && person.getFirstName() != null && !person.getFirstName().isEmpty() && person.getBirthday() != null)
                loanApplicationDao.updateCurrentQuestion(loanApplication.getId(), ProcessQuestion.Question.Constants.RUNNING_PREEVALUATION);
            else
                loanApplicationDao.updateCurrentQuestion(loanApplication.getId(), ProcessQuestion.Question.Constants.NO_RENIEC_BEFORE_PRE_EVALUATION);
        }

        if (branding != null)
            loanApplicationDao.updateEntityId(loanApplication.getId(), branding.getEntity().getId());

        loanApplication = loanApplicationDao.getLoanApplication(loanApplication.getId(), locale);
        loanApplicationDao.updatePerson(loanApplication.getId(), user.getPersonId(), user.getId());
        if (loanApplication.getCurrentProcessQuestion() != null && loanApplication.getCurrentProcessQuestion().getId() == ProcessQuestion.Question.Constants.RUNNING_PREEVALUATION) {
            Boolean runEvaluation = false;
            if (loanApplication.getEntityId() != null && loanApplication.getEntityId() == Entity.AZTECA && loanApplication.getProductCategoryId() == ProductCategory.GATEWAY)
                runEvaluation = true;
            question95Service.runPreEvaluationBotIfNoRunYet(loanApplication.getId(), runEvaluation);
        }

        // Apply external params if exists
        if (externalParams != null && !externalParams.trim().isEmpty()) {
            JSONObject jsonExternalParams = new JSONObject(CryptoUtil.decrypt(externalParams));
            if (JsonUtil.getIntFromJson(jsonExternalParams, "forcedEntity", null) != null &&
                    JsonUtil.getIntFromJson(jsonExternalParams, "forcedProduct", null) != null) {
                loanApplicationDao.updateEntityId(loanApplication.getId(), JsonUtil.getIntFromJson(jsonExternalParams, "forcedEntity", null));
                loanApplicationDao.updateProductId(loanApplication.getId(), JsonUtil.getIntFromJson(jsonExternalParams, "forcedProduct", null));
            } else if (JsonUtil.getIntFromJson(jsonExternalParams, "forcedProduct", null) != null) {
                loanApplicationDao.updateProductId(loanApplication.getId(), JsonUtil.getIntFromJson(jsonExternalParams, "forcedProduct", null));
            }
            if (JsonUtil.getIntFromJson(jsonExternalParams, "vehicleId", null) != null) {
                loanApplicationService.updateVehicle(loanApplication.getId(), JsonUtil.getIntFromJson(jsonExternalParams, "vehicleId", null));
            }
        }
        if ("productos-para-ti".equalsIgnoreCase(productType)) {
            loanApplicationDao.updateProductId(loanApplication.getId(), Product.LEADS_CONSUMO);
        }

        // Only if Argentina, start running the evaluation, so the bots doesnt take too long
        if (form.getCountryId() == CountryParam.COUNTRY_ARGENTINA) {
            bureauService.callNosisUpdateExternalData(form.getDocumentNumber());
            loanApplicationService.runEvaluationBot(loanApplication.getId(), false);
        }

        loanApplicationService.registerReferrerIfExists(loanApplication.getId(), form.getReferrerPersonId());

        funnelStepService.registerStep(loanApplication);

        if (marketingCampaignId != null) {
            loanApplicationService.updateMarketingCampaignByLoanApplication(loanApplication, marketingCampaignId, "registered");
        }

        return AjaxResponse.redirect(loanApplicationService.generateLoanApplicationLinkEntity(loanApplication));
    }

    @RequestMapping(value = "/credito-de-consumo-6", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public String showPersonalLoan6(ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String toReturn = showPersonalLoan5(model, locale, request, response, null, null, null);
        model.addAttribute("brandingBackgroundImage", "https://s3.amazonaws.com/solven-public/img/cc9.jpg");
        return toReturn;
    }

    @RequestMapping(value = "/saca-tu-credito", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object saveRemarketingLoan(ModelMap model, Locale locale, HttpServletRequest request,
                                      CreateRemarketingLoanApplicationForm form) throws Exception {

        form.setCountryId(countryContextService.getCountryParamsByRequest(request).getId());
        if (form.getCountryId() == CountryParam.COUNTRY_ARGENTINA) {
            form.setDocumentType(IdentityDocumentType.CDI);
        }
        ((CreateRemarketingLoanApplicationForm.Validator) form.getValidator()).configValidator(countryContextService.getCountryParamsByRequest(request).getId());

        form.getValidator().validate(locale);
        if (form.getValidator().isHasErrors()) {
            if (ArrayUtils.contains(new int[]{IdentityDocumentType.CDI, IdentityDocumentType.CUIL, IdentityDocumentType.CUIT}, form.getDocumentType())
                    && form.getDocumentNumber() != null
                    && (form.getDocumentNumber().startsWith("30") || form.getDocumentNumber().startsWith("33") || form.getDocumentNumber().startsWith("34"))) {
                ((CreateRemarketingLoanApplicationForm.Validator) form.getValidator()).documentNumber.setError("El CUIT ingresado corresponde a una empresa. Por el momento solo operamos con personas físicas. Gracias por visitarnos.");
                return AjaxResponse.errorFormValidation(form.getValidator().getErrorsJson());
            }

            if (ArrayUtils.contains(new int[]{IdentityDocumentType.CDI, IdentityDocumentType.CUIL, IdentityDocumentType.CUIT}, form.getDocumentType())) {
                if (form.getDocumentNumber() != null) {
                    if (!utilService.validarCuit(form.getDocumentNumber())) {
                        ((CreateRemarketingLoanApplicationForm.Validator) form.getValidator()).documentNumber.setError(messageSource.getMessage("static.message.cuit", null, locale));
                        return AjaxResponse.errorFormValidation(form.getValidator().getErrorsJson());
                    }
                }
            }

            // Validate that the document type is valid
            if (getValidIdentityDocumentTypes(request).stream().noneMatch(d -> d.getId().equals(form.getDocumentType()))) {
                return AjaxResponse.errorMessage("El tipo de documento es inválido");
            }

            return AjaxResponse.errorFormValidation(form.getValidator().getErrorsJson());
        }

        // Get the user
        User user = userDao.getUserByDocument(form.getDocumentType(), form.getDocumentNumber());
        if (user == null) {
            return AjaxResponse.errorMessage(messageSource.getMessage("static.rmk.startloan", null, locale));
        }

        // Insert the email or respond that the email is invalid
        userService.createEmailPassword(form.getEmail(), user.getId());

        LoanApplication loanApplication = loanApplicationDao.getActiveLoanApplicationByPerson(locale, user.getPersonId(), ProductCategory.CONSUMO);
        if (loanApplication == null) {
            return AjaxResponse.errorMessage(messageSource.getMessage("static.rmk.startloan", null, locale));
        }

        return AjaxResponse.redirect(loanApplicationService.generateLoanApplicationLinkEntity(loanApplication));
    }

    @RequestMapping(value = "/email-verification/{token}", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public String userEmailVerification(@PathVariable("token") String token,
                                        Locale locale,
                                        HttpServletRequest request,
                                        HttpServletResponse response) {
        try {
            String jsonString = CryptoUtil.decrypt(token);
            JSONObject json = new JSONObject(jsonString);

            int emailId = json.has("emailId") ? json.getInt("emailId") : 0;
            int userId = json.getInt("userId");
            long timeout = json.getLong("timeout");

            if (emailId != 0 && new Date(timeout).compareTo(new Date()) <= 0) {// COMPARE TIMEOUT VS TODAY
                return "404"; // TODO FAILURE HTML
            }

            userDao.validateEmailChange(userId, emailId);
            userDao.verifyEmail(userId, emailId, true);

            int loanId = json.has("loanId") ? json.getInt("loanId") : 0;
            if (loanId != 0) {
                LoanApplication loanApplication = loanApplicationService.getLoanApplicationById(loanId);
                if (loanApplication != null && loanApplication.getCreditId() == null) {
                    EntityProductParams entityProductParams = catalogService.getEntityProductParamById(loanApplication.getSelectedEntityProductParameterId());
                    if (entityProductParams.getAutomaticApproval() && entityProductParams.getRequireEmailValidationForApproval()) {

                        try {
                            loanApplicationService.approveLoanApplication(loanApplication.getId(), null, request, response, templateEngine, locale);

                        } finally {
                            String processToken = loanApplicationService.generateLoanApplicationToken(
                                    loanApplication.getUserId(),
                                    loanApplication.getPersonId(),
                                    loanApplication.getId());

                            return "redirect:" + request.getContextPath() + "/" +
                                    ProductCategory.GET_URL_BY_ID(loanApplication.getProductCategoryId()) + "/" +
                                    Configuration.EVALUATION_CONTROLLER_URL + "/" + processToken;
                        }
                    }
                }
            }

            return "/page-verificacion-email";
        } catch (Exception e) {
            e.printStackTrace();

            return "404";// TODO FAILURE HTML
        }
    }

    Object isValidForm(Locale locale, ShortProcessQuestion1Form form, HttpServletRequest request, int productCategory) throws Exception {

        ((ShortProcessQuestion1Form.Validator) form.getValidator()).configValidator(form.getCountryId());
        if (form.getCountryId() == CountryParam.COUNTRY_ARGENTINA) {
            form.setDocType(IdentityDocumentType.CDI);
        }

        if (brandingService.isBrandedByEntityId(request, Entity.BANBIF)) {
            ((ShortProcessQuestion1Form.Validator) form.getValidator()).acceptAgreement.setRequired(false);
            ((ShortProcessQuestion1Form.Validator) form.getValidator()).acceptAgreement2.setRequired(false);
            ((ShortProcessQuestion1Form.Validator) form.getValidator()).amount.setRequired(false);
            ((ShortProcessQuestion1Form.Validator) form.getValidator()).reason.setRequired(false);
            //NEW LINE
//            if(form.getABTesting() != null && form.getABTesting().equalsIgnoreCase("B")){
//                ((ShortProcessQuestion1Form.Validator) form.getValidator()).amount.setRequired(false);
//                ((ShortProcessQuestion1Form.Validator) form.getValidator()).phone.setRequired(false);
//                ((ShortProcessQuestion1Form.Validator) form.getValidator()).email.setRequired(false);
//            }
        }

        if (brandingService.isBrandedByEntityId(request, Entity.AZTECA)) {
            ((ShortProcessQuestion1Form.Validator) form.getValidator()).reason.setRequired(false);
            if (productCategory == ProductCategory.CUENTA_BANCARIA) {
                ((ShortProcessQuestion1Form.Validator) form.getValidator()).amount.setRequired(false);
                ((ShortProcessQuestion1Form.Validator) form.getValidator()).phone.setRequired(false);
                ((ShortProcessQuestion1Form.Validator) form.getValidator()).email.setRequired(false);
            } else if (productCategory == ProductCategory.VALIDACION_IDENTIDAD) {
                ((ShortProcessQuestion1Form.Validator) form.getValidator()).amount.setRequired(false);
                ((ShortProcessQuestion1Form.Validator) form.getValidator()).phone.setRequired(true);
                ((ShortProcessQuestion1Form.Validator) form.getValidator()).email.setRequired(true);
                ((ShortProcessQuestion1Form.Validator) form.getValidator()).acceptAgreement.setRequired(true);
                ((ShortProcessQuestion1Form.Validator) form.getValidator()).conditionsPolicy.setRequired(true);
                ((ShortProcessQuestion1Form.Validator) form.getValidator()).conditionsPolicy.setRequiredErrorMsg("Necesitamos que aceptes las condiciones.");
            } else if (form.getABTesting() != null || productCategory == ProductCategory.GATEWAY) {
                ((ShortProcessQuestion1Form.Validator) form.getValidator()).amount.setRequired(false);
                ((ShortProcessQuestion1Form.Validator) form.getValidator()).phone.setRequired(false);
                ((ShortProcessQuestion1Form.Validator) form.getValidator()).email.setRequired(false);
                ((ShortProcessQuestion1Form.Validator) form.getValidator()).acceptAgreement.setRequired(true);
                if (productCategory == ProductCategory.GATEWAY) {
                    ((ShortProcessQuestion1Form.Validator) form.getValidator()).conditionsPolicy.setRequired(true);
                    ((ShortProcessQuestion1Form.Validator) form.getValidator()).conditionsPolicy.setRequiredErrorMsg("Necesitamos que aceptes las condiciones.");
                }
            } else if (productCategory == ProductCategory.CONSEJ0) {
                ((ShortProcessQuestion1Form.Validator) form.getValidator()).amount.setRequired(false);
                ((ShortProcessQuestion1Form.Validator) form.getValidator()).phone.setRequired(false);
                ((ShortProcessQuestion1Form.Validator) form.getValidator()).email.setRequired(false);
                ((ShortProcessQuestion1Form.Validator) form.getValidator()).acceptAgreement.setRequired(true);
            }
        }

        /*
        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, locale);
        EntityBranding entityBranding = null;
        if (loanApplication.getEntityId() != null) {
            EntityBranding entityBrandingAux = catalogService.getEntityBranding(loanApplication.getEntityId());
            if (entityBrandingAux.getBranded())
                entityBranding = entityBrandingAux;
        }
*/
        ((ShortProcessQuestion1Form.Validator) form.getValidator()).amount.setMaxValue(productService.getMaxAmount(ProductCategory.CONSUMO, form.getCountryId()));
        ((ShortProcessQuestion1Form.Validator) form.getValidator()).amount.setMinValue(productService.getMinAmount(ProductCategory.CONSUMO, form.getCountryId()));
        form.getValidator().validate(locale);

        if (form.getValidator().isHasErrors()) {
            if (ArrayUtils.contains(new int[]{IdentityDocumentType.CDI, IdentityDocumentType.CUIL, IdentityDocumentType.CUIT}, form.getDocType())
                    && form.getDocumentNumber() != null
                    && (form.getDocumentNumber().startsWith("30") || form.getDocumentNumber().startsWith("33") || form.getDocumentNumber().startsWith("34"))) {
                ((ShortProcessQuestion1Form.Validator) form.getValidator()).documentNumber.setError("El CUIT ingresado corresponde a una empresa. Por el momento solo operamos con personas físicas. Gracias por visitarnos.");
                return AjaxResponse.errorFormValidation(form.getValidator().getErrorsJson());
            }

            if (ArrayUtils.contains(new int[]{IdentityDocumentType.CDI, IdentityDocumentType.CUIL, IdentityDocumentType.CUIT}, form.getDocType())) {
                if (form.getDocumentNumber() != null) {
                    if (!utilService.validarCuit(form.getDocumentNumber())) {
                        ((ShortProcessQuestion1Form.Validator) form.getValidator()).documentNumber.setError(messageSource.getMessage("static.message.cuit", null, locale));
                        return AjaxResponse.errorFormValidation(form.getValidator().getErrorsJson());
                    }
                }
            }

            // Validate that the document type is valid
            if (getValidIdentityDocumentTypes(form.getRequest()).stream().noneMatch(d -> d.getId().equals(form.getDocType()))) {
                return AjaxResponse.errorMessage("El tipo de documento es inválido");
            }

            return AjaxResponse.errorFormValidation(form.getValidator().getErrorsJson());
        }
        return null;
    }

    private List<IdentityDocumentType> getValidIdentityDocumentTypes(HttpServletRequest request) throws Exception {
        if (brandingService.isBranded(request)) {
            EntityBranding branding = brandingService.getEntityBranding(request);
            if (branding.getEntity().getDocumentTypes() != null && !branding.getEntity().getDocumentTypes().isEmpty())
                return brandingService.getEntityBranding(request).getEntity().getDocumentTypes();
        }
        return catalogService.getIdentityDocumentTypeByCountry(countryContextService.getCountryParamsByRequest(request).getId(), countryContextService.isCountryContextInArgentina(request));
    }

}
