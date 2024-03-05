package com.affirm.acquisition.controller;

import com.affirm.client.model.form.ContactForm;
import com.affirm.client.model.form.ProcessContactForm;
import com.affirm.common.dao.ComparisonDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.dao.SelfEvaluationDAO;
import com.affirm.common.model.annotation.ErrorControllerAnnotation;
import com.affirm.common.model.catalog.Agent;
import com.affirm.common.model.catalog.FundableBankComparisonCategory;
import com.affirm.common.model.catalog.ProductCategory;
import com.affirm.common.model.transactional.Comparison;
import com.affirm.common.model.transactional.SelfEvaluation;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.ComparisonService;
import com.affirm.common.service.impl.ComparisonServiceImpl;
import com.affirm.common.util.*;
import com.affirm.system.configuration.Configuration;
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

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.Locale;

/**
 * @author jrodriguez
 */

@Controller
@Scope("request")
public class ComparisonController {

    private static final Logger logger = Logger.getLogger(ComparisonController.class);
    public static final String URL = Configuration.COMPARISON_CONTROLLER_URL;

    @Autowired
    private MessageSource messageSource;
    @Autowired
    private ComparisonDAO comparisonDao;
    @Autowired
    private ComparisonService comparisonService;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private SelfEvaluationDAO selfEvaluationDao;

    @Autowired
    private PersonDAO personDAO;

    @RequestMapping(value = "/" + URL + "/backwards", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object goBack(
            ModelMap model, Locale locale, HttpServletRequest request,
            @RequestParam(value = "token", required = false) String token) throws Exception {

        Comparison comparison = comparisonDao.getComparison(comparisonService.getIdFromToken(token), locale);
        return ProcessQuestionResponse.goToQuestion(comparisonService.backward(comparison, request));
    }

    @RequestMapping(value = "/" + URL, method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public Object getComparison(
            ModelMap model, Locale locale, HttpServletRequest request,
            @RequestParam(value = "token", required = false) String token,
            @RequestParam(value = "agentSelected", required = false) Integer agentSelected) throws Exception {

        ProcessContactForm processContactForm = new ProcessContactForm();
        model.addAttribute("processContactForm", processContactForm);
        model.addAttribute("evaluationType", "comparison");

        if (token == null) {
            if (agentSelected == null) {
                model.addAttribute("urlToPOst", "/" + URL + "/agent");
                return "loanApplication/formQuestions/chooseAgent";
            } else {
                Agent agent = catalogService.getAgent(agentSelected);
                if (agent == null)
                    return "redirect:/" + URL;
                Comparison comparison = comparisonDao.registerComparison(agent.getId());
                comparisonService.forwardByResult(comparison, null, request);

                model.addAttribute("currentQuestion", comparisonService.getComparisonProcess(comparison).getFirstQuestionId());
                model.addAttribute("token", comparisonService.generateComparisonToken(comparison.getId()));
                model.addAttribute("evaluationType", "comparison");
                model.addAttribute("agent", agent);
                return "loanApplication/formQuestions/formQuestions";
            }
        }

        Comparison comparison = comparisonDao.getComparison(comparisonService.getIdFromToken(token), locale);

        // If the comparison is new, set the current question with the fist in the config
        if (comparison.getCurrentQuestionId() == null) {
            comparisonService.forwardByResult(comparison, null, request);
        }

        model.addAttribute("currentQuestion", comparison.getCurrentQuestionId());
        model.addAttribute("token", token);
        model.addAttribute("evaluationType", "comparison");
        model.addAttribute("agent", comparison.getAgent());
        return "loanApplication/formQuestions/formQuestions";
    }

    @RequestMapping(value = "/" + URL + "/agent", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public Object sendAgent(
            ModelMap model, Locale locale, HttpServletRequest request,
            @RequestParam("agentSelected") String agentSelected) throws Exception {
        return AjaxResponse.redirect(request.getContextPath() + "/" + URL + "?agentSelected=" + agentSelected);
    }

    @RequestMapping(value = "/" + URL + "/{token}", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public Object getComparisonByToken(
            ModelMap model, Locale locale, HttpServletRequest request,
            @PathVariable("token") String token) throws Exception {

        Comparison comparison = comparisonDao.getComparison(comparisonService.getIdFromToken(token), locale);

        ProcessContactForm processContactForm = new ProcessContactForm();
        model.addAttribute("processContactForm", processContactForm);

        ContactForm contactForm = new ContactForm();
        model.addAttribute("contactForm", contactForm);

        // If the comparison is new, set the current question with the first in the config
        if (comparison.getCurrentQuestionId() == null) {
            comparisonService.forwardByResult(comparison, null, request);
        } else if (comparison.getExecuted()) {
            SelfEvaluation selfEvaluation = comparison.getSelfEvaluationId() != null ? selfEvaluationDao.getSelfEvaluation(comparison.getSelfEvaluationId(), locale) : null;
            ComparisonUpdateForm form = new ComparisonUpdateForm();
            form.setAmount(comparison.getAmount().intValue());
            form.setInstallments(comparison.getInstallments());
            if (selfEvaluation != null && selfEvaluation.getScore() != null) {
                form.setRateType(ComparisonServiceImpl.COMPARISON_RATE_TYPE_PERSONALIZED);
                model.addAttribute("comparisonResults", comparisonService.executeComparison(comparison, locale, ComparisonServiceImpl.COMPARISON_RATE_TYPE_PERSONALIZED, selfEvaluation.getScore(), 0));
            } else {
                form.setRateType(ComparisonServiceImpl.COMPARISON_RATE_TYPE_MIN);
                model.addAttribute("comparisonResults", comparisonService.executeComparison(comparison, locale, ComparisonServiceImpl.COMPARISON_RATE_TYPE_MIN, null, 0));
            }
            String iconClass = catalogService.getComparisonReason(comparison.getComparisonReasonId(), locale).getIcon();
            model.addAttribute("iconClass", iconClass);
            model.addAttribute("comparison", comparison);
            model.addAttribute("rateType", form.getRateType());
            model.addAttribute("form", form);
            model.addAttribute("personName", selfEvaluation != null ? personDAO.getPerson(catalogService, locale, selfEvaluation.getPersonId(), false).getFirstName() : null);
            return "/loanApplication/formQuestions/comparator";
        }

        model.addAttribute("currentQuestion", comparison.getCurrentQuestionId());
        model.addAttribute("token", token);
        model.addAttribute("evaluationType", "comparison");
        model.addAttribute("agent", comparison.getAgent());
        return "loanApplication/formQuestions/formQuestions";
    }

    @RequestMapping(value = "/" + URL + "/updateComparisonResult", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object updateComparisonResult(
            ModelMap model, Locale locale, HttpServletRequest request, ComparisonUpdateForm form,
            @RequestParam("token") String token) throws Exception {

        form.getValidator().validate(locale);
        if (form.getValidator().isHasErrors()) {
            return AjaxResponse.errorFormValidation(form.getValidator().getErrorsJson());
        }

        comparisonDao.updateAmount(comparisonService.getIdFromToken(token), form.getAmount());
        comparisonDao.updateInstallments(comparisonService.getIdFromToken(token), form.getInstallments());

        Comparison comparison = comparisonDao.getComparison(comparisonService.getIdFromToken(token), locale);

        SelfEvaluation selfEvaluation = comparison.getSelfEvaluationId() != null ? selfEvaluationDao.getSelfEvaluation(comparison.getSelfEvaluationId(), locale) : null;
        model.addAttribute("comparisonResults", comparisonService.executeComparison(comparison, locale, form.getRateType(), selfEvaluation != null ? selfEvaluation.getScore() : null, 0));
        model.addAttribute("comparison", comparison);
        model.addAttribute("rateType", form.getRateType());
        model.addAttribute("token", token);
        return "/loanApplication/formQuestions/comparator :: resultsContent";
    }

    @RequestMapping(value = "/" + URL + "/moreResults", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object getMoreResults(
            ModelMap model, Locale locale, HttpServletRequest request,
            @RequestParam("token") String token,
            @RequestParam("offset") int offset,
            @RequestParam("rateType") int rateType) throws Exception {

        Comparison comparison = comparisonDao.getComparison(comparisonService.getIdFromToken(token), locale);
        SelfEvaluation selfEvaluation = comparison.getSelfEvaluationId() != null ? selfEvaluationDao.getSelfEvaluation(comparison.getSelfEvaluationId(), locale) : null;

        model.addAttribute("comparisonResults", comparisonService.executeComparison(comparison, locale, rateType, selfEvaluation != null ? selfEvaluation.getScore() : null, offset));
        model.addAttribute("comparison", comparison);
        model.addAttribute("token", token);
        return "/loanApplication/formQuestions/comparator :: resultsContent";
    }

    @RequestMapping(value = "/" + URL + "/applyForResult", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object applyForResult(
            ModelMap model, Locale locale, HttpServletRequest request,
            @RequestParam("token") String token,
            @RequestParam("bankId") Integer bankId,
            @RequestParam("comparisonCategoryId") Integer comparisonCategoryId) throws Exception {

        FundableBankComparisonCategory fundableBankComparisonCategory = catalogService.getFundableBankComparisonCategory(bankId, comparisonCategoryId);
        if (fundableBankComparisonCategory.getBank().getEntity() == null || fundableBankComparisonCategory.getComparisonCategory().getProduct() == null) {
            return AjaxResponse.errorMessage("La entidad no está asociada con la plataforma.");
        }

        JSONObject jsonParams = new JSONObject();
        jsonParams.put("forcedEntity", fundableBankComparisonCategory.getBank().getEntity().getId());
        jsonParams.put("forcedProduct", fundableBankComparisonCategory.getComparisonCategory().getProduct().getId());
        String externalParams = CryptoUtil.encrypt(jsonParams.toString());
        Comparison comparison = comparisonDao.getComparison(comparisonService.getIdFromToken(token), locale);
        return AjaxResponse.redirect(request.getContextPath() + "/" + ProductCategory.CONSUMO_CATEGORY_URL + "/" + Configuration.EVALUATION_CONTROLLER_URL + "?agentSelected=" + comparison.getAgent().getId() + "&externalParams=" + externalParams);
    }

    public static class ComparisonUpdateForm extends FormGeneric implements Serializable {

        private Integer amount;
        private Integer installments;
        private Integer rateType;

        public ComparisonUpdateForm() {
            this.setValidator(new ComparisonUpdateForm.Validator());
        }

        public class Validator extends FormValidator implements Serializable {

            public IntegerFieldValidator amount;
            public IntegerFieldValidator installments;
            public IntegerFieldValidator rateType;

            public Validator() {
                addValidator(amount = new IntegerFieldValidator(ValidatorUtil.SELF_EVALUATION_AMOUNT));
                addValidator(installments = new IntegerFieldValidator(ValidatorUtil.SELF_EVALUATION_INSTALLMENTS));
                addValidator(rateType = new IntegerFieldValidator().setRequired(true));
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
                return ComparisonUpdateForm.this;
            }
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

        public Integer getRateType() {
            return rateType;
        }

        public void setRateType(Integer rateType) {
            this.rateType = rateType;
        }
    }
}

