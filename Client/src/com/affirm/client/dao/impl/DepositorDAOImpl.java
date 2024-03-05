package com.affirm.client.dao.impl;

import com.affirm.client.dao.DepositorDAO;
import com.affirm.client.model.DepositorAmount;
import com.affirm.client.model.DepositorRegisteredPayment;
import com.affirm.client.model.RebateTransaction;
import com.affirm.common.dao.JsonResolverDAO;
import com.affirm.common.service.CatalogService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.stereotype.Repository;

import java.sql.Types;

/**
 * Created by jrodriguez on 26/09/16.
 */

@Repository
public class DepositorDAOImpl extends JsonResolverDAO implements DepositorDAO {

    @Autowired
    private CatalogService catalogService;

    @Override
    public DepositorAmount getAmountsByDepositorCode(String depositorCode, int wsClientId, String fullcargaReference) {
        JSONObject dbJson = queryForObjectTrx("select * from credit.ws_get_amounts(?, ?, ?)", JSONObject.class,
                new SqlParameterValue(Types.VARCHAR, depositorCode),
                new SqlParameterValue(Types.INTEGER, wsClientId),
                new SqlParameterValue(Types.VARCHAR, fullcargaReference));
        if (dbJson == null)
            return null;

        DepositorAmount amount = new DepositorAmount();
        amount.fillFromDb(dbJson);
        return amount;
    }

    @Override
    public DepositorRegisteredPayment registerPayment(int transactionId, double amount, int wsClientId) {
        JSONObject dbJson = queryForObjectTrx("select * from credit.ws_register_payment(?, ?, ?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, transactionId),
                new SqlParameterValue(Types.NUMERIC, amount),
                new SqlParameterValue(Types.INTEGER, wsClientId));
        if (dbJson == null)
            return null;

        DepositorRegisteredPayment payment = new DepositorRegisteredPayment();
        payment.fillFromDb(dbJson);
        return payment;
    }

    @Override
    public RebateTransaction rebateTransaction(int transactionId, int wsClientId) {
        JSONObject dbJson = queryForObjectTrx("select * from credit.ws_rebate_transaction(?, ?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, transactionId),
                new SqlParameterValue(Types.INTEGER, wsClientId));
        if (dbJson == null) {
            return null;
        }

        RebateTransaction transaction = new RebateTransaction();
        transaction.fillFromDb(dbJson);
        return transaction;
    }

}
