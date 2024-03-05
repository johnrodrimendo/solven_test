package com.affirm.iovation.ws.transaction;

import com.affirm.iovation.ws.transaction.check.CheckTransactionDetails;
import com.affirm.iovation.ws.transaction.check.CheckTransactionDetailsResponse;
import com.affirm.iovation.ws.transaction.check.PortType;
import com.affirm.iovation.ws.transaction.check.Service;

/**
 * Created by jarmando on 05/01/17.
 */
public class CheckTest {

    public static void main (String ... args) {
        CheckTransactionDetails checkParams = new CheckTransactionDetails();
        Service checkService = new Service();
        PortType portyType = checkService.getCheckTransactionDetails();
        //portyType.checkTransactionDetails("susId", "susAccount", "susPasscode", "endUserId", "accountCode", "blackbox", "type", "txnProps");

        CheckTransactionDetailsResponse checkResponse;

        PortType portType;
    }
}
