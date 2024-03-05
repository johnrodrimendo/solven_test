package com.affirm.common.model.transactional;

import com.affirm.common.model.catalog.Agent;
import com.affirm.common.model.catalog.CountryParam;
import com.affirm.common.model.catalog.CreditUsage;
import com.affirm.common.model.catalog.LoanApplicationReason;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by jrodriguez on 09/06/16.
 */

public class SelfEvaluation implements Serializable {

    private Integer id;
    private Integer personId;
    private Integer currentQuestionId;
    private LoanApplicationReason reason;
    private CreditUsage usage;
    private Integer installments;
    private Double amount;
    private Double fixedGrossIncome;
    private Integer score;
    private JSONObject jsonScore;
    private Date registerDate;
    private Double downPayment;
    private Agent agent;
    private List<ProcessQuestionSequence> questionSequence = new ArrayList<>();
    private CountryParam countryParam;
    private List<Integer> bots;

    public void fillFromDb(JSONObject json, CatalogService catalog, Locale locale) {
        setId(JsonUtil.getIntFromJson(json, "self_evaluation_id", null));
        setPersonId(JsonUtil.getIntFromJson(json, "person_id", null));
        setCurrentQuestionId(JsonUtil.getIntFromJson(json, "current_process_question_id", null));
        if (JsonUtil.getIntFromJson(json, "loan_reason_id", null) != null)
            setReason(catalog.getLoanApplicationReason(locale, (JsonUtil.getIntFromJson(json, "loan_reason_id", null))));
        if (JsonUtil.getIntFromJson(json, "ussage_id", null) != null)
            setUsage(catalog.getCreditUsage(locale, JsonUtil.getIntFromJson(json, "ussage_id", null)));
        setInstallments(JsonUtil.getIntFromJson(json, "installments", null));
        setAmount(JsonUtil.getDoubleFromJson(json, "amount", null));
        setFixedGrossIncome(JsonUtil.getDoubleFromJson(json, "fixed_gross_income", null));
        setScore(JsonUtil.getIntFromJson(json, "score", null));
        setJsonScore(JsonUtil.getJsonObjectFromJson(json, "js_score", null));
        setRegisterDate(JsonUtil.getPostgresDateFromJson(json, "register_date", null));
        if (JsonUtil.getJsonArrayFromJson(json, "proccess_question_id_sequence", null) != null)
            setQuestionSequence(new Gson().fromJson(JsonUtil.getJsonArrayFromJson(json, "proccess_question_id_sequence", null).toString(), new TypeToken<ArrayList<ProcessQuestionSequence>>() {
            }.getType()));
        setDownPayment(JsonUtil.getDoubleFromJson(json, "down_payment", null));
        if (JsonUtil.getIntFromJson(json, "form_assistant_id", null) != null)
            setAgent(catalog.getAgent(JsonUtil.getIntFromJson(json, "form_assistant_id", null)));
        if (JsonUtil.getIntFromJson(json, "country_id", null) != null)
            setCountryParam(catalog.getCountryParam(JsonUtil.getIntFromJson(json, "country_id", null)));
        if (JsonUtil.getJsonArrayFromJson(json, "js_bot_id", null) != null) {
            setBots(new Gson().fromJson(JsonUtil.getJsonArrayFromJson(json, "js_bot_id", null).toString(), new TypeToken<ArrayList<Integer>>() {
            }.getType()));
        }

    }

    public String getScoreLabel() {
        if (score != null)
            if (countryParam.getId() == CountryParam.COUNTRY_PERU)
                switch (score) {
                    case 1:
                        return "Malo";
                    case 2:
                        return "Regular";
                    case 3:
                        return "Bueno";
                    case 4:
                        return "Muy bueno";
                    case 5:
                        return "Excelente";
                }
            else if (countryParam.getId() == CountryParam.COUNTRY_ARGENTINA)
                switch (score) {
                    case -1:
                        return "Negativo";
                    case 0:
                        return "Neutro";
                    case 1:
                        return "Positivo";
                }
        return "";
    }

    public String getScoreMessage() {
        return getJsonScore() != null ? JsonUtil.getStringFromJson(getJsonScore(), "message", null) : null;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPersonId() {
        return personId;
    }

    public void setPersonId(Integer personId) {
        this.personId = personId;
    }

    public LoanApplicationReason getReason() {
        return reason;
    }

    public void setReason(LoanApplicationReason reason) {
        this.reason = reason;
    }

    public CreditUsage getUsage() {
        return usage;
    }

    public void setUsage(CreditUsage usage) {
        this.usage = usage;
    }

    public Integer getInstallments() {
        return installments;
    }

    public void setInstallments(Integer installments) {
        this.installments = installments;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getFixedGrossIncome() {
        return fixedGrossIncome;
    }

    public void setFixedGrossIncome(Double fixedGrossIncome) {
        this.fixedGrossIncome = fixedGrossIncome;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public JSONObject getJsonScore() {
        return jsonScore;
    }

    public void setJsonScore(JSONObject jsonScore) {
        this.jsonScore = jsonScore;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public Integer getCurrentQuestionId() {
        return currentQuestionId;
    }

    public void setCurrentQuestionId(Integer currentQuestionId) {
        this.currentQuestionId = currentQuestionId;
    }

    public List<ProcessQuestionSequence> getQuestionSequence() {
        return questionSequence;
    }

    public void setQuestionSequence(List<ProcessQuestionSequence> questionSequence) {
        this.questionSequence = questionSequence;
    }

    public Double getDownPayment() {
        return downPayment;
    }

    public void setDownPayment(Double downPayment) {
        this.downPayment = downPayment;
    }

    public Agent getAgent() {
        return agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }

    public CountryParam getCountryParam() {
        return countryParam;
    }

    public void setCountryParam(CountryParam countryParam) {
        this.countryParam = countryParam;
    }

    public List<Integer> getBots() {
        return bots;
    }

    public void setBots(List<Integer> bots) {
        this.bots = bots;
    }
}