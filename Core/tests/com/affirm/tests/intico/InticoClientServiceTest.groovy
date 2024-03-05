package com.affirm.tests.intico

import com.affirm.common.dao.LoanApplicationDAO
import com.affirm.common.dao.UserDAO
import com.affirm.common.model.catalog.EntityProductParams
import com.affirm.common.model.catalog.UserFileType
import com.affirm.common.model.transactional.LoanApplication
import com.affirm.common.model.transactional.MatiResult
import com.affirm.common.model.transactional.UserFile
import com.affirm.common.service.CatalogService
import com.affirm.intico.service.InticoClientService
import com.affirm.mati.model.CreateVerificationResponse
import com.affirm.mati.service.MatiService
import com.affirm.security.dao.SecurityDAO
import com.affirm.system.configuration.Configuration
import com.affirm.tests.BaseConfig
import groovy.transform.CompileStatic
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

@CompileStatic
class InticoClientServiceTest extends BaseConfig {

    @Autowired
    private InticoClientService inticoClientService;


    @Test
    void TestFlow() {
        String smsText = String.format("Tenemos un descuento especial para que canceles tu deuda en Banco Azteca. ¡Ingresa \u00BF en %s \u00F3 para mejorar tu situación financiera!", Configuration.hostEnvIsProduction() ? "https://bit.ly/3qwQFS3" : "https://bit.ly/3quqjjC");
        inticoClientService.sendInticoSms("954448023",smsText);

    }

    @Test
    void TestResult() {
        inticoClientService.verifySmsStatus("WS0CTUB3HLFZSQ6UB");
//        .replaceAll("[^\\p{ASCII}]", "")  Normalizer.normalize("b6e59eb6-640a-47a6-9601-6634f328442d-moño-rojo.png", Normalizer.Form.NFD).replaceAll(" ","-").replaceAll("[^\\p{ASCII}]", "")
    }



}
