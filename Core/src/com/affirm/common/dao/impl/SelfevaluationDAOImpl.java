/**
 *
 */
package com.affirm.common.dao.impl;

import com.affirm.common.dao.JsonResolverDAO;
import com.affirm.common.dao.SelfEvaluationDAO;
import com.affirm.common.model.transactional.SelfEvaluation;
import com.affirm.common.service.CatalogService;
import com.google.gson.Gson;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.util.Locale;

/**
 * @author jrodriguez
 */

@Repository
public class SelfevaluationDAOImpl extends JsonResolverDAO implements SelfEvaluationDAO {


    @Autowired
    private CatalogService catalogService;

    @Override
    public SelfEvaluation registerSelfEvaluation(int countryId) {
        Integer id = queryForObjectEvaluation("select * from evaluation.register_self_evaluation(?)", Integer.class,
                new SqlParameterValue(Types.INTEGER, countryId));
        SelfEvaluation selfEvaluation = new SelfEvaluation();
        selfEvaluation.setId(id);
        selfEvaluation.setCountryParam(catalogService.getCountryParam(countryId));
        return selfEvaluation;
    }

    @Override
    public void updateCurrentQuestion(int selfEvaluationId, int questionId) {
        update("update evaluation.tb_self_evaluation set current_process_question_id = ? where self_evaluation_id = ?",
                EVALUATION_DB,
                new SqlParameterValue(Types.INTEGER, questionId),
                new SqlParameterValue(Types.INTEGER, selfEvaluationId));
    }

    @Override
    public SelfEvaluation getSelfEvaluation(int selfEvaluationId, Locale locale) {
        JSONObject dbJson = queryForObjectEvaluation("select * from evaluation.get_self_evalution(?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, selfEvaluationId));
        if (dbJson == null)
            return null;

        SelfEvaluation selfEvaluation = new SelfEvaluation();
        selfEvaluation.fillFromDb(dbJson, catalogService, locale);
        return selfEvaluation;
    }

    @Override
    public void updateQuestionSequence(int selfEvaluationId, String sequence) {
        update("update evaluation.tb_self_evaluation set proccess_question_id_sequence = ? where self_evaluation_id = ?",
                EVALUATION_DB,
                new SqlParameterValue(Types.OTHER, sequence),
                new SqlParameterValue(Types.INTEGER, selfEvaluationId));
    }

    @Override
    public void updatePerson(int selfEvaluationId, int personId) {
        update("update evaluation.tb_self_evaluation set person_id = ? where self_evaluation_id = ?",
                EVALUATION_DB,
                new SqlParameterValue(Types.INTEGER, personId),
                new SqlParameterValue(Types.INTEGER, selfEvaluationId));
    }

    @Override
    public void updateInstallments(int selfEvaluationId, int installments) {
        update("update evaluation.tb_self_evaluation set installments = ? where self_evaluation_id = ?",
                EVALUATION_DB,
                new SqlParameterValue(Types.INTEGER, installments),
                new SqlParameterValue(Types.INTEGER, selfEvaluationId));
    }

    @Override
    public void updateAmount(int selfEvaluationId, int amount) {
        update("update evaluation.tb_self_evaluation set amount = ? where self_evaluation_id = ?",
                EVALUATION_DB,
                new SqlParameterValue(Types.INTEGER, amount),
                new SqlParameterValue(Types.INTEGER, selfEvaluationId));
    }

    @Override
    public void updateFixedGrossIncome(int selfEvaluationId, int fixedGrossIncome) {
        update("update evaluation.tb_self_evaluation set fixed_gross_income = ? where self_evaluation_id = ?",
                EVALUATION_DB,
                new SqlParameterValue(Types.INTEGER, fixedGrossIncome),
                new SqlParameterValue(Types.INTEGER, selfEvaluationId));
    }

    @Override
    public void updateLoanReason(int selfEvaluationId, int loanReasonId) {
        update("update evaluation.tb_self_evaluation set loan_reason_id = ? where self_evaluation_id = ?",
                EVALUATION_DB,
                new SqlParameterValue(Types.INTEGER, loanReasonId),
                new SqlParameterValue(Types.INTEGER, selfEvaluationId));
    }

    @Override
    public void updateUsage(int selfEvaluationId, int usageId) {
        update("update evaluation.tb_self_evaluation set ussage_id = ? where self_evaluation_id = ?",
                EVALUATION_DB,
                new SqlParameterValue(Types.INTEGER, usageId),
                new SqlParameterValue(Types.INTEGER, selfEvaluationId));
    }

    @Override
    public void updateDownPayment(int selfEvaluationId, int downPayment) {
        update("update evaluation.tb_self_evaluation set down_payment = ? where self_evaluation_id = ?",
                EVALUATION_DB,
                new SqlParameterValue(Types.INTEGER, downPayment),
                new SqlParameterValue(Types.INTEGER, selfEvaluationId));
    }

    @Override
    public void updateFormAssistant(int selfEvaluationId, int formAssistant) {
        update("update evaluation.tb_self_evaluation set form_assistant_id = ? where self_evaluation_id = ?",
                EVALUATION_DB,
                new SqlParameterValue(Types.INTEGER, formAssistant),
                new SqlParameterValue(Types.INTEGER, selfEvaluationId));
    }

    @Override
    public String runSelfEvaluation(int selfEvaluationId) {
        return queryForObjectEvaluation("select * from evaluation.self_evaluation(?)", String.class,
                new SqlParameterValue(Types.INTEGER, selfEvaluationId));
    }

    @Override
    public void updateBots(int selfEvaluationId, Integer[] bots) {
        update("update evaluation.tb_self_evaluation set js_bot_id = ? where self_evaluation_id = ?",
                EVALUATION_DB,
                new SqlParameterValue(Types.OTHER, bots != null ? new Gson().toJson(bots) : null),
                new SqlParameterValue(Types.INTEGER, selfEvaluationId));
    }

    @Override
    public SelfEvaluation getActiveSelfEvaluationByPerson(int personId, Locale locale) {
        JSONObject dbJson = queryForObjectEvaluation("select * from evaluation.get_last_self_evaluation(?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, personId));
        if (dbJson == null)
            return null;

        SelfEvaluation selfEvaluation = new SelfEvaluation();
        selfEvaluation.fillFromDb(dbJson, catalogService, locale);
        return selfEvaluation;
    }



}
