package com.affirm.tests.heroku


import com.affirm.heroku.HerokuServiceCall
import com.affirm.tests.BaseConfig
import groovy.transform.CompileStatic
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import org.springframework.beans.factory.annotation.Autowired

@CompileStatic
class HerokuServiceCallTest extends BaseConfig {

    @Autowired
    private HerokuServiceCall herokuServiceCall

    @Test
    void shouldNotFailRequestDynoInformation() {
        String url = "https://api.heroku.com/apps/prd-solven-entity/dynos/web.1"

        Executable executable = {
            herokuServiceCall.getDynos(url)
        }

        Assertions.assertDoesNotThrow(executable)
    }

}
