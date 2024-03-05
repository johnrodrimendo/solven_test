package com.affirm.acquisition.controller;

import com.affirm.client.model.form.ProcessContactForm;
import com.affirm.common.dao.SelfEvaluationDAO;
import com.affirm.common.model.annotation.ErrorControllerAnnotation;
import com.affirm.common.model.transactional.SelfEvaluation;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.SelfEvaluationService;
import com.affirm.common.util.AjaxResponse;
import com.affirm.common.util.ProcessQuestionResponse;
import com.affirm.system.configuration.Configuration;
import org.apache.log4j.Logger;
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
import java.util.Locale;

/**
 * @author jrodriguez
 */

@Controller
@Scope("request")
public class SelfEvaluationController {

    private static final Logger logger = Logger.getLogger(SelfEvaluationController.class);
    public static final String URL = Configuration.SELF_EVALUATION_CONTROLLER_URL;

    @Autowired
    private MessageSource messageSource;
    @Autowired
    private SelfEvaluationDAO selfEvaluationDao;
    @Autowired
    private SelfEvaluationService selfEvaluationService;
    @Autowired
    private CatalogService catalogService;

    @RequestMapping(value = "/" + URL + "/backwards", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object goBack(
            ModelMap model, Locale locale, HttpServletRequest request,
            @RequestParam(value = "token", required = false) String token) throws Exception {

        SelfEvaluation selfEvaluation = selfEvaluationDao.getSelfEvaluation(selfEvaluationService.getIdFromToken(token), locale);
        return ProcessQuestionResponse.goToQuestion(selfEvaluationService.backward(selfEvaluation, request));
    }

    @RequestMapping(value = "/" + URL, method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public Object getSelfEvaluation(
            ModelMap model, Locale locale, HttpServletRequest request,
            @RequestParam(value = "token", required = false) String token,
            @RequestParam(value = "agentSelected", required = false) Integer agentSelected) throws Exception {

        ProcessContactForm processContactForm = new ProcessContactForm();
        model.addAttribute("processContactForm", processContactForm);
        model.addAttribute("evaluationType", "selfevaluation");

        if (token == null) {
            model.addAttribute("currentQuestion", selfEvaluationService.getSelfEvaluationProcess(null, request).getFirstQuestionId());
            model.addAttribute("agent", catalogService.getHiddenAssistant());
            model.addAttribute("showSelfEvaluationAleternate", true);
            return "loanApplication/formQuestions/formQuestions";
        }

        SelfEvaluation selfEvaluation = selfEvaluationDao.getSelfEvaluation(selfEvaluationService.getIdFromToken(token), locale);

        // If the self evaluation is new, set the current question with the fist in the config
        if (selfEvaluation.getCurrentQuestionId() == null) {
            selfEvaluationService.forwardByResult(selfEvaluation, null, request);
        }

        model.addAttribute("currentQuestion", selfEvaluation.getCurrentQuestionId());
        model.addAttribute("token", token);
        model.addAttribute("evaluationType", "selfevaluation");
        model.addAttribute("agent", selfEvaluation.getAgent());
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
    public Object getSelfEvaluationByToken(
            ModelMap model, Locale locale, HttpServletRequest request,
            @PathVariable("token") String token,
            @RequestParam(value = "showWelcome", required = false, defaultValue = "false") boolean showWelcome) throws Exception {

        Integer selfEvaluationId = selfEvaluationService.getIdFromToken(token);

        if(selfEvaluationId == null) return "404";

        SelfEvaluation selfEvaluation = selfEvaluationDao.getSelfEvaluation(selfEvaluationId, locale);

        if(selfEvaluation == null) return "404";

        ProcessContactForm processContactForm = new ProcessContactForm();
        model.addAttribute("processContactForm", processContactForm);
        model.addAttribute("showWelcome", showWelcome);
        model.addAttribute("showWelcomeMessage", "¡Bienvenido nuevamente! Te llevaré al lugar donde dejaste tu última autoevaluación.");

        // If the self evaluation is new, set the current question with the first in the config
        if (selfEvaluation.getCurrentQuestionId() == null) {
            selfEvaluationService.forwardByResult(selfEvaluation, null, request);
        }

        model.addAttribute("currentQuestion", selfEvaluation.getCurrentQuestionId());
        model.addAttribute("token", token);
        model.addAttribute("evaluationType", "selfevaluation");
        model.addAttribute("agent", selfEvaluation.getAgent());
        return "loanApplication/formQuestions/formQuestions";
    }
}
