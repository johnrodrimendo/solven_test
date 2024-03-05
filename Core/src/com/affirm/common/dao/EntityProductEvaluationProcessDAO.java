package com.affirm.common.dao;

import java.util.List;

public interface EntityProductEvaluationProcessDAO {
    void updatePreliminaryEvaluationStatus(Character status, int loanApplicationId, int entityId, int productId);

    void updateEvaluationStatus(Character status, int loanApplicationId, int entityId, int productId);

    void updatePreliminaryEvaluationRetries(int retries, int loanApplicationId, int entityId, int productId);

    void updateEvaluationRetries(int retries, int loanApplicationId, int entityId, int productId);

    void updatePreliminaryEvaluationQueryBots(List<Integer> botIds, int loanApplicationId, int entityId, int productId);

    void updateEvaluationQueryBots(List<Integer> botIds, int loanApplicationId, int entityId, int productId);

    void updateIsReady(boolean isReady, int loanApplicationId, int entityId, int productId);

    void updateIsSelectable(boolean isSelectable, int loanApplicationId, int productId);

    void updateIsSelectable(boolean isSelectable, int loanApplicationId, int productId, int entityId);
}
