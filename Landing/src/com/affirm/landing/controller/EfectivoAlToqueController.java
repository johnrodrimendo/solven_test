package com.affirm.landing.controller;


import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.NewDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.dao.UserDAO;
import com.affirm.common.model.annotation.ErrorControllerAnnotation;
import com.affirm.common.model.catalog.*;
import com.affirm.common.model.form.EfectivoAlToqueForm;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.model.transactional.User;
import com.affirm.common.service.*;
import com.affirm.common.service.question.QuestionFlowService;
import com.affirm.common.util.AjaxResponse;
import com.affirm.common.util.SqlErrorMessageException;
import com.affirm.common.util.Util;
import com.affirm.system.configuration.Configuration;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Locale;

@Controller("efectivoaltoqueController")
@Scope("request")
public class EfectivoAlToqueController {

    private static final Logger logger = Logger.getLogger(EfectivoAlToqueController.class);
    public static final String EFECTIVO_AL_TOQUE = "/efectivo-al-toque";

    @Autowired
    MessageSource messageSource;
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
    private ProductService productService;


    @RequestMapping(value = EFECTIVO_AL_TOQUE, method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public String buildLoanForm(ModelMap model, Locale locale, HttpServletRequest request) throws Exception {

        ProductCountryDomain productCountryDomain = productService.getProductDomainByRequest(request);
        if (productCountryDomain == null) {
            return "forward:/404";
        }

        EfectivoAlToqueForm form = new EfectivoAlToqueForm();
        ((EfectivoAlToqueForm.Validator) form.getValidator()).amount.setMaxValue(productService.getMaxAmount(ProductCategory.CONSUMO, CountryParam.COUNTRY_PERU));
        ((EfectivoAlToqueForm.Validator) form.getValidator()).amount.setMinValue(productService.getMinAmount(ProductCategory.CONSUMO, CountryParam.COUNTRY_PERU));
        ((EfectivoAlToqueForm.Validator) form.getValidator()).configValidator(CountryParam.COUNTRY_PERU);

        model.addAttribute("leadProducts", catalogService.getLeadsProductActivity());
        model.addAttribute("leadActivityTypes", catalogService.getLeadActivityTypes());
        model.addAttribute("userRegisterForm", form);
        return "/efectivoaltoque/LandingEfectivoAltoque";
    }

    @RequestMapping(value = EFECTIVO_AL_TOQUE + "/synthesized", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object registerPersonalLoan5(ModelMap model, Locale locale, HttpServletRequest request,
                                        @RequestParam(value = "documentNumber", required = false) String documentNumber) throws Exception {
        if (documentNumber != null && (documentNumber.length() == 8 || documentNumber.length() == 9) && documentNumber.matches("^[0-9]+$"))
            webscrapperService.callRunSynthesized(documentNumber, null);
        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = EFECTIVO_AL_TOQUE, method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object getPersonalLoan5(ModelMap model, Locale locale, HttpServletRequest request, QuestionFlowService.Type flowType,
                                   EfectivoAlToqueForm form,
                                   @RequestParam(value = "token", required = false) String token,
                                   @RequestParam(value = "externalParams", required = false) String externalParams,
                                   @RequestParam(value = "agent", required = false) Integer agentId,
                                   @RequestParam(value = "source", required = false) String source,
                                   @RequestParam(value = "medium", required = false) String medium,
                                   @RequestParam(value = "campaign", required = false) String campaign,
                                   @RequestParam(value = "term", required = false) String term,
                                   @RequestParam(value = "content", required = false) String content,
                                   @RequestParam(value = "gclid", required = false) String gclid,
                                   @RequestParam(value = "gaClientID", required = false) String gaClientID) throws Exception {

        form.setAgentId(Agent.ALE_EFECTIVO_AL_TOQUE);
        form.setRequest(request);
        form.setSource(source);
        form.setMedium(medium);
        form.setCampaign(campaign);
        form.setTerm(term);
        form.setContent(content);
        form.setGclid(gclid);
        form.setGaClientID(gaClientID);
        Object answer = isValidForm(locale, form);

        if (answer != null)
            return answer;

        User user = userService.getOrRegisterUser(form.getDocType(), form.getDocumentNumber(), null, null, null);

        user = userDao.getUser(user.getId());
        if (user.getEmail() != null && !user.getEmail().equalsIgnoreCase(form.getEmail())) {
            throw new SqlErrorMessageException(null, "El  email no coincide con el registrado");
        } else if (user.getEmail() == null) {
            int emailId = userDao.registerEmailChange(user.getId(), form.getEmail().toLowerCase());
            userDao.validateEmailChange(user.getId(), emailId);
        }

        LoanApplication loanApplication = loanApplicationDao.getActiveLoanApplicationByPerson(locale, user.getPersonId(), ProductCategory.LEADS);

        if (loanApplication != null) {
            userDao.registerPhoneNumber(loanApplication.getUserId(), String.valueOf(CountryParam.COUNTRY_PERU), form.getPhone());
            return AjaxResponse.redirect(loanApplicationService.generateLoanApplicationLinkEntity(loanApplication));
        }
        loanApplication = loanApplicationDao.registerLoanApplication(
                user.getId(),
                form.getAmount(),
                Configuration.DEFAULT_INSTALLMENTS,
                null,
                null,
                null,
                null,
                LoanApplication.ORIGIN_ALTERNATIVE_LANDING,
                null,
                null,
                null,
                CountryParam.COUNTRY_PERU);
        loanApplicationDao.registerLeadsLoanApplication(loanApplication.getId(), form.getProductType(), form.getActivityType());
        userService.registerIpUbication(Util.getClientIpAddres(form.getRequest()), loanApplication.getId());
        loanApplicationDao.updateProductCategory(loanApplication.getId(), ProductCategory.LEADS);
        loanApplicationDao.updateProductId(loanApplication.getId(), Product.LEADS);
        loanApplicationDao.updateFormAssistant(loanApplication.getId(), form.getAgentId());
        loanApplicationDao.updateSourceMediumCampaign(loanApplication.getId(), form.getSource(), form.getMedium(), form.getCampaign());
        loanApplicationDao.updateTermContent(loanApplication.getId(), form.getTerm(), form.getContent());
        loanApplicationDao.updateGoogleClickId(loanApplication.getId(), form.getGclid());
        loanApplicationDao.updateGAClientID(loanApplication.getId(), form.getGaClientID());
        loanApplicationDao.updateUserAgent(loanApplication.getId(), form.getRequest().getHeader("User-Agent"));
        loanApplicationDao.updateCurrentQuestion(loanApplication.getId(), ProcessQuestion.Question.Constants.NO_RENIEC_BEFORE_PRE_EVALUATION);
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
            }
        }
        loanApplication = loanApplicationDao.getLoanApplication(loanApplication.getId(), locale);
        loanApplicationDao.updatePerson(loanApplication.getId(), user.getPersonId(), user.getId());
        userDao.registerPhoneNumber(loanApplication.getUserId(), String.valueOf(CountryParam.COUNTRY_PERU), form.getPhone());

        return AjaxResponse.redirect(loanApplicationService.generateLoanApplicationLinkEntity(loanApplication));
    }

    @RequestMapping(value = "/resultado", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public String getResult(ModelMap model, Locale locale, HttpServletRequest request) throws Exception {

        ProductCountryDomain productCountryDomain = productService.getProductDomainByRequest(request);
        if (productCountryDomain == null) {
            return "forward:/404";
        }

        return "/efectivoaltoque/LandingEfectivoAltoqueResult";
    }

    private void createEmailPassword(String email, Integer userId) throws Exception {
        User user = userDao.getUser(userId);
        if (user.getEmail() != null && !user.getEmail().equalsIgnoreCase(email)) {
            throw new SqlErrorMessageException(null, "El  email no coincide con el registrado");
        } else if (user.getEmail() == null) {
            int emailId = userDao.registerEmailChange(userId, email.toLowerCase());
            userDao.validateEmailChange(userId, emailId);
        }
    }

    private List<IdentityDocumentType> getValidIdentityDocumentTypes(HttpServletRequest request) throws Exception {
        if (brandingService.isBranded(request)) {
            EntityBranding branding = brandingService.getEntityBranding(request);
            if (branding.getEntity().getDocumentTypes() != null && !branding.getEntity().getDocumentTypes().isEmpty())
                return brandingService.getEntityBranding(request).getEntity().getDocumentTypes();
        }
        return catalogService.getIdentityDocumentTypeByCountry(countryContextService.getCountryParamsByRequest(request).getId(), countryContextService.isCountryContextInArgentina(request));
    }

    Object isValidForm(Locale locale, EfectivoAlToqueForm form) throws Exception {

        ((EfectivoAlToqueForm.Validator) form.getValidator()).amount.setMaxValue(productService.getMaxAmount(ProductCategory.CONSUMO, CountryParam.COUNTRY_PERU));
        ((EfectivoAlToqueForm.Validator) form.getValidator()).amount.setMinValue(productService.getMinAmount(ProductCategory.CONSUMO, CountryParam.COUNTRY_PERU));
        form.getValidator().validate(locale);

        if (form.getValidator().isHasErrors()) {

            if (getValidIdentityDocumentTypes(form.getRequest()).stream().noneMatch(d -> d.getId().equals(form.getDocType())))
                return AjaxResponse.errorMessage("El tipo de documento es inválido");


            return AjaxResponse.errorFormValidation(form.getValidator().getErrorsJson());
        }
        return null;
    }
}

