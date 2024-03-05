package com.affirm.tests.dao

import com.affirm.common.dao.SelfEvaluationDAO
import com.affirm.common.model.transactional.SelfEvaluation
import com.affirm.tests.BaseConfig
import org.junit.Assert
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class SelfEvaluationDAOTest extends BaseConfig {

    @Autowired
    SelfEvaluationDAO selfEvaluationDAO

    static final int COUNTRY_ID = 51
    static final int SELF_EVALUATION_ID = 1000
    static final int QUESTINO_ID = 95
    static final Locale LOCALE = Locale.US
    static final String SEQUENCE = "1"
    static final int PERSON_ID = 2007
    static final int INSTALLMENTS = 2008
    static final int AMOUNT = 15000
    static final int FIXED_GROSS_INCOME = 5000
    static final int LOAN_REASON_ID = 2000
    static final int USAGE_ID = 2001
    static final int DOWN_PAYMENT = 250
    static final int FORM_ASSITANT = 3
    static final Integer[] BOTS = null

    @Test
    void registerSelfEvaluationFromSelfEvaluationDAO() {
        SelfEvaluation result = selfEvaluationDAO.registerSelfEvaluation(COUNTRY_ID)
        Assert.assertNotNull(result)
    }

    @Test
    void updateCurrentQuestionFromSelfEvaluationDAO() {
        selfEvaluationDAO.updateCurrentQuestion(SELF_EVALUATION_ID, QUESTINO_ID)
    }

    @Test
    void getSelfEvaluationFromSelfEvaluationDAO() {
        SelfEvaluation result = selfEvaluationDAO.getSelfEvaluation(SELF_EVALUATION_ID, LOCALE)
        Assert.assertNull(result)
    }

    @Test
    void updateQuestionSequenceFromSelfEvaluationDAO() {
        SelfEvaluation result = selfEvaluationDAO.updateQuestionSequence(SELF_EVALUATION_ID, SEQUENCE)
        Assert.assertNull(result)
    }

    @Test
    void updatePersonFromSelfEvaluationDAO() {
        selfEvaluationDAO.updatePerson(SELF_EVALUATION_ID, PERSON_ID)
    }

    @Test
    void updateInstallmentsFromSelfEvaluationDAO() {
        selfEvaluationDAO.updateInstallments(SELF_EVALUATION_ID, INSTALLMENTS)
    }

    @Test
    void updateAmountFromSelfEvaluationDAO() {
        selfEvaluationDAO.updateAmount(SELF_EVALUATION_ID, AMOUNT)
    }

    @Test
    void updateFixedGrossIncomeFromSelfEvaluationDAO() {
        selfEvaluationDAO.updateFixedGrossIncome(SELF_EVALUATION_ID, FIXED_GROSS_INCOME)
    }

    @Test
    void updateLoanReasonFromSelfEvaluationDAO() {
        selfEvaluationDAO.updateLoanReason(SELF_EVALUATION_ID, LOAN_REASON_ID)
    }

    @Test
    void updateUsageFromSelfEvaluationDAO() {
        selfEvaluationDAO.updateUsage(SELF_EVALUATION_ID, USAGE_ID)
    }

    @Test
    void updateDownPaymentFromSelfEvaluationDAO() {
        selfEvaluationDAO.updateDownPayment(SELF_EVALUATION_ID, DOWN_PAYMENT)
    }

    @Test
    void updateFormAssistantFromSelfEvaluationDAO() {
        selfEvaluationDAO.updateFormAssistant(SELF_EVALUATION_ID, FORM_ASSITANT)
    }

    @Test
    void runSelfEvaluationFromSelfEvaluationDAO() {
        String result = selfEvaluationDAO.runSelfEvaluation(SELF_EVALUATION_ID)
        Assert.assertNull(result)
    }

    @Test
    void updateBotsFromSelfEvaluationDAO() {
        selfEvaluationDAO.updateBots(SELF_EVALUATION_ID, BOTS)
    }

    @Test
    void getActiveSelfEvaluationByPersonFromSelfEvaluationDAO() {
        SelfEvaluation result = selfEvaluationDAO.getActiveSelfEvaluationByPerson(PERSON_ID, LOCALE)
        Assert.assertNull(result)
    }
}

