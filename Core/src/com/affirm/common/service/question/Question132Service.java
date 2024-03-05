package com.affirm.common.service.question;

import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.model.catalog.CountryParam;
import com.affirm.common.model.catalog.CustomProfession;
import com.affirm.common.model.form.Question131Form;
import com.affirm.common.model.form.Question132Form;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.util.AjaxResponse;
import com.affirm.common.util.FormGeneric;
import com.affirm.common.util.ResponseEntityException;
import com.affirm.system.configuration.Configuration;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

@Service("question132Service")
public class Question132Service extends AbstractQuestionService<Question132Form> {

    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private Question26Service question26Service;
    @Autowired
    private Question27Service question27Service;
    @Autowired
    private Question88Service question88Service;
    @Autowired
    private Question82Service question82Service;
    @Autowired
    private Question28Service question28Service;
    @Autowired
    private Question29Service question29Service;
    @Autowired
    private Question30Service question30Service;
    @Autowired
    private Question31Service question31Service;
    @Autowired
    private Question131Service question131Service;
    @Autowired
    private Question32Service question32Service;
    @Autowired
    private Question38Service question38Service;
    @Autowired
    private Question90Service question90Service;
    @Autowired
    private Question61Service question61Service;
    @Autowired
    private Question46Service question46Service;
    @Autowired
    private Question33Service question33Service;
    @Autowired
    private Question89Service question89Service;
    @Autowired
    private Question34Service question34Service;
    @Autowired
    private Question37Service question37Service;
    @Autowired
    private Question92Service question92Service;
    @Autowired
    private Question91Service question91Service;
    @Autowired
    private Question47Service question47Service;
    @Autowired
    private Question98Service question98Service;
    @Autowired
    private Question120Service question120Service;
    @Autowired
    private Question40Service question40Service;
    @Autowired
    private Question41Service question41Service;
    @Autowired
    private Question42Service question42Service;
    @Autowired
    private Question43Service question43Service;
    @Autowired
    private Question44Service question44Service;
    @Autowired
    private Question45Service question45Service;
    @Autowired
    private Question35Service question35Service;
    @Autowired
    private Question36Service question36Service;
    @Autowired
    private Question104Service question104Service;
    @Autowired
    private Question39Service question39Service;
    @Autowired
    private Question99Service question99Service;
    @Autowired
    private Question100Service question100Service;
    @Autowired
    private Question101Service question101Service;
    @Autowired
    private Question103Service question103Service;

    @Override
    public Map<String, Object> getViewAttributes(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean fillSavedData, Map<String, Object> params) throws Exception {
        Map<String, Object> attributes = new HashMap<>();
        switch (flowType) {
            case LOANAPPLICATION:

                Map<Integer, Map<String, Object>> questionAttrs = new HashMap<>();
                questionAttrs.put(26, question26Service.getViewAttributes(flowType, id, locale, fillSavedData, params));
                questionAttrs.put(27, question27Service.getViewAttributes(flowType, id, locale, fillSavedData, params));
                questionAttrs.put(88, question88Service.getViewAttributes(flowType, id, locale, fillSavedData, params));
                questionAttrs.put(82, question82Service.getViewAttributes(flowType, id, locale, fillSavedData, params));
                questionAttrs.put(28, question28Service.getViewAttributes(flowType, id, locale, fillSavedData, params));
                questionAttrs.put(29, question29Service.getViewAttributes(flowType, id, locale, fillSavedData, params));
                questionAttrs.put(30, question30Service.getViewAttributes(flowType, id, locale, fillSavedData, params));
                questionAttrs.put(31, question31Service.getViewAttributes(flowType, id, locale, fillSavedData, params));
                questionAttrs.put(131, question131Service.getViewAttributes(flowType, id, locale, fillSavedData, params));
                questionAttrs.put(32, question32Service.getViewAttributes(flowType, id, locale, fillSavedData, params));
                questionAttrs.put(38, question38Service.getViewAttributes(flowType, id, locale, fillSavedData, params));
                questionAttrs.put(90, question90Service.getViewAttributes(flowType, id, locale, fillSavedData, params));
                questionAttrs.put(61, question61Service.getViewAttributes(flowType, id, locale, fillSavedData, params));
                questionAttrs.put(46, question46Service.getViewAttributes(flowType, id, locale, fillSavedData, params));
                questionAttrs.put(33, question33Service.getViewAttributes(flowType, id, locale, fillSavedData, params));
                questionAttrs.put(89, question89Service.getViewAttributes(flowType, id, locale, fillSavedData, params));
                questionAttrs.put(34, question34Service.getViewAttributes(flowType, id, locale, fillSavedData, params));
                questionAttrs.put(37, question37Service.getViewAttributes(flowType, id, locale, fillSavedData, params));
                questionAttrs.put(92, question92Service.getViewAttributes(flowType, id, locale, fillSavedData, params));
                questionAttrs.put(91, question91Service.getViewAttributes(flowType, id, locale, fillSavedData, params));
                questionAttrs.put(47, question47Service.getViewAttributes(flowType, id, locale, fillSavedData, params));
                questionAttrs.put(98, question98Service.getViewAttributes(flowType, id, locale, fillSavedData, params));
                questionAttrs.put(120, question120Service.getViewAttributes(flowType, id, locale, fillSavedData, params));
                questionAttrs.put(40, question40Service.getViewAttributes(flowType, id, locale, fillSavedData, params));
                questionAttrs.put(41, question41Service.getViewAttributes(flowType, id, locale, fillSavedData, params));
                questionAttrs.put(42, question42Service.getViewAttributes(flowType, id, locale, fillSavedData, params));
                questionAttrs.put(43, question43Service.getViewAttributes(flowType, id, locale, fillSavedData, params));
                questionAttrs.put(44, question44Service.getViewAttributes(flowType, id, locale, fillSavedData, params));
                questionAttrs.put(45, question45Service.getViewAttributes(flowType, id, locale, fillSavedData, params));
                questionAttrs.put(35, question35Service.getViewAttributes(flowType, id, locale, fillSavedData, params));
                questionAttrs.put(36, question36Service.getViewAttributes(flowType, id, locale, fillSavedData, params));
                questionAttrs.put(104, question104Service.getViewAttributes(flowType, id, locale, fillSavedData, params));
                questionAttrs.put(39, question39Service.getViewAttributes(flowType, id, locale, fillSavedData, params));
                questionAttrs.put(99, question99Service.getViewAttributes(flowType, id, locale, fillSavedData, params));
                questionAttrs.put(100, question100Service.getViewAttributes(flowType, id, locale, fillSavedData, params));
                questionAttrs.put(101, question101Service.getViewAttributes(flowType, id, locale, fillSavedData, params));
                questionAttrs.put(103, question103Service.getViewAttributes(flowType, id, locale, fillSavedData, params));

                attributes.put("questionAttrs", questionAttrs);
                break;
        }
        return attributes;
    }

    @Override
    public String getQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Question132Form form) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                return "DEFAULT";
        }
        return null;
    }

    public Map<Integer, FormGeneric> getQuestionFormsToProcess(Question132Form form, QuestionFlowService.Type flowType, Integer id) throws Exception {
        Map<Integer, FormGeneric> forms = new LinkedHashMap<>();
        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
        if (loanApplication.getCountryId() == CountryParam.COUNTRY_PERU) {
            forms.put(26, form.getQuestion26Form());
            if (question26Service.getQuestionResultToGo(flowType, id, form.getQuestion26Form()).equals("DEPENDENT")) {
                forms.put(131, form.getQuestion131Form());
                forms.put(27, form.getQuestion27Form());
                forms.put(88, form.getQuestion88Form());
                forms.put(82, form.getQuestion82Form());
                forms.put(28, form.getQuestion28Form());
                forms.put(29, form.getQuestion29Form());
                forms.put(30, form.getQuestion30Form());
                if (question30Service.getQuestionResultToGo(flowType, id, form.getQuestion30Form()).equals("VARIABLE_INCOME")) {
                    forms.put(31, form.getQuestion31Form());
                }
            } else if (question26Service.getQuestionResultToGo(flowType, id, form.getQuestion26Form()).equals("INDEPENDENT")) {
                forms.put(32, form.getQuestion32Form());
                if (question32Service.getQuestionResultToGo(flowType, id, form.getQuestion32Form()).equals("OWN")) {
                    form.setQuestion131Form(new Question131Form());
                    form.getQuestion131Form().setCustomProfession(CustomProfession.BUSINNES_OWNER);
                    forms.put(131, form.getQuestion131Form());
                    forms.put(38, form.getQuestion38Form());
                    forms.put(90, form.getQuestion90Form());
                    forms.put(61, form.getQuestion61Form());
                    forms.put(46, form.getQuestion46Form());    
                } else if (question32Service.getQuestionResultToGo(flowType, id, form.getQuestion32Form()).equals("PROFESSIONAL_SERVICES")) {
                    forms.put(131, form.getQuestion131Form());
                    forms.put(33, form.getQuestion33Form());
                    forms.put(89, form.getQuestion89Form());
                    forms.put(34, form.getQuestion34Form());
                    forms.put(37, form.getQuestion37Form());
                } else if (question32Service.getQuestionResultToGo(flowType, id, form.getQuestion32Form()).equals("RENT")) {
                    forms.put(131, form.getQuestion131Form());
                    forms.put(92, form.getQuestion92Form());
                    forms.put(89, form.getQuestion89Form());
                    forms.put(34, form.getQuestion34Form());
                }
            } else if (question26Service.getQuestionResultToGo(flowType, id, form.getQuestion26Form()).equals("PENSIONER")) {
                forms.put(98, form.getQuestion98Form());
            }
        } else if (loanApplication.getCountryId() == CountryParam.COUNTRY_ARGENTINA) {
            forms.put(26, form.getQuestion26Form());
            if (question26Service.getQuestionResultToGo(flowType, id, form.getQuestion26Form()).equals("DEPENDENT")) {
                forms.put(27, form.getQuestion27Form());
                forms.put(88, form.getQuestion88Form());
                forms.put(82, form.getQuestion82Form());
                forms.put(28, form.getQuestion28Form());
                forms.put(29, form.getQuestion29Form());
                forms.put(30, form.getQuestion30Form());
                if (question30Service.getQuestionResultToGo(flowType, id, form.getQuestion30Form()).equals("VARIABLE_INCOME")) {
                    forms.put(31, form.getQuestion31Form());
                }
                forms.put(120, form.getQuestion120Form());
            } else if (question26Service.getQuestionResultToGo(flowType, id, form.getQuestion26Form()).equals("INDEPENDENT")) {
                forms.put(32, form.getQuestion32Form());
                if (question32Service.getQuestionResultToGo(flowType, id, form.getQuestion32Form()).equals("OWN")) {
                    forms.put(90, form.getQuestion90Form());
                    forms.put(40, form.getQuestion40Form());
                    forms.put(41, form.getQuestion41Form());
                    forms.put(42, form.getQuestion42Form());
                    forms.put(43, form.getQuestion43Form());
                    forms.put(44, form.getQuestion44Form());
                    forms.put(45, form.getQuestion45Form());
                } else if (question32Service.getQuestionResultToGo(flowType, id, form.getQuestion32Form()).equals("PROFESSIONAL_SERVICES")) {
                    forms.put(89, form.getQuestion89Form());
                    forms.put(34, form.getQuestion34Form());
                    forms.put(35, form.getQuestion35Form());
                    forms.put(36, form.getQuestion36Form());
                    forms.put(37, form.getQuestion37Form());
                } else if (question32Service.getQuestionResultToGo(flowType, id, form.getQuestion32Form()).equals("RENT")) {
                    forms.put(47, form.getQuestion47Form());
                    forms.put(91, form.getQuestion91Form());
                } else if (question32Service.getQuestionResultToGo(flowType, id, form.getQuestion32Form()).equals("SHAREHOLDER")) {
                    forms.put(38, form.getQuestion38Form());
                    forms.put(104, form.getQuestion104Form());
                    forms.put(39, form.getQuestion39Form());
                    forms.put(61, form.getQuestion61Form());
                    forms.put(40, form.getQuestion40Form());
                    forms.put(41, form.getQuestion41Form());
                    forms.put(42, form.getQuestion42Form());
                    forms.put(43, form.getQuestion43Form());
                    forms.put(44, form.getQuestion44Form());
                    forms.put(45, form.getQuestion45Form());
                    forms.put(46, form.getQuestion46Form());
                }
            } else if (question26Service.getQuestionResultToGo(flowType, id, form.getQuestion26Form()).equals("PENSIONER")) {
                forms.put(98, form.getQuestion98Form());
            } else if (question26Service.getQuestionResultToGo(flowType, id, form.getQuestion26Form()).equals("MONOTRIBUTISTA")) {
                forms.put(99, form.getQuestion99Form());
                forms.put(100, form.getQuestion100Form());
                forms.put(101, form.getQuestion101Form());
                forms.put(103, form.getQuestion103Form());
            }
        }
        return forms;
    }

    @Override
    protected void validateForm(QuestionFlowService.Type flowType, Integer id, Question132Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                Map<Integer, FormGeneric> forms = getQuestionFormsToProcess(form, flowType, id);
                for (Map.Entry<Integer, FormGeneric> entry : forms.entrySet()) {
                    Object questionService = applicationContext.getBean("question" + entry.getKey() + "Service");
                    ((AbstractQuestionService) questionService).validateForm(flowType, id, entry.getValue(), locale);
                    if (entry.getValue().getValidator().isHasErrors()) {
                        JSONObject jsonError = new JSONObject(entry.getValue().getValidator().getErrorsJson());
                        jsonError.put("questionId", entry.getKey());
                        throw new ResponseEntityException(AjaxResponse.errorFormValidation(jsonError.toString()));
                    }
                }
                break;
        }
    }

    @Override
    public void saveData(QuestionFlowService.Type flowType, Integer id, Question132Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                Map<Integer, FormGeneric> forms = getQuestionFormsToProcess(form, flowType, id);
                for (Map.Entry<Integer, FormGeneric> entry : forms.entrySet()) {
                    Object questionService = applicationContext.getBean("question" + entry.getKey() + "Service");
                    ((AbstractQuestionService) questionService).saveData(flowType, id, entry.getValue(), locale);
                }
                break;
        }
    }

    @Override
    public String getSkippedQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean saveData) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                break;
        }
        return null;
    }

    @Override
    public Object customMethod(String path, QuestionFlowService.Type flowType, Integer id, Locale locale, Map<String, Object> params) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, locale);
                switch (path) {
                    case "homeAddress":
                        JSONObject jsonAddress = question82Service.getJsonHomeAddress(loanApplication, locale);
                        if (jsonAddress != null)
                            return AjaxResponse.ok(jsonAddress.toString());

                        return AjaxResponse.errorMessage("No tienes dirección registrada");
                    case "occupation":
//                        Question26Form question26Form = new Question26Form();
//                        question26Form.setActivityType((int) params.get("activityType"));
//                        question26Service.saveData(flowType, id, question26Form, Configuration.getDefaultLocale());

                        Map<String, Object> paramsProfessions = new HashMap<>();
                        paramsProfessions.put("activityTypeId", params.get("activityType"));
                        return question131Service.customMethod("getCustomProfessions", flowType, loanApplication.getId(), locale, paramsProfessions);
                }
                break;
        }
        throw new Exception("No method configured");
    }
}
