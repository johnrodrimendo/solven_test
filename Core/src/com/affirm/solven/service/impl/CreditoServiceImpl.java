package com.affirm.solven.service.impl;

import com.affirm.common.dao.CreditDAO;
import com.affirm.common.model.catalog.CreditStatus;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by dev5 on 24/04/17.
 */
public class CreditoServiceImpl {

    @Autowired
    CreditDAO creditDAO;

    public void confirmarOperacion(int operationId, int solvenCreditId) throws Exception{
        if(operationId == 1){
            creditDAO.updateCreditStatus(solvenCreditId, CreditStatus.ORIGINATED, 5);
        } else if (operationId == 2){
            creditDAO.updateCreditStatus(solvenCreditId,CreditStatus.ORIGINATED_DISBURSED, 5);
            //agreementService.confirmDisbursement(solvenCreditId, request, response, Configuration.getDefaultLocale());
        }
    }

}

