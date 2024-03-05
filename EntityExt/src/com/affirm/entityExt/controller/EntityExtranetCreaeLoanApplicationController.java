package com.affirm.entityExt.controller;


import com.affirm.client.model.ExtranetPainterLoanApplication;
import com.affirm.client.model.LoggedUserEntity;
import com.affirm.client.service.EntityExtranetService;
import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.dao.UserDAO;
import com.affirm.common.model.annotation.ErrorControllerAnnotation;
import com.affirm.common.model.annotation.RequiresPermissionOr403;
import com.affirm.common.model.catalog.*;
import com.affirm.common.model.form.EntityExtranetCreateLoanApplicationForm;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.LoanApplicationService;
import com.affirm.common.service.ProductService;
import com.affirm.common.service.UtilService;
import com.affirm.common.util.AjaxResponse;
import com.affirm.security.model.SysUser;
import com.affirm.system.configuration.Configuration;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * @author jrodriguez
 */
@Controller("entityExtranetCreaeLoanApplicationController")
public class EntityExtranetCreaeLoanApplicationController {

    private static Logger logger = Logger.getLogger(EntityExtranetCreaeLoanApplicationController.class);

    @Autowired
    private EntityExtranetService entityExtranetService;
    @Autowired
    private LoanApplicationService loanApplicationService;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private UtilService utilService;
    @Autowired
    private ProductService productService;
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private PersonDAO personDAO;


    @RequestMapping(value = "/createLoanApplications", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "menu:loan:create:view", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public Object createLoanApplications(
            ModelMap model, Locale locale, HttpServletRequest request) throws Exception {

        LoggedUserEntity loggedUser = entityExtranetService.getLoggedUserEntity();

        // If its entity SOLVE, check the countries array to show, else return the country from the entity
        List<CountryParam> countries;
        if (loggedUser.getPrincipalEntity().getId().equals(Entity.AFFIRM)) {
            countries = new ArrayList<>(loggedUser.getCountries().keySet());
        } else {
            countries = Arrays.asList(catalogService.getCountryParam(loggedUser.getPrincipalEntity().getCountryId()));
        }
        model.addAttribute("countries", countries);

        if (countries.size() == 1) {
            EntityExtranetCreateLoanApplicationForm form = new EntityExtranetCreateLoanApplicationForm();
            form.setCountryId(countries.get(0).getId());
            configValidator(form, loggedUser);
            form.setNonExistingPerson(false);
            model.addAttribute("form", form);
        }

        return new ModelAndView("/entityExtranet/createLoanApplications");
    }

    @RequestMapping(value = "/createLoanApplications/form", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "menu:loan:create:view", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object getCreateLoanApplicationsForm(
            ModelMap model, Locale locale, HttpServletRequest request,
            @RequestParam("countryId") Integer countryId) throws Exception {

        // Validate the country
        LoggedUserEntity loggedUser = entityExtranetService.getLoggedUserEntity();
        List<CountryParam> countries = new ArrayList<>(loggedUser.getCountries().keySet());
        if (countries.stream().noneMatch(c -> c.getId().equals(countryId))) {
            return AjaxResponse.errorMessage("El pais es invalido");
        }

        EntityExtranetCreateLoanApplicationForm form = new EntityExtranetCreateLoanApplicationForm();
        form.setCountryId(countryId);
        configValidator(form, loggedUser);
        form.setNonExistingPerson(false);
        model.addAttribute("form", form);
        return new ModelAndView("/entityExtranet/createLoanApplications :: create_loan_form");
    }

    @RequestMapping(value = "/createLoanApplications", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "loan:create:store", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object createLoan(
            ModelMap modelMap, Locale locale, EntityExtranetCreateLoanApplicationForm form) throws Exception {

        // Validate the country
        List<CountryParam> countries;
        LoggedUserEntity loggedUser = entityExtranetService.getLoggedUserEntity();
        if (loggedUser.getPrincipalEntity().getId().equals(Entity.AFFIRM)) {
            countries = new ArrayList<>(loggedUser.getCountries().keySet());
        } else {
            countries = Arrays.asList(catalogService.getCountryParam(loggedUser.getPrincipalEntity().getCountryId()));
        }
        if (countries.size() == 1 && form.getCountryId() == null)
            form.setCountryId(countries.get(0).getId());
        if (countries.stream().noneMatch(c -> c.getId().equals(form.getCountryId()))) {
            return AjaxResponse.errorMessage("El pais es invalido");
        }

        // Set the default values
        form.setEntityUserId(loggedUser.getId());
        form.setAgentId(Configuration.DEFAULT_AGENT_ID);
        Affiliator affiliator = loggedUser.getAffiliatorId() != null ? userDAO.getAffiliator(loggedUser.getAffiliatorId()) : null;
        if (affiliator != null)
            form.setSource(affiliator.getName());
        else
            form.setSource("Promotor");
        form.setMedium("Campo");
        // If the entity is affrim, its a marketplace loan app
        if (loggedUser.getPrincipalEntity().getId().equals(Entity.AFFIRM)) {
            form.setCampaign(""); //TODO
            form.setEntityBrandingId(null);
        } else {
            form.setCampaign(loggedUser.getPrincipalEntity().getShortName());
            form.setEntityBrandingId(loggedUser.getPrincipalEntity().getId());
        }

        //Validate the form
        configValidator(form, loggedUser);
        ResponseEntity validateResponse = loanApplicationService.validateLoanApplicationCreateForm(form, locale);
        if (validateResponse != null) {
            return validateResponse;
        }

        // In case the person has an active loan application
        LoanApplication activeLoanApplication = loanApplicationService.getActiveLoanApplication(form.getDocType(), form.getDocumentNumber(), ProductCategory.CONSUMO);
        if (activeLoanApplication != null)
            return AjaxResponse.errorMessage("La persona ya tiene una solicitud activa");

        // Create and redirect to the new loan application
        loanApplicationService.createLoanApplication(form);

        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = "/createLoanApplications/results", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "menu:loan:create:view", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object getResults(
            ModelMap model, Locale locale, HttpServletRequest request) throws Exception {

        List<ExtranetPainterLoanApplication> applications = loanApplicationDao.getLoanApplicationByEntityUser(entityExtranetService.getLoggedUserEntity().getId(), ExtranetPainterLoanApplication.class);
        model.addAttribute("applications", applications);
        return new ModelAndView("/entityExtranet/createLoanApplications :: results");
    }

    @RequestMapping(value = "/createLoanApplications/assistLoan", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "loan:create:assist", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object assistLoan(
            ModelMap modelMap, Locale locale,
            @RequestParam("loan") Integer loanApplicationId) throws Exception {

        if (loanApplicationId == null)
            return AjaxResponse.errorMessage("La solicitud no existe");

        LoggedUserEntity loggedUser = entityExtranetService.getLoggedUserEntity();
        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(loanApplicationId, locale);
        if (loanApplication.getEntityUserId() == null || !loanApplication.getEntityUserId().equals(loggedUser.getId()))
            return AjaxResponse.errorMessage("La solicitud no existe");

        return AjaxResponse.redirect(loanApplicationService.generateLoanApplicationLinkEntity(loanApplication));
    }

    @RequestMapping(value = "/createLoanApplications/sendEmailProcessLink", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "loan:create:assist", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object sendEmailProcessLink(Locale locale, @RequestParam("loan") Integer loanApplicationId) throws Exception {
        if (loanApplicationId == null)
            return AjaxResponse.errorMessage("La solicitud no existe");

        LoggedUserEntity loggedUser = entityExtranetService.getLoggedUserEntity();
        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(loanApplicationId, locale);
        if (loanApplication.getEntityUserId() == null || !loanApplication.getEntityUserId().equals(loggedUser.getId()))
            return AjaxResponse.errorMessage("La solicitud no existe");
        if (loanApplication.getStatus().getId() == LoanApplicationStatus.EXPIRED) {
            return AjaxResponse.errorMessage("La solicitud se encuentra expirada");
        }

        Person person = personDAO.getPerson(catalogService, locale, loanApplication.getPersonId(), false);
        User user = userDAO.getUser(person.getUserId());

        JSONObject json = new JSONObject();
        json.put("CLIENT_NAME", person.getFirstName());
        json.put("AGENT_FULLNAME", loanApplication.getAgent() != null ? loanApplication.getAgent().getName() : Configuration.AGENT_FULLNAME_COLLECTION);
        json.put("AGENT_IMAGE_URL", loanApplication.getAgent() != null ? loanApplication.getAgent().getAvatarUrl() : Configuration.AGENT_IMAGE_URL_COLLECTION);
        json.put("LINK", loanApplicationService.generateLoanApplicationLinkEntity(loanApplication));

        loanApplicationDao.registerAssistedProcessSchedule(loanApplication.getId(), null);

        entityExtranetService.sendInteractionProcessLink(loanApplication, user.getEmail(), json);

        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = "/createLoanApplications/documentNumber", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "loan:create:assist", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object getUserByDocumentNumber(
            ModelMap model, Locale locale,
            EntityExtranetCreateLoanApplicationForm form) throws Exception {

        if (form.getDocType() == null || form.getDocType() == 0)
            return AjaxResponse.errorMessage("Seleccione un tipo de documento");
        if (form.getDocumentNumber() == null || form.getDocumentNumber().isEmpty())
            return AjaxResponse.errorMessage("Ingresa nro. documento");

        List<CountryParam> countries;
        LoggedUserEntity loggedUser = entityExtranetService.getLoggedUserEntity();
        if (loggedUser.getPrincipalEntity().getId().equals(Entity.AFFIRM)) {
            countries = new ArrayList<>(loggedUser.getCountries().keySet());
        } else {
            countries = Arrays.asList(catalogService.getCountryParam(loggedUser.getPrincipalEntity().getCountryId()));
        }
        if (countries.size() == 1 && form.getCountryId() == null)
            form.setCountryId(countries.get(0).getId());
        if (countries.stream().noneMatch(c -> c.getId().equals(form.getCountryId()))) {
            return AjaxResponse.errorMessage("El pais es invalido");
        }

        User user = userDAO.getUserByDocument(form.getDocType(), form.getDocumentNumber());
        if (user != null) {
            Person person = personDAO.getPerson(catalogService, locale, user.getPersonId(), false);

            if (person == null || person.getName() == null || person.getFirstSurname() == null || person.getLastSurname() == null || person.getBirthday() == null) {
                form.setNonExistingPerson(true);
            } else {
                form.setNonExistingPerson(false);
            }

            form.setName(person != null ? person.getName() : null);
            form.setSurname((person != null && form.getCountryId() == CountryParam.COUNTRY_PERU) ? person.getFirstSurname() : person != null ? person.getFullSurnames() : null);
            form.setLastSurname((person != null && form.getCountryId() == CountryParam.COUNTRY_PERU) ? person.getLastSurname() : null);
            form.setBirthday((person != null && person.getBirthday() != null) ? new SimpleDateFormat("dd/MM/yyyy").format(person.getBirthday()) : null);
            form.setPhone(user.getPhoneNumber());
            form.setEmail(user.getEmail());
        } else if (form.getDocType() != IdentityDocumentType.CE) {
            Reniec reniec = personDAO.getReniecDBData(form.getDocumentNumber());

            if (reniec == null) {
                form.setNonExistingPerson(true);
            } else {
                form.setNonExistingPerson(false);
            }

            form.setName(reniec != null ? reniec.getName() : null);
            form.setSurname(reniec != null ? reniec.getFirstSurname() : null);
            form.setLastSurname(reniec != null ? reniec.getLastSurname() : null);
            form.setBirthday(reniec != null ? new SimpleDateFormat("dd/MM/yyyy").format(reniec.getBirthday()) : null);
            form.setPhone(null);
            form.setEmail(null);
        } else {
            form.setName(null);
            form.setSurname(null);
            form.setLastSurname(null);
            form.setBirthday(null);
            form.setPhone(null);
            form.setEmail(null);
            form.setNonExistingPerson(true);
        }

        configValidator(form, loggedUser);

        model.addAttribute("form", form);
        return "/entityExtranet/createLoanApplications :: create_loan_form";
    }

    private void configValidator(EntityExtranetCreateLoanApplicationForm form, LoggedUserEntity loggedUser) throws Exception {

        Integer entityId = null;
        if (!loggedUser.getPrincipalEntity().getId().equals(Entity.AFFIRM)) {
            entityId = loggedUser.getPrincipalEntity().getId();
        }

        int countryId = form.getCountryId();
        Integer maxInstallments = entityId != null ? productService.getMaxInstalmentsEntity(ProductCategory.CONSUMO, countryId, entityId) : productService.getMaxInstalments(ProductCategory.CONSUMO, countryId);
        Integer minInstallments = entityId != null ? productService.getMinInstalmentsEntity(ProductCategory.CONSUMO, countryId, entityId) : productService.getMinInstalments(ProductCategory.CONSUMO, countryId);
        Integer maxAmount = entityId != null ? productService.getMaxAmountEntity(ProductCategory.CONSUMO, countryId, entityId) : productService.getMaxAmount(ProductCategory.CONSUMO, countryId);
        Integer minAmount = entityId != null ? productService.getMinAmountEntity(ProductCategory.CONSUMO, countryId, entityId) : productService.getMinAmount(ProductCategory.CONSUMO, countryId);

        ((EntityExtranetCreateLoanApplicationForm.Validator) form.getValidator()).configValidator(countryId);
        ((EntityExtranetCreateLoanApplicationForm.Validator) form.getValidator()).installments.setMaxValue(maxInstallments);
        ((EntityExtranetCreateLoanApplicationForm.Validator) form.getValidator()).installments.setMinValue(minInstallments);
        ((EntityExtranetCreateLoanApplicationForm.Validator) form.getValidator()).amount.setMaxValue(maxAmount);
        ((EntityExtranetCreateLoanApplicationForm.Validator) form.getValidator()).amount.setMinValue(minAmount);
        ((EntityExtranetCreateLoanApplicationForm.Validator) form.getValidator()).surname.setFieldName(form.getCountryId() == CountryParam.COUNTRY_PERU ? "Apellido Paterno" : "Apellido");
    }
}
