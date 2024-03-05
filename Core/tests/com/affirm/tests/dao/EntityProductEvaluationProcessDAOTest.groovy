package com.affirm.tests.dao

import com.affirm.common.dao.EntityProductEvaluationProcessDAO
import com.affirm.tests.BaseConfig
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class EntityProductEvaluationProcessDAOTest extends BaseConfig {

    @Autowired
    EntityProductEvaluationProcessDAO entityProductEvaluationProcessDAO

    static final Character STATUS = 'A'
    static final int ENTITY_ID = 88888
    static final Integer PRODUCT_ID = 22222
    static final int LOAN_APPLICATION_ID = 78987
    static final int RETRIES = 3
    static final List<Integer> BOT_IDS = new ArrayList<>()
    static final boolean IS_READY = true
    static final boolean IS_SELECTABLE = true

    @Test
    void updatePreliminaryEvaluationStatusFromEntityProductEvaluationProcessDAO() {
        entityProductEvaluationProcessDAO.updatePreliminaryEvaluationStatus(STATUS, LOAN_APPLICATION_ID, ENTITY_ID,
                PRODUCT_ID)
    }

    @Test
    void updateEvaluationStatusFromEntityProductEvaluationProcessDAO() {
        entityProductEvaluationProcessDAO.updateEvaluationStatus(STATUS, LOAN_APPLICATION_ID, ENTITY_ID, PRODUCT_ID)
    }

    @Test
    void updatePreliminaryEvaluationRetriesFromEntityProductEvaluationProcessDAO() {
        entityProductEvaluationProcessDAO.updatePreliminaryEvaluationRetries(RETRIES, LOAN_APPLICATION_ID, ENTITY_ID,
                PRODUCT_ID)
    }

    @Test
    void updateEvaluationRetriesFromEntityProductEvaluationProcessDAO() {
        entityProductEvaluationProcessDAO.updateEvaluationRetries(RETRIES, LOAN_APPLICATION_ID, ENTITY_ID, PRODUCT_ID)
    }

    @Test
    void updatePreliminaryEvaluationQueryBotsFromEntityProductEvaluationProcessDAO() {
        entityProductEvaluationProcessDAO.updatePreliminaryEvaluationQueryBots(BOT_IDS, LOAN_APPLICATION_ID, ENTITY_ID,
                PRODUCT_ID)
    }

    @Test
    void updateEvaluationQueryBotsFromEntityProductEvaluationProcessDAO() {
        entityProductEvaluationProcessDAO.updateEvaluationQueryBots(BOT_IDS, LOAN_APPLICATION_ID, ENTITY_ID, PRODUCT_ID)
    }

    @Test
    void updateIsReadyFromEntityProductEvaluationProcessDAO() {
        entityProductEvaluationProcessDAO.updateIsReady(IS_READY, LOAN_APPLICATION_ID, ENTITY_ID, PRODUCT_ID)
    }

    @Test
    void updateIsSelectableFromEntityProductEvaluationProcessDAO() {
        entityProductEvaluationProcessDAO.updateIsSelectable(IS_SELECTABLE, LOAN_APPLICATION_ID, PRODUCT_ID, ENTITY_ID)
    }

    @Test
    void updateIsSelectable2FromEntityProductEvaluationProcessDAO() {
        entityProductEvaluationProcessDAO.updateIsSelectable(IS_SELECTABLE, LOAN_APPLICATION_ID, PRODUCT_ID)
    }
}
