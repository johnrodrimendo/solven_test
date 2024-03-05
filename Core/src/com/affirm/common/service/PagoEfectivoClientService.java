package com.affirm.common.service;

import com.affirm.common.model.pagoefectivo.PagoEfectivoCreateAuthorizationResponse;
import com.affirm.common.model.pagoefectivo.PagoEfectivoCreateCIPResponse;
import org.json.JSONObject;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

public interface PagoEfectivoClientService {

    PagoEfectivoCreateAuthorizationResponse generateAuthToken(int entityProductParamId) throws IOException;

    PagoEfectivoCreateCIPResponse createCIPResponse(int loanApplicationId, Locale locale) throws Exception;

    boolean validateWebhookData(String body,String signature) throws Exception;

    void CIPPayed(String body) throws Exception;

    void verifyStatusCIP(String CIP, Integer loanCollectionPaymentId) throws Exception;


}
