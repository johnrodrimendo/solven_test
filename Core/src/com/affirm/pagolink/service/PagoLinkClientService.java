package com.affirm.pagolink.service;

import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.pagolink.model.PagoLinkCreateSessionResponse;
import com.affirm.pagolink.model.PagoLinkEcommerceAuthorizationResponse;
import com.affirm.pagolink.model.PagoLinkOrderDetail;
import com.affirm.pagolink.model.PagoLinkResponse;

import java.util.Locale;

public interface PagoLinkClientService {

    String getMerchantId(int entityProductParameterId, Integer campaignId);

    String generateToken(Integer loanApplicationId) throws Exception;

    PagoLinkResponse createPaymentLink(int loanApplicationId, Locale locale) throws Exception;

    PagoLinkOrderDetail getOrderDetail(String orderId, Locale locale) throws Exception;


    PagoLinkEcommerceAuthorizationResponse getAuthorizationResponse(int loanApplicationId, String transactionToken, Locale locale) throws Exception;

}
