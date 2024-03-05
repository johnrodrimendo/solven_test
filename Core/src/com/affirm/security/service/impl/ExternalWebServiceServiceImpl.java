package com.affirm.security.service.impl;

import com.affirm.common.dao.WebServiceDAO;
import com.affirm.common.model.catalog.EntityWebService;
import com.affirm.common.util.JsonUtil;
import com.affirm.fdlm.creditoconsumo.response.Sucursal;
import com.affirm.security.dao.SecurityDAO;
import com.affirm.security.model.EntityWsResult;
import com.affirm.security.service.ExternalWebServiceService;
import com.google.gson.Gson;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by dev5 on 23/02/18.
 */
@Service
public class ExternalWebServiceServiceImpl implements ExternalWebServiceService{

    @Autowired
    SecurityDAO securityDAO;
    @Autowired
    private WebServiceDAO webServiceDAO;

    @Override
    public String getPersonScore(Integer loanApplicationId, Integer entityWs) throws Exception{
        EntityWsResult externalWsResult = securityDAO.getEntityResultWS(loanApplicationId, entityWs);
        return externalWsResult != null && externalWsResult.getResult() != null ? externalWsResult.getResult().toString() : null;
    }

    @Override
    public Sucursal getSucursalFdlm(Integer loanApplicationId) {
        JSONObject responseJsonObject = webServiceDAO.getExternalServiceResponse(loanApplicationId, EntityWebService.FDLM_TOPAZ_OBTENER_SUCURSAL);
        JSONObject resultJsonObject = JsonUtil.getJsonObjectFromJson(responseJsonObject, "result", new JSONObject());
        JSONObject sucursalJsonObject = JsonUtil.getJsonObjectFromJson(resultJsonObject, "DATOS", new JSONObject());
        Sucursal sucursal = new Gson().fromJson(sucursalJsonObject.toString(), Sucursal.class);

        return sucursal;
    }

}
