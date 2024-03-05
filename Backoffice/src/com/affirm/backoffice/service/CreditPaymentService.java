package com.affirm.backoffice.service;


import com.affirm.common.model.transactional.MultiCreditPayment;
import org.apache.poi.ss.usermodel.Sheet;
import org.json.JSONArray;

import java.util.Locale;

/**
 * Created by jrodriguez on 23/08/16.
 */
public interface CreditPaymentService {

    JSONArray parseBankPaymentFile(String data, int bankId) throws Exception;

    void processBankPayments(JSONArray payments, int bankId) throws Exception;

    void registerMultiPayment(MultiCreditPayment multiCreditPayment, int creditId, double paymentAmount) throws Exception;

    void processMultipaymentAutomatically(Locale locale) throws Exception;

    JSONArray parseOpenMarketFile(Sheet datatypeSheet) throws Exception;

    void processMarketPayment(JSONArray payments, int sysUserId) throws Exception;

    JSONArray parseCloseMarketFile(Sheet datatypeSheet) throws Exception;
}
