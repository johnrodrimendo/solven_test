package com.affirm.backoffice.controller.question;

import com.affirm.common.model.annotation.ErrorControllerAnnotation;
import com.affirm.common.model.catalog.ProcessQuestion;
import com.affirm.common.model.catalog.ProcessQuestionCategory;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.UtilService;
import com.affirm.common.service.question.AbstractQuestionService;
import com.affirm.common.service.question.QuestionFlowService;
import com.affirm.common.util.AjaxResponse;
import com.affirm.common.util.FormGeneric;
import com.affirm.common.util.ResponseEntityException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Controller
@Scope("request")
public class QuestionDefaultController {

    @Autowired
    private QuestionFlowService questionFlowService;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private UtilService utilService;
    @Autowired
    private CatalogService catalogService;

    @RequestMapping(value = "/loanApplication/question/{questionId}", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object getQuestion(
            ModelMap model, Locale locale, HttpServletRequest request,
            @RequestParam("identifier") Integer identifier,
            @PathVariable("questionId") Integer questionId,
            @RequestParam(value = "questionParams", required = false) String questionParams,
            @RequestParam("answeredQuestion") boolean answeredQuestion) throws Exception {

        QuestionFlowService.Type flowType = QuestionFlowService.Type.LOANAPPLICATION;

        Object questionServiceObject = applicationContext.getBean("question" + questionId + "Service");
        if (questionServiceObject != null) {
            Map<String, Object> params = new HashMap<>();
            if(questionParams != null && !questionParams.isEmpty()){
                JSONObject questionParamsJson = new JSONObject(questionParams);
                params = questionParamsJson.toMap();
            }

            Map<String, Object> mapAtr = ((AbstractQuestionService) questionServiceObject).getViewAttributes(flowType, identifier, locale, answeredQuestion, params);
            for (Map.Entry<String, Object> attr : mapAtr.entrySet()) {
                model.addAttribute(attr.getKey(), attr.getValue());
            }
            model.addAttribute("randomId", utilService.getRandomName());
            return new ModelAndView(questionFlowService.getQuestionHtmlFragmentPath(questionId));
        }
        throw new Exception("No existe servicio para la pregunta " + questionId);
    }

//    @RequestMapping(value = "/loanApplication/question/{questionId}/canSkip", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object canSkip(
            ModelMap model, Locale locale, HttpServletRequest request,
            @RequestParam("identifier") Integer identifier,
            @PathVariable("questionId") Integer questionId) throws Exception {

        QuestionFlowService.Type flowType = QuestionFlowService.Type.LOANAPPLICATION;

        Object questionServiceObject = applicationContext.getBean("question" + questionId + "Service");
        if (questionServiceObject != null) {

            String whereToSkip = ((AbstractQuestionService) questionServiceObject).getSkippedQuestionResultToGo(flowType, identifier, locale, false);
            if (whereToSkip != null)
                return AjaxResponse.ok("true");
        }
        return AjaxResponse.ok("false");
    }

    @RequestMapping(value = "/loanApplication/question/{questionId}/validate", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object validateQuestion(
            ModelMap model, Locale locale, HttpServletRequest request,
            @RequestParam("identifier") Integer identifier,
            @PathVariable("questionId") Integer questionId,
            @RequestParam("jsonParams") String jsonParam) throws Exception {

        Object questionServiceObject = applicationContext.getBean("question" + questionId + "Service");

        FormGeneric form = ((AbstractQuestionService) questionServiceObject).fromJsonToForm(jsonParam);

        // These questions doesnt validate!
        if(questionId == ProcessQuestion.Question.Constants.ALL_PERSONAL_INFORMATION || questionId == ProcessQuestion.Question.Constants.ALL_INCOME){
            return AjaxResponse.ok(null);
        }
        ResponseEntity validationResponse = ((AbstractQuestionService) questionServiceObject).validateFormBackoffice(
                QuestionFlowService.Type.LOANAPPLICATION,
                identifier,
                form,
                locale);
        if (validationResponse != null) {
            return validationResponse;
        }

        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = "/loanApplication/question/{questionId}/save", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object saveQuestion(
            ModelMap model, Locale locale, HttpServletRequest request,
            @RequestParam("identifier") Integer identifier,
            @PathVariable("questionId") Integer questionId,
            @RequestParam("jsonParams") String jsonParam) throws Exception {

        Object questionServiceObject = applicationContext.getBean("question" + questionId + "Service");

        FormGeneric form = ((AbstractQuestionService) questionServiceObject).fromJsonToForm(jsonParam);

        String skipResponse = ((AbstractQuestionService) questionServiceObject).getSkippedQuestionResultToGo(
                QuestionFlowService.Type.LOANAPPLICATION,
                identifier,
                locale,
                false);
        if (skipResponse != null) {
            return ((AbstractQuestionService) questionServiceObject).skipQuestionBackoffice(
                    QuestionFlowService.Type.LOANAPPLICATION,
                    identifier,
                    locale,
                    request,
                    questionId);
        }

        ResponseEntity validationResponse = ((AbstractQuestionService) questionServiceObject).validateFormBackoffice(
                QuestionFlowService.Type.LOANAPPLICATION,
                identifier,
                form,
                locale);
        if (validationResponse != null) {
            return validationResponse;
        }

        // Save the data
        try {
            ((AbstractQuestionService) questionServiceObject).saveData(QuestionFlowService.Type.LOANAPPLICATION, identifier, form, locale);
        } catch (ResponseEntityException rex) {
            return rex.getResponseEntity();
        }

        return ((AbstractQuestionService) questionServiceObject).goToNextQuestionBackoffice(
                QuestionFlowService.Type.LOANAPPLICATION,
                identifier,
                form,
                request,
                questionId);
    }

//    @RequestMapping(value = "/loanApplication/question/save", method = RequestMethod.POST)
////    @RequiresPermissionOr403(permissions = "credit:disbursement:register", type = RequiresPermissionOr403.Type.AJAX)
//    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
//    public Object refreshQuestionProcessModal(
//            ModelMap model, Locale locale, HttpServletRequest request,
//            @RequestParam("loanApplicationId") Integer loanApplicationId,
//            @RequestParam("questionId") Integer questionId,
//            @RequestParam("jsonParams") String jsonParam) throws Exception {
//
//        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(loanApplicationId, locale);
//        Object questionServiceObject = applicationContext.getBean("question" + questionId + "Service");
//
//        Integer questionIdToGo = null;
//        if (questionServiceObject != null) {
//            FormGeneric form = ((AbstractQuestionService) questionServiceObject).fromJsonToForm(jsonParam);
//
//            ResponseEntity validationResponse = ((AbstractQuestionService) questionServiceObject).validateFormBackoffice(
//                    QuestionFlowService.Type.LOANAPPLICATION,
//                    loanApplicationId,
//                    form,
//                    locale);
//            if (validationResponse != null) {
//                return validationResponse;
//            }
//
//            questionIdToGo = ((AbstractQuestionService) questionServiceObject).getQuestionIdToGo(
//                    QuestionFlowService.Type.LOANAPPLICATION,
//                    loanApplicationId,
//                    form,
//                    request,
//                    questionId);
//        }
//
//        int categoryId = catalogService.getProcessQuestion(loanApplication.getCurrentQuestionId()).getCategory().getId();
//        TreeProcessQuestionWrapper wrapper = loanApplicationQuestionService.getCurrentCategoryQuestions(loanApplication, questionIdToGo, categoryId);
//        model.addAttribute("treeQuestion", wrapper);
//        if (wrapper == null) {
//            // theres no more questions, so show the button to register the questions
//            return new ModelAndView("fragments/questionProcessFragments :: categorySaveButonDiv");
//        } else {
//            return new ModelAndView("fragments/questionProcessFragments :: questionDiv");
//        }
//    }

//    @RequestMapping(value = "/{evaluationType:" +
//            (EVALUATION ? Configuration.EVALUATION_CONTROLLER_URL : "") + "|" +
//            (SELFEVALUATION ? Configuration.SELF_EVALUATION_CONTROLLER_URL : "") + "}/question/" + QUESTION_ID, method = RequestMethod.POST)
//    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
//    public Object postQuestion(
//            ModelMap model, Locale locale, HttpServletRequest request, Question21Form form,
//            @PathVariable("evaluationType") String evaluationType,
//            @RequestParam(value = "token", required = false) String token) throws Exception {
//
//        QuestionFlowService.Type flowType = questionFlowService.getFlowTypeByClientUrl(evaluationType);
//        Integer identifier = questionFlowService.getFlowIdentifierFromToken(token, flowType);
//
//        // Validate
//        ResponseEntity validationResponse = question21Service.validateFormClient(flowType, identifier, form, locale);
//        if (validationResponse != null) {
//            return validationResponse;
//        }
//
//        // Save the data
//        question21Service.saveData(flowType, identifier, form);
//
//        // Go to next question
//        return question21Service.goToNextQuestionClient(flowType, identifier, form, request);
//    }
}
