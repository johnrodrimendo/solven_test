package com.affirm.client.dao;

import com.affirm.common.model.catalog.GuaranteedVehicle;

import java.util.List;

public interface GuaranteedVehicleDAO {
    List<GuaranteedVehicle> getGuaranteedVehicles();
    void saveGuaranteedVehicles(String vehiclesJSON);
}
