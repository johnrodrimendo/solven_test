/**
 *
 */
package com.affirm.common.service.impl;

import com.affirm.common.dao.PersonDAO;
import com.affirm.common.dao.SelfEvaluationDAO;
import com.affirm.common.model.catalog.CountryParam;
import com.affirm.common.model.catalog.ProcessQuestion;
import com.affirm.common.model.catalog.ProcessQuestionsConfiguration;
import com.affirm.common.model.transactional.Person;
import com.affirm.common.model.transactional.ProcessQuestionSequence;
import com.affirm.common.model.transactional.QueryBot;
import com.affirm.common.model.transactional.SelfEvaluation;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.CountryContextService;
import com.affirm.common.service.SelfEvaluationService;
import com.affirm.common.service.WebscrapperService;
import com.affirm.common.util.CryptoUtil;
import com.affirm.common.util.JsonUtil;
import com.affirm.system.configuration.Configuration;
import com.google.gson.Gson;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * @author jrodriguez
 */

@Service("selfEvaluationService")
public class SelfEvaluationServiceImpl implements SelfEvaluationService {

    private static Logger logger = Logger.getLogger(SelfEvaluationServiceImpl.class);

    @Autowired
    private CatalogService catalogService;
    @Autowired
    private SelfEvaluationDAO selfEvaluationDao;
    @Autowired
    private CountryContextService countryContextService;
    @Autowired
    private WebscrapperService webscrapperService;
    @Autowired
    private PersonDAO personDao;

    @Override
    public String generateSelfEvaluationToken(int selfEvaluationId) {
        JSONObject jsonParam = new JSONObject();
        jsonParam.put("selfevaluation", selfEvaluationId);
        return CryptoUtil.encrypt(jsonParam.toString());
    }

    @Override
    public Integer getIdFromToken(String token) throws Exception {
        String decrypt = CryptoUtil.decrypt(token);
        if (decrypt == null)
            return null;

        JSONObject jsonObject = new JSONObject(decrypt);
        return JsonUtil.getIntFromJson(jsonObject, "selfevaluation", null);
    }

    @Override
    public ProcessQuestionsConfiguration getSelfEvaluationProcess(SelfEvaluation selfEvaluation, HttpServletRequest request) throws Exception {
        CountryParam countryParam;
        if (selfEvaluation != null)
            countryParam = selfEvaluation.getCountryParam();
        else
            countryParam = countryContextService.getCountryParamsByRequest(request);

        ProcessQuestionsConfiguration config = new ProcessQuestionsConfiguration();
        config.fillFromDb(new JSONObject(countryParam.getSelfEvaluationprocessQuestion()));
        return config;
    }

    @Override
    public Integer getQuestionIdByResult(SelfEvaluation selfEvaluation, int questionId, String processQuestionResult, HttpServletRequest request) throws Exception {
        ProcessQuestion processQuestion = getSelfEvaluationProcess(selfEvaluation, request).getQuestions().stream().filter(q -> q.getId() == questionId).findFirst().orElse(null);
        if (processQuestion != null) {
            return processQuestion.getResultQuestionId(processQuestionResult);
        }
        return null;
    }

    @Override
    public Integer forwardByResult(SelfEvaluation selfEvaluation, String processQuestionResult, HttpServletRequest request) throws Exception {

        Integer questionIdtoUpdate;

        // If there is no current question, update with the first question in configuration
        if (selfEvaluation.getCurrentQuestionId() == null) {
            questionIdtoUpdate = getSelfEvaluationProcess(selfEvaluation, request).getFirstQuestionId();
        }
        // Else, update with the result question
        else {
            questionIdtoUpdate = getQuestionIdByResult(selfEvaluation, selfEvaluation.getCurrentQuestionId(), processQuestionResult, request);
        }
        selfEvaluationDao.updateCurrentQuestion(selfEvaluation.getId(), questionIdtoUpdate);
        selfEvaluation.setCurrentQuestionId(questionIdtoUpdate);

        // Update finish date last question
        if (!selfEvaluation.getQuestionSequence().isEmpty())
            selfEvaluation.getQuestionSequence().get(selfEvaluation.getQuestionSequence().size() - 1).setFinishDate(new Date());

        // Update sequence
        selfEvaluation.getQuestionSequence().add(new ProcessQuestionSequence(questionIdtoUpdate, ProcessQuestionSequence.TYPE_FORWARD, new Date(), null, request != null ? request.getHeader("User-Agent") : null));
        selfEvaluationDao.updateQuestionSequence(selfEvaluation.getId(), new Gson().toJson(selfEvaluation.getQuestionSequence()));

        return questionIdtoUpdate;
    }

    @Override
    public Integer backward(SelfEvaluation selfEvaluation, HttpServletRequest request) throws Exception {

        Integer questionIdtoUpdate = null;

        // If there is no current question, update with the first question in configuration
        if (selfEvaluation.getQuestionSequence().size() > 1) {
            for (int i = selfEvaluation.getQuestionSequence().size() - 1; i >= 0; i--) {
                ProcessQuestionSequence question = selfEvaluation.getQuestionSequence().get(i);
                if (question.getId().intValue() == selfEvaluation.getCurrentQuestionId() && question.getType() == ProcessQuestionSequence.TYPE_FORWARD) {
                    questionIdtoUpdate = selfEvaluation.getQuestionSequence().get(i - 1).getId();
                    break;
                }
            }
        }
        if (questionIdtoUpdate == null) {
            throw new Exception("Cant go back!!");
        }

        selfEvaluationDao.updateCurrentQuestion(selfEvaluation.getId(), questionIdtoUpdate);
        selfEvaluation.setCurrentQuestionId(questionIdtoUpdate);

        // Update finish date last question
        if (!selfEvaluation.getQuestionSequence().isEmpty())
            selfEvaluation.getQuestionSequence().get(selfEvaluation.getQuestionSequence().size() - 1).setFinishDate(new Date());

        // Update sequence
        selfEvaluation.getQuestionSequence().add(new ProcessQuestionSequence(questionIdtoUpdate, ProcessQuestionSequence.TYPE_BACKWARD, new Date(), null, request != null ? request.getHeader("User-Agent") : null));
        selfEvaluationDao.updateQuestionSequence(selfEvaluation.getId(), new Gson().toJson(selfEvaluation.getQuestionSequence()));

        return questionIdtoUpdate;
    }

    @Override
    public String getCalificationLabel(Integer calification) {
        if (calification == null)
            return "";

        String label = "";
        switch (calification) {
            case 1:
                label = "Malo";
                break;
            case 2:
                label = "Regular";
                break;
            case 3:
                label = "Bueno";
                break;
            case 4:
                label = "Muy bueno";
                break;
            case 5:
                label = "Excelente";
                break;
        }
        return label;
    }

    @Override
    public void runSelfEvaluationArgentina(SelfEvaluation selfEvaluation) throws Exception{
        Person person = personDao.getPerson(catalogService, Configuration.getDefaultLocale(), selfEvaluation.getPersonId(), false);
        webscrapperService.setCountry(person.getCountry());
        // Run bots
        QueryBot BCRAQueryBot = webscrapperService.callBCRABot(person.getDocumentType().getId(), person.getDocumentNumber(), person.getUserId());
        QueryBot AFIPQueryBot = webscrapperService.callAFIPBot(person.getDocumentType().getId(), person.getDocumentNumber(), person.getUserId());
        QueryBot ANSESQueryBot = webscrapperService.callANSESBot(person.getDocumentType().getId(), person.getDocumentNumber(), person.getUserId());

        selfEvaluationDao.updateBots(selfEvaluation.getId(), new Integer[]{BCRAQueryBot.getId(), AFIPQueryBot.getId(), ANSESQueryBot.getId()});
    }
}
