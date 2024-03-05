package com.affirm.acquisition.controller.rest;

import com.affirm.client.dao.DepositorDAO;
import com.affirm.client.model.DepositorAmount;
import com.affirm.client.model.DepositorRegisteredPayment;
import com.affirm.client.model.RebateTransaction;
import com.affirm.client.model.annotation.ErrorRestControllerAnnotation;
import com.affirm.client.util.ApiWsResponse;
import com.affirm.common.model.transactional.WsClient;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

/**
 * @author jrodriguez
 */
@RestController
@RequestMapping("/api")
public class DepositorController {

    private static Logger logger = Logger.getLogger(DepositorController.class);

    @Autowired
    private DepositorDAO depositorDao;


    @RequestMapping(value = "/depositor/amount", method = RequestMethod.GET)
    @ErrorRestControllerAnnotation
    public Object showIndex(
            ModelMap model, Locale locale, HttpServletRequest request,
            @RequestParam("fullcargaReference") String fullcargaReference,
            @RequestParam("depositorCode") String depositorCode) throws Exception {
        // TODO Validate fields

        WsClient wsClient = (WsClient) request.getAttribute("wsClient");
        DepositorAmount amount = depositorDao.getAmountsByDepositorCode(depositorCode, wsClient.getId(), fullcargaReference);
        if (amount == null) {
            return ApiWsResponse.error("depositor_code_doesnt_exists", "El código de depositor no existe.");
        } else if (amount.getErrorMessage() != null && amount.getErrorMessage().equals("fullcarga.payment.notAllowed")) {
            return ApiWsResponse.error("depositor_code_not_enable", "El código de depositor no está habilitado por este medio.");
        } else if (amount.getErrorMessage() != null) {
            return ApiWsResponse.error(null, null);
        } else if (amount.getTotal() <= 0) {
            return ApiWsResponse.error("depositor_code_without_debt", "El código de depositor no tiene deudas.");
        } else {
            amount.setFullcargaReference(fullcargaReference);
            return ApiWsResponse.ok(amount);
        }
    }

    @RequestMapping(value = "/depositor/payment", method = RequestMethod.POST)
    @ErrorRestControllerAnnotation
    public Object registerPayment(
            ModelMap model, Locale locale, HttpServletRequest request,
            @RequestParam("transactionId") Integer transactionId,
            @RequestParam("amount") Double amount) throws Exception {
        // TODO Validate fields
        // TODO The transactionId should only be used once

        WsClient wsClient = (WsClient) request.getAttribute("wsClient");
        if (amount < 25) {
            return ApiWsResponse.error("minimum_amount", "Monto es menor al minimo");
        }

        DepositorRegisteredPayment registeredPayment = depositorDao.registerPayment(transactionId, amount, wsClient.getId());
        if (registeredPayment == null) {
            return ApiWsResponse.error("transaction_doesnt_exists", "La transacción no existe.");
        } else if (registeredPayment.getErrorMessage() != null && registeredPayment.getErrorMessage().equals("fullcarga.payment.clientTransactionMismatch")) {
            return ApiWsResponse.error("transaction_doesnt_exists", "La transacción no existe.");
        } else if (registeredPayment.getErrorMessage() != null) {
            return ApiWsResponse.error(null, null);
        }
        return ApiWsResponse.ok(registeredPayment);
    }

    @RequestMapping(value = "/depositor/transaction/rebate", method = RequestMethod.POST)
    @ErrorRestControllerAnnotation
    public Object rebateTransaction(
            ModelMap model, Locale locale, HttpServletRequest request,
            @RequestParam("transactionId") Integer transactionId) throws Exception {
        // TODO Validate fields
        // TODO The transactionId should only be used once

        WsClient wsClient = (WsClient) request.getAttribute("wsClient");
        RebateTransaction transaction = depositorDao.rebateTransaction(transactionId, wsClient.getId());

        if (transaction == null) {
            return ApiWsResponse.ok("fsdfsdfsdfsd");
        } else if (transaction.getErrorMessage() != null && transaction.getErrorMessage().equals("fullcarga.payment.clientTransactionMismatch")) {
            return ApiWsResponse.error("transaction_doesnt_exists", "La transacción no existe.");
        } else if (transaction.getErrorMessage() != null && transaction.getErrorMessage().equals("fullcarga.payment.nonExistentTransaction")) {
            return ApiWsResponse.error("transaction_doesnt_exists", "La transacción no existe.");
        } else if (transaction.getErrorMessage() != null && transaction.getErrorMessage().equals("fullcarga.payment.transactionAccredited")) {
            return ApiWsResponse.error("transaction_accredited", "La transacción ya ha sido acreditada.");
        } else if (transaction.getErrorMessage() != null && transaction.getErrorMessage().equals("fullcarga.payment.transactionRebated")) {
            return ApiWsResponse.error("transaction_rebated", "La transacción ya ha sido rechazada.");
        } else if (transaction.getErrorMessage() != null) {
            return ApiWsResponse.error(null, null);
        }
        return ApiWsResponse.ok(null);
    }
}