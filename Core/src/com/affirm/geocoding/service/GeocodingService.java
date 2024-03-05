package com.affirm.geocoding.service;

import com.affirm.acceso.model.Direccion;
import com.affirm.geocoding.model.GeocodingResponse;
import com.affirm.geocoding.model.GeocodingResult;

public interface GeocodingService {

    GeocodingResponse getGeoLocation(int loanApplicationId, String urlParams);

    GeocodingResult getGeoLocationResult(int loanApplicationId, Direccion direccion) throws Exception;
}
