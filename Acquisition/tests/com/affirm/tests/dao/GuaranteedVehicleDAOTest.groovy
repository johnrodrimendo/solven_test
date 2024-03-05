package com.affirm.tests.dao

import com.affirm.client.dao.GuaranteedVehicleDAO
import com.affirm.common.model.catalog.GuaranteedVehicle
import com.affirm.tests.BaseAcquisitionConfig
import org.junit.Assert
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class GuaranteedVehicleDAOTest extends BaseAcquisitionConfig {

    @Autowired
    GuaranteedVehicleDAO guaranteedVehicleDAO

    static final String VEHICLES_JSON = '[{"model": "A1", "maintainedCarBrandId": "1", "mileageId": 1, "year": 2019, "currencyId": 0, "price": 50000, "isAccepted": true}]'

    @Test
    void getGuaranteedVehiclesFromGuaranteedVehicleDAODAO() {
        List<GuaranteedVehicle> result = guaranteedVehicleDAO.getGuaranteedVehicles()
        Assert.assertNotNull(result)
    }

    @Test
    void saveGuaranteedVehiclesFromGuaranteedVehicleDAODAO() {
        guaranteedVehicleDAO.saveGuaranteedVehicles(VEHICLES_JSON)
    }
}
