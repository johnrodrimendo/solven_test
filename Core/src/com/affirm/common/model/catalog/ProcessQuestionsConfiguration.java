package com.affirm.common.model.catalog;

import com.affirm.common.util.JsonUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by john on 08/11/16.
 */
public class ProcessQuestionsConfiguration implements Serializable {

    private Integer firstQuestionId;
    private Integer secondQuestionId;
    private Integer fromSelfEvaluationQuestionId;
    private Integer afterValidationQuestionId;
    private List<ProcessQuestion> questions;

    public void fillFromDb(JSONObject json) {
        setFirstQuestionId(JsonUtil.getIntFromJson(json, "firstQuestionId", null));
        setFromSelfEvaluationQuestionId(JsonUtil.getIntFromJson(json, "fromSelfEvaluationQuestionId", null));
        setAfterValidationQuestionId(JsonUtil.getIntFromJson(json, "afterValidationQuestionId", null));
        if (JsonUtil.getJsonArrayFromJson(json, "questions", null) != null) {
            setQuestions(new ArrayList<>());
            JSONArray arrayQuestions = JsonUtil.getJsonArrayFromJson(json, "questions", null);
            for (int i = 0; i < arrayQuestions.length(); i++) {
                ProcessQuestion question = new ProcessQuestion();
                question.fillFromProcessQuestionConfiguration(arrayQuestions.getJSONObject(i));
                getQuestions().add(question);
            }
        }
    }

    public Integer getFirstQuestionId() {
        return firstQuestionId;
    }

    public void setFirstQuestionId(Integer firstQuestionId) {
        this.firstQuestionId = firstQuestionId;
    }

    public List<ProcessQuestion> getQuestions() {
        return questions;
    }

    public void setQuestions(List<ProcessQuestion> questions) {
        this.questions = questions;
    }

    public String toJson() {
        return new JSONObject(this).toString();
    }

    public Integer getFromSelfEvaluationQuestionId() {
        return fromSelfEvaluationQuestionId;
    }

    public void setFromSelfEvaluationQuestionId(Integer fromSelfEvaluationQuestionId) {
        this.fromSelfEvaluationQuestionId = fromSelfEvaluationQuestionId;
    }

    public Integer getSecondQuestionId() {
        return secondQuestionId;
    }

    public void setSecondQuestionId(Integer secondQuestionId) {
        this.secondQuestionId = secondQuestionId;
    }

    public Integer getAfterValidationQuestionId() {
        return afterValidationQuestionId;
    }

    public void setAfterValidationQuestionId(Integer afterValidationQuestionId) {
        this.afterValidationQuestionId = afterValidationQuestionId;
    }
}
