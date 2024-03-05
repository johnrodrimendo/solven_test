package com.affirm.client.controller;

import com.affirm.client.model.form.UserRegisterDocumentForm;
import com.affirm.client.model.form.ValidateCellphoneForm;
import com.affirm.client.service.LoanApplicationClService;
import com.affirm.common.dao.BotDAO;
import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.dao.UserDAO;
import com.affirm.common.model.annotation.ErrorControllerAnnotation;
import com.affirm.common.model.catalog.CountryParam;
import com.affirm.common.model.catalog.IdentityDocumentType;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.model.transactional.Person;
import com.affirm.common.model.transactional.Reniec;
import com.affirm.common.model.transactional.User;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.LoanApplicationService;
import com.affirm.common.service.UserService;
import com.affirm.common.service.impl.SyncBotService;
import com.affirm.common.util.AjaxResponse;
import com.affirm.common.util.CryptoUtil;
import com.affirm.common.util.Util;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Locale;

@Controller
@Scope("request")
@RequestMapping("/user")
public class UserController {

    private static final Logger logger = Logger.getLogger(UserController.class);

    @Autowired
    private UserService userService;
    @Autowired
    private UserDAO userDao;
    @Autowired
    private PersonDAO personDao;
    @Autowired
    private LoanApplicationService loanApplicationService;
    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private LoanApplicationClService loanApplicationClService;
    @Autowired
    private BotDAO botDAO;
    @Autowired
    private SyncBotService syncbotService;

    @RequestMapping(value = "/accessfirst", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public Object registerAccessFirst(
            HttpSession session, Locale locale,
            @RequestParam(value = "docTypeSooner", required = false) Integer docType,
            @RequestParam(value = "docNumberSooner", required = false) String docNumber,
            @RequestParam("emailSooner") String email,
            @RequestParam(value = "loanReasonId", required = false) Integer loanReasonId,
            @RequestParam(value = "product", required = false) Integer productId) throws Exception {
        userDao.registerEarlyAccess(docType, docNumber, email, loanReasonId, productId);
        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = "/validateDocument/{product}", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public ResponseEntity<String> validateDocument(
            HttpSession session, Locale locale, UserRegisterDocumentForm form,
            @PathVariable("product") Integer productId) throws Exception {
        // validate form
        form.getValidator().validate(locale);
        if (form.getValidator().isHasErrors()) {
            return AjaxResponse.errorFormValidation(form.getValidator().getErrorsJson());
        }

        // Check if user exists
        User user = userDao.getUserByDocument(form.getDocType(), form.getDocNumber());
        if(user != null) {
            Person person = personDao.getPerson(catalogService, locale, user.getPersonId(), false);
            if(person != null && person.getBirthday() != null) {
                if (form.getDocType() == IdentityDocumentType.CE) {
                    boolean match = Util.parseDate(form.getBirthDate(), "dd/MM/yyyy").equals(person.getBirthday());
                    if(!match) {
                        return AjaxResponse.errorMessage("Lo sentimos, el número de documento no coincide con el cumpleaños indicado");
                    }
                }
                // Check if has activeloan application
                LoanApplication activeLoanApplication = loanApplicationDao.getActiveLoanApplicationByPerson(locale, user.getPersonId(), productId);
                if (activeLoanApplication != null) {
                    JSONObject jsonResponse = new JSONObject();
                    jsonResponse.put("message", "Hola " + person.getFirstName() + ", tienes una solicitud en proceso. Por favor ingresa tu número de celular para continuar.");
                    return AjaxResponse.ok(jsonResponse.toString());
                }

                if (user.getPhoneNumber() != null) {
                    JSONObject jsonResponse = new JSONObject();
                    jsonResponse.put("message", "Hola " + person.getFirstName() + ", bienvenido de vuelta. Por favor ingresa tu número de celular para continuar.");
                    return AjaxResponse.ok(jsonResponse.toString());
                }
            }
            else if(form.getDocType() == IdentityDocumentType.CE) {
                Integer queryId = syncbotService.callMigraciones(form.getDocNumber(), Util.parseDate(form.getBirthDate(), "dd/MM/yyyy"));
                session.setAttribute("ceQueryId", queryId);
                return AjaxResponse.ok("waitCE");
            }
        } else {
            // Check if the document exists
            if (form.getDocType() == IdentityDocumentType.DNI) {
                Reniec reniec = personDao.getReniecDBData(form.getDocNumber());
                if (reniec == null) {
                    return AjaxResponse.errorMessage("No encontramos suficiente información tuya para poder continuar. Esperamos poder ayudarte muy pronto.");
                }
            }
            else if(form.getDocType() == IdentityDocumentType.CE) {
                Integer queryId = syncbotService.callMigraciones(form.getDocNumber(), Util.parseDate(form.getBirthDate(), "dd/MM/yyyy"));
                session.setAttribute("ceQueryId", queryId);
                return AjaxResponse.ok("waitCE");
            }
            else {
                throw new Exception("Ese no es un tipo de documento válido.");
            }
        }
        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = "/validCE/", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public ResponseEntity<String> validCE (
            HttpSession session, Locale locale) throws Exception {
        Object ceQueryId = session.getAttribute("ceQueryId");
        Integer ceQueryIdInt = Util.intOrNull(ceQueryId.toString());
        Boolean result = syncbotService.migracionesSuccess(ceQueryIdInt);
        if(result == null) {
            return AjaxResponse.errorMessage("Lo sentimos, ha habido un error y no hemos podido validar sus datos. Por favor, inténtelo nuevamente.");
        }
        if(result == false) {
            return AjaxResponse.errorMessage("Lo sentimos, el número de documento no coincide con la fecha de nacimiento.");
        } else {
            return AjaxResponse.ok(null);
        }
    }

    @RequestMapping(value = "/cellphone/validate", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public ResponseEntity validateCellphone(
            ModelMap model, Locale locale, HttpSession session, HttpServletRequest request,
            @RequestParam("loanAplicationToken") String loanAplicationToken,
            ValidateCellphoneForm phoneForm) throws Exception {

        String decrypted = CryptoUtil.decrypt(loanAplicationToken);
        JSONObject jsonDecrypted = new JSONObject(decrypted);

        // Validate the form
        phoneForm.getValidator().validate(locale);
        if (phoneForm.getValidator().isHasErrors()) {
            return AjaxResponse.errorFormValidation(phoneForm.getValidator().getErrorsJson());
        }

        // Validate the auth token
        String errorMessage = userService.validateSmsAuthToken(jsonDecrypted.getInt("user"), phoneForm.getAuthToken(), locale);
        if (errorMessage != null) {
            return AjaxResponse.errorMessage(errorMessage);
        }

        // Login in LoanApplicationProcess
        loanApplicationClService.loginLoanApplicationProcess(loanAplicationToken);

        // Response
        return AjaxResponse.redirect(request.getContextPath() + "/loanapplication/" + loanAplicationToken);
    }

    @RequestMapping(value = "/{userToken}/resendSmsToken", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public ResponseEntity resendSmsToken(
            ModelMap model, Locale locale, HttpServletRequest request,
            @PathVariable("userToken") String userToken) throws Exception {

        String decrypted = CryptoUtil.decrypt(userToken);
        JSONObject jsonDecrypted = new JSONObject(decrypted);

        User user = userDao.getUser(jsonDecrypted.getInt("user"));
        if (user.getPhoneNumber() != null) {
            userService.sendAuthTokenSms(user.getId(), user.getPersonId(), user.getCountryCode(), user.getPhoneNumber(), user.getSimpleName(),jsonDecrypted.getInt("loan"), null, CountryParam.COUNTRY_PERU);
            return AjaxResponse.ok(null);
        } else {
            return AjaxResponse.errorMessage("No tienes un número asociado");
        }
    }

//    @RequestMapping(value = "/+", method = RequestMethod.POST)
//    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
//    public ResponseEntity resendSmsToken(
//            ModelMap model, Locale locale, HttpServletRequest request,
//            @RequestParam("userId") Integer userId) throws Exception {
//
//        // Check if the users alredy exists
//        User user = userDao.getUser(userId);
//        if (user.getPhoneNumber() != null) {
//            userClService.sendAuthTokenSms(user.getId(), user.getPersonId(), user.getCountryCode(), user.getPhoneNumber(), user.getSimpleName());
//            return AjaxResponse.ok(null);
//        } else {
//            return AjaxResponse.errorMessage("No tienes un número asociado");
//        }
//    }

}
