package com.affirm.tests.security


import com.affirm.security.service.ExternalWebServiceService
import com.affirm.tests.BaseConfig
import groovy.transform.CompileStatic
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import org.springframework.beans.factory.annotation.Autowired

@CompileStatic
class ExternalWebServiceServiceTest extends BaseConfig {

    @Autowired
    private ExternalWebServiceService externalWebServiceService;

    @Test
    void shouldNotFailRequestWithServiceName() {

        Executable executable = {
            externalWebServiceService.getSucursalFdlm(10349);
        }

        Assertions.assertDoesNotThrow(executable)
    }
}
