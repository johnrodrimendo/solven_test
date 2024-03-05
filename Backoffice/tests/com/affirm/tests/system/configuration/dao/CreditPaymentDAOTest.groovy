package com.affirm.tests.system.configuration.dao

import com.affirm.backoffice.dao.CreditPaymentDAO
import com.affirm.backoffice.model.CreditPayment
import com.affirm.common.model.transactional.SalaryAdvancePayment
import com.affirm.tests.BaseBoConfig
import org.json.JSONArray
import org.junit.Assert
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class CreditPaymentDAOTest extends BaseBoConfig {

    @Autowired
    CreditPaymentDAO creditPaymentDAO

    static final String COUNTRY_JSON_ARRAY = '["12345"]'
    static final Integer BANK_ID = 12345
    static final String JSON_PAYMENTS = '["55555"]'
    static final String JSON_IDS = '["33333"]'
    static final Integer SYS_USER_ID = 77777
    static final Integer CREDIT_PAYMENT_ID = 99999
    static final String CREDIT_CODE = "123"
    static final Locale LOCALE = Locale.US
    static final Integer MULTI_CREDIT_PAYMENT_ID = 55555
    static final Integer CREDIT_ID = 11111
    static final JSONArray PAYMENTS = null

    @Test
    void getPendigDisbursementFromCreditPaymentDAO() {
        List<CreditPayment> list = creditPaymentDAO.getPendingCreditPayment(COUNTRY_JSON_ARRAY)
        Assert.assertNull(list)
    }

    @Test
    void registerCreditPaymentsFromCreditPaymentDAO() {
        List<CreditPayment> list = creditPaymentDAO.registerCreditPayments(JSON_PAYMENTS, BANK_ID)
        Assert.assertNull(list)
    }

    @Test
    void registerCreditPaymentConfirmationFromCreditPaymentDAO() {
        creditPaymentDAO.registerCreditPaymentConfirmation(JSON_IDS, SYS_USER_ID)
    }

    @Test
    void updateCreditPaymentFromCreditPaymentDAO() {
        boolean result = creditPaymentDAO.updateCreditPayment(CREDIT_PAYMENT_ID, CREDIT_CODE, SYS_USER_ID)
        Assert.assertFalse(result)
    }

    @Test
    void getPendingSalaryAdvancePaymentstFromCreditPaymentDAO() {
        List<SalaryAdvancePayment> list = creditPaymentDAO.getPendingSalaryAdvancePayments(LOCALE)
        Assert.assertNotNull(list)
    }

    @Test
    void registerMultiCreditPaymentFromCreditPaymentDAO() {
        Integer result = creditPaymentDAO.registerMultiCreditPayment(MULTI_CREDIT_PAYMENT_ID, CREDIT_ID, JSON_PAYMENTS)
        Assert.assertNotNull(result)
    }

    @Test
    void registerMarketPaymentFromCreditPaymentDAO() {
        creditPaymentDAO.registerMarketPayment(PAYMENTS, SYS_USER_ID)
    }

}
