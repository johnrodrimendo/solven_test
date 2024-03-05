package com.affirm.security.service;

import com.affirm.fdlm.creditoconsumo.response.Sucursal;

/**
 * Created by dev5 on 23/02/18.
 */
public interface ExternalWebServiceService {

    String getPersonScore(Integer loanApplicationId, Integer entityWs) throws Exception;

    Sucursal getSucursalFdlm(Integer loanApplicationId);

}
