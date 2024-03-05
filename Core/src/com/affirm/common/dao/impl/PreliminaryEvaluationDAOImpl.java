package com.affirm.common.dao.impl;

import com.affirm.common.dao.JsonResolverDAO;
import com.affirm.common.dao.PreliminaryEvaluationDAO;
import com.affirm.common.model.transactional.LoanApplicationPreliminaryEvaluation;
import com.affirm.common.service.CatalogService;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Repository
public class PreliminaryEvaluationDAOImpl extends JsonResolverDAO implements PreliminaryEvaluationDAO {

    @Autowired
    private CatalogService catalogService;

    @Override
    public void updateStatus(Integer prelimimaryEvaluationId, Character status) {
        update("UPDATE evaluation.tb_preliminary_evaluation SET status = ? WHERE preliminary_evaluation_id = ?;",
                EVALUATION_DB,
                new SqlParameterValue(Types.CHAR, status),
                new SqlParameterValue(Types.INTEGER, prelimimaryEvaluationId));
    }

    @Override
    public void updateIsApproved(Integer prelimimaryEvaluationId, Boolean isApproved) {
        update("UPDATE evaluation.tb_preliminary_evaluation SET is_approved = ? WHERE preliminary_evaluation_id = ?;",
                EVALUATION_DB,
                new SqlParameterValue(Types.BOOLEAN, isApproved),
                new SqlParameterValue(Types.INTEGER, prelimimaryEvaluationId));
    }

    @Override
    public void updateRunDefaultEvaluation(Integer prelimimaryEvaluationId, Boolean runDefaultEvaluation) {
        update("UPDATE evaluation.tb_preliminary_evaluation SET run_default_evaluation = ? WHERE preliminary_evaluation_id = ?;",
                EVALUATION_DB,
                new SqlParameterValue(Types.BOOLEAN, runDefaultEvaluation),
                new SqlParameterValue(Types.INTEGER, prelimimaryEvaluationId));
    }

    @Override
    public List<LoanApplicationPreliminaryEvaluation> getPreliminaryEvaluationsWithHardFilters(int loanApplicationId, Locale locale) throws Exception {
        JSONArray dbArray = queryForObjectEvaluation("select * from evaluation.get_preliminary_evaluations_with_filters(?)", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
        if (dbArray == null) {
            return new ArrayList<>();
        }

        List<LoanApplicationPreliminaryEvaluation> evaluations = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            LoanApplicationPreliminaryEvaluation evaluation = new LoanApplicationPreliminaryEvaluation();
            evaluation.fillFromDb(dbArray.getJSONObject(i), catalogService, locale);
            evaluations.add(evaluation);
        }
        return evaluations;
    }

}
