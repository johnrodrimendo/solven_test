package com.affirm.client.dao;

import com.affirm.client.model.DepositorAmount;
import com.affirm.client.model.DepositorRegisteredPayment;
import com.affirm.client.model.RebateTransaction;

/**
 * Created by jrodriguez on 26/09/16.
 */
public interface DepositorDAO {

    DepositorAmount getAmountsByDepositorCode(String depositorCode, int wsClientId, String fullcargaReference);

    DepositorRegisteredPayment registerPayment(int transactionId, double amount, int wsClientId);

    RebateTransaction rebateTransaction(int transactionId, int wsClientId);
}
