package com.affirm.backoffice.dao.impl;

import com.affirm.backoffice.dao.CreditPaymentDAO;
import com.affirm.backoffice.model.CreditPayment;
import com.affirm.common.dao.JsonResolverDAO;
import com.affirm.common.model.transactional.SalaryAdvancePayment;
import com.affirm.common.service.CatalogService;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by jrodriguez on 08/06/16.
 */

@Repository()
public class CreditPaymentDAOImpl extends JsonResolverDAO implements CreditPaymentDAO {

    @Autowired
    private CatalogService catalogService;

    @Override
    public List<CreditPayment> getPendingCreditPayment(String country) throws Exception {
        JSONArray dbArray = queryForObject("select * from credit.bo_get_pending_payments(?::JSON)", JSONArray.class, READABLE_DB,
                new SqlParameterValue(Types.OTHER, country));
        if (dbArray == null) {
            return null;
        }

        List<CreditPayment> payments = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            CreditPayment payment = new CreditPayment();
            payment.fillFromDb(dbArray.getJSONObject(i), catalogService);
            payments.add(payment);
        }
        return payments;
    }

    @Override
    public List<CreditPayment> registerCreditPayments(String jsonPayments, int bankId) throws Exception {
        JSONArray dbArray = queryForObjectTrx("select * from credit.bo_register_payments_json(?, ?)", JSONArray.class,
                new SqlParameterValue(Types.OTHER, jsonPayments),
                new SqlParameterValue(Types.INTEGER, bankId));
        if (dbArray == null) {
            return null;
        }

        List<CreditPayment> payments = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            CreditPayment payment = new CreditPayment();
            payment.fillFromDb(dbArray.getJSONObject(i), catalogService);
            payments.add(payment);
        }
        return payments;
    }

    @Override
    public void registerCreditPaymentConfirmation(String ids, int sysUserId) throws Exception {
        queryForObjectTrx("select * from credit.bo_confirm_payment(?, ?)", String.class,
                new SqlParameterValue(Types.OTHER, ids),
                new SqlParameterValue(Types.INTEGER, sysUserId));
    }

    @Override
    public boolean updateCreditPayment(int creditPaymentId, String creditCode, int sysUserId) throws Exception {

        boolean result = queryForObjectTrx("select * from credit.upd_payment_credit(?, ?, ?)", Boolean.class,
                new SqlParameterValue(Types.INTEGER, creditPaymentId),
                new SqlParameterValue(Types.VARCHAR, creditCode),
                new SqlParameterValue(Types.INTEGER, sysUserId));

        return result;
    }

    @Override
    public List<SalaryAdvancePayment> getPendingSalaryAdvancePayments(Locale locale) throws Exception {
        JSONArray dbArray = queryForObjectTrx("select * from credit.get_pending_credit_payment_multi()", JSONArray.class);
        if (dbArray == null)
            return null;

        List<SalaryAdvancePayment> payments = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            SalaryAdvancePayment payment = new SalaryAdvancePayment();
            payment.fillFromDb(dbArray.getJSONObject(i), catalogService, locale);
            payments.add(payment);
        }
        return payments;
    }

    @Override
    public Integer registerMultiCreditPayment(int multiCreditPaymentId, int creditId, String paymentJson) throws Exception {
        return queryForObjectTrx("select * from credit.bo_register_payment(?, ?, ?)", Integer.class,
                new SqlParameterValue(Types.INTEGER, multiCreditPaymentId),
                new SqlParameterValue(Types.INTEGER, creditId),
                new SqlParameterValue(Types.OTHER, paymentJson));
    }

    @Override
    public void registerMarketPayment(JSONArray payments, int sysUserId) throws Exception{
        queryForObjectTrx("select * from credit.bo_register_payment_batch(?, ?::JSON)", String.class,
                new SqlParameterValue(Types.INTEGER, sysUserId),
                new SqlParameterValue(Types.OTHER, payments));
    }


}
