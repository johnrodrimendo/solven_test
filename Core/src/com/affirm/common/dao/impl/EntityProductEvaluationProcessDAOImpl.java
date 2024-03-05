package com.affirm.common.dao.impl;

import com.affirm.common.dao.EntityProductEvaluationProcessDAO;
import com.affirm.common.dao.JsonResolverDAO;
import com.affirm.common.service.CatalogService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.util.List;

@Repository("entityProductEvaluationProcessDao")
public class EntityProductEvaluationProcessDAOImpl extends JsonResolverDAO implements EntityProductEvaluationProcessDAO {



    @Autowired
    private CatalogService catalogService;

    @Override
    public void updatePreliminaryEvaluationStatus(Character status, int loanApplicationId, int entityId, int productId) {
        update("update evaluation.tb_entity_product_evaluation set preliminary_evaluation_status = ? where loan_application_id = ? and entity_id  = ? and product_id = ?;",
                EVALUATION_DB,
                new SqlParameterValue(Types.CHAR, status),
                new SqlParameterValue(Types.INTEGER, loanApplicationId),
                new SqlParameterValue(Types.INTEGER, entityId),
                new SqlParameterValue(Types.INTEGER, productId));
    }

    @Override
    public void updateEvaluationStatus(Character status, int loanApplicationId, int entityId, int productId) {
        update("update evaluation.tb_entity_product_evaluation set evaluation_status = ? where loan_application_id = ? and entity_id  = ? and product_id = ?;",
                EVALUATION_DB,
                new SqlParameterValue(Types.CHAR, status),
                new SqlParameterValue(Types.INTEGER, loanApplicationId),
                new SqlParameterValue(Types.INTEGER, entityId),
                new SqlParameterValue(Types.INTEGER, productId));
    }

    @Override
    public void updatePreliminaryEvaluationRetries(int retries, int loanApplicationId, int entityId, int productId) {
        update("update evaluation.tb_entity_product_evaluation set preliminary_evaluation_retries = ? where loan_application_id = ? and entity_id  = ? and product_id = ?;",
                EVALUATION_DB,
                new SqlParameterValue(Types.INTEGER, retries),
                new SqlParameterValue(Types.INTEGER, loanApplicationId),
                new SqlParameterValue(Types.INTEGER, entityId),
                new SqlParameterValue(Types.INTEGER, productId));
    }

    @Override
    public void updateEvaluationRetries(int retries, int loanApplicationId, int entityId, int productId) {
        update("update evaluation.tb_entity_product_evaluation set evaluation_retries = ? where loan_application_id = ? and entity_id  = ? and product_id = ?;",
                EVALUATION_DB,
                new SqlParameterValue(Types.INTEGER, retries),
                new SqlParameterValue(Types.INTEGER, loanApplicationId),
                new SqlParameterValue(Types.INTEGER, entityId),
                new SqlParameterValue(Types.INTEGER, productId));
    }

    @Override
    public void updatePreliminaryEvaluationQueryBots(List<Integer> botIds, int loanApplicationId, int entityId, int productId) {
        update("update evaluation.tb_entity_product_evaluation set js_preliminary_evaluation_bots = ? where loan_application_id = ? and entity_id  = ? and product_id = ?;",
                EVALUATION_DB,
                new SqlParameterValue(Types.OTHER, new Gson().toJson(botIds)),
                new SqlParameterValue(Types.INTEGER, loanApplicationId),
                new SqlParameterValue(Types.INTEGER, entityId),
                new SqlParameterValue(Types.INTEGER, productId));
    }

    @Override
    public void updateEvaluationQueryBots(List<Integer> botIds, int loanApplicationId, int entityId, int productId) {
        update("update evaluation.tb_entity_product_evaluation set js_evaluation_bots = ? where loan_application_id = ? and entity_id  = ? and product_id = ?;",
                EVALUATION_DB,
                new SqlParameterValue(Types.OTHER, new Gson().toJson(botIds)),
                new SqlParameterValue(Types.INTEGER, loanApplicationId),
                new SqlParameterValue(Types.INTEGER, entityId),
                new SqlParameterValue(Types.INTEGER, productId));
    }

    @Override
    public void updateIsReady(boolean isReady, int loanApplicationId, int entityId, int productId) {
        update("update evaluation.tb_entity_product_evaluation set is_ready = ? where loan_application_id = ? and entity_id  = ? and product_id = ?;",
                EVALUATION_DB,
                new SqlParameterValue(Types.BOOLEAN, isReady),
                new SqlParameterValue(Types.INTEGER, loanApplicationId),
                new SqlParameterValue(Types.INTEGER, entityId),
                new SqlParameterValue(Types.INTEGER, productId));
    }

    @Override
    public void updateIsSelectable(boolean isSelectable, int loanApplicationId, int productId) {
        update("update evaluation.tb_entity_product_evaluation set is_selectable = ? where loan_application_id = ? and product_id = ?;",
                EVALUATION_DB,
                new SqlParameterValue(Types.BOOLEAN, isSelectable),
                new SqlParameterValue(Types.INTEGER, loanApplicationId),
                new SqlParameterValue(Types.INTEGER, productId));
    }

    @Override
    public void updateIsSelectable(boolean isSelectable, int loanApplicationId, int productId, int entityId) {
        update("update evaluation.tb_entity_product_evaluation set is_selectable = ? where loan_application_id = ? and product_id = ? and entity_id = ?;",
                EVALUATION_DB,
                new SqlParameterValue(Types.BOOLEAN, isSelectable),
                new SqlParameterValue(Types.INTEGER, loanApplicationId),
                new SqlParameterValue(Types.INTEGER, productId),
                new SqlParameterValue(Types.INTEGER, entityId));
    }


}
