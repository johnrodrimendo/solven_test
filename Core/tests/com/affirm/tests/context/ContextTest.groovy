package com.affirm.tests.context

import com.affirm.common.service.UserService
import com.affirm.tests.BaseConfig
import groovy.transform.CompileStatic
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

import static org.junit.jupiter.api.Assertions.assertNotNull

@CompileStatic
class ContextTest extends BaseConfig {

    @Autowired
    UserService userService

    @Test
    void contextIsLoadedSuccess() {
        assertNotNull userService
    }
}
