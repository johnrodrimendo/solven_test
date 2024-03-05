package com.affirm.tests.pagolink


import com.affirm.intico.service.InticoClientService
import com.affirm.pagolink.model.PagoLinkCreateSessionResponse
import com.affirm.pagolink.model.PagoLinkOrderDetail
import com.affirm.pagolink.model.PagoLinkResponse
import com.affirm.pagolink.service.PagoLinkClientService
import com.affirm.system.configuration.Configuration
import com.affirm.tests.BaseConfig
import com.google.gson.Gson
import groovy.transform.CompileStatic
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

@CompileStatic
class PagoLinkClientServiceTest extends BaseConfig {

    @Autowired
    private PagoLinkClientService pagoLinkClientService;


    @Test
    void TestFlow() {

       String tokenRecovery = pagoLinkClientService.generateToken(641302);
        String tokenVigente = pagoLinkClientService.generateToken(641502 	);

        System.out.println(tokenRecovery);
        System.out.println(tokenVigente);

    }

    @Test
    void testGetOrderDetail() {

        PagoLinkOrderDetail detail = pagoLinkClientService.getOrderDetail("P1s0usXDAHdT", Configuration.defaultLocale)

        System.out.println(new Gson().toJson(detail));

    }

    @Test
    void testCreateOrder() {

        PagoLinkResponse response = pagoLinkClientService.createPaymentLink(637204,Configuration.defaultLocale)

        System.out.println(new Gson().toJson(response));

    }


    @Test
    void testCreateToken() {

        String response = pagoLinkClientService.generateToken(638108)

        System.out.println(new Gson().toJson(response));

    }



}
