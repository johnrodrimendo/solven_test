package com.affirm.acquisition.controller;

import com.affirm.client.dao.EmployerCLDAO;
import com.affirm.client.model.form.*;
import com.affirm.client.service.EmployerService;
import com.affirm.client.service.SalaryAdvanceService;
import com.affirm.common.dao.*;
import com.affirm.common.model.annotation.ErrorControllerAnnotation;
import com.affirm.common.model.catalog.*;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.*;
import com.affirm.common.util.*;
import com.affirm.system.configuration.Configuration;
import com.google.api.client.auth.oauth2.TokenResponse;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.thymeleaf.spring4.SpringTemplateEngine;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.*;

@Controller
@Scope("request")
public class SalaryAdvanceController {

    private static final Logger logger = Logger.getLogger(SalaryAdvanceController.class);
    public static final String LOANAPPLICATION_URL = Configuration.SALARY_ADVANCE_CONTROLLER_URL;
    public static final String LANDING_URL = "adelanto-de-sueldo";

    @Autowired
    private UserService userService;
    @Autowired
    private UserDAO userDao;
    @Autowired
    private WebscrapperService webscrapperService;
    @Autowired
    private PersonDAO personDao;
    @Autowired
    private CreditService creditService;
    @Autowired
    private EmployerCLDAO employerDao;
    @Autowired
    private EmployerService employerService;
    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private SalaryAdvanceService salaryAdvanceService;
    @Autowired
    private LoanApplicationService loanApplicationService;
    @Autowired
    private SpringTemplateEngine templateEngine;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private CreditDAO creditDao;
    @Autowired
    private UtilService utilService;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private OauthService oauthService;
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private CountryContextService countryContextService;
    @Autowired
    private NewDAO newDao;

    @RequestMapping(value = "/" + LANDING_URL, method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public ModelAndView showAdelanto(
            ModelMap model, Locale locale, HttpServletRequest request,
            @RequestParam(value = "errorMessage", required = false) String errorMessage) throws Exception {

        return new ModelAndView("redirect:/empresas");
    }


    @RequestMapping(value = "/" + LOANAPPLICATION_URL + "/{loanAplicationToken}", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public Object showLoanApplication(
            ModelMap model, Locale locale, HttpServletRequest request,
            @PathVariable("loanAplicationToken") String loanAplicationToken) throws Exception {

        String decrypted = CryptoUtil.decrypt(loanAplicationToken);
        JSONObject jsonDecrypted = new JSONObject(decrypted);

        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(jsonDecrypted.getInt("loan"), locale);

        // If token has confirmationlink, update the status of loanApplication
        if (loanApplication.getStatus().getId() == LoanApplicationStatus.NEW && jsonDecrypted.has("confirmationlink")) {
            loanApplicationDao.updateLoanApplicationStatus(loanApplication.getId(), LoanApplicationStatus.APPROVED, null);
            loanApplication.setStatus(catalogService.getLoanApplicationStatus(locale, LoanApplicationStatus.APPROVED));
        }

        // If token has email, validate it
        if (jsonDecrypted.has("validationlink")) {
            int userId = loanApplication.getUserId();
            if (jsonDecrypted.has("email")) {
                String email = jsonDecrypted.getString("email");
                int emailId = userDao.registerEmailChange(userId, email.toLowerCase());
                userDao.validateEmailChange(userId, emailId);
            } else if (jsonDecrypted.has("emailid")) {
                userDao.validateEmailChange(userId, jsonDecrypted.getInt("emailid"));
            }
        }

        // Verify that  has status EVAL_APPROVED, else show error page
        switch (loanApplication.getStatus().getId()) {
            case LoanApplicationStatus.NEW:
                // Show error page
                return new ModelAndView("redirect:/" + LANDING_URL);
//                model.addAttribute("message", "Falta validar la solicitud para poder acceder");
//                return new ModelAndView("error");
            case LoanApplicationStatus.APPROVED:
                return new ModelAndView("redirect:/" + LOANAPPLICATION_URL + "/" + loanAplicationToken + "/offer");
            case LoanApplicationStatus.REJECTED_AUTOMATIC:
            case LoanApplicationStatus.REJECTED:
                return new ModelAndView("404");
            case LoanApplicationStatus.APPROVED_SIGNED:
                return new ModelAndView("redirect:/" + LOANAPPLICATION_URL + "/" + loanAplicationToken + "/postSignature");
            case LoanApplicationStatus.EXPIRED:
                ModelAndView modelAndView = new ModelAndView("401");

                Agent agent = loanApplication.getAgent();

                if (agent == null) {
                    int size = catalogService.getFormAssistantsAgents(null).size();

                    int index = new Random().nextInt(size);

                    agent = catalogService.getFormAssistantsAgents(null).get(index);
                }

                modelAndView.addObject("agent", agent);

                return modelAndView;
        }
        return new ModelAndView("404");
    }

    @RequestMapping(value = "/" + LOANAPPLICATION_URL, method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public Object registerLoanApplication(
            ModelMap model, Locale locale, HttpServletRequest request,
            SalaryAdvanceRegisterForm form,
            @RequestParam(value = "source", required = false) String source,
            @RequestParam(value = "medium", required = false) String medium,
            @RequestParam(value = "campaign", required = false) String campaign,
            @RequestParam(value = "term", required = false) String term,
            @RequestParam(value = "content", required = false) String content,
            @RequestParam(value = "gaClientID", required = false) String gaClientID) throws Exception {

        // Validate form fields
        form.getValidator().validate(locale);
        if (form.getValidator().isHasErrors()) {
            return AjaxResponse.errorFormValidation(form.getValidator().getErrorsJson());
        }

        int registerType = form.getEmail() != null ? LoanApplicationRegisterType.EMAIL : LoanApplicationRegisterType.DNI;

        // Check if there are employees asociated
        List<Employee> employees = employeeService.getEmployeesByEmailOrDocumentByProduct(form.getEmail(), form.getDocType(), form.getDocNumber(), Product.SALARY_ADVANCE, locale);
        Integer evaluationId = loanApplicationDao.registerIntent(form.getDocType(), form.getDocNumber());

        if (employees.isEmpty()) {
            JSONObject jsonError = new JSONObject();
            jsonError.put("type", "noEmployee");
            jsonError.put("msg", "No perteneces a una empresa asociada a este producto.<br/>" +
                    "Obtén tu crédito <a href=\"" + request.getContextPath() + "/" + ProductCategory.CONSUMO_CATEGORY_URL + "/" + Configuration.EVALUATION_CONTROLLER_URL + "\">aquí</a>");

            //return AjaxResponse.errorMessage(jsonError.toString());

            String message = "No perteneces a una empresa asociada a este producto.<br/>" + "Obtén tu crédito <a href=\"" + request.getContextPath() + "/" + ProductCategory.CONSUMO_CATEGORY_URL + "/" + Configuration.EVALUATION_CONTROLLER_URL + "\">aquí</a>";
            return AjaxResponse.errorMessage(message);

        }

        // TODO Show to the user all the employees that he is associated, so he can chose one. For now we pick the first one
        Employee employee = employees.get(0);

        if (!employee.getActive()) {

            return AjaxResponse.errorMessage("Lo sentimos, no estas activo en la lista de planilla de tu empresa.");
        }

        // Check there is no rejection reason
        SalaryAdvanceCalculatedAmount advancecalculated = loanApplicationDao.calculateSalaryAdvanceAmmount(evaluationId, employee.getId(), employee.getEmployer().getId(), locale);
        if (advancecalculated.getRejectionReasonKey() != null) {
            return AjaxResponse.errorMessage(messageSource.getMessage(advancecalculated.getRejectionReasonKey(), new String[]{employee.getEmployer().getDaysAfterEndOfMonth() + "", employee.getEmployer().getDaysBeforeEndOfMonth() + ""}, locale));
        }

        // Create an user if it doesnt exist
        User user;
        if (employee.getUserId() == null) {
            // Register the user
            user = userDao.registerUser(null, null, null, employee.getDocType().getId(), employee.getDocNumber(), null);
            // Asociate the employee with the person
            //personDao.updateEmployeePersonId(employee.getId(), user.getPersonId());
            employee.setUserId(user.getId());
            employee.setPersonId(user.getPersonId());
        } else {
            user = userDao.getUser(employee.getUserId());
        }

        // Get the active loanApplication or create it
        LoanApplication loanApplication = loanApplicationDao.getActiveLoanApplicationByPerson(locale, user.getPersonId(), ProductCategory.ADELANTO);
        if (loanApplication == null) {
            loanApplication = salaryAdvanceService.registerLoanApplication(employee, Util.getClientIpAddres(request), 'W', locale, registerType,
                    countryContextService.getCountryParamsByRequest(request).getId());
        } else {
            loanApplicationDao.updateRegisterType(loanApplication.getId(), registerType);
            loanApplication.setRegisterType(catalogService.getLoanApplicationRegisterTypeById(registerType));
        }

        loanApplicationDao.updateSourceMediumCampaign(loanApplication.getId(), source, medium, campaign);
        loanApplicationDao.updateTermContent(loanApplication.getId(), term, content);
        loanApplicationDao.updateGAClientID(loanApplication.getId(), gaClientID);
        loanApplicationDao.updateUserAgent(loanApplication.getId(), request.getHeader("User-Agent"));

        if (form.getEmail() != null) {
            // Send email with the loan application link
            salaryAdvanceService.sendConfirmationMail(loanApplication.getId(), user.getId(), user.getPersonId(), employee.getWorkEmail(), employee.getEmployer(), locale);

            JSONObject json = new JSONObject();
            json.put("message", "Te hemos enviado un correo con el link de tu credito.");
            return AjaxResponse.ok(json.toString());
        } else if (form.getDocType() != null) {
            // Move to the next section to validate the cellphone
            JSONObject json = new JSONObject();
            json.put("loanApplicationToken", loanApplicationService.generateLoanApplicationToken(user.getId(), user.getPersonId(), loanApplication.getId()));
            return AjaxResponse.ok(json.toString());
        } else {
            return AjaxResponse.errorMessage(null);
        }
    }

    @RequestMapping(value = "/" + LANDING_URL + "/cellphone", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public Object validatecellphone(
            ModelMap model, Locale locale, HttpServletRequest request,
            RegisterCellphoneForm form,
            @RequestParam("loanApplicationToken") String loanApplicationToken) throws Exception {

        String decrypted = CryptoUtil.decrypt(loanApplicationToken);
        JSONObject jsonDecrypted = new JSONObject(decrypted);

        // Validate form fields
        form.getValidator().validate(locale);
        if (form.getValidator().isHasErrors()) {
            return AjaxResponse.errorFormValidation(form.getValidator().getErrorsJson());
        }

        User user = userDao.getUser(jsonDecrypted.getInt("user"));
        if (user.getPhoneNumber() != null && user.getPhoneNumber().equals(form.getCellphone()) && user.getPhoneVerified() != null && user.getPhoneVerified()) {
            // Redirect to loanOffer with special param
            Map<String, Object> params = new HashMap<>();
            params.put("confirmationlink", true);
            loanApplicationToken = loanApplicationService.generateLoanApplicationToken(
                    jsonDecrypted.getInt("user"), jsonDecrypted.getInt("person"), jsonDecrypted.getInt("loan"), params);

            return AjaxResponse.redirect(request.getContextPath() + "/" + LOANAPPLICATION_URL + "/" + loanApplicationToken);
        } else {
            // Save cellphone and send token
            userDao.registerPhoneNumber(jsonDecrypted.getInt("user"), form.getCountryCode(), form.getCellphone());
            userService.sendAuthTokenSms(jsonDecrypted.getInt("user"), jsonDecrypted.getInt("person"), form.getCountryCode(), form.getCellphone(), null, jsonDecrypted.getInt("loan"), null, CountryParam.COUNTRY_PERU);
            loanApplicationDao.updateSmsSent(jsonDecrypted.getInt("loan"));
        }

        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = "/" + LOANAPPLICATION_URL + "/resendPin", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public Object resendcellphonePin(
            ModelMap model, Locale locale, HttpServletRequest request,
            @RequestParam("loanApplicationToken") String loanApplicationToken) throws Exception {

        String decrypted = CryptoUtil.decrypt(loanApplicationToken);
        JSONObject jsonDecrypted = new JSONObject(decrypted);

        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(jsonDecrypted.getInt("loan"), locale);
        List<PhoneNumber> userPhoneNumbers = userDao.getAllPhoneNumbersByUser(loanApplication.getUserId());
        PhoneNumber userPhoneNumber = userService.getUserPhoneNumberToVerify(userPhoneNumbers);
        if (!userPhoneNumber.canRetrySms())
            return AjaxResponse.errorMessage("Aún no puedes reenviar el sms");

        try {
            userService.sendAuthTokenInteractionWithProvider(loanApplication.getUserId(), loanApplication.getPersonId(), loanApplication.getCountry().getId() + "", userPhoneNumber.getPhoneNumber(), null, loanApplication.getId(), null, loanApplication.getCountryId(), InteractionType.SMS, InteractionProvider.TWILIO);
            loanApplicationDao.updateSmsSent(loanApplication.getId());
            return AjaxResponse.ok(null);
        } catch (Exception e) {
            userService.sendAuthTokenInteractionWithProvider(loanApplication.getUserId(), loanApplication.getPersonId(), loanApplication.getCountry().getId() + "", userPhoneNumber.getPhoneNumber(), null, loanApplication.getId(), null, loanApplication.getCountryId(), InteractionType.SMS, InteractionProvider.AWS);
            loanApplicationDao.updateSmsSent(loanApplication.getId());
            return AjaxResponse.ok(null);
        }
    }

    @RequestMapping(value = "/" + LOANAPPLICATION_URL + "/voiceCallPin", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public Object voiceCallPin(
            ModelMap model, Locale locale, HttpServletRequest request,
            @RequestParam("loanApplicationToken") String loanApplicationToken) throws Exception {

        String decrypted = CryptoUtil.decrypt(loanApplicationToken);
        JSONObject jsonDecrypted = new JSONObject(decrypted);

        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(jsonDecrypted.getInt("loan"), locale);
        List<PhoneNumber> userPhoneNumbers = userDao.getAllPhoneNumbersByUser(loanApplication.getUserId());
        PhoneNumber userPhoneNumber = userService.getUserPhoneNumberToVerify(userPhoneNumbers);
        if (userPhoneNumber != null && !userPhoneNumber.canRetryCall())
            return AjaxResponse.errorMessage("Aún no puedes reintentar la llamada");

        userService.sendAuthTokenInteractionWithProvider(loanApplication.getUserId(), loanApplication.getPersonId(), loanApplication.getCountry().getId() + "", userPhoneNumber.getPhoneNumberForCall(loanApplication.getCountry().getId()), null, loanApplication.getId(), null, loanApplication.getCountryId(), InteractionType.CALL, null);
        loanApplicationDao.updateSmsSent(loanApplication.getId());
        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = "/" + LANDING_URL + "/cellphone/validate", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public Object validatecellphone(
            ModelMap model, Locale locale, HttpServletRequest request,
            ValidateCellphoneForm form,
            @RequestParam("loanApplicationToken") String loanApplicationToken) throws Exception {

        String decrypted = CryptoUtil.decrypt(loanApplicationToken);
        JSONObject jsonDecrypted = new JSONObject(decrypted);

        // Validate form fields
        form.getValidator().validate(locale);
        if (form.getValidator().isHasErrors()) {
            return AjaxResponse.errorFormValidation(form.getValidator().getErrorsJson());
        }

        // Validate the auth token
        String errorMessage = userService.validateSmsAuthToken(jsonDecrypted.getInt("user"), form.getAuthToken(), locale);
        if (errorMessage != null) {
            return AjaxResponse.errorMessage(errorMessage);
        }

        // Redirect to loanOffer with special param
        Map<String, Object> params = new HashMap<>();
        params.put("confirmationlink", true);
        loanApplicationToken = loanApplicationService.generateLoanApplicationToken(
                jsonDecrypted.getInt("user"), jsonDecrypted.getInt("person"), jsonDecrypted.getInt("loan"), params);

        return AjaxResponse.redirect(request.getContextPath() + "/" + LOANAPPLICATION_URL + "/" + loanApplicationToken);
    }

    @RequestMapping(value = "/" + LOANAPPLICATION_URL + "/{loanApplicationToken}/offer", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public Object showLoanApplicationOffer(
            ModelMap model, Locale locale, HttpServletRequest request,
            @PathVariable("loanApplicationToken") String loanApplicationToken) throws Exception {

        String decrypted = CryptoUtil.decrypt(loanApplicationToken);
        JSONObject jsonDecrypted = new JSONObject(decrypted);

        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(jsonDecrypted.getInt("loan"), locale);
        User user = userDao.getUser(jsonDecrypted.getInt("user"));

        // Validate the status is correct
        if (loanApplication.getStatus().getId() != LoanApplicationStatus.APPROVED) {
            return new ModelAndView("redirect:/" + LOANAPPLICATION_URL + "/" + loanApplicationToken);
        }

        // Validate there is no rejection
        Employee employee = personDao.getEmployeeByPerson(loanApplication.getPersonId(), loanApplication.getEmployer().getId(), locale);
        SalaryAdvanceCalculatedAmount advance = loanApplicationDao.calculateSalaryAdvanceAmmount(employee.getId(), employee.getEmployer().getId(), locale);
        if (advance.getRejectionReasonKey() != null) {
            return "redirect:/adelanto?errorMessage=" + URLEncoder.encode(messageSource.getMessage(advance.getRejectionReasonKey(), new String[]{employee.getEmployer().getDaysAfterEndOfMonth() + "", employee.getEmployer().getDaysBeforeEndOfMonth() + ""}, locale), "UTF-8");
        }

        // Generate the loan offer
        List<LoanOffer> offers = loanApplicationDao.getLoanOffers(loanApplication.getId());
        LoanOffer offer = null;
        if (offers == null) {
            // Update first due date
            EmployerPaymentDay paymentDay = employerService.getEmployerCurrentPaymentDay(employee.getEmployer().getId());
            loanApplicationDao.updateFirstDueDate(loanApplication.getId(), Date.from(paymentDay.getPaymentDay().atStartOfDay(ZoneId.systemDefault()).toInstant()));

            // Generate loan offers
            offer = loanApplicationDao.createLoanOffersSalaryAdvance(loanApplication.getId(), loanApplication.getAmount(), advance.getEntityRates().get(0).getCommission(), employee.getEmployer().getId());
        } else {
            offer = offers.get(0);
        }

        ProcessContactForm processContactForm = new ProcessContactForm();
        model.addAttribute("processContactForm", processContactForm);
        model.addAttribute("offer", offer);
        model.addAttribute("isWeekday", creditService.isDisbursementActive(new Date()));
        model.addAttribute("loanApplication", loanApplication);
        model.addAttribute("loanApplicationRegisterType", loanApplication.getRegisterType().getRegisterTypeId());
        model.addAttribute("minCommission", advance.getEntityRates().get(0).getCommission());
        model.addAttribute("maxCommission", new Double(advance.getEntityRates().get(0).getCommission() * 2));
        model.addAttribute("rates", advance.getEntityRates());
        model.addAttribute("loanApplicationToken", loanApplicationToken);
        model.addAttribute("person", personDao.getPerson(catalogService, locale, loanApplication.getPersonId(), false));
        model.addAttribute("personAddress", personDao.getPersonContactInformation(locale, loanApplication.getPersonId()));
//        model.addAttribute("entityParams", catalogService.getEntityProductParam(loanApplication.getEn));

        if (user.getPhoneNumber() != null) {
            model.addAttribute("phoneNumber", utilService.maskCellphone(user.getPhoneNumber()));
        }

        boolean hasBrowserLocation = !(loanApplication.getNavLatitude() == null || loanApplication.getNavLongitude() == null);
        model.addAttribute("browserLocation", hasBrowserLocation);
        if (!hasBrowserLocation) {
            loanApplicationDao.updateConsentNavGeolocation(loanApplication.getId(), false);
        }
        return "products/salaryadvance/salaryAdvanceOffer";
    }

    @RequestMapping(value = "/" + LOANAPPLICATION_URL + "/{loanAplicationToken}/browserLocation", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public ResponseEntity<String> registerLoanApplicationBrowserLocation(
            Locale locale,
            @RequestParam("latitude") Double latitude,
            @RequestParam("longitude") Double longitude,
            @PathVariable("loanAplicationToken") String loanAplicationToken) throws Exception {

        String decrypted = CryptoUtil.decrypt(loanAplicationToken);
        JSONObject jsonDecrypted = new JSONObject(decrypted);

        // Register the browser location
        userService.registerBrowserUbication(jsonDecrypted.getInt("loan"), latitude, longitude);
        return AjaxResponse.ok(null);
    }


//    @RequestMapping(value = "/" + LOANAPPLICATION_URL + "/{loanAplicationToken}/offer/tcea", method = RequestMethod.GET)
//    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
//    public Object getLoanOfferTcea(
//            ModelMap model, Locale locale, HttpServletRequest request,
//            SalaryAdvanceOfferForm offerForm,
//            @PathVariable("loanAplicationToken") String loanAplicationToken) throws Exception {
//
//        String decrypted = CryptoUtil.decrypt(loanAplicationToken);
//        JSONObject jsonDecrypted = new JSONObject(decrypted);
//
//        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(jsonDecrypted.getInt("loan"), locale);
//
//        // Validate is the correct status
//        if (loanApplication.getStatus().getId() != LoanApplicationStatus.APPROVED) {
//            return AjaxResponse.redirect(request.getContextPath() + "/" + LOANAPPLICATION_URL + "/" + loanAplicationToken);
//        }
//
//        // Validate there is no rejection
//        Employee employee = personDao.getEmployeeByPerson(loanApplication.getPersonId(), loanApplication.getEmployer().getId(), locale);
//        SalaryAdvanceCalculatedAmount advance = loanApplicationDao.calculateSalaryAdvanceAmmount(employee.getId(), employee.getEmployer().getId(), locale);
//        if (advance.getRejectionReasonKey() != null) {
//            return AjaxResponse.errorMessage(messageSource.getMessage(advance.getRejectionReasonKey(), null, locale));
//        }
//
//        // Get the loan offer
//        List<LoanOffer> offers = loanApplicationDao.getLoanOffers(loanApplication.getId());
//        LoanOffer offer = null;
//        if (offers == null) {
//            return AjaxResponse.redirect(request.getContextPath() + "/" + LOANAPPLICATION_URL + "/" + loanAplicationToken);
//        } else {
//            offer = offers.get(0);
//        }
//
//        // Validate the form
////        ((SalaryAdvanceOfferForm.SalaryAdvanceOfferFormValidator) offerForm.getValidator()).amount.setMaxValue(offer.getMaxAmmount().intValue()).setMinValue(offer.getMinAmmount().intValue());
////        ((SalaryAdvanceOfferForm.SalaryAdvanceOfferFormValidator) offerForm.getValidator()).commission.setMinValue(advance.getMinCommission().intValue());
////        offerForm.getValidator().validate(locale);
////        if (offerForm.getValidator().isHasErrors()) {
////            return AjaxResponse.errorFormValidation(offerForm.getValidator().getErrorsJson());
////        }
//
//        // Generate loanofer with new ammount and commision and select it
//        offer = loanApplicationDao.createLoanOffersSalaryAdvance(loanApplication.getId(), offerForm.getAmount(), offerForm.getCommission(), employee.getEmployer().getId());
//
//        JSONObject jsonResponse = new JSONObject();
//        jsonResponse.put("tcea", utilService.percentFormat(offer.getEffectiveAnnualCostRate()));
//        return AjaxResponse.ok(jsonResponse.toString());
//    }

    @RequestMapping(value = "/" + LOANAPPLICATION_URL + "/{loanAplicationToken}/offer/updateOffer", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object updateLoanApplicationOffer(
            ModelMap model, Locale locale, HttpServletRequest request,
            SalaryAdvanceOfferForm offerForm,
            @PathVariable("loanAplicationToken") String loanAplicationToken) throws Exception {

        String decrypted = CryptoUtil.decrypt(loanAplicationToken);
        JSONObject jsonDecrypted = new JSONObject(decrypted);

        offerForm.getValidator().validate(locale);
        if (offerForm.getValidator().isHasErrors()) {
            return AjaxResponse.errorMessage("Los campos no son válidos");
        }

        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(jsonDecrypted.getInt("loan"), locale);

        // Validate is the correct status
        if (loanApplication.getStatus().getId() != LoanApplicationStatus.APPROVED) {
            return AjaxResponse.redirect(request.getContextPath() + "/" + LOANAPPLICATION_URL + "/" + loanAplicationToken);
        }

        // Validate there is no rejection
        Employee employee = personDao.getEmployeeByPerson(loanApplication.getPersonId(), loanApplication.getEmployer().getId(), locale);
        SalaryAdvanceCalculatedAmount advance = loanApplicationDao.calculateSalaryAdvanceAmmount(employee.getId(), employee.getEmployer().getId(), locale);
        if (advance.getRejectionReasonKey() != null) {
            return AjaxResponse.errorMessage(messageSource.getMessage(advance.getRejectionReasonKey(), new String[]{employee.getEmployer().getDaysAfterEndOfMonth() + "", employee.getEmployer().getDaysBeforeEndOfMonth() + ""}, locale));
        }

        // Get the loan offer
        List<LoanOffer> offers = loanApplicationDao.getLoanOffers(loanApplication.getId());
        LoanOffer offer = null;
        if (offers == null) {
            return AjaxResponse.redirect(request.getContextPath() + "/" + LOANAPPLICATION_URL + "/" + loanAplicationToken);
        } else {
            offer = offers.get(0);
        }

        // Validate the values
        EntityRate entityRate = advance.getEntityRateByAmount(offerForm.getAmount());
        ((SalaryAdvanceOfferForm.SalaryAdvanceOfferFormValidator) offerForm.getValidator()).amount.setMaxValue(offer.getMaxAmmount().intValue()).setMinValue(offer.getMinAmmount().intValue());
        ((SalaryAdvanceOfferForm.SalaryAdvanceOfferFormValidator) offerForm.getValidator()).commission.setMinValue(entityRate.getCommission()).setMaxValue(entityRate.getCommission() * 2);
        offerForm.getValidator().validate(locale);
        if (offerForm.getValidator().isHasErrors()) {
            return AjaxResponse.errorMessage("Los valores no son válidos");
        }

        // Generate loanofer with new ammount and commision and select it
        offer = loanApplicationDao.createLoanOffersSalaryAdvance(loanApplication.getId(), offerForm.getAmount(), offerForm.getCommission(), employee.getEmployer().getId());

        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = "/" + LOANAPPLICATION_URL + "/{loanAplicationToken}/offer/contract", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.OTHER)
    public Object getContract(
            ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response,
            @PathVariable("loanAplicationToken") String loanApplicationToken) throws Exception {

//        String decrypted = CryptoUtil.decrypt(loanApplicationToken);
//        JSONObject jsonDecrypted = new JSONObject(decrypted);
//
//        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(jsonDecrypted.getInt("loan"), locale);
//        List<LoanOffer> offers = loanApplicationDao.getLoanOffers(jsonDecrypted.getInt("loan"));
//        if (offers == null) {
//            return AjaxResponse.errorMessage("No existen ofertas");
//        }
//
//        EntityProductParams params = catalogService.getEntityProductParam(offers.get(0).getEntityId(), Product.SALARY_ADVANCE);
//        EntityBranding entityBranding = catalogService.getEntityBranding(offers.get(0).getEntityId());
//
//        model.addAttribute("params", params);
//        model.addAttribute("entityBranding", entityBranding);
//        model.addAttribute("offer", offers.get(0));
//        model.addAttribute("loanApplication", loanApplication);
//        model.addAttribute("loanApplicationToken", loanApplicationToken);
//        model.addAttribute("person", personDao.getPerson(locale, loanApplication.getPersonId(), false));
//        model.addAttribute("personAddress", personDao.getPersonContactInformation(locale, loanApplication.getPersonId()));
//        return new ModelAndView("fragments/salaryAdvanceOfferFragment :: contract");

        JSONObject jsonDecrypted = new JSONObject(CryptoUtil.decrypt(loanApplicationToken));

        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(jsonDecrypted.getInt("loan"), locale);
        Person person = personDao.getPerson(catalogService, locale, loanApplication.getPersonId(), false);
        List<LoanOffer> offers = loanApplicationDao.getLoanOffers(jsonDecrypted.getInt("loan"));
        if (offers == null) {
            return AjaxResponse.errorMessage("No existen ofertas");
        }

        String filename = loanApplication.getId() + "_contrato_" + personDao.getPerson(catalogService, locale, person.getId(), false).getFirstName() + ".pdf";
        byte[] pdfAsBytes = creditService.createOfferContract(loanApplication, offers.get(0), request, response, locale, templateEngine, filename, true);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        ResponseEntity<byte[]> downloadablePdf = new ResponseEntity<byte[]>(pdfAsBytes, headers, HttpStatus.OK);
        return downloadablePdf;
    }

    @RequestMapping(value = "/" + LOANAPPLICATION_URL + "/{loanAplicationToken}/offer", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object registerLoanApplicationOffer(
            ModelMap model, Locale locale, HttpServletRequest request,
            @PathVariable("loanAplicationToken") String loanAplicationToken) throws Exception {

        String decrypted = CryptoUtil.decrypt(loanAplicationToken);
        JSONObject jsonDecrypted = new JSONObject(decrypted);

        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(jsonDecrypted.getInt("loan"), locale);

        // Validate is the correct status
        if (loanApplication.getStatus().getId() != LoanApplicationStatus.APPROVED) {
            return AjaxResponse.redirect(request.getContextPath() + "/" + LOANAPPLICATION_URL + "/" + loanAplicationToken);
        }

        // Validate there is no rejection
        Employee employee = personDao.getEmployeeByPerson(loanApplication.getPersonId(), loanApplication.getEmployer().getId(), locale);
        SalaryAdvanceCalculatedAmount advance = loanApplicationDao.calculateSalaryAdvanceAmmount(employee.getId(), employee.getEmployer().getId(), locale);
        if (advance.getRejectionReasonKey() != null) {
            return AjaxResponse.errorMessage(messageSource.getMessage(advance.getRejectionReasonKey(), new String[]{employee.getEmployer().getDaysAfterEndOfMonth() + "", employee.getEmployer().getDaysBeforeEndOfMonth() + ""}, locale));
        }

        // Get the loan offer
        List<LoanOffer> offers = loanApplicationDao.getLoanOffers(loanApplication.getId());
        LoanOffer offer = null;
        if (offers == null) {
            return AjaxResponse.redirect(request.getContextPath() + "/" + LOANAPPLICATION_URL + "/" + loanAplicationToken);
        } else {
            offer = offers.get(0);
        }
        loanApplicationDao.registerSelectedLoanOffer(offer.getId(), loanApplication.getFirstDueDate());

        // Validate the form
//        ((SalaryAdvanceOfferForm.SalaryAdvanceOfferFormValidator) offerForm.getValidator()).amount.setMaxValue(offer.getMaxAmmount().intValue()).setMinValue(offer.getMinAmmount().intValue());
//        ((SalaryAdvanceOfferForm.SalaryAdvanceOfferFormValidator) offerForm.getValidator()).commission.setMinValue(advance.getMinCommission().intValue());
//        offerForm.getValidator().validate(locale);
//        if (offerForm.getValidator().isHasErrors()) {
//            return AjaxResponse.errorFormValidation(offerForm.getValidator().getErrorsJson());
//        }

        // Generate loanofer with new ammount and commision and select it
//        offer = loanApplicationDao.createLoanOffersSalaryAdvance(loanApplication.getId(), offerForm.getAmount(), offerForm.getCommission(), employee.getEmployer().getId());
//        loanApplicationDao.registerSelectedLoanOffer(offer.getId(), loanApplication.getFirstDueDate());

        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = "/" + LOANAPPLICATION_URL + "/{loanAplicationToken}/offer/sendvalidation", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object sendValidatoinLoanApplicationOffer(
            ModelMap model, Locale locale, HttpServletRequest request,
            RegisterCellphoneForm cellphoneForm, ValidateEmailForm emailForm,
            @PathVariable("loanAplicationToken") String loanAplicationToken) throws Exception {

        String decrypted = CryptoUtil.decrypt(loanAplicationToken);
        JSONObject jsonDecrypted = new JSONObject(decrypted);

        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(jsonDecrypted.getInt("loan"), locale);

        // Validations
        if (loanApplication.getStatus().getId() != LoanApplicationStatus.APPROVED) {
            return AjaxResponse.redirect(request.getContextPath() + "/" + LOANAPPLICATION_URL + "/" + loanAplicationToken);
        }

        if (loanApplication.getRegisterType().getRegisterTypeId() == LoanApplicationRegisterType.EMAIL) {
            // Validate
            cellphoneForm.getValidator().validate(locale);
            if (cellphoneForm.getValidator().isHasErrors())
                return AjaxResponse.errorMessage("Lo sentimos, el número de teléfono ingresado es incorrecto");

            // Validate or register the phone number
            User user = userDao.getUser(jsonDecrypted.getInt("user"));
            if (user.getPhoneNumber() != null && !user.getPhoneNumber().equals(cellphoneForm.getCellphone())) {
                return AjaxResponse.errorMessage("Lo sentimos, el número de teléfono ingresado es incorrecto");
            } else if (user.getPhoneNumber() == null) {
                userDao.registerPhoneNumber(jsonDecrypted.getInt("user"), cellphoneForm.getCountryCode(), cellphoneForm.getCellphone());
            }

            // Send sms auth token
            userService.sendAuthTokenSms(jsonDecrypted.getInt("user"), jsonDecrypted.getInt("person"), cellphoneForm.getCountryCode(), cellphoneForm.getCellphone(), user.getSimpleName(), loanApplication.getId(), null, loanApplication.getCountryId());
        } else if (loanApplication.getRegisterType().getRegisterTypeId() == LoanApplicationRegisterType.DNI) {
            // Validate
            emailForm.getValidator().validate(locale);
            if (emailForm.getValidator().isHasErrors())
                return AjaxResponse.errorMessage("Lo sentimos, el email enviado no es correcto");

            // Register email
            User user = userDao.getUser(jsonDecrypted.getInt("user"));
            Integer emailId = null;
            if (user.getEmail() != null) {
                // Validate tha the email is one of the previous emails the user had
                List<UserEmail> userEmails = userDao.getUserEmails(user.getId());
                boolean existsEmail = false;
                if (userEmails != null) {
                    existsEmail = userEmails.stream().filter(e -> e.getEmail().equalsIgnoreCase(emailForm.getEmail())).findAny().isPresent();
                }
                if (!user.getEmail().equalsIgnoreCase(emailForm.getEmail()) && !existsEmail) {
                    return AjaxResponse.errorMessage("Lo sentimos, el email enviado no es correcto");
                }
            } else {
                emailId = userDao.registerEmailChange(jsonDecrypted.getInt("user"), emailForm.getEmail());
            }

            // Send email with token
            Employee employee = personDao.getEmployeeByPerson(loanApplication.getPersonId(), loanApplication.getEmployer().getId(), locale);
            salaryAdvanceService.sendValidationMail(loanApplication.getId(), jsonDecrypted.getInt("user"), jsonDecrypted.getInt("person"), employee.getEmployer(), emailId, emailForm.getEmail(), locale);

        }

        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = "/" + LOANAPPLICATION_URL + "/{loanAplicationToken}/offer/validate", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object validateLoanApplicationOffer(
            ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response,
            ValidateCellphoneForm form,
            @PathVariable("loanAplicationToken") String loanAplicationToken) throws Exception {

        String decrypted = CryptoUtil.decrypt(loanAplicationToken);
        JSONObject jsonDecrypted = new JSONObject(decrypted);

        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(jsonDecrypted.getInt("loan"), locale);

        // Validations
        if (loanApplication.getStatus().getId() != LoanApplicationStatus.APPROVED) {
            return AjaxResponse.redirect(request.getContextPath() + "/" + LOANAPPLICATION_URL + "/" + loanAplicationToken);
        }
        form.getValidator().validate(locale);
        if (form.getValidator().isHasErrors()) {
            return AjaxResponse.errorMessage("Campo obligatorio.");
        }

        // Validate the auth token
        String errorMessage = userService.validateSmsAuthToken(jsonDecrypted.getInt("user"), form.getAuthToken(), locale);
        if (errorMessage != null) {
            return AjaxResponse.errorMessage(errorMessage);
        }

        // Aprove the loan aplication
        // TODO Put in a service so the Backoffice could call this to
        Credit credit = loanApplicationDao.generateCredit(loanApplication.getId(), locale);
        loanApplicationDao.updateLoanApplicationStatus(loanApplication.getId(), LoanApplicationStatus.APPROVED_SIGNED, null);
        creditDao.generateOriginalSchedule(credit);

        // Register the signature
        LoanOffer offer = loanApplicationDao.getLoanOffers(loanApplication.getId()).get(0);
        Person person = personDao.getPerson(catalogService, locale, loanApplication.getPersonId(), false);
        String lastSurname = person.getLastSurname() != null ? person.getLastSurname() : "";
        loanApplicationDao.registerLoanApplicationSIgnature(
                offer.getId(),
                person.getFirstName() + " " + person.getFirstSurname() + " " + lastSurname,
                person.getDocumentType().getId(),
                person.getDocumentNumber());

        // Create and sen the contract pdf to the client
        loanApplicationService.sendPostSignatureInteractions(jsonDecrypted.getInt("user"), jsonDecrypted.getInt("loan"), null, null, templateEngine, locale);

        return AjaxResponse.redirect(request.getContextPath() + "/" + LOANAPPLICATION_URL + "/" + loanAplicationToken);
    }

    @RequestMapping(value = "/" + LOANAPPLICATION_URL + "/{loanAplicationToken}/offer/validate/email", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object validateLoanApplicationOfferMail(
            ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response,
            @PathVariable("loanAplicationToken") String loanAplicationToken) throws Exception {

        String decrypted = CryptoUtil.decrypt(loanAplicationToken);
        JSONObject jsonDecrypted = new JSONObject(decrypted);

        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(jsonDecrypted.getInt("loan"), locale);
        List<LoanOffer> offers = loanApplicationDao.getLoanOffers(loanApplication.getId());

        // Validations
        if (loanApplication.getStatus().getId() != LoanApplicationStatus.APPROVED) {
            //return AjaxResponse.redirect(request.getContextPath() + "/" + LOANAPPLICATION_URL + "/" + loanAplicationToken);
            return new ModelAndView("redirect:/" + LOANAPPLICATION_URL + "/" + loanAplicationToken);
        }
        if (!JsonUtil.getBooleanFromJson(jsonDecrypted, "validationlink", false)) {
            //return AjaxResponse.redirect(request.getContextPath() + "/" + LOANAPPLICATION_URL + "/" + loanAplicationToken);
            return new ModelAndView("redirect:/" + LOANAPPLICATION_URL + "/" + loanAplicationToken);
        }
        if (offers == null || !offers.stream().anyMatch(o -> o.getSelected() != null && o.getSelected())) {
            return new ModelAndView("redirect:/" + LOANAPPLICATION_URL + "/" + loanAplicationToken);
        }

        // Validate the email change
        if (JsonUtil.getIntFromJson(jsonDecrypted, "emailid", null) != null) {
            userDao.validateEmailChange(jsonDecrypted.getInt("user"), JsonUtil.getIntFromJson(jsonDecrypted, "emailid", null));
        }

        // Aprove the loan aplication
        // TODO Put in a service so the Backoffice could call this to
        Credit credit = loanApplicationDao.generateCredit(loanApplication.getId(), locale);
        loanApplicationDao.updateLoanApplicationStatus(loanApplication.getId(), LoanApplicationStatus.APPROVED_SIGNED, null);
        creditDao.generateOriginalSchedule(credit);

        // Register the signature
        LoanOffer offer = loanApplicationDao.getLoanOffers(loanApplication.getId()).get(0);
        Person person = personDao.getPerson(catalogService, locale, loanApplication.getPersonId(), false);
        loanApplicationDao.registerLoanApplicationSIgnature(
                offer.getId(),
                person.getFirstName() + " " + person.getFirstSurname() + " " + person.getLastSurname(),
                person.getDocumentType().getId(),
                person.getDocumentNumber());

        // Create and sen the contract pdf to the client
        loanApplicationService.sendPostSignatureInteractions(jsonDecrypted.getInt("user"), jsonDecrypted.getInt("loan"), request, response, templateEngine, locale);

        return new ModelAndView("redirect:/" + LOANAPPLICATION_URL + "/" + loanAplicationToken);
    }

    @RequestMapping(value = "/" + LOANAPPLICATION_URL + "/{loanAplicationToken}/socialnetworks", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object asociateSocialNetwork(
            ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response,
            AsociateSocialNetworkForm form,
            @PathVariable("loanAplicationToken") String loanAplicationToken) throws Exception {

        String decrypted = CryptoUtil.decrypt(loanAplicationToken);
        JSONObject jsonDecrypted = new JSONObject(decrypted);

        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(jsonDecrypted.getInt("loan"), locale);

        // Validations
        if (loanApplication.getStatus().getId() != LoanApplicationStatus.APPROVED_SIGNED) {
            return AjaxResponse.redirect(request.getContextPath() + "/" + LOANAPPLICATION_URL + "/" + loanAplicationToken);
        }
        form.getValidator().validate(locale);
        if (form.getValidator().isHasErrors()) {
            return AjaxResponse.errorMessage("Campo obligatorio.");
        }

        // Get the accessToken
        TokenResponse tokenResponse = oauthService.getAccessToken(form.getOauthNetworkByProvider(), form.getAuthCode());

        // Get the profile data
        NetworkProfile networkProfile = oauthService.getNetworkProfile(form.getOauthNetworkByProvider(), tokenResponse.getAccessToken());

        // Register the data
        UserNetworkToken networkToken = userDao.registerNetworkAccessToken(
                jsonDecrypted.getInt("user"),
                form.getSocialNetwork(),
                tokenResponse.getAccessToken(),
                tokenResponse.getRefreshToken(),
                networkProfile.getEmail(),
                networkProfile.getId());
        userDao.updateNetworkProfile(networkToken.getId(), networkProfile.getResponse().toString());

        // Register the Facebook or Linkedin data
        switch (form.getOauthNetworkByProvider()) {
            case FACEBOOK:
                UserFacebook userFacebbok = new UserFacebook();
                userFacebbok.fillFromApi(networkProfile.getResponse());
                userFacebbok.setFacebookFriends(oauthService.requestFriendsCount(Configuration.OauthNetwork.FACEBOOK, tokenResponse.getAccessToken()));
                userDao.registerFacebook(jsonDecrypted.getInt("user"), userFacebbok);
                break;
            case LINKEDIN:
                PersonLinkedIn personLinkedin = new PersonLinkedIn();
                personLinkedin.fillFromApi(networkProfile.getResponse());
                userDao.registerLinkedin(jsonDecrypted.getInt("person"), personLinkedin);
                break;
            case GOOGLE:
            case WINDOWS:
            case YAHOO:
                // Execute the get data of the user email in the worker
                webscrapperService.callUserEmailDataBot(jsonDecrypted.getInt("user"), form.getSocialNetwork());
                break;
        }

        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = "/" + LOANAPPLICATION_URL + "/{loanAplicationToken}/postSignature", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    @ResponseBody
    public ModelAndView postSignature(
            ModelMap model, Locale locale,
            @PathVariable("loanAplicationToken") String loanAplicationToken) throws Exception {
        JSONObject jsonDecrypt = new JSONObject(CryptoUtil.decrypt(loanAplicationToken));

        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(jsonDecrypt.getInt("loan"), locale);
        if (loanApplication.getStatus().getId() != LoanApplicationStatus.APPROVED_SIGNED) {
            return new ModelAndView("redirect:/" + LOANAPPLICATION_URL + "/" + loanAplicationToken);
        }

        Credit credit = creditDao.getCreditByID(loanApplication.getCreditId(), locale, false, Credit.class);

        model.addAttribute("loanApplicationUpdatedDate", new SimpleDateFormat(Configuration.BACKOFFICE_FRONT_SHORT_DATE_FORMAT).format(loanApplication.getUpdatedDate()));
        model.addAttribute("loanAplicationToken", loanAplicationToken);
        model.addAttribute("productUrl", LOANAPPLICATION_URL);
        model.addAttribute("loanApplication", loanApplication);
        model.addAttribute("credit", credit);
        model.addAttribute("entityProductParams", catalogService.getEntityProductParamById(credit.getEntityProductParameterId()));
        PersonBankAccountInformation bankAccount = personDao.getPersonBankAccountInformation(locale, jsonDecrypt.getInt("person"));
        model.addAttribute("bankAccountInformation", personDao.getPersonBankAccountInformation(locale, loanApplication.getPersonId()));
        model.addAttribute("person", personDao.getPerson(catalogService, locale, loanApplication.getPersonId(), false));
        if (credit.getProduct().getId() == Product.SALARY_ADVANCE) {
            model.addAttribute("facebook", userDao.getUserFacebook(jsonDecrypt.getInt("user")) != null);
            model.addAttribute("linkedin", userDao.getLinkedin(jsonDecrypt.getInt("person")) != null);
            model.addAttribute("google", userDao.getUserNetworkTokenByProvider(jsonDecrypt.getInt("user"), 'G') != null);
            model.addAttribute("windows", userDao.getUserNetworkTokenByProvider(jsonDecrypt.getInt("user"), 'W') != null);
            model.addAttribute("yahoo", userDao.getUserNetworkTokenByProvider(jsonDecrypt.getInt("user"), 'Y') != null);
        }
        if (credit.getProduct().getId() == Product.AGREEMENT) {
            model.addAttribute("associated", personDao.getAssociated(jsonDecrypt.getInt("person"), credit.getEntity().getId()));
        }
        return new ModelAndView("/loanApplication/formQuestions/postSignature");
    }

    @RequestMapping(value = "/" + LOANAPPLICATION_URL + "/register", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public Object registerLoanApplication(
            ModelMap model, Locale locale, HttpServletRequest request,
            SalaryAdvanceForm form) throws Exception {

        // Validate form fields
        form.getValidator().validate(locale);
        if (form.getValidator().isHasErrors()) {
            return AjaxResponse.errorFormValidation(form.getValidator().getErrorsJson());
        }

        int registerType = form.getEmail() != null ? LoanApplicationRegisterType.EMAIL : LoanApplicationRegisterType.DNI;

        // Check if there are employees asociated
        List<Employee> employees = employeeService.getEmployeesByEmailOrDocumentByProduct(form.getEmail(), form.getDocType(), form.getDocNumber(), Product.SALARY_ADVANCE, locale);
        Integer evaluationId = loanApplicationDao.registerIntent(form.getDocType(), form.getDocNumber());

        if (employees.isEmpty()) {
            JSONObject jsonError = new JSONObject();
            jsonError.put("type", "noEmployee");
            jsonError.put("msg", "No perteneces a una empresa asociada a este producto.<br/>" +
                    "Obtén tu crédito <a href=\"" + request.getContextPath() + "/" + ProductCategory.CONSUMO_CATEGORY_URL + "/" + Configuration.EVALUATION_CONTROLLER_URL + "\">aquí</a>");

            //return AjaxResponse.errorMessage(jsonError.toString());

            String message = "No perteneces a una empresa asociada a este producto.<br/>" + "Obtén tu crédito <a href=\"" + request.getContextPath() + "/" + ProductCategory.CONSUMO_CATEGORY_URL + "/" + Configuration.EVALUATION_CONTROLLER_URL + "\">aquí</a>";
            return AjaxResponse.errorMessage(message);

        }

        // TODO Show to the user all the employees that he is associated, so he can chose one. For now we pick the first one
        Employee employee = employees.get(0);
        if (!employee.getActive()) {
            return AjaxResponse.errorMessage("Lo sentimos, no estas activo en la lista de planilla de tu empresa.");
        }

        // Check there is no rejection reason
        SalaryAdvanceCalculatedAmount advancecalculated = loanApplicationDao.calculateSalaryAdvanceAmmount(evaluationId, employee.getId(), employee.getEmployer().getId(), locale);
        if (advancecalculated.getRejectionReasonKey() != null) {
            return AjaxResponse.errorMessage(messageSource.getMessage(advancecalculated.getRejectionReasonKey(), new String[]{employee.getEmployer().getDaysAfterEndOfMonth() + "", employee.getEmployer().getDaysBeforeEndOfMonth() + ""}, locale));
        }


        // Create an user if it doesnt exist
        User user;
        if (employee.getUserId() == null) {
            user = userService.getOrRegisterUser(employee.getDocType().getId(), employee.getDocNumber(), null, null, null);
            employee.setUserId(user.getId());
            employee.setPersonId(user.getPersonId());
        } else {
            user = userDao.getUser(employee.getUserId());
        }

        // Get the active loanApplication or create it
        LoanApplication loanApplication = loanApplicationDao.getActiveLoanApplicationByPerson(locale, user.getPersonId(), ProductCategory.ADELANTO);
        if (loanApplication == null) {
            loanApplication = salaryAdvanceService.registerLoanApplication(employee, Util.getClientIpAddres(request), 'W', locale, registerType,
                    countryContextService.getCountryParamsByRequest(request).getId());
        } else {
            loanApplicationDao.updateRegisterType(loanApplication.getId(), registerType);
            loanApplication.setRegisterType(catalogService.getLoanApplicationRegisterTypeById(registerType));
        }

        loanApplicationDao.updateSourceMediumCampaign(loanApplication.getId(), form.getSource(), form.getMedium(), form.getCampaign());
        loanApplicationDao.updateTermContent(loanApplication.getId(), form.getTerm(), form.getContent());
        loanApplicationDao.updateGAClientID(loanApplication.getId(), form.getGaClientID());
        loanApplicationDao.updateUserAgent(loanApplication.getId(), request.getHeader("User-Agent"));

        if (form.getCellphone() != null) {
            if (user.getPhoneNumber() != null && user.getPhoneNumber().equals(form.getCellphone()) && user.getPhoneVerified() != null && user.getPhoneVerified()) {
                // Redirect to loanOffer with special param
                Map<String, Object> params = new HashMap<>();
                params.put("confirmationlink", true);
                String loanApplicationToken = loanApplicationService.generateLoanApplicationToken(user.getId(), user.getPersonId(), loanApplication.getId(), params);
                return AjaxResponse.redirect(request.getContextPath() + "/" + LOANAPPLICATION_URL + "/" + loanApplicationToken);
            } else {
                // Save cellphone and send token
                userDao.registerPhoneNumber(user.getId(), "51", form.getCellphone());
                userService.sendAuthTokenSms(user.getId(), user.getPersonId(), "51", form.getCellphone(), null, loanApplication.getId(), null, CountryParam.COUNTRY_PERU);
            }
        }

        if (form.getEmail() != null) {
            // Send email with the loan application link
            salaryAdvanceService.sendConfirmationMail(loanApplication.getId(), user.getId(), user.getPersonId(), employee.getWorkEmail(), employee.getEmployer(), locale);

            JSONObject json = new JSONObject();
            json.put("message", "Te hemos enviado un correo con el link de tu credito.");
            return AjaxResponse.ok(json.toString());
        } else if (form.getDocType() != null) {
            // Move to the next section to validate the cellphone
            JSONObject json = new JSONObject();
            json.put("loanApplicationToken", loanApplicationService.generateLoanApplicationToken(user.getId(), user.getPersonId(), loanApplication.getId()));
            return AjaxResponse.ok(json.toString());
        } else {
            return AjaxResponse.errorMessage(null);
        }
    }

}
