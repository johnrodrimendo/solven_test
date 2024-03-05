package com.affirm.tests.geocoding


import com.affirm.common.model.pagoefectivo.PagoEfectivoCreateAuthorizationResponse
import com.affirm.common.model.pagoefectivo.PagoEfectivoCreateCIPResponse
import com.affirm.common.service.PagoEfectivoClientService
import com.affirm.geocoding.model.GeocodingResponse
import com.affirm.geocoding.service.GeocodingService
import com.affirm.system.configuration.Configuration
import com.affirm.tests.BaseConfig
import com.google.gson.Gson
import groovy.transform.CompileStatic
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

@CompileStatic
class GeoCodingServiceTest extends BaseConfig {

    @Autowired
    private GeocodingService geocodingService;

    @Test
    void shoulGetGeoCodingResponse() {
        GeocodingResponse data = geocodingService.getGeoLocation(641563, "Lima+Lima+Av.+España+734");
        System.out.println(new Gson().toJson(data));
    }



}
