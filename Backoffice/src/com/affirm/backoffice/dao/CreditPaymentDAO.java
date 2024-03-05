/**
 *
 */
package com.affirm.backoffice.dao;

import com.affirm.backoffice.model.CreditPayment;
import com.affirm.common.model.transactional.SalaryAdvancePayment;
import org.json.JSONArray;

import java.util.List;
import java.util.Locale;

/**
 * @author jrodriguez
 */
public interface CreditPaymentDAO {

    List<CreditPayment> getPendingCreditPayment(String country) throws Exception;

    List<CreditPayment> registerCreditPayments(String jsonPayments, int bankId) throws Exception;

    void registerCreditPaymentConfirmation(String ids, int sysUserId) throws Exception;

    boolean updateCreditPayment(int creditPaymentId, String creditCode, int sysUserId) throws Exception;

    List<SalaryAdvancePayment> getPendingSalaryAdvancePayments(Locale locale) throws Exception;

    Integer registerMultiCreditPayment(int multiCreditPaymentId, int creditId, String paymentJson) throws Exception;

    void registerMarketPayment(JSONArray payments, int sysUserId) throws Exception;
}
