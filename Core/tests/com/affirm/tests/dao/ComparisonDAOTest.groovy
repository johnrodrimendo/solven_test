package com.affirm.tests.dao

import com.affirm.common.dao.ComparisonDAO
import com.affirm.common.model.catalog.ComparisonReason
import com.affirm.common.model.catalog.ProcessQuestion
import com.affirm.common.model.transactional.Comparison
import com.affirm.common.model.transactional.ComparisonResult
import com.affirm.system.configuration.Configuration
import com.affirm.tests.BaseConfig
import org.json.JSONObject
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import org.springframework.beans.factory.annotation.Autowired

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow
import static org.junit.jupiter.api.Assertions.assertNotNull

class ComparisonDAOTest extends BaseConfig {

    @Autowired
    private ComparisonDAO comparisonDAO

    private static final Integer COMPARISON_ID = 150
    private static final Integer SELF_EVALUATION_ID = 169

    @Test
    void shouldRegisterComparison() {
        Comparison comparison = comparisonDAO.registerComparison(1)

        assertNotNull(comparison)
    }

    @Test
    void shouldUpdateCurrentQuestion() {
        Executable executable = {
            comparisonDAO.updateCurrentQuestion(COMPARISON_ID, ProcessQuestion.Question.OFFER.id)
        }

        assertDoesNotThrow(executable)
    }

    @Test
    void shouldUpdateQuestionSequence() {
        Executable executable = { comparisonDAO.updateQuestionSequence(COMPARISON_ID, new JSONObject().toString()) }

        assertNotNull(executable)
    }

    @Test
    void shouldGetComparison() {
        Comparison comparison = comparisonDAO.getComparison(COMPARISON_ID, Configuration.defaultLocale)

        assertNotNull(comparison)
    }

//    @Test
//    void shouldUpdateActivityType() {
//        Executable executable = { comparisonDAO.updateActivityType(COMPARISON_ID, ActivityType.DEPENDENT) }
//
//        assertNotNull(executable)
//    }

    @Test
    void shouldUpdateComparisonReason() {
        Executable executable = {
            comparisonDAO.updateComparisonReason(COMPARISON_ID, ComparisonReason.REASON_GROUP_LOAN)
        }

        assertDoesNotThrow(executable)
    }

    @Test
    void shouldUpdateFixedGrosIncome() {
        Executable executable = { comparisonDAO.updateFixedGrosIncome(COMPARISON_ID, 5000.0) }

        assertDoesNotThrow(executable)
    }

    @Test
    void shouldUpdateInstallments() {
        Executable executable = { comparisonDAO.updateInstallments(COMPARISON_ID, 24) }

        assertDoesNotThrow(executable)
    }

    @Test
    void shouldUpdateAmount() {
        Executable executable = { comparisonDAO.updateAmount(COMPARISON_ID, 5000) }

        assertDoesNotThrow(executable)
    }

//    @Test
//    void shouldUpdateEmail() {
//        Executable executable = { comparisonDAO.updateEmail(COMPARISON_ID, 'dalvera@hotmail.es') }
//
//        assertDoesNotThrow(executable)
//    }

//    @Test
//    void shouldUpdatePassword() {
//        Executable executable = { comparisonDAO.updatePassword(COMPARISON_ID, 'mysecretpassword') }
//
//        assertDoesNotThrow(executable)
//    }

//    @Test
//    void shouldUpdateNetworkToken() {
//        Executable executable = { comparisonDAO.updateNetworkToken(COMPARISON_ID, Integer networkToken) }
//
//        assertDoesNotThrow(executable)
//    }

    @Test
    void shouldUpdateSelfEvaluationId() {
        Executable executable = { comparisonDAO.updateSelfEvaluationId(COMPARISON_ID, SELF_EVALUATION_ID) }

        assertDoesNotThrow(executable)
    }

    @Test
    void shouldExecuteComparison() {
        List<ComparisonResult> comparisonResultList = comparisonDAO.executeComparison(COMPARISON_ID, Configuration.defaultLocale)

        assertNotNull(comparisonResultList)
    }
}
