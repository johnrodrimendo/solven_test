package com.affirm.client.service;

import com.affirm.common.model.catalog.VehicleDealership;

import javax.servlet.http.HttpServletRequest;

public interface VehicleService {

    String createVehicleEvaluationUrl(int vehicleId, HttpServletRequest request);

    VehicleDealership getDealerByCredit(Integer creditId);
}
