package com.affirm.common.service.question;

import com.affirm.common.service.EvaluationService;
import com.affirm.common.service.SelfEvaluationService;
import com.affirm.system.configuration.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("questionFlowService")
public class QuestionFlowService {

    public enum Type {
        SELFEVALUATION,
        LOANAPPLICATION
    }

    @Autowired
    private EvaluationService evaluationService;
    @Autowired
    private SelfEvaluationService selfEvaluationService;

    public String getQuestionHtmlFragmentPath(int questionId) {
        String fragment = "";
        switch (Configuration.getApplicationType()) {
            case CLIENT:
//                return "question/" + questionId + "_question :: question_client";
                return "loanApplication/formQuestions/formQuestions :: question_" + questionId;
            case BACKOFFICE:
                return "question/" + questionId + "_question :: question_backoffice";
        }
        return null;
    }

    public Type getFlowTypeByClientUrl(String url){
        switch (url) {
            case Configuration.EVALUATION_CONTROLLER_URL:
                return Type.LOANAPPLICATION;
            case Configuration.SELF_EVALUATION_CONTROLLER_URL:
                return Type.SELFEVALUATION;
        }
        return null;
    }

    public Integer getFlowIdentifierFromToken(String token, Type type) throws Exception{
        switch (type) {
            case LOANAPPLICATION:
                return evaluationService.getIdFromToken(token);
            case SELFEVALUATION:
                return selfEvaluationService.getIdFromToken(token);
        }
        return null;
    }
}
