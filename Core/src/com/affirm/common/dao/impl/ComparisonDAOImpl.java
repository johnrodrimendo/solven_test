/**
 *
 */
package com.affirm.common.dao.impl;

import com.affirm.common.dao.ComparisonDAO;
import com.affirm.common.dao.JsonResolverDAO;
import com.affirm.common.model.transactional.Comparison;
import com.affirm.common.model.transactional.ComparisonResult;
import com.affirm.common.service.CatalogService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author jrodriguez
 */

@Repository
public class ComparisonDAOImpl extends JsonResolverDAO implements ComparisonDAO {


    @Autowired
    private CatalogService catalogService;

    @Override
    public Comparison registerComparison(int formAssistant) {
        Integer id = queryForObjectTrx("select * from comparator.register_comparison(?)", Integer.class,
                new SqlParameterValue(Types.INTEGER, formAssistant));
        Comparison comparison = new Comparison();
        comparison.setId(id);
        return comparison;
    }

    @Override
    public void updateCurrentQuestion(int comparisonId, int questionId) {
        updateTrx("UPDATE comparator.tb_comparison set current_procces_question_id = ? where comparison_id = ?",
                new SqlParameterValue(Types.INTEGER, questionId),
                new SqlParameterValue(Types.INTEGER, comparisonId));
    }

    @Override
    public void updateQuestionSequence(int comparisonId, String sequence) {
        updateTrx("UPDATE comparator.tb_comparison set proccess_question_id_sequence = ? where comparison_id = ?",
                new SqlParameterValue(Types.OTHER, sequence),
                new SqlParameterValue(Types.INTEGER, comparisonId));
    }

    @Override
    public Comparison getComparison(int comparisonId, Locale locale) {
        JSONObject dbJson = queryForObjectTrx("select * from comparator.get_comparison(?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, comparisonId));
        if (dbJson == null)
            return null;

        Comparison comparison = new Comparison();
        comparison.fillFromDb(dbJson, catalogService, locale);
        return comparison;
    }

    @Override
    public void updateActivityType(int comparisonId, Integer activityType) {
        updateTrx("UPDATE comparator.tb_comparison set activity_type = ? where comparison_id = ?",
                new SqlParameterValue(Types.INTEGER, activityType),
                new SqlParameterValue(Types.INTEGER, comparisonId));
    }

    @Override
    public void updateComparisonReason(int comparisonId, Integer comparisonReasonId) {
        updateTrx("UPDATE comparator.tb_comparison set comparison_reason_id = ? where comparison_id = ?",
                new SqlParameterValue(Types.INTEGER, comparisonReasonId),
                new SqlParameterValue(Types.INTEGER, comparisonId));
    }

    @Override
    public void updateFixedGrosIncome(int comparisonId, Double fixedGrossIncome) {
        updateTrx("UPDATE comparator.tb_comparison set fixed_gross_income = ? where comparison_id = ?",
                new SqlParameterValue(Types.NUMERIC, fixedGrossIncome),
                new SqlParameterValue(Types.INTEGER, comparisonId));
    }

    @Override
    public void updateInstallments(int comparisonId, int installments) {
        updateTrx("UPDATE comparator.tb_comparison set installments = ? where comparison_id = ?",
                new SqlParameterValue(Types.INTEGER, installments),
                new SqlParameterValue(Types.INTEGER, comparisonId));
    }

    @Override
    public void updateAmount(int comparisonId, int amount) {
        updateTrx("UPDATE comparator.tb_comparison set amount = ? where comparison_id = ?",
                new SqlParameterValue(Types.INTEGER, amount),
                new SqlParameterValue(Types.INTEGER, comparisonId));
    }

    @Override
    public void updateEmail(int comparisonId, String email) {
        updateTrx("UPDATE comparator.tb_comparison set email = ? where comparison_id = ?",
                new SqlParameterValue(Types.VARCHAR, email),
                new SqlParameterValue(Types.INTEGER, comparisonId));
    }

    @Override
    public void updatePassword(int comparisonId, String password) {
        updateTrx("UPDATE comparator.tb_comparison set password = ? where comparison_id = ?",
                new SqlParameterValue(Types.VARCHAR, password),
                new SqlParameterValue(Types.INTEGER, comparisonId));
    }

    @Override
    public void updateNetworkToken(int comparisonId, Integer networkToken) {
        updateTrx("UPDATE comparator.tb_comparison set network_token_id = ? where comparison_id = ?",
                new SqlParameterValue(Types.INTEGER, networkToken),
                new SqlParameterValue(Types.INTEGER, comparisonId));
    }

    @Override
    public void updateSelfEvaluationId(int comparisonId, int selfEvaluationId) {
        updateTrx("UPDATE comparator.tb_comparison set self_evaluation_id = ? where comparison_id = ?",
                new SqlParameterValue(Types.INTEGER, selfEvaluationId),
                new SqlParameterValue(Types.INTEGER, comparisonId));
    }

    @Override
    public List<ComparisonResult> executeComparison(int comparisonId, Locale locale) {
        JSONArray dbArray = queryForObjectTrx("SELECT * FROM comparator.execute_comparison(?)", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, comparisonId));
        if (dbArray == null)
            return null;

        List<ComparisonResult> results = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            ComparisonResult result = new ComparisonResult();
            result.fillFromDb(dbArray.getJSONObject(i), catalogService, locale);
            results.add(result);
        }
        return results;
    }



}
