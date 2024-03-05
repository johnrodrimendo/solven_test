package com.affirm.tests.dao

import com.affirm.common.dao.EntityExtranetDAO
import com.affirm.tests.BaseConfig
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import org.springframework.beans.factory.annotation.Autowired

class EntityExtranetDAOTest extends BaseConfig {

    @Autowired
    private EntityExtranetDAO entityExtranetDAO

    @Test
    void shouldRegisterLogActivity() {
        String permission = 'loanApplication:register:view'
        int entityExtranetUserId = 1
        Executable executable = { entityExtranetDAO.registerLogActivity(permission, entityExtranetUserId) }

        Assertions.assertDoesNotThrow(executable)
    }

    @Test
    void shouldRegisterEntityUserRole() {
        int entiyUserId = 1
        List<Integer> roles = Arrays.asList(22, 27)
        Executable executable = { entityExtranetDAO.registerEntityUserRole(entiyUserId, roles) }

        Assertions.assertDoesNotThrow(executable)
    }

}
