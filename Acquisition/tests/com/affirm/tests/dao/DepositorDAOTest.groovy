package com.affirm.tests.dao

import com.affirm.client.dao.DepositorDAO
import com.affirm.client.model.DepositorAmount
import com.affirm.client.model.DepositorRegisteredPayment
import com.affirm.client.model.RebateTransaction
import com.affirm.tests.BaseAcquisitionConfig
import org.junit.Assert
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class DepositorDAOTest extends BaseAcquisitionConfig {

    @Autowired
    DepositorDAO depositorDAO

    static final String DEPOSITOR_CODE = "12345"
    static final int WS_CLIENT_ID = 11111
    static final String FULL_CARGA_REFERENCE = ""
    static final int TRANSACTION_ID = 987654321
    static final double AMOUNT = 5000

    @Test
    void getAmountsByDepositorCodeFromDepositorDAO() {
        DepositorAmount result = depositorDAO.getAmountsByDepositorCode(DEPOSITOR_CODE, WS_CLIENT_ID, FULL_CARGA_REFERENCE)
        Assert.assertNull(result)
    }

    @Test
    void registerPaymentFromDepositorDAO() {
        DepositorRegisteredPayment result = depositorDAO.registerPayment(TRANSACTION_ID, AMOUNT, WS_CLIENT_ID)
        Assert.assertNull(result)
    }

    @Test
    void rebateTransactionFromDepositorDAO() {
        RebateTransaction result = depositorDAO.rebateTransaction(TRANSACTION_ID, WS_CLIENT_ID)
        Assert.assertNotNull(result)
    }
}
