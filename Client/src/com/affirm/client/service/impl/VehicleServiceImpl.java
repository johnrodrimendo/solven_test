package com.affirm.client.service.impl;

import com.affirm.client.service.VehicleService;
import com.affirm.common.dao.CreditDAO;
import com.affirm.common.model.catalog.ProductCategory;
import com.affirm.common.model.catalog.VehicleDealership;
import com.affirm.common.model.transactional.Credit;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.CryptoUtil;
import com.affirm.system.configuration.Configuration;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service("vehicleService")
public class VehicleServiceImpl implements VehicleService {

    @Autowired
    CatalogService catalogService;
    @Autowired
    CreditDAO creditDAO;

    private static Logger logger = Logger.getLogger(VehicleServiceImpl.class);

    @Override
    public String createVehicleEvaluationUrl(int vehicleId, HttpServletRequest request){
        JSONObject jsonParams = new JSONObject();
        jsonParams.put("vehicleId", vehicleId);
        String externalParams = CryptoUtil.encrypt(jsonParams.toString());
        return request.getContextPath() + "/" + ProductCategory.VEHICULO_CATEGORY_URL + "/" + Configuration.EVALUATION_CONTROLLER_URL + "?externalParams=" + externalParams;
    }

    @Override
    public VehicleDealership getDealerByCredit(Integer creditId){
        Credit credit = creditDAO.getCreditByID(creditId, Configuration.getDefaultLocale(), false, Credit.class);
        return credit.getVehicle().getVehicleDealership();
    }

}
