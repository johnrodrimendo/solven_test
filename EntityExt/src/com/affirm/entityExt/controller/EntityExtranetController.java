package com.affirm.entityExt.controller;


import com.affirm.bancodelsol.model.CampaniaBds;
import com.affirm.bancodelsol.service.BancoDelSolService;
import com.affirm.bancodelsol.service.impl.BancoDelSolServiceImpl;
import com.affirm.client.dao.EntityCLDAO;
import com.affirm.client.dao.ExtranetDateDAO;
import com.affirm.client.dao.GuaranteedVehicleDAO;
import com.affirm.client.model.ExtranetDate;
import com.affirm.client.model.ExtranetPainterLoanApplication;
import com.affirm.client.model.LoggedUserEntity;
import com.affirm.client.model.ResetPassword;
import com.affirm.client.model.form.RegisterEntityUserForm;
import com.affirm.client.service.EmployerService;
import com.affirm.client.service.EntityExtranetService;
import com.affirm.client.service.VehicleSheetService;
import com.affirm.client.service.impl.EntityExtranetServiceImpl;
import com.affirm.client.util.InvalidPasswordException;
import com.affirm.client.util.MaxUserSessionReachedException;
import com.affirm.client.util.MustChangePasswordException;
import com.affirm.client.util.NoUserFoundException;
import com.affirm.common.dao.*;
import com.affirm.common.model.transactional.ExtranetMenuEntity;
import com.affirm.common.model.StatusExtranetReport;
import com.affirm.common.model.UserOfHierarchy;
import com.affirm.common.model.annotation.ErrorControllerAnnotation;
import com.affirm.common.model.annotation.RequiresPermissionOr403;
import com.affirm.common.model.catalog.*;
import com.affirm.common.model.catalog.Currency;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.*;
import com.affirm.common.service.question.Question22Service;
import com.affirm.common.service.question.QuestionFlowService;
import com.affirm.common.util.*;
import com.affirm.security.dao.SecurityDAO;
import com.affirm.security.model.EntityEmailToken;
import com.affirm.security.model.SecurityInterceptor;
import com.affirm.security.model.SysUser;
import com.affirm.system.configuration.Configuration;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import jxl.read.biff.BiffException;
import net.sf.ehcache.CacheManager;
import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author jrodriguez
 */
@Controller("entityExtranetController")
@Scope("request")
public class EntityExtranetController {

    private static Logger logger = Logger.getLogger(EntityExtranetController.class);

    @Autowired
    private EntityExtranetService entityExtranetService;
    @Autowired
    private UserService userService;
    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private CacheManager ehCacheManager;
    @Autowired
    private EvaluationService evaluationService;
    @Autowired
    private CreditDAO creditDAO;
    @Autowired
    private PersonDAO personDao;
    @Autowired
    private LoanApplicationService loanApplicationService;
    @Autowired
    private SysUserDAO sysUserDao;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private ReportsService reportsService;
    @Autowired
    private OriginatorDAO originatorDAO;
    @Autowired
    private ProductService productService;
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private EmployerDAO employerDAO;
    @Autowired
    private EmployerService employerService;
    @Autowired
    private UtilService utilService;
    @Autowired
    private VehicleSheetService vehicleSheetService;
    @Autowired
    private GuaranteedVehicleDAO guaranteedVehicleDAO;
    @Autowired
    private ExtranetDateDAO extranetDateDAO;
    @Autowired
    private EntityCLDAO entityClDAO;
    @Autowired
    private EntityExtranetDAO entityExtranetDao;
    @Autowired
    private ReportsDAO reportsDao;
    @Autowired
    private BureauService bureauService;
    @Autowired
    private Question22Service question22Service;
    @Autowired
    private BancoDelSolService bancoDelSolService;
    @Autowired
    private RccDAO rccDao;
    @Autowired
    private SecurityDAO securityDAO;
    @Autowired
    private BrandingService brandingService;


    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String extranet(ModelMap model, Locale locale, HttpServletRequest request) throws Exception {

        if(SecurityUtils.getSubject().isPermitted("menu:report:funnelv2:view")){
            return "redirect:/funnel";
        }else if(SecurityUtils.getSubject().isPermitted("menu:report:funnelv3:view")){
            return "redirect:/funnelv3";
        }else if(SecurityUtils.getSubject().isPermitted("menu:funnelCollection:view")){
            return "redirect:/funnelCollection";
        }else if(SecurityUtils.getSubject().isPermitted("menu:loanApplication:register:view")){
            return "redirect:/home";
        }else if (SecurityUtils.getSubject().isPermitted("menu:credit:pending:view")) {
            return "redirect:/pending";
        }else if (SecurityUtils.getSubject().isPermitted("menu:credit:toUpload:view")) {
            return "redirect:/toUpload";
        }else if (SecurityUtils.getSubject().isPermitted("menu:credit:rejected:view")) {
            return "redirect:/rejected";
        }else if (SecurityUtils.getSubject().isPermitted("menu:credit:beingProcessed:view")) {
            return "redirect:/beingProcessed";
        } else if(SecurityUtils.getSubject().isPermitted("menu:loan:evaluate:view")){
            return "redirect:/"+EntityExtranetLoanEvaluationController.URL;
        }else if(SecurityUtils.getSubject().isPermitted("menu:credit:callcenter:view")){
            return "redirect:/callcenter";
        }else if (SecurityUtils.getSubject().isPermitted("menu:credit:generated:view")) {
            return "redirect:/generated";
        }else if (SecurityUtils.getSubject().isPermitted("menu:credit:terminated:view")) {
            return "redirect:/terminated";
        }else if(SecurityUtils.getSubject().isPermitted("menu:marketingCampaign:view")){
            return "redirect:/"+EntityExtranetMarketingCampaignController.URL;
        }else if(SecurityUtils.getSubject().isPermitted("menu:paymentCommitment:view")){
            return "redirect:/"+EntityExtranetPaymentCommitmentController.URL;
        }else if(SecurityUtils.getSubject().isPermitted("menu:communications:view")){
            return "redirect:/"+EntityExtranetCommunicationsController.URL;
        }else if (SecurityUtils.getSubject().isPermitted("menu:report:view")) {
            return "redirect:/reports";
        }else if (SecurityUtils.getSubject().isPermitted("menu:loanAffiliator:register:view")) {
            return "redirect:/newLoanApplication";
        }else if (SecurityUtils.getSubject().isPermitted("menu:affiliator:register:view")) {
            return "redirect:/affiliators";
        }else if (SecurityUtils.getSubject().isPermitted("menu:vehicle:upload:view")) {
            return "redirect:/vehicles";
        }else if (SecurityUtils.getSubject().isPermitted("menu:process:loanApplication")) {
            return "redirect:/home";
        }else if(SecurityUtils.getSubject().isPermitted("menu:credit:generatedtc:view")){
            return "redirect:/generatedtc";
        }else if(SecurityUtils.getSubject().isPermitted("menu:credit:terminatedtc:view")){
            return "redirect:/terminatedtc";
        }else if(SecurityUtils.getSubject().isPermitted("menu:leads:leadsToDeliver:view")){
            return "redirect:/leadsToDeliver";
        }else if(SecurityUtils.getSubject().isPermitted("menu:leads:leadsDelivered:view")){
            return "redirect:/leadsDelivered";
        }else if(SecurityUtils.getSubject().isPermitted("menu:system:monitorApps:view")){
            return "redirect:/monitorApps";
        }else if(SecurityUtils.getSubject().isPermitted("menu:user:management:view")){
            return "redirect:/userManagement";
        }
        else if (true || SecurityUtils.getSubject().isPermitted("menu:credit:leads:view")) {// TODO WHY TRUE || ...
            return "redirect:/leads";
        }
        return "redirect:/welcome";

    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String extranetLogin(
            ModelMap model, Locale locale, HttpServletRequest request,
            @RequestParam(value = "message", required = false) String initialMessage) throws Exception {
        if (initialMessage != null && !initialMessage.trim().isEmpty())
            model.addAttribute("initialMessage", CryptoUtil.decrypt(initialMessage));
        return "/entityExtranet/extranetLogin";
    }

    @RequestMapping(value = "/extranetEntityReset/{resetToken}", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public String getReset(
            ModelMap model, Locale locale, HttpServletRequest request,
            @PathVariable("resetToken") String resetToken) throws Exception {

        if (resetToken == null)
            return "redirect:/login?message=" + CryptoUtil.encrypt("No se ha encontrado un token válido.");

        Boolean isValidResetToken =  userDAO.isResetPasswordTokenUsed(resetToken);

        if (isValidResetToken == null)
            return "redirect:/login?message=" + CryptoUtil.encrypt("No se ha encontrado un token válido.");

        if (isValidResetToken)
            return "redirect:/login?message=" + CryptoUtil.encrypt("El token  para cambiar contraseña ha expirado.");

        Gson gson = new Gson();
        System.out.println(CryptoUtil.decrypt(resetToken));
        ResetPassword resetPassword = gson.fromJson(CryptoUtil.decrypt(resetToken), ResetPassword.class);

        if (resetPassword.getExpireTime() != null) {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
            calendar.setTime(sdf.parse(resetPassword.getExpireTime()));

            if (!calendar.getTime().after(new Date()))
                return "redirect:/login?message=" + CryptoUtil.encrypt("El token  para cambiar contraseña ha expirado.");
        }

//        model.addAttribute("urlLogin", Configuration.getClientDomain().concat("/funcionarios/login"));
        model.addAttribute("token", resetToken);
        return "entityExtranet/extranetReset";
    }

    @RequestMapping(value = "/extranetEntityReset", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object doReset(
            ModelMap model, Locale locale, HttpServletRequest request,
            @RequestParam("token") String token,
            @RequestParam("password") String password,
            @RequestParam("repassword") String repassword) throws Exception {

        if (!password.equals(repassword)) return AjaxResponse.errorMessage("Ambas contraseñas deben ser iguales.");

        if (password.length() < Configuration.MIN_PASSWORD)
            return AjaxResponse.errorMessage("La contraseña debe contener por lo menos " + String.valueOf(Configuration.MIN_PASSWORD) + " caracteres.");

        Pattern pattern = Pattern.compile(StringFieldValidator.PATTER_REGEX_PASSWORD);
        if (!pattern.matcher(password).matches())
            return AjaxResponse.errorMessage("El password debe contener por lo menos un número, una letra y/o un caracter especial [!@#$%^&*_].");


        Gson gson = new Gson();
        ResetPassword resetPassword = gson.fromJson(CryptoUtil.decrypt(token), ResetPassword.class);

        if (resetPassword.getEmail().equals(password))
            return AjaxResponse.errorMessage("La contraseña no puede ser igual al usuario.");

        EntityBranding entityBranding = brandingService.getEntityBranding(request);
        UserEntity user = userDAO.getUserEntityByEmail(resetPassword.getEmail(), locale, entityBranding != null ? entityBranding.getEntity().getId() : null);
        if (!entityExtranetService.validPassword(user.getId(), password)) {
            if (Configuration.OLD_PASSWORDS > 1)
                return AjaxResponse.errorMessage("La contraseña no puede ser igual a las últimas " + Configuration.OLD_PASSWORDS + " contraseñas utilizadas.");
            else if (Configuration.OLD_PASSWORDS == 1)
                return AjaxResponse.errorMessage("La contraseña no puede ser igual a las última contraseña utilizada.");
        }

        if (userDAO.updateResetPassword(token, resetPassword.getEmail(), CryptoUtil.hashPassword(password)))
            return AjaxResponse.ok("");
        else
            return AjaxResponse.errorMessage("El token ha expirado");
    }

    @RequestMapping(value = "/extranetEntityResetPassword", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object resetCredentials(
            ModelMap model, Locale locale, HttpServletRequest request,
            @RequestParam("email") String email) throws Exception {

        EntityBranding entityBranding = brandingService.getEntityBranding(request);
        UserEntity userEntity = userDAO.getUserEntityByEmail(email, locale, entityBranding != null ? entityBranding.getEntity().getId() : null);
        if (userEntity == null)
            return AjaxResponse.errorMessage("El email no está asociado a algun usuario.");
        Integer entityId = null;
        UserEntity userEntityById = userDAO.getUserEntityById(userEntity.getId(), locale);
        if(userEntityById != null && userEntityById.getEntities() != null && userEntityById.getEntities().size() == 1) entityId = userEntityById.getEntities().get(0).getId();
        entityExtranetService.sendResetPasswordEmail(email, locale, entityId);
        return AjaxResponse.ok("");
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object doExtranetLogin(
            Locale locale, HttpServletRequest request,
            @RequestParam("email") String email,
            @RequestParam("password") String password) throws Exception {

//        if(!entityExtranetService.validLoginDate(email)) return AjaxResponse.errorMessage("No tienes acceso al sistema por estar fuera de tu horario laboral.");

        EntityBranding entityBranding = brandingService.getEntityBranding(request);
        UserEntity userEntity = userDAO.getUserEntityByEmail(email, Configuration.getDefaultLocale(), entityBranding != null ? entityBranding.getEntity().getId() : null);

        if (userEntity != null && !userEntity.getActive()) {
            return AjaxResponse.errorMessage("Usuario Inactivo - Contáctese con el administrador");
        }

        AuthenticationToken token = new EntityEmailToken(email.toLowerCase().trim(), password, locale, request);

        // Try Catch just to catch the NoUserFoundException for appropiate error message
        try {
            entityExtranetService.login(token, request);
        } catch (MaxUserSessionReachedException ex) {
            return AjaxResponse.errorMessage(ex.getMessage());
        } catch (NoUserFoundException ex) {
            return AjaxResponse.errorMessage(ex.getMessage());
        } catch (InvalidPasswordException ex) {
            return AjaxResponse.errorMessage(ex.getMessage());
        } catch (MustChangePasswordException ex) {
            return AjaxResponse.errorMessage(ex.getMessage());
        } catch (AuthenticationException ex) {
            return AjaxResponse.errorMessage("");
        }

        LoggedUserEntity loggedUserEntity = entityExtranetService.getLoggedUserEntity();

        if (loggedUserEntity.getResultMessage() != null && !loggedUserEntity.getResultMessage().isEmpty())
            return AjaxResponse.errorMessage(messageSource.getMessage(loggedUserEntity.getResultMessage(), null, locale));

        String resetToken = entityExtranetService.generateResetPassword(email);

        if (loggedUserEntity.getFirstSignIn())
            return AjaxResponse.redirect(request.getContextPath() + "/extranetEntityReset/" + resetToken);

        return AjaxResponse.redirect(request.getContextPath() + "/");
    }

    @RequestMapping(value = "/home", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "menu:loanApplication:register:view", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public Object showHome(
            ModelMap model, Locale locale, HttpServletRequest request) throws Exception {
        Integer loggedUserEntity = entityExtranetService.getLoggedUserEntity().getEntities().get(0).getId();
        Currency currency = entityExtranetService.getActiveEntityCurrency();
        List<EntityProduct> entityProducts = catalogService.getEntityProductsByEntity(loggedUserEntity);
        List<Product> products = entityProducts.stream().map(EntityProduct::getProduct).collect(Collectors.toList());
        List<ProductCategory> categories = new ArrayList<>();
        products.forEach(p -> {
            if (!categories.contains(p.getProductCategory()))
                categories.add(p.getProductCategory());
        });
        model.addAttribute("currency", currency);
        model.addAttribute("products", products);
        model.addAttribute("categories", categories);
        model.addAttribute("form", loggedUserEntity == Entity.BANCO_DEL_SOL ? new CreateLoanBancoDelSolForm() : new CreateLoanForm());

        if (loggedUserEntity == Entity.BANCO_DEL_SOL) {
            JSONObject json = bancoDelSolService.commissionClusterByClientType();
            JSONObject sancorClientJson = json.getJSONObject(BancoDelSolServiceImpl.CLIENTE_SANCOR_CLUSTER_NAME);
            JSONObject noSancorClientJson = json.getJSONObject(BancoDelSolServiceImpl.CLIENTE_NO_SANCOR_CLUSTER_NAME);

//            TODO FIND A BETTER WAY
            model.put("sancorMinInstallments", sancorClientJson.getInt("minInstallments"));
            model.put("sancorMaxInstallments", sancorClientJson.getInt("maxInstallments"));
            model.put("noSancorMinInstallments", noSancorClientJson.getInt("minInstallments"));
            model.put("noSancorMaxInstallments", noSancorClientJson.getInt("maxInstallments"));
            model.put("sancorMinAmount", sancorClientJson.getInt("minAmount"));
            model.put("noSancorMinAmount", noSancorClientJson.getInt("minAmount"));
            model.put("sancorMaxAmount", sancorClientJson.getInt("maxAmount"));
            model.put("noSancorMaxAmount", noSancorClientJson.getInt("maxAmount"));
            model.put("sancorMinRateCommission", sancorClientJson.getDouble("minRateCommission"));
            model.put("sancorMaxRateCommission", sancorClientJson.getDouble("maxRateCommission"));
            model.put("noSancorMinRateCommission", noSancorClientJson.getDouble("minRateCommission"));
            model.put("noSancorMaxRateCommission", noSancorClientJson.getDouble("maxRateCommission"));
        }
        return new ModelAndView("/entityExtranet/extranetDashboard");
    }

    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object logout(
            ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response) throws Exception {
        entityExtranetService.onLogout(entityExtranetService.getLoggedUserEntity().getSessionId(), new Date());
        SecurityUtils.getSubject().logout();
        return AjaxResponse.redirect(request.getContextPath());
    }

    @RequestMapping(value = "/credit/{creditId}", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public Object showCredit(
            ModelMap model, Locale locale, HttpServletRequest request,
            @PathVariable("creditId") int creditId) throws Exception {

        Credit credit = creditDAO.getCreditByID(creditId, locale, false, Credit.class);
        if (credit == null || !credit.getEntity().getId().equals(entityExtranetService.getLoggedUserEntity().getPrincipalEntity().getId()))
            throw new Exception("The credit doesnt exists");

        if (entityExtranetService.getLoggedUserEntity().getSysUserId() == null)
            throw new Exception("Not authorized");

        SysUser sysUser = sysUserDao.getSysUserById(entityExtranetService.getLoggedUserEntity().getSysUserId());
        if (sysUser == null)
            throw new Exception("Not authorized");

        String basicAuth = null;
        if (Configuration.hostEnvIsProduction()) {
            GoogleAuthenticator gAuth2 = new GoogleAuthenticator();
            int authCode = gAuth2.getTotpPassword(CryptoUtil.decryptAuthSecret(sysUser.getUserName(), sysUser.getTfaSharedSecret()));
            basicAuth = "Basic " + new String(new Base64().encode((sysUser.getUserName() + ":" + authCode).getBytes()));
        } else {
            basicAuth = "Basic " + new String(new Base64().encode((sysUser.getUserName() + ":" + sysUser.getUserName() + new SimpleDateFormat("H").format(new Date())).getBytes()));
        }

        {
            JSONObject json = new JSONObject();
            json.put("creditId", creditId);
            json.put("entityId", entityExtranetService.getLoggedUserEntity().getEntities().get(0).getId());
            java.net.URL urlConnection = new URL(Configuration.getBackofficeDomain() + "/externalView/credit/" + CryptoUtil.encrypt(json.toString()));
            URLConnection urlContent = urlConnection.openConnection();
            urlContent.setRequestProperty(SecurityInterceptor.HEADER_SOLVEN_BOT, "true");
            urlContent.setRequestProperty("Authorization", basicAuth);
            BufferedReader in = new BufferedReader(new InputStreamReader(urlContent.getInputStream(), Charset.forName("ISO-8859-1")));
            String inputLine;
            StringBuffer creditHtml = new StringBuffer();

            while ((inputLine = in.readLine()) != null)
                creditHtml.append(inputLine);
            in.close();
            model.addAttribute("creditHtml", creditHtml.toString().replaceAll("portlet light", "portlet light bordered"));
        }

        {
            JSONObject json = new JSONObject();
            json.put("personId", credit.getPersonId());
            json.put("panel", "client");
            java.net.URL urlConnection = new URL(Configuration.getBackofficeDomain() + "/externalView/person/" + CryptoUtil.encrypt(json.toString()));
            URLConnection urlContent = urlConnection.openConnection();
            urlContent.setRequestProperty(SecurityInterceptor.HEADER_SOLVEN_BOT, "true");
            urlContent.setRequestProperty("Authorization", basicAuth);
            BufferedReader in = new BufferedReader(new InputStreamReader(urlContent.getInputStream(), Charset.forName("ISO-8859-1")));
            String inputLine;
            StringBuffer personHtml = new StringBuffer();

            while ((inputLine = in.readLine()) != null)
                personHtml.append(inputLine);
            in.close();
            model.addAttribute("personClientHtml", personHtml.toString().replaceAll("portlet light", "portlet light bordered"));
        }

        {
            JSONObject json = new JSONObject();
            json.put("personId", credit.getPersonId());
            json.put("panel", "partner");
            java.net.URL urlConnection = new URL(Configuration.getBackofficeDomain() + "/externalView/person/" + CryptoUtil.encrypt(json.toString()));
            URLConnection urlContent = urlConnection.openConnection();
            urlContent.setRequestProperty(SecurityInterceptor.HEADER_SOLVEN_BOT, "true");
            urlContent.setRequestProperty("Authorization", basicAuth);
            BufferedReader in = new BufferedReader(new InputStreamReader(urlContent.getInputStream(), Charset.forName("ISO-8859-1")));
            String inputLine;
            StringBuffer personHtml = new StringBuffer();

            while ((inputLine = in.readLine()) != null)
                personHtml.append(inputLine);
            in.close();
            model.addAttribute("personPartnerHtml", personHtml.toString().replaceAll("portlet light", "portlet light bordered"));
        }

        {
            JSONObject json = new JSONObject();
            json.put("personId", credit.getPersonId());
            json.put("panel", "contact");
            java.net.URL urlConnection = new URL(Configuration.getBackofficeDomain() + "/externalView/person/" + CryptoUtil.encrypt(json.toString()));
            URLConnection urlContent = urlConnection.openConnection();
            urlContent.setRequestProperty(SecurityInterceptor.HEADER_SOLVEN_BOT, "true");
            urlContent.setRequestProperty("Authorization", basicAuth);
            BufferedReader in = new BufferedReader(new InputStreamReader(urlContent.getInputStream(), Charset.forName("ISO-8859-1")));
            String inputLine;
            StringBuffer personHtml = new StringBuffer();

            while ((inputLine = in.readLine()) != null)
                personHtml.append(inputLine);
            in.close();
            model.addAttribute("personContactHtml", personHtml.toString().replaceAll("portlet light", "portlet light bordered"));
        }

        {
            JSONObject json = new JSONObject();
            json.put("personId", credit.getPersonId());
            json.put("panel", "income");
            java.net.URL urlConnection = new URL(Configuration.getBackofficeDomain() + "/externalView/person/" + CryptoUtil.encrypt(json.toString()));
            URLConnection urlContent = urlConnection.openConnection();
            urlContent.setRequestProperty(SecurityInterceptor.HEADER_SOLVEN_BOT, "true");
            urlContent.setRequestProperty("Authorization", basicAuth);
            BufferedReader in = new BufferedReader(new InputStreamReader(urlContent.getInputStream(), Charset.forName("ISO-8859-1")));
            String inputLine;
            StringBuffer personHtml = new StringBuffer();

            while ((inputLine = in.readLine()) != null)
                personHtml.append(inputLine);
            in.close();
            model.addAttribute("personIncomeHtml", personHtml.toString().replaceAll("portlet light", "portlet light bordered"));
        }

        {
            JSONObject json = new JSONObject();
            json.put("personId", credit.getPersonId());
            json.put("panel", "bank_account");
            java.net.URL urlConnection = new URL(Configuration.getBackofficeDomain() + "/externalView/bankAccount/" + CryptoUtil.encrypt(json.toString()));
            URLConnection urlContent = urlConnection.openConnection();
            urlContent.setRequestProperty(SecurityInterceptor.HEADER_SOLVEN_BOT, "true");
            urlContent.setRequestProperty("Authorization", basicAuth);
            BufferedReader in = new BufferedReader(new InputStreamReader(urlContent.getInputStream(), Charset.forName("ISO-8859-1")));
            String inputLine;
            StringBuffer personHtml = new StringBuffer();

            while ((inputLine = in.readLine()) != null)
                personHtml.append(inputLine);
            in.close();
            model.addAttribute("personBankAccountHtml", personHtml.toString().replaceAll("portlet light", "portlet light bordered"));
        }

        {
            if (credit.getCountry().getId() == CountryParam.COUNTRY_ARGENTINA || credit.getEntity().getId() == Entity.CREDIGOB) {
                JSONObject json = new JSONObject();
                json.put("personId", credit.getPersonId());
                json.put("panel", "tab-rcc");
                java.net.URL urlConnection = new URL(Configuration.getBackofficeDomain() + "/externalView/bcraResult/" + CryptoUtil.encrypt(json.toString()));
                URLConnection urlContent = urlConnection.openConnection();
                urlContent.setRequestProperty(SecurityInterceptor.HEADER_SOLVEN_BOT, "true");
                urlContent.setRequestProperty("Authorization", basicAuth);
                BufferedReader in = new BufferedReader(new InputStreamReader(urlContent.getInputStream(), Charset.forName("ISO-8859-1")));
                String inputLine;
                StringBuffer personHtml = new StringBuffer();

                while ((inputLine = in.readLine()) != null)
                    personHtml.append(inputLine);
                in.close();
                model.addAttribute("bcraResultTitle", credit.getCountry().getId() == CountryParam.COUNTRY_ARGENTINA ? "BCRA" : "SBS");
                model.addAttribute("bcraResultHtml", personHtml.toString().replaceAll("portlet light", "portlet light bordered"));
            }
        }

        {
            if (credit.getEntity().getId() == Entity.MULTIFINANZAS) {
                JSONObject json = new JSONObject();
                json.put("personId", credit.getPersonId());
                java.net.URL urlConnection = new URL(Configuration.getBackofficeDomain() + "/externalView/nosisResult/" + CryptoUtil.encrypt(json.toString()));
                URLConnection urlContent = urlConnection.openConnection();
                urlContent.setRequestProperty(SecurityInterceptor.HEADER_SOLVEN_BOT, "true");
                urlContent.setRequestProperty("Authorization", basicAuth);
                BufferedReader in = new BufferedReader(new InputStreamReader(urlContent.getInputStream(), Charset.forName("ISO-8859-1")));
                String inputLine;
                StringBuffer personHtml = new StringBuffer();

                while ((inputLine = in.readLine()) != null)
                    personHtml.append(inputLine);
                in.close();
                model.addAttribute("nosisResultTitle", "NOSIS");
                model.addAttribute("nosisResultHtml", personHtml.toString().replaceAll("portlet light", "portlet light bordered"));
            }
        }
        model.addAttribute("hideLoading", true);
        model.addAttribute("sidebarClosed", true);
        return new ModelAndView("/entityExtranet/extranetCreditDetail");
    }

    @RequestMapping(value = "/createLoanApplication/documentNumber", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object showPersonByDocumentNumber(
            ModelMap model, Locale locale,
            @RequestParam("docType") Integer docType, @RequestParam("docNumber") String docNumber) throws Exception {
        if(docType == 0)
            return AjaxResponse.errorMessage("Seleccione un tipo de documento");
        if(docNumber == null || docNumber.isEmpty())
            return AjaxResponse.errorMessage("Ingrese el nro. documento");

        if (personDao.isInBdsSancorEmployees(docNumber))
            if(rccDao.getLastCampaniaBds(docNumber) == null)
                return AjaxResponse.errorMessage("Empleado GSS, contactarse por mail con solicitudes@bancodelsol.com y un asistente comercial se comunicará a la brevedad.");

        User user = userDAO.getUserByDocument(docType, docNumber);
        if(user != null) {
            Person person = personDao.getPerson(catalogService, locale, user.getPersonId(), false);
            if(person.getName() != null){
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

                JSONObject result = new JSONObject();

                if(entityExtranetService.getLoggedUserEntity().containsEntityId(Entity.BANCO_DEL_SOL)){
                    result.put("name", person.getFullSurnames()+" "+person.getFirstName() + " " + person.getOtherNames());
                }else{
                    result.put("name", person.getFirstName() + " " + person.getOtherNames());
                    result.put("lastname", person.getFullSurnames());
                }
                if(person.getBirthday() != null)
                    result.put("birthdate", sdf.format(person.getBirthday()));
                result.put("phone", user.getPhoneNumber());
                result.put("email", user.getEmail());
                result.put("documentNumber", person.getDocumentNumber());

                return AjaxResponse.ok(result.toString());
            }
        }

        String fullName = personDao.getPadronAfipFullName(docNumber);
        JSONObject result = new JSONObject();

        if(fullName != null){
            String []fullNameArr = fullName.split(" ");
            StringBuilder sb = new StringBuilder();
            for(int i=0;i < fullNameArr.length;i++){
                String tmpName = fullNameArr[i].toLowerCase();
                sb.append(tmpName.toUpperCase().charAt(0)+tmpName.substring(1,tmpName.length())+" ");
            }
            result.put("name", sb.toString().trim());
            result.put("documentNumber", docNumber);
            return AjaxResponse.ok(result.toString());
        }else{
            return AjaxResponse.errorMessage("Numero de documento no encontrado");
        }
    }

    @RequestMapping(value = "/createLoanApplication", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "loanApplication:register:store", type = RequiresPermissionOr403.Type.AJAX, saveLog = true, webApp = WebApplication.ENTITY_EXTRANET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object createLoanApplication(
            ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response, CreateLoanForm form) throws Exception {

        form.getValidator().validate(locale);
        if (form.getValidator().isHasErrors()) {
            return AjaxResponse.errorFormValidation(form.getValidator().getErrorsJson());
        }

        if (!personDao.isDocumentValidForEntity(entityExtranetService.getLoggedUserEntity().getEntities().get(0).getId(), form.getCategory(), form.getDocType(), form.getDocNumber()))
            return AjaxResponse.errorMessage("No puedes crear solicitudes para ese documento de identidad.");

        User user = userService.getOrRegisterUser(form.getDocType(), form.getDocNumber(), null, null, null);

        boolean loanExisted = true;
        LoanApplication currentLoanApplication = loanApplicationDao.getActiveLoanApplicationByPerson(locale, user.getPersonId(), form.getCategory());
        if (currentLoanApplication == null) {
            loanExisted = false;
            currentLoanApplication = loanApplicationDao.registerLoanApplication(
                    user.getId(),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    LoanApplication.ORIGIN_EXTRANET_ENTITY,
                    null,
                    null,
                    null,
                    entityExtranetService.getLoggedUserEntity().getEntities().get(0).getCountryId());
            loanApplicationDao.updateEntityId(currentLoanApplication.getId(), entityExtranetService.getLoggedUserEntity().getPrincipalEntity().getId());
            loanApplicationDao.updateProductCategory(currentLoanApplication.getId(), form.getCategory());
            loanApplicationDao.updateFormAssistant(currentLoanApplication.getId(), catalogService.getFormAssistantsAgents(null).get(0).getId());
            loanApplicationDao.updateEntityUser(currentLoanApplication.getId(), entityExtranetService.getLoggedUserEntity().getId());
            currentLoanApplication = loanApplicationDao.getLoanApplication(currentLoanApplication.getId(), locale);
            evaluationService.forwardByResult(currentLoanApplication, null, request);
            evaluationService.forwardByResult(currentLoanApplication, "DEFAULT", request);
            if (form.getDocType() == IdentityDocumentType.CE) {
                evaluationService.forwardByResult(currentLoanApplication, "CE", request);
            } else {
                evaluationService.forwardByResult(currentLoanApplication, "DEFAULT", request);
            }
        } else {
            loanApplicationDao.updateEntityUser(currentLoanApplication.getId(), entityExtranetService.getLoggedUserEntity().getId());
        }

        return AjaxResponse.redirect("/" + ProductCategory.GET_URL_BY_ID(currentLoanApplication.getProductCategoryId()) +
                "/" + Configuration.EVALUATION_CONTROLLER_URL +
                "/" + evaluationService.generateEvaluationToken(currentLoanApplication.getUserId(), currentLoanApplication.getPersonId(), currentLoanApplication.getId()) +
                (loanExisted ? "?showWelcome=true" : ""));
    }

    @RequestMapping(value = "/bancoDelSol/createLoanApplication", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "menu:loanApplication:register:view", type = RequiresPermissionOr403.Type.AJAX, saveLog = true, webApp = WebApplication.ENTITY_EXTRANET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object createBancoDelSolLoanApplication(
            ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response, CreateLoanBancoDelSolForm form) throws Exception {

        form.getValidator().validate(locale);
        if (form.getValidator().isHasErrors()) {
            return AjaxResponse.errorFormValidation(form.getValidator().getErrorsJson());
        }

        Integer loggedUserEntity = entityExtranetService.getLoggedUserEntity().getEntities().get(0).getId();
        Integer loggedUserCountry = entityExtranetService.getLoggedUserEntity().getEntities().get(0).getCountryId();

        if (!personDao.isDocumentValidForEntity(loggedUserEntity, ProductCategory.CONSUMO, 4, form.getDocNumber()))
            return AjaxResponse.errorMessage("No podés crear solicitudes para ese documento de identidad.");

        String fullName = personDao.getPadronAfipFullName(form.getDocNumber());
        String[] nameSplitted = fullName != null ? fullName.split(" ") : null;

        User user = userService.getOrRegisterUser(4, form.getDocNumber(), null, nameSplitted != null ? nameSplitted[nameSplitted.length - 1] : null,  nameSplitted != null ? nameSplitted[0] : null);

        if (user.getEmail() != null && !user.getEmail().equals(form.getEmail())) {
            return AjaxResponse.errorMessage("El email no coincide con el registrado.");
        }

        personDao.updateBirthday(user.getPersonId(), utilService.parseDate(form.getBirthdate(), "dd/MM/yyyy", locale));
        personDao.updatePhoneNumber(user.getId(), "54", "(" + form.getCode() + ") " + form.getPhone());
        personDao.updateAddressInformation(user.getPersonId(), null, form.getProvince(), null, null, null, null, null, null, null, null);

        LoanApplication currentLoanApplication = loanApplicationDao.getActiveLoanApplicationByPerson(locale, user.getPersonId(), ProductCategory.CONSUMO, Entity.BANCO_DEL_SOL);

        if (currentLoanApplication == null) {

            currentLoanApplication = loanApplicationDao.registerLoanApplication(
                    user.getId(),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    LoanApplication.ORIGIN_EXTRANET_ENTITY,
                    null,
                    null,
                    null,
                    loggedUserCountry);
            loanApplicationDao.updateEntityId(currentLoanApplication.getId(), loggedUserEntity);
            loanApplicationDao.updateProductCategory(currentLoanApplication.getId(), ProductCategory.CONSUMO);
            loanApplicationDao.updateFormAssistant(currentLoanApplication.getId(), catalogService.getFormAssistantsAgents(Entity.BANCO_DEL_SOL).get(0).getId());
            loanApplicationDao.updateEntityUser(currentLoanApplication.getId(), entityExtranetService.getLoggedUserEntity().getId());
            currentLoanApplication = loanApplicationDao.getLoanApplication(currentLoanApplication.getId(), locale);
            loanApplicationDao.updateEntityCustomData(currentLoanApplication.getId(),
                    currentLoanApplication.getEntityCustomData()
                            .put(LoanApplication.EntityCustomDataKeys.BANCO_DEL_SOL_LOAN_DESTINATION.getKey(), form.getDestination())
                            .put(LoanApplication.EntityCustomDataKeys.BANCO_DEL_SOL_LOAN_REASON.getKey(), form.getReason())
                            .put(LoanApplication.EntityCustomDataKeys.BANCO_DEL_SOL_LOAN_CLIENT_TYPE.getKey(), form.getClientType())
                            .put(LoanApplication.EntityCustomDataKeys.BANCO_DEL_SOL_PROVINCIA_RETIRO.getKey(), form.getProvince())
                            .put(LoanApplication.EntityCustomDataKeys.BANCO_DEL_SOL_ENTITY_USER_CHANNEL_ID.getKey(), entityExtranetService.getLoggedUserEntity().getEntityAcquisitionChannelId())
            );
            userService.registerIpUbication(Util.getClientIpAddres(request), currentLoanApplication.getId());
            CampaniaBds campaniaBds = rccDao.getLastCampaniaBds(form.getDocNumber());
            if(campaniaBds != null){
                //loanApplicationDao.updateEntityProductParameterId(currentLoanApplication.getId(), EntityProductParams.ENT_PROD_PARAM_BANCO_DEL_SOL_CAMPANA);
                Person person = personDao.getPerson(user.getPersonId(), false, locale);
                if(person.getGender() ==  null && campaniaBds.getGender() != null)
                    personDao.updateGender(person.getId(), campaniaBds.getGender());
                if(campaniaBds.getNames() != null && campaniaBds.getApellido() != null){
                    personDao.updateName(person.getId(), campaniaBds.getNames());
                    personDao.updateFirstSurname(person.getId(), campaniaBds.getApellido());
                }
            }else{
                //loanApplicationDao.updateEntityProductParameterId(currentLoanApplication.getId(), catalogService.getEntityAcquisitionChannelById(entityExtranetService.getLoggedUserEntity().getEntityAcquisitionChannelId()).getEntityProductParameterId());
            }
            loanApplicationDao.updateCurrentQuestion(currentLoanApplication.getId(), ProcessQuestion.Question.Constants.RUN_IOVATION);

            int emailId = userDAO.registerEmailChange(currentLoanApplication.getUserId(), form.getEmail().toLowerCase());
            userDAO.validateEmailChange(currentLoanApplication.getUserId(), emailId);
        } else {
            if (currentLoanApplication.getEntityId() == null || !currentLoanApplication.getEntityId().equals(Entity.BANCO_DEL_SOL)) {
                return AjaxResponse.errorMessage("No se puede crear la solicitud ya que el usuario ya cuenta con una activa en Solven.");
            }

            if (!currentLoanApplication.getEntityUserId().equals(entityExtranetService.getLoggedUserEntity().getId())) {
                return AjaxResponse.errorMessage("Otro usuario ya creó una solicitud con el CUIT/CUIL ingresado.");
            }

            loanApplicationDao.updateEntityUser(currentLoanApplication.getId(), entityExtranetService.getLoggedUserEntity().getId()); //TODO: check if this is necessary

            int emailId = userDAO.registerEmailChange(currentLoanApplication.getUserId(), form.getEmail().toLowerCase());
            userDAO.validateEmailChange(currentLoanApplication.getUserId(), emailId);

            validateReevaluation(currentLoanApplication, form, locale);
        }

        // Call Nosis to update the external data
        bureauService.callNosisUpdateExternalData(form.getDocNumber());

        return AjaxResponse.redirect(loanApplicationService.generateLoanApplicationLinkEntity(currentLoanApplication));
    }

    private void validateReevaluation(LoanApplication currentLoanApplication, CreateLoanBancoDelSolForm form, Locale locale) {
        String currentClientType = JsonUtil.getStringFromJson(currentLoanApplication.getEntityCustomData(), LoanApplication.EntityCustomDataKeys.BANCO_DEL_SOL_LOAN_CLIENT_TYPE.getKey(), null);
        String currentProvince = JsonUtil.getStringFromJson(currentLoanApplication.getEntityCustomData(), LoanApplication.EntityCustomDataKeys.BANCO_DEL_SOL_PROVINCIA_RETIRO.getKey(), null);

        if (!Objects.equals(currentClientType, form.getClientType()) || !Objects.equals(currentProvince, form.getProvince())) {

            if (Arrays.asList(
                    LoanApplicationStatus.NEW,
                    LoanApplicationStatus.PRE_EVAL_APPROVED,
                    LoanApplicationStatus.EVAL_APPROVED,
                    LoanApplicationStatus.WAITING_APPROVAL,
                    LoanApplicationStatus.APPROVED,
                    LoanApplicationStatus.REJECTED_AUTOMATIC,
                    LoanApplicationStatus.REJECTED_AUTOMATICALLY_EVALUATION).contains(currentLoanApplication.getStatus().getId())) {

                if (currentLoanApplication.getCreditId() != null) {
                    Credit credit = creditDAO.getCreditByID(currentLoanApplication.getCreditId(), locale, false, Credit.class);
                    if (CreditStatus.INACTIVE_W_SCHEDULE == credit.getStatus().getId()) {
                        loanApplicationDao.updateEntityCustomData(currentLoanApplication.getId(),
                                currentLoanApplication.getEntityCustomData()
                                        .put(LoanApplication.EntityCustomDataKeys.BANCO_DEL_SOL_LOAN_DESTINATION.getKey(), form.getDestination())
                                        .put(LoanApplication.EntityCustomDataKeys.BANCO_DEL_SOL_LOAN_REASON.getKey(), form.getReason())
                                        .put(LoanApplication.EntityCustomDataKeys.BANCO_DEL_SOL_LOAN_CLIENT_TYPE.getKey(), form.getClientType())
                                        .put(LoanApplication.EntityCustomDataKeys.BANCO_DEL_SOL_PROVINCIA_RETIRO.getKey(), form.getProvince())
                        );

                        creditDAO.returnCreditToLoanApplication(currentLoanApplication.getCreditId());
                        loanApplicationService.reevaluateLoanApplicationsButKeepBureaus(currentLoanApplication.getId());
                    }
                } else {
                    loanApplicationDao.updateEntityCustomData(currentLoanApplication.getId(),
                            currentLoanApplication.getEntityCustomData()
                                    .put(LoanApplication.EntityCustomDataKeys.BANCO_DEL_SOL_LOAN_DESTINATION.getKey(), form.getDestination())
                                    .put(LoanApplication.EntityCustomDataKeys.BANCO_DEL_SOL_LOAN_REASON.getKey(), form.getReason())
                                    .put(LoanApplication.EntityCustomDataKeys.BANCO_DEL_SOL_LOAN_CLIENT_TYPE.getKey(), form.getClientType())
                                    .put(LoanApplication.EntityCustomDataKeys.BANCO_DEL_SOL_PROVINCIA_RETIRO.getKey(), form.getProvince())
                    );

                    if (Arrays.asList(
                            LoanApplicationStatus.EVAL_APPROVED,
                            LoanApplicationStatus.WAITING_APPROVAL,
                            LoanApplicationStatus.APPROVED,
                            LoanApplicationStatus.REJECTED_AUTOMATIC,
                            LoanApplicationStatus.REJECTED_AUTOMATICALLY_EVALUATION).contains(currentLoanApplication.getStatus().getId())) {

                        loanApplicationService.reevaluateLoanApplicationsButKeepBureaus(currentLoanApplication.getId());
                    }
                }
            }
        }
    }

    @RequestMapping(value = "/reports", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "menu:report:view", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public Object showReports(
            ModelMap model, Locale locale, HttpServletRequest request) throws Exception {
        int extranetEntityId = entityExtranetService.getLoggedUserEntity().getEntities().get(0).getId();

        List<EntityProductParams> products = catalogService.getEntityProductParams().stream().filter(e -> e.getEntity().getId().equals(extranetEntityId)).distinct().collect(Collectors.toList());

        if(SecurityUtils.getSubject().isPermitted("report:funnel:execute")){
            List<ReportProces> historicReports = reportsDao.getFunnelReportProcesHistoric(WebApplication.ENTITY_EXTRANET, Report.REPORTE_FUNNEL, extranetEntityId, 0, 5);
            model.addAttribute("historicReports", historicReports);
        }

        model.addAttribute("products", products);
        model.addAttribute("form", new CreateLoanForm());
        model.addAttribute("entity", extranetEntityId);

        if (Entity.BANCO_DEL_SOL == extranetEntityId) {
            int userId = entityExtranetService.getLoggedUserEntity().getId();

            if(SecurityUtils.getSubject().isPermitted("report:solicitudesEnProceso:execute")){
                List<ReportProces> loanInProcessHistoricReports = reportsDao.getReportProcesHistoric(userId, Report.REPORTE_SOLICITUDES_EN_PROCESO_EXT_BDS, 0, 5);
                model.addAttribute("loanInProcessHistoricReports", loanInProcessHistoricReports);
            }
            if(SecurityUtils.getSubject().isPermitted("report:creditosADesembolsar:execute")){
                List<ReportProces> loanToBeDisbursedHistoricReports = reportsDao.getReportProcesHistoric(userId, Report.REPORTE_CREDITOS_A_DESEMBOLSAR_EXT_BDS, 0, 5);
                model.addAttribute("loanToBeDisbursedHistoricReports", loanToBeDisbursedHistoricReports);
            }
            if(SecurityUtils.getSubject().isPermitted("report:creditosDesembolsados:execute")){
                List<ReportProces> disbursedLoanHistoricReports = reportsDao.getReportProcesHistoric(userId, Report.REPORTE_CREDITOS_DESEMBOLSADOS_EXT_BDS, 0, 5);
                model.addAttribute("disbursedLoanHistoricReports", disbursedLoanHistoricReports);
            }
            if(SecurityUtils.getSubject().isPermitted("report:riesgosBds:execute")){
                List<ReportProces> loanRisksHistoricReports = reportsDao.getReportProcesHistoric(userId, Report.REPORTE_RIESGOS_EXT_BDS, 0, 5);
                model.addAttribute("loanRisksHistoricReports", loanRisksHistoricReports);
            }

            List<UserOfHierarchy> organizers = userDAO.getUsersOfHierarchy();
            organizers.sort(Comparator.comparing(UserOfHierarchy::getFirstSurname, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)));

            model.addAttribute("organizers", organizers);
            model.addAttribute("loanStatuses", StatusExtranetReport.getLoanStatuses());
            model.addAttribute("internalStatuses", StatusExtranetReport.getInternalStatuses());
            model.addAttribute("creditStatuses", StatusExtranetReport.getCreditStatuses());
        }

        if (Entity.FUNDACION_DE_LA_MUJER == extranetEntityId) {
            int userId = entityExtranetService.getLoggedUserEntity().getId();

            if(SecurityUtils.getSubject().isPermitted("report:fdlmSolicitudesEnProceso:execute")){
                model.addAttribute("loanInProcessHistoricReports", reportsDao.getReportProcesHistoric(userId, Report.REPORTE_SOLICITUDES_EN_PROCESO_EXT_FDLM, 0, 5));
            }

            model.addAttribute("loanStatuses", StatusExtranetReport.getLoanStatusesFDLM());
            model.addAttribute("creditStatuses", StatusExtranetReport.getCreditStatusesFDLM());// TODO
        }

        if (SecurityUtils.getSubject().isPermitted("report:loansLight:execute")) {
            int userId = entityExtranetService.getLoggedUserEntity().getId();
            model.addAttribute("loansLightHistoricReports", reportsDao.getReportProcesHistoric(userId, Report.REPORTE_SOLICITUDES_LIGHT, 0, 5, WebApplication.ENTITY_EXTRANET));
        }

        return new ModelAndView("/entityExtranet/extranetReports");
    }

    @RequestMapping(value = "/reports/loansLight/export", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "report:loansLight:execute", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object showReportingLoansLight(@RequestParam(value = "startDate[]", required = false) String startDate,
                                          @RequestParam(value = "endDate[]", required = false) String endDate) throws Exception {
        Date from = new Date();
        Date to = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        if (startDate != null && !startDate.isEmpty() && endDate != null && !endDate.isEmpty()) {
            from = formatter.parse(startDate);
            to = formatter.parse(endDate);
        }

        Integer[] entities = { entityExtranetService.getPrincipalEntity().getId() };
        Integer[] countries = { entityExtranetService.getPrincipalEntity().getCountryId() };

        reportsService.createReporteSolicitudesLight(entityExtranetService.getLoggedUserEntity().getId(), from, to, countries, entities, WebApplication.ENTITY_EXTRANET);

        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = "/reports/loansLight/list", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "report:loansLight:execute", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public Object showLoansLightReportList(ModelMap model) throws Exception {
        int userId = entityExtranetService.getLoggedUserEntity().getId();
        List<ReportProces> historicReports = reportsDao.getReportProcesHistoric(userId, Report.REPORTE_SOLICITUDES_LIGHT, 0, 5, WebApplication.ENTITY_EXTRANET);
        model.addAttribute("loansLightHistoricReports", historicReports);
        return new ModelAndView("/entityExtranet/extranetReports :: loansLightResults");
    }

    @RequestMapping(value = "/reports/funnelReport/export", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "report:funnel:execute", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object showReportingFunnelExport(ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response,
                                            @RequestParam(value = "creationFrom[]", required = false) String creationFrom,
                                            @RequestParam(value = "creationTo[]", required = false) String creationTo,
                                            @RequestParam(value = "status[]", required = false) Integer dateType,
                                            @RequestParam(value = "product[]", required = false) Integer[] products) throws Exception {
        int origin = WebApplication.ENTITY_EXTRANET;
        Date from;
        Date to;
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        if (creationFrom != null && !creationFrom.isEmpty() && creationTo != null && !creationTo.isEmpty()) {
            from = formatter.parse(creationFrom);
            to = formatter.parse(creationTo);
        } else {
            Calendar today = Calendar.getInstance();

            from = today.getTime();
            to = today.getTime();
        }

        Integer[] entities = {entityExtranetService.getLoggedUserEntity().getEntities().get(0).getId()};
        Integer[] countries = {entityExtranetService.getLoggedUserEntity().getEntities().get(0).getCountryId()};

        reportsService.createReporteFunnelBo(entityExtranetService.getLoggedUserEntity().getId(), from, to, dateType, entities, new Integer[]{}, "", "", "", origin, countries);

        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = "/reports/funnelReport/list", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "report:funnel:execute", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public Object showFunnelReportList(ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response) throws Exception {

        if(entityExtranetService.getLoggedUserEntity() == null)
            return AjaxResponse.ok(null);

        List<ReportProces> historicReports = reportsDao.getFunnelReportProcesHistoric(
                WebApplication.ENTITY_EXTRANET, Report.REPORTE_FUNNEL, entityExtranetService.getLoggedUserEntity().getEntities().get(0).getId(), 0, 10);
        model.addAttribute("historicReports", historicReports);
        return new ModelAndView("/entityExtranet/extranetReports :: results");
    }

    @RequestMapping(value = "/reports/funnelReport/getMediums", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "report:funnel:execute", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public Object getMediumParameters(ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response,
                                      @RequestParam(value = "source", required = false) String source) throws Exception {

        List<String> mediums = loanApplicationService.getUtmMediumsBySource(source);
        return AjaxResponse.ok(new Gson().toJson(mediums));
    }

    @RequestMapping(value = "/reports/funnelReport/getCampaings", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "report:funnel:execute", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public Object getCampaignParameters(ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response,
                                        @RequestParam(value = "medium", required = false) String medium,
                                        @RequestParam(value = "source", required = false) String source) throws Exception {

        List<String> campaigns = loanApplicationService.getUtmCampaignsByMedium(source, medium);
        return AjaxResponse.ok(new Gson().toJson(campaigns));
    }

    @RequestMapping(value = "/reports/ripleyGeneradosNoDesembolsados", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "credit:generated:report", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public void ripleyGeneradosNoDesembolsados(
            ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response) throws Exception {

        if (!entityExtranetService.getLoggedUserEntity().containsEntityId(Entity.RIPLEY)) {
            return;
        }

        byte[] file = reportsService.createRipleyReportExcel();

        if (file != null) {
            MediaType contentType = MediaType.valueOf("application/vnd.ms-excel");
            response.setHeader("Content-disposition", "attachment; filename=ripley-reporte-sef-abono.xlsx");
            response.setContentType(contentType.getType());
            response.getOutputStream().write(file);
        }
    }

//    TODO MOVER A CLASE APARTE
    public static class CreateLoanForm extends FormGeneric implements Serializable {

        private Integer docType;
        private String docNumber;
        private Integer category;

        public CreateLoanForm() {
            this.setValidator(new CreateLoanForm.Validator());
        }

        public class Validator extends FormValidator implements Serializable {

            public IntegerFieldValidator docType;
            public StringFieldValidator docNumber;
            public IntegerFieldValidator category;

            public Validator() {
                addValidator(docType = new IntegerFieldValidator(ValidatorUtil.DOC_TYPE_ID));
                addValidator(docNumber = new StringFieldValidator(ValidatorUtil.DOC_NUMBER_DNI));
                addValidator(category = new IntegerFieldValidator().setRequired(true));
            }

            @Override
            protected void setDynamicValidations() {
                if (CreateLoanForm.this.docType == IdentityDocumentType.DNI) {
                    docNumber.update(ValidatorUtil.DOC_NUMBER_DNI);
                } else if (CreateLoanForm.this.docType == IdentityDocumentType.CE) {
                    docNumber.update(ValidatorUtil.DOC_NUMBER_CE);
                }
            }

            @Override
            protected Object getSubclass() {
                return this;
            }

            @Override
            protected Object getFormClass() {
                return CreateLoanForm.this;
            }
        }

        public Integer getDocType() {
            return docType;
        }

        public void setDocType(Integer docType) {
            this.docType = docType;
        }

        public String getDocNumber() {
            return docNumber;
        }

        public void setDocNumber(String docNumber) {
            this.docNumber = docNumber;
        }

        public Integer getCategory() {
            return category;
        }

        public void setCategory(Integer category) {
            this.category = category;
        }
    }

    public static class CreateLoanBancoDelSolForm extends FormGeneric implements Serializable {

        private String destination;
        private String reason;
        private String clientType;
        private String name;
        private String docNumber;
        private String birthdate;
        private String province;
        private String code;
        private String phone;
        private String email;

        public CreateLoanBancoDelSolForm() {
            this.setValidator(new CreateLoanBancoDelSolForm.Validator());
        }

        public class Validator extends FormValidator implements Serializable {

            public StringFieldValidator destination;
            public StringFieldValidator reason;
            public StringFieldValidator clientType;
            public StringFieldValidator name;
            public StringFieldValidator docNumber;
            public StringFieldValidator birthdate;
            public StringFieldValidator province;
            public StringFieldValidator code;
            public StringFieldValidator phone;
            public StringFieldValidator email;

            public Validator() {
                addValidator(destination = new StringFieldValidator().setRequired(true));
                addValidator(reason = new StringFieldValidator().setRequired(true));
                addValidator(clientType = new StringFieldValidator().setRequired(true));
                addValidator(name = new StringFieldValidator().setRequired(false).setRestricted(true).setMaxCharacters(60).setValidRegex(StringFieldValidator.REGEXP_NAMES));
                addValidator(docNumber = new StringFieldValidator(ValidatorUtil.DOC_NUMBER_CUIT));
                addValidator(birthdate = new StringFieldValidator(ValidatorUtil.BIRTHDAY).setRequired(true).setFieldName("Fecha de Nac."));
                addValidator(province = new StringFieldValidator(ValidatorUtil.PROVINCE));
                addValidator(code = new StringFieldValidator(ValidatorUtil.ARGENTINA_PHONE_AREA_CODE).setMinCharacters(2).setMaxCharacters(4));
                addValidator(phone = new StringFieldValidator(ValidatorUtil.CELLPHONE_NUMBER_ARGENTINA).setMinCharacters(7).setMaxCharacters(9));
                addValidator(email = new StringFieldValidator(ValidatorUtil.EMAIL).setRequired(true).setFieldName("Correo Electronico"));
            }

            @Override
            protected void setDynamicValidations() {
                phone.setMaxCharacters(11 - (CreateLoanBancoDelSolForm.this.code != null ? CreateLoanBancoDelSolForm.this.code.length() : 0));
                phone.setMinCharacters(11 - (CreateLoanBancoDelSolForm.this.code != null ? CreateLoanBancoDelSolForm.this.code.length() : 0));
            }

            @Override
            protected Object getSubclass() {
                return this;
            }

            @Override
            protected Object getFormClass() {
                return CreateLoanBancoDelSolForm.this;
            }

        }

        public String getDestination() {
            return destination;
        }

        public void setDestination(String destination) {
            this.destination = destination;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }

        public String getClientType() {
            return clientType;
        }

        public void setClientType(String clientType) {
            this.clientType = clientType;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDocNumber() {
            return docNumber;
        }

        public void setDocNumber(String docNumber) {
            this.docNumber = docNumber;
        }

        public String getBirthdate() {
            return birthdate;
        }

        public void setBirthdate(String birthdate) {
            this.birthdate = birthdate;
        }

        public String getProvince() {
            return province;
        }

        public void setProvince(String province) {
            this.province = province;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }

    @RequestMapping(value = "/entity/excel", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "report:create", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    @ResponseBody
    public void extranetExportCreditExcel(
            ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response) throws Exception {

        CSVWriter writer = new CSVWriter();
        String reportName = "ReporteCreditosGenerados.csv";
        JSONArray jsonArray = entityExtranetService.getFullCreditInfo(entityExtranetService.getLoggedUserEntity().getEntities().get(0).getId());
        response.setContentType("text/csv");
        response.setHeader("Content-disposition", "attachment;filename=" + reportName);
        if (jsonArray != null && jsonArray.length() > 0) {
            JsonFlattener parser = new JsonFlattener();
            List<Map<String, String>> flatJson = parser.parseJson(jsonArray.toString());
            response.getOutputStream().write(writer.writeAsCSV(flatJson, reportName).getBytes());
        } else {
            response.getOutputStream().write(new String("No hay registros a mostrar").getBytes());
        }
    }

    @RequestMapping(value = "/entity/credits", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "report:create", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    @ResponseBody
    public void extranetExportCredit(
            ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-disposition", "attachment; filename=Reporte de Créditos.xlsx");
        response.getOutputStream().write(reportsService.createLoanDetailReport());
    }

    @RequestMapping(value = "/affiliators", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "menu:affiliator:register:view", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    @ResponseBody
    public Object extranetAffiliators(
            ModelMap modelMap, Locale locale, HttpServletRequest request, HttpServletResponse response
    ) throws Exception {

        modelMap.addAttribute("form", new AffiliatorForm());
        modelMap.addAttribute("banks", catalogService.getBanks(false));
        modelMap.addAttribute("locale", locale);

        return new ModelAndView("/entityExtranet/registerAffiliator");
    }

    @RequestMapping(value = "/newLoanApplication", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "menu:loanAffiliator:register:view", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public Object showRegisterLoanApplication(
            ModelMap model, Locale locale, HttpServletRequest request) throws Exception {

        CreateNewLoanForm form = new CreateNewLoanForm();
        ((CreateNewLoanForm.Validator) form.getValidator()).amount.setMinValue(productService.getMinAmount(ProductCategory.CONSUMO, 51));
        ((CreateNewLoanForm.Validator) form.getValidator()).amount.setMaxValue(productService.getMaxAmount(ProductCategory.CONSUMO, 51));
        ((CreateNewLoanForm.Validator) form.getValidator()).installments.setMinValue(productService.getMinInstalments(ProductCategory.CONSUMO, 51));
        ((CreateNewLoanForm.Validator) form.getValidator()).installments.setMaxValue(productService.getMaxInstalments(ProductCategory.CONSUMO, 51));

        model.addAttribute("form", form);
        model.addAttribute("loanApplications", loanApplicationDao.getLoanApplicationsByEntityUser(locale, entityExtranetService.getLoggedUserEntity().getId(), ExtranetPainterLoanApplication.class));
        return new ModelAndView("/entityExtranet/registerLoanApplication");
    }

    @RequestMapping(value = "/newLoanApplication", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "loanAffiliator:register:store", type = RequiresPermissionOr403.Type.AJAX, saveLog = true, webApp = WebApplication.ENTITY_EXTRANET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public Object showRegisterLoanApplicationPost(
            ModelMap model, Locale locale, HttpServletRequest request, CreateNewLoanForm form) throws Exception {

        ((CreateNewLoanForm.Validator) form.getValidator()).amount.setMinValue(productService.getMinAmount(ProductCategory.CONSUMO, 51));
        ((CreateNewLoanForm.Validator) form.getValidator()).amount.setMaxValue(productService.getMaxAmount(ProductCategory.CONSUMO, 51));
        ((CreateNewLoanForm.Validator) form.getValidator()).installments.setMinValue(productService.getMinInstalments(ProductCategory.CONSUMO, 51));
        ((CreateNewLoanForm.Validator) form.getValidator()).installments.setMaxValue(productService.getMaxInstalments(ProductCategory.CONSUMO, 51));

        form.getValidator().validate(locale);
        if (form.getValidator().isHasErrors()) {
            return AjaxResponse.errorFormValidation(form.getValidator().getErrorsJson());
        }

        User user = userService.getOrRegisterUser(form.getDocType(), form.getDocNumber(), null, null, null);

        if (user.getEmail() != null && !user.getEmail().equals(form.getEmail())) {
            return AjaxResponse.errorMessage("El email no coincide con el registrado.");
        }

        boolean loanExisted = true;
        LoanApplication currentLoanApplication = loanApplicationDao.getActiveLoanApplicationByPerson(locale, user.getPersonId(), ProductCategory.CONSUMO);
        if (currentLoanApplication == null) {
            loanExisted = false;
            currentLoanApplication = loanApplicationDao.registerLoanApplication(
                    user.getId(),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    LoanApplication.ORIGIN_AFFILIATOR,
                    null,
                    null,
                    null,
                    CountryParam.COUNTRY_PERU);
            loanApplicationDao.updateProductCategory(currentLoanApplication.getId(), ProductCategory.CONSUMO);
            loanApplicationDao.updateFormAssistant(currentLoanApplication.getId(), catalogService.getFormAssistantsAgents(null).get(0).getId());
            loanApplicationDao.updateReason(currentLoanApplication.getId(), form.getReason());
            loanApplicationDao.updateCurrentQuestion(currentLoanApplication.getId(), ProcessQuestion.Question.Constants.IOVATION_AFTER_PREEVALUATION);
            loanApplicationDao.updateInstallments(currentLoanApplication.getId(), form.getInstallments());
            loanApplicationDao.updateAmount(currentLoanApplication.getId(), form.getAmount());
            loanApplicationDao.updateEntityUser(currentLoanApplication.getId(), entityExtranetService.getLoggedUserEntity().getId());
            loanApplicationDao.updateEvaluationProcessReadyPreEvaluation(currentLoanApplication.getId(), true);

            currentLoanApplication = loanApplicationDao.getLoanApplication(currentLoanApplication.getId(), locale);

            int emailId = userDAO.registerEmailChange(currentLoanApplication.getUserId(), form.getEmail().toLowerCase());
            userDAO.validateEmailChange(currentLoanApplication.getUserId(), emailId);

            loanApplicationService.runEvaluationBot(currentLoanApplication.getId(), true);
        } else {
            return AjaxResponse.errorMessage("El solicitante tiene otra solicitud en proceso en ese momento.");
        }

        return AjaxResponse.redirect(request.getContextPath() + "/newLoanApplication");
    }

    @RequestMapping(value = "/affiliators", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "affiliator:register:store", type = RequiresPermissionOr403.Type.AJAX, saveLog = true, webApp = WebApplication.ENTITY_EXTRANET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public Object extranetRegiterAffiliators(
            ModelMap modelMap,
            Locale locale,
            HttpServletRequest request,
            HttpServletResponse response,
            AffiliatorForm form
    ) throws Exception {
        form.getValidator().validate(locale);
        if (form.getValidator().isHasErrors()) {
            return AjaxResponse.errorFormValidation(form.getValidator().getErrorsJson());
        }

        String ubigeoId = form.getDepartamento() != null && form.getProvincia() != null && form.getDistrito() != null
                ? form.getDepartamento() + form.getProvincia() + form.getDistrito() : null;

        LoggedUserEntity loggedUserEntity = entityExtranetService.getLoggedUserEntity();
        UserEntity userEntity = userDAO.getUserEntityByEmail(form.getEmail(), locale, loggedUserEntity.getPrincipalEntity().getId());

        if (userEntity != null) {
            return AjaxResponse.errorMessage("El email ya se encuentra registrado.");
        }

        originatorDAO.registerAffiliator(form.getName(), form.getRuc(), form.getEmail(), form.getPhoneNumber(), ubigeoId, form.getBankId(), form.getBankAccountNumber(), null, null);

        return AjaxResponse.redirect(request.getContextPath() + "/affiliators");
    }

//    TODO MOVE FROM HERE
    public static class AffiliatorForm extends FormGeneric implements Serializable {
        private String name;
        private String ruc;
        private String email;
        private String phoneNumber;
        private String bankAccountNumber;
        private Integer bankId;
        private String departamento;
        private String provincia;
        private String distrito;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getRuc() {
            return ruc;
        }

        public void setRuc(String ruc) {
            this.ruc = ruc;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public Integer getBankId() {
            return bankId;
        }

        public void setBankId(Integer bankId) {
            this.bankId = bankId;
        }

        public String getBankAccountNumber() {
            return bankAccountNumber;
        }

        public void setBankAccountNumber(String bankAccountNumber) {
            this.bankAccountNumber = bankAccountNumber;
        }

        public String getDepartamento() {
            return departamento;
        }

        public void setDepartamento(String departamento) {
            this.departamento = departamento;
        }

        public String getProvincia() {
            return provincia;
        }

        public void setProvincia(String provincia) {
            this.provincia = provincia;
        }

        public String getDistrito() {
            return distrito;
        }

        public void setDistrito(String distrito) {
            this.distrito = distrito;
        }

        public AffiliatorForm() {
            setValidator(new AffiliatorForm.Validator());
        }

        public class Validator extends FormValidator implements Serializable {

            public StringFieldValidator email;
            public StringFieldValidator ruc;
            public StringFieldValidator name;
            public StringFieldValidator phoneNumber;
            public StringFieldValidator bankAccountNumber;
            public StringFieldValidator departamento;
            public StringFieldValidator provincia;
            public StringFieldValidator distrito;

            public Validator() {
                addValidator(email = new StringFieldValidator(ValidatorUtil.EMAIL).setFieldName("Correo electrónico").setRequired(true));
                addValidator(ruc = new StringFieldValidator(ValidatorUtil.RUC).setFieldName("RUC").setRequired(false));
                addValidator(name = new StringFieldValidator(ValidatorUtil.COMPANY_NAME).setFieldName("Nombre del Comercio").setRequired(true));
                addValidator(phoneNumber = new StringFieldValidator(ValidatorUtil.CELLPHONE_NUMBER).setFieldName("Celular").setRequired(true));
                addValidator(bankAccountNumber = new StringFieldValidator(ValidatorUtil.BANK_ACCOUNT_NUMBER).setFieldName("Cuenta Bancaria").setRequired(false));
                addValidator(departamento = new StringFieldValidator(ValidatorUtil.DEPARTMENT).setFieldName("Departamento").setRequired(true));
                addValidator(provincia = new StringFieldValidator(ValidatorUtil.PROVINCE).setFieldName("Provincia").setRequired(true));
                addValidator(distrito = new StringFieldValidator(ValidatorUtil.DISTRICT).setFieldName("Distrito").setRequired(true));
            }

            @Override
            protected void setDynamicValidations() {

            }

            @Override
            protected Object getSubclass() {
                return this;
            }

            @Override
            protected Object getFormClass() {
                return AffiliatorForm.this;
            }
        }
    }

    public static class CreateNewLoanForm extends FormGeneric implements Serializable {

        private Integer docType;
        private String docNumber;
        private Integer reason;
        private Integer amount;
        private Integer installments;
        private String email;

        public CreateNewLoanForm() {
            this.setValidator(new CreateNewLoanForm.Validator());
        }

        public class Validator extends FormValidator implements Serializable {

            public IntegerFieldValidator docType;
            public StringFieldValidator docNumber;
            public IntegerFieldValidator reason;
            public IntegerFieldValidator amount;
            public IntegerFieldValidator installments;
            public StringFieldValidator email;

            public Validator() {
                addValidator(docType = new IntegerFieldValidator(ValidatorUtil.DOC_TYPE_ID).setFieldName("Tipo de Documento"));
                addValidator(docNumber = new StringFieldValidator(ValidatorUtil.DOC_NUMBER_DNI).setFieldName("Número de Documento"));
                addValidator(reason = new IntegerFieldValidator().setRequired(true).setFieldName("Motivo"));
                addValidator(amount = new IntegerFieldValidator(ValidatorUtil.SELF_EVALUATION_AMOUNT).setMaxValueErrorMsg("validation.int.maxValueMoney").setMinValueErrorMsg("validation.int.minValueMoney").setFieldName("monto"));
                addValidator(installments = new IntegerFieldValidator(ValidatorUtil.SELF_EVALUATION_INSTALLMENTS).setFieldName("plazo"));
                addValidator(email = new StringFieldValidator(ValidatorUtil.EMAIL).setFieldName("email").setRequired(true));
            }

            @Override
            protected void setDynamicValidations() {
                if (CreateNewLoanForm.this.docType == IdentityDocumentType.DNI) {
                    docNumber.update(ValidatorUtil.DOC_NUMBER_DNI);
                } else if (CreateNewLoanForm.this.docType == IdentityDocumentType.CE) {
                    docNumber.update(ValidatorUtil.DOC_NUMBER_CE);
                }
            }

            @Override
            protected Object getSubclass() {
                return this;
            }

            @Override
            protected Object getFormClass() {
                return CreateNewLoanForm.this;
            }
        }

        public Integer getDocType() {
            return docType;
        }

        public void setDocType(Integer docType) {
            this.docType = docType;
        }

        public String getDocNumber() {
            return docNumber;
        }

        public void setDocNumber(String docNumber) {
            this.docNumber = docNumber;
        }

        public Integer getReason() {
            return reason;
        }

        public void setReason(Integer reason) {
            this.reason = reason;
        }

        public Integer getAmount() {
            return amount;
        }

        public void setAmount(Integer amount) {
            this.amount = amount;
        }

        public Integer getInstallments() {
            return installments;
        }

        public void setInstallments(Integer installments) {
            this.installments = installments;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }

    @RequestMapping(value = "/employers", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "menu:company:register:view", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    @ResponseBody
    public Object extranetEmployers(ModelMap modelMap, Locale locale, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LoggedUserEntity user = entityExtranetService.getLoggedUserEntity();
        List<Employer> employers = employerDAO.getEmployersByEntity(user.getEntities().get(0).getId(), locale);

        modelMap.addAttribute("form", new EmployerForm());
        modelMap.addAttribute("employers", employers);

        return new ModelAndView("/entityExtranet/registerEmployer");
    }

    @RequestMapping(value = "/employers", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "company:register:store", type = RequiresPermissionOr403.Type.AJAX, saveLog = true, webApp = WebApplication.ENTITY_EXTRANET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object extranetRegisterEmployer(ModelMap modelMap, Locale locale, HttpServletRequest request, HttpServletResponse response, EmployerForm form) throws Exception {

        form.getValidator().validate(locale);
        if (form.getValidator().isHasErrors()) {
            return AjaxResponse.errorFormValidation(form.getValidator().getErrorsJson());
        }

        LoggedUserEntity user = entityExtranetService.getLoggedUserEntity();
        JSONArray arrayJsonUsers = new JSONArray();

        if (form.getUser1Email() != null) {
            arrayJsonUsers.put(0, new JSONObject()
                    .put("person_name", form.getUser1Name())
                    .put("email", form.getUser1Email())
                    .put("first_surname", form.getUser1FirstSurname())
                    .put("last_surname", form.getUser1LastSurname()));
        }

        if (form.getUser2Email() != null) {
            arrayJsonUsers.put(1, new JSONObject()
                    .put("person_name", form.getUser2Name())
                    .put("email", form.getUser2Email())
                    .put("first_surname", form.getUser2FirstSurname())
                    .put("last_surname", form.getUser2LastSurname()));
        }

        if (form.getUser3Email() != null) {
            arrayJsonUsers.put(2, new JSONObject()
                    .put("person_name", form.getUser3Name())
                    .put("email", form.getUser3Email())
                    .put("first_surname", form.getUser3FirstSurname())
                    .put("last_surname", form.getUser3LastSurname()));
        }

        employerService.registerEmployer(user.getEntities().get(0).getId(), form.getName(), form.getRuc(), form.getAddress(),
                form.getPhone(), form.getProfession(), form.getCutoffDay(), form.getPaymentDay(), form.getTea(), arrayJsonUsers);
        ehCacheManager.clearAll();
        return AjaxResponse.redirect(request.getContextPath() + "/employers");
    }

    @RequestMapping(value = "/employers/activate", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object activateEmployer(ModelMap modelMap,
                                   @RequestParam("active") Boolean active,
                                   @RequestParam("employerId") Integer employerId) throws Exception {
        LoggedUserEntity user = entityExtranetService.getLoggedUserEntity();

        employerDAO.activateEmployerByEntity(user.getEntities().get(0).getId(), employerId, active);

        return AjaxResponse.ok("");
    }

    @RequestMapping(value = "/employers/updateTea", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object updateEmployerTea(ModelMap modelMap,
                                    @RequestParam("tea") Double tea,
                                    @RequestParam("employerId") Integer employerId) throws Exception {
        LoggedUserEntity user = entityExtranetService.getLoggedUserEntity();

        employerDAO.updateEmployerTeaByEntity(user.getEntities().get(0).getId(), employerId, tea);
        ehCacheManager.clearAll();
        return AjaxResponse.ok("");
    }

    public static class EmployerForm extends FormGeneric implements Serializable {
        private String name;
        private String ruc;
        private String address;
        private String phone;
        private Integer profession;
        private String user1Name;
        private String user1FirstSurname;
        private String user1LastSurname;
        private String user1Email;
        private String user2Name;
        private String user2FirstSurname;
        private String user2LastSurname;
        private String user2Email;
        private String user3Name;
        private String user3FirstSurname;
        private String user3LastSurname;
        private String user3Email;
        private Integer cutoffDay;
        private Integer paymentDay;
        private Double tea;
        private Boolean active;

        public EmployerForm() {
            this.setValidator(new EmployerForm.Validator());
        }

        public class Validator extends FormValidator implements Serializable {
            public StringFieldValidator name;
            public StringFieldValidator ruc;
            public StringFieldValidator address;
            public StringFieldValidator phone;
            public IntegerFieldValidator profession;
            public StringFieldValidator user1Name;
            public StringFieldValidator user1FirstSurname;
            public StringFieldValidator user1LastSurname;
            public StringFieldValidator user1Email;
            public StringFieldValidator user2Name;
            public StringFieldValidator user2FirstSurname;
            public StringFieldValidator user2LastSurname;
            public StringFieldValidator user2Email;
            public StringFieldValidator user3Name;
            public StringFieldValidator user3FirstSurname;
            public StringFieldValidator user3LastSurname;
            public StringFieldValidator user3Email;
            public IntegerFieldValidator cutoffDay;
            public IntegerFieldValidator paymentDay;
            public DoubleFieldValidator tea;

            public Validator() {
                addValidator(name = new StringFieldValidator().setRequired(true).setFieldName("Nombre"));
                addValidator(ruc = new StringFieldValidator(ValidatorUtil.RUC).setRequired(true).setFieldName("RUC"));
                addValidator(address = new StringFieldValidator().setRequired(true).setFieldName("Direccion"));
                addValidator(phone = new StringFieldValidator(ValidatorUtil.PHONE_OR_CELLPHONE).setRequired(true).setFieldName("Telefono"));
                addValidator(profession = new IntegerFieldValidator().setRequired(true).setFieldName("Rubro"));
                // User 1
                addValidator(user1Name = new StringFieldValidator().setRequired(true).setFieldName("Nombre del usuario 1"));
                addValidator(user1FirstSurname = new StringFieldValidator().setRequired(true).setFieldName("Apellido paterno del usuario 1"));
                addValidator(user1LastSurname = new StringFieldValidator().setRequired(true).setFieldName("Apellido materno del usuario 1"));
                addValidator(user1Email = new StringFieldValidator(ValidatorUtil.EMAIL).setRequired(true).setFieldName("Email del usuario 1"));
                // User 2
                addValidator(user2Name = new StringFieldValidator().setRequired(false).setFieldName("Nombre del usuario 2"));
                addValidator(user2FirstSurname = new StringFieldValidator().setRequired(false).setFieldName("Apellido paterno del usuario 2"));
                addValidator(user2LastSurname = new StringFieldValidator().setRequired(false).setFieldName("Apellido materno del usuario 2"));
                addValidator(user2Email = new StringFieldValidator(ValidatorUtil.EMAIL).setRequired(false).setFieldName("Email del usuario 2"));
                // User 3
                addValidator(user3Name = new StringFieldValidator().setRequired(false).setFieldName("Nombre del usuario 3"));
                addValidator(user3FirstSurname = new StringFieldValidator().setRequired(false).setFieldName("Apellido paterno del usuario 3"));
                addValidator(user3LastSurname = new StringFieldValidator().setRequired(false).setFieldName("Apellido materno del usuario 3"));
                addValidator(user3Email = new StringFieldValidator(ValidatorUtil.EMAIL).setRequired(false).setFieldName("Email del usuario 3"));

                addValidator(cutoffDay = new IntegerFieldValidator().setRequired(true).setFieldName("Fecha de Corte"));
                addValidator(paymentDay = new IntegerFieldValidator().setRequired(true).setFieldName("Fecha de Pago"));
                addValidator(tea = new DoubleFieldValidator().setRequired(true).setFieldName("Tasa de interes"));
            }

            @Override
            protected void setDynamicValidations() {
            }

            @Override
            protected Object getSubclass() {
                return this;
            }

            @Override
            protected Object getFormClass() {
                return EmployerForm.this;
            }
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getRuc() {
            return ruc;
        }

        public void setRuc(String ruc) {
            this.ruc = ruc;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public Integer getProfession() {
            return profession;
        }

        public void setProfession(Integer profession) {
            this.profession = profession;
        }

        public String getUser1Name() {
            return user1Name;
        }

        public void setUser1Name(String user1Name) {
            this.user1Name = user1Name;
        }

        public String getUser1FirstSurname() {
            return user1FirstSurname;
        }

        public void setUser1FirstSurname(String user1FirstSurname) {
            this.user1FirstSurname = user1FirstSurname;
        }

        public String getUser1LastSurname() {
            return user1LastSurname;
        }

        public void setUser1LastSurname(String user1LastSurname) {
            this.user1LastSurname = user1LastSurname;
        }

        public String getUser1Email() {
            return user1Email;
        }

        public void setUser1Email(String user1Email) {
            this.user1Email = user1Email;
        }

        public String getUser2Name() {
            return user2Name;
        }

        public void setUser2Name(String user2Name) {
            this.user2Name = user2Name;
        }

        public String getUser2FirstSurname() {
            return user2FirstSurname;
        }

        public void setUser2FirstSurname(String user2FirstSurname) {
            this.user2FirstSurname = user2FirstSurname;
        }

        public String getUser2LastSurname() {
            return user2LastSurname;
        }

        public void setUser2LastSurname(String user2LastSurname) {
            this.user2LastSurname = user2LastSurname;
        }

        public String getUser2Email() {
            return user2Email;
        }

        public void setUser2Email(String user2Email) {
            this.user2Email = user2Email;
        }

        public String getUser3Name() {
            return user3Name;
        }

        public void setUser3Name(String user3Name) {
            this.user3Name = user3Name;
        }

        public String getUser3FirstSurname() {
            return user3FirstSurname;
        }

        public void setUser3FirstSurname(String user3FirstSurname) {
            this.user3FirstSurname = user3FirstSurname;
        }

        public String getUser3LastSurname() {
            return user3LastSurname;
        }

        public void setUser3LastSurname(String user3LastSurname) {
            this.user3LastSurname = user3LastSurname;
        }

        public String getUser3Email() {
            return user3Email;
        }

        public void setUser3Email(String user3Email) {
            this.user3Email = user3Email;
        }

        public Integer getCutoffDay() {
            return cutoffDay;
        }

        public void setCutoffDay(Integer cutoffDay) {
            this.cutoffDay = cutoffDay;
        }

        public Integer getPaymentDay() {
            return paymentDay;
        }

        public void setPaymentDay(Integer paymentDay) {
            this.paymentDay = paymentDay;
        }

        public Double getTea() {
            return tea;
        }

        public void setTea(Double tea) {
            this.tea = tea;
        }

        public Boolean getActive() {
            return active;
        }

        public void setActive(Boolean active) {
            this.active = active;
        }
    }

    @RequestMapping(value = "/vehicles", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "menu:vehicle:upload:view", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    @ResponseBody
    public Object extranetVehicles(ModelMap modelMap, Locale locale, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LoggedUserEntity user = entityExtranetService.getLoggedUserEntity();

        if (isValidAccesoUser(user)) {
            List<Employer> employers = employerDAO.getEmployersByEntity(user.getEntities().get(0).getId(), locale);
            List<GuaranteedVehicle> vehicleList = guaranteedVehicleDAO.getGuaranteedVehicles();
            modelMap.addAttribute("form", new EmployerForm());
            modelMap.addAttribute("vehicleList", vehicleList);
            return new ModelAndView("/entityExtranet/extranetVehicleSheet");
        } else {
            return "500";
        }
    }

    @RequestMapping(value = "/vehicles/file", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "vehicle:upload:store", type = RequiresPermissionOr403.Type.AJAX, saveLog = true, webApp = WebApplication.ENTITY_EXTRANET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public Object extranetLoadVehicles(ModelMap modelMap, Locale locale, HttpServletRequest request,
                                       HttpServletResponse response, @RequestParam("file") MultipartFile[] file
    ) throws Exception {
        LoggedUserEntity user = entityExtranetService.getLoggedUserEntity();
        if (isValidAccesoUser(user)) {
            SecurityUtils.getSubject().getSession().setAttribute("vehiclesToAdd", null);

            if (file == null || file.length < 1)
                return AjaxResponse.errorMessage("No se subio ningun excel. Int&eacute;ntalo nuevamente.");

            JSONObject resJson = null;
            try {
                resJson = vehicleSheetService.getJsonVehicles(file);
            } catch (BiffException | IOException ex) {
                return AjaxResponse.errorMessage("El formato del archivo Excel no es el correcto. Intentar con Excel 97-2003 - Extensión .xls");
            }

            return AjaxResponse.ok(resJson.toString());
        } else {
            return AjaxResponse.errorMessage("no tiene permisos");
        }
    }

    @RequestMapping(value = "/vehicles/file/confirm", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "vehicle:upload:store", type = RequiresPermissionOr403.Type.AJAX, saveLog = true, webApp = WebApplication.ENTITY_EXTRANET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public ResponseEntity<String> extranetSaveVehicles(
            Locale locale,
            @RequestParam(value = "vehiclesToUpload", required = false) String vehiclesToUpload,
            @RequestParam("disablePrevious") Boolean disablePrevious) throws Exception {
        LoggedUserEntity user = entityExtranetService.getLoggedUserEntity();
        if (isValidAccesoUser(user)) {
            JSONObject jsonResponse = null;
            jsonResponse = vehicleSheetService.saveVehicles(vehiclesToUpload);
            if (jsonResponse != null)
                return AjaxResponse.ok(jsonResponse.toString());
            return AjaxResponse.errorMessage("");
        } else {
            return AjaxResponse.errorMessage("no tiene permisos");
        }
    }

    @RequestMapping(value = "/vehicles/excel/template", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    @ResponseBody
    public void extranetImportPaysheetExcelTemplate(
            ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LoggedUserEntity user = entityExtranetService.getLoggedUserEntity();
        if (isValidAccesoUser(user)) {
            response.setHeader("Content-disposition", "attachment; filename=Campos_vehiculos.xls");
            response.setContentType("application/vnd.ms-excel");
            vehicleSheetService.createImportEmployeesExcelTemplate(response.getOutputStream());
        } else {
            return;
        }
    }

    @RequestMapping(value = "/agenda", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "menu:vehicle:appointments:view", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    @ResponseBody
    public Object extranetCalendar(ModelMap modelMap) throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        Date startDate = calendar.getTime();
        calendar.add(Calendar.DATE, 6);
        calendar.add(Calendar.SECOND, -1);
        Date endDate = calendar.getTime();
        List<ExtranetDate> extranetDates = extranetDateDAO.getExtranetDates(startDate, endDate);
        modelMap.addAttribute("extranetDates", new Gson().toJson(extranetDates));
        return new ModelAndView("/entityExtranet/extranetDates");
    }

    boolean isValidAccesoUser(LoggedUserEntity user) {
        for (Entity entity : user.getEntities()) {
            if (entity.getId() == Entity.ACCESO) {
                return true;
            }
        }
        return false;
    }

    @RequestMapping(value = "/updateTfaLogin", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "user:management:tfa", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object activateTfaLogin(
            ModelMap modelMap, Locale locale,
            @RequestParam("value") String value) throws Exception {
        boolean valueToUpdate = Boolean.parseBoolean(value);
        int entityId = entityExtranetService.getLoggedUserEntity().getEntities().get(0).getId();
        entityExtranetService.activateTfaLogin(entityId, valueToUpdate, locale);
        catalogService.getEntity(entityId).setTfaLogin(valueToUpdate);
        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = "/udpatePermissions", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "user:management:permission", type = RequiresPermissionOr403.Type.AJAX, saveLog = true, webApp = WebApplication.ENTITY_EXTRANET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object updateUserPermissions(
            Locale locale,
            @RequestParam("roles") String rolesAndProducts,
            @RequestParam("entityUserId") Integer entityUserId) throws Exception {

        List<Integer> loggedUserEntityIds = entityExtranetService.getLoggedUserEntity().getEntities().stream().map(Entity::getId).collect(Collectors.toList());

        UserEntity userEntityToUpdate = userDAO.getUserEntityById(entityUserId, locale);
        if (userEntityToUpdate.getEntities().stream().noneMatch(e -> loggedUserEntityIds.contains(e.getId()))) {
            return AjaxResponse.errorMessage("El usuario no pertenece a tu empresa");
        }

        List<ExtranetMenuEntity> extranetMenuEntities = securityDAO.getExtranetMenuEntities(entityExtranetService.getLoggedUserEntity().getPrincipalEntity().getId());
        JSONArray rolesArray = new JSONArray(rolesAndProducts);
        List<Integer> rolesToUpdate = new ArrayList<>();

        List<EntityExtranetUser.MenuEntityProductCategory> entityProductCategoriesToUpdate = new ArrayList<>();

        for(int i=0;i<rolesArray.length(); i++){
            EntityExtranetUser.MenuEntityProductCategory menuEntityProductCategory = new EntityExtranetUser.MenuEntityProductCategory();
            JSONObject roleJson = rolesArray.getJSONObject(i);
            if(roleJson.has("extranetMenuEntityId")) menuEntityProductCategory.setMenuEntityId(roleJson.getInt("extranetMenuEntityId"));
            if(roleJson.has("roleGroups")){
                JSONArray roleGroupsArray =roleJson.getJSONArray("roleGroups");
                if(roleGroupsArray.length() > 0){
                    menuEntityProductCategory.setRoleGroups(new ArrayList<>());
                    List<String> roleGroupCodes = new ArrayList<>();
                    for(int j=0;j<roleGroupsArray.length();j++){
                        roleGroupCodes.add(roleGroupsArray.getString(j));
                    }
                    menuEntityProductCategory.setRoleGroups(roleGroupCodes);
                    List<Integer> rolesToupdate = extranetMenuEntities.stream()
                            .filter(e -> e.getExtranetMenu().getId().equals(roleJson.getInt("extranetMenuEntityId")))
                            .map(e -> e.getroleGroupToShow())
                            .flatMap(Collection::stream)
                            .filter(rg -> roleGroupCodes.stream().anyMatch(r -> r.equalsIgnoreCase(rg.getCode())))
                            .map(rg -> rg.getRoles())
                            .flatMap(Collection::stream)
                            .map(r-> r.getId())
                            .collect(Collectors.toList());
                    rolesToUpdate.addAll(rolesToupdate);
                }
            }
            if(roleJson.has("entityProductCategories")){
                JSONArray entityProductCategoriesGroupsArray =roleJson.getJSONArray("entityProductCategories");
                if(entityProductCategoriesGroupsArray.length() > 0){
                    List<Integer> entityProductCategoriesGroupIds = new ArrayList<>();
                    for(int j=0;j<entityProductCategoriesGroupsArray.length();j++){
                        entityProductCategoriesGroupIds.add(entityProductCategoriesGroupsArray.getInt(j));
                    }
                    menuEntityProductCategory.setProductCategories(entityProductCategoriesGroupIds);
                }
            }
            if(menuEntityProductCategory.getMenuEntityId() != null) entityProductCategoriesToUpdate.add(menuEntityProductCategory);
        }
        entityExtranetDao.registerEntityUserRole(entityUserId, rolesToUpdate);
        entityExtranetDao.updateEntityUserInformation(entityUserId,entityExtranetService.getLoggedUserEntity().getPrincipalEntity().getId(),entityProductCategoriesToUpdate);

        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = "/activityLog", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "menu:user:logs:view", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public Object activityLog(
            ModelMap model, Locale locale, HttpServletRequest request) throws Exception {
        Integer offset = 0;
        LoggedUserEntity user = entityExtranetService.getLoggedUserEntity();

        List<EntityExtranetUserActionLog> totalUserActionLog = new ArrayList<>();
        for (Entity entity : user.getEntities()) {
            List<EntityExtranetUserActionLog> userActionLogs = entityExtranetService.getUserActionsLog(entity.getId(), null, offset, null);
            if (userActionLogs != null && userActionLogs.size() > 0) {
                totalUserActionLog.addAll(userActionLogs);
            }
        }
        List<EntityExtranetUserActionLog> userActionsList = new ArrayList<>();
        int listSize = totalUserActionLog.size();
        for (Entity entity : user.getEntities()) {
            List<EntityExtranetUserActionLog> userActionLogs = entityExtranetService.getUserActionsLog(entity.getId(), null, offset, 200);
            if (userActionLogs != null && userActionLogs.size() > 0) {
                userActionsList.addAll(userActionLogs);
            }
        }
        model.addAttribute("userActionsList", userActionsList);
        model.addAttribute("page", "activityLog");
        model.addAttribute("title", "Log de acciones");
        model.addAttribute("listSize", listSize);
        return new ModelAndView("/entityExtranet/extranetActionsLog");
    }

    @RequestMapping(value = "/activityLog/list", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "menu:user:logs:view", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public Object activityLogList(
            ModelMap model, Locale locale, HttpServletRequest request,
            @RequestParam(value = "listSize", required = false, defaultValue = "0") int listSize,
            @RequestParam(value = "offset", required = false, defaultValue = "0") int offset,
            @RequestParam(value = "limit", required = false, defaultValue = "200") int limit) throws Exception {
        LoggedUserEntity user = entityExtranetService.getLoggedUserEntity();
        List<EntityExtranetUserActionLog> userActionsList = new ArrayList<>();
        for (Entity entity : user.getEntities()) {
            List<EntityExtranetUserActionLog> userActionLogs = entityExtranetService.getUserActionsLog(entity.getId(), null, offset, limit);
            if (userActionLogs != null && userActionLogs.size() > 0) {
                userActionsList.addAll(userActionLogs);
            }
        }
        model.addAttribute("userActionsList", userActionsList);
        model.addAttribute("page", "activityLog");
        model.addAttribute("title", "Log de acciones");
        model.addAttribute("listSize", listSize);
        return new ModelAndView("/entityExtranet/extranetActionsLog::list");
    }

    @RequestMapping(value = "/userManagement", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "menu:user:management:view", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public Object userManagment(ModelMap model) throws Exception {
        Entity pricipalEntity = entityExtranetService.getLoggedUserEntity().getPrincipalEntity();
        List<ExtranetMenuEntity> extranetMenuEntities = securityDAO.getExtranetMenuEntities(pricipalEntity.getId());

        model.addAttribute("extranetMenuEntities", extranetMenuEntities);
        model.addAttribute("userList", entityExtranetService.getEntityUsersForPermissionModification());
        model.addAttribute("entity", pricipalEntity);
        model.addAttribute("page", "userManagement");
        model.addAttribute("title", "Administracion de usuarios");
        RegisterEntityUserForm registerEntityUserForm = new RegisterEntityUserForm();
        model.addAttribute("registerEntityUserForm", registerEntityUserForm);
        return new ModelAndView("/entityExtranet/extranetUserManagement");
    }

    @RequestMapping(value = "/createUser", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object createUser(
            ModelMap modelMap, Locale locale,
            RegisterEntityUserForm registerEntityUserForm) throws Exception {
        registerEntityUserForm.getValidator().validate(locale);
        if (registerEntityUserForm.getValidator().isHasErrors()) {
            return AjaxResponse.errorFormValidation(registerEntityUserForm.getValidator().getErrorsJson());
        }
        int entityId = entityExtranetService.getLoggedUserEntity().getEntities().get(0).getId();
        String message = entityExtranetService.registerUserEntity(entityId,
                registerEntityUserForm.getName(),
                registerEntityUserForm.getFirstSurname(),
                registerEntityUserForm.getEmail(), locale);

        if (message.equals("entity_user.exists")) {
            return AjaxResponse.errorMessage("El email ingresado ya se encuentra registrado");
        }
        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = "/welcome", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public Object showWelcome(
            ModelMap model, Locale locale, HttpServletRequest request) throws Exception {
        LoggedUserEntity user = entityExtranetService.getLoggedUserEntity();
        List<Integer> roles = entityClDAO.getRolesByEntityUser(user.getId());

        model.addAttribute("user", user.getName());
        model.addAttribute("page", "welcome");
        model.addAttribute("title", "Bienvenido");
        if (roles == null)
            model.addAttribute("message", "Por el momento no cuentas con ningún permiso");
        return new ModelAndView("/entityExtranet/welcome");
    }

    @RequestMapping(value = "/address/province", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object getProvinces(
            ModelMap model, Locale locale, HttpServletRequest request,
            @RequestParam("departmentId") String departmentId) throws Exception {

        Map<String, Object> params = new HashMap<>();
        params.put("departmentId", departmentId);
        return question22Service.customMethod("province", QuestionFlowService.Type.LOANAPPLICATION, null, locale, params);
    }

    @RequestMapping(value = "/address/district", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object getDistricts(
            ModelMap model, Locale locale, HttpServletRequest request,
            @RequestParam("departmentId") String departmentId,
            @RequestParam("provinceId") String provinceId) throws Exception {

        Map<String, Object> params = new HashMap<>();
        params.put("departmentId", departmentId);
        params.put("provinceId", provinceId);
        return question22Service.customMethod("district", QuestionFlowService.Type.LOANAPPLICATION, null, locale, params);
    }

    @RequestMapping(value = "/loan_progress", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object getLoanProgress(
            ModelMap modelMap, Locale locale, @RequestParam(required = false,value = "tray") Integer tray) throws Exception {
        LoggedUserEntity loggedUserEntity = entityExtranetService.getLoggedUserEntity();
        int entityId = loggedUserEntity.getEntities().get(0).getId();
        List<JSONObject> progresses = creditDAO.getLoanProgressesFromEntity(entityId, true);
        return AjaxResponse.ok(progresses != null ? new Gson().toJson(progresses) : null);
    }
}
