package com.affirm.tests.dao

import com.affirm.common.dao.PreliminaryEvaluationDAO
import com.affirm.common.model.transactional.LoanApplicationPreliminaryEvaluation
import com.affirm.system.configuration.Configuration
import com.affirm.tests.BaseConfig
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import org.springframework.beans.factory.annotation.Autowired

class PreliminaryEvaluationDAOTest extends BaseConfig {

    @Autowired
    private PreliminaryEvaluationDAO preliminaryEvaluationDAO

    private static final Integer PRELIMINARY_EVALUATION_ID = 20911
    private static final Integer LOAN_APPLICATION_ID = 9488

//    @Test
//    void shouldUpdateStatus() {
//        Executable executable = preliminaryEvaluationDAO.updateStatus(PRELIMINARY_EVALUATION_ID, '')
//
//        Assertions.assertDoesNotThrow(executable)
//    }

    @Test
    void shouldUpdateIsApproved() {
        boolean isApproved = true
        Executable approvedExecutable = {
            preliminaryEvaluationDAO.updateIsApproved(PRELIMINARY_EVALUATION_ID, isApproved)
        }

        boolean notApproved = true
        Executable notApprovedExecutable = {
            preliminaryEvaluationDAO.updateIsApproved(PRELIMINARY_EVALUATION_ID, notApproved)
        }

        Assertions.assertDoesNotThrow(approvedExecutable)
        Assertions.assertDoesNotThrow(notApprovedExecutable)
    }

    @Test
    void shouldUpdateRunDefaultEvaluation() {
        boolean runDefaultEvaluation = true
        Executable runExecutable = {
            preliminaryEvaluationDAO.updateRunDefaultEvaluation(PRELIMINARY_EVALUATION_ID, runDefaultEvaluation)
        }

        boolean doNotrunDefaultEvaluation = false
        Executable noRunExecutable = {
            preliminaryEvaluationDAO.updateRunDefaultEvaluation(PRELIMINARY_EVALUATION_ID, doNotrunDefaultEvaluation)
        }

        Assertions.assertDoesNotThrow(runExecutable)
        Assertions.assertDoesNotThrow(noRunExecutable)
    }

    @Test
    void shouldGetPreliminaryEvaluationsWithHardFilters() {
        List<LoanApplicationPreliminaryEvaluation> preliminaryEvaluationList = preliminaryEvaluationDAO.getPreliminaryEvaluationsWithHardFilters(LOAN_APPLICATION_ID, Configuration.defaultLocale)

        Assertions.assertNotNull(preliminaryEvaluationList)
    }

}
